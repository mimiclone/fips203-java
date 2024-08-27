package com.mimiclone.fips203.encaps.mlkem;

import com.mimiclone.fips203.ParameterSet;
import com.mimiclone.fips203.codec.Codec;
import com.mimiclone.fips203.codec.MLKEMCodec;
import com.mimiclone.fips203.encaps.Encapsulation;
import com.mimiclone.fips203.encaps.EncapsulationException;
import com.mimiclone.fips203.hash.Hash;
import com.mimiclone.fips203.hash.MLKEMHash;
import com.mimiclone.fips203.key.EncapsulationKey;
import com.mimiclone.fips203.sampler.MLKEMSampler;
import com.mimiclone.fips203.sampler.Sampler;
import com.mimiclone.fips203.transforms.MLKEMNumberTheoreticTransform;
import com.mimiclone.fips203.transforms.NumberTheoreticTransform;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.nio.ByteBuffer;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MLKEMEncapsulator implements com.mimiclone.fips203.encaps.Encapsulator {

    private final ParameterSet parameterSet;

    private final Hash hash;

    private final Codec codec;

    private final Sampler sampler;

    private final NumberTheoreticTransform ntt;

    public static MLKEMEncapsulator create(ParameterSet parameterSet) {
        Hash hash = MLKEMHash.create(parameterSet);
        Codec codec = MLKEMCodec.build(parameterSet);
        Sampler sampler = MLKEMSampler.create(parameterSet);
        NumberTheoreticTransform ntt = MLKEMNumberTheoreticTransform.fips203(parameterSet);
        return new MLKEMEncapsulator(parameterSet, hash, codec, sampler, ntt);
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
        byte[] cipherTextBytes = encryptKPKE(ek.getBytes(), entropy, random);

        return MLKEMEncapsulation.build(sharedSecretBytes, cipherTextBytes);
    }

    /**
     * Implements Algorithm 14  (K-PKE.Encrypt) of the FIPS203 Standard.
     * @param ekPKE An array of {@code 384*k+32} bytes representing the encryption key.
     * @param message An array of 32 bytes representing the message to encrypt.
     * @param random An array of 32 bytes representing the entropy into the system.
     * @return A {@code 32(du*k + dv)} byte array representing the cipherText.
     */
    byte[] encryptKPKE(byte[] ekPKE, byte[] message, byte[] random) {

        int n = 0;

        // Create a byte buffer to wrap the passed in ekPKE
        ByteBuffer ekPKEBuffer = ByteBuffer.wrap(ekPKE);

        // Allocate tHat
        int[][] tHat = new int[parameterSet.getK()][];

        // Iterate over the 384-byte chunks of tHat and perform a byte decode on each chunk
        // When this operation is complete there will be 32-bytes remaining in the buffer
        // which are the seed rho.
        for (int i = 0; i < parameterSet.getK(); i++) {

            // Split off a 384-byte chunk of ekPKE
            byte[] ekPKEChunk = new byte[384];
            ekPKEBuffer.get(ekPKEChunk);

            // Fill tHat
            tHat[i] = codec.byteDecode(12, ekPKEChunk);

        }

        // Split off rho
        byte[] rho = new byte[32];
        ekPKEBuffer.get(rho);

        // Regenerate aHatMatrix
        int[][][] aHatMatrix = new int[parameterSet.getK()][parameterSet.getK()][];
        for (int i = 0; i < parameterSet.getK(); i++) {
            for (int j = 0; j < parameterSet.getK(); j++) {
                aHatMatrix[i][j] = sampler.sampleNTT(rho, (byte) j, (byte) i);
            }
        }

        // Generate y
        int[][] y = new int[parameterSet.getK()][];
        for (int i = 0; i < parameterSet.getK(); i++) {
            y[i] = sampler.samplePolyCBDEta1(hash.prfEta1(random, (byte) n));
            n++;
        }

        // Generate e1
        int[][] e1 = new int[parameterSet.getK()][];
        for (int i = 0; i < parameterSet.getK(); i++) {
            e1[i] = sampler.samplePolyCBDEta2(hash.prfEta2(random, (byte) n));
            n++;
        }

        // Sample e2
        int[] e2 = sampler.samplePolyCBDEta2(hash.prfEta2(random, (byte) n));

        // Generate yHat
        int[][] yHat = new int[parameterSet.getK()][];
        for (int i = 0; i < parameterSet.getK(); i++) {
            yHat[i] = ntt.transform(y[i]);
        }

        // Generate u
        int[][] u = new int[parameterSet.getK()][];
        int[][] matrixOp = ntt.matrixMultiply(ntt.matrixTranspose(aHatMatrix), yHat);
        for (int i = 0; i < parameterSet.getK(); i++) {
            u[i] = ntt.inverse(matrixOp[i]);
        }
        u = ntt.matrixAdd(u, e1);

        // Generate mu
        int[] decodedMessage = codec.byteDecode(1, message);
        int[] mu = codec.decompress(1, decodedMessage);

        // Generate v
        int[] v = ntt.arrayAdd(ntt.arrayAdd(ntt.inverse(ntt.vectorTransposeMultiply(tHat, yHat)), e2), mu);

        // Generate result
        int resultLength = 32 * (parameterSet.getDu() * parameterSet.getK() + parameterSet.getDv());
        ByteBuffer resultBuffer = ByteBuffer.allocate(resultLength);

        for (int[] ints : u) {
            resultBuffer.put(codec.byteEncode(parameterSet.getDu(), codec.compress(parameterSet.getDu(), ints)));
        }
        resultBuffer.put(codec.byteEncode(parameterSet.getDv(), codec.compress(parameterSet.getDv(), v)));

        return resultBuffer.array().clone();

    }

}
