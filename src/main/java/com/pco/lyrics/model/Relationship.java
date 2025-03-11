package com.pco.lyrics.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a relationship in the PCO API
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Relationship {
    private RelationshipData data;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RelationshipData {
        private String type;
        private String id;
    }
} 