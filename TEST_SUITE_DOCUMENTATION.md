# Heartbeat Microservice - Test Suite Documentation

This document provides comprehensive documentation for the test suite created for the heartbeat microservice project.

## Test Suite Overview

The test suite consists of 5 main test categories covering different aspects of the application:

1. **Application Tests** - Basic application context and startup tests
2. **Controller Tests** - Unit tests for REST endpoints
3. **Integration Tests** - Full application integration testing
4. **Configuration Tests** - Validation of application configuration
5. **Performance Tests** - Load and performance testing

## Test Structure

```
src/test/java/com/example/heartbeat/
├── HeartbeatMicroserviceApplicationTests.java          # Application context tests
├── controller/
│   └── HealthControllerTest.java                       # Controller unit tests
├── integration/
│   └── HeartbeatIntegrationTest.java                   # Integration tests
├── config/
│   └── HeartbeatConfigurationTest.java                 # Configuration validation tests
└── performance/
    └── HeartbeatPerformanceTest.java                   # Performance and load tests

src/test/resources/
└── application-test.properties                         # Test configuration
```

## Test Categories

### 1. Application Tests (`HeartbeatMicroserviceApplicationTests`)

**Purpose**: Verify that the Spring Boot application context loads correctly.

**Tests**:

- `contextLoads()` - Ensures Spring application context loads without errors
- `applicationStartsSuccessfully()` - Verifies application can start without exceptions

**Configuration**:

- Disables Eureka client to avoid external dependencies during testing
- Uses test profile

### 2. Controller Tests (`HealthControllerTest`)

**Purpose**: Unit test the REST endpoints using MockMvc.

**Tests**:

- `homeEndpoint_ShouldReturnServiceInformation()` - Tests `/` endpoint response structure and content
- `statusEndpoint_ShouldReturnDetailedStatus()` - Tests `/status` endpoint with heartbeat configuration
- `homeEndpoint_ShouldReturnValidTimestamp()` - Validates timestamp field in home response
- `statusEndpoint_ShouldReturnValidTimestamp()` - Validates timestamp field in status response
- `homeEndpoint_ShouldHaveCorrectResponseStructure()` - Verifies exact response structure
- `statusEndpoint_ShouldHaveCorrectResponseStructure()` - Verifies exact response structure

**Key Validations**:

- Response status codes (200 OK)
- JSON response structure
- Required fields presence
- Heartbeat configuration values (10 seconds interval, 30 seconds expiration)

### 3. Integration Tests (`HeartbeatIntegrationTest`)

**Purpose**: Test the complete application with real HTTP requests.

**Tests**:

- `homeEndpoint_ShouldReturnCorrectResponse()` - Full HTTP test of home endpoint
- `statusEndpoint_ShouldReturnCorrectResponse()` - Full HTTP test of status endpoint
- `actuatorHealthEndpoint_ShouldBeAccessible()` - Tests Spring Boot Actuator health endpoint
- `actuatorInfoEndpoint_ShouldBeAccessible()` - Tests Spring Boot Actuator info endpoint
