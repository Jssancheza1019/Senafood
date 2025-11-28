// Archivo: src/main/java/com/senafood/controller/PqrsfViewController.java

package com.senafood.controller;

import com.senafood.model.PQRSF;
import com.senafood.model.User; // ¡IMPORTAR ESTO!
import com.senafood.service.PQRSFService;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // ¡IMPORTAR ESTO!
import org.springframework.stereotype.Controller; 
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller 
@RequestMapping("/pqrsf")
public class PqrsfViewController {

    private final PQRSFService pqrsfService;

    public PqrsfViewController(PQRSFService pqrsfService) {
        this.pqrsfService = pqrsfService;
    }

    // ... (Métodos listPQRSF y showCreateForm)

    // 1. Muestra la lista
    @GetMapping
    public String listPQRSF(Model model) {
        model.addAttribute("pqrsfList", pqrsfService.obtenerTodos());
        return "pqrsf/list"; 
    }

    // 2. Muestra el formulario de creación
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("pqrsf", new PQRSF()); 
        return "pqrsf/form"; 
    }

    // 3. Procesa el formulario (POST a /pqrsf/save) - CORRECCIÓN CLAVE
    @PostMapping("/save")
    public String savePQRSF(
            @AuthenticationPrincipal User userLogueado, 
            @ModelAttribute("pqrsf") PQRSF pqrsf, 
            RedirectAttributes attributes) {
        
        if (userLogueado == null) {
             attributes.addFlashAttribute("errorMessage", "Debe iniciar sesión para registrar una solicitud.");
             return "redirect:/login"; 
        }
        
        // CORRECCIÓN: Asignar el OBJETO User completo, no el ID, para la relación JPA
        pqrsf.setUsuario(userLogueado); 

        pqrsfService.guardarPQRSF(pqrsf);
        attributes.addFlashAttribute("successMessage", "Solicitud PQRSF registrada con éxito!");
        return "redirect:/pqrsf"; 
    }
    
    // 4. Muestra el detalle
    @GetMapping("/{id}")
    public String viewPQRSF(@PathVariable Long id, Model model, RedirectAttributes attributes) {
        return pqrsfService.findById(id)
            .map(pqrsf -> {
                model.addAttribute("pqrsf", pqrsf);
                return "pqrsf/view"; 
            })
            .orElseGet(() -> {
                attributes.addFlashAttribute("errorMessage", "Solicitud PQRSF no encontrada.");
                return "redirect:/pqrsf";
            });
    }
}