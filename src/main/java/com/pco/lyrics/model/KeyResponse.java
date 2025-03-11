package com.pco.lyrics.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a key response from the PCO API
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeyResponse {
    private Key data;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Key {
        private String id;
        private String type;
        private KeyAttributes attributes;
        
        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class KeyAttributes {
            private String name;
            private String created_at;
            private String updated_at;
        }
    }
} 