package com.mimiclone.fips203.encaps.mlkem;

import com.mimiclone.fips203.ParameterSet;
import com.mimiclone.fips203.encaps.EncapsulationException;
import com.mimiclone.fips203.key.EncapsulationKey;
import com.mimiclone.fips203.key.SharedSecretKey;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MLKEMEncapsulator implements com.mimiclone.fips203.encaps.Encapsulator {

    final ParameterSet parameterSet;

    @Override
    public SharedSecretKey encapsulate(EncapsulationKey ek) throws EncapsulationException {
        return null;
    }

}
