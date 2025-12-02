package com.senafood.service;

import com.senafood.model.Proveedor;
import com.senafood.repository.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para manejar operaciones de negocio relacionadas con Proveedores.
 */
@Service
public class ProveedorService {

    // Nota: El repositorio debe estar en com.senafood.repository y el modelo en com.senafood.model
    @Autowired
    private ProveedorRepository proveedorRepository;

    /**
     * Obtiene la lista de todos los proveedores.
     * @return Lista de objetos Proveedor.
     */
    public List<Proveedor> findAll() {
        return proveedorRepository.findAll();
    }

    /**
     * Busca un proveedor por su ID.
     * @param id ID del proveedor.
     * @return Un Optional que contiene el Proveedor si existe.
     */
    public Optional<Proveedor> findById(Long id) {
        return proveedorRepository.findById(id);
    }

    /**
     * Guarda o actualiza un proveedor.
     * @param proveedor El objeto Proveedor a guardar.
     * @return El Proveedor guardado.
     */
    public Proveedor save(Proveedor proveedor) {
        return proveedorRepository.save(proveedor);
    }

    /**
     * Elimina un proveedor por su ID.
     * @param id ID del proveedor a eliminar.
     */
    public void deleteById(Long id) {
        proveedorRepository.deleteById(id);
    }
}