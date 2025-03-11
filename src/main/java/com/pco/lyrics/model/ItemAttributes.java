package com.pco.lyrics.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemAttributes {
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
    
    private String title;
    private int sequence;
    
    @JsonProperty("item_type")
    private String itemType;
    
    private String description;
    private int length;
    
    @JsonProperty("plan_notes_count")
    private int planNotesCount;
    
    @JsonProperty("service_position")
    private String servicePosition;
    
    private String key;
    
    @JsonProperty("has_attachments")
    private boolean hasAttachments;
    
    @JsonProperty("has_lyrics")
    private boolean hasLyrics;
} 