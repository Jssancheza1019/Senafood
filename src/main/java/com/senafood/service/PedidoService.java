package com.senafood.service;

import com.senafood.model.Pedido;
import com.senafood.model.DetalleCarrito;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PedidoService {
    
    /**
     * Guarda el pedido y sus detalles, descuenta stock y asigna el usuario.
     * @param detalles Lista de DetalleCarrito del carrito de la sesión.
     * @param total Monto total del pedido.
     * @param metodoPago Método de pago.
     * @param username Nombre de usuario (email) del cliente logueado.
     * @return Pedido guardado.
     */
    // **********************************************
    // CAMBIO: Se añade 'username' a la firma del método
    Pedido crearYGuardarPedido(List<DetalleCarrito> detalles, BigDecimal total, String metodoPago, String username);
    // **********************************************
    
    // Métodos CRUD básicos
    Optional<Pedido> findById(Long id);
    List<Pedido> findAll();
}