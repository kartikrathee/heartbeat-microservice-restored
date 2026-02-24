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
- `nonExistentEndpoint_ShouldReturn404()` - Tests error handling for invalid endpoints
- `applicationShouldStartWithCorrectPort()` - Validates random port assignment

**Configuration**:

- Uses `RANDOM_PORT` to avoid port conflicts
- Real HTTP requests using `TestRestTemplate`
- Eureka client disabled for testing

### 4. Configuration Tests (`HeartbeatConfigurationTest`)

**Purpose**: Validate all application configuration properties.

**Tests**:

- `applicationName_ShouldBeConfiguredCorrectly()` - Validates service name
- `serverPort_ShouldBeConfiguredCorrectly()` - Validates server port configuration
- `eurekaServiceUrl_ShouldBeConfiguredCorrectly()` - Validates Eureka server URL
- `heartbeatInterval_ShouldBeConfiguredCorrectly()` - Validates 10-second heartbeat interval
- `leaseExpiration_ShouldBeConfiguredCorrectly()` - Validates 30-second lease expiration
- `preferIpAddress_ShouldBeConfiguredCorrectly()` - Validates IP preference setting
- `instanceId_ShouldBeConfiguredCorrectly()` - Validates instance ID format
- `managementEndpoints_ShouldBeConfiguredCorrectly()` - Validates exposed actuator endpoints
- `healthEndpointDetails_ShouldBeConfiguredCorrectly()` - Validates health endpoint details
- `heartbeatConfiguration_ShouldHaveOptimalValues()` - Validates heartbeat timing relationships

**Key Validations**:

- All Eureka configuration properties
- Heartbeat timing configuration
- Actuator endpoint configuration
- Optimal value relationships (expiration > 2x heartbeat interval)

### 5. Performance Tests (`HeartbeatPerformanceTest`)

**Purpose**: Validate application performance under load.

**Tests**:

- `homeEndpoint_ShouldHandleConcurrentRequests()` - Tests 50 concurrent requests to home endpoint
- `statusEndpoint_ShouldHandleConcurrentRequests()` - Tests 50 concurrent requests to status endpoint
- `endpoints_ShouldRespondWithinAcceptableTime()` - Validates response times < 1 second
- `endpoints_ShouldMaintainConsistentResponseTimes()` - Tests response time consistency
- `actuatorHealthEndpoint_ShouldHandleHighLoad()` - Tests 100 concurrent requests to health endpoint

**Performance Criteria**:

- Individual response time < 1 second
- Average response time < 500ms
- 95% success rate under high load
- Response time consistency (no response > 3x average)

## Test Configuration

### Test Properties (`application-test.properties`)

```properties
# Disable Eureka client during testing
eureka.client.enabled=false
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false

# Test server configuration
server.port=0  # Random port
spring.application.name=heartbeat-microservice-test

# Enhanced logging for debugging
logging.level.com.example.heartbeat=DEBUG
logging.level.org.springframework.web=DEBUG

# Additional actuator endpoints for testing
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always
management.endpoint.metrics.enabled=true
```

## Running Tests

### Individual Test Categories

```bash
# Run application tests
mvn test -Dtest="*HeartbeatMicroserviceApplicationTests"

# Run controller tests
mvn test -Dtest="*HealthControllerTest"

# Run integration tests
mvn test -Dtest="*HeartbeatIntegrationTest"

# Run configuration tests
mvn test -Dtest="*HeartbeatConfigurationTest"

# Run performance tests
mvn test -Dtest="*HeartbeatPerformanceTest"
```

### All Tests

```bash
# Run all tests
mvn test

# Run tests with detailed output
mvn test -Dtest.verbose=true

# Run tests and generate reports
mvn test surefire-report:report
```

## Test Coverage

The test suite provides comprehensive coverage of:

### Functional Coverage

- ✅ Application startup and context loading
- ✅ REST endpoint functionality
- ✅ JSON response structure validation
- ✅ Error handling (404 responses)
- ✅ Spring Boot Actuator integration

### Configuration Coverage

- ✅ Eureka client configuration
- ✅ Heartbeat timing configuration
- ✅ Server and application settings
- ✅ Actuator endpoint configuration
- ✅ Configuration value validation

### Integration Coverage

- ✅ Full HTTP request/response cycle
- ✅ Real server startup and shutdown
- ✅ Port binding and networking
- ✅ Cross-component integration

### Performance Coverage

- ✅ Concurrent request handling
- ✅ Response time validation
- ✅ Load testing (up to 100 concurrent requests)
- ✅ Performance consistency validation

## Validation Script Integration

The `run_and_validate.sh` script has been updated to work with this test suite:

### Script Features

- ✅ Project structure validation
- ✅ Maven and Java environment checks
- ✅ Compilation validation
- ✅ Test execution and reporting
- ✅ Feature validation (heartbeat configuration)
- ✅ Integration testing with real application startup
- ✅ Comprehensive reporting

### Script Usage

```bash
# Make script executable
chmod +x run_and_validate.sh

# Run full validation
./run_and_validate.sh

# View generated report
cat validation_report.txt
```

## Best Practices Implemented

### Test Organization

- Clear separation of concerns (unit, integration, performance)
- Descriptive test method names
- Proper test categorization

### Test Configuration

- Isolated test environment
- Disabled external dependencies (Eureka)
- Random port assignment to avoid conflicts

### Assertions

- Comprehensive response validation
- Both positive and negative test cases
- Performance criteria validation

### Error Handling

- Graceful handling of missing dependencies
- Proper cleanup of resources
- Clear error messages

## Continuous Integration

The test suite is designed to work well in CI/CD environments:

- No external dependencies required
- Fast execution (< 2 minutes total)
- Clear pass/fail criteria
- Detailed reporting
- Proper resource cleanup

## Troubleshooting

### Common Issues

1. **Port Conflicts**: Tests use random ports to avoid conflicts
2. **Eureka Connection**: Disabled in test configuration
3. **Timing Issues**: Adequate wait times for application startup
4. **Resource Cleanup**: Automatic cleanup in test teardown

### Debug Mode

Enable debug logging by adding to test configuration:

```properties
logging.level.com.example.heartbeat=DEBUG
logging.level.org.springframework.test=DEBUG
```

## Future Enhancements

Potential improvements to the test suite:

1. **Code Coverage Reports**: Integration with JaCoCo
2. **Contract Testing**: API contract validation
3. **Security Testing**: Authentication and authorization tests
4. **Database Testing**: If persistence is added
5. **Monitoring Tests**: Metrics and monitoring validation

This comprehensive test suite ensures the heartbeat microservice is thoroughly validated across all functional, integration, configuration, and performance aspects.
