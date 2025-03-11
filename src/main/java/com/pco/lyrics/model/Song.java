package com.pco.lyrics.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Song {
    private String id;
    private String type;
    private SongAttributes attributes;
    private SongRelationships relationships;
    private Links links;
} 