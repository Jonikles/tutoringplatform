package com.tutoringplatform;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Tutoring Platform Test Suite")
@SelectPackages({
        "com.tutoringplatform.command",
        "com.tutoringplatform.controllers",
        "com.tutoringplatform.models",
        "com.tutoringplatform.observer",
        "com.tutoringplatform.repositories.impl",
        "com.tutoringplatform.services"
})
public class TestSuiteRunner {
    // This class remains empty. It is used only as a holder for the above
    // annotations.
}

/**
 * To run all tests: 
 * mvn test
 * 
 * To run specific test class:
 * mvn test -Dtest=StudentServiceTest
 * 
 * To run tests with coverage:
 * mvn test jacoco:report
 * 
 * To run only unit tests (exclude integration):
 * mvn test -Dgroups="unit"
 * 
 * To run only integration tests:
 * mvn test -Dgroups="integration"
 */