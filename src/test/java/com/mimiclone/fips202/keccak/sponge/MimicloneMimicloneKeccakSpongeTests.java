package com.mimiclone.fips202.keccak.sponge;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MimicloneMimicloneKeccakSpongeTests {

    private static MimicloneKeccakSponge mimicloneKeccakSponge;

    @BeforeAll
    static void setup() {
        mimicloneKeccakSponge = MimicloneKeccakSponge.create(512);
    }

    @Test
    public void testPadding() {

        // Zero length message
        byte[] paddingZero = mimicloneKeccakSponge.pad(0);
        assertEquals(136, paddingZero.length);

        // One byte message
        byte[] paddingOne = mimicloneKeccakSponge.pad(1);
        assertEquals(135, paddingOne.length);

        // 135 byte message
        byte[] padding135 = mimicloneKeccakSponge.pad(135);
        assertEquals(1, padding135.length);

        // 136 byte message
        byte[] padding136 = mimicloneKeccakSponge.pad(136);
        assertEquals(136, padding136.length);

    }

}
