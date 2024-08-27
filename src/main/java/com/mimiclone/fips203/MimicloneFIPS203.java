package com.mimiclone.fips203;

import com.mimiclone.fips203.decaps.Decapsulator;
import com.mimiclone.fips203.decaps.mlkem.MLKEMDecapsulator;
import com.mimiclone.fips203.encaps.Encapsulation;
import com.mimiclone.fips203.encaps.Encapsulator;
import com.mimiclone.fips203.encaps.mlkem.MLKEMEncapsulator;
import com.mimiclone.fips203.key.*;
import com.mimiclone.fips203.key.check.KeyPairCheckException;
import com.mimiclone.fips203.key.gen.KeyPairGeneration;
import com.mimiclone.fips203.key.gen.KeyPairGenerationException;
import com.mimiclone.fips203.key.gen.mlkem.MLKEMKeyPairGenerator;
import com.mimiclone.fips203.message.CipherText;

import java.security.DrbgParameters;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.SecureRandomParameters;

public class MimicloneFIPS203 implements FIPS203 {

    // Global constant n denoting the standard block size in bits
    // Defined in section 2.4 of the FIPS203 Specification
    public static final int N = 256;

    // Global constant q denoting the prime integer 3329 = 2^8 * 13 + 1
    // Defined in section 2.4 of the FIPS203 Specification
    public static final int Q = 3329;

    // Secure RBG algorithm set name
    private static final String SECURE_RBG_ALGO = "DRBG";

    // FIPS 203 Parameter Set assigned
    private final ParameterSet parameterSet;

    private final SecureRandom secureRandom;

    private final KeyPairGeneration keyPairGenerator;

    private final Encapsulator encapsulator;

    private final Decapsulator decapsulator;

    MimicloneFIPS203(ParameterSet parameterSet) {

        // Assign the chosen parameter set
        this.parameterSet = parameterSet;

        try {

            // Create secure random parameters
            SecureRandomParameters secureParams = DrbgParameters.instantiation(
                    parameterSet.getMinSecurityStrength(),
                    DrbgParameters.Capability.PR_AND_RESEED,
                    null);

            // Create sure random instance
            secureRandom = SecureRandom.getInstance(SECURE_RBG_ALGO, secureParams);

        } catch (NoSuchAlgorithmException e) {
            throw new FIPS203Exception(e.getMessage());
        }

        // Initialize the Key Pair Generator
        this.keyPairGenerator = MLKEMKeyPairGenerator.create(parameterSet);

        // Initialize the Encapsulator
        this.encapsulator = MLKEMEncapsulator.create(parameterSet);

        // Initialize the Decapsulator
        this.decapsulator = new MLKEMDecapsulator(parameterSet);

    }

    @Override
    public ParameterSet getParameterSet() {
        return parameterSet;
    }

    /**
     * Implements Algorithm 19 (ML-KEM.KeyGen) of the FIPS203 Specification
     *
     * @return A FIPS203KeyPair instance.
     */
    @Override
    public KeyPair generateKeyPair() throws KeyPairGenerationException {

        // FIPS203:Algorithm19:Line1
        // Generate 'd', a value of 32 random bytes
        byte[] d = new byte[32];
        secureRandom.nextBytes(d);

        // FIPS203:Algorithm19:Line2
        // Generate 'z', a value of 32 random bytes
        byte[] z = new byte[32];
        secureRandom.nextBytes(z);

        // FIPS203:Algorithm19:Line3
        // The spec requires a null check here for d and z, but it isn't possible
        // for them to be null.  Checking would raise a compiler error

        // Invoke Key Generation
        KeyPairGeneration keyGeneration = MLKEMKeyPairGenerator.create(parameterSet);
        return keyGeneration.generateKeyPair(d, z);

    }

    /**
     * Implements Algorithm
     * @param keyPair
     * @throws KeyPairCheckException
     */
    @Override
    public void keyPairCheck(KeyPair keyPair) throws KeyPairCheckException {
        // TODO: Implement key pair checking
        throw new KeyPairCheckException("Key pair checking has not yet been implemented.");
    }

    /**
     * Implements Algorithm 20 (ML-KEM.Encaps) of the FIPS203 Specification.
     *
     * This generates 32-bytes of entropy and passes it along to the internal implementation.
     *
     * @param key An {@code EncapsulationKey} instance.
     * @return A {@code SharedSecretKey}
     */
    @Override
    public Encapsulation encapsulate(EncapsulationKey key) {

        // Generate 32 bytes of securely random entropy
        byte[] m = new byte[32];
        secureRandom.nextBytes(m);

        // The spec requires a null check here for m, but Java is designed such that it isn't possible for them to
        // be null.

        return encapsulator.encapsulate(key, m);
    }

    /**
     * Implements Algorithm 21 (ML-KEM.Decaps) of the FIPS203 Specification.
     * No randomness is generated so this is a simple passthrough to the internal implementation.
     *
     * @param key
     * @param cipherText
     * @return
     */
    @Override
    public SharedSecretKey decapsulate(DecapsulationKey key, CipherText cipherText) {
        return decapsulator.decapsulate(key, cipherText);
    }
}
