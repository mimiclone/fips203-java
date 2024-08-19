package com.mimiclone.fips203.transforms;

import lombok.AllArgsConstructor;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class MimicloneNTT implements NumberTheoretic {

    /**
     * The modulus base
     */
    private final BigInteger q;

    private static final int INPUT_OUTPUT_LENGTH = 256;

    public static MimicloneNTT withModulus(BigInteger q) {
        return new MimicloneNTT(q);
    }

    /**
     * Operation must be performed using BigInteger since val could be up to 17^255
     * @param val
     * @param q
     * @return
     */
    int mod(BigInteger val, int q) {
        BigInteger bq = BigInteger.valueOf(q);
        return val.mod(bq).add(bq).mod(bq).intValue();
    }

    BigInteger bitRev7(BigInteger n) {
        BigInteger result = BigInteger.valueOf(0);
        for (int i = 0; i < 7; i++) {
            result = (result.shiftLeft(1)).or(n.and(BigInteger.ONE));
            n = n.shiftRight(1);
        }
        return result;
    }

    @Override
    public BigInteger[] transform(BigInteger[] input) {

        // Validate input is correct length
        if (input == null || input.length != INPUT_OUTPUT_LENGTH) {
            throw new IllegalArgumentException("Input must be an array of %d long values".formatted(INPUT_OUTPUT_LENGTH));
        }

        // Validate input has properly bounded values in modulo q
        List<Integer> incorrectIndexes = IntStream.range(0, input.length)
                .filter(i -> input[i].compareTo(BigInteger.ZERO) < 0 || input[i].compareTo(q) > 0)
                .boxed().toList();
        if (!incorrectIndexes.isEmpty()) {
            throw new IllegalArgumentException("Input values at the following indexes were not in modulo %d: %s".formatted(q, incorrectIndexes));
        }

        // Make a copy of the input to operate on
        // This variable is called f-hat in the FIPS203 spec, Algorithm 9, Line 1
        BigInteger[] result = input.clone();

        final int[] lenVals = {
                128, 64, 64, 32, 32, 32, 32, 16, 16, 16, 16, 16, 16, 16, 16, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
                8, 8, 8, 4, 4, 4, 4,4, 4, 4, 4, 4, 4, 4, 4,4, 4, 4, 4, 4, 4, 4, 4,4, 4, 4, 4,4, 4, 4, 4,4, 4, 4, 4,
                2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
                2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2
        };
        final int[] startVals = {
                0, 0, 128, 0, 64, 128, 192, 0, 32, 64, 96, 128, 160, 192, 224, 0, 16, 32, 48, 64, 80, 96, 112, 128,
                144, 160, 176, 192, 208, 224, 240, 0, 8, 16, 24, 32, 40, 48, 56, 64, 72, 80, 88, 96, 104, 112, 120,
                128, 136, 144, 152, 160, 168, 176, 184, 192, 200, 208, 216, 224, 232, 240, 248, 0, 4, 8, 12, 16, 20,
                24, 28, 32, 36, 40, 44, 48, 52, 56, 60, 64, 68, 72, 76, 80, 84, 88, 92, 96, 100, 104, 108, 112, 116,
                120, 124, 128, 132, 136, 140, 144, 148, 152, 156, 160, 164, 168, 172, 176, 180, 184, 188, 192, 196,
                200, 204, 208, 212, 216, 220, 224, 228, 232, 236, 240, 244, 248, 252
        };

        for (int i = 0; i < lenVals.length; i++) {

            // Retrieve pre-calculated loop values
            int len = lenVals[i];
            int start = startVals[i];

            // Calculate zeta.
            // Must use BigInteger here because 17^128 will overflow native data types
            // Zeta itself will be bound between 0 and 3329 (q).
            BigInteger zeta = BigInteger.valueOf(17).modPow(bitRev7(BigInteger.valueOf(i+1)), q);

            // Core transform loop
            for (int j = start; j < start + len; j++) {
                BigInteger t = zeta.multiply(input[j + len]).mod(q);
                result[j + len] = result[j].subtract(t).mod(q);
                result[j] = result[j].add(t).mod(q);
            }
        }

//        int i = 1;
//
//        /*
//        7 iterations
//
//        The termination condition in the spec is defined as len >= 2, but this can be
//        simplified to len > 1 because the iteration values are 128, 64, 32, 16, 8, 4, 2, 1,
//        and we only want 7 iterations.  The value of len is only updated in the loop counter,
//        so it doesn't make sense that they defined it as a multistep comparison in the spec
//        since most compilers will optimize >=2 as >1 anyway, and this will just add opcodes
//        to implementations in interpreted languages.  This is why we shouldn't let
//        mathematicians write programming specs.
//        */
//        for (int len = 128; len > 1; len /= 2) {
//
//            /*
//            This is yet another very weird way to define a loop.
//            It performs a total of 127 iterations (including the outer loop)
//            In this case i goes from 1 ... 127 before calculating zeta, and i is not involved in calculating t of f-hat
//            (only start and len are, and they do not change during the j loop so can be fixed)
//            We can unroll the outer two loops with pre-calculations and replace them with a single loop based on i=1, i<128.
//
//            for len=128, 1 iteration
//                0,
//                [256]
//            for len=64, 2 iterations
//                0, 128,
//                [256]
//            for len=32, 4 iterations
//                0, 64, 128, 192,
//                [256]
//            for len=16, 8 iterations
//                0, 32, 64, 96, 128, 160, 192, 224,
//                [256]
//            for len=8, 16 iterations
//                0, 16, 32, 48, 64, 80, 96, 112, 128, 144,
//                160, 176, 192, 208, 224, 240,
//                [256]
//            for len=4, 32 iterations
//                000, 008, 016, 024, 032, 040, 048, 056, 064, 072,
//                080, 088, 096, 104, 112, 120, 128, 136, 144, 152,
//                160, 168, 176, 184, 192, 200, 208, 216, 224, 232,
//                240, 248,
//                [256]
//            for len=2, 64 iterations
//                0, 4, 8, 12, 16, 20, 24, 28, 32, 36,
//                40, 44, 48, 52, 56, 60, 64, 68, 72, 76,
//                80, 84, 88, 92, 96, 100, 104, 108, 112, 116,
//                120, 124, 128, 132, 136, 140, 144, 148, 152, 156,
//                160, 164, 168, 172, 176, 180, 184, 188, 192, 196,
//                200, 204, 208, 212, 216, 220, 224, 228, 232, 236,
//                240, 244, 248, 252,
//                [256]
//
//             */
//            for (int start = 0; start < 256; start += 2 * len) {
//
//                // Calculate zeta.  Must use BigInteger here because 17^128 will overflow native types
//                BigInteger zeta = BigInteger.valueOf(17).modPow(bitRev7(BigInteger.valueOf(i)), q);
//
//
//                // Increment i, which is really just a counter for the total number start loop iterations
//                // We should have looped on i and used it to index into a start value array
//                // as well as the pre-computed bitrev7 values (of which they ignore the first one anyway).
//                i++;
//
//                /*
//                Yet another weirdly defined iteration.  Begins at start, increments by one and terminates at start + len
//                So every time through the loop we do len iterations, but start changes the j values
//                - start=0, 128 iters
//                - start=0, 64 iters
//                - start=128, 64 iters
//                 */
//                for (int j = start; j < start + len; j++) {
//                    BigInteger t = zeta.multiply(input[j + len]).mod(q);
//                    result[j + len] = result[j].subtract(t).mod(q);
//                    result[j] = result[j].add(t).mod(q);
//                }
//
//            }
//        }

        // Return the resulting transform
        return result;
    }

    @Override
    public BigInteger[] inverse(BigInteger[] input) {
        return input.clone();
    }
}
