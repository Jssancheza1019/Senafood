package com.senafood.repository;

import com.senafood.model.Promocion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromocionRepository extends JpaRepository<Promocion, Integer> {
    // Spring Data JPA proporciona automáticamente los métodos CRUD
}