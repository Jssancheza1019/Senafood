package com.senafood.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import java.security.Principal; // Necesario para obtener el usuario logueado

@Controller
public class MainController {

    //  Mapeo de la Landing Page (Usuarios no logueados)
    @GetMapping("/")
    public String home() {
        return "index"; 
    }
    
    // Mapeo del Dashboard (Usuarios logueados)
    @GetMapping("/dashboard")
    public String userDashboard(Principal principal) {
        // La vista de Thymeleaf buscará src/main/resources/templates/dashboard.html
        return "dashboard"; 
    }

    @GetMapping("/admin_panel")
    public String showAdminPanel() {
        // Asegúrate de que tienes un archivo llamado admin_panel.html 
        // en src/main/resources/templates/
        return "admin/admin_panel"; 
    }
    
}