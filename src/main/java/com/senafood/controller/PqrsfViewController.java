package com.senafood.controller;

import java.util.List;
import java.util.Optional;

import com.senafood.model.PQRSF;
import com.senafood.model.User; 
import com.senafood.service.PQRSFService;
import com.senafood.service.UserServiceImpl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public PqrsfViewController(PQRSFService pqrsfService, UserServiceImpl userServiceImpl) {
        this.pqrsfService = pqrsfService;
        this.userServiceImpl = userServiceImpl;
    }

    // 1. Lista para ADMIN con paginación
    @GetMapping
    public String listPQRSF(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());

        Page<PQRSF> pqrsfPage = pqrsfService.obtenerTodos(pageable);

        model.addAttribute("pqrsfList", pqrsfPage.getContent());
        model.addAttribute("page", pqrsfPage);
        model.addAttribute("totalPages", pqrsfPage.getTotalPages());
        model.addAttribute("currentPage", page);

        return "pqrsf/list";
    }

    // 2. Lista para CLIENTE con sus propias solicitudes
    @GetMapping("/mis-solicitudes")
    public String listarPqrsfCliente(Model model, @AuthenticationPrincipal User userLogueado) {

        if (userLogueado == null) {
            return "redirect:/login";
        }

        List<PQRSF> pqrsfList = pqrsfService.findByUsuario(userLogueado);
        model.addAttribute("pqrsfList", pqrsfList);

        return "pqrsf/cliente/list";
    }

    // 3. Mostrar formulario de creación
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("pqrsf", new PQRSF());
        return "pqrsf/form";
    }

    // 4. Guardar nueva PQRSF
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

        return "redirect:/pqrsf/mis-solicitudes";
    }

    // 5. Ver detalle de una PQRSF
    @GetMapping("/{id}")
    public String viewPQRSF(
            @PathVariable Long id,
            Model model,
            @AuthenticationPrincipal User userLogueado,
            RedirectAttributes attributes) {

        Optional<PQRSF> pqrsfOptional = pqrsfService.findById(id);

        if (pqrsfOptional.isEmpty()) {
            attributes.addFlashAttribute("errorMessage", "Solicitud PQRSF no encontrada.");
            return "redirect:/pqrsf";
        }

        PQRSF pqrsf = pqrsfOptional.get();

        boolean isAdmin = userLogueado.getRol().getNombreRol().equals("Administrador");

        if (!isAdmin && !pqrsf.getUsuario().getIdUsuario().equals(userLogueado.getIdUsuario())) {
            attributes.addFlashAttribute("errorMessage", "Acceso denegado. No tienes permiso para ver esta solicitud.");
            return "redirect:/pqrsf/mis-solicitudes";
        }

        boolean debeGuardar = false;

        if (isAdmin) {
            if (!pqrsf.isLeida()) {
                pqrsf.setLeida(true);
                debeGuardar = true;
            }

            if ("CERRADO".equals(pqrsf.getEstado())) {
                pqrsf.setEstado("PENDIENTE");
                debeGuardar = true;
            }
        }

        if (debeGuardar) {
            pqrsfService.guardarPQRSF(pqrsf);
        }

        model.addAttribute("pqrsf", pqrsf);
        model.addAttribute("isAdmin", isAdmin);

        return "pqrsf/view";
    }
}
