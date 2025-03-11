package com.pco.lyrics.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Included {
    private String type;
    private String id;
    private IncludedAttributes attributes;
    private IncludedRelationships relationships;
    private Links links;
} 