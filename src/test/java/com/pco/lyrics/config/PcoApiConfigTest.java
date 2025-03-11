package com.pco.lyrics.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PcoApiConfigTest {

    @InjectMocks
    private PcoApiConfig pcoApiConfig;

    @Test
    void pcoWebClient_withAppIdAndSecret_shouldCreateWebClient() {
        // Arrange
        ReflectionTestUtils.setField(pcoApiConfig, "baseUrl", "https://api.planningcenteronline.com");
        ReflectionTestUtils.setField(pcoApiConfig, "appId", "test-app-id");
        ReflectionTestUtils.setField(pcoApiConfig, "secret", "test-secret");

        // Act
        WebClient webClient = pcoApiConfig.pcoWebClient();

        // Assert
        assertNotNull(webClient);
    }

    @Test
    void pcoWebClient_withPersonalAccessToken_shouldCreateWebClient() {
        // Arrange
        ReflectionTestUtils.setField(pcoApiConfig, "baseUrl", "https://api.planningcenteronline.com");
        ReflectionTestUtils.setField(pcoApiConfig, "appId", "test-app-id");
        ReflectionTestUtils.setField(pcoApiConfig, "secret", "pco_pat_test-token");

        // Act
        WebClient webClient = pcoApiConfig.pcoWebClient();

        // Assert
        assertNotNull(webClient);
    }

    @Test
    void pcoWebClient_withActualCredentials_shouldCreateWebClient() {
        // Arrange
        ReflectionTestUtils.setField(pcoApiConfig, "baseUrl", "https://api.planningcenteronline.com");
        ReflectionTestUtils.setField(pcoApiConfig, "appId", "YOUR_APP_ID_HERE");
        ReflectionTestUtils.setField(pcoApiConfig, "secret", "YOUR_SECRET_HERE");

        // Act
        WebClient webClient = pcoApiConfig.pcoWebClient();

        // Assert
        assertNotNull(webClient);

        // Note: This test will fail without valid credentials
        // To run this test, replace the placeholders with actual credentials
        // but do not commit them to version control
    }
} 