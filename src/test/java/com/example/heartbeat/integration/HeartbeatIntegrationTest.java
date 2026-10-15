package com.example.heartbeat.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "eureka.client.enabled=false",
    "eureka.client.register-with-eureka=false",
    "eureka.client.fetch-registry=false"
})
class HeartbeatIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void homeEndpoint_ShouldReturnCorrectResponse() {
        String url = "http://localhost:" + port + "/";
        
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("service")).isEqualTo("heartbeat-microservice");
        assertThat(response.getBody().get("status")).isEqualTo("UP");
        assertThat(response.getBody().get("message")).isEqualTo("Microservice is running and sending heartbeats to Eureka");
        assertThat(response.getBody().get("timestamp")).isNotNull();
    }

    @Test
    void statusEndpoint_ShouldReturnCorrectResponse() {
        String url = "http://localhost:" + port + "/status";
        
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("service")).isEqualTo("heartbeat-microservice");
        assertThat(response.getBody().get("status")).isEqualTo("HEALTHY");
        assertThat(response.getBody().get("heartbeat_interval")).isEqualTo("10 seconds");
        assertThat(response.getBody().get("lease_expiration")).isEqualTo("30 seconds");
        assertThat(response.getBody().get("timestamp")).isNotNull();
    }

    @Test
    void actuatorHealthEndpoint_ShouldBeAccessible() {
        String url = "http://localhost:" + port + "/actuator/health";
        
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo("UP");
    }

    @Test
    void actuatorInfoEndpoint_ShouldBeAccessible() {
        String url = "http://localhost:" + port + "/actuator/info";
        
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        // Info endpoint should be accessible (may return empty object if no info configured)
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void nonExistentEndpoint_ShouldReturn404() {
        String url = "http://localhost:" + port + "/nonexistent";
        
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void applicationShouldStartWithCorrectPort() {
        assertThat(port).isGreaterThan(0);
        assertThat(port).isNotEqualTo(8080); // Should be random port for testing
    }
}
