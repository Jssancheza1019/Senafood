package com.senafood.repository;

import com.senafood.model.OrdenCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interfaz Repository para acceder a los datos de OrdenCompra.
 * Extiende JpaRepository para obtener las operaciones CRUD estándar.
 */
@Repository
public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, Long> {
    // Métodos personalizados, si se necesitan (ej: buscar por estado o proveedor)
}