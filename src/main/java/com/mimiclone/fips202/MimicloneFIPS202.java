package com.mimiclone.fips202;

import com.mimiclone.fips202.keccak.Keccak;

public class MimicloneFIPS202 implements FIPS202 {

    @Override
    public Keccak keccakPermutation(Keccak.Permutation permutation) {

        return new Keccak(permutation);

    }

}
