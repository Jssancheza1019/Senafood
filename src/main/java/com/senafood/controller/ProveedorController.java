package com.senafood.controller;

import com.senafood.model.Proveedor;
import com.senafood.service.ProveedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

/**
 * Controlador para manejar las vistas y operaciones CRUD de Proveedores.
 * Mapea la ruta base /proveedores
 */
@Controller
@RequestMapping("/proveedor")
public class ProveedorController {

    @Autowired
    private ProveedorService proveedorService;

    /**
     * Muestra la vista principal con la tabla de proveedores. (READ)
     * Mapea a src/main/resources/templates/proveedor/view.html (basado en tu estructura de carpetas)
     */
    @GetMapping
    public String listarProveedores(Model model) {
        List<Proveedor> proveedores = proveedorService.findAll();
        model.addAttribute("proveedor", proveedores);
        // El nombre de la plantilla es "proveedor/view"
        return "proveedor/view";
    }

    /**
     * Muestra el formulario para crear un nuevo proveedor. (CREATE)
     * Mapea a src/main/resources/templates/proveedor/form.html
     */
    @GetMapping("/form")
    public String mostrarFormulario(Model model) {
        model.addAttribute("proveedor", new Proveedor());
        model.addAttribute("titulo", "Crear Nuevo Proveedor");
        return "proveedor/form";
    }

    /**
     * Muestra el formulario para editar un proveedor existente. (UPDATE - Parte 1)
     */
    @GetMapping("/form/{id}")
    public String editarProveedor(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Proveedor> proveedor = proveedorService.findById(id);

        if (proveedor.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Proveedor no encontrado.");
            return "redirect:/proveedor";
        }
        
        model.addAttribute("proveedor", proveedor.get());
        model.addAttribute("titulo", "Editar Proveedor N° " + id);
        return "proveedor/form";
    }

    /**
     * Guarda el proveedor enviado desde el formulario. (CREATE/UPDATE - Parte 2)
     */
    @PostMapping("/save")
    public String guardarProveedor(Proveedor proveedor, RedirectAttributes redirectAttributes) {
        proveedorService.save(proveedor);
        redirectAttributes.addFlashAttribute("success", "Proveedor guardado exitosamente.");
        return "redirect:/proveedor";
    }

    /**
     * Elimina un proveedor. (DELETE)
     */
    @GetMapping("/delete/{id}")
    public String eliminarProveedor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            proveedorService.deleteById(id);
            redirectAttributes.addFlashAttribute("warning", "Proveedor eliminado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el proveedor.");
        }
        return "redirect:/proveedor";
    }
}