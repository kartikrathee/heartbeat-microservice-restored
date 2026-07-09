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
