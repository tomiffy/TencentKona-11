/*
 * Copyright (c) 2017, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have
 * questions.
 */
package jdk.incubator.vector;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;
import jdk.internal.vm.annotation.ForceInline;
import static jdk.incubator.vector.VectorIntrinsics.*;

@SuppressWarnings("cast")
final class Short256Vector extends ShortVector<Shapes.S256Bit> {
    static final Short256Species SPECIES = new Short256Species();

    static final Short256Vector ZERO = new Short256Vector();

    static final int LENGTH = SPECIES.length();

    private final short[] vec; // Don't access directly, use getElements() instead.

    private short[] getElements() {
        return VectorIntrinsics.maybeRebox(this).vec;
    }

    Short256Vector() {
        vec = new short[SPECIES.length()];
    }

    Short256Vector(short[] v) {
        vec = v;
    }

    @Override
    public int length() { return LENGTH; }

    // Unary operator

    @Override
    Short256Vector uOp(FUnOp f) {
        short[] vec = getElements();
        short[] res = new short[length()];
        for (int i = 0; i < length(); i++) {
            res[i] = f.apply(i, vec[i]);
        }
        return new Short256Vector(res);
    }

    @Override
    Short256Vector uOp(Mask<Short, Shapes.S256Bit> o, FUnOp f) {
        short[] vec = getElements();
        short[] res = new short[length()];
        boolean[] mbits = ((Short256Mask)o).getBits();
        for (int i = 0; i < length(); i++) {
            res[i] = mbits[i] ? f.apply(i, vec[i]) : vec[i];
        }
        return new Short256Vector(res);
    }

    // Binary operator

    @Override
    Short256Vector bOp(Vector<Short, Shapes.S256Bit> o, FBinOp f) {
        short[] res = new short[length()];
        short[] vec1 = this.getElements();
        short[] vec2 = ((Short256Vector)o).getElements();
        for (int i = 0; i < length(); i++) {
            res[i] = f.apply(i, vec1[i], vec2[i]);
        }
        return new Short256Vector(res);
    }

    @Override
    Short256Vector bOp(Vector<Short, Shapes.S256Bit> o1, Mask<Short, Shapes.S256Bit> o2, FBinOp f) {
        short[] res = new short[length()];
        short[] vec1 = this.getElements();
        short[] vec2 = ((Short256Vector)o1).getElements();
        boolean[] mbits = ((Short256Mask)o2).getBits();
        for (int i = 0; i < length(); i++) {
            res[i] = mbits[i] ? f.apply(i, vec1[i], vec2[i]) : vec1[i];
        }
        return new Short256Vector(res);
    }

    // Trinary operator

    @Override
    Short256Vector tOp(Vector<Short, Shapes.S256Bit> o1, Vector<Short, Shapes.S256Bit> o2, FTriOp f) {
        short[] res = new short[length()];
        short[] vec1 = this.getElements();
        short[] vec2 = ((Short256Vector)o1).getElements();
        short[] vec3 = ((Short256Vector)o2).getElements();
        for (int i = 0; i < length(); i++) {
            res[i] = f.apply(i, vec1[i], vec2[i], vec3[i]);
        }
        return new Short256Vector(res);
    }

    @Override
    Short256Vector tOp(Vector<Short, Shapes.S256Bit> o1, Vector<Short, Shapes.S256Bit> o2, Mask<Short, Shapes.S256Bit> o3, FTriOp f) {
        short[] res = new short[length()];
        short[] vec1 = getElements();
        short[] vec2 = ((Short256Vector)o1).getElements();
        short[] vec3 = ((Short256Vector)o2).getElements();
        boolean[] mbits = ((Short256Mask)o3).getBits();
        for (int i = 0; i < length(); i++) {
            res[i] = mbits[i] ? f.apply(i, vec1[i], vec2[i], vec3[i]) : vec1[i];
        }
        return new Short256Vector(res);
    }

    @Override
    short rOp(short v, FBinOp f) {
        short[] vec = getElements();
        for (int i = 0; i < length(); i++) {
            v = f.apply(i, v, vec[i]);
        }
        return v;
    }

