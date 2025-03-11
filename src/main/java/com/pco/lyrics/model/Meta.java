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
public class Meta {
    @JsonProperty("total_count")
    private int totalCount;
    
    private int count;
    
    // The 'next' field can be either a String or an Object in the PCO API response
    private Object next;
} 