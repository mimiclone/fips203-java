package com.mimiclone.fips203.key.impl;

import com.mimiclone.fips203.key.DecapsulationKey;
import com.mimiclone.fips203.key.EncapsulationKey;
import com.mimiclone.fips203.key.FIPS203KeyPair;
import lombok.AllArgsConstructor;
import javax.security.auth.kerberos.EncryptionKey;

@AllArgsConstructor
public class FIPS203KeyPairImpl implements FIPS203KeyPair {

    private final EncapsulationKey encapsulationKey;
    private final DecapsulationKey decapsulationKey;

    @Override
    public EncapsulationKey getEncapsulationKey() {
        return encapsulationKey;
    }

    @Override
    public DecapsulationKey getDecapsulationKey() {
        return decapsulationKey;
    }
}
