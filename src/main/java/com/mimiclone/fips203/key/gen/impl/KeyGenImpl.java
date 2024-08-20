package com.mimiclone.fips203.key.gen.impl;

import com.github.aelstad.keccakj.core.KeccakSponge;
import com.github.aelstad.keccakj.fips202.Shake256;
import com.mimiclone.fips203.ParameterSet;
import com.mimiclone.fips203.key.FIPS203KeyPair;
import com.mimiclone.fips203.key.gen.FIPS203KeyGeneration;
import com.mimiclone.fips203.key.gen.KeyPairGenerationException;
import com.mimiclone.fips203.key.impl.FIPS203KeyPairImpl;
import com.mimiclone.fips203.transforms.MimicloneNTT;
import com.mimiclone.fips203.transforms.NumberTheoretic;
import lombok.AllArgsConstructor;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.BitSet;

@AllArgsConstructor
public final class KeyGenImpl implements FIPS203KeyGeneration {

    private final ParameterSet parameterSet;

    /**
     * Precomputed values of gamma = 𝜁^(2BitRev7(𝑖)+1) mod 𝑞 as provided in Appendix A of the FIPS203 Specification
     * on Page 45.  Computation of these values can overflow built-in data types before being
     * bounded by the modulus so it is significantly easier and faster to work with precomputed values.
     * These values are used in the implementation of Algorithm 11 when multiplying polynomial
     * coefficient matrices in NTT space.
     */
    final int[] nttGammaVals = {
            17, -17, 2761, -2761, 583, -583, 2649, -2649,
            1637, -1637, 723, -723, 2288, -2288, 1100, -1100,
            1409, -1409, 2662, -2662, 3281, -3281, 233, -233,
            756, -756, 2156, -2156, 3015, -3015, 3050, -3050,
            1703, -1703, 1651, -1651, 2789, -2789, 1789, -1789,
            1847, -1847, 952, -952, 1461, -1461, 2687, -2687,
            939, -939, 2308, -2308, 2437, -2437, 2388, -2388,
            733, -733, 2337, -2337, 268, -268, 641, -641,
            1584, -1584, 2298, -2298, 2037, -2037, 3220, -3220,
            375, -375, 2549, -2549, 2090, -2090, 1645, -1645,
            1063, -1063, 319, -319, 2773, -2773, 757, -757,
            2099, -2099, 561, -561, 2466, -2466, 2594, -2594,
            2804, -2804, 1092, -1092, 403, -403, 1026, -1026,
            1143, -1143, 2150, -2150, 2775, -2775, 886, -886,
            1722, -1722, 1212, -1212, 1874, -1874, 1029, -1029,
            2110, -2110, 2935, -2935, 885, -885, 2154, -2154
    };

    /**
     * Implements Algorithm 16 (ML-KEM.KeyGen_internal) of the FIPS203 Specification
     *
     * @param d A byte array of exactly length 32 of randomly generated noise
     * @param z A byte array of exactly length 32 of randomly generated noise
     *
     * @return FIPS203KeyPair
     */
    @Override
    public FIPS203KeyPair generateKeyPair(byte[] d, byte[] z) {

        // Ensure d exists and is 32 bytes long
        if (d == null || d.length != 32) {
            throw new KeyPairGenerationException("Entropy source 'd' must be 32 bytes");
        }

        // Ensure z exists and is 32 bytes long
        if (z == null || z.length != 32) {
            throw new KeyPairGenerationException("Entropy source 'z' must be 32 bytes");
        }

        // Call K-PKE.KeyGen
        FIPS203KeyPair baseKeyPair = generateKPKE(d);

        // Retrieve bytes array for the pke keys
        byte[] ekPKE = baseKeyPair.encapsulationKey().getBytes();
        byte[] dkPKE = baseKeyPair.decapsulationKey().getBytes();

        // Hash the encapsulation key
        byte[] ekHash;
        try {
            ekHash = sha3hash256(ekPKE);
        } catch (NoSuchAlgorithmException e) {
            throw new KeyPairGenerationException("Hashing algorithm 'SHA3-256' unavailable on the System.");
        }

        // Calculate byte length of decaps key components
        int dkResultLength = dkPKE.length + ekPKE.length + ekHash.length + z.length;

        // Allocate byte array for composite decaps key
        byte[] dkResult = new byte[dkResultLength];
        ByteBuffer dkResultBuffer = ByteBuffer.wrap(dkResult)
                .put(dkPKE)
                .put(ekPKE)
                .put(ekHash)
                .put(z);

        // Create result keypair
        // The implementation itself will make a copy of the key bytes, so we don't need to
        // worry about it being modified by outside code.
        return FIPS203KeyPairImpl.fromBytes(ekPKE, dkResult);

    }

