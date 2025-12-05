package com.senafood.repository;

import com.senafood.model.PQRSF;
import com.senafood.model.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PqrsfRepository extends JpaRepository<PQRSF, Long> {

    // Consulta paginada para el Administrador (JOIN FETCH optimiza la carga del usuario)
    @Query("SELECT p FROM PQRSF p JOIN FETCH p.usuario")
    Page<PQRSF> findAllWithUsuario(Pageable pageable);

    // Consulta para Cliente: PQRSF ordenadas por fecha de creación descendente
    List<PQRSF> findByUsuarioOrderByCreateAtDesc(User usuario);
}
