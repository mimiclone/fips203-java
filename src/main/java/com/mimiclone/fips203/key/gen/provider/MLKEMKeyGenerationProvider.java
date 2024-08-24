package com.mimiclone.fips203.key.gen.provider;

import com.mimiclone.fips203.ParameterSet;
import lombok.RequiredArgsConstructor;

import java.security.KeyPair;
import java.security.KeyPairGeneratorSpi;
import java.security.SecureRandom;

@RequiredArgsConstructor
public class MLKEMKeyGenerationProvider extends KeyPairGeneratorSpi {

    private final ParameterSet params;

    public static MLKEMKeyGenerationProvider getMLKEM512Provider() {
        return new MLKEMKeyGenerationProvider(ParameterSet.ML_KEM_512);
    }

    public static MLKEMKeyGenerationProvider getMLKEM768Provider() {
        return new MLKEMKeyGenerationProvider(ParameterSet.ML_KEM_768);
    }

    public static MLKEMKeyGenerationProvider getMLKEM1024Provider() {
        return new MLKEMKeyGenerationProvider(ParameterSet.ML_KEM_1024);
    }

    @Override
    public void initialize(int keysize, SecureRandom random) {

    }

    @Override
    public KeyPair generateKeyPair() {
        return null;
    }
}
