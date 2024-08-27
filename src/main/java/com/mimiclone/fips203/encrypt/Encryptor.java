package com.mimiclone.fips203.encrypt;

public interface Encryptor {

    byte[] encrypt(byte[] ekPKE, byte[] message, byte[] random);

}
