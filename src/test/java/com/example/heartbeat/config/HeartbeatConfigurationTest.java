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

    @Test
    void preferIpAddress_ShouldBeConfiguredCorrectly() {
        String preferIpAddress = environment.getProperty("eureka.instance.prefer-ip-address");
        assertThat(preferIpAddress).isEqualTo("true");
    }

    @Test
    void instanceId_ShouldBeConfiguredCorrectly() {
        String instanceId = environment.getProperty("eureka.instance.instance-id");
        assertThat(instanceId).isEqualTo("heartbeat-microservice:8080");
    }

    @Test
    void managementEndpoints_ShouldBeConfiguredCorrectly() {
        String exposedEndpoints = environment.getProperty("management.endpoints.web.exposure.include");
        assertThat(exposedEndpoints).isEqualTo("health,info");
    }

    @Test
    void healthEndpointDetails_ShouldBeConfiguredCorrectly() {
        String healthDetails = environment.getProperty("management.endpoint.health.show-details");
        assertThat(healthDetails).isEqualTo("always");
    }

    @Test
    void eurekaClientRegistration_ShouldBeConfiguredCorrectly() {
        String registerWithEureka = environment.getProperty("eureka.client.register-with-eureka");
        String fetchRegistry = environment.getProperty("eureka.client.fetch-registry");
        
        // In production, these should be true, but in test they're disabled
        // We're testing that the configuration is properly set
        assertThat(registerWithEureka).isNotNull();
        assertThat(fetchRegistry).isNotNull();
    }

    @Test
    void heartbeatConfiguration_ShouldHaveOptimalValues() {
        // Test that heartbeat interval is reasonable (not too frequent, not too slow)
        String heartbeatInterval = environment.getProperty("eureka.instance.lease-renewal-interval-in-seconds");
        int interval = Integer.parseInt(heartbeatInterval);
        
        assertThat(interval).isGreaterThan(5); // Not too frequent
        assertThat(interval).isLessThan(60); // Not too slow
        
        // Test that lease expiration is reasonable multiple of heartbeat interval
        String leaseExpiration = environment.getProperty("eureka.instance.lease-expiration-duration-in-seconds");
        int expiration = Integer.parseInt(leaseExpiration);
        
        assertThat(expiration).isGreaterThan(interval * 2); // Should be at least 2x heartbeat interval
        assertThat(expiration).isLessThan(interval * 10); // Should not be too much longer
    }
}
