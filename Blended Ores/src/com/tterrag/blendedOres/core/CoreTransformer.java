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

/**
 * @author fishtaco, tterrag
 * 
 */
@SuppressWarnings("unused")
public class CoreTransformer implements IClassTransformer
{

	private final String ORE_CLASS_NAME = "net.minecraft.block.BlockOre";
	private final String OBF_ORE_CLASS_NAME = "apo";
	private final String RENDER_METHOD_NAME = "getRenderType";
	private final String RENDER_METHOD_NAME_OBF = "func_71857_b";
	private final String RENDER_METHOD_NOTCH = "a";
	private final String RENDER_METHOD_DESC = "()I";
	private final String OBF_RENDER_METHOD_DESC = "()I";

	@Override
	public byte[] transform(String arg0, String arg1, byte[] arg2)
	{
		if (arg1.compareTo(ORE_CLASS_NAME) == 0 || arg1.compareTo(OBF_ORE_CLASS_NAME) == 0)
			return patchClassOre(arg1, arg2, true);
		return arg2;
	}

	private byte[] patchClassOre(String name, byte[] bytes, boolean isObfuscated)
	{
		System.out.println("BEGINNING ORE TRANSFORMATION");
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

		MethodNode mv = new MethodNode(Opcodes.ACC_PUBLIC, "getRenderType", "()I", null, null);
	    mv.visitCode();
	    mv.visitVarInsn(Opcodes.ALOAD, 0);
	    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/tterrag/blendedOres/core/CoreMethods", "CoreMethods", "()I");
	    mv.visitInsn(Opcodes.RETURN);
	    mv.visitMaxs(1, 2);
	    mv.visitEnd();
	    classNode.methods.add(mv);
	    
		classNode.accept(cw);
		
		System.out.println("ORE TRANSFORMATION COMPLETE");
		return cw.toByteArray();
		
		//BELOW HERE OLD CODE ***********************************************************
		
		/*
		Iterator<MethodNode> methods = classNode.methods.iterator();
		while (methods.hasNext())
		{
			
			/*
			MethodNode m = methods.next();
			if ((m.name.equals(this.RENDER_METHOD_NAME) || m.name.equals(this.RENDER_METHOD_NAME_OBF) || m.name.equals(this.RENDER_METHOD_NOTCH))
					&& (m.desc.equals(this.RENDER_METHOD_DESC) || m.desc.equals(OBF_RENDER_METHOD_DESC)))
			{
				System.out.println("BEGINNING TRANSFORMATION!");
				for (int index = 0; index < m.instructions.size(); index++)
				{
					if (m.instructions.get(index).getType() == AbstractInsnNode.METHOD_INSN)
					{

						LabelNode lmm1Node = new LabelNode(new Label());

						LabelNode jumpLabel = new LabelNode(new Label());

						// make new instruction list
						InsnList toInject = new InsnList();

						toInject.add(new VarInsnNode(Opcodes.ALOAD, 1));
						toInject.add(new VarInsnNode(Opcodes.ILOAD, 2));
						toInject.add(new VarInsnNode(Opcodes.ILOAD, 3));
						toInject.add(new VarInsnNode(Opcodes.ILOAD, 4));

						toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "niel/mod/core/CoreMethods", "canConnectFenceTo", this.RENDER_METHOD_DESC));

						toInject.add(jumpLabel);
						toInject.add(lmm1Node);

						m.instructions.insert(m.instructions.get(index), toInject);
						System.out.println("TRANSFORMATION COMPLETE!");
						break;
					}
				}
			}
		}

		classNode.accept(cw);

		return cw.toByteArray();*/
	}
}