package com.mimiclone.fips203.decaps;

import com.mimiclone.fips203.key.DecapsulationKey;
import com.mimiclone.fips203.key.SharedSecretKey;
import com.mimiclone.fips203.message.CipherText;

public interface Decapsulator {

    SharedSecretKey decapsulate(DecapsulationKey key, CipherText cipherText) throws DecapsulationException;

}
