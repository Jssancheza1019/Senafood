package com.senafood.repository;

import com.senafood.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    // Aquí puedes añadir métodos de búsqueda personalizados (e.g., findByFechaCreacion)
}