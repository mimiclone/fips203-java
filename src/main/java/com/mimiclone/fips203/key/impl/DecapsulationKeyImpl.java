package com.mimiclone.fips203.key.impl;

import com.mimiclone.fips203.key.DecapsulationKey;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DecapsulationKeyImpl implements DecapsulationKey {

    private final byte[] keyBytes;

    @Override
    public byte[] getBytes() {
        return keyBytes.clone();
    }
}
