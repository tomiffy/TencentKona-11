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
import java.util.Arrays;
import java.util.Objects;
import jdk.internal.vm.annotation.ForceInline;
import static jdk.incubator.vector.VectorIntrinsics.*;

@SuppressWarnings("cast")
final class Byte256Vector extends ByteVector<Shapes.S256Bit> {
    static final Byte256Species SPECIES = new Byte256Species();

    static final Byte256Vector ZERO = new Byte256Vector();

    static final int LENGTH = SPECIES.length();

    private final byte[] vec; // Don't access directly, use getElements() instead.

    private byte[] getElements() {
        return VectorIntrinsics.maybeRebox(this).vec;
    }

    Byte256Vector() {
        vec = new byte[SPECIES.length()];
    }

    Byte256Vector(byte[] v) {
        vec = v;
    }

    @Override
    public int length() { return LENGTH; }

    // Unary operator

    @Override
    Byte256Vector uOp(FUnOp f) {
        byte[] vec = getElements();
        byte[] res = new byte[length()];
        for (int i = 0; i < length(); i++) {
            res[i] = f.apply(i, vec[i]);
        }
        return new Byte256Vector(res);
    }

    @Override
    Byte256Vector uOp(Mask<Byte, Shapes.S256Bit> o, FUnOp f) {
        byte[] vec = getElements();
        byte[] res = new byte[length()];
        boolean[] mbits = ((Byte256Mask)o).getBits();
        for (int i = 0; i < length(); i++) {
            res[i] = mbits[i] ? f.apply(i, vec[i]) : vec[i];
        }
        return new Byte256Vector(res);
    }

    // Binary operator

    @Override
    Byte256Vector bOp(Vector<Byte, Shapes.S256Bit> o, FBinOp f) {
        byte[] res = new byte[length()];
        byte[] vec1 = this.getElements();
        byte[] vec2 = ((Byte256Vector)o).getElements();
        for (int i = 0; i < length(); i++) {
            res[i] = f.apply(i, vec1[i], vec2[i]);
        }
        return new Byte256Vector(res);
    }

    @Override
    Byte256Vector bOp(Vector<Byte, Shapes.S256Bit> o1, Mask<Byte, Shapes.S256Bit> o2, FBinOp f) {
        byte[] res = new byte[length()];
        byte[] vec1 = this.getElements();
        byte[] vec2 = ((Byte256Vector)o1).getElements();
        boolean[] mbits = ((Byte256Mask)o2).getBits();
        for (int i = 0; i < length(); i++) {
            res[i] = mbits[i] ? f.apply(i, vec1[i], vec2[i]) : vec1[i];
        }
        return new Byte256Vector(res);
    }

    // Trinary operator

    @Override
    Byte256Vector tOp(Vector<Byte, Shapes.S256Bit> o1, Vector<Byte, Shapes.S256Bit> o2, FTriOp f) {
        byte[] res = new byte[length()];
        byte[] vec1 = this.getElements();
        byte[] vec2 = ((Byte256Vector)o1).getElements();
        byte[] vec3 = ((Byte256Vector)o2).getElements();
        for (int i = 0; i < length(); i++) {
            res[i] = f.apply(i, vec1[i], vec2[i], vec3[i]);
        }
        return new Byte256Vector(res);
    }

    @Override
    Byte256Vector tOp(Vector<Byte, Shapes.S256Bit> o1, Vector<Byte, Shapes.S256Bit> o2, Mask<Byte, Shapes.S256Bit> o3, FTriOp f) {
        byte[] res = new byte[length()];
        byte[] vec1 = getElements();
        byte[] vec2 = ((Byte256Vector)o1).getElements();
        byte[] vec3 = ((Byte256Vector)o2).getElements();
        boolean[] mbits = ((Byte256Mask)o3).getBits();
        for (int i = 0; i < length(); i++) {
            res[i] = mbits[i] ? f.apply(i, vec1[i], vec2[i], vec3[i]) : vec1[i];
        }
        return new Byte256Vector(res);
    }

    @Override
    byte rOp(byte v, FBinOp f) {
        byte[] vec = getElements();
        for (int i = 0; i < length(); i++) {
            v = f.apply(i, v, vec[i]);
        }
        return v;
    }

    // Binary operations with scalars

