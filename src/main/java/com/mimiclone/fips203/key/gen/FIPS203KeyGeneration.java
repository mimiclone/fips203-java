package com.mimiclone.fips203.key.gen;

import com.mimiclone.fips203.key.FIPS203KeyPair;

public interface FIPS203KeyGeneration {

    FIPS203KeyPair generateKeyPair(byte[] d, byte[] z);

}
