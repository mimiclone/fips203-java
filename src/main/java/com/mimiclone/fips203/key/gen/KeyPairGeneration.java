package com.mimiclone.fips203.key.gen;

import com.mimiclone.fips203.key.KeyPair;

public interface KeyPairGeneration {

    KeyPair generateKeyPair(byte[] d, byte[] z);

}
