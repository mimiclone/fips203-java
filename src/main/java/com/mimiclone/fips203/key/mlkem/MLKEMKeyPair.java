package com.mimiclone.fips203.key.mlkem;

import com.mimiclone.fips203.key.DecapsulationKey;
import com.mimiclone.fips203.key.EncapsulationKey;
import com.mimiclone.fips203.key.KeyPair;

public record MLKEMKeyPair(EncapsulationKey encapsulationKey,
                           DecapsulationKey decapsulationKey) implements KeyPair {

    public static KeyPair fromBytes(byte[] ek, byte[] dk) {
        return new MLKEMKeyPair(MLKEMEncapsulationKey.create(ek), MLKEMDecapsulationKey.create(dk));
    }
}
