package com.github.terminatornl.tickcentral.asm;

import com.github.terminatornl.tickcentral.core.TrueITickable;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

public class HubAPITransformer implements IClassTransformer {


	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if (basicClass == null || transformedName.equals("com.github.terminatornl.tickcentral.api.TickHub") == false) {
			return basicClass;
		}
		ClassReader reader = new ClassReader(basicClass);
		ClassNode classNode = new ClassNode();
		reader.accept(classNode, 0);

		for (MethodNode method : classNode.methods) {
			switch (method.name) {
				case "trueRandomTick":
					HubAPITransformer.convertInstruction(BlockTransformer.TRUE_RANDOM_TICK_NAME, method.instructions);
					break;
				case "trueUpdateTick":
					HubAPITransformer.convertInstruction(BlockTransformer.TRUE_UPDATE_TICK_NAME, method.instructions);
					break;
				case "trueUpdate":
					HubAPITransformer.convertInstruction(ITickableTransformer.TRUE_ITICKABLE_UPDATE, TrueITickable.class.getName().replace(".","/"), method.instructions);
					break;
			}
		}
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);
		return writer.toByteArray();
	}

	public static void convertInstruction(String target, InsnList instructions) {
		Iterator<AbstractInsnNode> iterator = instructions.iterator();
		while(iterator.hasNext()){
			AbstractInsnNode node = iterator.next();
			switch (node.getOpcode()){
				case Opcodes.INVOKEDYNAMIC:
				case Opcodes.INVOKEINTERFACE:
				case Opcodes.INVOKESPECIAL:
				case Opcodes.INVOKESTATIC:
				case Opcodes.INVOKEVIRTUAL:
					MethodInsnNode methodNode = (MethodInsnNode) node;
					methodNode.name = target;
			}
		}
	}


	public static void convertInstruction(String target, String owner, InsnList instructions) {
		Iterator<AbstractInsnNode> iterator = instructions.iterator();
		while(iterator.hasNext()){
			AbstractInsnNode node = iterator.next();
			switch (node.getOpcode()){
				case Opcodes.INVOKEDYNAMIC:
				case Opcodes.INVOKEINTERFACE:
				case Opcodes.INVOKESPECIAL:
				case Opcodes.INVOKESTATIC:
				case Opcodes.INVOKEVIRTUAL:
					MethodInsnNode methodNode = (MethodInsnNode) node;
					methodNode.name = target;
					methodNode.owner = owner;
			}
		}
	}

}
