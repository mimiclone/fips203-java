package com.mimiclone.fips203.message;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MLKEMCipherText implements CipherText {

    private final byte[] cipherText;

    public static MLKEMCipherText create(byte[] cipherText) {
        return new MLKEMCipherText(cipherText.clone());
    }

    @Override
    public byte[] getBytes() {
        return cipherText.clone();
    }

}
