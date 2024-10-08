package com.mimiclone.fips203.key.mlkem;

import com.mimiclone.fips203.key.DecapsulationKey;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MLKEMDecapsulationKey implements DecapsulationKey {

    private final byte[] keyBytes;

    public static MLKEMDecapsulationKey create(byte[] keyBytes) {
        return new MLKEMDecapsulationKey(keyBytes.clone());
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
