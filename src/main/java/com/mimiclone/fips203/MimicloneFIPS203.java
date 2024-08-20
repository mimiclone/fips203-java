package com.mimiclone.fips203;

import com.mimiclone.fips203.key.*;
import com.mimiclone.fips203.key.check.KeyPairCheckException;
import com.mimiclone.fips203.key.gen.FIPS203KeyGeneration;
import com.mimiclone.fips203.key.gen.KeyPairGenerationException;
import com.mimiclone.fips203.key.gen.impl.KeyGenImpl;

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

    MimicloneFIPS203(ParameterSet parameterSet) {

        // Assign the chosen parameter set
        this.parameterSet = parameterSet;

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
    public FIPS203KeyPair generateKeyPair() {

        // Get the secure RBG
        SecureRandom secureRandom;

        try {

            // Create secure random parameters
            SecureRandomParameters secureParams = DrbgParameters.instantiation(
                    parameterSet.getMinSecurityStrength(),
                    DrbgParameters.Capability.PR_AND_RESEED,
                    null);

            // Create sure random instance
            secureRandom = SecureRandom.getInstance(SECURE_RBG_ALGO, secureParams);

        } catch (NoSuchAlgorithmException e) {
            // FIPS203:Algorithm19:Line4
            // Not finding the algorithm would case d and z to be null,
            // so we throw an error here.
            throw new KeyPairGenerationException(e.getMessage());
        }

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
        FIPS203KeyGeneration keyGeneration = new KeyGenImpl(parameterSet);
        return keyGeneration.generateKeyPair(d, z);

    }

    @Override
    public void keyPairCheck(FIPS203KeyPair keyPair) throws KeyPairCheckException {
        // TODO: Implement key pair checking
        throw new KeyPairCheckException("Key pair checking has not yet been implemented.");
    }

    @Override
    public byte[] encapsulateBlock(EncapsulationKey key, byte[] clearText) {
        return new byte[0];
    }

    @Override
    public byte[] decapsulateBlock(DecapsulationKey key, byte[] cypherText) {
        return new byte[0];
    }
}
