package com.mimiclone.fips203.encaps;

import com.mimiclone.fips203.key.SharedSecretKey;
import com.mimiclone.fips203.message.CipherText;

public interface Encapsulation {

    SharedSecretKey getSharedSecretKey();

    CipherText getCipherText();

}
