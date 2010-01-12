/*
 * [The "BSD licence"]
 * Copyright (c) 2009 Ben Gruver
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.jf.dexlib.Code.Format;

import org.jf.dexlib.Code.*;
import org.jf.dexlib.DexFile;
import org.jf.dexlib.Util.NumberUtils;
import org.jf.dexlib.Util.AnnotatedOutput;

public class Instruction21t extends Instruction implements OffsetInstruction, SingleRegisterInstruction {
    public static final Instruction.InstructionFactory Factory = new Factory();
    private byte regA;
    private short offset;

    public Instruction21t(Opcode opcode, short regA, short offB) {
        super(opcode);

        if (regA >= 1 << 8) {
            throw new RuntimeException("The register number must be less than v256");
        }

        if (offB == 0) {
            throw new RuntimeException("The offset cannot be 0.");
        }

        this.regA = (byte)regA;
        this.offset = offB;
    }

    private Instruction21t(Opcode opcode, byte[] buffer, int bufferIndex) {
        super(opcode);

        assert buffer[bufferIndex] == opcode.value;

        regA = buffer[bufferIndex + 1];
        offset = NumberUtils.decodeShort(buffer, bufferIndex + 2);
        assert offset != 0;
    }

    protected void writeInstruction(AnnotatedOutput out, int currentCodeOffset) {
        out.writeByte(opcode.value);
        out.writeByte(regA);
        out.writeShort(offset);
    }

    public void updateOffset(int offset) {
        if (offset < Short.MIN_VALUE || offset > Short.MAX_VALUE) {
            throw new RuntimeException("The offset " + offset + " is out of range. It must be in [-32768, 32767]");
        }
        if (offset == 0) {
            throw new RuntimeException("The offset cannot be 0");
        }
        this.offset = (short)offset;
    }

    public Format getFormat() {
        return Format.Format21t;
    }

    public int getRegisterA() {
        return regA & 0xFF;
    }

    public int getOffset() {
        return offset;
    }

    private static class Factory implements Instruction.InstructionFactory {
        public Instruction makeInstruction(DexFile dexFile, Opcode opcode, byte[] buffer, int bufferIndex) {
            return new Instruction21t(opcode, buffer, bufferIndex);
        }
    }
}
