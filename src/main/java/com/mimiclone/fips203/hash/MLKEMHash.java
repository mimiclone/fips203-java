package com.mimiclone.fips203.hash;

import com.mimiclone.fips202.keccak.core.KeccakSponge;
import com.mimiclone.fips202.keccak.io.BitInputStream;
import com.mimiclone.fips202.keccak.io.BitOutputStream;
import com.mimiclone.fips203.ParameterSet;
import com.mimiclone.fips203.key.gen.KeyPairGenerationException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MLKEMHash implements Hash {

    private final ParameterSet parameterSet;

    private final MessageDigest sha3Hash256;

    private final MessageDigest sha3Hash512;

    private final KeccakSponge shake128;

    private final KeccakSponge shake256;

    public static MLKEMHash create(ParameterSet parameterSet) {

        MessageDigest sha3Hash256;
        MessageDigest sha3Hash512;
        KeccakSponge shake128;
        KeccakSponge shake256;
        try {

            // Bootstrap SHA3-256
            sha3Hash256 = MessageDigest.getInstance("SHA3-256");

            // Bootstrap SHA3-512
            sha3Hash512 = MessageDigest.getInstance("SHA3-512");

            // Bootstrap SHAKE128
            shake128 = new KeccakSponge(XOFParameterSet.SHAKE128);

            // Bootstrap SHAKE256
            shake256 = new KeccakSponge(XOFParameterSet.SHAKE256);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        return new MLKEMHash(parameterSet, sha3Hash256, sha3Hash512, shake128, shake256);
    }

    @Override
    public byte[] prfEta1(byte[] s, byte b) {

        int eta = parameterSet.getEta1();

        // Init XOF
        BitOutputStream absorbStream = shake256.getAbsorbStream();
        BitInputStream squeezeStream = shake256.getSqueezeStream();

        // Absorb s and b
        absorbStream.write(s);
        absorbStream.write(new byte[] {b});

        // Squeeze the result
        byte[] digest = new byte[64 * eta];
        if (squeezeStream.read(digest) != digest.length) {
            throw new KeyPairGenerationException("PRF SHAKE256.Squeeze() operation failed");
        }

        return digest;

    }

    @Override
    public byte[] prfEta2(byte[] s, byte b) {

        int eta = parameterSet.getEta2();

        // Init XOF
        BitOutputStream absorbStream = shake256.getAbsorbStream();
        BitInputStream squeezeStream = shake256.getSqueezeStream();

        // Absorb s and b
        absorbStream.write(s);
        absorbStream.write(new byte[] {b});

        // Squeeze the result
        byte[] digest = new byte[64 * eta];
        if (squeezeStream.read(digest) != digest.length) {
            throw new KeyPairGenerationException("PRF SHAKE256.Squeeze() operation failed");
        }

        return digest;

    }

    @Override
    public byte[] gHash(byte[] c) {

        return sha3Hash512.digest(c);

    }

    @Override
    public byte[] hHash(byte[] s) {

        return sha3Hash256.digest(s);

    }

    @Override
    public byte[] jHash(byte[] s) {

        // Init XOF
        BitOutputStream absorbStream = shake256.getAbsorbStream();
        BitInputStream squeezeStream = shake256.getSqueezeStream();

        // Absorb s
        absorbStream.write(s);

        // Squeeze the result
        byte[] digest = new byte[32];
        if (squeezeStream.read(digest) != digest.length) {
            throw new KeyPairGenerationException("PRF SHAKE256.Squeeze() operation failed");
        }

        return digest;

    }
}
