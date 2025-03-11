package com.pco.lyrics;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

/**
 * A simple standalone test that directly accesses the PCO API without using Spring Boot.
 * This helps verify that the credentials are correct and working.
 */
public class DirectPcoApiTest {

    private static final String BASE_URL = "https://api.planningcenteronline.com";
    private static final String APP_ID = "YOUR_APP_ID_HERE";
    private static final String SECRET = "YOUR_SECRET_HERE";

    @Test
    void testDirectApiAccessWithBasicAuth() throws Exception {
        System.out.println("Testing direct API access with Basic Authentication");
        
        // Create URL for service types endpoint
        URL url = new URL(BASE_URL + "/services/v2/service_types");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        // Set request method
        connection.setRequestMethod("GET");
        
        // Set headers
        connection.setRequestProperty("Accept", "application/json");
        
        // Set Basic authentication header
        String auth = APP_ID + ":" + SECRET;
        String authHeader = "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());
        connection.setRequestProperty("Authorization", authHeader);
        
        // Print request details
        System.out.println("Request: GET " + url);
        System.out.println("Authorization: Basic " + Base64.getEncoder().encodeToString(auth.getBytes()).substring(0, 10) + "...");
        
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
    
    @Test
    void testDirectApiAccessWithBearerToken() throws Exception {
        System.out.println("Testing direct API access with Bearer Token");
        
        // Create URL for service types endpoint
        URL url = new URL(BASE_URL + "/services/v2/service_types");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        // Set request method
        connection.setRequestMethod("GET");
        
        // Set headers
        connection.setRequestProperty("Accept", "application/json");
        
        // Set Bearer token authentication header
        String authHeader = "Bearer " + SECRET;
        connection.setRequestProperty("Authorization", authHeader);
        
        // Print request details
        System.out.println("Request: GET " + url);
        System.out.println("Authorization: Bearer " + SECRET.substring(0, 10) + "...");
        
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