package com.mimiclone.fips203;


import com.mimiclone.fips203.key.DecapsulationKey;
import com.mimiclone.fips203.key.EncapsulationKey;
import com.mimiclone.fips203.key.FIPS203KeyPair;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FIPS203Tests {

    private final FIPS203 fips203MlKem512 = new MimicloneFIPS203(ParameterSet.ML_KEM_512);
    private final FIPS203 fips203MlKem768 = new MimicloneFIPS203(ParameterSet.ML_KEM_768);
    private final FIPS203 fips203MlKem1024 = new MimicloneFIPS203(ParameterSet.ML_KEM_1024);

    @Test
    void mlKem512KeyPairGen() {

        // Validate the Parameter Set
        ParameterSet parameterSet = ParameterSet.ML_KEM_512;
        assertEquals(parameterSet, fips203MlKem512.getParameterSet());
        assertEquals(800, parameterSet.getEncapsulationKeyLength());
        assertEquals(1632, parameterSet.getDecapsulationKeyLength());
        assertEquals(768, parameterSet.getCiphertextLength());
        assertEquals(32, parameterSet.getSharedSecretKeyLength());

        // Generate the key pair
        FIPS203KeyPair keyPair = fips203MlKem512.generateKeyPair();

        // Ensure the KeyPair object is not null
        assertNotNull(keyPair);

        // Get the EncapsulationKey
        EncapsulationKey encapsulationKey = keyPair.getEncapsulationKey();

        // Ensure the EncapsulationKey is not null
        assertNotNull(encapsulationKey);

        // Get the key bytes
        byte[] encapsulationKeyBytes = encapsulationKey.getBytes();

        // Ensure the bytes are not null
        assertNotNull(encapsulationKeyBytes);

        // Ensure the bytes are the appropriate length based on the parameter set
        assertEquals(parameterSet.getEncapsulationKeyLength(), encapsulationKeyBytes.length);

        // Get the DecapsulationKey
        DecapsulationKey decapsulationKey = keyPair.getDecapsulationKey();

        // Ensure the DecapsulationKey is not null
        assertNotNull(decapsulationKey);

        // Get the key bytes
        byte[] decapsulationKeyBytes = decapsulationKey.getBytes();

        // Ensure the bytes are not null
        assertNotNull(decapsulationKeyBytes);

        // Ensure the bytes are the appropriate length based on the parameter set
        assertEquals(parameterSet.getDecapsulationKeyLength(), decapsulationKeyBytes.length);

    }

}
