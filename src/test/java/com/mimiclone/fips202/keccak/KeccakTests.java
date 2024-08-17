package com.mimiclone.fips202.keccak;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class KeccakTests {

    @Test
    public void testSafeMod() {

        // Create the permutation engine
        Keccak keccak = new Keccak(Keccak.Permutation.KECCAK_F1600);
        assertNotNull(keccak);

        assertEquals(3, keccak.safeMod(-2));
        assertEquals(4, keccak.safeMod(-1));
        assertEquals(0, keccak.safeMod(0));
        assertEquals(1, keccak.safeMod(1));
        assertEquals(0, keccak.safeMod(5));
        assertEquals(1, keccak.safeMod(6));

    }

    @Test
    public void testKeccak1600() {

        // Create the input data
        final long[][] inputData = new long[5][5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                inputData[i][j] = 0L;
            }
        }

        // Create the permutation engine
        Keccak keccak = new Keccak(Keccak.Permutation.KECCAK_F1600);
        assertNotNull(keccak);

        // Run the permutation
        final long[][] resultData = keccak.permute(inputData);

        for (int i = 0; i < 5; i++) {
            StringBuilder builder = new StringBuilder();
            for (int j = 0; j < 5; j++) {
                builder.append(String.format("%016X ", resultData[j][i]));
            }
            System.out.println(builder.toString());
        }

        assertNotNull(resultData);
        assertEquals(5, resultData.length);
        for (int i = 0; i < 5; i++) {
            assertEquals(5, resultData[i].length);
        }

    }

}
