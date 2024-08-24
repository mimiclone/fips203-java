package com.mimiclone.fips203.decaps.mlkem;

import com.mimiclone.fips203.ParameterSet;
import com.mimiclone.fips203.decaps.DecapsulationException;
import com.mimiclone.fips203.key.DecapsulationKey;
import com.mimiclone.fips203.key.SharedSecretKey;
import com.mimiclone.fips203.message.CipherText;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MLKEMDecapsulator implements com.mimiclone.fips203.decaps.Decapsulator {

    final ParameterSet parameterSet;

    public static MLKEMDecapsulator create(ParameterSet parameterSet) {
        return new MLKEMDecapsulator(parameterSet);
    }

    @Override
    public SharedSecretKey decapsulate(DecapsulationKey key, CipherText cipherText) throws DecapsulationException {
        return null;
    }
}
