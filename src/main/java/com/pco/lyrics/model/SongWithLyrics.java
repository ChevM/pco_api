package com.pco.lyrics.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model class representing a song with its lyrics and additional metadata.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SongWithLyrics {
    private String title;
    private String author;
    private String lyrics;
    private String key;
    private String ccliNumber;
    private String arrangementName;
    private String sequence;
} 