package com.pco.lyrics.controller;

import com.pco.lyrics.model.*;
import com.pco.lyrics.service.PcoApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PcoApiControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PcoApiService pcoApiService;

    @Test
    void getServiceTypes_shouldReturnServiceTypes() throws Exception {
        // Arrange
        ServiceType serviceType = new ServiceType();
        serviceType.setId("1");
        serviceType.setType("ServiceType");
        
        ServiceTypeAttributes attributes = new ServiceTypeAttributes();
        attributes.setName("Weekend Service");
        serviceType.setAttributes(attributes);
        
        when(pcoApiService.getServiceTypes()).thenReturn(List.of(serviceType));

        // Act & Assert
        mockMvc.perform(get("/api/pco/service-types")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is("1")))
                .andExpect(jsonPath("$[0].type", is("ServiceType")))
                .andExpect(jsonPath("$[0].attributes.name", is("Weekend Service")));
    }

    @Test
    void getPlans_shouldReturnPlans() throws Exception {
        // Arrange
        Plan plan = new Plan();
        plan.setId("123");
        plan.setType("Plan");
        
        PlanAttributes attributes = new PlanAttributes();
        attributes.setTitle("Sunday Service");
        attributes.setDates("March 10, 2024");
        plan.setAttributes(attributes);
        
        when(pcoApiService.getPlans("1")).thenReturn(List.of(plan));

        // Act & Assert
        mockMvc.perform(get("/api/pco/service-types/1/plans")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is("123")))
                .andExpect(jsonPath("$[0].type", is("Plan")))
                .andExpect(jsonPath("$[0].attributes.title", is("Sunday Service")))
                .andExpect(jsonPath("$[0].attributes.dates", is("March 10, 2024")));
    }

    @Test
    void getSongsWithLyrics_shouldReturnSongsWithLyrics() throws Exception {
        // Arrange
        SongWithLyrics song = SongWithLyrics.builder()
                .title("Amazing Grace")
                .author("John Newton")
                .lyrics("Amazing grace, how sweet the sound\nThat saved a wretch like me")
                .key("G")
                .ccliNumber("12345")
                .build();
        
        when(pcoApiService.getSongsWithLyrics("123")).thenReturn(List.of(song));

        // Act & Assert
        mockMvc.perform(get("/api/pco/plans/123/songs")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Amazing Grace")))
                .andExpect(jsonPath("$[0].author", is("John Newton")))
                .andExpect(jsonPath("$[0].key", is("G")))
                .andExpect(jsonPath("$[0].ccliNumber", is("12345")));
    }
} 