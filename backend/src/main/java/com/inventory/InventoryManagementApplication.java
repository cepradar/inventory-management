package com.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class InventoryManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(InventoryManagementApplication.class, args);
    }
}

// Root application class for the Spring Boot application.
// This initializes the application context and starts the embedded Tomcat server.
