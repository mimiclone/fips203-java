package com.mimiclone.fips203.codec;

import com.mimiclone.CryptoUtils;
import com.mimiclone.fips203.ParameterSet;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.math.BigInteger;
import java.util.BitSet;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MLKEMCodec implements Codec {

    private final ParameterSet parameterSet;

    public static MLKEMCodec build(ParameterSet parameterSet) {
        return new MLKEMCodec(parameterSet);
    }


    /**
     *
     * @param f
     * @return
     */
    @Override
    public byte[] byteEncode(int d, int[] f) {

        // Declare bitset
        int bitCapacity = 256 * d;
        BitSet b = new BitSet(bitCapacity);

        // If d < 12, then m = 2^d otherwise m = q
        BigInteger m = d < 12 ? BigInteger.valueOf(2).pow(d) : BigInteger.valueOf(parameterSet.getQ());

        // Iterate over the input array
        for (int i = 0; i < 256; i++) {

            // Extract a single integer (modulo m) -> Assumes big endian bit order and 32-bit ints
            int a = f[i] & CryptoUtils.INT_BIT_MASKS[d];

            // Iterate over the bits in the integer
            for (int j = 0; j < d; j++) {

                // Calculate the bit index for the operation
                int bitIndex = i * d + j;

                // b[i*d+j] = a mod 2 = LSB(a)
                b.set(bitIndex, (a & CryptoUtils.INT_BIT_MASKS[1]) != 0);

                // Update a
                a = (a - (b.get(bitIndex) ? 1 : 0))/2;

            }

        }

        // Convert the bitset to a byte array
        byte[] result = new byte[bitCapacity/8];
        byte[] bitsAsBytes = b.toByteArray();
        System.arraycopy(bitsAsBytes, 0, result, 0, bitsAsBytes.length);
        return result;

    }

    @Override
    public int[] compress(int d, int[] x) {

        // return ((x * d.Exp2() + (_param.Q / 2)) / _param.Q);

        int[] result = new int[x.length];
        BigInteger bq = BigInteger.valueOf(parameterSet.getQ());
        for (int i = 0; i < x.length; i++) {

            BigInteger bx = BigInteger.valueOf(x[i]);
            result[i] = bx.multiply(BigInteger.TWO.pow(d))
                    .add(bq.divide(BigInteger.TWO))
                    .divide(bq)
                    .intValue();

        }

        return result;
    }

    @Override
    public int[] byteDecode(int d, byte[] f) {

//        var b = BytesToBits(B);
//        var F = new int[256];
//        var m = d == 12 ? _param.Q : d.Exp2();
//
//        for (var i = 0; i < 256; i++)
//        {
//            for (var j = 0; j < d; j++)
//            {
//                F[i] = (F[i] + (b[(i * d) + j] ? j.Exp2() : 0)) % m;
//            }
//        }
//
//        return F;

        BitSet bits = BitSet.valueOf(f);
        int[] result = new int[256];
        int m = (d == 12) ? parameterSet.getQ() : BigInteger.TWO.pow(d).intValue();

        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < d; j++) {
                result[i] = BigInteger.valueOf(result[i])
                        .add(
                                bits.get(i * d + j) ? BigInteger.TWO.pow(j) : BigInteger.ZERO
                        ).mod(BigInteger.valueOf(m))
                        .intValue();
            }
        }

        return result;
    }

    @Override
    public int[] decompress(int d, int[] y) {

        // return ((y * _param.Q + (d.Exp2() / 2)) / d.Exp2());

        int[] result = new int[y.length];
        BigInteger bq = BigInteger.valueOf(parameterSet.getQ());
        BigInteger d2 = BigInteger.TWO.pow(d);
        BigInteger d2Half = d2.divide(BigInteger.TWO);

        for (int i = 0; i < y.length; i++) {
            BigInteger by = BigInteger.valueOf(y[i]);
            result[i] = by.multiply(bq)
                    .add(d2Half)
                    .divide(d2)
                    .intValue();
        }

        return result;
    }
}
