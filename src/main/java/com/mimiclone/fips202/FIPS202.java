package com.mimiclone.fips202;

import com.mimiclone.fips202.keccak.Keccak;

public interface FIPS202 {

    Keccak keccakPermutation(Keccak.Permutation permutation);

}
