package com.senafood.controller;

import com.senafood.model.DetalleCarrito;
import com.senafood.model.Producto;
import com.senafood.model.Pedido;
import com.senafood.service.ProductoService;
import com.senafood.service.PedidoService; 

import org.springframework.beans.factory.annotation.Autowired;
// **********************************************
// IMPORTACIONES AÑADIDAS para obtener el usuario logueado
import org.springframework.security.core.Authentication; 
import org.springframework.security.core.context.SecurityContextHolder;
// **********************************************
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession; 

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    @Autowired 
    private ProductoService productoService;

    @Autowired
    private PedidoService pedidoService; 

    private static final String ATTRIBUTE_CART = "carritoActivo";

    /**
     * 1. Añade un producto al carrito (sesión).
     */
    @PostMapping("/agregar")
    public String agregarProducto(@RequestParam Long idProducto, HttpSession session, RedirectAttributes ra) {
        
        @SuppressWarnings("unchecked")
        List<DetalleCarrito> detalles = (List<DetalleCarrito>) session.getAttribute(ATTRIBUTE_CART);
        if (detalles == null) {
            detalles = new ArrayList<>();
            session.setAttribute(ATTRIBUTE_CART, detalles);
        }

        // Uso de findById del servicio para obtener la información completa
        Optional<Producto> productoOpt = productoService.findById(idProducto); 
        
        if (productoOpt.isPresent()) {
            Producto producto = productoOpt.get();
            boolean encontrado = false;
            
            if (producto.getStock() == null || producto.getStock() < 1) {
                ra.addFlashAttribute("error", "El producto " + producto.getNombre() + " no tiene stock disponible.");
                return "redirect:/catalogo";
            }
            
            for (DetalleCarrito detalle : detalles) {
                if (detalle.getIdProducto().equals(idProducto)) {
                    
                    if (detalle.getCantidad() + 1 > producto.getStock()) {
                        ra.addFlashAttribute("error", "No hay suficiente stock de " + producto.getNombre());
                        return "redirect:/catalogo";
                    }
                    
                    detalle.setCantidad(detalle.getCantidad() + 1);
                    detalle.setSubTotal(producto.getCostoUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad())));
                    encontrado = true;
                    break;
                }
            }
            
            if (!encontrado) {
                DetalleCarrito nuevoDetalle = new DetalleCarrito();
                nuevoDetalle.setIdProducto(idProducto);
                nuevoDetalle.setNombreProducto(producto.getNombre());
                nuevoDetalle.setImagen(producto.getImagen());
                nuevoDetalle.setPrecioUnitario(producto.getCostoUnitario());
                nuevoDetalle.setCantidad(1);
                nuevoDetalle.setSubTotal(producto.getCostoUnitario());
                detalles.add(nuevoDetalle);
            }
            
            ra.addFlashAttribute("success", "Producto " + producto.getNombre() + " añadido al carrito.");
        } else {
            ra.addFlashAttribute("error", "El producto no existe o fue eliminado.");
        }

        return "redirect:/carrito/detalle";
    }

    /**
     * 2. Muestra la vista detallada del carrito.
     */
    @GetMapping("/detalle")
    public String verDetalle(HttpSession session, Model model) {
        
        @SuppressWarnings("unchecked")
        List<DetalleCarrito> detalles = (List<DetalleCarrito>) session.getAttribute(ATTRIBUTE_CART);
        
        if (detalles == null) {
            detalles = new ArrayList<>();
        }
        
        BigDecimal total = detalles.stream()
                .map(DetalleCarrito::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        model.addAttribute("detalles", detalles);
        model.addAttribute("totalPagar", total);
        model.addAttribute("titulo", "Detalle de tu Carrito");
        
        return "carrito/detalle";
    }

    /**
     * 3. Procesa el pago en efectivo, guarda el pedido y limpia el carrito.
     */
    @PostMapping("/pago/efectivo")
    public String procesarPagoEfectivo(HttpSession session, RedirectAttributes ra) {
        
        @SuppressWarnings("unchecked")
        List<DetalleCarrito> detalles = (List<DetalleCarrito>) session.getAttribute(ATTRIBUTE_CART);
        
        if (detalles == null || detalles.isEmpty()) {
            ra.addFlashAttribute("error", "El carrito está vacío. No se puede procesar el pedido.");
            return "redirect:/carrito/detalle";
        }

        // **********************************************
        // INICIO CAMBIOS CRÍTICOS
        // Obtener la información de autenticación actual
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // El email es el 'username' en la implementación de UserDetails
        String username = auth.getName(); 
        // FIN CAMBIOS CRÍTICOS
        // **********************************************

        BigDecimal total = detalles.stream()
            .map(DetalleCarrito::getSubTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        try {
            // **********************************************
            // CAMBIO: Pasar el nombre de usuario (email) al servicio para que asigne la FK
            Pedido pedidoGuardado = pedidoService.crearYGuardarPedido(detalles, total, "Efectivo", username);
            // **********************************************
            
            // 4. Limpiar el carrito de la sesión
            session.removeAttribute(ATTRIBUTE_CART);
            
            // Pasa el total como Flash Attribute para mostrarlo en la vista de confirmación
            ra.addFlashAttribute("total", pedidoGuardado.getTotal().toString()); 
            ra.addFlashAttribute("idPedido", pedidoGuardado.getIdPedido());
            
            return "redirect:/carrito/confirmacion";
            
        } catch (RuntimeException e) {
            // Captura la excepción del servicio (Stock insuficiente o Usuario no encontrado/FK nula)
            ra.addFlashAttribute("error", "Error al procesar el pedido. " + e.getMessage());
            return "redirect:/carrito/detalle";
        }
    }

    /**
     * 4. Vista de confirmación de pedido.
     */
    @GetMapping("/confirmacion")
    public String confirmacionPedido(Model model, 
                                     @ModelAttribute("total") String totalString, 
                                     @ModelAttribute("idPedido") Long idPedido,
                                     RedirectAttributes ra) {
        
        if (totalString == null || totalString.isEmpty()) {
            ra.addFlashAttribute("error", "No se encontró un pedido reciente.");
            return "redirect:/catalogo"; 
        }

        model.addAttribute("titulo", "¡Pedido Guardado!");
        model.addAttribute("idPedido", idPedido);
        
        try {
            model.addAttribute("totalPagado", new BigDecimal(totalString));
        } catch (NumberFormatException e) {
             model.addAttribute("totalPagado", BigDecimal.ZERO);
        }
        
        return "carrito/confirmacion";
    }

    /**
     * 5. Actualiza la cantidad de un producto en el carrito.
     */
    @PostMapping("/update")
    public String updateCantidad(@RequestParam Long idProducto, 
                                 @RequestParam String action, // 'sumar' o 'restar'
                                 HttpSession session, 
                                 RedirectAttributes ra) {
        
        @SuppressWarnings("unchecked")
        List<DetalleCarrito> detalles = (List<DetalleCarrito>) session.getAttribute(ATTRIBUTE_CART);
        
        if (detalles == null) {
            return "redirect:/carrito/detalle";
        }

        // Buscar y modificar el detalle
        detalles.stream()
            .filter(d -> d.getIdProducto().equals(idProducto))
            .findFirst()
            .ifPresent(detalle -> {
                
                Optional<Producto> productoOpt = productoService.findById(idProducto);
                if (productoOpt.isEmpty()) {
                     ra.addFlashAttribute("error", "Producto no encontrado.");
                     return;
                }
                Producto producto = productoOpt.get();
                
                int nuevaCantidad = detalle.getCantidad();
                
                if ("sumar".equals(action)) {
                    if (nuevaCantidad + 1 <= producto.getStock()) {
                        nuevaCantidad++;
                    } else {
                        ra.addFlashAttribute("error", "Stock máximo (" + producto.getStock() + ") alcanzado para " + detalle.getNombreProducto());
                    }
                } else if ("restar".equals(action) && nuevaCantidad > 1) {
                    nuevaCantidad--;
                } else if ("restar".equals(action) && nuevaCantidad == 1) {
                    // Si la cantidad es 1 y queremos restar, lo eliminamos.
                    detalles.remove(detalle);
                    ra.addFlashAttribute("success", "Producto eliminado del carrito.");
                    return; // Terminamos la operación aquí
                }

                // Actualizar cantidad y subTotal si la cantidad cambió
                if (nuevaCantidad != detalle.getCantidad()) {
                    detalle.setCantidad(nuevaCantidad);
                    detalle.setSubTotal(producto.getCostoUnitario().multiply(BigDecimal.valueOf(nuevaCantidad)));
                    ra.addFlashAttribute("success", "Cantidad actualizada.");
                }
            });

        // Si la lista de detalles queda vacía, quitamos el atributo de la sesión.
        if (detalles.isEmpty()) {
              session.removeAttribute(ATTRIBUTE_CART);
        }

        return "redirect:/carrito/detalle";
    }
}