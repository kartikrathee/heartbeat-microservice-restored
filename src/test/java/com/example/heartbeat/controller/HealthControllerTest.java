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
                    .andExpect(header().string("Content-Type", "application/json"));
        }

        @Test
        @DisplayName("Endpoints should accept compatible Accept headers")
        void endpoints_ShouldAcceptCompatibleAcceptHeaders() throws Exception {
            // Test with application/json
            mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));

            // Test with */*
            mockMvc.perform(get("/").accept(MediaType.ALL))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));

            // Test with text/plain (should return 406 Not Acceptable)
            mockMvc.perform(get("/").accept(MediaType.TEXT_PLAIN))
                    .andExpect(status().isNotAcceptable());
        }

        @Test
        @DisplayName("Endpoints should return 406 for incompatible Accept headers")
        void endpoints_ShouldReturn406ForIncompatibleAcceptHeaders() throws Exception {
            // Test various incompatible Accept headers
            mockMvc.perform(get("/").accept(MediaType.TEXT_PLAIN))
                    .andExpect(status().isNotAcceptable());

            mockMvc.perform(get("/").accept(MediaType.TEXT_HTML))
                    .andExpect(status().isNotAcceptable());

            mockMvc.perform(get("/status").accept(MediaType.APPLICATION_XML))
                    .andExpect(status().isNotAcceptable());
        }
    }

    @Nested
    @DisplayName("Data Validation Tests")
    class DataValidationTests {

        @Test
        @DisplayName("Timestamp should be valid ISO format")
        void endpoints_ShouldReturnValidISOTimestamp() throws Exception {
            MvcResult homeResult = mockMvc.perform(get("/"))
                    .andExpect(status().isOk())
                    .andReturn();

            MvcResult statusResult = mockMvc.perform(get("/status"))
                    .andExpect(status().isOk())
                    .andReturn();

            // Verify home endpoint timestamp
            Map<String, Object> homeResponse = objectMapper.readValue(
                    homeResult.getResponse().getContentAsString(), Map.class);
            String homeTimestamp = homeResponse.get("timestamp").toString();
            
            assertThatNoException().isThrownBy(() -> LocalDateTime.parse(homeTimestamp));

            // Verify status endpoint timestamp
            Map<String, Object> statusResponse = objectMapper.readValue(
                    statusResult.getResponse().getContentAsString(), Map.class);
            String statusTimestamp = statusResponse.get("timestamp").toString();
            
            assertThatNoException().isThrownBy(() -> LocalDateTime.parse(statusTimestamp));
        }

        @Test
        @DisplayName("Service name should be consistent across endpoints")
        void endpoints_ShouldReturnConsistentServiceName() throws Exception {
            MvcResult homeResult = mockMvc.perform(get("/"))
                    .andExpect(status().isOk())
                    .andReturn();

            MvcResult statusResult = mockMvc.perform(get("/status"))
                    .andExpect(status().isOk())
                    .andReturn();

            Map<String, Object> homeResponse = objectMapper.readValue(
                    homeResult.getResponse().getContentAsString(), Map.class);
            Map<String, Object> statusResponse = objectMapper.readValue(
                    statusResult.getResponse().getContentAsString(), Map.class);

            assertThat(homeResponse.get("service")).isEqualTo(statusResponse.get("service"));
            assertThat(homeResponse.get("service")).isEqualTo("heartbeat-microservice");
        }

        @Test
        @DisplayName("Heartbeat configuration should have valid values")
        void statusEndpoint_ShouldReturnValidHeartbeatConfiguration() throws Exception {
            MvcResult result = mockMvc.perform(get("/status"))
                    .andExpect(status().isOk())
                    .andReturn();

            Map<String, Object> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), Map.class);

            String heartbeatInterval = response.get("heartbeat_interval").toString();
            String leaseExpiration = response.get("lease_expiration").toString();

            // Verify format and values
            assertThat(heartbeatInterval).matches("\\d+ seconds");
            assertThat(leaseExpiration).matches("\\d+ seconds");
            assertThat(heartbeatInterval).isEqualTo("10 seconds");
            assertThat(leaseExpiration).isEqualTo("30 seconds");

            // Verify lease expiration is greater than heartbeat interval
            int heartbeatValue = Integer.parseInt(heartbeatInterval.split(" ")[0]);
            int leaseValue = Integer.parseInt(leaseExpiration.split(" ")[0]);
            assertThat(leaseValue).isGreaterThan(heartbeatValue);
        }
    }

    @Nested
    @DisplayName("Performance and Reliability Tests")
    class PerformanceAndReliabilityTests {

        @RepeatedTest(5)
        @DisplayName("Endpoints should be consistent across multiple calls")
        void endpoints_ShouldBeConsistentAcrossMultipleCalls() throws Exception {
            // Test home endpoint consistency
            MvcResult result1 = mockMvc.perform(get("/"))
                    .andExpect(status().isOk())
                    .andReturn();

            MvcResult result2 = mockMvc.perform(get("/"))
                    .andExpect(status().isOk())
                    .andReturn();

            Map<String, Object> response1 = objectMapper.readValue(
