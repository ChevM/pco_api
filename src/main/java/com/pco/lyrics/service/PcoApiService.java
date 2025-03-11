package com.pco.lyrics.service;

import com.pco.lyrics.model.*;

import java.util.List;

public interface PcoApiService {
    
    /**
     * Get all service types
     * @return List of service types
     */
    List<ServiceType> getServiceTypes();
    
    /**
     * Get plans for a specific service type
     * @param serviceTypeId The service type ID
     * @return List of plans
     */
    List<Plan> getPlans(String serviceTypeId);
    
    /**
     * Get songs with lyrics for a specific plan
     * @param planId The plan ID
     * @return List of songs with lyrics
     */
    List<SongWithLyrics> getSongsWithLyrics(String planId);
} 