package com.senafood.controller;

import java.util.Optional;
import com.senafood.model.PQRSF;
import com.senafood.model.User; 
import com.senafood.service.PQRSFService;
import org.springframework.security.core.annotation.AuthenticationPrincipal; 
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

    // Muestra la lista
    @GetMapping
    public String listPQRSF(Model model) {
        model.addAttribute("pqrsfList", pqrsfService.obtenerTodos());
        return "pqrsf/list"; 
    }

    // Muestra el formulario de creación
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("pqrsf", new PQRSF()); 
        return "pqrsf/form"; 
    }

    // Procesa el formulario (POST a /pqrsf/save) - CORRECCIÓN CLAVE
    @PostMapping("/save")
    public String savePQRSF(
            @AuthenticationPrincipal User userLogueado, 
            @ModelAttribute("pqrsf") PQRSF pqrsf, 
            RedirectAttributes attributes) {
        
        if (userLogueado == null) {
            attributes.addFlashAttribute("errorMessage", "Debe iniciar sesión para registrar una solicitud.");
            return "redirect:/login"; 
        }
        
        pqrsf.setUsuario(userLogueado); 

        pqrsfService.guardarPQRSF(pqrsf);
        attributes.addFlashAttribute("successMessage", "Solicitud PQRSF registrada con éxito!");
        return "redirect:/pqrsf"; 
    }
    
    // Muestra el detalle (¡AQUÍ DEBES HACER EL CAMBIO!)
    @GetMapping("/{id}")
public String viewPQRSF(@PathVariable Long id, Model model, RedirectAttributes attributes) {
    
    Optional<PQRSF> pqrsfOptional = pqrsfService.findById(id);
    
    if (pqrsfOptional.isEmpty()) {
        attributes.addFlashAttribute("errorMessage", "Solicitud PQRSF no encontrada.");
        return "redirect:/pqrsf";
    }
    
    PQRSF pqrsf = pqrsfOptional.get();

    boolean debeGuardar = false; // Bandera para saber si necesitamos guardar los cambios

    // ⭐ 1. LÓGICA DE MARCAR COMO LEÍDA (NUEVO CÓDIGO)
    if (!pqrsf.isLeida()) {
        pqrsf.setLeida(true);
        debeGuardar = true; // Debemos guardar porque el estado de lectura cambió
        attributes.addFlashAttribute("infoMessage", "La solicitud ha sido marcada como Leída.");
    }
    
    // ⭐ 2. LÓGICA DE CAMBIO DE CERRADO A PENDIENTE (EXISTENTE)
    if ("CERRADO".equals(pqrsf.getEstado())) {
        pqrsf.setEstado("PENDIENTE");
        debeGuardar = true; // Debemos guardar porque el estado de gestión cambió
        
        // Si ya hay un mensaje de info, podemos concatenarlo o simplemente dejar el nuevo
        if (!pqrsf.isLeida()) {
             attributes.addFlashAttribute("infoMessage", "El estado de la solicitud ha sido cambiado automáticamente a PENDIENTE.");
        }
    }
    
    // ⭐ 3. PERSISTIR CAMBIOS
    // Guardamos la PQRSF si cualquiera de los dos campos (estado o leida) cambió
    if (debeGuardar) {
        pqrsfService.guardarPQRSF(pqrsf); 
    }

    model.addAttribute("pqrsf", pqrsf);
    return "pqrsf/view"; 
}
}
