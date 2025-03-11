package com.pco.lyrics.controller;

import com.pco.lyrics.model.Plan;
import com.pco.lyrics.model.PlanAttributes;
import com.pco.lyrics.model.ServiceType;
import com.pco.lyrics.model.ServiceTypeAttributes;
import com.pco.lyrics.model.SongWithLyrics;
import com.pco.lyrics.service.PcoApiService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PcoApiControllerTest {

    @Mock
    private PcoApiService pcoApiService;

    @InjectMocks
    private PcoApiController pcoApiController;

    @Test
    void getServiceTypes_shouldReturnServiceTypes() {
        // Arrange
        ServiceType serviceType = createServiceType();
        when(pcoApiService.getServiceTypes()).thenReturn(List.of(serviceType));

        // Act
        ResponseEntity<List<ServiceType>> response = pcoApiController.getServiceTypes();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("1", response.getBody().get(0).getId());
        assertEquals("Weekend Service", response.getBody().get(0).getAttributes().getName());
        
        verify(pcoApiService).getServiceTypes();
    }

    @Test
    void getPlans_shouldReturnPlans() {
        // Arrange
        Plan plan = createPlan();
        when(pcoApiService.getPlans("1")).thenReturn(List.of(plan));

        // Act
        ResponseEntity<List<Plan>> response = pcoApiController.getPlans("1");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("123", response.getBody().get(0).getId());
        assertEquals("March 10, 2024", response.getBody().get(0).getAttributes().getDates());
        
        verify(pcoApiService).getPlans("1");
    }

    @Test
    void getSongsWithLyrics_shouldReturnSongsWithLyrics() {
        // Arrange
        SongWithLyrics song = createSongWithLyrics();
        when(pcoApiService.getSongsWithLyrics("123")).thenReturn(List.of(song));

        // Act
        ResponseEntity<List<SongWithLyrics>> response = pcoApiController.getSongsWithLyrics("123");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Amazing Grace", response.getBody().get(0).getTitle());
        assertEquals("John Newton", response.getBody().get(0).getAuthor());
        assertEquals("G", response.getBody().get(0).getKey());
        
        verify(pcoApiService).getSongsWithLyrics("123");
    }

    private ServiceType createServiceType() {
        ServiceType serviceType = new ServiceType();
        serviceType.setId("1");
        serviceType.setType("ServiceType");
        
        ServiceTypeAttributes attributes = new ServiceTypeAttributes();
        attributes.setName("Weekend Service");
        attributes.setFrequency("Weekly");
        
        serviceType.setAttributes(attributes);
        
        return serviceType;
    }

    private Plan createPlan() {
        Plan plan = new Plan();
        plan.setId("123");
        plan.setType("Plan");
        
        PlanAttributes attributes = new PlanAttributes();
        attributes.setTitle("Sunday Service");
        attributes.setDates("March 10, 2024");
        attributes.setCreatedAt(LocalDateTime.now());
        attributes.setUpdatedAt(LocalDateTime.now());
        
        plan.setAttributes(attributes);
        
        return plan;
    }

    private SongWithLyrics createSongWithLyrics() {
        return SongWithLyrics.builder()
                .title("Amazing Grace")
                .author("John Newton")
                .lyrics("Amazing grace, how sweet the sound\nThat saved a wretch like me")
                .key("G")
                .ccliNumber("12345")
                .build();
    }
} 