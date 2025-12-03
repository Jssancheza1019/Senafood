package com.senafood.repository;

import com.senafood.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interfaz Repository para acceder a los datos de Proveedor.
 * Extiende JpaRepository para obtener las operaciones CRUD estándar.
 */
@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    // Aquí se pueden añadir métodos de consulta personalizados si son necesarios.
}