package com.senafood.service;

import com.senafood.model.OrdenCompra;
import com.senafood.repository.OrdenCompraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Clase de Servicio que maneja la lógica de negocio para las Órdenes de Compra.
 */
@Service
public class OrdenCompraService {

    @Autowired
    private OrdenCompraRepository ordenCompraRepository;

    /**
     * Obtiene una lista de todas las órdenes de compra.
     * @return Lista de OrdenCompra.
     */
    public List<OrdenCompra> findAll() {
        return ordenCompraRepository.findAll();
    }

    /**
     * Busca una orden de compra por su ID.
     * @param id El ID de la orden de compra.
     * @return Un Optional que contiene la OrdenCompra si se encuentra.
     */
    public Optional<OrdenCompra> findById(Long id) {
        return ordenCompraRepository.findById(id);
    }

    /**
     * Guarda o actualiza una orden de compra. Calcula el total antes de guardar.
     * @param ordenCompra El objeto OrdenCompra a guardar.
     * @return La OrdenCompra guardada.
     */
    public OrdenCompra save(OrdenCompra ordenCompra) {
        // Lógica de negocio: Calcular el total antes de guardar
        if (ordenCompra.getCantidad() != null && ordenCompra.getPrecioUnitario() != null) {
            BigDecimal total = ordenCompra.getPrecioUnitario()
                                        .multiply(new BigDecimal(ordenCompra.getCantidad()));
            ordenCompra.setTotal(total);
        }
        return ordenCompraRepository.save(ordenCompra);
    }

    /**
     * Elimina una orden de compra por su ID.
     * @param id El ID de la orden de compra a eliminar.
     */
    public void deleteById(Long id) {
        ordenCompraRepository.deleteById(id);
    }
}