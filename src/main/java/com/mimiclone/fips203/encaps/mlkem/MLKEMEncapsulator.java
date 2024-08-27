package com.mimiclone.fips203.encaps.mlkem;

import com.mimiclone.fips203.ParameterSet;
import com.mimiclone.fips203.encaps.Encapsulation;
import com.mimiclone.fips203.encaps.EncapsulationException;
import com.mimiclone.fips203.encrypt.Encryptor;
import com.mimiclone.fips203.encrypt.kpke.KPKEEncryptor;
import com.mimiclone.fips203.hash.Hash;
import com.mimiclone.fips203.hash.MLKEMHash;
import com.mimiclone.fips203.key.EncapsulationKey;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.nio.ByteBuffer;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MLKEMEncapsulator implements com.mimiclone.fips203.encaps.Encapsulator {

    private final ParameterSet parameterSet;
    private final Hash hash;
    private final Encryptor encryptor;

    public static MLKEMEncapsulator create(ParameterSet parameterSet) {
        return new MLKEMEncapsulator(
                parameterSet,
                MLKEMHash.create(parameterSet),
                KPKEEncryptor.create(parameterSet)
        );
    }

    /**
     * Implements Algorithm 17 (ML-KEM.Encaps_internal) of the FIPS203 Standard
     * @param ek
     * @param entropy
     * @return
     * @throws EncapsulationException
     */
    @Override
    public Encapsulation encapsulate(EncapsulationKey ek, byte[] entropy) throws EncapsulationException {

        // Derive encapsulation key hash
        byte[] ekHash = hash.hHash(ek.getBytes());

        // Concatenate entropy and encapsulation key hash
        byte[] entropyAndKeyHash = ByteBuffer.allocate(64).put(entropy).put(ekHash).array();

        // Generate the shared secret and randomness
        byte[] sharedSecretAndRandom = hash.gHash(entropyAndKeyHash);

        // Split out the shared secret and randomness
        ByteBuffer sharedSecretAndRandomBuffer = ByteBuffer.wrap(sharedSecretAndRandom);

        // Split out shared secret
        byte[] sharedSecretBytes = new byte[32];
        sharedSecretAndRandomBuffer.get(sharedSecretBytes);

        // Split out random
        byte[] random = new byte[32];
        sharedSecretAndRandomBuffer.get(random);

        // Generate cipherText bytes
        byte[] cipherTextBytes = encryptor.encrypt(ek.getBytes(), entropy, random);

        return MLKEMEncapsulation.build(sharedSecretBytes, cipherTextBytes);
    }

}
