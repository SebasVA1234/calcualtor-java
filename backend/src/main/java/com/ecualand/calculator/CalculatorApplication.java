package com.ecualand.calculator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Spring Boot application. When run, this class
 * bootstraps the application context and starts the embedded web server.
 */
@SpringBootApplication
public class CalculatorApplication{

    public static void main(String[] args) {
        SpringApplication.run(CalculatorApplication.class, args);
    }
}