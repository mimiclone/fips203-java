package com.mimiclone.fips203.key.mlkem;

import com.mimiclone.fips203.key.DecapsulationKey;
import com.mimiclone.fips203.key.EncapsulationKey;
import com.mimiclone.fips203.key.FIPS203KeyPair;

public record MLKEMKeyPair(EncapsulationKey encapsulationKey,
                           DecapsulationKey decapsulationKey) implements FIPS203KeyPair {

    public static FIPS203KeyPair fromBytes(byte[] ek, byte[] dk) {
        return new MLKEMKeyPair(new MLKEMEncapsulationKey(ek), new MLKEMDecapsulationKey(dk));
    }
}
