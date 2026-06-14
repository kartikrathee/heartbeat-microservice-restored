package com.example.heartbeat.config;

import com.example.heartbeat.HeartbeatMicroserviceApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = HeartbeatMicroserviceApplication.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "eureka.client.enabled=false",
    "eureka.client.register-with-eureka=false",
    "eureka.client.fetch-registry=false"
})
class HeartbeatConfigurationTest {

    @Autowired
    private Environment environment;

    @Test
    void applicationName_ShouldBeConfiguredCorrectly() {
        String applicationName = environment.getProperty("spring.application.name");
        assertThat(applicationName).isEqualTo("heartbeat-microservice");
    }

    @Test
    void serverPort_ShouldBeConfiguredCorrectly() {
        String serverPort = environment.getProperty("server.port");
        assertThat(serverPort).isEqualTo("8080");
    }

    @Test
    void eurekaServiceUrl_ShouldBeConfiguredCorrectly() {
        String eurekaServiceUrl = environment.getProperty("eureka.client.service-url.defaultZone");
        assertThat(eurekaServiceUrl).isEqualTo("http://localhost:8761/eureka/");
    }

    @Test
    void heartbeatInterval_ShouldBeConfiguredCorrectly() {
        String heartbeatInterval = environment.getProperty("eureka.instance.lease-renewal-interval-in-seconds");
        assertThat(heartbeatInterval).isEqualTo("10");
    }

    @Test
    void leaseExpiration_ShouldBeConfiguredCorrectly() {
        String leaseExpiration = environment.getProperty("eureka.instance.lease-expiration-duration-in-seconds");
        assertThat(leaseExpiration).isEqualTo("30");
    }
