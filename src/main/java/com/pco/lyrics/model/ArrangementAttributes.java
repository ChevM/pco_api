package com.pco.lyrics.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArrangementAttributes {
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
    
    private int bpm;
    
    @JsonProperty("chart_key")
    private String chartKey;
    
    private int length;
    private String meter;
    private String name;
    private String notes;
    private boolean print;
    private boolean rehearsal;
    private List<String> sequence;
    private String lyrics;
    
    @JsonProperty("chord_chart")
    private String chordChart;
} 