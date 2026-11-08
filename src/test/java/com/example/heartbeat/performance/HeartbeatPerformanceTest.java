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
            assertThat(response.getBody().get("service")).isEqualTo("heartbeat-microservice");
            assertThat(response.getBody().get("status")).isEqualTo("HEALTHY");
            assertThat(response.getBody().get("heartbeat_interval")).isEqualTo("10 seconds");
        }

        executor.shutdown();
    }

    @Test
    void endpoints_ShouldRespondWithinAcceptableTime() {
        String homeUrl = "http://localhost:" + port + "/";
        String statusUrl = "http://localhost:" + port + "/status";

        // Test home endpoint response time
        Instant start = Instant.now();
        ResponseEntity<Map> homeResponse = restTemplate.getForEntity(homeUrl, Map.class);
        Duration homeResponseTime = Duration.between(start, Instant.now());

        assertThat(homeResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(homeResponseTime.toMillis()).isLessThan(1000); // Should respond within 1 second

        // Test status endpoint response time
        start = Instant.now();
        ResponseEntity<Map> statusResponse = restTemplate.getForEntity(statusUrl, Map.class);
        Duration statusResponseTime = Duration.between(start, Instant.now());

        assertThat(statusResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(statusResponseTime.toMillis()).isLessThan(1000); // Should respond within 1 second
    }

    @Test
    void endpoints_ShouldMaintainConsistentResponseTimes() {
        String url = "http://localhost:" + port + "/";
        List<Long> responseTimes = new ArrayList<>();

        // Make multiple requests and measure response times
        for (int i = 0; i < 20; i++) {
            Instant start = Instant.now();
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            Duration responseTime = Duration.between(start, Instant.now());
            
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            responseTimes.add(responseTime.toMillis());
        }

        // Calculate average response time
        double averageResponseTime = responseTimes.stream()
            .mapToLong(Long::longValue)
            .average()
            .orElse(0.0);

        // Verify response times are consistent (no response should be more than 3x the average)
        for (Long responseTime : responseTimes) {
            assertThat(responseTime).isLessThan((long) (averageResponseTime * 3));
        }

        // Verify average response time is reasonable
        assertThat(averageResponseTime).isLessThan(500); // Average should be less than 500ms
    }

    @Test
    void actuatorHealthEndpoint_ShouldHandleHighLoad() throws Exception {
        int numberOfRequests = 100;
        ExecutorService executor = Executors.newFixedThreadPool(20);
        List<CompletableFuture<ResponseEntity<Map>>> futures = new ArrayList<>();

        String url = "http://localhost:" + port + "/actuator/health";

        // Submit many concurrent requests to health endpoint
        for (int i = 0; i < numberOfRequests; i++) {
            CompletableFuture<ResponseEntity<Map>> future = CompletableFuture.supplyAsync(() ->
                restTemplate.getForEntity(url, Map.class), executor);
            futures.add(future);
        }

        // Wait for all requests to complete
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
            futures.toArray(new CompletableFuture[0]));
        allFutures.get(60, TimeUnit.SECONDS);

        // Verify all responses
        int successCount = 0;
        for (CompletableFuture<ResponseEntity<Map>> future : futures) {
            ResponseEntity<Map> response = future.get();
            if (response.getStatusCode() == HttpStatus.OK) {
                successCount++;
                assertThat(response.getBody().get("status")).isEqualTo("UP");
            }
        }

        // At least 95% of requests should succeed
        assertThat(successCount).isGreaterThanOrEqualTo((int) (numberOfRequests * 0.95));

        executor.shutdown();
    }
}
