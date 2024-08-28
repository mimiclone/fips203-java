package com.mimiclone.fips203.key.mlkem;

import com.mimiclone.fips203.key.EncapsulationKey;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MLKEMEncapsulationKey implements EncapsulationKey {

    private final byte[] keyBytes;

    public static MLKEMEncapsulationKey create(byte[] keyBytes) {
        return new MLKEMEncapsulationKey(keyBytes.clone());
    }

    @Override
    public byte[] getBytes() {
        return keyBytes.clone();
    }

    @Override
    public void destroy() {
        Arrays.fill(keyBytes, (byte)0);
    }

}
