package com.mimiclone.fips203.reduce.barrett;

import com.mimiclone.fips203.ParameterSet;
import com.mimiclone.fips203.reduce.Reducer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BarrettReducerTests {
    
    @Test
    public void testModulusMultiplication() {

        Reducer barrettReducer = BarrettReducer.create(ParameterSet.ML_KEM_1024);

        long min = -1L;
        long max = -1L;

        for (int a = 0; a < 3329; a++) {
            for (int b = 0; b < 3329; b++) {

                int mul = a * b;
                int knownValue = mul % 3329;

                long start = System.nanoTime();
                int barretValue = barrettReducer.reduce(mul);
                long stop = System.nanoTime();
                long time = stop - start;

                if (min == -1L || time < min) { min = time; }
                if (max == -1L || time > max) { max = time; }

                assertEquals(knownValue, barretValue);

            }
        }

        // Ensure we captured min and max values
        assertTrue(min > -1);
        assertTrue(max > min);

        // Calculate the spread between min and max
        long spread = max - min;
        System.out.printf("Metrics: min=%d, max=%d, spread=%d\n", min, max, spread);

        // Ensure the variance in execution times is less than 50 microseconds
        assertTrue(spread < 50000);
        
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
