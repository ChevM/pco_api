package com.pco.lyrics.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    private String id;
    private String type;
    private ItemAttributes attributes;
    private ItemRelationships relationships;
    private Links links;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemAttributes {
        private String title;
        private String sequence;
        @JsonProperty("item_type")
        private String itemType;
        private String key;
        private Integer length;
        private String description;
        private String htmlDetails;
        private String servicePosition;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemRelationships {
        private Relationship plan;
        private Relationship song;
        private Relationship arrangement;
        private Relationship key;
    }
} 