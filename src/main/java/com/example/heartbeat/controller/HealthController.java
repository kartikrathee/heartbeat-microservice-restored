package com.example.heartbeat.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "heartbeat-microservice");
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "Microservice is running and sending heartbeats to Eureka");
        return response;
    }

    @GetMapping("/status")
    public Map<String, Object> status() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "heartbeat-microservice");
        response.put("status", "HEALTHY");
        response.put("heartbeat_interval", "10 seconds");
        response.put("lease_expiration", "30 seconds");
        response.put("timestamp", LocalDateTime.now());
        return response;
    }
}
