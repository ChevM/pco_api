package com.pco.lyrics.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pco.lyrics.model.*;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PcoApiServiceWebClientTest {

    private MockWebServer mockWebServer;
    private PcoApiServiceImpl pcoApiService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        
        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();
        
        pcoApiService = new PcoApiServiceImpl(webClient);
        objectMapper = new ObjectMapper();
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void getSongsWithLyrics_shouldReturnSongsWithLyrics() throws Exception {
        // Arrange
        // Mock response for service types
        ServiceTypesResponse serviceTypesResponse = createServiceTypesResponse();
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(objectMapper.writeValueAsString(serviceTypesResponse)));
        
        // Mock response for plans
        PlansResponse plansResponse = createPlansResponse();
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(objectMapper.writeValueAsString(plansResponse)));
        
        // Mock response for items
        ItemsResponse itemsResponse = createItemsResponse();
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(objectMapper.writeValueAsString(itemsResponse)));
        
        // Mock response for arrangement
        ArrangementResponse arrangementResponse = createArrangementResponse();
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(objectMapper.writeValueAsString(arrangementResponse)));
        
        // Mock response for key
        KeyResponse keyResponse = createKeyResponse();
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(objectMapper.writeValueAsString(keyResponse)));

        // Act
        List<SongWithLyrics> result = pcoApiService.getSongsWithLyrics("123");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Amazing Grace", result.get(0).getTitle());
        assertEquals("John Newton", result.get(0).getAuthor());
        assertEquals("G", result.get(0).getKey());
        assertEquals("12345", result.get(0).getCcliNumber());
        assertEquals("Amazing grace, how sweet the sound\nThat saved a wretch like me", result.get(0).getLyrics());
        assertEquals("Default Arrangement", result.get(0).getArrangementName());
        assertEquals("1", result.get(0).getSequence());
    }
    
    private ServiceTypesResponse createServiceTypesResponse() {
        ServiceTypesResponse response = new ServiceTypesResponse();
        
        ServiceType serviceType = new ServiceType();
        serviceType.setId("1");
        serviceType.setType("ServiceType");
        
        ServiceTypeAttributes attributes = new ServiceTypeAttributes();
        attributes.setName("Weekend Service");
        serviceType.setAttributes(attributes);
        
        response.setData(List.of(serviceType));
        
        return response;
    }
    
    private PlansResponse createPlansResponse() {
        PlansResponse response = new PlansResponse();
        
        Plan plan = new Plan();
        plan.setId("123");
        plan.setType("Plan");
        
        PlanAttributes attributes = new PlanAttributes();
        attributes.setTitle("Sunday Service");
        plan.setAttributes(attributes);
        
        response.setData(List.of(plan));
        
        return response;
    }

    private ItemsResponse createItemsResponse() {
        ItemsResponse response = new ItemsResponse();
        
        Item item = new Item();
        item.setId("456");
        item.setType("Item");
        
        Item.ItemAttributes attributes = new Item.ItemAttributes();
        attributes.setTitle("Amazing Grace");
        attributes.setItem_type("song");
        attributes.setKey("G");
        attributes.setSequence("1");
        item.setAttributes(attributes);
        
        Item.ItemRelationships relationships = new Item.ItemRelationships();
        
        Relationship songRelationship = new Relationship();
        Relationship.RelationshipData songData = new Relationship.RelationshipData();
        songData.setId("789");
        songData.setType("Song");
        songRelationship.setData(songData);
        relationships.setSong(songRelationship);
        
        Relationship arrangementRelationship = new Relationship();
        Relationship.RelationshipData arrangementData = new Relationship.RelationshipData();
        arrangementData.setId("101");
        arrangementData.setType("Arrangement");
        arrangementRelationship.setData(arrangementData);
        relationships.setArrangement(arrangementRelationship);
        
        // Create key relationship
        Relationship keyRelationship = new Relationship();
        Relationship.RelationshipData keyData = new Relationship.RelationshipData();
        keyData.setId("202");
        keyData.setType("Key");
        keyRelationship.setData(keyData);
        relationships.setKey(keyRelationship);
        
        item.setRelationships(relationships);
        
        Included included = new Included();
        included.setId("789");
        included.setType("Song");
        
        IncludedAttributes includedAttributes = new IncludedAttributes();
        includedAttributes.setTitle("Amazing Grace");
        includedAttributes.setAuthor("John Newton");
        includedAttributes.setCcliNumber("12345");
        included.setAttributes(includedAttributes);
        
        response.setData(List.of(item));
        response.setIncluded(List.of(included));
        
        return response;
    }

    private ArrangementResponse createArrangementResponse() {
        ArrangementResponse response = new ArrangementResponse();
        
        Arrangement arrangement = new Arrangement();
        arrangement.setId("101");
        arrangement.setType("Arrangement");
        
        Arrangement.ArrangementAttributes attributes = new Arrangement.ArrangementAttributes();
        attributes.setName("Default Arrangement");
        attributes.setLyrics("Amazing grace, how sweet the sound\nThat saved a wretch like me");
        arrangement.setAttributes(attributes);
        
        response.setData(arrangement);
        
        return response;
    }

    private KeyResponse createKeyResponse() {
        KeyResponse response = new KeyResponse();
        
        KeyResponse.Key key = new KeyResponse.Key();
        key.setId("202");
        key.setType("Key");
        
        KeyResponse.KeyAttributes attributes = new KeyResponse.KeyAttributes();
        attributes.setName("G Major");
        key.setAttributes(attributes);
        
        response.setData(key);
        
        return response;
    }
} 