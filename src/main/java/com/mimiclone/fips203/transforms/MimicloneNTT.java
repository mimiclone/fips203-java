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

    BigInteger bitRev7(BigInteger n) {
        BigInteger result = BigInteger.valueOf(0);
        for (int i = 0; i < 7; i++) {
            result = (result.shiftLeft(1)).or(n.and(BigInteger.ONE));
            n = n.shiftRight(1);
        }
        return result;
    }

    BigInteger calcZeta(int i) {
        return BigInteger.valueOf(17).modPow(bitRev7(BigInteger.valueOf(i)), q);
    }

    private void validateInput(BigInteger[] input) {

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
    }

    @Override
    public BigInteger[] transform(BigInteger[] input) {

        // Validate the input
        validateInput(input);

        // Make a copy of the input to operate on
        // This variable is called f-hat in the FIPS203 spec, Algorithm 9, Line 1
        BigInteger[] result = input.clone();

        final int[] zetaVals = {
                1, 1729, 2580, 3289, 2642, 630, 1897, 848,
                1062, 1919, 193, 797, 2786, 3260, 569, 1746,
                296, 2447, 1339, 1476, 3046, 56, 2240, 1333,
                1426, 2094, 535, 2882, 2393, 2879, 1974, 821,
                289, 331, 3253, 1756, 1197, 2304, 2277, 2055,
                650, 1977, 2513, 632, 2865, 33, 1320, 1915,
                2319, 1435, 807, 452, 1438, 2868, 1534, 2402,
                2647, 2617, 1481, 648, 2474, 3110, 1227, 910,
                17, 2761, 583, 2649, 1637, 723, 2288, 1100,
                1409, 2662, 3281, 233, 756, 2156, 3015, 3050,
                1703, 1651, 2789, 1789, 1847, 952, 1461, 2687,
                939, 2308, 2437, 2388, 733, 2337, 268, 641,
                1584, 2298, 2037, 3220, 375, 2549, 2090, 1645,
                1063, 319, 2773, 757, 2099, 561, 2466, 2594,
                2804, 1092, 403, 1026, 1143, 2150, 2775, 886,
                1722, 1212, 1874, 1029, 2110, 2935, 885, 2154
        };
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
            BigInteger zeta = BigInteger.valueOf(zetaVals[i + 1]);

            // Core transform loop
            for (int j = start; j < start + len; j++) {
                BigInteger t = zeta.multiply(result[j + len]).mod(q);
                result[j + len] = result[j].subtract(t).mod(q);
                result[j] = result[j].add(t).mod(q);
            }
        }

        // Return the resulting transform
        return result;
    }

    @Override
    public BigInteger[] inverse(BigInteger[] input) {

        // Validate the input
        validateInput(input);

        // Make a copy of the input to operate on
        // This variable is called f-hat in the FIPS203 spec, Algorithm 9, Line 1
        BigInteger[] result = input.clone();

        // TODO: Reverse these values to make indexing more straightforward in the algo
        final int[] zetaVals = {
                1, 1729, 2580, 3289, 2642, 630, 1897, 848,
                1062, 1919, 193, 797, 2786, 3260, 569, 1746,
                296, 2447, 1339, 1476, 3046, 56, 2240, 1333,
                1426, 2094, 535, 2882, 2393, 2879, 1974, 821,
                289, 331, 3253, 1756, 1197, 2304, 2277, 2055,
                650, 1977, 2513, 632, 2865, 33, 1320, 1915,
                2319, 1435, 807, 452, 1438, 2868, 1534, 2402,
                2647, 2617, 1481, 648, 2474, 3110, 1227, 910,
                17, 2761, 583, 2649, 1637, 723, 2288, 1100,
                1409, 2662, 3281, 233, 756, 2156, 3015, 3050,
                1703, 1651, 2789, 1789, 1847, 952, 1461, 2687,
                939, 2308, 2437, 2388, 733, 2337, 268, 641,
                1584, 2298, 2037, 3220, 375, 2549, 2090, 1645,
                1063, 319, 2773, 757, 2099, 561, 2466, 2594,
                2804, 1092, 403, 1026, 1143, 2150, 2775, 886,
                1722, 1212, 1874, 1029, 2110, 2935, 885, 2154
        };
        final int[] lenVals = {
                2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
                2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 4, 4, 4, 4, 4,
                4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 8, 8, 8, 8, 8, 8, 8, 8,
                8, 8, 8, 8, 8, 8, 8, 8, 16, 16, 16, 16, 16, 16, 16, 16, 32, 32, 32, 32, 64, 64, 128
        };
        final int[] startVals = {
                0, 4, 8, 12, 16, 20, 24, 28, 32, 36, 40, 44, 48, 52, 56, 60, 64, 68, 72, 76, 80, 84, 88, 92, 96, 100,
                104, 108, 112, 116, 120, 124, 128, 132, 136, 140, 144, 148, 152, 156, 160, 164, 168, 172, 176, 180, 184,
                188, 192, 196, 200, 204, 208, 212, 216, 220, 224, 228, 232, 236, 240, 244, 248, 252, 0, 8, 16, 24, 32,
                40, 48, 56, 64, 72, 80, 88, 96, 104, 112, 120, 128, 136, 144, 152, 160, 168, 176, 184, 192, 200, 208,
                216, 224, 232, 240, 248, 0, 16, 32, 48, 64, 80, 96, 112, 128, 144, 160, 176, 192, 208, 224, 240, 0, 32,
                64, 96, 128, 160, 192, 224, 0, 64, 128, 192, 0, 128, 0
        };

        for (int i = 0; i < lenVals.length; i++) {

            // Retrieve pre-calculated loop values
            int len = lenVals[i];
            int start = startVals[i];

            // Calculate zeta.
            // Must use BigInteger here because 17^128 will overflow native data types
            // Zeta itself will be bound between 0 and 3329 (q).
            BigInteger zeta = BigInteger.valueOf(zetaVals[127 - i]);

            // Core inverse transform loop
            for (int j = start; j < start + len; j++) {
                BigInteger t = result[j];
                result[j] = t.add(result[j + len]).mod(q);
                result[j + len] = zeta.multiply(result[j + len].subtract(t)).mod(q);
            }
        }

        // Multiply all entries
        for (int i = 0; i < result.length; i++) {
            result[i] = result[i].multiply(BigInteger.valueOf(3303)).mod(q);
        }

        // Return the resulting transform
        return result;

    }
}
