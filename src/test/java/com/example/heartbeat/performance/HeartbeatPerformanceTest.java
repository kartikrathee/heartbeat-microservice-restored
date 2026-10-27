package com.example.heartbeat.performance;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "eureka.client.enabled=false",
    "eureka.client.register-with-eureka=false",
    "eureka.client.fetch-registry=false"
})
class HeartbeatPerformanceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void homeEndpoint_ShouldHandleConcurrentRequests() throws Exception {
        int numberOfThreads = 10;
        int requestsPerThread = 5;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        List<CompletableFuture<ResponseEntity<Map>>> futures = new ArrayList<>();

        String url = "http://localhost:" + port + "/";

        // Submit concurrent requests
        for (int i = 0; i < numberOfThreads * requestsPerThread; i++) {
            CompletableFuture<ResponseEntity<Map>> future = CompletableFuture.supplyAsync(() ->
                restTemplate.getForEntity(url, Map.class), executor);
            futures.add(future);
        }

        // Wait for all requests to complete
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
            futures.toArray(new CompletableFuture[0]));
        allFutures.get(30, TimeUnit.SECONDS);

        // Verify all responses
        for (CompletableFuture<ResponseEntity<Map>> future : futures) {
            ResponseEntity<Map> response = future.get();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().get("service")).isEqualTo("heartbeat-microservice");
            assertThat(response.getBody().get("status")).isEqualTo("UP");
        }

        executor.shutdown();
    }

    @Test
    void statusEndpoint_ShouldHandleConcurrentRequests() throws Exception {
        int numberOfThreads = 10;
        int requestsPerThread = 5;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        List<CompletableFuture<ResponseEntity<Map>>> futures = new ArrayList<>();

        String url = "http://localhost:" + port + "/status";

        // Submit concurrent requests
        for (int i = 0; i < numberOfThreads * requestsPerThread; i++) {
            CompletableFuture<ResponseEntity<Map>> future = CompletableFuture.supplyAsync(() ->
                restTemplate.getForEntity(url, Map.class), executor);
            futures.add(future);
        }

        // Wait for all requests to complete
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
            futures.toArray(new CompletableFuture[0]));
        allFutures.get(30, TimeUnit.SECONDS);

        // Verify all responses
        for (CompletableFuture<ResponseEntity<Map>> future : futures) {
            ResponseEntity<Map> response = future.get();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
