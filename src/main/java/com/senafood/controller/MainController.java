package com.senafood.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import java.security.Principal; // Necesario para obtener el usuario logueado

@Controller
public class MainController {

    // 1. Mapeo de la Landing Page (Usuarios no logueados)
    @GetMapping("/")
    public String home() {
        return "index"; 
    }
    
    // 2. Mapeo del Dashboard (Usuarios logueados)
    @GetMapping("/dashboard")
    public String userDashboard(Principal principal) {
        // La vista de Thymeleaf buscará src/main/resources/templates/dashboard.html
        return "dashboard"; 
    }
    
}