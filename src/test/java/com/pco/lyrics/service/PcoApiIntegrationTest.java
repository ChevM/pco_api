package com.pco.lyrics.service;

import com.pco.lyrics.model.Plan;
import com.pco.lyrics.model.ServiceType;
import com.pco.lyrics.model.SongWithLyrics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class PcoApiIntegrationTest {

    @Autowired
    private PcoApiService pcoApiService;

    @Test
    void getServiceTypes_shouldReturnServiceTypes() {
        // Act
        List<ServiceType> serviceTypes = pcoApiService.getServiceTypes();

        // Assert
        assertNotNull(serviceTypes);
        assertFalse(serviceTypes.isEmpty());
        
        // Print details for debugging
        System.out.println("Service Types:");
        serviceTypes.forEach(serviceType -> {
            System.out.println("ID: " + serviceType.getId() + 
                              ", Name: " + serviceType.getAttributes().getName());
        });
    }

    @Test
    void getPlans_shouldReturnPlans() {
        // First get service types to find a valid ID
        List<ServiceType> serviceTypes = pcoApiService.getServiceTypes();
        assertFalse(serviceTypes.isEmpty());
        
        String serviceTypeId = serviceTypes.get(0).getId();
        
        // Act
        List<Plan> plans = pcoApiService.getPlans(serviceTypeId);
        
        // Assert
        assertNotNull(plans);
        // Note: It's possible there might not be any plans for this service type
        
        // Print details for debugging
        System.out.println("Plans for Service Type " + serviceTypeId + ":");
        plans.forEach(plan -> {
            System.out.println("ID: " + plan.getId() + 
                              ", Title: " + plan.getAttributes().getTitle() + 
                              ", Dates: " + plan.getAttributes().getDates());
        });
    }

    @Test
    void getSongsWithLyrics_shouldReturnSongsWithLyrics() {
        // First get service types
        List<ServiceType> serviceTypes = pcoApiService.getServiceTypes();
        assertFalse(serviceTypes.isEmpty());
        
        String serviceTypeId = serviceTypes.get(0).getId();
        
        // Then get plans
        List<Plan> plans = pcoApiService.getPlans(serviceTypeId);
        
        // If there are no plans, we can't test further
        if (plans.isEmpty()) {
            System.out.println("No plans found for service type " + serviceTypeId);
            return;
        }
        
        String planId = plans.get(0).getId();
        
        // Act
        List<SongWithLyrics> songs = pcoApiService.getSongsWithLyrics(planId);
        
        // Assert
        assertNotNull(songs);
        // Note: It's possible there might not be any songs for this plan
        
        // Print details for debugging
        System.out.println("Songs for Plan " + planId + ":");
        songs.forEach(song -> {
            System.out.println("Title: " + song.getTitle() + 
                              ", Author: " + song.getAuthor() + 
                              ", Key: " + song.getKey());
            System.out.println("Lyrics (first 100 chars): " + 
                              (song.getLyrics() != null && song.getLyrics().length() > 100 ? 
                               song.getLyrics().substring(0, 100) + "..." : 
                               song.getLyrics()));
        });
    }
} 