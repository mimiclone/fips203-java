package com.mimiclone.fips203.decaps.mlkem;

import com.mimiclone.fips203.ParameterSet;
import com.mimiclone.fips203.decaps.DecapsulationException;
import com.mimiclone.fips203.decrypt.Decryptor;
import com.mimiclone.fips203.decrypt.kpke.KPKEDecryptor;
import com.mimiclone.fips203.encrypt.Encryptor;
import com.mimiclone.fips203.encrypt.kpke.KPKEEncryptor;
import com.mimiclone.fips203.hash.Hash;
import com.mimiclone.fips203.hash.MLKEMHash;
import com.mimiclone.fips203.key.DecapsulationKey;
import com.mimiclone.fips203.key.SharedSecretKey;
import com.mimiclone.fips203.key.mlkem.MLKEMSharedSecretKey;
import com.mimiclone.fips203.message.CipherText;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.nio.ByteBuffer;
import java.util.Arrays;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MLKEMDecapsulator implements com.mimiclone.fips203.decaps.Decapsulator {

    private final ParameterSet parameterSet;

    private final Hash hash;
    private final Encryptor encryptor;
    private final Decryptor decryptor;

    public static MLKEMDecapsulator create(ParameterSet parameterSet) {
        return new MLKEMDecapsulator(
                parameterSet,
                MLKEMHash.create(parameterSet),
                KPKEEncryptor.create(parameterSet),
                KPKEDecryptor.create(parameterSet)
        );
    }

    @Override
    public SharedSecretKey decapsulate(DecapsulationKey key, CipherText cipherText) throws DecapsulationException {

        // Wrap a copy of the decapsulation key into a buffer
        ByteBuffer dkBuffer = ByteBuffer.wrap(key.getBytes());

        // Extract the PKE decryption key (first 384*k bytes)
        byte[] dkPKE = new byte[384*parameterSet.getK()];
        dkBuffer.get(dkPKE);

        // Extract the PKE encryption key (next 384*k + 32 bytes)
        byte[] ekPKE = new byte[384*parameterSet.getK() + 32];
        dkBuffer.get(ekPKE);

        // Extract the PKE encryption key hash (next 32 bytes)
        byte[] h = new byte[32];
        dkBuffer.get(h);

        // Extract the implicit rejection value (next 32 bytes)
        byte[] z = new byte[32];
        dkBuffer.get(z);

        // Extract the cipherText bytes
        byte[] c = cipherText.getBytes();

        // Decrypt the ciphertext
        byte[] mPrime = decryptor.decrypt(dkPKE, c);

        // Hash the concatenation of the shared secret and its own hash
        ByteBuffer integrityCheckInputBuffer = ByteBuffer.allocate(mPrime.length + h.length)
                        .put(mPrime).put(h);
        ByteBuffer integrityCheckOutputBuffer = ByteBuffer.wrap(hash.gHash(integrityCheckInputBuffer.array().clone()));

        // Split out kPrime
        byte[] kPrime = new byte[32];
        integrityCheckOutputBuffer.get(kPrime);

        // Split out rPrime
        byte[] rPrime = new byte[32];
        integrityCheckOutputBuffer.get(rPrime);

        // Generate kBar (implicit rejection flag)
        ByteBuffer implicitRejectionBuffer = ByteBuffer.allocate(z.length + c.length).put(z).put(c);
        byte[] kBar = hash.jHash(implicitRejectionBuffer.array().clone());

        // K-PKE encrypt the recovered shared secret and the calculated randomness kPrime
        byte[] cPrime = encryptor.encrypt(ekPKE, mPrime, rPrime);

        // Check integrity of calculated values
        if (!Arrays.equals(c, cPrime)) {

            // Set the implicit rejection flag
            kPrime = kBar.clone();

            // Destroy the internal implicit rejection value
            // NOTE: Java uses JVM-managed garbage collection so we must overwrite the value to guarantee
            //       value destruction.
            Arrays.fill(kBar, (byte)0L);
            kBar = null;

        }

        // Construct and return the calculated shared secret key
        return MLKEMSharedSecretKey.create(kPrime);
    }
}
