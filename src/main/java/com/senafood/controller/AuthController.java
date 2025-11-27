package com.senafood.controller;

import com.senafood.model.User;
import com.senafood.service.UserServiceImpl;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

// Asegúrate de que tu IDE tenga estos imports si usaste loggers antes
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;


@Controller
public class AuthController {

    private final UserServiceImpl userService;

    public AuthController(UserServiceImpl userService) {
        this.userService = userService;
    }

    // =============================
    //      RUTAS GET (VISTAS)
    // =============================

    // La ruta de inicio (/) se ha ELIMINADO de este controlador 
    // porque ahora está en MainController.java para evitar conflictos.

    // Vista de registro
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        // Asegura que el objeto User esté siempre disponible para el formulario Thymeleaf
        model.addAttribute("user", new User());
        return "register"; // templates/register.html
    }

    // Vista de login
    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // templates/login.html
    }

    // =============================
    //      PROCESAR REGISTRO POST
    // =============================

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, Model model) {

        // 1. Validar si el email ya existe
        if (userService.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("registrationError", "Ya existe una cuenta registrada con ese email.");
            return "register";
        }

        try {
            // 2. Guardar usuario con contraseña encriptada y rol asignado (en UserServiceImpl)
            userService.save(user);

            // 3. Redirigir al login con mensaje de éxito
            return "redirect:/login?success";

        } catch (DataIntegrityViolationException e) {
            // Captura errores de restricción de DB (ej. un campo NOT NULL vacío)
            // Se recomienda usar el mensaje localizado para dar más detalles al usuario.
            model.addAttribute("registrationError", "Error al guardar los datos. Verifique la información: " + e.getLocalizedMessage());
            return "register";

        } catch (RuntimeException e) {
            // 🚨 CRÍTICO: Captura la excepción de Rol no encontrado del UserService
            // y usa e.getMessage() para devolver el mensaje exacto a la vista.
            model.addAttribute("registrationError", e.getMessage()); 
            return "register";
        }
    }
}