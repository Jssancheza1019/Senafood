
package com.senafood.repository;

import com.senafood.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    // Busca un Role por el nombre de la columna 'nombreRol'
    Optional<Role> findByNombreRol(String nombreRol); 
}