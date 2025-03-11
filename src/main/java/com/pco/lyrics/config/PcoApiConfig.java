package com.pco.lyrics.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Base64;

@Configuration
public class PcoApiConfig {

    @Value("${pco.api.base-url}")
    private String baseUrl;

    @Value("${pco.api.app-id}")
    private String appId;

    @Value("${pco.api.secret}")
    private String secret;

    @Bean
    public WebClient pcoWebClient() {
        // Always use Basic authentication for PCO API
        // Even though the secret starts with "pco_pat_", PCO API requires Basic auth
        String auth = appId + ":" + secret;
        String authHeader = "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());

        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, authHeader)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
} 