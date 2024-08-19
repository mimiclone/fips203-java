package com.mimiclone.fips203.transforms;

import java.math.BigInteger;

public interface NumberTheoretic {

    /**
     * Performs a number theoretic transform of an array of 256 integer in modulo q
     *
     * @param input An array of 256 integers in modulo q
     * @return An array of 256 integers in modulo q transformed using the NTT algorithm
     */
    BigInteger[] transform(BigInteger[] input);

    /**
     * Performs the inverse of a number theoretic transform of an array of 256 integers in modulo q
     *
     * @param input An array of 256 integers in modulo q representing a number theoretic transform
     * @return An array of 256 integers in modulo q with the transform reversed
     */
    BigInteger[] inverse(BigInteger[] input);

}