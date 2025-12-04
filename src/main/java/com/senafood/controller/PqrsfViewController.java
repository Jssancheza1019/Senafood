package com.senafood.controller;

import java.security.Principal; 
import java.util.List;
import java.util.Optional;

import com.senafood.model.PQRSF;
import com.senafood.model.User; 
import com.senafood.service.PQRSFService;
import com.senafood.service.UserServiceImpl; 

import org.springframework.security.core.annotation.AuthenticationPrincipal; 
import org.springframework.stereotype.Controller; 
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller 
@RequestMapping("/pqrsf")
public class PqrsfViewController {

    private final PQRSFService pqrsfService;
    private final UserServiceImpl userServiceImpl; 

    // Constructor que inyecta ambos servicios
    public PqrsfViewController(PQRSFService pqrsfService, UserServiceImpl userServiceImpl) {
        this.pqrsfService = pqrsfService;
        this.userServiceImpl = userServiceImpl;
    }

    // 1. Muestra la lista del ADMINISTRADOR (ruta base /pqrsf)
    @GetMapping
    public String listPQRSF(Model model) {
        model.addAttribute("pqrsfList", pqrsfService.obtenerTodos());
        return "pqrsf/list"; 
    }
    
    // ⭐ 2. MÉTODO NUEVO: Listado de solicitudes filtradas para el Cliente ⭐
    @GetMapping("/mis-solicitudes")
    public String listarPqrsfCliente(Model model, @AuthenticationPrincipal User userLogueado) {
        
        if (userLogueado == null) {
            return "redirect:/login"; 
        }
        
        // Llama al servicio para obtener solo las PQRSF del usuario logueado
        List<PQRSF> pqrsfList = pqrsfService.findByUsuario(userLogueado);
        
        model.addAttribute("pqrsfList", pqrsfList);
        
        // ⭐ ¡CORRECCIÓN CLAVE DE TEMPLATE! Ahora busca 'list.html' en la carpeta 'cliente' ⭐
        return "pqrsf/cliente/list"; 
    }

    // 3. Muestra el formulario de creación
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("pqrsf", new PQRSF()); 
        return "pqrsf/form"; 
    }

    // 4. Procesa el formulario
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
        
        // Redirige al cliente a su lista filtrada después de crear
        return "redirect:/pqrsf/mis-solicitudes"; 
    }
    
    // 5. Muestra el detalle (con verificación de seguridad)
    @GetMapping("/{id}")
    public String viewPQRSF(@PathVariable Long id, Model model, @AuthenticationPrincipal User userLogueado, RedirectAttributes attributes) {
        
        Optional<PQRSF> pqrsfOptional = pqrsfService.findById(id);
        
        if (pqrsfOptional.isEmpty()) {
            attributes.addFlashAttribute("errorMessage", "Solicitud PQRSF no encontrada.");
            return "redirect:/pqrsf";
        }
        
        PQRSF pqrsf = pqrsfOptional.get();

        // 1. Determinar si el usuario es un administrador
        boolean isAdmin = userLogueado.getRol().getNombreRol().equals("ADMIN"); 

        // 2. Si el usuario no es admin Y la PQRSF no le pertenece, denegar el acceso.
        if (!isAdmin && !pqrsf.getUsuario().getIdUsuario().equals(userLogueado.getIdUsuario())) {
            attributes.addFlashAttribute("errorMessage", "Acceso denegado. No tienes permiso para ver esta solicitud.");
            return "redirect:/pqrsf/mis-solicitudes"; 
        }
        
        boolean debeGuardar = false; 

        // LÓGICA DE MARCAR COMO LEÍDA 
        if (!pqrsf.isLeida()) {
            pqrsf.setLeida(true);
            debeGuardar = true; 
        }
        
        // LÓGICA DE CAMBIO DE CERRADO A PENDIENTE 
        if ("CERRADO".equals(pqrsf.getEstado())) {
            pqrsf.setEstado("PENDIENTE");
            debeGuardar = true; 
        }
        
        // PERSISTIR CAMBIOS
        if (debeGuardar) {
            pqrsfService.guardarPQRSF(pqrsf); 
        }

        model.addAttribute("pqrsf", pqrsf);
        return "pqrsf/view"; 
    }
}