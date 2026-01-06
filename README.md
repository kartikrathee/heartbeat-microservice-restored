# Heartbeat Microservice - Service Registry Configuration

This project demonstrates the configuration of heartbeat mechanisms for a Spring Boot microservice that registers with Netflix Eureka service discovery server.

## Project Structure

```
heartbeat-microservice/
├── src/
│   └── main/
│       └── java/
│           └── com/example/heartbeat/
│               ├── HeartbeatMicroserviceApplication.java (contains embedded configuration)
│               └── controller/
│                   └── HealthController.java
├── pom.xml
├── HEARTBEAT_CONFIGURATION_EXPLANATION.md
└── README.md
```

## Key Configuration

The main heartbeat configuration is embedded directly in the Java code within `HeartbeatMicroserviceApplication.java`:

```java
// Heartbeat Configuration - KEY PROPERTY
// Controls how often (in seconds) the microservice sends its heartbeat to the Eureka server
properties.put("eureka.instance.lease-renewal-interval-in-seconds", "10");
```

This property controls how often (every 10 seconds) the microservice sends a "still alive" signal to the Eureka registry. All configuration is now embedded in Java code, eliminating the need for external configuration files.

## Quick Start

### Prerequisites

- Java 11 or higher
- Maven 3.6+
- Running Eureka Server (default: http://localhost:8761)

### Running the Application

1. **Start Eureka Server** (if not already running):

   ```bash
   # You'll need a separate Eureka server running on port 8761
   # This can be downloaded from Spring Cloud samples or created separately
   ```

2. **Build and run the microservice**:

   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

3. **Verify the service is running**:

   ```bash
   curl http://localhost:8080/
   curl http://localhost:8080/status
   ```

4. **Check Eureka Dashboard**:
   - Open http://localhost:8761 in your browser
   - Verify that `HEARTBEAT-MICROSERVICE` appears in the registered instances

## Endpoints

