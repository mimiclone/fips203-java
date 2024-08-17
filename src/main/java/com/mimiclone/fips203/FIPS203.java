package com.mimiclone.fips203;

import com.mimiclone.fips203.key.*;
import com.mimiclone.fips203.key.check.KeyPairCheckException;

public interface FIPS203 {

    /**
     * Convenience method to verify the ParameterSet of the underlyig implementation.
     */
    ParameterSet getParameterSet();

    /**
     * Implementation of the KeyGen algorithm as specified in the FIPS203 Specification
     */
    FIPS203KeyPair generateKeyPair();

    void keyPairCheck(FIPS203KeyPair keyPair) throws KeyPairCheckException;

    /**
     * Implementation of the Encaps algorithm as specified in the FIPS203 Specification
     * @return An array of exactly 32 bytes representing the encapsulated cyphertext
     */
    byte[] encapsulateBlock(EncapsulationKey key, byte[] clearText);

    /**
     * Implementation of the Decaps algorithm as specified in the FIPS203 Specification
     * @return An array of exactly 32 bytes representing the decapsulated cleartext.
     */
    byte[] decapsulateBlock(DecapsulationKey key, byte[] cypherText);

}
