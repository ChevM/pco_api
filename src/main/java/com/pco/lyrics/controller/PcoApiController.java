package com.pco.lyrics.controller;

import com.pco.lyrics.model.Plan;
import com.pco.lyrics.model.ServiceType;
import com.pco.lyrics.model.SongWithLyrics;
import com.pco.lyrics.service.PcoApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for PCO API endpoints.
 */
@RestController
@RequestMapping("/api/pco")
@RequiredArgsConstructor
@Slf4j
public class PcoApiController {

    private final PcoApiService pcoApiService;

    /**
     * Get all service types.
     *
     * @return List of service types
     */
    @GetMapping("/service-types")
    public ResponseEntity<List<ServiceType>> getServiceTypes() {
        log.debug("REST request to get all service types");
        return ResponseEntity.ok(pcoApiService.getServiceTypes());
    }

    /**
     * Get plans for a specific service type.
     *
     * @param serviceTypeId The service type ID
     * @return List of plans
     */
    @GetMapping("/service-types/{serviceTypeId}/plans")
    public ResponseEntity<List<Plan>> getPlans(@PathVariable String serviceTypeId) {
        log.debug("REST request to get plans for service type: {}", serviceTypeId);
        return ResponseEntity.ok(pcoApiService.getPlans(serviceTypeId));
    }

    /**
     * Get songs with lyrics for a specific plan.
     *
     * @param planId The plan ID
     * @return List of songs with lyrics
     */
    @GetMapping("/plans/{planId}/songs")
    public ResponseEntity<List<SongWithLyrics>> getSongsWithLyrics(@PathVariable String planId) {
        log.debug("REST request to get songs with lyrics for plan: {}", planId);
        return ResponseEntity.ok(pcoApiService.getSongsWithLyrics(planId));
    }
} 