    @Override
    @ForceInline
    public ByteVector<Shapes.S256Bit> add(byte o) {
        return add(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public ByteVector<Shapes.S256Bit> add(byte o, Mask<Byte,Shapes.S256Bit> m) {
        return add(SPECIES.broadcast(o), m);
    }

    @Override
    @ForceInline
    public ByteVector<Shapes.S256Bit> sub(byte o) {
        return sub(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public ByteVector<Shapes.S256Bit> sub(byte o, Mask<Byte,Shapes.S256Bit> m) {
        return sub(SPECIES.broadcast(o), m);
    }

    @Override
    @ForceInline
    public ByteVector<Shapes.S256Bit> mul(byte o) {
        return mul(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public ByteVector<Shapes.S256Bit> mul(byte o, Mask<Byte,Shapes.S256Bit> m) {
        return mul(SPECIES.broadcast(o), m);
    }

    @Override
    @ForceInline
    public ByteVector<Shapes.S256Bit> min(byte o) {
        return min(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public ByteVector<Shapes.S256Bit> max(byte o) {
        return max(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public Mask<Byte, Shapes.S256Bit> equal(byte o) {
        return equal(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public Mask<Byte, Shapes.S256Bit> notEqual(byte o) {
        return notEqual(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public Mask<Byte, Shapes.S256Bit> lessThan(byte o) {
        return lessThan(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public Mask<Byte, Shapes.S256Bit> lessThanEq(byte o) {
        return lessThanEq(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public Mask<Byte, Shapes.S256Bit> greaterThan(byte o) {
        return greaterThan(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public Mask<Byte, Shapes.S256Bit> greaterThanEq(byte o) {
        return greaterThanEq(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public ByteVector<Shapes.S256Bit> blend(byte o, Mask<Byte,Shapes.S256Bit> m) {
        return blend(SPECIES.broadcast(o), m);
    }


    @Override
    @ForceInline
    public ByteVector<Shapes.S256Bit> and(byte o) {
        return and(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public ByteVector<Shapes.S256Bit> and(byte o, Mask<Byte,Shapes.S256Bit> m) {
        return and(SPECIES.broadcast(o), m);
    }

    @Override
    @ForceInline
    public ByteVector<Shapes.S256Bit> or(byte o) {
        return or(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public ByteVector<Shapes.S256Bit> or(byte o, Mask<Byte,Shapes.S256Bit> m) {
        return or(SPECIES.broadcast(o), m);
    }

    @Override
    @ForceInline
    public ByteVector<Shapes.S256Bit> xor(byte o) {
        return xor(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public ByteVector<Shapes.S256Bit> xor(byte o, Mask<Byte,Shapes.S256Bit> m) {
        return xor(SPECIES.broadcast(o), m);
    }

    @Override
    @ForceInline
    public Byte256Vector neg() {
        return SPECIES.zero().sub(this);
    }

    // Unary operations

    @Override
    @ForceInline
    public Byte256Vector abs() {
        return (Byte256Vector) VectorIntrinsics.unaryOp(
            VECTOR_OP_ABS, Byte256Vector.class, byte.class, LENGTH,
            this,
            v1 -> ((Byte256Vector)v1).uOp((i, a) -> (byte) Math.abs(a)));
    }


    @Override
    @ForceInline
    public Byte256Vector not() {
        return (Byte256Vector) VectorIntrinsics.unaryOp(
            VECTOR_OP_NOT, Byte256Vector.class, byte.class, LENGTH,
            this,
            v1 -> ((Byte256Vector)v1).uOp((i, a) -> (byte) ~a));
    }
    // Binary operations

    @Override
    @ForceInline
    public Byte256Vector add(Vector<Byte,Shapes.S256Bit> o) {
        Objects.requireNonNull(o);
        Byte256Vector v = (Byte256Vector)o;
        return (Byte256Vector) VectorIntrinsics.binaryOp(
            VECTOR_OP_ADD, Byte256Vector.class, byte.class, LENGTH,
            this, v,
            (v1, v2) -> ((Byte256Vector)v1).bOp(v2, (i, a, b) -> (byte)(a + b)));
    }

    @Override
    @ForceInline
    public Byte256Vector sub(Vector<Byte,Shapes.S256Bit> o) {
        Objects.requireNonNull(o);
        Byte256Vector v = (Byte256Vector)o;
        return (Byte256Vector) VectorIntrinsics.binaryOp(
            VECTOR_OP_SUB, Byte256Vector.class, byte.class, LENGTH,
            this, v,
            (v1, v2) -> ((Byte256Vector)v1).bOp(v2, (i, a, b) -> (byte)(a - b)));
    }

    @Override
    @ForceInline
    public Byte256Vector mul(Vector<Byte,Shapes.S256Bit> o) {
        Objects.requireNonNull(o);
        Byte256Vector v = (Byte256Vector)o;
        return (Byte256Vector) VectorIntrinsics.binaryOp(
            VECTOR_OP_MUL, Byte256Vector.class, byte.class, LENGTH,
            this, v,
            (v1, v2) -> ((Byte256Vector)v1).bOp(v2, (i, a, b) -> (byte)(a * b)));
    }

    @Override
    @ForceInline
    public Byte256Vector min(Vector<Byte,Shapes.S256Bit> o) {
        Objects.requireNonNull(o);
        Byte256Vector v = (Byte256Vector)o;
        return (Byte256Vector) VectorIntrinsics.binaryOp(
            VECTOR_OP_MIN, Byte256Vector.class, byte.class, LENGTH,
            this, v,
            (v1, v2) -> ((Byte256Vector)v1).bOp(v2, (i, a, b) -> (byte) ((a < b) ? a : b)));
    }

    @Override
    @ForceInline
    public Byte256Vector max(Vector<Byte,Shapes.S256Bit> o) {
        Objects.requireNonNull(o);
        Byte256Vector v = (Byte256Vector)o;
        return (Byte256Vector) VectorIntrinsics.binaryOp(
            VECTOR_OP_MAX, Byte256Vector.class, byte.class, LENGTH,
            this, v,
            (v1, v2) -> ((Byte256Vector)v1).bOp(v2, (i, a, b) -> (byte) ((a > b) ? a : b)));
        }



    @Override
    @ForceInline
    public Byte256Vector and(Vector<Byte,Shapes.S256Bit> o) {
        Objects.requireNonNull(o);
        Byte256Vector v = (Byte256Vector)o;
        return (Byte256Vector) VectorIntrinsics.binaryOp(
            VECTOR_OP_AND, Byte256Vector.class, byte.class, LENGTH,
            this, v,
            (v1, v2) -> ((Byte256Vector)v1).bOp(v2, (i, a, b) -> (byte)(a & b)));
    }

    @Override
    @ForceInline
    public Byte256Vector or(Vector<Byte,Shapes.S256Bit> o) {
        Objects.requireNonNull(o);
        Byte256Vector v = (Byte256Vector)o;
        return (Byte256Vector) VectorIntrinsics.binaryOp(
            VECTOR_OP_OR, Byte256Vector.class, byte.class, LENGTH,
            this, v,
            (v1, v2) -> ((Byte256Vector)v1).bOp(v2, (i, a, b) -> (byte)(a | b)));
    }

    @Override
    @ForceInline
    public Byte256Vector xor(Vector<Byte,Shapes.S256Bit> o) {
        Objects.requireNonNull(o);
        Byte256Vector v = (Byte256Vector)o;
        return (Byte256Vector) VectorIntrinsics.binaryOp(
            VECTOR_OP_XOR, Byte256Vector.class, byte.class, LENGTH,
            this, v,
            (v1, v2) -> ((Byte256Vector)v1).bOp(v2, (i, a, b) -> (byte)(a ^ b)));
    }

    @Override
    @ForceInline
    public Byte256Vector and(Vector<Byte,Shapes.S256Bit> v, Mask<Byte, Shapes.S256Bit> m) {
        return blend(and(v), m);
    }

    @Override
    @ForceInline
    public Byte256Vector or(Vector<Byte,Shapes.S256Bit> v, Mask<Byte, Shapes.S256Bit> m) {
        return blend(or(v), m);
    }

    @Override
    @ForceInline
    public Byte256Vector xor(Vector<Byte,Shapes.S256Bit> v, Mask<Byte, Shapes.S256Bit> m) {
        return blend(xor(v), m);
    }

    // Ternary operations


    // Type specific horizontal reductions

    @Override
    @ForceInline
    public byte addAll() {
        return (byte) VectorIntrinsics.reductionCoerced(
            VECTOR_OP_ADD, Byte256Vector.class, byte.class, LENGTH,
            this,
            v -> (long) v.rOp((byte) 0, (i, a, b) -> (byte) (a + b)));
    }

    @Override
    @ForceInline
    public byte andAll() {
        return (byte) VectorIntrinsics.reductionCoerced(
            VECTOR_OP_AND, Byte256Vector.class, byte.class, LENGTH,
            this,
            v -> (long) v.rOp((byte) -1, (i, a, b) -> (byte) (a & b)));
    }

    @Override
    @ForceInline
    public byte andAll(Mask<Byte, Shapes.S256Bit> m) {
        return blend(SPECIES.broadcast((byte) -1), m).andAll();
    }

    @Override
    @ForceInline
    public byte minAll() {
        return (byte) VectorIntrinsics.reductionCoerced(
            VECTOR_OP_MIN, Byte256Vector.class, byte.class, LENGTH,
            this,
            v -> (long) v.rOp(Byte.MAX_VALUE , (i, a, b) -> (byte) ((a < b) ? a : b)));
    }

    @Override
    @ForceInline
    public byte maxAll() {
        return (byte) VectorIntrinsics.reductionCoerced(
            VECTOR_OP_MAX, Byte256Vector.class, byte.class, LENGTH,
            this,
            v -> (long) v.rOp(Byte.MIN_VALUE , (i, a, b) -> (byte) ((a > b) ? a : b)));
    }

    @Override
    @ForceInline
    public byte mulAll() {
        return (byte) VectorIntrinsics.reductionCoerced(
            VECTOR_OP_MUL, Byte256Vector.class, byte.class, LENGTH,
            this,
            v -> (long) v.rOp((byte) 1, (i, a, b) -> (byte) (a * b)));
    }

    @Override
    @ForceInline
    public byte subAll() {
        return (byte) VectorIntrinsics.reductionCoerced(
            VECTOR_OP_SUB, Byte256Vector.class, byte.class, LENGTH,
            this,
            v -> (long) v.rOp((byte) 0, (i, a, b) -> (byte) (a - b)));
    }

    @Override
    @ForceInline
    public byte orAll() {
        return (byte) VectorIntrinsics.reductionCoerced(
            VECTOR_OP_OR, Byte256Vector.class, byte.class, LENGTH,
            this,
            v -> (long) v.rOp((byte) 0, (i, a, b) -> (byte) (a | b)));
    }

    @Override
    @ForceInline
    public byte orAll(Mask<Byte, Shapes.S256Bit> m) {
        return blend(SPECIES.broadcast((byte) 0), m).orAll();
    }

    @Override
    @ForceInline
    public byte xorAll() {
        return (byte) VectorIntrinsics.reductionCoerced(
            VECTOR_OP_XOR, Byte256Vector.class, byte.class, LENGTH,
            this,
            v -> (long) v.rOp((byte) 0, (i, a, b) -> (byte) (a ^ b)));
    }

    @Override
    @ForceInline
    public byte xorAll(Mask<Byte, Shapes.S256Bit> m) {
        return blend(SPECIES.broadcast((byte) 0), m).xorAll();
    }


    @Override
    @ForceInline
    public byte addAll(Mask<Byte, Shapes.S256Bit> m) {
        return blend(SPECIES.broadcast((byte) 0), m).addAll();
    }

    @Override
    @ForceInline
    public byte subAll(Mask<Byte, Shapes.S256Bit> m) {
        return blend(SPECIES.broadcast((byte) 0), m).subAll();
    }

    @Override
    @ForceInline
    public byte mulAll(Mask<Byte, Shapes.S256Bit> m) {
        return blend(SPECIES.broadcast((byte) 1), m).mulAll();
    }

    @Override
    @ForceInline
    public byte minAll(Mask<Byte, Shapes.S256Bit> m) {
        return blend(SPECIES.broadcast(Byte.MAX_VALUE), m).minAll();
    }

    @Override
    @ForceInline
    public byte maxAll(Mask<Byte, Shapes.S256Bit> m) {
        return blend(SPECIES.broadcast(Byte.MIN_VALUE), m).maxAll();
    }

    // Memory operations

    @Override
    @ForceInline
    public void intoArray(byte[] a, int ix) {
        Objects.requireNonNull(a);
        ix = VectorIntrinsics.checkIndex(ix, a.length, LENGTH);
        VectorIntrinsics.store(Byte256Vector.class, byte.class, LENGTH,
                               a, ix, this,
                               (arr, idx, v) -> v.forEach((i, a_) -> ((byte[])arr)[idx + i] = a_));
    }

    @Override
    @ForceInline
    public void intoArray(byte[] a, int ax, Mask<Byte, Shapes.S256Bit> m) {
        // TODO: use better default impl: forEach(m, (i, a_) -> a[ax + i] = a_);
        Byte256Vector oldVal = SPECIES.fromArray(a, ax);
        Byte256Vector newVal = oldVal.blend(this, m);
        newVal.intoArray(a, ax);
    }

    @Override
    @ForceInline
    public void intoByteArray(byte[] a, int ix) {
        Objects.requireNonNull(a);
        ix = VectorIntrinsics.checkIndex(ix, a.length, bitSize() / Byte.SIZE);
        VectorIntrinsics.store(Byte256Vector.class, byte.class, LENGTH,
                               a, ix, this,
                               (arr, idx, v) -> {
                                   byte[] tarr = (byte[])arr;
                                   ByteBuffer bb = ByteBuffer.wrap(tarr, idx, tarr.length - idx).order(ByteOrder.nativeOrder());
                                   ByteBuffer fb = bb;
                                   v.forEach((i, e) -> fb.put(e));
                               });
    }

    @Override
    @ForceInline
    public void intoByteArray(byte[] a, int ix, Mask<Byte, Shapes.S256Bit> m) {
        Byte256Vector oldVal = SPECIES.fromByteArray(a, ix);
        Byte256Vector newVal = oldVal.blend(this, m);
        newVal.intoByteArray(a, ix);
    }

    @Override
    @ForceInline
    public void intoByteBuffer(ByteBuffer bb, int ix) {
        if (bb.hasArray() && !bb.isReadOnly() && bb.order() == ByteOrder.nativeOrder()) {
            int num_bytes = bitSize() / Byte.SIZE;
            int ax = VectorIntrinsics.checkIndex(ix, bb.limit(), num_bytes);
            VectorIntrinsics.store(Byte256Vector.class, byte.class, LENGTH,
                                   bb.array(), ax, this,
                                   (arr, idx, v) -> {
                                       byte[] tarr = (byte[])arr;
                                       ByteBuffer lbb = ByteBuffer.wrap(tarr, idx, tarr.length - idx).order(ByteOrder.nativeOrder());
                                       ByteBuffer fb = lbb;
                                       v.forEach((i, e) -> fb.put(e));
                                   });
        } else {
            super.intoByteBuffer(bb, ix);
        }
    }

    @Override
    @ForceInline
    public void intoByteBuffer(ByteBuffer bb, int ix, Mask<Byte, Shapes.S256Bit> m) {
        Byte256Vector oldVal = SPECIES.fromByteBuffer(bb, ix);
        Byte256Vector newVal = oldVal.blend(this, m);
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

        Byte256Vector that = (Byte256Vector) o;
        return Arrays.equals(this.getElements(), that.getElements());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(vec);
    }

    // Binary test

    @Override
    Byte256Mask bTest(Vector<Byte, Shapes.S256Bit> o, FBinTest f) {
        byte[] vec1 = getElements();
        byte[] vec2 = ((Byte256Vector)o).getElements();
        boolean[] bits = new boolean[length()];
        for (int i = 0; i < length(); i++){
            bits[i] = f.apply(i, vec1[i], vec2[i]);
        }
        return new Byte256Mask(bits);
    }

    // Comparisons

    @Override
    @ForceInline
    public Byte256Mask equal(Vector<Byte, Shapes.S256Bit> o) {
        Objects.requireNonNull(o);
        Byte256Vector v = (Byte256Vector)o;

        return (Byte256Mask) VectorIntrinsics.compare(
            BT_eq, Byte256Vector.class, Byte256Mask.class, byte.class, LENGTH,
            this, v,
            (v1, v2) -> v1.bTest(v2, (i, a, b) -> a == b));
    }

    @Override
    @ForceInline
    public Byte256Mask notEqual(Vector<Byte, Shapes.S256Bit> o) {
        Objects.requireNonNull(o);
        Byte256Vector v = (Byte256Vector)o;

        return (Byte256Mask) VectorIntrinsics.compare(
            BT_ne, Byte256Vector.class, Byte256Mask.class, byte.class, LENGTH,
            this, v,
            (v1, v2) -> v1.bTest(v2, (i, a, b) -> a != b));
    }

    @Override
    @ForceInline
    public Byte256Mask lessThan(Vector<Byte, Shapes.S256Bit> o) {
        Objects.requireNonNull(o);
        Byte256Vector v = (Byte256Vector)o;

        return (Byte256Mask) VectorIntrinsics.compare(
            BT_lt, Byte256Vector.class, Byte256Mask.class, byte.class, LENGTH,
            this, v,
            (v1, v2) -> v1.bTest(v2, (i, a, b) -> a < b));
    }

    @Override
    @ForceInline
    public Byte256Mask lessThanEq(Vector<Byte, Shapes.S256Bit> o) {
        Objects.requireNonNull(o);
        Byte256Vector v = (Byte256Vector)o;

        return (Byte256Mask) VectorIntrinsics.compare(
            BT_le, Byte256Vector.class, Byte256Mask.class, byte.class, LENGTH,
            this, v,
            (v1, v2) -> v1.bTest(v2, (i, a, b) -> a <= b));
    }

    @Override
    @ForceInline
    public Byte256Mask greaterThan(Vector<Byte, Shapes.S256Bit> o) {
        Objects.requireNonNull(o);
        Byte256Vector v = (Byte256Vector)o;

        return (Byte256Mask) VectorIntrinsics.compare(
            BT_gt, Byte256Vector.class, Byte256Mask.class, byte.class, LENGTH,
            this, v,
            (v1, v2) -> v1.bTest(v2, (i, a, b) -> a > b));
    }

    @Override
    @ForceInline
    public Byte256Mask greaterThanEq(Vector<Byte, Shapes.S256Bit> o) {
        Objects.requireNonNull(o);
        Byte256Vector v = (Byte256Vector)o;

        return (Byte256Mask) VectorIntrinsics.compare(
            BT_ge, Byte256Vector.class, Byte256Mask.class, byte.class, LENGTH,
            this, v,
            (v1, v2) -> v1.bTest(v2, (i, a, b) -> a >= b));
    }

    // Foreach

    @Override
    void forEach(FUnCon f) {
        byte[] vec = getElements();
        for (int i = 0; i < length(); i++) {
            f.apply(i, vec[i]);
        }
    }

    @Override
    void forEach(Mask<Byte, Shapes.S256Bit> o, FUnCon f) {
        boolean[] mbits = ((Byte256Mask)o).getBits();
        forEach((i, a) -> {
            if (mbits[i]) { f.apply(i, a); }
        });
    }



    @Override
    public Byte256Vector rotateEL(int j) {
        byte[] vec = getElements();
        byte[] res = new byte[length()];
        for (int i = 0; i < length(); i++){
            res[(j + i) % length()] = vec[i];
        }
        return new Byte256Vector(res);
    }

    @Override
    public Byte256Vector rotateER(int j) {
        byte[] vec = getElements();
        byte[] res = new byte[length()];
        for (int i = 0; i < length(); i++){
            int z = i - j;
            if(j < 0) {
                res[length() + z] = vec[i];
            } else {
                res[z] = vec[i];
            }
        }
        return new Byte256Vector(res);
    }

    @Override
    public Byte256Vector shiftEL(int j) {
        byte[] vec = getElements();
        byte[] res = new byte[length()];
        for (int i = 0; i < length() - j; i++) {
            res[i] = vec[i + j];
        }
        return new Byte256Vector(res);
    }

    @Override
    public Byte256Vector shiftER(int j) {
        byte[] vec = getElements();
        byte[] res = new byte[length()];
        for (int i = 0; i < length() - j; i++){
            res[i + j] = vec[i];
        }
        return new Byte256Vector(res);
    }

    @Override
    public Byte256Vector shuffle(Vector<Byte, Shapes.S256Bit> o, Shuffle<Byte, Shapes.S256Bit> s) {
        Byte256Vector v = (Byte256Vector) o;
        return uOp((i, a) -> {
            byte[] vec = this.getElements();
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
    public Byte256Vector swizzle(Shuffle<Byte, Shapes.S256Bit> s) {
        return uOp((i, a) -> {
            byte[] vec = this.getElements();
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
    public Byte256Vector blend(Vector<Byte, Shapes.S256Bit> o1, Mask<Byte, Shapes.S256Bit> o2) {
        Objects.requireNonNull(o1);
        Objects.requireNonNull(o2);
        Byte256Vector v = (Byte256Vector)o1;
        Byte256Mask   m = (Byte256Mask)o2;

        return (Byte256Vector) VectorIntrinsics.blend(
            Byte256Vector.class, Byte256Mask.class, byte.class, LENGTH,
            this, v, m,
            (v1, v2, m_) -> v1.bOp(v2, (i, a, b) -> m_.getElement(i) ? b : a));
    }

    // Accessors

    @Override
    public byte get(int i) {
        if (i < 0 || i >= LENGTH) {
            throw new IllegalArgumentException("Index " + i + " must be zero or positive, and less than " + LENGTH);
        }
        return (byte) VectorIntrinsics.extract(
                                Byte256Vector.class, byte.class, LENGTH,
                                this, i,
                                (vec, ix) -> {
                                    byte[] vecarr = vec.getElements();
                                    return (long)vecarr[ix];
                                });
    }

    @Override
    public Byte256Vector with(int i, byte e) {
        if (i < 0 || i >= LENGTH) {
            throw new IllegalArgumentException("Index " + i + " must be zero or positive, and less than " + LENGTH);
        }
        return VectorIntrinsics.insert(
                                Byte256Vector.class, byte.class, LENGTH,
                                this, i, (long)e,
                                (v, ix, bits) -> {
                                    byte[] res = v.getElements().clone();
                                    res[ix] = (byte)bits;
                                    return new Byte256Vector(res);
                                });
    }

    // Mask

    static final class Byte256Mask extends AbstractMask<Byte, Shapes.S256Bit> {
        static final Byte256Mask TRUE_MASK = new Byte256Mask(true);
        static final Byte256Mask FALSE_MASK = new Byte256Mask(false);

        // FIXME: was temporarily put here to simplify rematerialization support in the JVM
        private final boolean[] bits; // Don't access directly, use getBits() instead.

        public Byte256Mask(boolean[] bits) {
            this(bits, 0);
        }

        public Byte256Mask(boolean[] bits, int i) {
            this.bits = Arrays.copyOfRange(bits, i, i + species().length());
        }

        public Byte256Mask(boolean val) {
            boolean[] bits = new boolean[species().length()];
            Arrays.fill(bits, val);
            this.bits = bits;
        }

        boolean[] getBits() {
            return VectorIntrinsics.maybeRebox(this).bits;
        }

        @Override
        Byte256Mask uOp(MUnOp f) {
            boolean[] res = new boolean[species().length()];
            boolean[] bits = getBits();
            for (int i = 0; i < species().length(); i++) {
                res[i] = f.apply(i, bits[i]);
            }
            return new Byte256Mask(res);
        }

        @Override
        Byte256Mask bOp(Mask<Byte, Shapes.S256Bit> o, MBinOp f) {
            boolean[] res = new boolean[species().length()];
            boolean[] bits = getBits();
            boolean[] mbits = ((Byte256Mask)o).getBits();
            for (int i = 0; i < species().length(); i++) {
                res[i] = f.apply(i, bits[i], mbits[i]);
            }
            return new Byte256Mask(res);
        }

        @Override
        public Byte256Species species() {
            return SPECIES;
        }

        @Override
        public Byte256Vector toVector() {
            byte[] res = new byte[species().length()];
            boolean[] bits = getBits();
            for (int i = 0; i < species().length(); i++) {
                res[i] = (byte) (bits[i] ? -1 : 0);
            }
            return new Byte256Vector(res);
        }

        @Override
        @ForceInline
        @SuppressWarnings("unchecked")
        public <Z> Mask<Z, Shapes.S256Bit> rebracket(Species<Z, Shapes.S256Bit> species) {
            Objects.requireNonNull(species);
            // TODO: check proper element type
            return VectorIntrinsics.reinterpret(
                Byte256Mask.class, byte.class, LENGTH,
                species.elementType(), species.length(), this,
                (m, t) -> m.reshape(species)
            );
        }

        // Unary operations

        @Override
        @ForceInline
        public Byte256Mask not() {
            return (Byte256Mask) VectorIntrinsics.unaryOp(
                                             VECTOR_OP_NOT, Byte256Mask.class, byte.class, LENGTH,
                                             this,
                                             (m1) -> m1.uOp((i, a) -> !a));
        }

        // Binary operations

        @Override
        @ForceInline
        public Byte256Mask and(Mask<Byte,Shapes.S256Bit> o) {
            Objects.requireNonNull(o);
            Byte256Mask m = (Byte256Mask)o;
            return VectorIntrinsics.binaryOp(VECTOR_OP_AND, Byte256Mask.class, byte.class, LENGTH,
                                             this, m,
                                             (m1, m2) -> m1.bOp(m2, (i, a, b) -> a & b));
        }

        @Override
        @ForceInline
        public Byte256Mask or(Mask<Byte,Shapes.S256Bit> o) {
            Objects.requireNonNull(o);
            Byte256Mask m = (Byte256Mask)o;
            return VectorIntrinsics.binaryOp(VECTOR_OP_OR, Byte256Mask.class, byte.class, LENGTH,
                                             this, m,
                                             (m1, m2) -> m1.bOp(m2, (i, a, b) -> a | b));
        }

        // Reductions

        @Override
        @ForceInline
        public boolean anyTrue() {
            return VectorIntrinsics.test(COND_notZero, Byte256Mask.class, byte.class, LENGTH,
                                         this, this,
                                         (m1, m2) -> super.anyTrue());
        }

        @Override
        @ForceInline
        public boolean allTrue() {
            return VectorIntrinsics.test(COND_carrySet, Byte256Mask.class, byte.class, LENGTH,
                                         this, species().maskAllTrue(),
                                         (m1, m2) -> super.allTrue());
        }
    }

    // Shuffle

    static final class Byte256Shuffle extends AbstractShuffle<Byte, Shapes.S256Bit> {
        static final IntVector.IntSpecies<Shapes.S256Bit> INT_SPECIES = IntVector.species(Shapes.S_256_BIT);

        public Byte256Shuffle(int[] reorder) {
            super(reorder);
        }

        public Byte256Shuffle(int[] reorder, int i) {
            super(reorder, i);
        }

        @Override
        public Byte256Species species() {
            return SPECIES;
        }

        @Override
        public IntVector.IntSpecies<Shapes.S256Bit> intSpecies() {
            return INT_SPECIES;
        }
    }

    // Species

    @Override
    public Byte256Species species() {
        return SPECIES;
    }

    static final class Byte256Species extends ByteSpecies<Shapes.S256Bit> {
        static final int BIT_SIZE = Shapes.S_256_BIT.bitSize();

        static final int LENGTH = BIT_SIZE / Byte.SIZE;

        @Override
        public String toString() {
           StringBuilder sb = new StringBuilder("Shape[");
           sb.append(bitSize()).append(" bits, ");
           sb.append(length()).append(" ").append(byte.class.getSimpleName()).append("s x ");
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
        public Class<Byte> elementType() {
            return byte.class;
        }

        @Override
        @ForceInline
        public int elementSize() {
            return Byte.SIZE;
        }

        @Override
        @ForceInline
        public Shapes.S256Bit shape() {
            return Shapes.S_256_BIT;
        }

        @Override
        Byte256Vector op(FOp f) {
            byte[] res = new byte[length()];
            for (int i = 0; i < length(); i++) {
                res[i] = f.apply(i);
            }
            return new Byte256Vector(res);
        }

        @Override
        Byte256Vector op(Mask<Byte, Shapes.S256Bit> o, FOp f) {
            byte[] res = new byte[length()];
            boolean[] mbits = ((Byte256Mask)o).getBits();
            for (int i = 0; i < length(); i++) {
                if (mbits[i]) {
                    res[i] = f.apply(i);
                }
            }
            return new Byte256Vector(res);
        }

        // Factories

        @Override
        public Byte256Mask maskFromValues(boolean... bits) {
            return new Byte256Mask(bits);
        }

        @Override
        public Byte256Mask maskFromArray(boolean[] bits, int i) {
            return new Byte256Mask(bits, i);
        }

        @Override
        public Byte256Shuffle shuffleFromValues(int... ixs) {
            return new Byte256Shuffle(ixs);
        }

        @Override
        public Byte256Shuffle shuffleFromArray(int[] ixs, int i) {
            return new Byte256Shuffle(ixs, i);
        }

        @Override
        public Byte256Shuffle shuffleFromVector(Vector<Integer, Shapes.S256Bit> v) {
            int[] a = ((IntVector<Shapes.S256Bit>) v).toArray();
            return new Byte256Shuffle(a, 0);
        }

        @Override
        @ForceInline
        public Byte256Vector zero() {
            return VectorIntrinsics.broadcastCoerced(Byte256Vector.class, byte.class, LENGTH,
                                                     0,
                                                     (z -> ZERO));
        }

        @Override
        @ForceInline
        public Byte256Vector broadcast(byte e) {
            return VectorIntrinsics.broadcastCoerced(
                Byte256Vector.class, byte.class, LENGTH,
                e,
                ((long bits) -> SPECIES.op(i -> (byte)bits)));
        }

        @Override
        @ForceInline
        public Byte256Mask maskAllTrue() {
            return VectorIntrinsics.broadcastCoerced(Byte256Mask.class, byte.class, LENGTH,
                                                     (byte)-1,
                                                     (z -> Byte256Mask.TRUE_MASK));
        }

        @Override
        @ForceInline
        public Byte256Mask maskAllFalse() {
            return VectorIntrinsics.broadcastCoerced(Byte256Mask.class, byte.class, LENGTH,
                                                     0,
                                                     (z -> Byte256Mask.FALSE_MASK));
        }

        @Override
        @ForceInline
        public Byte256Vector scalars(byte... es) {
            Objects.requireNonNull(es);
            int ix = VectorIntrinsics.checkIndex(0, es.length, LENGTH);
            return (Byte256Vector) VectorIntrinsics.load(Byte256Vector.class, byte.class, LENGTH,
                                                        es, ix,
                                                        (arr, idx) -> super.fromArray((byte[]) arr, idx));
        }

        @Override
        @ForceInline
        public Byte256Vector fromArray(byte[] a, int ix) {
            Objects.requireNonNull(a);
            ix = VectorIntrinsics.checkIndex(ix, a.length, LENGTH);
            return (Byte256Vector) VectorIntrinsics.load(Byte256Vector.class, byte.class, LENGTH,
                                                        a, ix,
                                                        (arr, idx) -> super.fromArray((byte[]) arr, idx));
        }

        @Override
        @ForceInline
        public Byte256Vector fromArray(byte[] a, int ax, Mask<Byte, Shapes.S256Bit> m) {
            return zero().blend(fromArray(a, ax), m); // TODO: use better default impl: op(m, i -> a[ax + i]);
        }

        @Override
        @ForceInline
        public Byte256Vector fromByteArray(byte[] a, int ix) {
            Objects.requireNonNull(a);
            ix = VectorIntrinsics.checkIndex(ix, a.length, bitSize() / Byte.SIZE);
            return (Byte256Vector) VectorIntrinsics.load(Byte256Vector.class, byte.class, LENGTH,
                                                        a, ix,
                                                        (arr, idx) -> super.fromByteArray((byte[]) arr, idx));
        }

        @Override
        @ForceInline
        public Byte256Vector fromByteArray(byte[] a, int ix, Mask<Byte, Shapes.S256Bit> m) {
            return zero().blend(fromByteArray(a, ix), m);
        }

        @Override
        @ForceInline
        public Byte256Vector fromByteBuffer(ByteBuffer bb, int ix) {
            if (bb.hasArray() && !bb.isReadOnly() && bb.order() == ByteOrder.nativeOrder()) {
                int num_bytes = bitSize() / Byte.SIZE;
                int ax = VectorIntrinsics.checkIndex(ix, bb.limit(), num_bytes);
                return (Byte256Vector) VectorIntrinsics.load(Byte256Vector.class, byte.class, LENGTH,
                                                            bb.array(), ax,
                                                            (arr, idx) -> super.fromByteArray((byte[]) arr, idx));
            } else {
                return (Byte256Vector)super.fromByteBuffer(bb, ix);
            }
        }

        @Override
        @ForceInline
        public Byte256Vector fromByteBuffer(ByteBuffer bb, int ix, Mask<Byte, Shapes.S256Bit> m) {
            return zero().blend(fromByteBuffer(bb, ix), m);
        }

        @ForceInline
        @SuppressWarnings("unchecked")
        private <S extends Shape> Byte256Vector castFromByte(ByteVector<S> o) {
            if (o.bitSize() == 64) {
                Byte64Vector so = (Byte64Vector)o;
                return VectorIntrinsics.cast(
                    Byte64Vector.class, byte.class, so.length(),
                    byte.class, LENGTH, so,
                    (v, t) -> (Byte256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 128) {
                Byte128Vector so = (Byte128Vector)o;
                return VectorIntrinsics.cast(
                    Byte128Vector.class, byte.class, so.length(),
                    byte.class, LENGTH, so,
                    (v, t) -> (Byte256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 256) {
                Byte256Vector so = (Byte256Vector)o;
                return VectorIntrinsics.cast(
                    Byte256Vector.class, byte.class, so.length(),
                    byte.class, LENGTH, so,
                    (v, t) -> (Byte256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 512) {
                Byte512Vector so = (Byte512Vector)o;
                return VectorIntrinsics.cast(
                    Byte512Vector.class, byte.class, so.length(),
                    byte.class, LENGTH, so,
                    (v, t) -> (Byte256Vector)super.cast(v)
                );
            } else {
                throw new InternalError("Unimplemented size");
            }
        }

        @ForceInline
        @SuppressWarnings("unchecked")
        private <S extends Shape> Byte256Vector castFromShort(ShortVector<S> o) {
            if (o.bitSize() == 64) {
                Short64Vector so = (Short64Vector)o;
                return VectorIntrinsics.cast(
                    Short64Vector.class, short.class, so.length(),
                    byte.class, LENGTH, so,
                    (v, t) -> (Byte256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 128) {
                Short128Vector so = (Short128Vector)o;
                return VectorIntrinsics.cast(
                    Short128Vector.class, short.class, so.length(),
                    byte.class, LENGTH, so,
                    (v, t) -> (Byte256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 256) {
                Short256Vector so = (Short256Vector)o;
                return VectorIntrinsics.cast(
                    Short256Vector.class, short.class, so.length(),
                    byte.class, LENGTH, so,
                    (v, t) -> (Byte256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 512) {
                Short512Vector so = (Short512Vector)o;
                return VectorIntrinsics.cast(
                    Short512Vector.class, short.class, so.length(),
                    byte.class, LENGTH, so,
                    (v, t) -> (Byte256Vector)super.cast(v)
                );
            } else {
                throw new InternalError("Unimplemented size");
            }
        }

        @ForceInline
        @SuppressWarnings("unchecked")
        private <S extends Shape> Byte256Vector castFromInt(IntVector<S> o) {
            if (o.bitSize() == 64) {
                Int64Vector so = (Int64Vector)o;
                return VectorIntrinsics.cast(
                    Int64Vector.class, int.class, so.length(),
                    byte.class, LENGTH, so,
                    (v, t) -> (Byte256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 128) {
                Int128Vector so = (Int128Vector)o;
                return VectorIntrinsics.cast(
                    Int128Vector.class, int.class, so.length(),
                    byte.class, LENGTH, so,
                    (v, t) -> (Byte256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 256) {
                Int256Vector so = (Int256Vector)o;
                return VectorIntrinsics.cast(
                    Int256Vector.class, int.class, so.length(),
                    byte.class, LENGTH, so,
                    (v, t) -> (Byte256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 512) {
                Int512Vector so = (Int512Vector)o;
                return VectorIntrinsics.cast(
                    Int512Vector.class, int.class, so.length(),
                    byte.class, LENGTH, so,
                    (v, t) -> (Byte256Vector)super.cast(v)
                );
            } else {
                throw new InternalError("Unimplemented size");
            }
        }

        @ForceInline
        @SuppressWarnings("unchecked")
        private <S extends Shape> Byte256Vector castFromLong(LongVector<S> o) {
            if (o.bitSize() == 64) {
                Long64Vector so = (Long64Vector)o;
                return VectorIntrinsics.cast(
                    Long64Vector.class, long.class, so.length(),
                    byte.class, LENGTH, so,
                    (v, t) -> (Byte256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 128) {
                Long128Vector so = (Long128Vector)o;
                return VectorIntrinsics.cast(
                    Long128Vector.class, long.class, so.length(),
                    byte.class, LENGTH, so,
                    (v, t) -> (Byte256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 256) {
                Long256Vector so = (Long256Vector)o;
                return VectorIntrinsics.cast(
                    Long256Vector.class, long.class, so.length(),
                    byte.class, LENGTH, so,
                    (v, t) -> (Byte256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 512) {
                Long512Vector so = (Long512Vector)o;
                return VectorIntrinsics.cast(
                    Long512Vector.class, long.class, so.length(),
                    byte.class, LENGTH, so,
                    (v, t) -> (Byte256Vector)super.cast(v)
                );
            } else {
                throw new InternalError("Unimplemented size");
            }
        }

        @ForceInline
        @SuppressWarnings("unchecked")
        private <S extends Shape> Byte256Vector castFromFloat(FloatVector<S> o) {
            if (o.bitSize() == 64) {
                Float64Vector so = (Float64Vector)o;
                return VectorIntrinsics.cast(
                    Float64Vector.class, float.class, so.length(),
                    byte.class, LENGTH, so,
                    (v, t) -> (Byte256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 128) {
                Float128Vector so = (Float128Vector)o;
                return VectorIntrinsics.cast(
                    Float128Vector.class, float.class, so.length(),
                    byte.class, LENGTH, so,
                    (v, t) -> (Byte256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 256) {
                Float256Vector so = (Float256Vector)o;
                return VectorIntrinsics.cast(
                    Float256Vector.class, float.class, so.length(),
                    byte.class, LENGTH, so,
                    (v, t) -> (Byte256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 512) {
                Float512Vector so = (Float512Vector)o;
                return VectorIntrinsics.cast(
                    Float512Vector.class, float.class, so.length(),
                    byte.class, LENGTH, so,
                    (v, t) -> (Byte256Vector)super.cast(v)
                );
            } else {
                throw new InternalError("Unimplemented size");
            }
        }

        @ForceInline
        @SuppressWarnings("unchecked")
        private <S extends Shape> Byte256Vector castFromDouble(DoubleVector<S> o) {
            if (o.bitSize() == 64) {
                Double64Vector so = (Double64Vector)o;
                return VectorIntrinsics.cast(
                    Double64Vector.class, double.class, so.length(),
                    byte.class, LENGTH, so,
                    (v, t) -> (Byte256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 128) {
                Double128Vector so = (Double128Vector)o;
                return VectorIntrinsics.cast(
                    Double128Vector.class, double.class, so.length(),
                    byte.class, LENGTH, so,
                    (v, t) -> (Byte256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 256) {
                Double256Vector so = (Double256Vector)o;
                return VectorIntrinsics.cast(
                    Double256Vector.class, double.class, so.length(),
                    byte.class, LENGTH, so,
                    (v, t) -> (Byte256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 512) {
                Double512Vector so = (Double512Vector)o;
                return VectorIntrinsics.cast(
                    Double512Vector.class, double.class, so.length(),
                    byte.class, LENGTH, so,
                    (v, t) -> (Byte256Vector)super.cast(v)
                );
            } else {
                throw new InternalError("Unimplemented size");
            }
        }

        @Override
        @ForceInline
        @SuppressWarnings("unchecked")
        public <E, S extends Shape> Byte256Vector cast(Vector<E, S> o) {
            Objects.requireNonNull(o);
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
        @SuppressWarnings("unchecked")
        public <F> Byte256Vector rebracket(Vector<F, Shapes.S256Bit> o) {
            Objects.requireNonNull(o);
            if (o.elementType() == byte.class) {
                Byte256Vector so = (Byte256Vector)o;
                return VectorIntrinsics.reinterpret(
                    Byte256Vector.class, byte.class, so.length(),
                    byte.class, LENGTH, so,
                    (v, t) -> (Byte256Vector)reshape(v)
                );
            } else if (o.elementType() == short.class) {
                Short256Vector so = (Short256Vector)o;
                return VectorIntrinsics.reinterpret(
                    Short256Vector.class, short.class, so.length(),
                    byte.class, LENGTH, so,
                    (v, t) -> (Byte256Vector)reshape(v)
                );
            } else if (o.elementType() == int.class) {
                Int256Vector so = (Int256Vector)o;
                return VectorIntrinsics.reinterpret(
                    Int256Vector.class, int.class, so.length(),
                    byte.class, LENGTH, so,
                    (v, t) -> (Byte256Vector)reshape(v)
                );
            } else if (o.elementType() == long.class) {
                Long256Vector so = (Long256Vector)o;
                return VectorIntrinsics.reinterpret(
                    Long256Vector.class, long.class, so.length(),
                    byte.class, LENGTH, so,
                    (v, t) -> (Byte256Vector)reshape(v)
                );
            } else if (o.elementType() == float.class) {
                Float256Vector so = (Float256Vector)o;
                return VectorIntrinsics.reinterpret(
                    Float256Vector.class, float.class, so.length(),
                    byte.class, LENGTH, so,
                    (v, t) -> (Byte256Vector)reshape(v)
                );
            } else if (o.elementType() == double.class) {
                Double256Vector so = (Double256Vector)o;
                return VectorIntrinsics.reinterpret(
                    Double256Vector.class, double.class, so.length(),
                    byte.class, LENGTH, so,
                    (v, t) -> (Byte256Vector)reshape(v)
                );
            } else {
                throw new InternalError("Unimplemented type");
            }
        }

        @Override
        @ForceInline
        @SuppressWarnings("unchecked")
        public <T extends Shape> Byte256Vector resize(Vector<Byte, T> o) {
            Objects.requireNonNull(o);
            if (o.bitSize() == 64) {
                Byte64Vector so = (Byte64Vector)o;
                return VectorIntrinsics.reinterpret(
                    Byte64Vector.class, byte.class, so.length(),
                    byte.class, LENGTH, so,
                    (v, t) -> (Byte256Vector)reshape(v)
                );
            } else if (o.bitSize() == 128) {
                Byte128Vector so = (Byte128Vector)o;
                return VectorIntrinsics.reinterpret(
                    Byte128Vector.class, byte.class, so.length(),
                    byte.class, LENGTH, so,
                    (v, t) -> (Byte256Vector)reshape(v)
                );
            } else if (o.bitSize() == 256) {
                Byte256Vector so = (Byte256Vector)o;
                return VectorIntrinsics.reinterpret(
                    Byte256Vector.class, byte.class, so.length(),
                    byte.class, LENGTH, so,
                    (v, t) -> (Byte256Vector)reshape(v)
                );
            } else if (o.bitSize() == 512) {
                Byte512Vector so = (Byte512Vector)o;
                return VectorIntrinsics.reinterpret(
                    Byte512Vector.class, byte.class, so.length(),
                    byte.class, LENGTH, so,
                    (v, t) -> (Byte256Vector)reshape(v)
                );
            } else {
                throw new InternalError("Unimplemented size");
            }
        }
    }
}
