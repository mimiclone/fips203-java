package com.mimiclone.fips202.keccak.sponge;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KeccakSpongeTests {

    private static KeccakSponge keccakSponge;

    @BeforeAll
    static void setup() {
        keccakSponge = KeccakSponge.create();
    }

    @Test
    public void testPadding() {

        // Zero length message
        byte[] paddingZero = keccakSponge.pad(0);
        assertEquals(136, paddingZero.length);

        // One byte message
        byte[] paddingOne = keccakSponge.pad(1);
        assertEquals(135, paddingOne.length);

        // 135 byte message
        byte[] padding135 = keccakSponge.pad(135);
        assertEquals(1, padding135.length);

        // 136 byte message
        byte[] padding136 = keccakSponge.pad(136);
        assertEquals(136, padding136.length);

    }

}
