package com.mimiclone.fips203;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Parameter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ParameterSet {

    ML_KEM_512(
            2,
            3,
            2,
            10,
            4,
            128,
            800,
            1632,
            768,
            32
    ),

    ML_KEM_768(
            3,
            2,
            2,
            10,
            4,
            192,
            1184,
            2400,
            1088,
            32
    ),

    ML_KEM_1024(
            4,
            2,
            2,
            11,
            5,
            256,
            1568,
            3168,
            1568,
            32
    );

    /**
     * From FIPS203 Section 8:
     * "The parameter k determines the dimensions of the matrix (A hat) that appears in
     * K-PKE.KeyGen and K-PKE.Encrypt.  It also determines the dimensions of vectors s
     * and e in K-PKE.KeyGen and the dimensions of vectors y and e1 in K-PKE.Encrypt.
     */
    private final int k;

    private final int n1;

    private final int n2;

    private final int du;

    private final int dv;

    /**
     * Minimum security strength for hash functions
     */
    private final int minSecurityStrength;

    /**
     * Length of the Encapsulation Key in bytes
     */
    private final int encapsulationKeyLength;

    /**
     * Length of the Decapsulation Key in bytes
     */
    private final int decapsulationKeyLength;

    /**
     * Length of the generated Ciphertext in bytes
     */
    private final int ciphertextLength;

    /**
     * Length of the Shared Secret Key in bytes
     */
    private final int sharedSecretKeyLength;

}
