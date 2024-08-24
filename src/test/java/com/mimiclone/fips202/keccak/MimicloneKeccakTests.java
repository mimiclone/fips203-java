package com.mimiclone.fips202.keccak;

import org.junit.jupiter.api.Test;

import java.util.BitSet;

import static org.junit.jupiter.api.Assertions.*;

public class MimicloneKeccakTests {

    @Test
    public void testSafeMod() {

        // Create the permutation engine
        MimicloneKeccak keccak = new MimicloneKeccak(MimicloneKeccak.Permutation.KECCAK_F1600);
        assertNotNull(keccak);

        assertEquals(0, keccak.mod(-5, 5));
        assertEquals(1, keccak.mod(-4, 5));
        assertEquals(2, keccak.mod(-3, 5));
        assertEquals(3, keccak.mod(-2, 5));
        assertEquals(4, keccak.mod(-1, 5));
        assertEquals(0, keccak.mod(0, 5));
        assertEquals(1, keccak.mod(1, 5));
        assertEquals(2, keccak.mod(2, 5));
        assertEquals(3, keccak.mod(3, 5));
        assertEquals(4, keccak.mod(4, 5));
        assertEquals(0, keccak.mod(5, 5));

    }

    @Test
    public void testKeccak1600() {

        // Create the input data
        // This is a 25x25 array of 64-bit longs with all bits set to zero
        final BitSet[][] inputData = new BitSet[5][5];
        for (int i=0; i < 5; i++) {
            for (int j=0; j < 5; j++) {
                inputData[i][j] = new BitSet(64);
            }
        }

        // Create the expected output data
        // This is a 25x25 array of 64-bit longs taken from the reference implementation set
        final long[][] expectedOutput = {
                {0xF1258F7940E1DDE7L, 0xFF97A42D7F8E6FD4L, 0xEB5AA93F2317D635L, 0x05E5635A21D9AE61L, 0x940C7922AE3A2614L},
                {0x84D5CCF933C0478AL, 0x90FEE5A0A44647C4L, 0xA9A6E6260D712103L, 0x64BEFEF28CC970F2L, 0x1841F924A2C509E4L},
                {0xD598261EA65AA9EEL, 0x8C5BDA0CD6192E76L, 0x81A57C16DBCF555FL, 0x613670957BC46611L, 0x16F53526E70465C2L},
                {0xBD1547306F80494DL, 0xAD30A6F71B19059CL, 0x43B831CD0347C826L, 0xB87C5A554FD00ECBL, 0x75F644E97F30A13BL},
                {0x8B284E056253D057L, 0x30935AB7D08FFC64L, 0x01F22F1A11A5569FL, 0x8C3EE88A1CCF32C8L, 0xEAF1FF7B5CECA249L},
        };

        // Convert to an array of BitSets to make sure we don't get any 2s compliment encoding issues in comparison.
        final BitSet[][] expectedOutBits = new BitSet[5][5];
        for (int i=0; i < 5; i++) {
            for (int j=0; j < 5; j++) {
                expectedOutBits[i][j] = BitSet.valueOf(new long[]{expectedOutput[i][j]});
            }
        }

        // Create the permutation engine
        MimicloneKeccak keccak = new MimicloneKeccak(MimicloneKeccak.Permutation.KECCAK_F1600);
        assertNotNull(keccak);

        // Run the permutation
        final BitSet[][] resultData = keccak.permute(inputData);

        // Validate result is not null
        assertNotNull(resultData);

        // Validate result x dimension is 5
        assertEquals(5, resultData.length);
        for (int i = 0; i < 5; i++) {

            // Validate all columns have height 5 (y dimension)
            assertEquals(5, resultData[i].length);
            for (int j = 0; j < 5; j++) {

                // Validate each lane contains the expected output
                BitSet expectedValue = expectedOutBits[i][j];
                BitSet resultValue = resultData[i][j];
                assertEquals(expectedValue, resultValue);
            }

        }

    }

}
