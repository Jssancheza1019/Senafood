package com.senafood.controller;

import com.senafood.model.Proveedor;
import com.senafood.service.ProveedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Controlador para la gestión de proveedores.
 * Mapea las peticiones HTTP a la lógica de la aplicación.
 */
@Controller
@RequestMapping("/proveedores")
public class ProveedorController {

    @Autowired
    private ProveedorService proveedorService;

    // Mapea a la ruta base /proveedores
    @GetMapping
    public String viewHomePage(Model model) {
        // CORRECCIÓN CLAVE: Llama al servicio para obtener la lista completa
        List<Proveedor> listaProveedores = proveedorService.getAllProveedores();
        
        // CORRECCIÓN CLAVE: Añade la lista al modelo con el nombre "listaProveedores"
        model.addAttribute("listaProveedores", listaProveedores);
        
        // Retorna el nombre de la plantilla Thymeleaf (debería ser view.html o list.html)
        // Usaremos 'proveedor/view' asumiendo que está en templates/proveedor/view.html
        return "proveedor/view"; 
    }

    // Muestra el formulario para un nuevo proveedor
    @GetMapping("/form")
    public String showForm(@RequestParam(required = false) Long id, Model model) {
        Proveedor proveedor;
        if (id != null) {
            // Caso de edición
            proveedor = proveedorService.getProveedorById(id);
        } else {
            // Caso de nuevo proveedor
            proveedor = new Proveedor();
        }
        model.addAttribute("proveedor", proveedor);
        return "proveedor/form";
    }

    // Maneja la acción de guardar un proveedor (nuevo o editado)
    @PostMapping("/save")
    public String saveProveedor(@ModelAttribute("proveedor") Proveedor proveedor) {
        // Guarda el proveedor a través del servicio
        proveedorService.saveProveedor(proveedor);
        // Redirige al listado principal después de guardar
        return "redirect:/proveedores";
    }

    // Maneja la acción de eliminar un proveedor
    @GetMapping("/delete/{id}")
    public String deleteProveedor(@PathVariable(value = "id") long id) {
        // Llama al método de eliminación del servicio
        this.proveedorService.deleteProveedorById(id);
        // Redirige al listado principal después de eliminar
        return "redirect:/proveedores";
    }
}