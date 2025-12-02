package com.senafood.repository;

import com.senafood.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    // Método para buscar productos por nombre (búsqueda insensible a mayúsculas)
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
    
    // Método para buscar por categoría
    List<Producto> findByCategoria(String categoria);
    
    // Método para buscar por código de barras
    Optional<Producto> findByCodigoBarras(String codigoBarras);
    
    // Método para encontrar productos con stock bajo
    List<Producto> findByStockLessThan(Integer stock);
    
    // Método para buscar por estado
    List<Producto> findByEstado(String estado);
    
    // Método para encontrar productos activos
    List<Producto> findByEstadoAndStockGreaterThan(String estado, Integer stock);
    
    // **CONSULTA CORREGIDA** para productos próximos a vencer (30 días)
    // Usamos DATE_ADD o ADDDATE dependiendo de tu base de datos
    @Query("SELECT p FROM Producto p WHERE p.fechaVencimiento <= FUNCTION('DATE_ADD', CURRENT_DATE, 30)")
    List<Producto> findProductosProximosAVencer();
    
    // Otra opción (especificar días como parámetro):
    @Query("SELECT p FROM Producto p WHERE p.fechaVencimiento BETWEEN CURRENT_DATE AND FUNCTION('DATE_ADD', CURRENT_DATE, :dias)")
    List<Producto> findProductosProximosAVencerDias(@Param("dias") Integer dias);
    
    // Método adicional: buscar productos por nombre y categoría
    List<Producto> findByNombreContainingIgnoreCaseAndCategoria(String nombre, String categoria);
    
    // Método para contar productos por categoría
    @Query("SELECT p.categoria, COUNT(p) FROM Producto p GROUP BY p.categoria")
    List<Object[]> countByCategoria();
    long count();
    // Buscar productos vencidos
    @Query("SELECT p FROM Producto p WHERE p.fechaVencimiento < CURRENT_DATE")
    List<Producto> findProductosVencidos();
}