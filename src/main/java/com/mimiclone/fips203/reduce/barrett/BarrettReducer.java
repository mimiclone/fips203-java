package com.mimiclone.fips203.reduce.barrett;

import com.mimiclone.fips203.ParameterSet;
import com.mimiclone.fips203.reduce.Reducer;
import com.mimiclone.fips203.reduce.ReductionException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class BarrettReducer implements Reducer {

    private final ParameterSet parameterSet;
    private final int modulus;
    private final int multiplier;
    private final long shift = 32;

    private static int calculateMultiplier(int modulus) {
        return (int) ((1L << 32) / modulus);
    }

    public static BarrettReducer create(ParameterSet parameterSet) {
        return new BarrettReducer(
                parameterSet,
                parameterSet.getQ(),
                calculateMultiplier(parameterSet.getQ())
        );
    }

    public static BarrettReducer create(ParameterSet parameterSet, int modulus) {
        return new BarrettReducer(
                parameterSet,
                modulus,
                calculateMultiplier(modulus)
        );
    }

    @Override
    public int reduce(int a) throws ReductionException {

        // Estimate the quotient
        int quotient = (int) (((long) a * multiplier) >> shift);

        // Calculate the remainder
        int remainder = a - (quotient * modulus);

        // Final correction
        int result;
        int correctedResult = remainder - modulus;
        if (remainder >= modulus) {
            result = correctedResult;
        } else {
            result = remainder;
        }

        return result;

    }
}