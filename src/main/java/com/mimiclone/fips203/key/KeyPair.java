package com.mimiclone.fips203.key;

public interface KeyPair {

    EncapsulationKey getEncapsulationKey();

    DecapsulationKey getDecapsulationKey();

}
