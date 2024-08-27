package com.mimiclone.fips203.encaps.mlkem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mimiclone.fips203.ParameterSet;
import com.mimiclone.fips203.encaps.Encapsulation;
import com.mimiclone.fips203.key.KeyPair;
import com.mimiclone.fips203.key.gen.mlkem.MLKEMKeyGeneratorTests;
import com.mimiclone.fips203.key.gen.mlkem.MLKEMKeyPairGenerator;
import com.mimiclone.fips203.key.mlkem.MLKEMEncapsulationKey;
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

public class MLKEMEncapsulatorTests {

    private TestPrompt prompt;

    private TestPrompt loadTestPrompt(String fromResource) throws IOException {

        // Create an ObjectMapper instance
        var objectMapper = new ObjectMapper();

        // Load the JSON test prompts
        try (InputStream inputStream = MLKEMEncapsulatorTests.class.getResourceAsStream(fromResource)) {
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
        MLKEMEncapsulator mlKemEncapsulator = MLKEMEncapsulator.create(params);

        // Print header
        System.out.printf("%n[Test Case %d] using %s Parameter Set:%n", testCase.getTcId(), params.getName());

        // Inputs
        byte[] inputEK = HexFormat.of().parseHex((String) testCase.getValues().get("ek"));
        byte[] inputM = HexFormat.of().parseHex((String) testCase.getValues().get("m"));

        // Outputs
        byte[] expectedC = HexFormat.of().parseHex((String) testCase.getValues().get("c"));
        byte[] expectedK = HexFormat.of().parseHex((String) testCase.getValues().get("k"));

        // Generate the encapsulation
        Encapsulation encapsulation = mlKemEncapsulator.encapsulate(MLKEMEncapsulationKey.create(inputEK), inputM);

        assertNotNull(encapsulation);
        assertNotNull(encapsulation.getCipherText());
        assertNotNull(encapsulation.getSharedSecretKey());

        // Extract the shared secret
        byte[] sharedSecret = encapsulation.getSharedSecretKey().getBytes();

        // Verify it is the expected length
        assertEquals(params.getSharedSecretKeyLength(), sharedSecret.length);

        // Extract the cipherText
        byte[] cipherText = encapsulation.getCipherText().getBytes();

        // Verify it is the expected length
        assertEquals(params.getCiphertextLength(), cipherText.length);

        // Iterate through each byte and validate they are the same
        System.out.printf(" -- Shared Secret Key%n");
        System.out.printf("   --> Expect: %s%n", HexFormat.of().formatHex(expectedK));
        System.out.printf("   --> Actual: %s%n", HexFormat.of().formatHex(sharedSecret));
        for (int i = 0; i < params.getSharedSecretKeyLength(); i++) {
            assertEquals(expectedK[i], sharedSecret[i]);
        }

        // Iterate through each byte and validate they are the same
        System.out.printf(" -- CipherText%n");
        System.out.printf("   --> Expect: %s%n", HexFormat.of().formatHex(expectedC));
        System.out.printf("   --> Actual: %s%n", HexFormat.of().formatHex(cipherText));
        for (int i = 0; i < params.getCiphertextLength(); i++) {
            assertEquals(expectedC[i], cipherText[i]);
        }
    }

    @Test
    public void mlKem512EncapsTest() {

        ParameterSet params = ParameterSet.ML_KEM_512;

        for (TestGroup testGroup: prompt.getTestGroups()) {
            if (Objects.equals(testGroup.getParameterSet(), params.getName())
                && Objects.equals(testGroup.getFunction(), "encapsulation")
            ) {
                System.out.printf("Group %d using %s Parameter Set:%n", testGroup.getTgId(), params.getName());
                for (TestCase testCase : testGroup.getTests()) {
                    execTestCase(params, testCase);
                }
            }
        }

    }

    @Test
    public void mlKem768EncapsTest() {

        ParameterSet params = ParameterSet.ML_KEM_768;

        for (TestGroup testGroup: prompt.getTestGroups()) {
            if (Objects.equals(testGroup.getParameterSet(), params.getName())
                    && Objects.equals(testGroup.getFunction(), "encapsulation")
            ) {
                System.out.printf("Group %d using %s Parameter Set:%n", testGroup.getTgId(), params.getName());
                for (TestCase testCase : testGroup.getTests()) {
                    execTestCase(params, testCase);
                }
            }
        }

    }

    @Test
    public void mlKem1024EncapsTest() {

        ParameterSet params = ParameterSet.ML_KEM_1024;

        for (TestGroup testGroup: prompt.getTestGroups()) {
            if (Objects.equals(testGroup.getParameterSet(), params.getName())
                    && Objects.equals(testGroup.getFunction(), "encapsulation")
            ) {
                System.out.printf("Group %d using %s Parameter Set:%n", testGroup.getTgId(), params.getName());
                for (TestCase testCase : testGroup.getTests()) {
                    execTestCase(params, testCase);
                }
            }
        }

    }

}
