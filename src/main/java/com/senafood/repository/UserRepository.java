// src/main/java/com/senafood/repository/UserRepository.java

package com.senafood.repository;

import com.senafood.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// JpaRepository<Entidad, Tipo_PK>
public interface UserRepository extends JpaRepository<User, Long> {

    // Método que Spring Data JPA genera automáticamente:
    // Busca un usuario por la columna 'email' que es nuestro 'username' de login
    Optional<User> findByEmail(String email); 
    
}