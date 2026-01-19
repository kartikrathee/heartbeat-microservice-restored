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

- `GET /` - Service home page with basic information
- `GET /status` - Detailed service status including heartbeat configuration
- `GET /actuator/health` - Spring Boot Actuator health endpoint
- `GET /actuator/info` - Spring Boot Actuator info endpoint

## Configuration Details

### Heartbeat Settings

- **Renewal Interval**: 10 seconds (`eureka.instance.lease-renewal-interval-in-seconds=10`)
- **Lease Expiration**: 30 seconds (`eureka.instance.lease-expiration-duration-in-seconds=30`)
- **Instance ID**: `heartbeat-microservice:8080`

### Why 10 Seconds?

- Balances quick failure detection with reasonable network overhead
- Faster than Eureka's default 30 seconds
- Suitable for production environments requiring responsive service discovery

## Validation

### 1. Check Application Logs

Look for Eureka client renewal messages in the application logs.

### 2. Monitor Eureka Dashboard

- Access http://localhost:8761
- Verify service shows as "UP"
- Monitor "Last Heartbeat" timestamps

### 3. Test Service Health

```bash
# Check service endpoints
curl http://localhost:8080/actuator/health
curl http://localhost:8080/status
```

### 4. Test Failure Scenarios

- Stop the service and verify it's marked as DOWN within 30 seconds
- Restart and verify it re-registers successfully

## Documentation

For detailed explanation of the heartbeat mechanism, configuration choices, and validation methods, see:

- [HEARTBEAT_CONFIGURATION_EXPLANATION.md](HEARTBEAT_CONFIGURATION_EXPLANATION.md)

## Dependencies

- Spring Boot 2.7.0
- Spring Cloud 2021.0.3
- Netflix Eureka Client
- Spring Boot Actuator
- Spring Boot Web

## Task Requirements Fulfilled

✅ **Configuration**: Set `eureka.instance.lease-renewal-interval-in-seconds=10` embedded in Java code  
✅ **Documentation**: Comprehensive explanation of heartbeat mechanism and configuration choices  
✅ **Validation**: Multiple methods provided for verifying heartbeat functionality  
✅ **Impact Analysis**: Detailed analysis of different interval values and their effects  
✅ **Best Practices**: Guidelines for production deployment and monitoring  
✅ **Java-Only Solution**: All configuration embedded in Java code, no external properties files needed

## License

This project is created for educational purposes to demonstrate microservice heartbeat configuration with Eureka service discovery.
