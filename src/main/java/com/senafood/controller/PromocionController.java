package com.senafood.controller;

import com.senafood.model.Promocion;
import com.senafood.service.PromocionService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/promociones") // Ruta base para el módulo
public class PromocionController {

    @Autowired
    private PromocionService promocionService;

    // --- 1. Listar (Read) - URL: /promociones ---
    @GetMapping
    public String listarPromociones(Model model) {
        model.addAttribute("listaPromociones", promocionService.listarTodas());
        // Retorna la vista: templates/promocion/list.html
        return "promocion/list"; 
    }

    // --- 2. Mostrar formulario de Creación/Edición - URL: /promociones/form ---
    @GetMapping("/form")
    public String mostrarFormulario(Model model, @RequestParam(required = false) Integer id) {
        Promocion promocion = new Promocion();

        if (id != null) {
            promocion = promocionService.buscarPorId(id).orElse(new Promocion());
        }
        
        model.addAttribute("promocion", promocion);
        model.addAttribute("productos", promocionService.listarTodosLosProductos()); 
        
        // Retorna la vista: templates/promocion/form.html
        return "promocion/form";
    }

    // --- 3. Guardar (Create/Update) - URL: /promociones/guardar (POST) ---
    @PostMapping("/guardar")
    public String guardarPromocion(@Valid @ModelAttribute("promocion") Promocion promocion, 
                                BindingResult result, 
                                Model model,
                                RedirectAttributes redirect) {
        
        // 1. Verificar errores de Bean Validation (@DecimalMin, @FutureOrPresent, etc.)
        if (result.hasErrors()) {
            // Si hay errores, volvemos al formulario para mostrar los mensajes de error
            model.addAttribute("productos", promocionService.listarTodosLosProductos());
            // Si es un producto nuevo, en el formulario el id_producto podría ser nulo.
            // Necesitas el producto para que no falle la preselección en el select.
            if (promocion.getProducto() == null) {
                // Crear un Producto dummy para evitar errores si el campo se perdió
                // Esto depende de cómo manejes el select en tu formulario, pero es una precaución.
                // Es mejor asegurarse de que el producto ID no se pierda en el formulario.
            }
            return "promocion/form";
        }

        // 2. Validación de lógica de negocio: Fecha Fin debe ser posterior a Fecha Inicio
        if (promocion.getFechaFin() != null && promocion.getFechaInicio() != null && 
            promocion.getFechaFin().isBefore(promocion.getFechaInicio())) {
            
            // Agregamos un error manual al BindingResult para que se muestre en el formulario
            result.rejectValue("fechaFin", "error.promocion", "La fecha fin debe ser posterior a la fecha de inicio.");
            model.addAttribute("productos", promocionService.listarTodosLosProductos());
            return "promocion/form";
        }
        
        // 3. Si todo es correcto, guardar la promoción
        promocionService.guardar(promocion);
        redirect.addFlashAttribute("success", "Promoción guardada exitosamente!");
        return "redirect:/promociones"; 
    }

    // --- 4. Eliminar (Delete) - URL: /promociones/eliminar/{id} ---
    @GetMapping("/eliminar/{id}")
    public String eliminarPromocion(@PathVariable Integer id, RedirectAttributes redirect) {
        try {
            promocionService.eliminar(id);
            redirect.addFlashAttribute("success", "Promoción eliminada correctamente!");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "No se pudo eliminar la promoción. Asegúrate de que no tenga dependencias.");
        }
        return "redirect:/promociones";
    }
}