    /**
     * Implements Algorithm 13 of the FIPS203 Specification.
     * This is described in Section 5.1 of the August 13 Spec Release starting on Page 28
     *
     * @param d An array of exactly 32 randomly generated bytes.
     *
     * @return An initial FIPS203KeyPair instance
     */
    FIPS203KeyPair generateKPKE(byte[] d) {

        // Ensure d exists and is 32 bytes long
        if (d == null || d.length != 32) {
            throw new KeyPairGenerationException("Entropy source 'd' must be 32 bytes");
        }

        // Get k as a byte value from parameter set
        int k = parameterSet.getK();
        byte[] kb = { (byte) k };

        // 1: Expand 32 + 1 bytes to two pseudorandom 32-byte seeds
        byte[] dk = new byte[33];
        ByteBuffer buffer = ByteBuffer.wrap(dk);
        buffer.put(d);
        buffer.put(kb);

        byte[] rho;
        byte[] sigma;
        try {

            // Generate the combined seeds
            byte[] rhoAndSigma = sha3hash512(dk);
            if (rhoAndSigma == null || rhoAndSigma.length != 64) {
                throw new KeyPairGenerationException("Unable to generate 'rho' and 'sigma' 32-byte seed values");
            }

            // Wrap rhoAndSigma in a ByteBuffer for future reads
            ByteBuffer rhoAndSigmaBuffer = ByteBuffer.wrap(rhoAndSigma);

            // Split out rho
            rho = new byte[32];
            rhoAndSigmaBuffer.get(rho);

            // Split out sigma
            sigma = new byte[32];
            rhoAndSigmaBuffer.get(sigma);

        } catch (NoSuchAlgorithmException e) {
            throw new KeyPairGenerationException("K-PKE seed generation failed because SHA3-512 could not be found on the system.");
        }

        int n = 0;

        int[][][] aHatMatrix = new int[k][k][256];

        // Generate A hat matrix
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < k; j++) {
                aHatMatrix[i][j] = sampleNTT(
                        ByteBuffer.wrap(new byte[34])
                                .put(rho).put((byte) j).put((byte) i)
                                .array().clone()
                );
            }
        }

        // Generate s
        int[][] s = new int[k][256];
        for (int i = 0; i < k; i++) {
            s[i] = samplePolyCBD(genPRFBytes(sigma, (byte) n));
            n++;
        }

        // Generate e
        int[][] e = new int[k][256];
        for (int i = 0; i < k; i++) {
            e[i] = samplePolyCBD(genPRFBytes(sigma, (byte) n));
            n++;
        }

        // Retrieve NTT implementation
        NumberTheoretic ntt = MimicloneNTT.fips203();

        // Calculate sHat
        int[][] sHat = new int[k][256];
        for (int i = 0; i < k; i++) {
            sHat[i] = ntt.transform(s[i]);
        }

        // Calculate eHat
        int[][] eHat = new int[k][256];
        for (int i = 0; i < k; i++) {
            eHat[i] = ntt.transform(e[i]);
        }

        // Noisy linear system in NTT domain
        int[][] tHat = new int[k][256];

        // Iterate over the sheets of aHat
        for (int i = 0; i < k; i++) {

            // Iterate over the lanes of this slice of aHat
            for (int j = 0; j < k; j++) {

                // Perform the NTT multiplication of a lane from aHat and a lane from sHat
                int[] mul = multiplyNTTs(aHatMatrix[i][j], sHat[j]);

                // for each entry in tHat, add the result of the NTT Multiplication
                for (int entry = 0; entry < 256; entry++) {
                    tHat[i][entry] = mul[entry] + eHat[j][entry];
                }

            }
        }

        // ByteEncode ekPKE
        byte[] ekPKE = new byte[384*k+32];
        ByteBuffer ekPKEBuffer = ByteBuffer.wrap(ekPKE);
        for (int i = 0; i < k; i++) {
            ekPKEBuffer.put(byteEncode12(tHat[i]));
        }

        // Append Rho
        ekPKEBuffer.put(rho);

        // ByteEncode dkPKE
        byte[] dkPKE = new byte[384*k];
        ByteBuffer dkPKEBuffer = ByteBuffer.wrap(dkPKE);
        for (int i = 0; i < k; i++) {
            dkPKEBuffer.put(byteEncode12(sHat[i]));
        }

        // Create and return the wrapped KeyPair
        return FIPS203KeyPairImpl.fromBytes(ekPKE, dkPKE);
    }

    /**
     * Implementation of Algorithm 3 in the FIPS203 Standard.
     * <p>
     * Java does not have a native data type for an individual bit aside from boolean, which
     * comes with the baggage of logical interpretation.  For that reason we substitute a
     * {@code BitSet} class for the array of bits specified in the Standard.
     * <p>
     * This algorithm is likely not to be used at all in the implementation, but we provide
     * it for completeness with the standard.
     *
     * @param bits a {@code BitSet} of length {@code 8 * l}
     *
     * @return A {@code byte} array of length {@code l}
     */
    byte[] bitsToBytes(BitSet bits) {

        // Compiler check to ensure number of bits is a multiple of 8.
        assert bits.length() % 8 == 0;

        // Convert to a copied byte array
        return bits.toByteArray().clone();

    }

    /**
     * Implements Algorithm 5 from Page 22 of the FIPS 203 Specification.
     * Encodes an array of 256 d-bit integers into a byte array for 1 <= d <= 12.
     * The value of d (number of bits) is determined by the parameter set.
     *
     * @param f An array of 256 integers in modulo m
     * @return
     */
    byte[] byteEncode12(int[] f) {

        byte[] outputArray = new byte[384];
        int outputIndex = 0;
        int bitBuffer = 0;
        int bitsInBuffer = 0;

        for (int value: f) {

            // Extract the least significant 12 bits
            int bits12 = value & 0xFFF;

            // Add these 12 bits to the buffer
            bitBuffer = (bitBuffer << 12) | bits12;
            bitsInBuffer += 12;

            // While we have at least 8 bits in buffer, output a byte
            while (bitsInBuffer >= 8) {
                bitsInBuffer -= 8;
                outputArray[outputIndex++] = (byte) (bitBuffer >> bitsInBuffer);
            }
        }

        return outputArray;

    }

    /**
     * Algorithm 11 of FIPS203 Specification
     * Multiples two coefficient vectors of length 256 in NTT space
     *
     * @param fHat An array of 256 ints representing the coefficients of a function f
     * @param gHat An array of 256 ints representing the coefficients of a function g
     *
     * @return An array of 256 ints that is the result of multiplication of the inputs in NTT space.  This value is
     * called hHat in the specification.
     */
    int[] multiplyNTTs(int[] fHat, int[] gHat) {

        // Compiler validation of input
        assert fHat != null;
        assert fHat.length == 256;

        // Compiler validation of input
        assert gHat != null;
        assert gHat.length == 256;

        int[] hHat = new int[256];

        for (int i = 0; i < 128; i++) {
            int[] c = baseCaseMultiply(
                    fHat[2*i],
                    fHat[2*i+1],
                    gHat[2*i],
                    gHat[2*i+1],
                    nttGammaVals[i]
            );
            hHat[2*i] = c[0];
            hHat[2*i+1] = c[1];
        }

        // Return the result
        return hHat;

    }

    int[] baseCaseMultiply(int a0, int a1, int b0, int b1, int gamma) {

        BigInteger a = BigInteger.valueOf(a0);
        BigInteger b = BigInteger.valueOf(a1);
        BigInteger c = BigInteger.valueOf(b0);
        BigInteger d = BigInteger.valueOf(b1);
        BigInteger e = BigInteger.valueOf(gamma);
        BigInteger q = BigInteger.valueOf(3329);

        // Perform multiplications using BigInteger to prevent overflow and make modulo arithmetic easier
        int c0 = a.multiply(c).mod(q).add(b.multiply(d).mod(q).multiply(d).mod(q).multiply(e).mod(q)).mod(q).intValue();
        int c1 = a.multiply(d).mod(q).add( b.multiply(c).mod(q)).mod(q).intValue();

        return new int[]{c0, c1};
    }

    int[] sampleNTT(byte[] seedPlusIndices) {

        // TODO: Init context with XOF

        // TODO: Absorb seedPlusIndices into context with XOF

        // TODO: Implement the rest

        // TODO: Replace with actual return value
        return new int[256];
    }

    /**
     * Implements Algorithm 8 of the FIPS203 specification.
     *
     * @param input A byte array of 64 * eta bytes, where eta is defined by the ParamaterSet.
     * @return An array of 256 integers within modulo q=3329 space
     */
    int[] samplePolyCBD(byte[] input) {

        // Get information from parameter set
        int eta = parameterSet.getEta1();

        // Validate input length
        if (input == null || input.length != 64*eta) {
            throw new KeyPairGenerationException("PolyCBD sample input must be %d bytes".formatted(64*eta));
        }

        // Declare result array
        int[] result = new int[256];

        BitSet b = BitSet.valueOf(input);
        for (int i = 0; i < 256; i++) {

            // Calculate X
            int x = 0;
            for (int j = 0; j < eta; j++) {
                x += b.get(2*i*eta + j) ? 1 : 0;
            }

            // Calculate Y
            int y = 0;
            for (int j = 0; j < eta; j++) {
                y += b.get(2*i*eta + eta + j) ? 1 : 0;
            }

            result[i] = BigInteger.valueOf(x - y).mod(BigInteger.valueOf(3329)).intValue();
        }

        return result;
    }

    /**
     * Turns a 32-byte array of secrets (plus padding) into a fixed-length output of 64*ETA bytes using
     * the SHAKE256 algorithm as a PRF (Pseudo Random Function).
     *
     * @param s
     * @param b
     * @return
     */
    byte[] genPRFBytes(byte[] s, byte b) {

        int eta = parameterSet.getEta1();

        // Add the
        byte[] sb = ByteBuffer.allocate(33)
                .put(s).put(b)
                .array().clone();

        byte[] result;
        try {
            result = shake256(sb, 64*eta);
        } catch (NoSuchAlgorithmException e) {
            throw new KeyPairGenerationException("SHAKE256 algorithm not available on system.");
        }

        return result;

    }

    /**
     * Implements the H hash function (SHA3-256) from the FIPS203 Specification.
     *
     * @param s A variable length array of bytes
     *
     * @return A byte array of exactly 32 bytes
     */
    final byte[] sha3hash256(byte[] s) throws NoSuchAlgorithmException {
        byte[] result = new byte[32];

        MessageDigest md = MessageDigest.getInstance("SHA3-256");
        result = md.digest(s);

        return result;
    }

    /**
     * Implements the SHAKE256 from the FIPS203 Specification.
     *
     * @param s A variable length array of bytes
     * @param outputLength number of bytes to return as output
     *
     * @return outputLength A byte array of exactly numBytes bytes
     */
    final byte[] shake256(byte[] s, int outputLength) throws NoSuchAlgorithmException {

        // TODO: Finish FIPS202 SHAKE256 XOF implementation
        // System algorithm is bullshit and doesn't come with XOF functionality
        // Temporarily using a third party library which is only conformant with the draft spec.

        KeccakSponge sponge = new Shake256();
        sponge.getAbsorbStream().write(s);

        byte[] digest = new byte[outputLength];
        sponge.getSqueezeStream().read(digest);

        return digest;

    }

    /**
     * Implements the G hash function (SHA3-512) from the FIPS203 Specification.
     *
     * The spec says that this function should return two 32-byte arrays, but since
     * Java does not handle tuple wrapping in this fashion we return a single
     * concatenated 64-byte array and expect the caller to split the upper and lower
     * 32-bits into separate seed values.
     *
     * In this future this may be wrapped in a SeedValue interface
     *
     * @param c Variable length byte array input seed
     * @return An array of exactly 64 bytes representing two concatenated 32-byte seed values
     *
     * @throws NoSuchAlgorithmException If the SHA3-512 algorithm cannot be found on the system.
     */
    protected final byte[] sha3hash512(byte[] c) throws NoSuchAlgorithmException {
        byte[] result = new byte[64];

        MessageDigest md = MessageDigest.getInstance("SHA3-512");
        result = md.digest(c);

        return result;
    }

}
