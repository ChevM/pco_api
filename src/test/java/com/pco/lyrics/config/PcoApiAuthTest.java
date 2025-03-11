package com.pco.lyrics.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

@SpringBootTest
@ActiveProfiles("test")
public class PcoApiAuthTest {

    @Value("${pco.api.base-url}")
    private String baseUrl;

    @Value("${pco.api.app-id}")
    private String appId;

    @Value("${pco.api.secret}")
    private String secret;

    @Test
    void testAuthenticationWithBasicAuth() throws Exception {
        System.out.println("Base URL: " + baseUrl);
        System.out.println("App ID: " + appId);
        System.out.println("Secret: " + (secret != null ? secret.substring(0, 10) + "..." : "null"));
        
        // Create URL
        URL url = new URL(baseUrl + "/services/v2/service_types");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        // Set request method
        connection.setRequestMethod("GET");
        
        // Set headers
        connection.setRequestProperty("Accept", "application/json");
        
        // Set authentication header - always use Basic auth
        String auth = appId + ":" + secret;
        String authHeader = "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());
        System.out.println("Using Basic authentication");
        connection.setRequestProperty("Authorization", authHeader);
        
        // Print request details
        System.out.println("Request: GET " + url);
        System.out.println("Authorization: " + authHeader.substring(0, 10) + "...");
        
        // Get response code
        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode);
        
        // Read response
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            
            // Print response (truncated for readability)
            String responseStr = response.toString();
            System.out.println("Response: " + (responseStr.length() > 100 ? 
                                              responseStr.substring(0, 100) + "..." : 
                                              responseStr));
        } else {
            System.out.println("Error response: " + connection.getResponseMessage());
            
            // Try to read error stream
            try (BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(connection.getErrorStream()))) {
                String errorLine;
                StringBuilder errorResponse = new StringBuilder();
                
                while ((errorLine = errorReader.readLine()) != null) {
                    errorResponse.append(errorLine);
                }
                
                System.out.println("Error details: " + errorResponse.toString());
            } catch (Exception e) {
                System.out.println("Could not read error stream: " + e.getMessage());
            }
        }
    }
} 