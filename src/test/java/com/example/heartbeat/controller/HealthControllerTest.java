package com.example.heartbeat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HealthController.class)
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void homeEndpoint_ShouldReturnServiceInformation() throws Exception {
        MvcResult result = mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.service").value("heartbeat-microservice"))
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.message").value("Microservice is running and sending heartbeats to Eureka"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andReturn();

        // Verify the response structure
        String responseContent = result.getResponse().getContentAsString();
        Map<String, Object> response = objectMapper.readValue(responseContent, Map.class);
        
        assertThat(response).containsKeys("service", "status", "timestamp", "message");
        assertThat(response.get("service")).isEqualTo("heartbeat-microservice");
        assertThat(response.get("status")).isEqualTo("UP");
    }

    @Test
    void statusEndpoint_ShouldReturnDetailedStatus() throws Exception {
        MvcResult result = mockMvc.perform(get("/status"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.service").value("heartbeat-microservice"))
                .andExpect(jsonPath("$.status").value("HEALTHY"))
                .andExpect(jsonPath("$.heartbeat_interval").value("10 seconds"))
                .andExpect(jsonPath("$.lease_expiration").value("30 seconds"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andReturn();

        // Verify the response structure and heartbeat configuration
        String responseContent = result.getResponse().getContentAsString();
        Map<String, Object> response = objectMapper.readValue(responseContent, Map.class);
        
        assertThat(response).containsKeys("service", "status", "heartbeat_interval", "lease_expiration", "timestamp");
        assertThat(response.get("heartbeat_interval")).isEqualTo("10 seconds");
        assertThat(response.get("lease_expiration")).isEqualTo("30 seconds");
    }

    @Test
    void homeEndpoint_ShouldReturnValidTimestamp() throws Exception {
        MvcResult result = mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        Map<String, Object> response = objectMapper.readValue(responseContent, Map.class);
        
        // Verify timestamp is present and not null
        assertThat(response.get("timestamp")).isNotNull();
        assertThat(response.get("timestamp").toString()).isNotEmpty();
    }

    @Test
    void statusEndpoint_ShouldReturnValidTimestamp() throws Exception {
        MvcResult result = mockMvc.perform(get("/status"))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        Map<String, Object> response = objectMapper.readValue(responseContent, Map.class);
        
        // Verify timestamp is present and not null
        assertThat(response.get("timestamp")).isNotNull();
        assertThat(response.get("timestamp").toString()).isNotEmpty();
    }

    @Test
    void homeEndpoint_ShouldHaveCorrectResponseStructure() throws Exception {
        MvcResult result = mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        Map<String, Object> response = objectMapper.readValue(responseContent, Map.class);
        
        // Verify all expected fields are present
        assertThat(response).hasSize(4);
        assertThat(response).containsOnlyKeys("service", "status", "timestamp", "message");
    }

    @Test
    void statusEndpoint_ShouldHaveCorrectResponseStructure() throws Exception {
        MvcResult result = mockMvc.perform(get("/status"))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        Map<String, Object> response = objectMapper.readValue(responseContent, Map.class);
        
        // Verify all expected fields are present
        assertThat(response).hasSize(5);
        assertThat(response).containsOnlyKeys("service", "status", "heartbeat_interval", "lease_expiration", "timestamp");
    }

    // ========== ADDITIONAL ROBUST TEST COVERAGE ==========

    @Nested
    @DisplayName("HTTP Method Tests")
    class HttpMethodTests {

        @Test
        @DisplayName("Home endpoint should only accept GET requests")
        void homeEndpoint_ShouldOnlyAcceptGetRequests() throws Exception {
            // Test unsupported HTTP methods
            mockMvc.perform(post("/"))
                    .andExpect(status().isMethodNotAllowed());

            mockMvc.perform(put("/"))
                    .andExpect(status().isMethodNotAllowed());

            mockMvc.perform(delete("/"))
                    .andExpect(status().isMethodNotAllowed());

            mockMvc.perform(patch("/"))
                    .andExpect(status().isMethodNotAllowed());
        }

        @Test
        @DisplayName("Status endpoint should only accept GET requests")
        void statusEndpoint_ShouldOnlyAcceptGetRequests() throws Exception {
            // Test unsupported HTTP methods
            mockMvc.perform(post("/status"))
                    .andExpect(status().isMethodNotAllowed());

            mockMvc.perform(put("/status"))
                    .andExpect(status().isMethodNotAllowed());

            mockMvc.perform(delete("/status"))
                    .andExpect(status().isMethodNotAllowed());

            mockMvc.perform(patch("/status"))
                    .andExpect(status().isMethodNotAllowed());
        }
    }

    @Nested
    @DisplayName("Content Type and Headers Tests")
    class ContentTypeAndHeadersTests {

        @Test
        @DisplayName("Endpoints should return JSON content type")
        void endpoints_ShouldReturnJsonContentType() throws Exception {
            mockMvc.perform(get("/"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(header().string("Content-Type", "application/json"));

            mockMvc.perform(get("/status"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
