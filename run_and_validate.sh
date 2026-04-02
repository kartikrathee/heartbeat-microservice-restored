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
        print_error "Compilation failed"
        exit 1
    fi
}

# Function to compile test sources
compile_tests() {
    print_status "Compiling test sources..."
    if mvn test-compile -q; then
        print_success "Test sources compiled successfully"
    else
        print_error "Test compilation failed"
        exit 1
    fi
}

# Function to run tests
run_tests() {
    print_status "Running tests..."
    
    # Run tests and capture output
    if mvn test -q > test_output.log 2>&1; then
        print_success "All tests passed"

        # Extract test results from Surefire reports
        for report in target/surefire-reports/TEST-*.xml; do
            if [[ -f "$report" ]]; then
                test_count=$(grep -o 'tests="[0-9]*"' "$report" | grep -o '[0-9]*')
                failures=$(grep -o 'failures="[0-9]*"' "$report" | grep -o '[0-9]*')
                errors=$(grep -o 'errors="[0-9]*"' "$report" | grep -o '[0-9]*')
                test_file=$(basename "$report" .xml | sed 's/TEST-//')
                print_success "Test Results ($test_file): $test_count tests run, $failures failures, $errors errors"
            fi
        done
    else
        print_error "Tests failed"
        echo "Test output:"
        cat test_output.log
        exit 1
    fi
}

# Function to validate test coverage
validate_test_coverage() {
    print_status "Validating test coverage..."
    
    # Check if test classes exist
    test_classes=(
        "target/test-classes/com/example/heartbeat/HeartbeatMicroserviceApplicationTests.class"
        "target/test-classes/com/example/heartbeat/controller/HealthControllerTest.class"
        "target/test-classes/com/example/heartbeat/integration/HeartbeatIntegrationTest.class"
        "target/test-classes/com/example/heartbeat/config/HeartbeatConfigurationTest.class"
        "target/test-classes/com/example/heartbeat/performance/HeartbeatPerformanceTest.class"
    )
    
    test_classes_found=0
    for test_class in "${test_classes[@]}"; do
        if [[ -f "$test_class" ]]; then
            test_classes_found=$((test_classes_found + 1))
        fi
    done
    
    if [[ $test_classes_found -eq ${#test_classes[@]} ]]; then
        print_success "All test classes found and compiled ($test_classes_found/${#test_classes[@]})"
    else
        print_warning "Some test classes not found ($test_classes_found/${#test_classes[@]} found)"
    fi

    # Check if main classes exist
    main_classes=(
        "target/classes/com/example/heartbeat/HeartbeatMicroserviceApplication.class"
        "target/classes/com/example/heartbeat/controller/HealthController.class"
    )
    
    main_classes_found=0
    for main_class in "${main_classes[@]}"; do
        if [[ -f "$main_class" ]]; then
            main_classes_found=$((main_classes_found + 1))
        fi
    done
    
    if [[ $main_classes_found -eq ${#main_classes[@]} ]]; then
        print_success "All main classes found and compiled ($main_classes_found/${#main_classes[@]})"
    else
        print_error "Main classes not found ($main_classes_found/${#main_classes[@]} found)"
        exit 1
    fi
}

# Function to run dependency check
check_dependencies() {
    print_status "Checking project dependencies..."
    
    if mvn dependency:resolve -q > /dev/null 2>&1; then
        print_success "All dependencies resolved successfully"
    else
        print_error "Failed to resolve dependencies"
        exit 1
    fi
}

# Function to validate specific test categories
validate_test_categories() {
    print_status "Validating test categories..."
