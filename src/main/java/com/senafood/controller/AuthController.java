package com.senafood.controller;

import com.senafood.model.User;
import com.senafood.service.UserServiceImpl;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class AuthController {

    private final UserServiceImpl userService;

    public AuthController(UserServiceImpl userService) {
        this.userService = userService;
    }


    // La ruta de inicio (/) se ha ELIMINADO de este controlador 
    // MainController.java para evitar conflictos.

    // Vista de registro
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        // Asegura que el objeto User esté siempre disponible para el formulario Thymeleaf
        model.addAttribute("user", new User());
        return "register"; 
    }

    // Vista de login
    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; 
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, Model model) {

        //  Validar si el email ya existe
        if (userService.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("registrationError", "Ya existe una cuenta registrada con ese email.");
            return "register";
        }

        try {
            // Guarda usuario con contraseña encriptada y rol asignado 
            userService.save(user);

            //  Redirige al ogin con mensaje de éxito
            return "redirect:/login?success";

        } catch (DataIntegrityViolationException e) {
            // Captura errores de restricción de DB 
            model.addAttribute("registrationError", "Error al guardar los datos. Verifique la información: " + e.getLocalizedMessage());
            return "register";

        } catch (RuntimeException e) {
            // Captura la excepción de Rol no encontrado del UserService
            // y usa e.getMessage() para devolver el mensaje exacto a la vista.
            model.addAttribute("registrationError", e.getMessage()); 
            return "register";
        }
    }
}