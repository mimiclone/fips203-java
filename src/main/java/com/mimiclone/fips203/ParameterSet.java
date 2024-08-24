package com.mimiclone.fips203;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ParameterSet {

    ML_KEM_512(
            "ML-KEM-512",
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
            "ML-KEM-768",
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
            "ML-KEM-1024",
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

    private final String name;

    /**
     * The n value is not actually part of the parameter set, it is a global variable
     * across the entire algorithm.  However, it is useful to have it available in all
     * the same places the parameters are used, so we set it here with no variance between
     * enum instances.
     */
    private final int n = 256;

    /**
     * The q value is not actually part of the parameter set, it is a global variable
     * across the entire algorithm.  However, it is useful to have it available in all
     * the same places the parameters are used, so we set it here with no variance between
     * enum instances.
     */
    private final int q = 3329;

    /**
     * From FIPS203 Section 8:
     * The parameter k determines the dimensions of the matrix (A hat) that appears in
     * K-PKE.KeyGen and K-PKE.Encrypt.  It also determines the dimensions of vectors s
     * and e in K-PKE.KeyGen and the dimensions of vectors y and e1 in K-PKE.Encrypt.
     */
    private final int k;

    /**
     * Specifies the distribution of vectors s and e in K-PKE.KeyGen and the vector y
     * in K-PKE.Encrypt
     */
    private final int eta1;

    /**
     * Specifies the distribution of vectors e1 and e2 in K-PKE.Encrypt
     */
    private final int eta2;

    /**
     * Used in the functions Compress, Decompress, ByteEncode, ByteDecode
     */
    private final int du;

    /**
     * Used in the functions Compress, Decompress, ByteEncode, ByteDecode
     */
    private final int dv;

    /**
     * Minimum security strength for hash and XOF functions
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
