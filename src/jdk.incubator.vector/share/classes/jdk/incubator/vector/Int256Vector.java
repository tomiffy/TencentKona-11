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
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.ReadOnlyBufferException;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.IntUnaryOperator;

import jdk.internal.misc.Unsafe;
import jdk.internal.vm.annotation.ForceInline;
import static jdk.incubator.vector.VectorIntrinsics.*;

@SuppressWarnings("cast")
final class Int256Vector extends IntVector<Shapes.S256Bit> {
    static final Int256Species SPECIES = new Int256Species();

    static final Int256Vector ZERO = new Int256Vector();

    static final int LENGTH = SPECIES.length();

    private final int[] vec; // Don't access directly, use getElements() instead.

    private int[] getElements() {
        return VectorIntrinsics.maybeRebox(this).vec;
    }

    Int256Vector() {
        vec = new int[SPECIES.length()];
    }

    Int256Vector(int[] v) {
        vec = v;
    }

    @Override
    public int length() { return LENGTH; }

    // Unary operator

    @Override
    Int256Vector uOp(FUnOp f) {
        int[] vec = getElements();
        int[] res = new int[length()];
        for (int i = 0; i < length(); i++) {
            res[i] = f.apply(i, vec[i]);
        }
        return new Int256Vector(res);
    }

    @Override
    Int256Vector uOp(Mask<Integer, Shapes.S256Bit> o, FUnOp f) {
        int[] vec = getElements();
        int[] res = new int[length()];
        boolean[] mbits = ((Int256Mask)o).getBits();
        for (int i = 0; i < length(); i++) {
            res[i] = mbits[i] ? f.apply(i, vec[i]) : vec[i];
        }
        return new Int256Vector(res);
    }

    // Binary operator

    @Override
    Int256Vector bOp(Vector<Integer, Shapes.S256Bit> o, FBinOp f) {
        int[] res = new int[length()];
        int[] vec1 = this.getElements();
        int[] vec2 = ((Int256Vector)o).getElements();
        for (int i = 0; i < length(); i++) {
            res[i] = f.apply(i, vec1[i], vec2[i]);
        }
        return new Int256Vector(res);
    }

    @Override
    Int256Vector bOp(Vector<Integer, Shapes.S256Bit> o1, Mask<Integer, Shapes.S256Bit> o2, FBinOp f) {
        int[] res = new int[length()];
        int[] vec1 = this.getElements();
        int[] vec2 = ((Int256Vector)o1).getElements();
        boolean[] mbits = ((Int256Mask)o2).getBits();
        for (int i = 0; i < length(); i++) {
            res[i] = mbits[i] ? f.apply(i, vec1[i], vec2[i]) : vec1[i];
        }
        return new Int256Vector(res);
    }

    // Trinary operator

    @Override
    Int256Vector tOp(Vector<Integer, Shapes.S256Bit> o1, Vector<Integer, Shapes.S256Bit> o2, FTriOp f) {
        int[] res = new int[length()];
        int[] vec1 = this.getElements();
        int[] vec2 = ((Int256Vector)o1).getElements();
        int[] vec3 = ((Int256Vector)o2).getElements();
        for (int i = 0; i < length(); i++) {
            res[i] = f.apply(i, vec1[i], vec2[i], vec3[i]);
        }
        return new Int256Vector(res);
    }

    @Override
    Int256Vector tOp(Vector<Integer, Shapes.S256Bit> o1, Vector<Integer, Shapes.S256Bit> o2, Mask<Integer, Shapes.S256Bit> o3, FTriOp f) {
        int[] res = new int[length()];
        int[] vec1 = getElements();
        int[] vec2 = ((Int256Vector)o1).getElements();
        int[] vec3 = ((Int256Vector)o2).getElements();
        boolean[] mbits = ((Int256Mask)o3).getBits();
        for (int i = 0; i < length(); i++) {
            res[i] = mbits[i] ? f.apply(i, vec1[i], vec2[i], vec3[i]) : vec1[i];
        }
        return new Int256Vector(res);
    }

    @Override
    int rOp(int v, FBinOp f) {
        int[] vec = getElements();
        for (int i = 0; i < length(); i++) {
            v = f.apply(i, v, vec[i]);
        }
        return v;
    }

    // Binary operations with scalars

