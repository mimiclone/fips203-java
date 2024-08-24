package com.mimiclone.fips203.key.mlkem;

import com.mimiclone.fips203.key.DecapsulationKey;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MLKEMDecapsulationKey implements DecapsulationKey {

    private final byte[] keyBytes;

    @Override
    public byte[] getBytes() {
        return keyBytes.clone();
    }
}
