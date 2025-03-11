package com.pco.lyrics.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Plan {
    private String id;
    private String type;
    private PlanAttributes attributes;
    private Links links;
} 