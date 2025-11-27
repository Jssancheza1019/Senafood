// src/main/java/com/senafood/SenaFoodApplication.java

package com.senafood;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// 1. ANOTACIÓN CRÍTICA: Le dice a Spring Boot dónde empezar a buscar componentes.
@SpringBootApplication 
public class SenaFoodApplication {
    
    // 2. MÉTODO PRINCIPAL: El punto de entrada de la aplicación Java.
    public static void main(String[] args) {
        // Ejecuta la aplicación Spring Boot.
        SpringApplication.run(SenaFoodApplication.class, args);
    }
}