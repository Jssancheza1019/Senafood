package com.senafood.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador que gestiona las rutas exclusivas para usuarios con el rol de Administrador.
 * La anotación @RequestMapping("/admin") define el prefijo de todas las URL en esta clase.
 * Spring Security ya protege este prefijo para requerir ROLE_ADMINISTRADOR.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    /**
     * Muestra la página principal del Panel de Administración.
     * Es accedida después del login exitoso por el CustomRole.java.
     * URL completa: /admin/panel
     * Retorna la plantilla: templates/admin/admin_panel.html
     */
    @GetMapping("/panel")
    public String adminPanel() {
        return "admin/admin_panel"; 
    }
}
