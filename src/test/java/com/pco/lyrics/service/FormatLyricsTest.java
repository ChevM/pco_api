package com.pco.lyrics.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FormatLyricsTest {

    @InjectMocks
    private PcoApiServiceImpl pcoApiService;

    @Test
    void formatLyrics_withNullLyrics_shouldReturnEmptyString() {
        // Act
        String result = (String) ReflectionTestUtils.invokeMethod(pcoApiService, "formatLyrics", (String) null);
        
        // Assert
        assertEquals("", result);
    }

    @Test
    void formatLyrics_withEmptyLyrics_shouldReturnEmptyString() {
        // Act
        String result = (String) ReflectionTestUtils.invokeMethod(pcoApiService, "formatLyrics", "");
        
        // Assert
        assertEquals("", result);
    }

    @Test
    void formatLyrics_withSingleLineBreak_shouldReplaceWithSpace() {
        // Arrange
        String lyrics = "Amazing grace\nhow sweet the sound";
        
        // Act
        String result = (String) ReflectionTestUtils.invokeMethod(pcoApiService, "formatLyrics", lyrics);
        
        // Assert
        assertEquals("Amazing grace how sweet the sound", result);
    }

    @Test
    void formatLyrics_withDoubleLineBreak_shouldPreserveDoubleLineBreak() {
        // Arrange
        String lyrics = "Amazing grace\n\nhow sweet the sound";
        
        // Act
        String result = (String) ReflectionTestUtils.invokeMethod(pcoApiService, "formatLyrics", lyrics);
        
        // Assert
        assertEquals("Amazing grace\n\nhow sweet the sound", result);
    }

    @Test
    void formatLyrics_withMixedLineBreaks_shouldFormatCorrectly() {
        // Arrange
        String lyrics = "Amazing grace\nhow sweet the sound\n\nThat saved a wretch\nlike me";
        
        // Act
        String result = (String) ReflectionTestUtils.invokeMethod(pcoApiService, "formatLyrics", lyrics);
        
        // Assert
        assertEquals("Amazing grace how sweet the sound\n\nThat saved a wretch like me", result);
    }

    @Test
    void formatLyrics_withMultipleConsecutiveLineBreaks_shouldFormatCorrectly() {
        // Arrange
        String lyrics = "Amazing grace\n\n\nhow sweet the sound";
        
        // Act
        String result = (String) ReflectionTestUtils.invokeMethod(pcoApiService, "formatLyrics", lyrics);
        
        // Assert
        // The current implementation treats \n\n\n as \n\n followed by \n
        // So it becomes \n\n followed by a space
        assertEquals("Amazing grace\n\n how sweet the sound", result);
    }
} 