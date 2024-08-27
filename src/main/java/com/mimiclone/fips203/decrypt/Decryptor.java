package com.mimiclone.fips203.decrypt;

public interface Decryptor {

    byte[] decrypt(byte[] dkPKE, byte[] cipherText);

}
