package com.senafood.controller;

import com.senafood.model.Producto;
import com.senafood.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor; // NUEVA IMPORTACIÓN
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder; // NUEVA IMPORTACIÓN
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/producto")
public class ProductoController {
    
    @Autowired
    private ProductoService productoService;
    
    // Formato de fecha para el formulario (yyyy-MM-dd para HTML5 input type="date")
    private static final SimpleDateFormat DATE_FORMAT_INPUT = new SimpleDateFormat("yyyy-MM-dd");
    // Formato para la base de datos (yy-MM-dd) - Mantener si se usa internamente
    private static final SimpleDateFormat DATE_FORMAT_DB = new SimpleDateFormat("yy-MM-dd");
    
    /**
     * Configura el DataBinder para que Spring pueda convertir automáticamente
     * la cadena de fecha (yyyy-MM-dd) del formulario a java.util.Date.
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // El tercer argumento (true) indica que los valores vacíos son permitidos.
        binder.registerCustomEditor(Date.class, new CustomDateEditor(DATE_FORMAT_INPUT, true));
        System.out.println("✅ CustomDateEditor registrado para yyyy-MM-dd");
    }
    
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
                        RedirectAttributes redirectAttributes) { // ELIMINADO: fechaVencimientoStr
        
        System.out.println("💾 Intentando guardar producto: " + producto.getNombre());
        
        // La fechaVencimiento ahora se vincula automáticamente al objeto 'producto'
        
        if (result.hasErrors()) {
            System.out.println("❌ Errores de validación encontrados en store()");
            result.getFieldErrors().forEach(error -> 
                System.out.println("   - " + error.getField() + ": " + error.getDefaultMessage())
            );
            return "producto/form";
        }
        
        try {
            // Validar fecha de vencimiento (ya como objeto Date)
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
        
        // No es necesario formatear la fecha aquí. El input type="date" de Thymeleaf
        // se encarga de mostrar la fecha en el formato correcto (yyyy-MM-dd)
        // cuando se usa th:field con un objeto Date.
        
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
                         RedirectAttributes redirectAttributes) { // ELIMINADO: fechaVencimientoStr
        
        System.out.println("🔄 Intentando actualizar producto ID: " + id);
        
        // La fechaVencimiento ahora se vincula automáticamente al objeto 'producto'
        
        if (result.hasErrors()) {
            System.out.println("❌ Errores de validación encontrados en update()");
            result.getFieldErrors().forEach(error -> 
                System.out.println("   - " + error.getField() + ": " + error.getDefaultMessage())
            );
            return "producto/form";
        }
        
        try {
            // Validar fecha de vencimiento (ya como objeto Date)
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

    // Archivo: ProductoController.java (Agregar este método)

    @GetMapping("/catalogo")
    public String catalogo(Model model) {
        System.out.println("🖼️ Cargando vista de Catálogo...");
        try {
            // Obtener la lista completa de productos
            List<Producto> productos = productoService.findAll();
            
            // Filtrar productos que estén activos y tengan stock disponible (> 0)
            List<Producto> productosDisponibles = productos.stream()
                    .filter(p -> "activo".equals(p.getEstado()) && p.getStock() > 0)
                    .toList();

            model.addAttribute("productos", productosDisponibles);
            model.addAttribute("titulo", "Catálogo de Productos");
            
            // ¡IMPORTANTE! Retorna el nombre del archivo dentro de 'templates/'
            return "producto/catalogo"; 
            
        } catch (Exception e) {
            System.err.println("❌ ERROR al cargar catálogo: " + e.getMessage());
            model.addAttribute("error", "Error al cargar el catálogo.");
            model.addAttribute("productos", new ArrayList<Producto>()); // Asegurar que la lista no sea null
            return "producto/catalogo";
        }
    }
}