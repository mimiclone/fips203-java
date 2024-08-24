package com.mimiclone.fips203.message;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CipherTextImpl implements CipherText {

    private final byte[] cipherText;

    @Override
    public byte[] getBytes() {
        return cipherText.clone();
    }

}
