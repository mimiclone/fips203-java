package com.mimiclone.fips203.key;

public interface FIPS203KeyPair {

    EncapsulationKey getEncapsulationKey();

    DecapsulationKey getDecapsulationKey();

}