    @Override
    @ForceInline
    public IntVector<Shapes.S256Bit> add(int o) {
        return add(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public IntVector<Shapes.S256Bit> add(int o, Mask<Integer,Shapes.S256Bit> m) {
        return add(SPECIES.broadcast(o), m);
    }

    @Override
    @ForceInline
    public IntVector<Shapes.S256Bit> sub(int o) {
        return sub(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public IntVector<Shapes.S256Bit> sub(int o, Mask<Integer,Shapes.S256Bit> m) {
        return sub(SPECIES.broadcast(o), m);
    }

    @Override
    @ForceInline
    public IntVector<Shapes.S256Bit> mul(int o) {
        return mul(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public IntVector<Shapes.S256Bit> mul(int o, Mask<Integer,Shapes.S256Bit> m) {
        return mul(SPECIES.broadcast(o), m);
    }

    @Override
    @ForceInline
    public IntVector<Shapes.S256Bit> min(int o) {
        return min(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public IntVector<Shapes.S256Bit> max(int o) {
        return max(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public Mask<Integer, Shapes.S256Bit> equal(int o) {
        return equal(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public Mask<Integer, Shapes.S256Bit> notEqual(int o) {
        return notEqual(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public Mask<Integer, Shapes.S256Bit> lessThan(int o) {
        return lessThan(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public Mask<Integer, Shapes.S256Bit> lessThanEq(int o) {
        return lessThanEq(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public Mask<Integer, Shapes.S256Bit> greaterThan(int o) {
        return greaterThan(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public Mask<Integer, Shapes.S256Bit> greaterThanEq(int o) {
        return greaterThanEq(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public IntVector<Shapes.S256Bit> blend(int o, Mask<Integer,Shapes.S256Bit> m) {
        return blend(SPECIES.broadcast(o), m);
    }


    @Override
    @ForceInline
    public IntVector<Shapes.S256Bit> and(int o) {
        return and(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public IntVector<Shapes.S256Bit> and(int o, Mask<Integer,Shapes.S256Bit> m) {
        return and(SPECIES.broadcast(o), m);
    }

    @Override
    @ForceInline
    public IntVector<Shapes.S256Bit> or(int o) {
        return or(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public IntVector<Shapes.S256Bit> or(int o, Mask<Integer,Shapes.S256Bit> m) {
        return or(SPECIES.broadcast(o), m);
    }

    @Override
    @ForceInline
    public IntVector<Shapes.S256Bit> xor(int o) {
        return xor(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public IntVector<Shapes.S256Bit> xor(int o, Mask<Integer,Shapes.S256Bit> m) {
        return xor(SPECIES.broadcast(o), m);
    }

    @Override
    @ForceInline
    public Int256Vector neg() {
        return SPECIES.zero().sub(this);
    }

    // Unary operations

    @ForceInline
    @Override
    public Int256Vector neg(Mask<Integer, Shapes.S256Bit> m) {
        return blend(neg(), m);
    }

    @Override
    @ForceInline
    public Int256Vector abs() {
        return VectorIntrinsics.unaryOp(
            VECTOR_OP_ABS, Int256Vector.class, int.class, LENGTH,
            this,
            v1 -> v1.uOp((i, a) -> (int) Math.abs(a)));
    }

    @ForceInline
    @Override
    public Int256Vector abs(Mask<Integer, Shapes.S256Bit> m) {
        return blend(abs(), m);
    }


    @Override
    @ForceInline
    public Int256Vector not() {
        return VectorIntrinsics.unaryOp(
            VECTOR_OP_NOT, Int256Vector.class, int.class, LENGTH,
            this,
            v1 -> v1.uOp((i, a) -> (int) ~a));
    }

    @ForceInline
    @Override
    public Int256Vector not(Mask<Integer, Shapes.S256Bit> m) {
        return blend(not(), m);
    }
    // Binary operations

    @Override
    @ForceInline
    public Int256Vector add(Vector<Integer,Shapes.S256Bit> o) {
        Objects.requireNonNull(o);
        Int256Vector v = (Int256Vector)o;
        return VectorIntrinsics.binaryOp(
            VECTOR_OP_ADD, Int256Vector.class, int.class, LENGTH,
            this, v,
            (v1, v2) -> v1.bOp(v2, (i, a, b) -> (int)(a + b)));
    }

    @Override
    @ForceInline
    public Int256Vector add(Vector<Integer,Shapes.S256Bit> v, Mask<Integer, Shapes.S256Bit> m) {
        return blend(add(v), m);
    }

    @Override
    @ForceInline
    public Int256Vector sub(Vector<Integer,Shapes.S256Bit> o) {
        Objects.requireNonNull(o);
        Int256Vector v = (Int256Vector)o;
        return VectorIntrinsics.binaryOp(
            VECTOR_OP_SUB, Int256Vector.class, int.class, LENGTH,
            this, v,
            (v1, v2) -> v1.bOp(v2, (i, a, b) -> (int)(a - b)));
    }

    @Override
    @ForceInline
    public Int256Vector sub(Vector<Integer,Shapes.S256Bit> v, Mask<Integer, Shapes.S256Bit> m) {
        return blend(sub(v), m);
    }

    @Override
    @ForceInline
    public Int256Vector mul(Vector<Integer,Shapes.S256Bit> o) {
        Objects.requireNonNull(o);
        Int256Vector v = (Int256Vector)o;
        return VectorIntrinsics.binaryOp(
            VECTOR_OP_MUL, Int256Vector.class, int.class, LENGTH,
            this, v,
            (v1, v2) -> v1.bOp(v2, (i, a, b) -> (int)(a * b)));
    }

    @Override
    @ForceInline
    public Int256Vector mul(Vector<Integer,Shapes.S256Bit> v, Mask<Integer, Shapes.S256Bit> m) {
        return blend(mul(v), m);
    }

    @Override
    @ForceInline
    public Int256Vector min(Vector<Integer,Shapes.S256Bit> o) {
        Objects.requireNonNull(o);
        Int256Vector v = (Int256Vector)o;
        return (Int256Vector) VectorIntrinsics.binaryOp(
            VECTOR_OP_MIN, Int256Vector.class, int.class, LENGTH,
            this, v,
            (v1, v2) -> ((Int256Vector)v1).bOp(v2, (i, a, b) -> (int) ((a < b) ? a : b)));
    }

    @Override
    @ForceInline
    public Int256Vector max(Vector<Integer,Shapes.S256Bit> o) {
        Objects.requireNonNull(o);
        Int256Vector v = (Int256Vector)o;
        return VectorIntrinsics.binaryOp(
            VECTOR_OP_MAX, Int256Vector.class, int.class, LENGTH,
            this, v,
            (v1, v2) -> v1.bOp(v2, (i, a, b) -> (int) ((a > b) ? a : b)));
        }

    @Override
    @ForceInline
    public Int256Vector and(Vector<Integer,Shapes.S256Bit> o) {
        Objects.requireNonNull(o);
        Int256Vector v = (Int256Vector)o;
        return VectorIntrinsics.binaryOp(
            VECTOR_OP_AND, Int256Vector.class, int.class, LENGTH,
            this, v,
            (v1, v2) -> v1.bOp(v2, (i, a, b) -> (int)(a & b)));
    }

    @Override
    @ForceInline
    public Int256Vector or(Vector<Integer,Shapes.S256Bit> o) {
        Objects.requireNonNull(o);
        Int256Vector v = (Int256Vector)o;
        return VectorIntrinsics.binaryOp(
            VECTOR_OP_OR, Int256Vector.class, int.class, LENGTH,
            this, v,
            (v1, v2) -> v1.bOp(v2, (i, a, b) -> (int)(a | b)));
    }

    @Override
    @ForceInline
    public Int256Vector xor(Vector<Integer,Shapes.S256Bit> o) {
        Objects.requireNonNull(o);
        Int256Vector v = (Int256Vector)o;
        return VectorIntrinsics.binaryOp(
            VECTOR_OP_XOR, Int256Vector.class, int.class, LENGTH,
            this, v,
            (v1, v2) -> v1.bOp(v2, (i, a, b) -> (int)(a ^ b)));
    }

    @Override
    @ForceInline
    public Int256Vector and(Vector<Integer,Shapes.S256Bit> v, Mask<Integer, Shapes.S256Bit> m) {
        return blend(and(v), m);
    }

    @Override
    @ForceInline
    public Int256Vector or(Vector<Integer,Shapes.S256Bit> v, Mask<Integer, Shapes.S256Bit> m) {
        return blend(or(v), m);
    }

    @Override
    @ForceInline
    public Int256Vector xor(Vector<Integer,Shapes.S256Bit> v, Mask<Integer, Shapes.S256Bit> m) {
        return blend(xor(v), m);
    }

    @Override
    @ForceInline
    public Int256Vector shiftL(int s) {
        return VectorIntrinsics.broadcastInt(
            VECTOR_OP_LSHIFT, Int256Vector.class, int.class, LENGTH,
            this, s,
            (v, i) -> v.uOp((__, a) -> (int) (a << i)));
    }

    @Override
    @ForceInline
    public Int256Vector shiftR(int s) {
        return VectorIntrinsics.broadcastInt(
            VECTOR_OP_URSHIFT, Int256Vector.class, int.class, LENGTH,
            this, s,
            (v, i) -> v.uOp((__, a) -> (int) (a >>> i)));
    }

    @Override
    @ForceInline
    public Int256Vector aShiftR(int s) {
        return VectorIntrinsics.broadcastInt(
            VECTOR_OP_RSHIFT, Int256Vector.class, int.class, LENGTH,
            this, s,
            (v, i) -> v.uOp((__, a) -> (int) (a >> i)));
    }

    @Override
    @ForceInline
    public Int256Vector shiftL(Vector<Integer,Shapes.S256Bit> s) {
        Int256Vector shiftv = (Int256Vector)s;
        // As per shift specification for Java, mask the shift count.
        shiftv = shiftv.and(species().broadcast(0x1f));
        return VectorIntrinsics.binaryOp(
            VECTOR_OP_LSHIFT, Int256Vector.class, int.class, LENGTH,
            this, shiftv,
            (v1, v2) -> v1.bOp(v2,(i,a, b) -> (int) (a << b)));
    }

    @Override
    @ForceInline
    public Int256Vector shiftR(Vector<Integer,Shapes.S256Bit> s) {
        Int256Vector shiftv = (Int256Vector)s;
        // As per shift specification for Java, mask the shift count.
        shiftv = shiftv.and(species().broadcast(0x1f));
        return VectorIntrinsics.binaryOp(
            VECTOR_OP_URSHIFT, Int256Vector.class, int.class, LENGTH,
            this, shiftv,
            (v1, v2) -> v1.bOp(v2,(i,a, b) -> (int) (a >>> b)));
    }

    @Override
    @ForceInline
    public Int256Vector ashiftR(Vector<Integer,Shapes.S256Bit> s) {
        Int256Vector shiftv = (Int256Vector)s;
        // As per shift specification for Java, mask the shift count.
        shiftv = shiftv.and(species().broadcast(0x1f));
        return VectorIntrinsics.binaryOp(
            VECTOR_OP_RSHIFT, Int256Vector.class, int.class, LENGTH,
            this, shiftv,
            (v1, v2) -> v1.bOp(v2,(i,a, b) -> (int) (a >> b)));
    }
    // Ternary operations


    // Type specific horizontal reductions

    @Override
    @ForceInline
    public int addAll() {
        return (int) VectorIntrinsics.reductionCoerced(
            VECTOR_OP_ADD, Int256Vector.class, int.class, LENGTH,
            this,
            v -> (long) v.rOp((int) 0, (i, a, b) -> (int) (a + b)));
    }

    @Override
    @ForceInline
    public int andAll() {
        return (int) VectorIntrinsics.reductionCoerced(
            VECTOR_OP_AND, Int256Vector.class, int.class, LENGTH,
            this,
            v -> (long) v.rOp((int) -1, (i, a, b) -> (int) (a & b)));
    }

    @Override
    @ForceInline
    public int andAll(Mask<Integer, Shapes.S256Bit> m) {
        return blend(SPECIES.broadcast((int) -1), m).andAll();
    }

    @Override
    @ForceInline
    public int minAll() {
        return (int) VectorIntrinsics.reductionCoerced(
            VECTOR_OP_MIN, Int256Vector.class, int.class, LENGTH,
            this,
            v -> (long) v.rOp(Integer.MAX_VALUE , (i, a, b) -> (int) ((a < b) ? a : b)));
    }

    @Override
    @ForceInline
    public int maxAll() {
        return (int) VectorIntrinsics.reductionCoerced(
            VECTOR_OP_MAX, Int256Vector.class, int.class, LENGTH,
            this,
            v -> (long) v.rOp(Integer.MIN_VALUE , (i, a, b) -> (int) ((a > b) ? a : b)));
    }

    @Override
    @ForceInline
    public int mulAll() {
        return (int) VectorIntrinsics.reductionCoerced(
            VECTOR_OP_MUL, Int256Vector.class, int.class, LENGTH,
            this,
            v -> (long) v.rOp((int) 1, (i, a, b) -> (int) (a * b)));
    }

    @Override
    @ForceInline
    public int subAll() {
        return (int) VectorIntrinsics.reductionCoerced(
            VECTOR_OP_SUB, Int256Vector.class, int.class, LENGTH,
            this,
            v -> (long) v.rOp((int) 0, (i, a, b) -> (int) (a - b)));
    }

    @Override
    @ForceInline
    public int orAll() {
        return (int) VectorIntrinsics.reductionCoerced(
            VECTOR_OP_OR, Int256Vector.class, int.class, LENGTH,
            this,
            v -> (long) v.rOp((int) 0, (i, a, b) -> (int) (a | b)));
    }

    @Override
    @ForceInline
    public int orAll(Mask<Integer, Shapes.S256Bit> m) {
        return blend(SPECIES.broadcast((int) 0), m).orAll();
    }

    @Override
    @ForceInline
    public int xorAll() {
        return (int) VectorIntrinsics.reductionCoerced(
            VECTOR_OP_XOR, Int256Vector.class, int.class, LENGTH,
            this,
            v -> (long) v.rOp((int) 0, (i, a, b) -> (int) (a ^ b)));
    }

    @Override
    @ForceInline
    public int xorAll(Mask<Integer, Shapes.S256Bit> m) {
        return blend(SPECIES.broadcast((int) 0), m).xorAll();
    }


    @Override
    @ForceInline
    public int addAll(Mask<Integer, Shapes.S256Bit> m) {
        return blend(SPECIES.broadcast((int) 0), m).addAll();
    }

    @Override
    @ForceInline
    public int subAll(Mask<Integer, Shapes.S256Bit> m) {
        return blend(SPECIES.broadcast((int) 0), m).subAll();
    }

    @Override
    @ForceInline
    public int mulAll(Mask<Integer, Shapes.S256Bit> m) {
        return blend(SPECIES.broadcast((int) 1), m).mulAll();
    }

    @Override
    @ForceInline
    public int minAll(Mask<Integer, Shapes.S256Bit> m) {
        return blend(SPECIES.broadcast(Integer.MAX_VALUE), m).minAll();
    }

    @Override
    @ForceInline
    public int maxAll(Mask<Integer, Shapes.S256Bit> m) {
        return blend(SPECIES.broadcast(Integer.MIN_VALUE), m).maxAll();
    }

    @Override
    @ForceInline
    public Shuffle<Integer, Shapes.S256Bit> toShuffle() {
        int[] a = toArray();
        int[] sa = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            sa[i] = (int) a[i];
        }
        return SPECIES.shuffleFromArray(sa, 0);
    }

    // Memory operations

    private static final int ARRAY_SHIFT = 31 - Integer.numberOfLeadingZeros(Unsafe.ARRAY_INT_INDEX_SCALE);

    @Override
    @ForceInline
    public void intoArray(int[] a, int ix) {
        Objects.requireNonNull(a);
        ix = VectorIntrinsics.checkIndex(ix, a.length, LENGTH);
        VectorIntrinsics.store(Int256Vector.class, int.class, LENGTH,
                               a, (((long) ix) << ARRAY_SHIFT) + Unsafe.ARRAY_INT_BASE_OFFSET,
                               this,
                               a, ix,
                               (arr, idx, v) -> v.forEach((i, e) -> arr[idx + i] = e));
    }

    @Override
    @ForceInline
    public final void intoArray(int[] a, int ax, Mask<Integer, Shapes.S256Bit> m) {
        // @@@ This can result in out of bounds errors for unset mask lanes
        Int256Vector oldVal = SPECIES.fromArray(a, ax);
        Int256Vector newVal = oldVal.blend(this, m);
        newVal.intoArray(a, ax);
    }

    @Override
    @ForceInline
    public void intoByteArray(byte[] a, int ix) {
        // @@@ Endianess
        Objects.requireNonNull(a);
        ix = VectorIntrinsics.checkIndex(ix, a.length, bitSize() / Byte.SIZE);
        VectorIntrinsics.store(Int256Vector.class, int.class, LENGTH,
                               a, ((long) ix) + Unsafe.ARRAY_BYTE_BASE_OFFSET,
                               this,
                               a, ix,
                               (c, idx, v) -> {
                                   ByteBuffer bbc = ByteBuffer.wrap(c, idx, c.length - idx).order(ByteOrder.nativeOrder());
                                   IntBuffer tb = bbc.asIntBuffer();
                                   v.forEach((i, e) -> tb.put(e));
                               });
    }

    @Override
    @ForceInline
    public final void intoByteArray(byte[] a, int ix, Mask<Integer, Shapes.S256Bit> m) {
        // @@@ This can result in out of bounds errors for unset mask lanes
        Int256Vector oldVal = SPECIES.fromByteArray(a, ix);
        Int256Vector newVal = oldVal.blend(this, m);
        newVal.intoByteArray(a, ix);
    }

    @Override
    @ForceInline
    public void intoByteBuffer(ByteBuffer bb, int ix) {
        // @@@ Endianess
        if (bb.order() != ByteOrder.nativeOrder()) {
            throw new IllegalArgumentException();
        }
        if (bb.isReadOnly()) {
            throw new ReadOnlyBufferException();
        }
        ix = VectorIntrinsics.checkIndex(ix, bb.limit(), bitSize() / Byte.SIZE);
        VectorIntrinsics.store(Int256Vector.class, int.class, LENGTH,
                               U.getObject(bb, BYTE_BUFFER_HB), ix + U.getLong(bb, BUFFER_ADDRESS),
                               this,
                               bb, ix,
                               (c, idx, v) -> {
                                   ByteBuffer bbc = c.duplicate().position(idx).order(ByteOrder.nativeOrder());
                                   IntBuffer tb = bbc.asIntBuffer();
                                   v.forEach((i, e) -> tb.put(e));
                               });
    }

    @Override
    @ForceInline
    public void intoByteBuffer(ByteBuffer bb, int ix, Mask<Integer, Shapes.S256Bit> m) {
        // @@@ This can result in out of bounds errors for unset mask lanes
        Int256Vector oldVal = SPECIES.fromByteBuffer(bb, ix);
        Int256Vector newVal = oldVal.blend(this, m);
        newVal.intoByteBuffer(bb, ix);
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

        // @@@ Use equal op
        Int256Vector that = (Int256Vector) o;
        return Arrays.equals(this.getElements(), that.getElements());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(vec);
    }

    // Binary test

    @Override
    Int256Mask bTest(Vector<Integer, Shapes.S256Bit> o, FBinTest f) {
        int[] vec1 = getElements();
        int[] vec2 = ((Int256Vector)o).getElements();
        boolean[] bits = new boolean[length()];
        for (int i = 0; i < length(); i++){
            bits[i] = f.apply(i, vec1[i], vec2[i]);
        }
        return new Int256Mask(bits);
    }

    // Comparisons

    @Override
    @ForceInline
    public Int256Mask equal(Vector<Integer, Shapes.S256Bit> o) {
        Objects.requireNonNull(o);
        Int256Vector v = (Int256Vector)o;

        return VectorIntrinsics.compare(
            BT_eq, Int256Vector.class, Int256Mask.class, int.class, LENGTH,
            this, v,
            (v1, v2) -> v1.bTest(v2, (i, a, b) -> a == b));
    }

    @Override
    @ForceInline
    public Int256Mask notEqual(Vector<Integer, Shapes.S256Bit> o) {
        Objects.requireNonNull(o);
        Int256Vector v = (Int256Vector)o;

        return VectorIntrinsics.compare(
            BT_ne, Int256Vector.class, Int256Mask.class, int.class, LENGTH,
            this, v,
            (v1, v2) -> v1.bTest(v2, (i, a, b) -> a != b));
    }

    @Override
    @ForceInline
    public Int256Mask lessThan(Vector<Integer, Shapes.S256Bit> o) {
        Objects.requireNonNull(o);
        Int256Vector v = (Int256Vector)o;

        return VectorIntrinsics.compare(
            BT_lt, Int256Vector.class, Int256Mask.class, int.class, LENGTH,
            this, v,
            (v1, v2) -> v1.bTest(v2, (i, a, b) -> a < b));
    }

    @Override
    @ForceInline
    public Int256Mask lessThanEq(Vector<Integer, Shapes.S256Bit> o) {
        Objects.requireNonNull(o);
        Int256Vector v = (Int256Vector)o;

        return VectorIntrinsics.compare(
            BT_le, Int256Vector.class, Int256Mask.class, int.class, LENGTH,
            this, v,
            (v1, v2) -> v1.bTest(v2, (i, a, b) -> a <= b));
    }

    @Override
    @ForceInline
    public Int256Mask greaterThan(Vector<Integer, Shapes.S256Bit> o) {
        Objects.requireNonNull(o);
        Int256Vector v = (Int256Vector)o;

        return (Int256Mask) VectorIntrinsics.compare(
            BT_gt, Int256Vector.class, Int256Mask.class, int.class, LENGTH,
            this, v,
            (v1, v2) -> v1.bTest(v2, (i, a, b) -> a > b));
    }

    @Override
    @ForceInline
    public Int256Mask greaterThanEq(Vector<Integer, Shapes.S256Bit> o) {
        Objects.requireNonNull(o);
        Int256Vector v = (Int256Vector)o;

        return VectorIntrinsics.compare(
            BT_ge, Int256Vector.class, Int256Mask.class, int.class, LENGTH,
            this, v,
            (v1, v2) -> v1.bTest(v2, (i, a, b) -> a >= b));
    }

    // Foreach

    @Override
    void forEach(FUnCon f) {
        int[] vec = getElements();
        for (int i = 0; i < length(); i++) {
            f.apply(i, vec[i]);
        }
    }

    @Override
    void forEach(Mask<Integer, Shapes.S256Bit> o, FUnCon f) {
        boolean[] mbits = ((Int256Mask)o).getBits();
        forEach((i, a) -> {
            if (mbits[i]) { f.apply(i, a); }
        });
    }


    Float256Vector toFP() {
        int[] vec = getElements();
        float[] res = new float[this.species().length()];
        for(int i = 0; i < this.species().length(); i++){
            res[i] = Float.intBitsToFloat(vec[i]);
        }
        return new Float256Vector(res);
    }

    @Override
    public Int256Vector rotateEL(int j) {
        int[] vec = getElements();
        int[] res = new int[length()];
        for (int i = 0; i < length(); i++){
            res[(j + i) % length()] = vec[i];
        }
        return new Int256Vector(res);
    }

    @Override
    public Int256Vector rotateER(int j) {
        int[] vec = getElements();
        int[] res = new int[length()];
        for (int i = 0; i < length(); i++){
            int z = i - j;
            if(j < 0) {
                res[length() + z] = vec[i];
            } else {
                res[z] = vec[i];
            }
        }
        return new Int256Vector(res);
    }

    @Override
    public Int256Vector shiftEL(int j) {
        int[] vec = getElements();
        int[] res = new int[length()];
        for (int i = 0; i < length() - j; i++) {
            res[i] = vec[i + j];
        }
        return new Int256Vector(res);
    }

    @Override
    public Int256Vector shiftER(int j) {
        int[] vec = getElements();
        int[] res = new int[length()];
        for (int i = 0; i < length() - j; i++){
            res[i + j] = vec[i];
        }
        return new Int256Vector(res);
    }

    @Override
    @ForceInline
    public Int256Vector rearrange(Vector<Integer, Shapes.S256Bit> v,
                                  Shuffle<Integer, Shapes.S256Bit> s, Mask<Integer, Shapes.S256Bit> m) {
        return this.rearrange(s).blend(v.rearrange(s), m);
    }

    @Override
    public Int256Vector rearrange(Shuffle<Integer, Shapes.S256Bit> s) {
        return uOp((i, a) -> {
            int[] vec = this.getElements();
            int ei = s.getElement(i);
            return vec[ei];
        });
    }

    @Override
    @ForceInline
    public Int256Vector blend(Vector<Integer, Shapes.S256Bit> o1, Mask<Integer, Shapes.S256Bit> o2) {
        Objects.requireNonNull(o1);
        Objects.requireNonNull(o2);
        Int256Vector v = (Int256Vector)o1;
        Int256Mask   m = (Int256Mask)o2;

        return VectorIntrinsics.blend(
            Int256Vector.class, Int256Mask.class, int.class, LENGTH,
            this, v, m,
            (v1, v2, m_) -> v1.bOp(v2, (i, a, b) -> m_.getElement(i) ? b : a));
    }

    // Accessors

    @Override
    public int get(int i) {
        if (i < 0 || i >= LENGTH) {
            throw new IllegalArgumentException("Index " + i + " must be zero or positive, and less than " + LENGTH);
        }
        return (int) VectorIntrinsics.extract(
                                Int256Vector.class, int.class, LENGTH,
                                this, i,
                                (vec, ix) -> {
                                    int[] vecarr = vec.getElements();
                                    return (long)vecarr[ix];
                                });
    }

    @Override
    public Int256Vector with(int i, int e) {
        if (i < 0 || i >= LENGTH) {
            throw new IllegalArgumentException("Index " + i + " must be zero or positive, and less than " + LENGTH);
        }
        return VectorIntrinsics.insert(
                                Int256Vector.class, int.class, LENGTH,
                                this, i, (long)e,
                                (v, ix, bits) -> {
                                    int[] res = v.getElements().clone();
                                    res[ix] = (int)bits;
                                    return new Int256Vector(res);
                                });
    }

    // Mask

    static final class Int256Mask extends AbstractMask<Integer, Shapes.S256Bit> {
        static final Int256Mask TRUE_MASK = new Int256Mask(true);
        static final Int256Mask FALSE_MASK = new Int256Mask(false);

        // FIXME: was temporarily put here to simplify rematerialization support in the JVM
        private final boolean[] bits; // Don't access directly, use getBits() instead.

        public Int256Mask(boolean[] bits) {
            this(bits, 0);
        }

        public Int256Mask(boolean[] bits, int offset) {
            boolean[] a = new boolean[species().length()];
            for (int i = 0; i < a.length; i++) {
                a[i] = bits[offset + i];
            }
            this.bits = a;
        }

        public Int256Mask(boolean val) {
            boolean[] bits = new boolean[species().length()];
            Arrays.fill(bits, val);
            this.bits = bits;
        }

        boolean[] getBits() {
            return VectorIntrinsics.maybeRebox(this).bits;
        }

        @Override
        Int256Mask uOp(MUnOp f) {
            boolean[] res = new boolean[species().length()];
            boolean[] bits = getBits();
            for (int i = 0; i < species().length(); i++) {
                res[i] = f.apply(i, bits[i]);
            }
            return new Int256Mask(res);
        }

        @Override
        Int256Mask bOp(Mask<Integer, Shapes.S256Bit> o, MBinOp f) {
            boolean[] res = new boolean[species().length()];
            boolean[] bits = getBits();
            boolean[] mbits = ((Int256Mask)o).getBits();
            for (int i = 0; i < species().length(); i++) {
                res[i] = f.apply(i, bits[i], mbits[i]);
            }
            return new Int256Mask(res);
        }

        @Override
        public Int256Species species() {
            return SPECIES;
        }

        @Override
        public Int256Vector toVector() {
            int[] res = new int[species().length()];
            boolean[] bits = getBits();
            for (int i = 0; i < species().length(); i++) {
                // -1 will result in the most significant bit being set in
                // addition to some or all other bits
                res[i] = (int) (bits[i] ? -1 : 0);
            }
            return new Int256Vector(res);
        }

        // Unary operations

        @Override
        @ForceInline
        public Int256Mask not() {
            return (Int256Mask) VectorIntrinsics.unaryOp(
                                             VECTOR_OP_NOT, Int256Mask.class, int.class, LENGTH,
                                             this,
                                             (m1) -> m1.uOp((i, a) -> !a));
        }

        // Binary operations

        @Override
        @ForceInline
        public Int256Mask and(Mask<Integer,Shapes.S256Bit> o) {
            Objects.requireNonNull(o);
            Int256Mask m = (Int256Mask)o;
            return VectorIntrinsics.binaryOp(VECTOR_OP_AND, Int256Mask.class, int.class, LENGTH,
                                             this, m,
                                             (m1, m2) -> m1.bOp(m2, (i, a, b) -> a & b));
        }

        @Override
        @ForceInline
        public Int256Mask or(Mask<Integer,Shapes.S256Bit> o) {
            Objects.requireNonNull(o);
            Int256Mask m = (Int256Mask)o;
            return VectorIntrinsics.binaryOp(VECTOR_OP_OR, Int256Mask.class, int.class, LENGTH,
                                             this, m,
                                             (m1, m2) -> m1.bOp(m2, (i, a, b) -> a | b));
        }

        // Reductions

        @Override
        @ForceInline
        public boolean anyTrue() {
            return VectorIntrinsics.test(COND_notZero, Int256Mask.class, int.class, LENGTH,
                                         this, this,
                                         (m1, m2) -> super.anyTrue());
        }

        @Override
        @ForceInline
        public boolean allTrue() {
            return VectorIntrinsics.test(COND_carrySet, Int256Mask.class, int.class, LENGTH,
                                         this, species().maskAllTrue(),
                                         (m1, m2) -> super.allTrue());
        }
    }

    // Shuffle

    static final class Int256Shuffle extends AbstractShuffle<Integer, Shapes.S256Bit> {
        Int256Shuffle(byte[] reorder) {
            super(reorder);
        }

        public Int256Shuffle(int[] reorder) {
            super(reorder);
        }

        public Int256Shuffle(int[] reorder, int i) {
            super(reorder, i);
        }

        public Int256Shuffle(IntUnaryOperator f) {
            super(f);
        }

        @Override
        public Int256Species species() {
            return SPECIES;
        }

        @Override
        public Int256Vector toVector() {
            int[] va = new int[SPECIES.length()];
            for (int i = 0; i < va.length; i++) {
              va[i] = (int) getElement(i);
            }
            return species().fromArray(va, 0);
        }

        @Override
        public Int256Shuffle rearrange(Vector.Shuffle<Integer, Shapes.S256Bit> o) {
            Int256Shuffle s = (Int256Shuffle) o;
            byte[] r = new byte[reorder.length];
            for (int i = 0; i < reorder.length; i++) {
                r[i] = reorder[s.reorder[i]];
            }
            return new Int256Shuffle(r);
        }
    }

    // Species

    @Override
    public Int256Species species() {
        return SPECIES;
    }

    static final class Int256Species extends IntSpecies<Shapes.S256Bit> {
        static final int BIT_SIZE = Shapes.S_256_BIT.bitSize();

        static final int LENGTH = BIT_SIZE / Integer.SIZE;

        @Override
        public String toString() {
           StringBuilder sb = new StringBuilder("Shape[");
           sb.append(bitSize()).append(" bits, ");
           sb.append(length()).append(" ").append(int.class.getSimpleName()).append("s x ");
           sb.append(elementSize()).append(" bits");
           sb.append("]");
           return sb.toString();
        }

        @Override
        @ForceInline
        public int bitSize() {
            return BIT_SIZE;
        }

        @Override
        @ForceInline
        public int length() {
            return LENGTH;
        }

        @Override
        @ForceInline
        public Class<Integer> elementType() {
            return int.class;
        }

        @Override
        @ForceInline
        public int elementSize() {
            return Integer.SIZE;
        }

        @Override
        @ForceInline
        public Shapes.S256Bit shape() {
            return Shapes.S_256_BIT;
        }

        @Override
        Int256Vector op(FOp f) {
            int[] res = new int[length()];
            for (int i = 0; i < length(); i++) {
                res[i] = f.apply(i);
            }
            return new Int256Vector(res);
        }

        @Override
        Int256Vector op(Mask<Integer, Shapes.S256Bit> o, FOp f) {
            int[] res = new int[length()];
            boolean[] mbits = ((Int256Mask)o).getBits();
            for (int i = 0; i < length(); i++) {
                if (mbits[i]) {
                    res[i] = f.apply(i);
                }
            }
            return new Int256Vector(res);
        }

        // Factories

        @Override
        public Int256Mask maskFromValues(boolean... bits) {
            return new Int256Mask(bits);
        }

        @Override
        public Int256Mask maskFromArray(boolean[] bits, int i) {
            return new Int256Mask(bits, i);
        }

        @Override
        public Int256Shuffle shuffle(IntUnaryOperator f) {
            return new Int256Shuffle(f);
        }

        @Override
        public Int256Shuffle shuffleIota() {
            return new Int256Shuffle(AbstractShuffle.IDENTITY);
        }

        @Override
        public Int256Shuffle shuffleFromValues(int... ixs) {
            return new Int256Shuffle(ixs);
        }

        @Override
        public Int256Shuffle shuffleFromArray(int[] ixs, int i) {
            return new Int256Shuffle(ixs, i);
        }

        @Override
        @ForceInline
        public Int256Vector zero() {
            return VectorIntrinsics.broadcastCoerced(Int256Vector.class, int.class, LENGTH,
                                                     0,
                                                     (z -> ZERO));
        }

        @Override
        @ForceInline
        public Int256Vector broadcast(int e) {
            return VectorIntrinsics.broadcastCoerced(
                Int256Vector.class, int.class, LENGTH,
                e,
                ((long bits) -> SPECIES.op(i -> (int)bits)));
        }

        @Override
        @ForceInline
        public Int256Mask maskAllTrue() {
            return VectorIntrinsics.broadcastCoerced(Int256Mask.class, int.class, LENGTH,
                                                     (int)-1,
                                                     (z -> Int256Mask.TRUE_MASK));
        }

        @Override
        @ForceInline
        public Int256Mask maskAllFalse() {
            return VectorIntrinsics.broadcastCoerced(Int256Mask.class, int.class, LENGTH,
                                                     0,
                                                     (z -> Int256Mask.FALSE_MASK));
        }

        @Override
        @ForceInline
        public Int256Vector scalars(int... es) {
            Objects.requireNonNull(es);
            int ix = VectorIntrinsics.checkIndex(0, es.length, LENGTH);
            return VectorIntrinsics.load(Int256Vector.class, int.class, LENGTH,
                                         es, Unsafe.ARRAY_INT_BASE_OFFSET,
                                         es, ix,
                                         (c, idx) -> op(n -> c[idx + n]));
        }

        @Override
        @ForceInline
        public Int256Vector fromArray(int[] a, int ix) {
            Objects.requireNonNull(a);
            ix = VectorIntrinsics.checkIndex(ix, a.length, LENGTH);
            return VectorIntrinsics.load(Int256Vector.class, int.class, LENGTH,
                                         a, (((long) ix) << ARRAY_SHIFT) + Unsafe.ARRAY_INT_BASE_OFFSET,
                                         a, ix,
                                         (c, idx) -> op(n -> c[idx + n]));
        }

        @Override
        @ForceInline
        public Int256Vector fromArray(int[] a, int ax, Mask<Integer, Shapes.S256Bit> m) {
            // @@@ This can result in out of bounds errors for unset mask lanes
            return zero().blend(fromArray(a, ax), m);
        }

        @Override
        @ForceInline
        public Int256Vector fromByteArray(byte[] a, int ix) {
            // @@@ Endianess
            Objects.requireNonNull(a);
            ix = VectorIntrinsics.checkIndex(ix, a.length, bitSize() / Byte.SIZE);
            return VectorIntrinsics.load(Int256Vector.class, int.class, LENGTH,
                                         a, ((long) ix) + Unsafe.ARRAY_BYTE_BASE_OFFSET,
                                         a, ix,
                                         (c, idx) -> {
                                             ByteBuffer bbc = ByteBuffer.wrap(c, idx, a.length - idx).order(ByteOrder.nativeOrder());
                                             IntBuffer tb = bbc.asIntBuffer();
                                             return op(i -> tb.get());
                                         });
        }

        @Override
        @ForceInline
        public Int256Vector fromByteArray(byte[] a, int ix, Mask<Integer, Shapes.S256Bit> m) {
            // @@@ This can result in out of bounds errors for unset mask lanes
            return zero().blend(fromByteArray(a, ix), m);
        }

        @Override
        @ForceInline
        public Int256Vector fromByteBuffer(ByteBuffer bb, int ix) {
            // @@@ Endianess
            if (bb.order() != ByteOrder.nativeOrder()) {
                throw new IllegalArgumentException();
            }
            ix = VectorIntrinsics.checkIndex(ix, bb.limit(), bitSize() / Byte.SIZE);
            return VectorIntrinsics.load(Int256Vector.class, int.class, LENGTH,
                                         U.getObject(bb, BYTE_BUFFER_HB), U.getLong(bb, BUFFER_ADDRESS) + ix,
                                         bb, ix,
                                         (c, idx) -> {
                                             ByteBuffer bbc = c.duplicate().position(idx).order(ByteOrder.nativeOrder());
                                             IntBuffer tb = bbc.asIntBuffer();
                                             return op(i -> tb.get());
                                         });
        }

        @Override
        @ForceInline
        public Int256Vector fromByteBuffer(ByteBuffer bb, int ix, Mask<Integer, Shapes.S256Bit> m) {
            // @@@ This can result in out of bounds errors for unset mask lanes
            return zero().blend(fromByteBuffer(bb, ix), m);
        }

        @ForceInline
        @SuppressWarnings("unchecked")
        private <S extends Shape> Int256Vector castFromByte(ByteVector<S> o) {
            if (o.bitSize() == 64) {
                Byte64Vector so = (Byte64Vector)o;
                return VectorIntrinsics.cast(
                    Byte64Vector.class, byte.class, so.length(),
                    int.class, LENGTH, so,
                    (v, t) -> (Int256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 128) {
                Byte128Vector so = (Byte128Vector)o;
                return VectorIntrinsics.cast(
                    Byte128Vector.class, byte.class, so.length(),
                    int.class, LENGTH, so,
                    (v, t) -> (Int256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 256) {
                Byte256Vector so = (Byte256Vector)o;
                return VectorIntrinsics.cast(
                    Byte256Vector.class, byte.class, so.length(),
                    int.class, LENGTH, so,
                    (v, t) -> (Int256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 512) {
                Byte512Vector so = (Byte512Vector)o;
                return VectorIntrinsics.cast(
                    Byte512Vector.class, byte.class, so.length(),
                    int.class, LENGTH, so,
                    (v, t) -> (Int256Vector)super.cast(v)
                );
            } else {
                throw new InternalError("Unimplemented size");
            }
        }

        @ForceInline
        @SuppressWarnings("unchecked")
        private <S extends Shape> Int256Vector castFromShort(ShortVector<S> o) {
            if (o.bitSize() == 64) {
                Short64Vector so = (Short64Vector)o;
                return VectorIntrinsics.cast(
                    Short64Vector.class, short.class, so.length(),
                    int.class, LENGTH, so,
                    (v, t) -> (Int256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 128) {
                Short128Vector so = (Short128Vector)o;
                return VectorIntrinsics.cast(
                    Short128Vector.class, short.class, so.length(),
                    int.class, LENGTH, so,
                    (v, t) -> (Int256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 256) {
                Short256Vector so = (Short256Vector)o;
                return VectorIntrinsics.cast(
                    Short256Vector.class, short.class, so.length(),
                    int.class, LENGTH, so,
                    (v, t) -> (Int256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 512) {
                Short512Vector so = (Short512Vector)o;
                return VectorIntrinsics.cast(
                    Short512Vector.class, short.class, so.length(),
                    int.class, LENGTH, so,
                    (v, t) -> (Int256Vector)super.cast(v)
                );
            } else {
                throw new InternalError("Unimplemented size");
            }
        }

        @ForceInline
        @SuppressWarnings("unchecked")
        private <S extends Shape> Int256Vector castFromInt(IntVector<S> o) {
            if (o.bitSize() == 64) {
                Int64Vector so = (Int64Vector)o;
                return VectorIntrinsics.cast(
                    Int64Vector.class, int.class, so.length(),
                    int.class, LENGTH, so,
                    (v, t) -> (Int256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 128) {
                Int128Vector so = (Int128Vector)o;
                return VectorIntrinsics.cast(
                    Int128Vector.class, int.class, so.length(),
                    int.class, LENGTH, so,
                    (v, t) -> (Int256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 256) {
                Int256Vector so = (Int256Vector)o;
                return VectorIntrinsics.cast(
                    Int256Vector.class, int.class, so.length(),
                    int.class, LENGTH, so,
                    (v, t) -> (Int256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 512) {
                Int512Vector so = (Int512Vector)o;
                return VectorIntrinsics.cast(
                    Int512Vector.class, int.class, so.length(),
                    int.class, LENGTH, so,
                    (v, t) -> (Int256Vector)super.cast(v)
                );
            } else {
                throw new InternalError("Unimplemented size");
            }
        }

        @ForceInline
        @SuppressWarnings("unchecked")
        private <S extends Shape> Int256Vector castFromLong(LongVector<S> o) {
            if (o.bitSize() == 64) {
                Long64Vector so = (Long64Vector)o;
                return VectorIntrinsics.cast(
                    Long64Vector.class, long.class, so.length(),
                    int.class, LENGTH, so,
                    (v, t) -> (Int256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 128) {
                Long128Vector so = (Long128Vector)o;
                return VectorIntrinsics.cast(
                    Long128Vector.class, long.class, so.length(),
                    int.class, LENGTH, so,
                    (v, t) -> (Int256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 256) {
                Long256Vector so = (Long256Vector)o;
                return VectorIntrinsics.cast(
                    Long256Vector.class, long.class, so.length(),
                    int.class, LENGTH, so,
                    (v, t) -> (Int256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 512) {
                Long512Vector so = (Long512Vector)o;
                return VectorIntrinsics.cast(
                    Long512Vector.class, long.class, so.length(),
                    int.class, LENGTH, so,
                    (v, t) -> (Int256Vector)super.cast(v)
                );
            } else {
                throw new InternalError("Unimplemented size");
            }
        }

        @ForceInline
        @SuppressWarnings("unchecked")
        private <S extends Shape> Int256Vector castFromFloat(FloatVector<S> o) {
            if (o.bitSize() == 64) {
                Float64Vector so = (Float64Vector)o;
                return VectorIntrinsics.cast(
                    Float64Vector.class, float.class, so.length(),
                    int.class, LENGTH, so,
                    (v, t) -> (Int256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 128) {
                Float128Vector so = (Float128Vector)o;
                return VectorIntrinsics.cast(
                    Float128Vector.class, float.class, so.length(),
                    int.class, LENGTH, so,
                    (v, t) -> (Int256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 256) {
                Float256Vector so = (Float256Vector)o;
                return VectorIntrinsics.cast(
                    Float256Vector.class, float.class, so.length(),
                    int.class, LENGTH, so,
                    (v, t) -> (Int256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 512) {
                Float512Vector so = (Float512Vector)o;
                return VectorIntrinsics.cast(
                    Float512Vector.class, float.class, so.length(),
                    int.class, LENGTH, so,
                    (v, t) -> (Int256Vector)super.cast(v)
                );
            } else {
                throw new InternalError("Unimplemented size");
            }
        }

        @ForceInline
        @SuppressWarnings("unchecked")
        private <S extends Shape> Int256Vector castFromDouble(DoubleVector<S> o) {
            if (o.bitSize() == 64) {
                Double64Vector so = (Double64Vector)o;
                return VectorIntrinsics.cast(
                    Double64Vector.class, double.class, so.length(),
                    int.class, LENGTH, so,
                    (v, t) -> (Int256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 128) {
                Double128Vector so = (Double128Vector)o;
                return VectorIntrinsics.cast(
                    Double128Vector.class, double.class, so.length(),
                    int.class, LENGTH, so,
                    (v, t) -> (Int256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 256) {
                Double256Vector so = (Double256Vector)o;
                return VectorIntrinsics.cast(
                    Double256Vector.class, double.class, so.length(),
                    int.class, LENGTH, so,
                    (v, t) -> (Int256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 512) {
                Double512Vector so = (Double512Vector)o;
                return VectorIntrinsics.cast(
                    Double512Vector.class, double.class, so.length(),
                    int.class, LENGTH, so,
                    (v, t) -> (Int256Vector)super.cast(v)
                );
            } else {
                throw new InternalError("Unimplemented size");
            }
        }

        @Override
        @ForceInline
        @SuppressWarnings("unchecked")
        public <E, S extends Shape> Int256Vector cast(Vector<E, S> o) {
            if (o.length() != LENGTH)
                throw new IllegalArgumentException("Vector length this species length differ");

            if (o.elementType() == byte.class) {
                ByteVector<S> so = (ByteVector<S>)o;
                return castFromByte(so);
            } else if (o.elementType() == short.class) {
                ShortVector<S> so = (ShortVector<S>)o;
                return castFromShort(so);
            } else if (o.elementType() == int.class) {
                IntVector<S> so = (IntVector<S>)o;
                return castFromInt(so);
            } else if (o.elementType() == long.class) {
                LongVector<S> so = (LongVector<S>)o;
                return castFromLong(so);
            } else if (o.elementType() == float.class) {
                FloatVector<S> so = (FloatVector<S>)o;
                return castFromFloat(so);
            } else if (o.elementType() == double.class) {
                DoubleVector<S> so = (DoubleVector<S>)o;
                return castFromDouble(so);
            } else {
                throw new InternalError("Unimplemented type");
            }
        }

        @Override
        @ForceInline
        public <E, S extends Shape> Int256Mask cast(Mask<E, S> m) {
            if (m.length() != LENGTH)
                throw new IllegalArgumentException("Mask length this species length differ");
            return new Int256Mask(m.toArray());
        }

        @Override
        @ForceInline
        public <E, S extends Shape> Int256Shuffle cast(Shuffle<E, S> s) {
            if (s.length() != LENGTH)
                throw new IllegalArgumentException("Shuffle length this species length differ");
            return new Int256Shuffle(s.toArray());
        }

        @Override
        @ForceInline
        @SuppressWarnings("unchecked")
        public <F> Int256Vector rebracket(Vector<F, Shapes.S256Bit> o) {
            Objects.requireNonNull(o);
            if (o.elementType() == byte.class) {
                Byte256Vector so = (Byte256Vector)o;
                return VectorIntrinsics.reinterpret(
                    Byte256Vector.class, byte.class, so.length(),
                    int.class, LENGTH, so,
                    (v, t) -> (Int256Vector)reshape(v)
                );
            } else if (o.elementType() == short.class) {
                Short256Vector so = (Short256Vector)o;
                return VectorIntrinsics.reinterpret(
                    Short256Vector.class, short.class, so.length(),
                    int.class, LENGTH, so,
                    (v, t) -> (Int256Vector)reshape(v)
                );
            } else if (o.elementType() == int.class) {
                Int256Vector so = (Int256Vector)o;
                return VectorIntrinsics.reinterpret(
                    Int256Vector.class, int.class, so.length(),
                    int.class, LENGTH, so,
                    (v, t) -> (Int256Vector)reshape(v)
                );
            } else if (o.elementType() == long.class) {
                Long256Vector so = (Long256Vector)o;
                return VectorIntrinsics.reinterpret(
                    Long256Vector.class, long.class, so.length(),
                    int.class, LENGTH, so,
                    (v, t) -> (Int256Vector)reshape(v)
                );
            } else if (o.elementType() == float.class) {
                Float256Vector so = (Float256Vector)o;
                return VectorIntrinsics.reinterpret(
                    Float256Vector.class, float.class, so.length(),
                    int.class, LENGTH, so,
                    (v, t) -> (Int256Vector)reshape(v)
                );
            } else if (o.elementType() == double.class) {
                Double256Vector so = (Double256Vector)o;
                return VectorIntrinsics.reinterpret(
                    Double256Vector.class, double.class, so.length(),
                    int.class, LENGTH, so,
                    (v, t) -> (Int256Vector)reshape(v)
                );
            } else {
                throw new InternalError("Unimplemented type");
            }
        }

        @Override
        @ForceInline
        @SuppressWarnings("unchecked")
        public <T extends Shape> Int256Vector resize(Vector<Integer, T> o) {
            Objects.requireNonNull(o);
            if (o.bitSize() == 64) {
                Int64Vector so = (Int64Vector)o;
                return VectorIntrinsics.reinterpret(
                    Int64Vector.class, int.class, so.length(),
                    int.class, LENGTH, so,
                    (v, t) -> (Int256Vector)reshape(v)
                );
            } else if (o.bitSize() == 128) {
                Int128Vector so = (Int128Vector)o;
                return VectorIntrinsics.reinterpret(
                    Int128Vector.class, int.class, so.length(),
                    int.class, LENGTH, so,
                    (v, t) -> (Int256Vector)reshape(v)
                );
            } else if (o.bitSize() == 256) {
                Int256Vector so = (Int256Vector)o;
                return VectorIntrinsics.reinterpret(
                    Int256Vector.class, int.class, so.length(),
                    int.class, LENGTH, so,
                    (v, t) -> (Int256Vector)reshape(v)
                );
            } else if (o.bitSize() == 512) {
                Int512Vector so = (Int512Vector)o;
                return VectorIntrinsics.reinterpret(
                    Int512Vector.class, int.class, so.length(),
                    int.class, LENGTH, so,
                    (v, t) -> (Int256Vector)reshape(v)
                );
            } else {
                throw new InternalError("Unimplemented size");
            }
        }
    }
}
