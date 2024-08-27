package com.mimiclone.fips203.key.mlkem;

import com.mimiclone.fips203.key.EncapsulationKey;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * This class is completely immutable and the bytes making up the key
 * cannot be modified once passed to the constructor.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MLKEMEncapsulationKey implements EncapsulationKey {

    private final byte[] keyBytes;

    public static MLKEMEncapsulationKey create(byte[] keyBytes) {
        return new MLKEMEncapsulationKey(keyBytes);
    }

    @Override
    public byte[] getBytes() {
        return keyBytes.clone();
    }

}
