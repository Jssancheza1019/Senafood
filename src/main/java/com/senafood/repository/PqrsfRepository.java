// Archivo: src/main/java/com/senafood/repository/PqrsfRepository.java

package com.senafood.repository;

import com.senafood.model.PQRSF;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // ¡IMPORTAR ESTO!
import org.springframework.stereotype.Repository;

import java.util.List; // ¡IMPORTAR ESTO!

@Repository
public interface PqrsfRepository extends JpaRepository<PQRSF, Long> {

    // Método para cargar la relación 'usuario' junto con la PQRSF
    @Query("SELECT p FROM PQRSF p JOIN FETCH p.usuario ORDER BY p.createAt DESC")
    List<PQRSF> findAllWithUsuario(); 
}