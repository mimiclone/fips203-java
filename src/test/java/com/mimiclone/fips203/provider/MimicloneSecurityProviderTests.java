package com.mimiclone.fips203.provider;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.KeyPairGenerator;
import java.security.Provider;
import java.security.Security;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MimicloneSecurityProviderTests {

    @BeforeEach
    public void setUp() {
        new MimicloneSecurityProvider().install();
    }

    @Test
    public void testSecurityProviders() {

        Provider mimicloneProvider = Security.getProvider(MimicloneSecurityProvider.PROVIDER_NAME);
        assertNotNull(mimicloneProvider);

        for (Provider provider : Security.getProviders()) {
            System.out.printf("Security Provider: [%s]:%n", provider.getName());
            for (Provider.Service service : provider.getServices()) {
               System.out.printf(" --> [%s] Service [%s]%n", service.getType(), service.getAlgorithm());
            }
        }

    }

}