    // Binary operations with scalars

    @Override
    @ForceInline
    public ShortVector<Shapes.S256Bit> add(short o) {
        return add(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public ShortVector<Shapes.S256Bit> add(short o, Mask<Short,Shapes.S256Bit> m) {
        return add(SPECIES.broadcast(o), m);
    }

    @Override
    @ForceInline
    public ShortVector<Shapes.S256Bit> addSaturate(short o) {
        return addSaturate(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public ShortVector<Shapes.S256Bit> addSaturate(short o, Mask<Short,Shapes.S256Bit> m) {
        return addSaturate(SPECIES.broadcast(o), m);
    }

    @Override
    @ForceInline
    public ShortVector<Shapes.S256Bit> sub(short o) {
        return sub(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public ShortVector<Shapes.S256Bit> sub(short o, Mask<Short,Shapes.S256Bit> m) {
        return sub(SPECIES.broadcast(o), m);
    }

    @Override
    @ForceInline
    public ShortVector<Shapes.S256Bit> subSaturate(short o) {
        return subSaturate(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public ShortVector<Shapes.S256Bit> subSaturate(short o, Mask<Short,Shapes.S256Bit> m) {
        return subSaturate(SPECIES.broadcast(o), m);
    }

    @Override
    @ForceInline
    public ShortVector<Shapes.S256Bit> mul(short o) {
        return mul(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public ShortVector<Shapes.S256Bit> mul(short o, Mask<Short,Shapes.S256Bit> m) {
        return mul(SPECIES.broadcast(o), m);
    }

    @Override
    @ForceInline
    public ShortVector<Shapes.S256Bit> div(short o) {
        return div(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public ShortVector<Shapes.S256Bit> div(short o, Mask<Short,Shapes.S256Bit> m) {
        return div(SPECIES.broadcast(o), m);
    }

    @Override
    @ForceInline
    public ShortVector<Shapes.S256Bit> min(short o) {
        return min(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public ShortVector<Shapes.S256Bit> max(short o) {
        return max(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public Mask<Short, Shapes.S256Bit> equal(short o) {
        return equal(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public Mask<Short, Shapes.S256Bit> notEqual(short o) {
        return notEqual(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public Mask<Short, Shapes.S256Bit> lessThan(short o) {
        return lessThan(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public Mask<Short, Shapes.S256Bit> lessThanEq(short o) {
        return lessThanEq(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public Mask<Short, Shapes.S256Bit> greaterThan(short o) {
        return greaterThan(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public Mask<Short, Shapes.S256Bit> greaterThanEq(short o) {
        return greaterThanEq(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public ShortVector<Shapes.S256Bit> blend(short o, Mask<Short,Shapes.S256Bit> m) {
        return blend(SPECIES.broadcast(o), m);
    }


    @Override
    @ForceInline
    public ShortVector<Shapes.S256Bit> and(short o) {
        return and(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public ShortVector<Shapes.S256Bit> and(short o, Mask<Short,Shapes.S256Bit> m) {
        return and(SPECIES.broadcast(o), m);
    }

    @Override
    @ForceInline
    public ShortVector<Shapes.S256Bit> or(short o) {
        return or(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public ShortVector<Shapes.S256Bit> or(short o, Mask<Short,Shapes.S256Bit> m) {
        return or(SPECIES.broadcast(o), m);
    }

    @Override
    @ForceInline
    public ShortVector<Shapes.S256Bit> xor(short o) {
        return xor(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public ShortVector<Shapes.S256Bit> xor(short o, Mask<Short,Shapes.S256Bit> m) {
        return xor(SPECIES.broadcast(o), m);
    }

    @Override
    @ForceInline
    public ShortVector<Shapes.S256Bit> floorDiv(short o) {
        return floorDiv(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public ShortVector<Shapes.S256Bit> floorDiv(short o, Mask<Short,Shapes.S256Bit> m) {
        return floorDiv(SPECIES.broadcast(o), m);
    }

    @Override
    @ForceInline
    public ShortVector<Shapes.S256Bit> floorMod(short o) {
        return floorMod(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public ShortVector<Shapes.S256Bit> floorMod(short o, Mask<Short,Shapes.S256Bit> m) {
        return floorMod(SPECIES.broadcast(o), m);
    }


    // Unary operations



    // Binary operations

    @Override
    @ForceInline
    public Short256Vector add(Vector<Short,Shapes.S256Bit> o) {
        Objects.requireNonNull(o);
        Short256Vector v = (Short256Vector)o;
        return (Short256Vector) VectorIntrinsics.binaryOp(
            VECTOR_OP_ADD, Short256Vector.class, short.class, LENGTH,
            this, v,
            (v1, v2) -> ((Short256Vector)v1).bOp(v2, (i, a, b) -> (short)(a + b)));
    }

    @Override
    @ForceInline
    public Short256Vector sub(Vector<Short,Shapes.S256Bit> o) {
        Objects.requireNonNull(o);
        Short256Vector v = (Short256Vector)o;
        return (Short256Vector) VectorIntrinsics.binaryOp(
            VECTOR_OP_SUB, Short256Vector.class, short.class, LENGTH,
            this, v,
            (v1, v2) -> ((Short256Vector)v1).bOp(v2, (i, a, b) -> (short)(a - b)));
    }

    @Override
    @ForceInline
    public Short256Vector mul(Vector<Short,Shapes.S256Bit> o) {
        Objects.requireNonNull(o);
        Short256Vector v = (Short256Vector)o;
        return (Short256Vector) VectorIntrinsics.binaryOp(
            VECTOR_OP_MUL, Short256Vector.class, short.class, LENGTH,
            this, v,
            (v1, v2) -> ((Short256Vector)v1).bOp(v2, (i, a, b) -> (short)(a * b)));
    }

    @Override
    @ForceInline
    public Short256Vector div(Vector<Short,Shapes.S256Bit> o) {
        Objects.requireNonNull(o);
        Short256Vector v = (Short256Vector)o;
        return (Short256Vector) VectorIntrinsics.binaryOp(
            VECTOR_OP_DIV, Short256Vector.class, short.class, LENGTH,
            this, v,
            (v1, v2) -> ((Short256Vector)v1).bOp(v2, (i, a, b) -> (short)(a / b)));
    }



    @Override
    @ForceInline
    public Short256Vector and(Vector<Short,Shapes.S256Bit> o) {
        Objects.requireNonNull(o);
        Short256Vector v = (Short256Vector)o;
        return (Short256Vector) VectorIntrinsics.binaryOp(
            VECTOR_OP_AND, Short256Vector.class, short.class, LENGTH,
            this, v,
            (v1, v2) -> ((Short256Vector)v1).bOp(v2, (i, a, b) -> (short)(a & b)));
    }

    @Override
    @ForceInline
    public Short256Vector or(Vector<Short,Shapes.S256Bit> o) {
        Objects.requireNonNull(o);
        Short256Vector v = (Short256Vector)o;
        return (Short256Vector) VectorIntrinsics.binaryOp(
            VECTOR_OP_OR, Short256Vector.class, short.class, LENGTH,
            this, v,
            (v1, v2) -> ((Short256Vector)v1).bOp(v2, (i, a, b) -> (short)(a | b)));
    }

    @Override
    @ForceInline
    public Short256Vector xor(Vector<Short,Shapes.S256Bit> o) {
        Objects.requireNonNull(o);
        Short256Vector v = (Short256Vector)o;
        return (Short256Vector) VectorIntrinsics.binaryOp(
            VECTOR_OP_XOR, Short256Vector.class, short.class, LENGTH,
            this, v,
            (v1, v2) -> ((Short256Vector)v1).bOp(v2, (i, a, b) -> (short)(a ^ b)));
    }

    @Override
    @ForceInline
    public Short256Vector and(Vector<Short,Shapes.S256Bit> v, Mask<Short, Shapes.S256Bit> m) {
        return blend(and(v), m);
    }

    @Override
    @ForceInline
    public Short256Vector or(Vector<Short,Shapes.S256Bit> v, Mask<Short, Shapes.S256Bit> m) {
        return blend(or(v), m);
    }

    @Override
    @ForceInline
    public Short256Vector xor(Vector<Short,Shapes.S256Bit> v, Mask<Short, Shapes.S256Bit> m) {
        return blend(xor(v), m);
    }

    @Override
    @ForceInline
    public Short256Vector shiftL(int s) {
        return (Short256Vector) VectorIntrinsics.broadcastInt(
            VECTOR_OP_LSHIFT, Short256Vector.class, short.class, LENGTH,
            this, s,
            (v, i) -> v.uOp((__, a) -> (short) (a << i)));
    }

    @Override
    @ForceInline
    public Short256Vector shiftR(int s) {
        return (Short256Vector) VectorIntrinsics.broadcastInt(
            VECTOR_OP_URSHIFT, Short256Vector.class, short.class, LENGTH,
            this, s,
            (v, i) -> v.uOp((__, a) -> (short) (a >>> i)));
    }

    @Override
    @ForceInline
    public Short256Vector aShiftR(int s) {
        return (Short256Vector) VectorIntrinsics.broadcastInt(
            VECTOR_OP_RSHIFT, Short256Vector.class, short.class, LENGTH,
            this, s,
            (v, i) -> v.uOp((__, a) -> (short) (a >> i)));
    }

    // Ternary operations


    // Type specific horizontal reductions


    // Memory operations

    @Override
    @ForceInline
    public void intoArray(short[] a, int ix) {
        Objects.requireNonNull(a);
        ix = VectorIntrinsics.checkIndex(ix, a.length, LENGTH);
        VectorIntrinsics.store(Short256Vector.class, short.class, LENGTH,
                               a, ix, this,
                               (arr, idx, v) -> v.forEach((i, a_) -> ((short[])arr)[idx + i] = a_));
    }

    @Override
    @ForceInline
    public void intoArray(short[] a, int ax, Mask<Short, Shapes.S256Bit> m) {
        // TODO: use better default impl: forEach(m, (i, a_) -> a[ax + i] = a_);
        Short256Vector oldVal = SPECIES.fromArray(a, ax);
        Short256Vector newVal = oldVal.blend(this, m);
        newVal.intoArray(a, ax);
    }

    //

    @Override
    public String toString() {
        return Arrays.toString(getElements());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;

        Short256Vector that = (Short256Vector) o;
        return Arrays.equals(this.getElements(), that.getElements());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(vec);
    }

    // Binary test

    @Override
    Short256Mask bTest(Vector<Short, Shapes.S256Bit> o, FBinTest f) {
        short[] vec1 = getElements();
        short[] vec2 = ((Short256Vector)o).getElements();
        boolean[] bits = new boolean[length()];
        for (int i = 0; i < length(); i++){
            bits[i] = f.apply(i, vec1[i], vec2[i]);
        }
        return new Short256Mask(bits);
    }

    // Comparisons


    // Foreach

    @Override
    void forEach(FUnCon f) {
        short[] vec = getElements();
        for (int i = 0; i < length(); i++) {
            f.apply(i, vec[i]);
        }
    }

    @Override
    void forEach(Mask<Short, Shapes.S256Bit> o, FUnCon f) {
        boolean[] mbits = ((Short256Mask)o).getBits();
        forEach((i, a) -> {
            if (mbits[i]) { f.apply(i, a); }
        });
    }



    @Override
    public Short256Vector rotateEL(int j) {
        short[] vec = getElements();
        short[] res = new short[length()];
        for (int i = 0; i < length(); i++){
            res[j + i % length()] = vec[i];
        }
        return new Short256Vector(res);
    }

    @Override
    public Short256Vector rotateER(int j) {
        short[] vec = getElements();
        short[] res = new short[length()];
        for (int i = 0; i < length(); i++){
            int z = i - j;
            if(j < 0) {
                res[length() + z] = vec[i];
            } else {
                res[z] = vec[i];
            }
        }
        return new Short256Vector(res);
    }

    @Override
    public Short256Vector shiftEL(int j) {
        short[] vec = getElements();
        short[] res = new short[length()];
        for (int i = 0; i < length() - j; i++) {
            res[i] = vec[i + j];
        }
        return new Short256Vector(res);
    }

    @Override
    public Short256Vector shiftER(int j) {
        short[] vec = getElements();
        short[] res = new short[length()];
        for (int i = 0; i < length() - j; i++){
            res[i + j] = vec[i];
        }
        return new Short256Vector(res);
    }

    @Override
    public Short256Vector shuffle(Vector<Short, Shapes.S256Bit> o, Shuffle<Short, Shapes.S256Bit> s) {
        Short256Vector v = (Short256Vector) o;
        return uOp((i, a) -> {
            short[] vec = this.getElements();
            int e = s.getElement(i);
            if(e >= 0 && e < length()) {
                //from this
                return vec[e];
            } else if(e < length() * 2) {
                //from o
                return v.getElements()[e - length()];
            } else {
                throw new ArrayIndexOutOfBoundsException("Bad reordering for shuffle");
            }
        });
    }

    @Override
    public Short256Vector swizzle(Shuffle<Short, Shapes.S256Bit> s) {
        return uOp((i, a) -> {
            short[] vec = this.getElements();
            int e = s.getElement(i);
            if(e >= 0 && e < length()) {
                return vec[e];
            } else {
                throw new ArrayIndexOutOfBoundsException("Bad reordering for shuffle");
            }
        });
    }

    @Override
    @ForceInline
    public Short256Vector blend(Vector<Short, Shapes.S256Bit> o1, Mask<Short, Shapes.S256Bit> o2) {
        Objects.requireNonNull(o1);
        Objects.requireNonNull(o2);
        Short256Vector v = (Short256Vector)o1;
        Short256Mask   m = (Short256Mask)o2;

        return (Short256Vector) VectorIntrinsics.blend(
            Short256Vector.class, Short256Mask.class, short.class, LENGTH,
            this, v, m,
            (v1, v2, m_) -> v1.bOp(v2, (i, a, b) -> m_.getElement(i) ? b : a));
    }

    @Override
    @ForceInline
    @SuppressWarnings("unchecked")
    public <F> Vector<F, Shapes.S256Bit> rebracket(Class<F> type) {
        Objects.requireNonNull(type);
        // TODO: check proper element type
        return VectorIntrinsics.rebracket(
            Short256Vector.class, short.class, LENGTH,
            type, this,
            (v, t) -> (Vector<F, Shapes.S256Bit>) v.reshape(t, v.shape())
        );
    }

    @Override
    public <F, Z extends Shape> Vector<F, Z> cast(Class<F> type, Z shape) {
        Vector.Species<F,Z> species = Vector.speciesInstance(type, shape);

        // Whichever is larger
        int blen = Math.max(species.bitSize(), bitSize()) / Byte.SIZE;
        ByteBuffer bb = ByteBuffer.allocate(blen);

        int limit = Math.min(species.length(), length());

        short[] vec = getElements();
        if (type == Byte.class) {
            for (int i = 0; i < limit; i++){
                bb.put(i, (byte) vec[i]);
            }
        } else if (type == Short.class) {
            for (int i = 0; i < limit; i++){
                bb.asShortBuffer().put(i, (short) vec[i]);
            }
        } else if (type == Integer.class) {
            for (int i = 0; i < limit; i++){
                bb.asIntBuffer().put(i, (int) vec[i]);
            }
        } else if (type == Long.class){
            for (int i = 0; i < limit; i++){
                bb.asLongBuffer().put(i, (long) vec[i]);
            }
        } else if (type == Float.class){
            for (int i = 0; i < limit; i++){
                bb.asFloatBuffer().put(i, (float) vec[i]);
            }
        } else if (type == Double.class){
            for (int i = 0; i < limit; i++){
                bb.asDoubleBuffer().put(i, (double) vec[i]);
            }
        } else {
            throw new UnsupportedOperationException("Bad lane type for casting.");
        }

        return species.fromByteBuffer(bb);
    }

    // Accessors

    @Override
    public short get(int i) {
        short[] vec = getElements();
        return vec[i];
    }

    @Override
    public Short256Vector with(int i, short e) {
        short[] res = vec.clone();
        res[i] = e;
        return new Short256Vector(res);
    }

    // Mask

    static final class Short256Mask extends AbstractMask<Short, Shapes.S256Bit> {
        static final Short256Mask TRUE_MASK = new Short256Mask(true);
        static final Short256Mask FALSE_MASK = new Short256Mask(false);

        // FIXME: was temporarily put here to simplify rematerialization support in the JVM
        private final boolean[] bits; // Don't access directly, use getBits() instead.

        public Short256Mask(boolean[] bits) {
            if (bits.length != LENGTH) {
                throw new IllegalArgumentException("Boolean array must be the same length as the masked vector");
            }
            this.bits = bits.clone();
        }

        public Short256Mask(boolean val) {
            boolean[] bits = new boolean[LENGTH];
            for (int i = 0; i < bits.length; i++) {
                bits[i] = val;
            }
            this.bits = bits;
        }

        boolean[] getBits() {
            return VectorIntrinsics.maybeRebox(this).bits;
        }

        @Override
        Short256Mask uOp(MUnOp f) {
            boolean[] res = new boolean[species().length()];
            boolean[] bits = getBits();
            for (int i = 0; i < species().length(); i++) {
                res[i] = f.apply(i, bits[i]);
            }
            return new Short256Mask(res);
        }

        @Override
        Short256Mask bOp(Mask<Short, Shapes.S256Bit> o, MBinOp f) {
            boolean[] res = new boolean[species().length()];
            boolean[] bits = getBits();
            boolean[] mbits = ((Short256Mask)o).getBits();
            for (int i = 0; i < species().length(); i++) {
                res[i] = f.apply(i, bits[i], mbits[i]);
            }
            return new Short256Mask(res);
        }

        @Override
        public Short256Species species() {
            return SPECIES;
        }

        @Override
        public Short256Vector toVector() {
            short[] res = new short[species().length()];
            boolean[] bits = getBits();
            for (int i = 0; i < species().length(); i++) {
                res[i] = (short) (bits[i] ? -1 : 0);
            }
            return new Short256Vector(res);
        }

        @Override
        @ForceInline
        @SuppressWarnings("unchecked")
        public <Z> Mask<Z, Shapes.S256Bit> rebracket(Class<Z> type) {
            Objects.requireNonNull(type);
            // TODO: check proper element type
            return VectorIntrinsics.rebracket(
                Short256Mask.class, short.class, LENGTH,
                type, this,
                (m, t) -> (Mask<Z, Shapes.S256Bit>)m.reshape(t, m.species().shape())
            );
        }

        // Unary operations

        //Mask<E, S> not();

        // Binary operations

        @Override
        @ForceInline
        public Short256Mask and(Mask<Short,Shapes.S256Bit> o) {
            Objects.requireNonNull(o);
            Short256Mask m = (Short256Mask)o;
            return VectorIntrinsics.binaryOp(VECTOR_OP_AND, Short256Mask.class, short.class, LENGTH,
                                             this, m,
                                             (m1, m2) -> m1.bOp(m2, (i, a, b) -> a & b));
        }

        @Override
        @ForceInline
        public Short256Mask or(Mask<Short,Shapes.S256Bit> o) {
            Objects.requireNonNull(o);
            Short256Mask m = (Short256Mask)o;
            return VectorIntrinsics.binaryOp(VECTOR_OP_OR, Short256Mask.class, short.class, LENGTH,
                                             this, m,
                                             (m1, m2) -> m1.bOp(m2, (i, a, b) -> a | b));
        }

        // Reductions

        @Override
        @ForceInline
        public boolean anyTrue() {
            return VectorIntrinsics.test(COND_notZero, Short256Mask.class, short.class, LENGTH,
                                         this, this,
                                         (m1, m2) -> super.anyTrue());
        }

        @Override
        @ForceInline
        public boolean allTrue() {
            return VectorIntrinsics.test(COND_carrySet, Short256Mask.class, short.class, LENGTH,
                                         this, species().trueMask(),
                                         (m1, m2) -> super.allTrue());
        }
    }

    // Shuffle

    static final class Short256Shuffle extends AbstractShuffle<Short, Shapes.S256Bit> {
        static final IntVector.IntSpecies<Shapes.S256Bit> INT_SPECIES = (IntVector.IntSpecies<Shapes.S256Bit>) Vector.speciesInstance(Integer.class, Shapes.S_256_BIT);

        public Short256Shuffle(int[] reorder) {
            super(reorder);
        }

        @Override
        public Short256Species species() {
            return SPECIES;
        }

        @Override
        public IntVector.IntSpecies<Shapes.S256Bit> intSpecies() {
            return INT_SPECIES;
        }
    }

    // Species

    @Override
    public Short256Species species() {
        return SPECIES;
    }

    static final class Short256Species extends ShortSpecies<Shapes.S256Bit> {
        static final int BIT_SIZE = Shapes.S_256_BIT.bitSize();

        static final int LENGTH = BIT_SIZE / Short.SIZE;

        @Override
        public String toString() {
           StringBuilder sb = new StringBuilder("Shape[");
           sb.append(bitSize()).append(" bits, ");
           sb.append(length()).append(" ").append(short.class.getSimpleName()).append("s x ");
           sb.append(elementSize()).append(" bits");
           sb.append("]");
           return sb.toString();
        }

        @Override
        public int bitSize() {
            return BIT_SIZE;
        }

        @Override
        public int length() {
            return LENGTH;
        }

        @Override
        public Class<Short> elementType() {
            return Short.class;
        }

        @Override
        public int elementSize() {
            return Short.SIZE;
        }

        @Override
        public Shapes.S256Bit shape() {
            return Shapes.S_256_BIT;
        }

        @Override
        Short256Vector op(FOp f) {
            short[] res = new short[length()];
            for (int i = 0; i < length(); i++) {
                res[i] = f.apply(i);
            }
            return new Short256Vector(res);
        }

        @Override
        Short256Vector op(Mask<Short, Shapes.S256Bit> o, FOp f) {
            short[] res = new short[length()];
            boolean[] mbits = ((Short256Mask)o).getBits();
            for (int i = 0; i < length(); i++) {
                if (mbits[i]) {
                    res[i] = f.apply(i);
                }
            }
            return new Short256Vector(res);
        }

        // Factories

        @Override
        public Short256Mask constantMask(boolean... bits) {
            return new Short256Mask(bits.clone());
        }

        @Override
        public Short256Shuffle constantShuffle(int... ixs) {
            return new Short256Shuffle(ixs);
        }

        @Override
        @ForceInline
        public Short256Vector zero() {
            return VectorIntrinsics.broadcastCoerced(Short256Vector.class, short.class, LENGTH,
                                                     0,
                                                     (z -> ZERO));
        }

        @Override
        @ForceInline
        public Short256Vector broadcast(short e) {
            return VectorIntrinsics.broadcastCoerced(
                Short256Vector.class, short.class, LENGTH,
                e,
                ((long bits) -> SPECIES.op(i -> (short)bits)));
        }

        @Override
        @ForceInline
        public Short256Mask trueMask() {
            return VectorIntrinsics.broadcastCoerced(Short256Mask.class, short.class, LENGTH,
                                                     (short)-1,
                                                     (z -> Short256Mask.TRUE_MASK));
        }

        @Override
        @ForceInline
        public Short256Mask falseMask() {
            return VectorIntrinsics.broadcastCoerced(Short256Mask.class, short.class, LENGTH,
                                                     0,
                                                     (z -> Short256Mask.FALSE_MASK));
        }

        @Override
        @ForceInline
        public Short256Vector fromArray(short[] a, int ix) {
            Objects.requireNonNull(a);
            ix = VectorIntrinsics.checkIndex(ix, a.length, LENGTH);
            return (Short256Vector) VectorIntrinsics.load(Short256Vector.class, short.class, LENGTH,
                                                        a, ix,
                                                        (arr, idx) -> super.fromArray((short[]) arr, idx));
        }

        @Override
        @ForceInline
        public Short256Vector fromArray(short[] a, int ax, Mask<Short, Shapes.S256Bit> m) {
            return zero().blend(fromArray(a, ax), m); // TODO: use better default impl: op(m, i -> a[ax + i]);
        }
    }
}
