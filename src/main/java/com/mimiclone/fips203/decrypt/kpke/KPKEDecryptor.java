package com.mimiclone.fips203.decrypt.kpke;

import com.mimiclone.fips203.ParameterSet;
import com.mimiclone.fips203.codec.Codec;
import com.mimiclone.fips203.codec.MLKEMCodec;
import com.mimiclone.fips203.decrypt.Decryptor;
import com.mimiclone.fips203.transforms.MLKEMNumberTheoreticTransform;
import com.mimiclone.fips203.transforms.NumberTheoreticTransform;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.nio.ByteBuffer;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class KPKEDecryptor implements Decryptor {

    private final ParameterSet parameterSet;
    private final Codec codec;
    private final NumberTheoreticTransform ntt;

    public static KPKEDecryptor create(ParameterSet parameterSet) {
        return new KPKEDecryptor(
                parameterSet,
                MLKEMCodec.create(parameterSet),
                MLKEMNumberTheoreticTransform.create(parameterSet)
        );
    }

    @Override
    public byte[] decrypt(byte[] dkPKE, byte[] cipherText) {

        // Wrap cipherText in a buffer
        ByteBuffer cipherTextBuffer = ByteBuffer.wrap(cipherText);

        // ALGO 1: Extract c1
        byte[] c1 = new byte[32 * parameterSet.getDu() * parameterSet.getK()];
        cipherTextBuffer.get(c1);

        // ALGO 2: Extract c2
        byte[] c2 = new byte[32 * parameterSet.getDv()];
        cipherTextBuffer.get(c2);

        // Setup up processing buffer c1
        ByteBuffer c1ChunkBuffer = ByteBuffer.wrap(c1);

        // ALGO 3: Calculate uPrime
        int[][] uPrime = new int[parameterSet.getK()][];
        for (int i = 0; i < parameterSet.getK(); i++) {
            byte[] c1Chunk = new byte[32 * parameterSet.getDu()];
            c1ChunkBuffer.get(c1Chunk);
            uPrime[i] = codec.decompress(
                    parameterSet.getDu(),
                    codec.byteDecode(
                            parameterSet.getDu(),
                            c1Chunk.clone()
                    )
            );
        }

        // ALGO 4: Calculate vPrime
        int[] vPrime = codec.decompress(
                parameterSet.getDv(),
                codec.byteDecode(
                        parameterSet.getDv(),
                        c2.clone()
                )
        );

        // Wrap dkPKE in a buffer for chunking
        ByteBuffer dkPKEChunkBuffer = ByteBuffer.wrap(dkPKE);
        byte[] dkPKEChunk = new byte[384];

        // ALGO 5: Calculate sHat
        int[][] sHat = new int[parameterSet.getK()][];
        for (int i = 0; i < parameterSet.getK(); i++) {
            dkPKEChunkBuffer.get(dkPKEChunk);
            sHat[i] = codec.byteDecode(12, dkPKEChunk.clone());
        }

        // ALGO 6: Calculate w
        int[][] uPrimeNTT = new int[parameterSet.getK()][];
        for (int i = 0; i < parameterSet.getK(); i++) {
            uPrimeNTT[i] = ntt.transform(uPrime[i]);
        }
        int[] w = ntt.arraySubtract(vPrime, ntt.inverse(ntt.vectorTransposeMultiply(sHat, uPrimeNTT)));

        // ALGO 7&8: Compress, encode and return plaintext
        return codec.byteEncode(1, codec.compress(1, w));
    }
}
