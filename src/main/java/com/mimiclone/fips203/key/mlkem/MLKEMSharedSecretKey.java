package com.mimiclone.fips203.key.mlkem;

import com.mimiclone.fips203.key.SharedSecretKey;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MLKEMSharedSecretKey implements SharedSecretKey {

    private final byte[] sharedSecret;

    public static MLKEMSharedSecretKey create(byte[] sharedSecret) {
        return new MLKEMSharedSecretKey(sharedSecret);
    }

    @Override
    public byte[] getBytes() {
        return sharedSecret.clone();
    }

    @Override
    public void destroy() {
        Arrays.fill(sharedSecret, (byte)0);
    }
}
