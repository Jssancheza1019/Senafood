package com.senafood.service;

import com.senafood.model.Producto;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ProductoService {
    
    // Métodos CRUD básicos
    List<Producto> findAll();
    Optional<Producto> findById(Long id);
    Producto save(Producto producto);
    Producto update(Long id, Producto producto);
    void delete(Long id);
    
    // Métodos de búsqueda
    List<Producto> searchByNombre(String nombre);
    List<Producto> findByCategoria(String categoria);
    Optional<Producto> findByCodigoBarras(String codigoBarras);
    
    // Métodos especiales
    List<Producto> findProductosConStockBajo(Integer stockMinimo);
    List<Producto> findProductosProximosAVencer();
    
    // Métodos para manejo de imágenes
    String saveImage(MultipartFile imagenFile) throws IOException;
    void deleteImage(String imagenName) throws IOException;
    
    // Métodos utilitarios
    boolean existsByCodigoBarras(String codigoBarras);
    List<String> findAllCategorias();

    // NUEVO MÉTODO PARA EL CARRITO/PEDIDO
    void descontarStock(Long idProducto, Integer cantidad);

 
}