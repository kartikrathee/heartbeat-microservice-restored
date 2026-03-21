#!/bin/bash
# Shell script to run and validate the Maven project and its test cases
# Project: heartbeat-microservice

set -e  # Exit on any error

# Color codes for output formatting
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_header() {
    echo -e "\n${BLUE}================================${NC}"
    echo -e "${BLUE} $1${NC}"
    echo -e "${BLUE}================================${NC}\n"
}

# Function to check if Maven is installed
check_maven() {
    print_status "Checking Maven installation..."
    if ! command -v mvn &> /dev/null; then
        print_error "Maven is not installed or not in PATH"
        print_error "Please install Maven and ensure it's in your PATH"
        exit 1
    fi
    
    mvn_version=$(mvn -version | head -n 1)
    print_success "Maven found: $mvn_version"
}

# Function to check Java version
check_java() {
    print_status "Checking Java installation..."
    if ! command -v java &> /dev/null; then
        print_error "Java is not installed or not in PATH"
        exit 1
    fi
    
    java_version=$(java -version 2>&1 | head -n 1)
    print_success "Java found: $java_version"
}

# Function to validate project structure
validate_project_structure() {
    print_status "Validating project structure..."
    
    required_files=(
        "pom.xml"
        "src/main/java/com/example/heartbeat/HeartbeatMicroserviceApplication.java"
        "src/main/java/com/example/heartbeat/controller/HealthController.java"
        "src/test/java/com/example/heartbeat/HeartbeatMicroserviceApplicationTests.java"
        "src/test/java/com/example/heartbeat/controller/HealthControllerTest.java"
        "src/test/java/com/example/heartbeat/integration/HeartbeatIntegrationTest.java"
        "src/test/java/com/example/heartbeat/config/HeartbeatConfigurationTest.java"
        "src/test/java/com/example/heartbeat/performance/HeartbeatPerformanceTest.java"
        "src/test/resources/application-test.properties"
    )

    for file in "${required_files[@]}"; do
        if [[ ! -f "$file" ]]; then
            print_error "Required file missing: $file"
            exit 1
        fi
    done

    print_success "Project structure validation passed"
}

# Function to clean the project
clean_project() {
    print_status "Cleaning project..."
    if mvn clean > /dev/null 2>&1; then
        print_success "Project cleaned successfully"
    else
        print_error "Failed to clean project"
        exit 1
    fi
}

# Function to compile the project
compile_project() {
    print_status "Compiling project..."
    if mvn compile -q; then
        print_success "Project compiled successfully"
    else
