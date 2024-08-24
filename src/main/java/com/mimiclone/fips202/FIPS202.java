package com.mimiclone.fips202;

import com.mimiclone.fips202.keccak.MimicloneKeccak;

public interface FIPS202 {

    MimicloneKeccak keccakPermutation(MimicloneKeccak.Permutation permutation);

}
