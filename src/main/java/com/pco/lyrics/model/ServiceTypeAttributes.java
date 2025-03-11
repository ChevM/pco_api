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
public class ServiceTypeAttributes {
    private String name;
    private String frequency;
    
    @JsonProperty("last_plan_from")
    private String lastPlanFrom;
    
    private String permissions;
    
    @JsonProperty("time_preference_option")
    private String timePreferenceOption;
} 