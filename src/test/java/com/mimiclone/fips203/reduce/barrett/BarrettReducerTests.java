package com.mimiclone.fips203.reduce.barrett;

import com.mimiclone.fips203.ParameterSet;
import com.mimiclone.fips203.reduce.Reducer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BarrettReducerTests {
    
    @Test
    public void testModulusMultiplication() {

        Reducer barrettReducer = BarrettReducer.create(ParameterSet.ML_KEM_1024);

        for (int a = 0; a < 3329; a++) {
            for (int b = 0; b < 3329; b++) {

                int mul = a * b;
                int knownValue = mul % 3329;
                int barretValue = barrettReducer.reduce(mul);

                assertEquals(knownValue, barretValue);

            }
        }
        
    }

    @Test
    public void testModulusAddition() {

        Reducer barrettReducer = BarrettReducer.create(ParameterSet.ML_KEM_1024);

        for (int a = 0; a < 3329; a++) {
            for (int b = 0; b < 3329; b++) {

                int mul = a + b;
                int knownValue = mul % 3329;
                int barretValue = barrettReducer.reduce(mul);

                assertEquals(knownValue, barretValue);

            }
        }

    }

    @Test
    public void testModulusSubtraction() {

        Reducer barrettReducer = BarrettReducer.create(ParameterSet.ML_KEM_1024);

        for (int a = 0; a < 3329; a++) {
            for (int b = 0; b < 3329; b++) {

                int mul = a - b;
                int knownValue = (mul + 3329) % 3329;
                int barretValue = barrettReducer.reduce(mul);

                assertEquals(knownValue, barretValue);

            }
        }

    }
    
}
