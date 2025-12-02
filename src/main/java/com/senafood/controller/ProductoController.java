package com.senafood.controller;

import com.senafood.model.Producto;
import com.senafood.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/producto")
public class ProductoController {
    
    @Autowired
    private ProductoService productoService;
    
    // Formato de fecha para el formulario (DD/MM/YYYY como el usuario ingresa)
    private static final SimpleDateFormat DATE_FORMAT_INPUT = new SimpleDateFormat("dd/MM/yyyy");
    // Formato para la base de datos (yy-MM-dd)
    private static final SimpleDateFormat DATE_FORMAT_DB = new SimpleDateFormat("yy-MM-dd");
    
    /**
     * Página principal - Lista todos los productos
     */
    @GetMapping
    public String index(Model model) {
        System.out.println("=== CARGANDO PÁGINA PRODUCTOS ===");
        
        try {
            List<Producto> productos = productoService.findAll();
            System.out.println("✅ Productos encontrados: " + productos.size());
            
            // Log de primeros productos para debug
            if (!productos.isEmpty()) {
                for (int i = 0; i < Math.min(3, productos.size()); i++) {
                    Producto p = productos.get(i);
                    System.out.println("📦 Producto " + (i+1) + ": " + p.getNombre() + " - $" + p.getCostoUnitario());
                }
            }
            
            model.addAttribute("productos", productos);
            model.addAttribute("titulo", "Gestión de Productos");
            
            // Obtener estadísticas
            long totalProductos = productos.size();
            long productosActivos = productos.stream()
                .filter(p -> "activo".equals(p.getEstado()))
                .count();
            long stockBajo = productos.stream()
                .filter(p -> p.getStock() < 10)
                .count();
            
            model.addAttribute("totalProductos", totalProductos);
            model.addAttribute("productosActivos", productosActivos);
            model.addAttribute("stockBajo", stockBajo);
            
            return "producto/index";
            
        } catch (Exception e) {
            System.err.println("❌ ERROR en index(): " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar productos: " + e.getMessage());
            return "producto/index";
        }
    }
    
    /**
     * Formulario para crear nuevo producto
     */
    @GetMapping("/create")
    public String create(Model model) {
        System.out.println("📝 Cargando formulario para crear producto...");
        
        Producto producto = new Producto();
        
        // Establecer valores por defecto
        producto.setStock(0);
        producto.setEstado("activo");
        producto.setIdInventario(1);
        
        // Establecer fecha por defecto (30 días desde hoy)
        Date fechaDefault = new Date();
        fechaDefault.setDate(fechaDefault.getDate() + 30);
        producto.setFechaVencimiento(fechaDefault);
        
        model.addAttribute("producto", producto);
        
        return "producto/form";
    }
    
    /**
     * Guardar nuevo producto
     */
    @PostMapping("/store")
    public String store(@Valid @ModelAttribute Producto producto,
                       BindingResult result,
                       @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile,
                       @RequestParam(value = "fechaVencimientoStr", required = false) String fechaVencimientoStr,
                       RedirectAttributes redirectAttributes) {
        
        System.out.println("💾 Intentando guardar producto: " + producto.getNombre());
        
        // DEBUG: Ver qué está llegando
        System.out.println("📅 fechaVencimientoStr recibida: " + fechaVencimientoStr);
        
        // Validar que la fecha no esté vacía
        if (fechaVencimientoStr == null || fechaVencimientoStr.trim().isEmpty()) {
            System.err.println("❌ Fecha de vencimiento vacía");
            result.rejectValue("fechaVencimiento", "error.producto", 
                "La fecha de vencimiento es obligatoria");
            return "producto/form";
        }
        
        // Convertir la fecha del formulario a Date
        try {
            // El usuario ingresa DD/MM/YYYY, lo convertimos a Date
            Date fecha = DATE_FORMAT_INPUT.parse(fechaVencimientoStr);
            producto.setFechaVencimiento(fecha);
            System.out.println("📅 Fecha parseada (Date): " + fecha);
            
            // Convertir a formato de BD (yy-MM-dd) y mostrar para debug
            String fechaBD = DATE_FORMAT_DB.format(fecha);
            System.out.println("🗄️  Fecha para BD (yy-MM-dd): " + fechaBD);
            
        } catch (ParseException e) {
            System.err.println("❌ Error al parsear fecha: " + fechaVencimientoStr);
            result.rejectValue("fechaVencimiento", "error.producto", 
                "Formato de fecha inválido. Use DD/MM/YYYY (ej: 28/01/2026)");
            return "producto/form";
        }
        
        if (result.hasErrors()) {
            System.out.println("❌ Errores de validación encontrados en store()");
            result.getFieldErrors().forEach(error -> 
                System.out.println("   - " + error.getField() + ": " + error.getDefaultMessage())
            );
            return "producto/form";
        }
        
        try {
            // Validar fecha de vencimiento
            if (producto.getFechaVencimiento() != null && 
                producto.getFechaVencimiento().before(new Date())) {
                result.rejectValue("fechaVencimiento", "error.producto", 
                    "La fecha de vencimiento debe ser posterior a hoy.");
                System.out.println("❌ Fecha de vencimiento inválida");
                return "producto/form";
            }
            
            // Validar código de barras único
            if (producto.getCodigoBarras() != null && 
                !producto.getCodigoBarras().isEmpty() &&
                productoService.existsByCodigoBarras(producto.getCodigoBarras())) {
                result.rejectValue("codigoBarras", "error.producto", 
                    "Ya existe un producto con este código de barras.");
                System.out.println("❌ Código de barras duplicado: " + producto.getCodigoBarras());
                return "producto/form";
            }
            
            // Manejar la imagen
            if (imagenFile != null && !imagenFile.isEmpty()) {
                try {
                    String fileName = productoService.saveImage(imagenFile);
                    producto.setImagen(fileName);
                    System.out.println("📸 Imagen guardada: " + fileName);
                } catch (IOException e) {
                    result.rejectValue("imagen", "error.producto", 
                        "Error al guardar la imagen: " + e.getMessage());
                    System.err.println("❌ Error al guardar imagen: " + e.getMessage());
                    return "producto/form";
                }
            }
            
            // Establecer valores por defecto
            if (producto.getEstado() == null || producto.getEstado().isEmpty()) {
                producto.setEstado("activo");
            }
            if (producto.getIdInventario() == null) {
                producto.setIdInventario(1);
            }
            
            // Guardar el producto
            Producto savedProducto = productoService.save(producto);
            
            System.out.println("✅ Producto guardado exitosamente con ID: " + savedProducto.getIdProducto());
            redirectAttributes.addFlashAttribute("success", 
                "✅ Producto '" + producto.getNombre() + "' creado exitosamente.");
            return "redirect:/producto";
            
        } catch (Exception e) {
            System.err.println("❌ Error al crear producto: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", 
                "❌ Error al crear el producto: " + e.getMessage());
            return "redirect:/producto/create";
        }
    }
    
    /**
     * Formulario para editar producto
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        System.out.println("📝 Cargando formulario para editar producto ID: " + id);
        
        Optional<Producto> productoOpt = productoService.findById(id);
        
        if (productoOpt.isEmpty()) {
            System.out.println("❌ Producto no encontrado con ID: " + id);
            return "redirect:/producto";
        }
        
        Producto producto = productoOpt.get();
        System.out.println("✅ Producto encontrado: " + producto.getNombre());
        
        // Formatear la fecha para mostrar en el formulario (DD/MM/YYYY)
        if (producto.getFechaVencimiento() != null) {
            String fechaFormateada = DATE_FORMAT_INPUT.format(producto.getFechaVencimiento());
            model.addAttribute("fechaFormateada", fechaFormateada);
            System.out.println("📅 Fecha formateada para vista: " + fechaFormateada);
        }
        
        model.addAttribute("producto", producto);
        
        return "producto/form";
    }
    
    /**
     * Actualizar producto existente
     */
    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id,
                        @Valid @ModelAttribute Producto producto,
                        BindingResult result,
                        @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile,
                        @RequestParam(value = "eliminarImagen", required = false) Boolean eliminarImagen,
                        @RequestParam(value = "fechaVencimientoStr", required = false) String fechaVencimientoStr,
                        RedirectAttributes redirectAttributes) {
        
        System.out.println("🔄 Intentando actualizar producto ID: " + id);
        
        // DEBUG: Ver qué está llegando
        System.out.println("📅 fechaVencimientoStr recibida: " + fechaVencimientoStr);
        
        // Validar que la fecha no esté vacía
        if (fechaVencimientoStr == null || fechaVencimientoStr.trim().isEmpty()) {
            System.err.println("❌ Fecha de vencimiento vacía en update");
            result.rejectValue("fechaVencimiento", "error.producto", 
                "La fecha de vencimiento es obligatoria");
            return "producto/form";
        }
        
        // Convertir la fecha del formulario a Date
        try {
            // El usuario ingresa DD/MM/YYYY, lo convertimos a Date
            Date fecha = DATE_FORMAT_INPUT.parse(fechaVencimientoStr);
            producto.setFechaVencimiento(fecha);
            System.out.println("📅 Fecha parseada (Date): " + fecha);
            
            // Convertir a formato de BD (yy-MM-dd) y mostrar para debug
            String fechaBD = DATE_FORMAT_DB.format(fecha);
            System.out.println("🗄️  Fecha para BD (yy-MM-dd): " + fechaBD);
            
        } catch (ParseException e) {
            System.err.println("❌ Error al parsear fecha: " + fechaVencimientoStr);
            result.rejectValue("fechaVencimiento", "error.producto", 
                "Formato de fecha inválido. Use DD/MM/YYYY (ej: 28/01/2026)");
            return "producto/form";
        }
        
        if (result.hasErrors()) {
            System.out.println("❌ Errores de validación encontrados en update()");
            result.getFieldErrors().forEach(error -> 
                System.out.println("   - " + error.getField() + ": " + error.getDefaultMessage())
            );
            return "producto/form";
        }
        
        try {
            // Validar fecha de vencimiento
            if (producto.getFechaVencimiento() != null && 
                producto.getFechaVencimiento().before(new Date())) {
                result.rejectValue("fechaVencimiento", "error.producto", 
                    "La fecha de vencimiento debe ser posterior a hoy.");
                System.out.println("❌ Fecha de vencimiento inválida");
                return "producto/form";
            }
            
            // Obtener producto existente
            Optional<Producto> productoExistenteOpt = productoService.findById(id);
            if (productoExistenteOpt.isEmpty()) {
                System.out.println("❌ Producto no encontrado con ID: " + id);
                redirectAttributes.addFlashAttribute("error", "Producto no encontrado.");
                return "redirect:/producto";
            }
            
            Producto productoExistente = productoExistenteOpt.get();
            System.out.println("📝 Producto existente: " + productoExistente.getNombre());
            
            // Validar código de barras único (excluyendo el producto actual)
            if (producto.getCodigoBarras() != null && 
                !producto.getCodigoBarras().isEmpty() &&
                !producto.getCodigoBarras().equals(productoExistente.getCodigoBarras()) &&
                productoService.existsByCodigoBarras(producto.getCodigoBarras())) {
                result.rejectValue("codigoBarras", "error.producto", 
                    "Ya existe otro producto con este código de barras.");
                System.out.println("❌ Código de barras duplicado: " + producto.getCodigoBarras());
                return "producto/form";
            }
            
            // Manejar eliminación de imagen
            if (Boolean.TRUE.equals(eliminarImagen) && productoExistente.getImagen() != null) {
                try {
                    productoService.deleteImage(productoExistente.getImagen());
                    producto.setImagen(null);
                    System.out.println("🗑️ Imagen eliminada: " + productoExistente.getImagen());
                } catch (IOException e) {
                    System.err.println("⚠️ Error eliminando imagen: " + e.getMessage());
                }
            }
            
            // Manejar nueva imagen
            if (imagenFile != null && !imagenFile.isEmpty()) {
                try {
                    // Eliminar imagen anterior si existe
                    if (productoExistente.getImagen() != null) {
                        productoService.deleteImage(productoExistente.getImagen());
                        System.out.println("🗑️ Imagen anterior eliminada: " + productoExistente.getImagen());
                    }
                    
                    // Guardar nueva imagen
                    String fileName = productoService.saveImage(imagenFile);
                    producto.setImagen(fileName);
                    System.out.println("📸 Nueva imagen guardada: " + fileName);
                } catch (IOException e) {
                    result.rejectValue("imagen", "error.producto", 
                        "Error al guardar la imagen: " + e.getMessage());
                    System.err.println("❌ Error al guardar imagen: " + e.getMessage());
                    return "producto/form";
                }
            } else if (!Boolean.TRUE.equals(eliminarImagen)) {
                // Mantener imagen existente si no se elimina ni se sube nueva
                producto.setImagen(productoExistente.getImagen());
                System.out.println("📷 Manteniendo imagen existente: " + productoExistente.getImagen());
            }
            
            // Mantener ID de inventario del producto existente
            producto.setIdInventario(productoExistente.getIdInventario());
            
            // Actualizar el producto
            productoService.update(id, producto);
            
            // Notificación si stock bajo
            if (producto.getStock() < 10) {
                redirectAttributes.addFlashAttribute("warning", 
                    "⚠️ El producto '" + producto.getNombre() + "' tiene stock bajo (" + producto.getStock() + " unidades).");
            }
            
            System.out.println("✅ Producto actualizado exitosamente");
            redirectAttributes.addFlashAttribute("success", 
                "✅ Producto '" + producto.getNombre() + "' actualizado exitosamente.");
            return "redirect:/producto";
            
        } catch (Exception e) {
            System.err.println("❌ Error al actualizar producto: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", 
                "❌ Error al actualizar: " + e.getMessage());
            return "redirect:/producto/edit/" + id;
        }
    }
    
