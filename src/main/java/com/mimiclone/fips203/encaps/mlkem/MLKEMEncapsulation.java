package com.mimiclone.fips203.encaps.mlkem;

import com.mimiclone.fips203.encaps.Encapsulation;
import com.mimiclone.fips203.key.SharedSecretKey;
import com.mimiclone.fips203.key.mlkem.MLKEMSharedSecretKey;
import com.mimiclone.fips203.message.CipherText;
import com.mimiclone.fips203.message.MLKEMCipherText;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MLKEMEncapsulation implements Encapsulation {

    private final SharedSecretKey sharedSecretKey;
    private final CipherText cipherText;

    static MLKEMEncapsulation build(byte[] sharedSecretKeyBytes, byte[] cipherTextBytes) {
        SharedSecretKey secretKey = MLKEMSharedSecretKey.create(sharedSecretKeyBytes);
        CipherText cipherText = MLKEMCipherText.create(cipherTextBytes);
        return new MLKEMEncapsulation(secretKey, cipherText);
    }

    @Override
    public SharedSecretKey getSharedSecretKey() {
        return sharedSecretKey;
    }

    @Override
    public CipherText getCipherText() {
        return cipherText;
    }
}
