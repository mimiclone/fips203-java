package com.mimiclone.fips203;

import com.mimiclone.fips203.key.*;
import com.mimiclone.fips203.key.check.KeyPairCheckException;
import com.mimiclone.fips203.key.gen.KeyPairGenerationException;
import com.mimiclone.fips203.message.CipherText;
import com.mimiclone.fips203.key.SharedSecretKey;

public interface FIPS203 {

    /**
     * Convenience method to verify the ParameterSet of the underlyig implementation.
     */
    ParameterSet getParameterSet();

    /**
     * Implementation of the KeyGen algorithm as specified in the FIPS203 Specification
     */
    FIPS203KeyPair generateKeyPair() throws KeyPairGenerationException;

    void keyPairCheck(FIPS203KeyPair keyPair) throws KeyPairCheckException;

    /**
     * Implementation of the Encaps algorithm as specified in the FIPS203 Specification
     * @return An array of exactly 32 bytes representing the encapsulated cyphertext
     */
    SharedSecretKey encapsulate(EncapsulationKey key);

    /**
     * Implementation of the Decaps algorithm as specified in the FIPS203 Specification
     * @return An array of exactly 32 bytes representing the decapsulated cleartext.
     */
    SharedSecretKey decapsulate(DecapsulationKey key, CipherText cipherText);

}