    /**
     * Eliminar producto
     */
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        System.out.println("🗑️ Intentando eliminar producto ID: " + id);
        
        try {
            Optional<Producto> productoOpt = productoService.findById(id);
            
            if (productoOpt.isPresent()) {
                String nombreProducto = productoOpt.get().getNombre();
                productoService.delete(id);
                System.out.println("✅ Producto eliminado: " + nombreProducto);
                redirectAttributes.addFlashAttribute("success", 
                    "✅ Producto '" + nombreProducto + "' eliminado exitosamente.");
            } else {
                System.out.println("❌ Producto no encontrado con ID: " + id);
                redirectAttributes.addFlashAttribute("error", "Producto no encontrado.");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error al eliminar producto: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", 
                "❌ No se puede eliminar: " + e.getMessage());
        }
        
        return "redirect:/producto";
    }
    
    /**
     * Buscar productos por nombre
     */
    @GetMapping("/search")
    public String search(@RequestParam(value = "query", required = false) String query, 
                        Model model) {
        System.out.println("🔍 Buscando productos con query: " + query);
        
        List<Producto> productos;
        
        if (query == null || query.trim().isEmpty()) {
            productos = productoService.findAll();
        } else {
            productos = productoService.searchByNombre(query);
        }
        
        System.out.println("✅ Resultados encontrados: " + productos.size());
        
        model.addAttribute("productos", productos);
        model.addAttribute("query", query);
        model.addAttribute("titulo", "Resultados de búsqueda");
        
        return "producto/index";
    }
    
    /**
     * Ver detalles de un producto
     */
    @GetMapping("/view/{id}")
    public String view(@PathVariable Long id, Model model) {
        System.out.println("👁️ Cargando detalles del producto ID: " + id);
        
        Optional<Producto> productoOpt = productoService.findById(id);
        
        if (productoOpt.isEmpty()) {
            System.out.println("❌ Producto no encontrado con ID: " + id);
            return "redirect:/producto";
        }
        
        Producto producto = productoOpt.get();
        System.out.println("✅ Producto encontrado: " + producto.getNombre());
        
        model.addAttribute("producto", producto);
        
        // Formatear fecha para mostrar
        if (producto.getFechaVencimiento() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            model.addAttribute("fechaFormateada", sdf.format(producto.getFechaVencimiento()));
        }
        
        // Determinar estado del stock
        String estadoStock;
        if (producto.getStock() <= 0) {
            estadoStock = "danger";
        } else if (producto.getStock() < 10) {
            estadoStock = "warning";
        } else {
            estadoStock = "success";
        }
        model.addAttribute("estadoStock", estadoStock);
        
        return "producto/view";
    }
    
    /**
     * Endpoint de prueba - Para verificar que el controlador funciona
     */
    @GetMapping("/test")
    @ResponseBody
    public String test() {
        return "✅ ProductoController funciona correctamente!";
    }
}