package com.senafood.service;

import com.senafood.model.Proveedor;
import com.senafood.repository.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * Servicio que implementa la lógica de negocio para la entidad Proveedor.
 */
@Service
public class ProveedorService {

    @Autowired
    private ProveedorRepository proveedorRepository;

    /**
     * Obtiene y retorna una lista de todos los proveedores.
     * @return Lista de objetos Proveedor.
     */
    public List<Proveedor> getAllProveedores() {
        // CORRECCIÓN CLAVE: findAll() retorna todos los registros de la base de datos.
        return proveedorRepository.findAll();
    }

    /**
     * Guarda o actualiza un proveedor en la base de datos.
     * @param proveedor El objeto Proveedor a guardar.
     */
    public void saveProveedor(Proveedor proveedor) {
        this.proveedorRepository.save(proveedor);
    }

    /**
     * Obtiene un proveedor por su ID.
     * @param id El ID del proveedor a buscar.
     * @return El objeto Proveedor si existe.
     */
    public Proveedor getProveedorById(long id) {
        Optional<Proveedor> optional = proveedorRepository.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            // Manejo de excepción o valor nulo si no se encuentra
            throw new RuntimeException("Proveedor no encontrado para el id :: " + id);
        }
    }

    /**
     * Elimina un proveedor por su ID.
     * @param id El ID del proveedor a eliminar.
     */
    public void deleteProveedorById(long id) {
        this.proveedorRepository.deleteById(id);
    }
}