package com.mimiclone.fips203.key.impl;

import com.mimiclone.fips203.key.EncapsulationKey;
import lombok.AllArgsConstructor;

/**
 * This class is completely immutable and the bytes making up the key
 * cannot be modified once passed to the constructor.
 */
@AllArgsConstructor
public class EncapsulationKeyImpl implements EncapsulationKey {

    private final byte[] keyBytes;

    @Override
    public byte[] getBytes() {
        return keyBytes.clone();
    }

}
