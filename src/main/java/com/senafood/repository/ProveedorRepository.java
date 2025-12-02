package com.senafood.repository;

import com.senafood.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad Proveedor.
 * Proporciona métodos CRUD y de consulta.
 */
@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    // Aquí se pueden añadir métodos de consulta personalizados si son necesarios.
}
