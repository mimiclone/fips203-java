package com.mimiclone.fips203.provider;

import com.mimiclone.fips203.ParameterSet;
import com.mimiclone.fips203.decaps.provider.MLKEMDecapsulatorProvider;
import lombok.RequiredArgsConstructor;

import javax.crypto.KEMSpi;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;

@RequiredArgsConstructor
public class MLKEMProvider implements KEMSpi {

    private final ParameterSet params;

    public static MLKEMProvider getMLKEM512Provider() {
        return new MLKEMProvider(ParameterSet.ML_KEM_512);
    }

    public static MLKEMProvider getMLKEM768Provider() {
        return new MLKEMProvider(ParameterSet.ML_KEM_768);
    }

    public static MLKEMProvider getMLKEM1024Provider() {
        return new MLKEMProvider(ParameterSet.ML_KEM_1024);
    }

    @Override
    public EncapsulatorSpi engineNewEncapsulator(PublicKey publicKey, AlgorithmParameterSpec spec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException, InvalidKeyException {
        return null;
    }

    @Override
    public DecapsulatorSpi engineNewDecapsulator(PrivateKey privateKey, AlgorithmParameterSpec spec) throws InvalidAlgorithmParameterException, InvalidKeyException {
        return MLKEMDecapsulatorProvider.getInstance(privateKey, spec);
    }
}
