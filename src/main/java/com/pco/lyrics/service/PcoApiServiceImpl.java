package com.pco.lyrics.service;

import com.pco.lyrics.model.*;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PcoApiServiceImpl implements PcoApiService {

    private final WebClient pcoWebClient;

    @Override
    @Retry(name = "pcoApi")
    public List<ServiceType> getServiceTypes() {
        log.debug("Getting service types");
        
        ServiceTypesResponse response = pcoWebClient.get()
                .uri("/services/v2/service_types")
                .retrieve()
                .bodyToMono(ServiceTypesResponse.class)
                .block();
        
        return response != null ? response.getData() : List.of();
    }

    @Override
    @Retry(name = "pcoApi")
    public List<Plan> getPlans(String serviceTypeId) {
        log.debug("Getting plans for service type: {}", serviceTypeId);
        
        PlansResponse response = pcoWebClient.get()
                .uri("/services/v2/service_types/{serviceTypeId}/plans", serviceTypeId)
                .retrieve()
                .bodyToMono(PlansResponse.class)
                .block();
        
        return response != null ? response.getData() : List.of();
    }

    @Override
    @Retry(name = "pcoApi")
    public List<SongWithLyrics> getSongsWithLyrics(String planId) {
        log.debug("Getting songs with lyrics for plan: {}", planId);
        
        // First, we need to find the service type ID for this plan
        // We'll get all service types and then check each one for the plan
        log.debug("Finding service type for plan: {}", planId);
        List<ServiceType> serviceTypes = getServiceTypes();
        String serviceTypeId = null;
        
        // Find the service type that contains this plan
        for (ServiceType serviceType : serviceTypes) {
            List<Plan> plans = getPlans(serviceType.getId());
            for (Plan plan : plans) {
                if (plan.getId().equals(planId)) {
                    serviceTypeId = serviceType.getId();
                    log.debug("Found service type: {} for plan: {}", serviceTypeId, planId);
                    break;
                }
            }
            if (serviceTypeId != null) {
                break;
            }
        }
        
        if (serviceTypeId == null) {
            log.error("Could not find service type for plan: {}", planId);
            return List.of();
        }
        
        // Get items for the plan
        log.debug("Fetching items for plan: {} with service type: {}", planId, serviceTypeId);
        ItemsResponse itemsResponse = pcoWebClient.get()
                .uri("/services/v2/service_types/{serviceTypeId}/plans/{planId}/items?include=song", serviceTypeId, planId)
                .retrieve()
                .bodyToMono(ItemsResponse.class)
                .block();
        
        if (itemsResponse == null) {
            log.error("No items response received for plan: {}", planId);
            return List.of();
        }
        
        if (itemsResponse.getData() == null) {
            log.error("No data in items response for plan: {}", planId);
            return List.of();
        }
        
        log.debug("Received {} items for plan: {}", itemsResponse.getData().size(), planId);
        
        // Create a map of song IDs to included songs
        Map<String, Included> songMap = new HashMap<>();
        if (itemsResponse.getIncluded() != null) {
            for (Included included : itemsResponse.getIncluded()) {
                if ("Song".equals(included.getType())) {
                    songMap.put(included.getId(), included);
                    log.debug("Added song to map: {}", included.getAttributes().getTitle());
                }
            }
        } else {
            log.warn("No included data in items response for plan: {}", planId);
        }
        
        log.debug("Found {} songs in included data", songMap.size());
        
        List<SongWithLyrics> songs = new ArrayList<>();
        
        // Process each item
        for (Item item : itemsResponse.getData()) {
            // Skip non-song items
            if (!"song".equals(item.getAttributes().getItemType())) {
                continue;
            }
            
            log.debug("Processing song item: {}", item.getAttributes().getTitle());
            
            // Get the song ID
            if (item.getRelationships() == null || item.getRelationships().getSong() == null || 
                item.getRelationships().getSong().getData() == null) {
                log.warn("Item has no song relationship: {}", item.getId());
                continue;
            }
            
            String songId = item.getRelationships().getSong().getData().getId();
            log.debug("Song ID from relationship: {}", songId);
            
            // Get the song from the included data
            Included song = songMap.get(songId);
            if (song == null) {
                log.warn("Song not found in included data: {}", songId);
                continue;
            }
            
            log.debug("Found song in included data: {}", song.getAttributes().getTitle());
            
            // Get the arrangements directly
            log.debug("Fetching arrangements for song: {}", songId);
            ArrangementsResponse arrangementsResponse = pcoWebClient.get()
                    .uri("/services/v2/songs/{songId}/arrangements", songId)
                    .retrieve()
                    .bodyToMono(ArrangementsResponse.class)
                    .block();
            
            if (arrangementsResponse == null || arrangementsResponse.getData() == null || arrangementsResponse.getData().isEmpty()) {
                log.warn("No arrangements found for song: {}", songId);
                continue;
            }
            
            log.debug("Found {} arrangements for song: {}", arrangementsResponse.getData().size(), songId);
            
            // Get the first arrangement
            Arrangement arrangement = arrangementsResponse.getData().get(0);
            String arrangementId = arrangement.getId();
            log.debug("Using arrangement: {}", arrangementId);
            
            // Get the lyrics directly from the arrangement
            String lyrics = arrangement.getAttributes().getLyrics();
            log.debug("Lyrics length: {}", lyrics != null ? lyrics.length() : 0);
            
            if (lyrics == null || lyrics.isEmpty()) {
                log.warn("No lyrics found in arrangement: {}", arrangementId);
                continue;
            }
            
            // Format the lyrics
            String formattedLyrics = formatLyrics(lyrics);
            
            // Get the key name from the key relationship if available
            String keyName = item.getAttributes().getKey(); // Use the key from item attributes as a fallback
            
            if (item.getRelationships().getKey() != null && 
                item.getRelationships().getKey().getData() != null) {
                // Fetch the key details from the API
                String keyId = item.getRelationships().getKey().getData().getId();
                log.debug("Found key relationship with ID: {}", keyId);
                try {
                    // Make a request to get the key details
                    log.debug("Making request to /services/v2/keys/{keyId} with keyId: {}", keyId);
                    KeyResponse keyResponse = pcoWebClient.get()
                        .uri("/services/v2/keys/{keyId}", keyId)
                        .retrieve()
                        .bodyToMono(KeyResponse.class)
                        .block();
                    
                    log.debug("Key response received: {}", keyResponse);
                    
                    if (keyResponse != null && keyResponse.getData() != null && 
                        keyResponse.getData().getAttributes() != null) {
                        keyName = keyResponse.getData().getAttributes().getName();
                        log.debug("Found key name: {}", keyName);
                    } else {
                        log.debug("Key response is null or incomplete, using fallback key: {}", keyName);
                    }
                } catch (Exception keyException) {
                    log.warn("Error fetching key details: {}, using fallback key: {}", keyException.getMessage(), keyName);
                }
            } else {
                log.debug("No key relationship found for item: {}, using fallback key: {}", item.getId(), keyName);
            }
            
            // Add the song to the list
            String sequenceString = item.getAttributes().getSequence() != null ? item.getAttributes().getSequence() : "";
            List<String> sequenceList = new ArrayList<>();
            if (sequenceString != null && !sequenceString.isEmpty()) {
                // Split the sequence string into a list
                sequenceList = Arrays.asList(sequenceString.split(","));
            }
            
            songs.add(SongWithLyrics.builder()
                    .title(song.getAttributes().getTitle())
                    .author(song.getAttributes().getAuthor())
                    .lyrics(formatLyricsWithSequence(arrangement.getAttributes().getLyrics(), sequenceList))
                    .key(keyName)
                    .ccliNumber(song.getAttributes().getCcliNumber())
                    .arrangementName(arrangement.getAttributes().getName())
                    .sequence(sequenceString)
                    .build());
            
            log.debug("Added song to result list: {}", song.getAttributes().getTitle());
        }
        
        log.debug("Returning {} songs with lyrics", songs.size());
        return songs;
    }
    
    /**
     * Format lyrics for display
     * @param lyrics The raw lyrics
     * @return Formatted lyrics
     */
    private String formatLyrics(String lyrics) {
        if (lyrics == null || lyrics.isEmpty()) {
            return "";
        }

        // Replace HTML line breaks with newlines
        String formatted = lyrics.replaceAll("<br\\s*/>", "\n");
        
        // Remove HTML tags
        formatted = formatted.replaceAll("<[^>]*>", "");
        
        // Decode HTML entities
        formatted = formatted.replaceAll("&amp;", "&")
                .replaceAll("&lt;", "<")
                .replaceAll("&gt;", ">")
                .replaceAll("&quot;", "\"")
                .replaceAll("&#39;", "'")
                .replaceAll("&nbsp;", " ");
        
        // Remove any leading numeric sequence at the beginning of the lyrics
        formatted = formatted.replaceAll("^\\d+\\s*\n\n", "");
        
        // Clean up multiple consecutive newlines
        formatted = formatted.replaceAll("\n{3,}", "\n\n");
        
        // Remove any trailing whitespace
        formatted = formatted.trim();
        
        return formatted;
    }
    
    /**
     * Format lyrics and enhance with sequence sections
     * @param lyrics The raw lyrics
     * @param sequenceList The list of sequence items
     * @return Formatted and enhanced lyrics
     */
    private String formatLyricsWithSequence(String lyrics, List<String> sequenceList) {
        String formatted = formatLyrics(lyrics);
        return enhanceLyricsWithSequence(formatted, sequenceList);
    }

    /**
     * Enhance lyrics by adding missing sequence sections
     * @param lyrics The formatted lyrics
     * @param sequenceList The list of sequence items
     * @return Enhanced lyrics with all sequence sections
     */
    private String enhanceLyricsWithSequence(String lyrics, List<String> sequenceList) {
        if (lyrics == null || sequenceList == null || sequenceList.isEmpty()) {
            return lyrics;
        }
        
        log.debug("Enhancing lyrics with sequence: {}", sequenceList);
        
        // If the sequence items are just numbers, we'll use them as an order to display sections
        boolean numericSequence = true;
        for (String item : sequenceList) {
            try {
                Integer.parseInt(item.trim());
            } catch (NumberFormatException e) {
                numericSequence = false;
                break;
            }
        }
        
        // If we have numeric sequence items, just return the original lyrics
        // But first, remove any leading numeric sequence at the beginning of the lyrics
        if (numericSequence) {
            log.debug("Numeric sequence detected, returning formatted lyrics");
            return formatLyrics(lyrics);
        }
        
        // Create a map to store sections found in the lyrics
        Map<String, String> sectionsInLyrics = new HashMap<>();
        
        // Split the lyrics by double newlines to get sections
        String[] sections = lyrics.split("\n\n");
        
        // Process each section to identify section headers
        for (int i = 0; i < sections.length; i++) {
            String section = sections[i].trim();
            
            // Check if this section starts with a known section type
            if (section.startsWith("Verse") || 
                section.startsWith("Chorus") || 
                section.startsWith("Bridge") || 
                section.startsWith("Tag") || 
                section.startsWith("Misc") || 
                section.startsWith("Instrumental") ||
                section.startsWith("VERSE") ||
                section.startsWith("CHORUS") ||
                section.startsWith("BRIDGE") ||
                section.startsWith("INSTRUMENTAL") ||
                section.startsWith("Vamp") ||
                section.startsWith("Interlude") ||
                section.startsWith("Pre-Chorus") ||
                section.startsWith("Descant") ||
                section.startsWith("Refrain") ||
                section.startsWith("Ending")) {
                
                // Extract the section name (first line)
                String[] lines = section.split("\n", 2);
                String sectionName = lines[0].trim();
                
                // Store the full section content
                sectionsInLyrics.put(sectionName, section);
                log.debug("Found section in lyrics: {}", sectionName);
            }
        }
        
        // If no sections were found, return the original lyrics
        if (sectionsInLyrics.isEmpty()) {
            log.debug("No sections found in lyrics, returning original");
            return lyrics;
        }
        
        // Create a StringBuilder to build the enhanced lyrics
        StringBuilder enhancedLyrics = new StringBuilder();
        
        // Keep track of sections we've already processed
        Set<String> processedSections = new HashSet<>();
        
        // Process each sequence item in order
        for (String sequenceItem : sequenceList) {
            boolean sectionFound = false;
            
            // Look for exact matches first
            for (Map.Entry<String, String> entry : sectionsInLyrics.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(sequenceItem) && !processedSections.contains(entry.getKey())) {
                    enhancedLyrics.append(entry.getValue()).append("\n\n");
                    processedSections.add(entry.getKey());
                    sectionFound = true;
                    log.debug("Added section {} to enhanced lyrics", entry.getKey());
                    break;
                }
            }
            
            // If no exact match, look for partial matches (e.g., "Verse" matches "Verse 1")
            if (!sectionFound) {
                for (Map.Entry<String, String> entry : sectionsInLyrics.entrySet()) {
                    if ((entry.getKey().startsWith(sequenceItem) || sequenceItem.startsWith(entry.getKey())) 
                        && !processedSections.contains(entry.getKey())) {
                        enhancedLyrics.append(entry.getValue()).append("\n\n");
                        processedSections.add(entry.getKey());
                        sectionFound = true;
                        log.debug("Added partial match section {} to enhanced lyrics for sequence item {}", 
                                 entry.getKey(), sequenceItem);
                        break;
                    }
                }
            }
            
            // If still no match, add a placeholder
            if (!sectionFound) {
                log.debug("No section found for sequence item: {}, adding placeholder", sequenceItem);
                enhancedLyrics.append(sequenceItem).append("\n\n");
            }
        }
        
        // Add any sections that weren't in the sequence at the end
        for (Map.Entry<String, String> entry : sectionsInLyrics.entrySet()) {
            if (!processedSections.contains(entry.getKey())) {
                enhancedLyrics.append(entry.getValue()).append("\n\n");
                log.debug("Added non-sequence section {} to enhanced lyrics", entry.getKey());
            }
        }
        
        return enhancedLyrics.toString().trim();
    }
} 