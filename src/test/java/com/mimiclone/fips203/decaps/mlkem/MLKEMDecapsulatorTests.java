package com.mimiclone.fips203.decaps.mlkem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mimiclone.fips203.ParameterSet;
import com.mimiclone.fips203.decaps.Decapsulator;
import com.mimiclone.fips203.key.DecapsulationKey;
import com.mimiclone.fips203.key.SharedSecretKey;
import com.mimiclone.fips203.key.mlkem.MLKEMDecapsulationKey;
import com.mimiclone.fips203.message.CipherText;
import com.mimiclone.fips203.message.MLKEMCipherText;
import com.mimiclone.harness.TestCase;
import com.mimiclone.harness.TestGroup;
import com.mimiclone.harness.TestPrompt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.HexFormat;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MLKEMDecapsulatorTests {

    private TestPrompt prompt;

    private TestPrompt loadTestPrompt(String fromResource) throws IOException {

        // Create an ObjectMapper instance
        var objectMapper = new ObjectMapper();

        // Load the JSON test prompts
        try (InputStream inputStream = MLKEMDecapsulatorTests.class.getResourceAsStream(fromResource)) {
            if (inputStream == null) {
                throw new IOException("Could not find " + fromResource);
            }

            // Deserialize JSON into POJO
            return objectMapper.readValue(inputStream, TestPrompt.class);
        }
    }

    @BeforeEach
    public void setUpTest() throws IOException {

        prompt = loadTestPrompt("internalProjection.json");

    }

    private void execTestCase(ParameterSet params, TestCase testCase) {

        // Create keygen under test
        Decapsulator mlKemDecapsulator = MLKEMDecapsulator.create(params);

        // Print header
        System.out.printf("%n[Test Case %d] using %s Parameter Set:%n", testCase.getTcId(), params.getName());

        // Input Bytes
        byte[] inputDK = HexFormat.of().parseHex((String) testCase.getValues().get("dk"));
        byte[] inputC = HexFormat.of().parseHex((String) testCase.getValues().get("c"));

        // Output Bytes
        byte[] expectedK = HexFormat.of().parseHex((String) testCase.getValues().get("k"));

        // Wrap raw inputs
        DecapsulationKey decapsulationKey = MLKEMDecapsulationKey.create(inputDK);
        CipherText cipherText = MLKEMCipherText.create(inputC);

        // Generate the encapsulation
        SharedSecretKey sharedSecretKey = mlKemDecapsulator.decapsulate(decapsulationKey, cipherText);

        assertNotNull(sharedSecretKey);
        assertNotNull(sharedSecretKey.getBytes());

        // Extract the shared secret
        byte[] sharedSecret = sharedSecretKey.getBytes();

        // Verify it is the expected length
        assertEquals(params.getSharedSecretKeyLength(), sharedSecret.length);

        // Iterate through each byte and validate they are the same
        System.out.printf(" -- Shared Secret Key%n");
        System.out.printf("   --> Expect: %s%n", HexFormat.of().formatHex(expectedK));
        System.out.printf("   --> Actual: %s%n", HexFormat.of().formatHex(sharedSecret));
        for (int i = 0; i < params.getSharedSecretKeyLength(); i++) {
            assertEquals(expectedK[i], sharedSecret[i]);
        }
    }

    @Test
    public void mlKem512DecapsTest() {

        ParameterSet params = ParameterSet.ML_KEM_512;

        for (TestGroup testGroup: prompt.getTestGroups()) {
            if (Objects.equals(testGroup.getParameterSet(), params.getName())
                && Objects.equals(testGroup.getFunction(), "decapsulation")
            ) {
                System.out.printf("Group %d using %s Parameter Set:%n", testGroup.getTgId(), params.getName());
                for (TestCase testCase : testGroup.getTests()) {

                    // Load shared ek and dk from group level
                    testCase.getValues().put("ek", testGroup.getEk());
                    testCase.getValues().put("dk", testGroup.getDk());

                    // Execute test case
                    execTestCase(params, testCase);

                }
            }
        }

    }

    @Test
    public void mlKem768DecapsTest() {

        ParameterSet params = ParameterSet.ML_KEM_768;

        for (TestGroup testGroup: prompt.getTestGroups()) {
            if (Objects.equals(testGroup.getParameterSet(), params.getName())
                    && Objects.equals(testGroup.getFunction(), "decapsulation")
            ) {
                System.out.printf("Group %d using %s Parameter Set:%n", testGroup.getTgId(), params.getName());
                for (TestCase testCase : testGroup.getTests()) {

                    // Load shared ek and dk from group level
                    testCase.getValues().put("ek", testGroup.getEk());
                    testCase.getValues().put("dk", testGroup.getDk());

                    // Execute test case
                    execTestCase(params, testCase);

                }
            }
        }

    }

    @Test
    public void mlKem1024DecapsTest() {

        ParameterSet params = ParameterSet.ML_KEM_1024;

        for (TestGroup testGroup: prompt.getTestGroups()) {
            if (Objects.equals(testGroup.getParameterSet(), params.getName())
                    && Objects.equals(testGroup.getFunction(), "decapsulation")
            ) {
                System.out.printf("Group %d using %s Parameter Set:%n", testGroup.getTgId(), params.getName());
                for (TestCase testCase : testGroup.getTests()) {

                    // Load shared ek and dk from group level
                    testCase.getValues().put("ek", testGroup.getEk());
                    testCase.getValues().put("dk", testGroup.getDk());

                    // Execute test case
                    execTestCase(params, testCase);

                }
            }
        }

    }

}
