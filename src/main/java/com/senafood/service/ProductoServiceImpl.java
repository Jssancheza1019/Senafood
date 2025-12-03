package com.senafood.service;

import com.senafood.model.Producto;
import com.senafood.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductoServiceImpl implements ProductoService {
    
    private final ProductoRepository productoRepository;
    
    // DIRECTORIO ACTUALIZADO: Cambiado a la ruta solicitada: static/img/productos/
    private static final String UPLOAD_DIR = "src/main/resources/static/img/productos/";
    
    public ProductoServiceImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
        createUploadDirectory();
        System.out.println("✅ ProductoServiceImpl inicializado");
    }
    
    /**
     * Crea el directorio de uploads si no existe
     */
    private void createUploadDirectory() {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                System.out.println("✅ Directorio de imágenes creado: " + uploadPath.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("❌ Error creando directorio: " + e.getMessage());
        }
    }
    
    @Override
    public List<Producto> findAll() {
        System.out.println("🔍 Buscando todos los productos...");
        List<Producto> productos = productoRepository.findAll();
        System.out.println("📊 Total productos en BD: " + productos.size());
        
        if (productos.isEmpty()) {
            System.out.println("⚠️ ¡ADVERTENCIA! No hay productos en la base de datos");
        }
        
        return productos;
    }
    
    @Override
    public Optional<Producto> findById(Long id) {
        System.out.println("🔍 Buscando producto con ID: " + id);
        return productoRepository.findById(id);
    }
    
    @Override
    public Producto save(Producto producto) {
        System.out.println("💾 Guardando nuevo producto: " + producto.getNombre());
        
        // Validar que el código de barras no exista (si se proporciona)
        if (producto.getCodigoBarras() != null && !producto.getCodigoBarras().isEmpty()) {
            Optional<Producto> existente = productoRepository.findByCodigoBarras(producto.getCodigoBarras());
            if (existente.isPresent()) {
                throw new RuntimeException("Ya existe un producto con el código de barras: " + producto.getCodigoBarras());
            }
        }
        
        // Establecer valores por defecto
        if (producto.getEstado() == null) {
            producto.setEstado("activo");
        }
        if (producto.getIdInventario() == null) {
            producto.setIdInventario(1); // Valor por defecto para cumplir con la BD
        }
        if (producto.getCreateAt() == null) {
            producto.setCreateAt(new Date());
        }
        producto.setUpdateAt(new Date());
        
        Producto saved = productoRepository.save(producto);
        System.out.println("✅ Producto guardado con ID: " + saved.getIdProducto());
        return saved;
    }
    
    @Override
    public Producto update(Long id, Producto productoData) {
        System.out.println("🔄 Actualizando producto ID: " + id);
        
        return productoRepository.findById(id)
            .map(productoExistente -> {
                System.out.println("📝 Producto encontrado: " + productoExistente.getNombre());
                
                // Actualizar solo los campos que pueden cambiar
                productoExistente.setNombre(productoData.getNombre());
                productoExistente.setDescripcion(productoData.getDescripcion());
                productoExistente.setCostoUnitario(productoData.getCostoUnitario());
                productoExistente.setStock(productoData.getStock());
                productoExistente.setFechaVencimiento(productoData.getFechaVencimiento());
                productoExistente.setCategoria(productoData.getCategoria());
                productoExistente.setEstado(productoData.getEstado());
                productoExistente.setUpdateAt(new Date());
                
                // Actualizar código de barras solo si es diferente y no existe otro producto con él
                if (productoData.getCodigoBarras() != null && 
                    !productoData.getCodigoBarras().equals(productoExistente.getCodigoBarras())) {
                    productoExistente.setCodigoBarras(productoData.getCodigoBarras());
                }
                
                // Actualizar imagen solo si se proporciona una nueva
                if (productoData.getImagen() != null && !productoData.getImagen().isEmpty()) {
                    productoExistente.setImagen(productoData.getImagen());
                }
                
                Producto updated = productoRepository.save(productoExistente);
                System.out.println("✅ Producto actualizado: " + updated.getNombre());
                return updated;
            })
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }
    
    @Override
    public void delete(Long id) {
        System.out.println("🗑️ Intentando eliminar producto ID: " + id);
        
        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        
        System.out.println("Producto a eliminar: " + producto.getNombre());
        
        // Eliminar la imagen asociada si existe
        if (producto.getImagen() != null && !producto.getImagen().isEmpty()) {
            try {
                deleteImage(producto.getImagen());
                System.out.println("🗑️ Imagen eliminada: " + producto.getImagen());
            } catch (IOException e) {
                System.err.println("⚠️ Error eliminando imagen: " + e.getMessage());
            }
        }
        
        productoRepository.delete(producto);
        System.out.println("✅ Producto eliminado ID: " + id);
    }
    
    @Override
    public List<Producto> searchByNombre(String nombre) {
        System.out.println("🔍 Buscando productos por nombre: " + nombre);
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }
    
    @Override
    public List<Producto> findByCategoria(String categoria) {
        System.out.println("🔍 Buscando productos por categoría: " + categoria);
        return productoRepository.findByCategoria(categoria);
    }
    
    @Override
    public Optional<Producto> findByCodigoBarras(String codigoBarras) {
        System.out.println("🔍 Buscando producto por código de barras: " + codigoBarras);
        return productoRepository.findByCodigoBarras(codigoBarras);
    }
    
    @Override
    public List<Producto> findProductosConStockBajo(Integer stockMinimo) {
        System.out.println("🔍 Buscando productos con stock bajo (menos de " + stockMinimo + ")");
        return productoRepository.findByStockLessThan(stockMinimo);
    }
    
    @Override
    public List<Producto> findProductosProximosAVencer() {
        System.out.println("🔍 Buscando productos próximos a vencer");
        return productoRepository.findProductosProximosAVencer();
    }
    
    @Override
    public String saveImage(MultipartFile imagenFile) throws IOException {
        if (imagenFile == null || imagenFile.isEmpty()) {
            System.out.println("📸 No se proporcionó imagen");
            return null;
        }
        
        System.out.println("📸 Guardando imagen: " + imagenFile.getOriginalFilename());
        
        // Generar un nombre único para la imagen
        String originalFileName = imagenFile.getOriginalFilename();
        String fileExtension = "";
        
        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        
        String fileName = UUID.randomUUID().toString() + fileExtension;
        
        // Ruta completa del archivo
        Path filePath = Paths.get(UPLOAD_DIR + fileName);
        
        // Guardar la imagen
        Files.copy(imagenFile.getInputStream(), filePath);
        
        System.out.println("✅ Imagen guardada: " + fileName + " en " + filePath);
        
        // Retornar solo el nombre del archivo (sin la ruta completa)
        return fileName;
    }
    
    @Override
    public void deleteImage(String imagenName) throws IOException {
        if (imagenName != null && !imagenName.isEmpty()) {
            Path filePath = Paths.get(UPLOAD_DIR + imagenName);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                System.out.println("🗑️ Imagen eliminada: " + imagenName);
            } else {
                System.out.println("⚠️ Imagen no encontrada para eliminar: " + imagenName);
            }
        }
    }
    
    @Override
    public boolean existsByCodigoBarras(String codigoBarras) {
        return productoRepository.findByCodigoBarras(codigoBarras).isPresent();
    }
    
    @Override
    public List<String> findAllCategorias() {
        System.out.println("🔍 Obteniendo todas las categorías...");
        List<String> categorias = productoRepository.findAll()
            .stream()
            .map(Producto::getCategoria)
            .filter(categoria -> categoria != null && !categoria.isEmpty())
            .distinct()
            .sorted()
            .collect(Collectors.toList());
        
        System.out.println("📊 Categorías encontradas: " + categorias);
        return categorias;
    }
    
    // Método utilitario que podría usarse en el futuro
    private LocalDate convertToLocalDate(Date date) {
        if (date == null) return null;
        return date.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate();
    }
    
    // Nuevo método: Buscar productos vencidos
    public List<Producto> findProductosVencidos() {
        System.out.println("🔍 Buscando productos vencidos");
        return productoRepository.findProductosVencidos();
    }
    
    // Nuevo método: Verificar si hay datos en la base de datos
    public boolean hasData() {
        long count = productoRepository.count();
        System.out.println("📊 Verificando datos - Total registros: " + count);
        return count > 0;
    }
}