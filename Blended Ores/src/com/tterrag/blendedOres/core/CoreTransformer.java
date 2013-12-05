package com.tterrag.blendedOres.core;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * @author fishtaco, tterrag
 * 
 */
@SuppressWarnings("unused")
public class CoreTransformer implements IClassTransformer
{	
	private final String ORE_CLASS_NAME = "net.minecraft.block.BlockOre";
	private final String OBF_ORE_CLASS_NAME = "apo";
	private final String REDSTONE_ORE_CLASS_NAME = "net.minecraft.block.BlockRedstoneOre";
	private final String OBF_REDSTONE_ORE_CLASS_NAME = "";	
	private final String RENDER_METHOD_NAME = "getRenderType";
	private final String RENDER_METHOD_NAME_OBF = "func_71857_b";
	private final String RENDER_METHOD_NOTCH = "a";
	private final String RENDER_METHOD_DESC = "()I";
	private final String OBF_RENDER_METHOD_DESC = "()I";

	@Override
	public byte[] transform(String arg0, String arg1, byte[] arg2)
	{
		if (arg1.compareTo(ORE_CLASS_NAME) == 0 || arg1.compareTo(OBF_ORE_CLASS_NAME) == 0 || arg1.compareTo(REDSTONE_ORE_CLASS_NAME) == 0 || arg1.compareTo(OBF_REDSTONE_ORE_CLASS_NAME) == 0)
			return patchClassOre(arg1, arg2, true);
		return arg2;
	}

	private byte[] patchClassOre(String name, byte[] bytes, boolean isObfuscated)
	{
		System.out.println("BEGINNING ORE TRANSFORMATION");
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
		cw.newConst(ClassWriter.COMPUTE_MAXS);

		MethodNode mn = new MethodNode(Opcodes.ACC_PUBLIC, "getRenderType", "()I", null, null);
		InsnList il = mn.instructions;
		il.add(new VarInsnNode(Opcodes.ALOAD, 0));
		il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/tterrag/blendedOres/core/CoreMethods", "getRenderType", "()I"));
		il.add(new InsnNode(Opcodes.IRETURN));
	    classNode.methods.add(mn);
	    
		classNode.accept(cw);
		
		System.out.println("ORE TRANSFORMATION COMPLETE");
		return cw.toByteArray();
	}
}