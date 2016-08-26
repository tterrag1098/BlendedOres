package com.tterrag.blendedores.core;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraft.launchwrapper.IClassTransformer;

public class CoreTransformer implements IClassTransformer {

    private static final String BLOCK_CLASS_NAME = "net.minecraft.block.Block";

    private static final String LAYER_METHOD_NAME = "canRenderInLayer";
    private static final String LAYER_METHOD_DESC = "(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/BlockRenderLayer;)Z";
    
    private static final String FORGE_RENDERER_CLASS_NAME = "net.minecraftforge.client.model.pipeline.ForgeBlockModelRenderer";
    private static final String FORGE_RENDERER_METHOD_NAME = "render";

    private static final String HOOK_CLASS_NAME = "com/tterrag/blendedores/core/CoreMethods";
    
    private static final String HOOK_LAYER_NAME = "canRenderInLayer";
    private static final String HOOK_LAYER_DESC = "(Lnet/minecraft/block/Block;Lnet/minecraft/util/BlockRenderLayer;)Z";
    
    private static final String HOOK_MODEL_NAME = "preGetQuads";
    private static final String HOOK_MODEL_DESC = "(Lnet/minecraft/client/renderer/block/model/IBakedModel;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;)V";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.equals(BLOCK_CLASS_NAME)) {
            return patchRenderLayer(transformedName, basicClass);
        } else if (transformedName.equals(FORGE_RENDERER_CLASS_NAME)) {
            return patchForgeRenderer(transformedName, basicClass);
        }
        return basicClass;
    }
    
    private ClassWriter cachedWriter;
    
    private ClassNode setup(byte[] bytes) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        cachedWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        cachedWriter.newConst(ClassWriter.COMPUTE_MAXS);
        
        return classNode;
    }

    private byte[] finish(ClassNode classNode) {
        classNode.accept(cachedWriter);
        return cachedWriter.toByteArray();
    }
    
    private byte[] patchRenderLayer(String name, byte[] bytes) {
        ClassNode classNode = setup(bytes);

        for (MethodNode m : classNode.methods) {
            if (m.name.equals(LAYER_METHOD_NAME) && m.desc.equals(LAYER_METHOD_DESC)) {
                for (int i = 0; i < m.instructions.size(); i++) {
                    AbstractInsnNode next = m.instructions.get(i);

                    if (next.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                        MethodInsnNode newNode = new MethodInsnNode(Opcodes.INVOKESTATIC, HOOK_CLASS_NAME, HOOK_LAYER_NAME, HOOK_LAYER_DESC, false);
                        m.instructions.set(next, newNode);
                    }
                }
            }
        }
        
        return finish(classNode);
    }
    
    private byte[] patchForgeRenderer(String name, byte[] bytes) {
        ClassNode classNode = setup(bytes);
        
        for (MethodNode m : classNode.methods) {
            if (m.name.equals(FORGE_RENDERER_METHOD_NAME)) {
                for (int i = 0; i < m.instructions.size(); i++) {
                    AbstractInsnNode next = m.instructions.get(i);
                    
                    if (next.getOpcode() == Opcodes.ISTORE && ((VarInsnNode)next).var == 9) {
                        InsnList newNodes = new InsnList();
                        newNodes.add(new VarInsnNode(Opcodes.ALOAD, 2));
                        newNodes.add(new VarInsnNode(Opcodes.ALOAD, 1));
                        newNodes.add(new VarInsnNode(Opcodes.ALOAD, 4));
                        newNodes.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOK_CLASS_NAME, HOOK_MODEL_NAME, HOOK_MODEL_DESC, false));
                        
                        m.instructions.insert(next, newNodes);
                    }
                }
            }
        }
        
        return finish(classNode);
    }
}
