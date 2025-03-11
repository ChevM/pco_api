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
public class PlanAttributes {
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
    
    private String title;
    private String dates;
    
    @JsonProperty("service_times")
    private List<String> serviceTimes;
    
    @JsonProperty("last_time_at")
    private LocalDateTime lastTimeAt;
    
    @JsonProperty("series_title")
    private String seriesTitle;
    
    @JsonProperty("plan_notes_count")
    private int planNotesCount;
    
    @JsonProperty("other_time_count")
    private int otherTimeCount;
    
    @JsonProperty("rehearsal_time_count")
    private int rehearsalTimeCount;
    
    @JsonProperty("plan_people_count")
    private int planPeopleCount;
    
    @JsonProperty("needed_positions")
    private int neededPositions;
    
    @JsonProperty("item_count")
    private int itemCount;
} 