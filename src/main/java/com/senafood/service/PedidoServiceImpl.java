package com.senafood.service;

import com.senafood.model.Pedido;
import com.senafood.model.DetallePedido;
import com.senafood.model.DetalleCarrito;
import com.senafood.model.User; // Importamos la clase User
import com.senafood.repository.PedidoRepository;
import com.senafood.repository.UserRepository; // Necesario para buscar el usuario
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductoService productoService; 
    // **********************************************
    // INICIO CAMBIOS CRÍTICOS
    private final UserRepository userRepository; // Inyectar el repositorio de User
    // FIN CAMBIOS CRÍTICOS
    // **********************************************

    @Autowired
    // **********************************************
    // CAMBIO: Añadir UserRepository al constructor
    public PedidoServiceImpl(PedidoRepository pedidoRepository, ProductoService productoService, UserRepository userRepository) {
        this.pedidoRepository = pedidoRepository;
        this.productoService = productoService;
        this.userRepository = userRepository; // Asignación
    }
    // **********************************************

    @Override
    // CRUCIAL: Mantiene @Transactional. Asegura que si falla el stock o el usuario, todo se revierte.
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class}) 
    // **********************************************
    // CAMBIO: Se añade 'username' a la firma
    public Pedido crearYGuardarPedido(List<DetalleCarrito> detalles, BigDecimal total, String metodoPago, String username) { 
        
        // 1. Obtener la entidad User a partir del username (email)
        User cliente = userRepository.findByEmail(username) // Buscar por email (que es el username)
            .orElseThrow(() -> new RuntimeException("Error: Usuario no encontrado con email: " + username));
        
        // 2. Crear el Pedido (Cabecera)
        Pedido nuevoPedido = new Pedido();
        nuevoPedido.setTotal(total);
        nuevoPedido.setMetodoPago(metodoPago);
        nuevoPedido.setFechaCreacion(LocalDateTime.now());
        
        // 3. Asignar el User (cliente) al pedido - ¡CRÍTICO para evitar el error de commit!
        nuevoPedido.setUsuario(cliente); 
        // **********************************************
        
        List<DetallePedido> detallesPedido = new ArrayList<>();

        // 4. Crear los DetallePedido, mapear y descontar stock
        for (DetalleCarrito dc : detalles) {
            
            // Lógica CRUCIAL: Descontar stock. Si falla, se hace ROLLBACK de todo.
            productoService.descontarStock(dc.getIdProducto(), dc.getCantidad()); 

            // Mapear DetalleCarrito a DetallePedido
            DetallePedido dp = new DetallePedido();
            dp.setPedido(nuevoPedido); 
            dp.setIdProducto(dc.getIdProducto());
            dp.setNombreProducto(dc.getNombreProducto());
            dp.setCantidad(dc.getCantidad());
            dp.setPrecioUnitario(dc.getPrecioUnitario());
            dp.setSubtotal(dc.getSubTotal());
            
            detallesPedido.add(dp);
        }
        
        // 5. Asignar los detalles y guardar
        // El CascadeType.ALL en Pedido.java asegura que los detalles se guarden.
        nuevoPedido.setDetalles(detallesPedido); 
        
        Pedido pedidoGuardado = pedidoRepository.save(nuevoPedido);
        return pedidoGuardado;
    }
    
    @Override
    public Optional<Pedido> findById(Long id) {
        return pedidoRepository.findById(id);
    }

    @Override
    public List<Pedido> findAll() {
        return pedidoRepository.findAll();
    }
}