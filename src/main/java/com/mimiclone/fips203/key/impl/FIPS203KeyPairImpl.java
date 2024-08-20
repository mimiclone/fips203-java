package com.mimiclone.fips203.key.impl;

import com.mimiclone.fips203.key.DecapsulationKey;
import com.mimiclone.fips203.key.EncapsulationKey;
import com.mimiclone.fips203.key.FIPS203KeyPair;

public record FIPS203KeyPairImpl(EncapsulationKey encapsulationKey,
                                 DecapsulationKey decapsulationKey) implements FIPS203KeyPair {

    public static FIPS203KeyPair fromBytes(byte[] ek, byte[] dk) {
        return new FIPS203KeyPairImpl(new EncapsulationKeyImpl(ek), new DecapsulationKeyImpl(dk));
    }
}
