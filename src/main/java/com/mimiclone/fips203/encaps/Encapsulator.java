package com.mimiclone.fips203.encaps;

import com.mimiclone.fips203.key.EncapsulationKey;
import com.mimiclone.fips203.key.SharedSecretKey;

public interface Encapsulator {

    SharedSecretKey encapsulate(EncapsulationKey ek) throws EncapsulationException;

}
