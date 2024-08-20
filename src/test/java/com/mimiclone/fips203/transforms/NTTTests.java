package com.mimiclone.fips203.transforms;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class NTTTests {

    @Test
    public void testInterface() {

        // Define expected input
        final int[] input = new int[256];
        for (int i = 0; i < 256; i++) {
            input[i] = 1;
        }

        // Define expected output
        final int[] expectedOutput = new int[256];

        // Instantiate transformer
        final NumberTheoretic ntt = MimicloneNTT.fips203();
        assertNotNull(ntt);

        // Perform transform
        final int[] transformOutput = ntt.transform(input);
        assertNotNull(transformOutput);
        assertEquals(expectedOutput.length, transformOutput.length);

        // Perform inverse
        final int[] inverseTransformOutput = ntt.inverse(transformOutput);
        assertNotNull(inverseTransformOutput);
        assertEquals(expectedOutput.length, inverseTransformOutput.length);

        // Compare individual bit results
        for (int i = 0; i < expectedOutput.length; i++) {
            assertEquals(input[i], inverseTransformOutput[i]);
            System.out.printf("%s", Integer.toHexString(input[i]));
            System.out.printf("%s", Integer.toHexString(transformOutput[i]));
            System.out.printf("%s", Integer.toHexString(inverseTransformOutput[i]));
        }

    }

    @Test
    public void testBitRev7() {

        // Define expected output
        int[] expectedOutput = {
                0, 64, 32, 96, 16, 80, 48, 112, 8, 72, 40, 104, 24, 88, 56, 120, 4, 68, 36, 100, 20, 84, 52, 116, 12,
                76, 44, 108, 28, 92, 60, 124, 2, 66, 34, 98, 18, 82, 50, 114, 10, 74, 42, 106, 26, 90, 58, 122, 6, 70,
                38, 102, 22, 86, 54, 118, 14, 78, 46, 110, 30, 94, 62, 126, 1, 65, 33, 97, 17, 81, 49, 113, 9, 73, 41,
                105, 25, 89, 57, 121, 5, 69, 37, 101, 21, 85, 53, 117, 13, 77, 45, 109, 29, 93, 61, 125, 3, 67, 35, 99,
                19, 83, 51, 115, 11, 75, 43, 107, 27, 91, 59, 123, 7, 71, 39, 103, 23, 87, 55, 119, 15, 79, 47, 111, 31,
                95, 63, 127,
        };

        // Instantiate the transformer
        final MimicloneNTT ntt = MimicloneNTT.withModulus(BigInteger.valueOf(3329));
        assertNotNull(ntt);

        // Loop through the values of i from 0 to 127 and verify the output
        for (int i = 0; i < expectedOutput.length; i++) {
            assertEquals(BigInteger.valueOf(expectedOutput[i]), ntt.bitRev7(BigInteger.valueOf(i)));
        }

    }

    @Test
    public void testZeta() {

        // Define expected output
        // This comes from Appendix A of the FIPS203 Spec on Page 44
        // and consist of pre-computed values for the 17^bitrev7(i) function
        // for values of i = 0 ... 127
        int[] expectedOutput = {
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

        // Instantiate the transformer
        final MimicloneNTT ntt = MimicloneNTT.withModulus(BigInteger.valueOf(3329));
        assertNotNull(ntt);

        // Loop through the values of i from 0 to 127 and verify the output
        for (int i = 0; i < expectedOutput.length; i++) {
            BigInteger output = BigInteger.valueOf(17).modPow(ntt.bitRev7(BigInteger.valueOf(i)), BigInteger.valueOf(3329));
            assertEquals(BigInteger.valueOf(expectedOutput[i]), output);
        }
    }

}
