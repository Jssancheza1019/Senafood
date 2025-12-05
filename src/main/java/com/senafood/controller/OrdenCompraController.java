package com.senafood.controller;

import com.senafood.model.OrdenCompra;
import com.senafood.model.Proveedor;
import com.senafood.service.OrdenCompraService;
import com.senafood.service.ProveedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controlador que maneja las solicitudes HTTP para la gestión de Órdenes de Compra.
 */
@Controller
@RequestMapping("/ordenescompra")
public class OrdenCompraController {

    @Autowired
    private OrdenCompraService ordenCompraService;

    @Autowired
    private ProveedorService proveedorService;

    // --- 1. LISTAR Órdenes de Compra ---
    @GetMapping
    public String viewHomePage(Model model) {
        List<OrdenCompra> listaOrdenes = ordenCompraService.findAll();
        model.addAttribute("listaOrdenesCompra", listaOrdenes); 
        return "ordencompra/view";
    }

    // --------------------------------------------------------------------------------
    // --- 2. FORMULARIO UNIFICADO (Crear o Editar) ---
    // Muestra el formulario vacío (si id es null) o cargado (si id no es null)
    // Ruta: /ordenescompra/form?id={id}
    @GetMapping("/form")
    public String showForm(@RequestParam(required = false) Long id, Model model) {
        OrdenCompra ordenCompra = new OrdenCompra(); // Objeto por defecto para crear
        
        // Si el ID viene en la URL, se trata de una EDICIÓN
        if (id != null) {
            Optional<OrdenCompra> optionalOrden = ordenCompraService.findById(id);
            if (optionalOrden.isPresent()) {
                // Si la OC existe, la cargamos al objeto
                ordenCompra = optionalOrden.get();
            } else {
                // Si la OC no existe, redirigimos
                return "redirect:/ordenescompra"; 
            }
        }

        // Obtener la lista de proveedores para el dropdown
        List<Proveedor> listaProveedores = proveedorService.getAllProveedores();
        
        // Añade los objetos necesarios al modelo
        model.addAttribute("ordencompra", ordenCompra);
        model.addAttribute("listaProveedores", listaProveedores);
        
        return "ordencompra/form"; // Retorna el archivo del formulario
    }

    // --------------------------------------------------------------------------------
    // --- 3. PROCESAR (Guardar o Actualizar) ---
    @PostMapping("/save")
    public String saveOrdenCompra(@ModelAttribute("ordencompra") OrdenCompra ordenCompra) {
        // El Service se encarga de calcular el total y la Repository de guardarlo/actualizarlo
        // Si ordenCompra.id es null -> Crea (INSERT)
        // Si ordenCompra.id tiene valor -> Actualiza (UPDATE)
        if (ordenCompra.getTotal() == null) {
        // Calcular el total aquí si no viene del formulario:
        // ordenCompra.setTotal(ordenCompra.getPrecioUnitario().multiply(ordenCompra.getCantidad()));
    }
        ordenCompraService.save(ordenCompra);
        return "redirect:/ordenescompra";
    }

    // --------------------------------------------------------------------------------
    // --- 4. ELIMINAR ---
    @GetMapping("/delete/{id}")
    public String deleteOrdenCompra(@PathVariable(value = "id") Long id) {
        ordenCompraService.deleteById(id);
        return "redirect:/ordenescompra";
    }
}