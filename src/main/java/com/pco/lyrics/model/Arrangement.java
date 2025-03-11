package com.pco.lyrics.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Arrangement {
    private String id;
    private String type;
    private ArrangementAttributes attributes;
    private Links links;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArrangementAttributes {
        private String name;
        private String lyrics;
        private Integer length;
        private String chordChart;
        private String meter;
        private Integer bpm;
    }
} 