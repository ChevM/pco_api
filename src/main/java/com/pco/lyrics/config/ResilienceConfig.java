package com.pco.lyrics.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;

/**
 * Configuration for resilience patterns (retry, circuit breaker) for external API calls.
 */
@Configuration
public class ResilienceConfig {

    /**
     * Configure retry for PCO API calls.
     * 
     * @return RetryRegistry with configured retry settings
     */
    @Bean
    public RetryRegistry retryRegistry() {
        RetryConfig retryConfig = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofMillis(500))
                .retryExceptions(Exception.class)
                .ignoreExceptions(WebClientResponseException.NotFound.class)
                .build();
        
        RetryRegistry retryRegistry = RetryRegistry.of(RetryConfig.ofDefaults());
        retryRegistry.addConfiguration("pcoApi", retryConfig);
        
        return retryRegistry;
    }
    
    /**
     * Configure circuit breaker for PCO API calls.
     * 
     * @return CircuitBreakerConfig with configured settings
     */
    @Bean
    public CircuitBreakerConfig circuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofMillis(1000))
                .permittedNumberOfCallsInHalfOpenState(2)
                .slidingWindowSize(10)
                .build();
    }
} 