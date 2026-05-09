package com.example.heartbeat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableEurekaClient
public class HeartbeatMicroserviceApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(HeartbeatMicroserviceApplication.class);
        
        // Programmatically set properties instead of using application.properties
        app.setDefaultProperties(getDefaultProperties());
        
        app.run(args);
    }
    
    /**
     * Define all application properties programmatically
     * This eliminates the need for application.properties file
     */
    private static Map<String, Object> getDefaultProperties() {
        Map<String, Object> properties = new HashMap<>();
        
        // Spring Boot Application Configuration
        properties.put("spring.application.name", "heartbeat-microservice");
        properties.put("server.port", "8080");
        
        // Eureka Client Configuration
        properties.put("eureka.client.service-url.defaultZone", "http://localhost:8761/eureka/");
        properties.put("eureka.client.register-with-eureka", "true");
        properties.put("eureka.client.fetch-registry", "true");
        
        // Heartbeat Configuration - KEY PROPERTY
        // Controls how often (in seconds) the microservice sends its heartbeat to the Eureka server
        properties.put("eureka.instance.lease-renewal-interval-in-seconds", "10");
        
        // Additional Eureka Instance Configuration
        properties.put("eureka.instance.lease-expiration-duration-in-seconds", "30");
        properties.put("eureka.instance.prefer-ip-address", "true");
        properties.put("eureka.instance.instance-id", "heartbeat-microservice:8080");
        
        // Health Check Configuration
        properties.put("management.endpoints.web.exposure.include", "health,info");
        properties.put("management.endpoint.health.show-details", "always");
        
        return properties;
    }
}
