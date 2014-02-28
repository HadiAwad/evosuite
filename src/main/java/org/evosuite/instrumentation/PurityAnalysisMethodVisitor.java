/**
 * Copyright (C) 2011,2012 Gordon Fraser, Andrea Arcuri and EvoSuite
 * contributors
 *
 * This file is part of EvoSuite.
 *
 * EvoSuite is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * EvoSuite is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Public License for more details.
 *
 * You should have received a copy of the GNU Public License along with
 * EvoSuite. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * 
 */
package org.evosuite.instrumentation;

import java.util.HashSet;
import java.util.Set;

import org.evosuite.assertion.PurityAnalyzer;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * For each PUTSTATIC/PUTFIELD marks the method as impure.
 *
 * @author Juan Galeotti
 */
public class PurityAnalysisMethodVisitor extends MethodVisitor {

	private boolean updatesField;
	private final PurityAnalyzer purityAnalyzer;
	private final String className;
	private final String methodName;
	private final String descriptor;

	/**
	 * <p>Constructor for PutStaticMethodAdapter.</p>
	 *
	 * @param mv a {@link org.objectweb.asm.MethodVisitor} object.
	 * @param className a {@link java.lang.String} object.
	 * @param purityAnalyzer 
	 * @param finalFields a {@link java.util.List} object.
	 */
	public PurityAnalysisMethodVisitor(String className, String methodName,
			String descriptor, MethodVisitor mv, PurityAnalyzer purityAnalyzer) {
		super(Opcodes.ASM4, mv);
		this.updatesField = false;
		this.purityAnalyzer = purityAnalyzer;
		this.className = className;
		this.methodName = methodName;
		this.descriptor = descriptor;
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.MethodAdapter#visitFieldInsn(int, java.lang.String, java.lang.String, java.lang.String)
	 */
	/** {@inheritDoc} */
	@Override
	public void visitFieldInsn(int opcode, String owner, String name,
			String desc) {
		if (opcode == Opcodes.PUTSTATIC || opcode == Opcodes.PUTFIELD) {
			updatesField = true;
		}
		super.visitFieldInsn(opcode, owner, name, desc);
	}

	public boolean updatesField() {
		return updatesField;
	}

	@Override
	public void visitMethodInsn(int opcode, String owner, String name,
			String desc) {

		if (!owner.replace("/", ".").startsWith("org.evosuite")) {
			if (opcode == Opcodes.INVOKESTATIC) {
				this.purityAnalyzer.addStaticCall(className, methodName,
						descriptor, owner, name, desc);
			} else if (opcode == Opcodes.INVOKEVIRTUAL) {
				this.purityAnalyzer.addVirtualCall(className, methodName,
						descriptor, owner, name, desc);

			} else if (opcode == Opcodes.INVOKEINTERFACE) {
				this.purityAnalyzer.addInterfaceCall(className, methodName,
						descriptor, owner, name, desc);

			} else if (opcode == Opcodes.INVOKESPECIAL) {
				this.purityAnalyzer.addSpecialCall(className, methodName,
						descriptor, owner, name, desc);
			}
		}
		super.visitMethodInsn(opcode, owner, name, desc);
	}
}