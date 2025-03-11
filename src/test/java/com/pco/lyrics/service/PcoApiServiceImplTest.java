package com.pco.lyrics.service;

import com.pco.lyrics.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PcoApiServiceImplTest {

    @Mock
    private WebClient webClientMock;

    @Mock
    private RequestHeadersUriSpec requestHeadersUriSpecMock;

    @Mock
    private RequestHeadersSpec requestHeadersSpecMock;

    @Mock
    private ResponseSpec responseSpecMock;

    @InjectMocks
    private PcoApiServiceImpl pcoApiService;

    @Test
    void getServiceTypes_shouldReturnServiceTypes() {
        // Arrange
        ServiceType serviceType = createServiceType();
        ServiceTypesResponse response = new ServiceTypesResponse();
        response.setData(List.of(serviceType));
        
        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri("/services/v2/service_types")).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(ServiceTypesResponse.class)).thenReturn(Mono.just(response));

        // Act
        List<ServiceType> result = pcoApiService.getServiceTypes();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getId());
        assertEquals("Weekend Service", result.get(0).getAttributes().getName());
        
        verify(webClientMock).get();
        verify(requestHeadersUriSpecMock).uri("/services/v2/service_types");
    }

    @Test
    void getPlans_shouldReturnPlans() {
        // Arrange
        Plan plan = createPlan();
        PlansResponse response = new PlansResponse();
        response.setData(List.of(plan));
        
        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(eq("/services/v2/service_types/{serviceTypeId}/plans"), eq("1"))).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(PlansResponse.class)).thenReturn(Mono.just(response));

        // Act
        List<Plan> result = pcoApiService.getPlans("1");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("123", result.get(0).getId());
        assertEquals("March 10, 2024", result.get(0).getAttributes().getDates());
        
        verify(webClientMock).get();
        verify(requestHeadersUriSpecMock).uri(eq("/services/v2/service_types/{serviceTypeId}/plans"), eq("1"));
    }

    @Test
    void getSongsWithLyrics_shouldReturnSongsWithLyrics_whenArrangementFetchSucceeds() {
        // Arrange
        // Mock service types response
        ServiceType serviceType = createServiceType();
        ServiceTypesResponse serviceTypesResponse = new ServiceTypesResponse();
        serviceTypesResponse.setData(List.of(serviceType));
        
        // Mock plans response
        Plan plan = createPlan();
        plan.setId("456"); // Match the planId we'll use in the test
        PlansResponse plansResponse = new PlansResponse();
        plansResponse.setData(List.of(plan));
        
        // Mock items response with song items
        ItemsResponse itemsResponse = createItemsResponseWithSongItems();
        
        // Mock arrangement response
        ArrangementResponse arrangementResponse = createArrangementResponse();
        
        // Set up WebClient mocks
        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        
        // For service types
        when(requestHeadersUriSpecMock.uri("/services/v2/service_types"))
            .thenReturn(requestHeadersSpecMock);
        
        // For plans
        when(requestHeadersUriSpecMock.uri(eq("/services/v2/service_types/{serviceTypeId}/plans"), eq("1")))
            .thenReturn(requestHeadersSpecMock);
        
        // For items
        when(requestHeadersUriSpecMock.uri(eq("/services/v2/service_types/{serviceTypeId}/plans/{planId}/items?include=song"), 
                                        eq("1"), eq("456")))
            .thenReturn(requestHeadersSpecMock);
        
        // For arrangement
        when(requestHeadersUriSpecMock.uri(eq("/services/v2/songs/{songId}/arrangements/{arrangementId}"), 
                                        eq("789"), eq("101")))
            .thenReturn(requestHeadersSpecMock);
        
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        
        // Set up response mocks
        when(responseSpecMock.bodyToMono(ServiceTypesResponse.class))
            .thenReturn(Mono.just(serviceTypesResponse));
        
        when(responseSpecMock.bodyToMono(PlansResponse.class))
            .thenReturn(Mono.just(plansResponse));
        
        when(responseSpecMock.bodyToMono(ItemsResponse.class))
            .thenReturn(Mono.just(itemsResponse));
        
        when(responseSpecMock.bodyToMono(ArrangementResponse.class))
            .thenReturn(Mono.just(arrangementResponse));

        // Act
        List<SongWithLyrics> result = pcoApiService.getSongsWithLyrics("456");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Amazing Grace", result.get(0).getTitle());
        assertEquals("John Newton", result.get(0).getAuthor());
        assertEquals("Amazing grace, how sweet the sound\nThat saved a wretch like me", result.get(0).getLyrics());
        assertEquals("G", result.get(0).getKey());
        assertEquals("12345", result.get(0).getCcliNumber());
        assertEquals("Default Arrangement", result.get(0).getArrangementName());
        assertEquals("1", result.get(0).getSequence());
        
        // Verify the correct endpoints were called
        verify(requestHeadersUriSpecMock).uri("/services/v2/service_types");
        verify(requestHeadersUriSpecMock).uri(eq("/services/v2/service_types/{serviceTypeId}/plans"), eq("1"));
        verify(requestHeadersUriSpecMock).uri(eq("/services/v2/service_types/{serviceTypeId}/plans/{planId}/items?include=song"), 
                                           eq("1"), eq("456"));
        verify(requestHeadersUriSpecMock).uri(eq("/services/v2/songs/{songId}/arrangements/{arrangementId}"), 
                                           eq("789"), eq("101"));
    }
    
    @Test
    void getSongsWithLyrics_shouldReturnSongsWithLyrics_whenArrangementFetchFails() {
        // Arrange
        // Mock service types response
        ServiceType serviceType = createServiceType();
        ServiceTypesResponse serviceTypesResponse = new ServiceTypesResponse();
        serviceTypesResponse.setData(List.of(serviceType));
        
        // Mock plans response
        Plan plan = createPlan();
        plan.setId("456"); // Match the planId we'll use in the test
        PlansResponse plansResponse = new PlansResponse();
        plansResponse.setData(List.of(plan));
        
        // Mock items response with song items
        ItemsResponse itemsResponse = createItemsResponseWithSongItems();
        
        // Mock arrangements response for fallback
        ArrangementsResponse arrangementsResponse = createArrangementsResponse();
        
        // Mock key response
        KeyResponse keyResponse = createKeyResponse();
        
        // Set up WebClient mocks
        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        
        // For service types
        when(requestHeadersUriSpecMock.uri("/services/v2/service_types"))
            .thenReturn(requestHeadersSpecMock);
        
        // For plans
        when(requestHeadersUriSpecMock.uri(eq("/services/v2/service_types/{serviceTypeId}/plans"), eq("1")))
            .thenReturn(requestHeadersSpecMock);
        
        // For items
        when(requestHeadersUriSpecMock.uri(eq("/services/v2/service_types/{serviceTypeId}/plans/{planId}/items?include=song"), 
                                        eq("1"), eq("456")))
            .thenReturn(requestHeadersSpecMock);
        
        // For arrangement - this will throw an exception
        when(requestHeadersUriSpecMock.uri(eq("/services/v2/songs/{songId}/arrangements/{arrangementId}"), 
                                        eq("789"), eq("101")))
            .thenReturn(requestHeadersSpecMock);
        
        // For arrangements fallback
        when(requestHeadersUriSpecMock.uri(eq("/services/v2/songs/{songId}/arrangements"), eq("789")))
            .thenReturn(requestHeadersSpecMock);
        
        // For key
        when(requestHeadersUriSpecMock.uri(eq("/services/v2/keys/{keyId}"), eq("202")))
            .thenReturn(requestHeadersSpecMock);
        
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        
        // Set up response mocks
        when(responseSpecMock.bodyToMono(ServiceTypesResponse.class))
            .thenReturn(Mono.just(serviceTypesResponse));
        
        when(responseSpecMock.bodyToMono(PlansResponse.class))
            .thenReturn(Mono.just(plansResponse));
        
        when(responseSpecMock.bodyToMono(ItemsResponse.class))
            .thenReturn(Mono.just(itemsResponse));
        
        // This will throw an exception when called
        when(responseSpecMock.bodyToMono(ArrangementResponse.class))
            .thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));
        
        // This is the fallback
        when(responseSpecMock.bodyToMono(ArrangementsResponse.class))
            .thenReturn(Mono.just(arrangementsResponse));
        
        // Mock the key response to throw an exception to test the fallback
        when(responseSpecMock.bodyToMono(KeyResponse.class))
            .thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        // Act
        List<SongWithLyrics> result = pcoApiService.getSongsWithLyrics("456");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Amazing Grace", result.get(0).getTitle());
        assertEquals("John Newton", result.get(0).getAuthor());
        assertEquals("Amazing grace, how sweet the sound\nThat saved a wretch like me", result.get(0).getLyrics());
        assertEquals("G", result.get(0).getKey());
        assertEquals("12345", result.get(0).getCcliNumber());
        assertEquals("Default Arrangement", result.get(0).getArrangementName());
        assertEquals("1", result.get(0).getSequence());
        
        // Verify the correct endpoints were called
        verify(requestHeadersUriSpecMock).uri("/services/v2/service_types");
        verify(requestHeadersUriSpecMock).uri(eq("/services/v2/service_types/{serviceTypeId}/plans"), eq("1"));
        verify(requestHeadersUriSpecMock).uri(eq("/services/v2/service_types/{serviceTypeId}/plans/{planId}/items?include=song"), 
                                           eq("1"), eq("456"));
        verify(requestHeadersUriSpecMock).uri(eq("/services/v2/songs/{songId}/arrangements/{arrangementId}"), 
                                           eq("789"), eq("101"));
        verify(requestHeadersUriSpecMock).uri(eq("/services/v2/songs/{songId}/arrangements"), eq("789"));
    }
    
    @Test
    void getSongsWithLyrics_shouldReturnEmptyList_whenServiceTypeNotFound() {
        // Arrange
        ServiceTypesResponse serviceTypesResponse = new ServiceTypesResponse();
        serviceTypesResponse.setData(List.of());
        
        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri("/services/v2/service_types")).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(ServiceTypesResponse.class)).thenReturn(Mono.just(serviceTypesResponse));

        // Act
        List<SongWithLyrics> result = pcoApiService.getSongsWithLyrics("456");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(requestHeadersUriSpecMock).uri("/services/v2/service_types");
        verifyNoMoreInteractions(requestHeadersUriSpecMock);
    }

    private ServiceType createServiceType() {
        ServiceType serviceType = new ServiceType();
        serviceType.setId("1");
        serviceType.setType("ServiceType");
        
        ServiceTypeAttributes attributes = new ServiceTypeAttributes();
        attributes.setName("Weekend Service");
        attributes.setFrequency("Weekly");
        
        serviceType.setAttributes(attributes);
        
        Links links = new Links();
        links.setSelf("https://api.planningcenteronline.com/services/v2/service_types/1");
        serviceType.setLinks(links);
        
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
        
        Links links = new Links();
        links.setSelf("https://api.planningcenteronline.com/services/v2/service_types/1/plans/123");
        plan.setLinks(links);
        
        return plan;
    }

    private ItemsResponse createItemsResponseWithSongItems() {
        ItemsResponse response = new ItemsResponse();
        
        // Create a song item
        Item item = new Item();
        item.setId("456");
        item.setType("Item");
        
        Item.ItemAttributes attributes = new Item.ItemAttributes();
        attributes.setTitle("Amazing Grace");
        attributes.setItem_type("song");
        attributes.setKey("G");
        attributes.setSequence("1");
        item.setAttributes(attributes);
        
        // Create song relationship
        Item.ItemRelationships relationships = new Item.ItemRelationships();
        
        Relationship songRelationship = new Relationship();
        Relationship.RelationshipData songData = new Relationship.RelationshipData();
        songData.setId("789");
        songData.setType("Song");
        songRelationship.setData(songData);
        relationships.setSong(songRelationship);
        
        // Create arrangement relationship
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
        
        // Create included song
        Included includedSong = new Included();
        includedSong.setId("789");
        includedSong.setType("Song");
        
        IncludedAttributes songAttributes = new IncludedAttributes();
        songAttributes.setTitle("Amazing Grace");
        songAttributes.setAuthor("John Newton");
        songAttributes.setCcliNumber("12345");
        includedSong.setAttributes(songAttributes);
        
        response.setData(List.of(item));
        response.setIncluded(List.of(includedSong));
        
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
    
    private ArrangementsResponse createArrangementsResponse() {
        ArrangementsResponse response = new ArrangementsResponse();
        
        Arrangement arrangement = new Arrangement();
        arrangement.setId("101");
        arrangement.setType("Arrangement");
        
        Arrangement.ArrangementAttributes attributes = new Arrangement.ArrangementAttributes();
        attributes.setName("Default Arrangement");
        attributes.setLyrics("Amazing grace, how sweet the sound\nThat saved a wretch like me");
        arrangement.setAttributes(attributes);
        
        response.setData(List.of(arrangement));
        
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