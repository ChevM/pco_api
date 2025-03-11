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
public class IncludedAttributes {
    private String title;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
    
    private String admin;
    private String author;
    private String copyright;
    private boolean hidden;
    private String notes;
    private String themes;
    
    @JsonProperty("last_scheduled_at")
    private LocalDateTime lastScheduledAt;
    
    @JsonProperty("ccli_number")
    private String ccliNumber;
} 