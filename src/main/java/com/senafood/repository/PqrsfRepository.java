// Archivo: src/main/java/com/senafood/repository/PqrsfRepository.java

package com.senafood.repository;

import com.senafood.model.PQRSF;
import com.senafood.model.User; // Necesitas importar la entidad Usuario
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; 
import org.springframework.stereotype.Repository;

import java.util.List; 

@Repository
public interface PqrsfRepository extends JpaRepository<PQRSF, Long> {

    // Método para el Administrador: Carga todas las PQRSF ordenadas, trayendo el usuario (optimización con FETCH JOIN)
    @Query("SELECT p FROM PQRSF p JOIN FETCH p.usuario ORDER BY p.createAt DESC")
    List<PQRSF> findAllWithUsuario(); 
    
    // ⭐ MÉTODO AÑADIDO PARA EL CLIENTE: Buscar PQRSF por su Usuario ⭐
    // Spring Data JPA lo implementa automáticamente si el nombre sigue la convención:
    // findBy[NombreCampo]OrderBy[OtroCampo]Desc
    // Asumiendo que la entidad PQRSF tiene un campo llamado 'usuario'.
    List<PQRSF> findByUsuarioOrderByCreateAtDesc(User usuario);
}