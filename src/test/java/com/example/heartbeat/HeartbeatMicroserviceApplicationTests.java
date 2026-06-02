package com.example.heartbeat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "eureka.client.enabled=false",
    "eureka.client.register-with-eureka=false",
    "eureka.client.fetch-registry=false"
})
class HeartbeatMicroserviceApplicationTests {

    @Test
    void contextLoads() {
        // This test ensures that the Spring application context loads successfully
        // without any configuration errors
    }

    @Test
    void applicationStartsSuccessfully() {
        // This test verifies that the application can start without throwing exceptions
        // The @SpringBootTest annotation ensures the full application context is loaded
    }
}
