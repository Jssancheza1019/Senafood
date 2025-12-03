// Archivo: src/main/java/com/senafood/service/PQRSFService.java

package com.senafood.service;

import com.senafood.model.PQRSF;
import com.senafood.model.User; // ⭐ Importar la entidad Usuario ⭐
import com.senafood.repository.PqrsfRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional; 

@Service 
public class PQRSFService {

    // Inyección de dependencia por constructor
    private final PqrsfRepository pqrsfRepository;

    public PQRSFService(PqrsfRepository pqrsfRepository) {
        this.pqrsfRepository = pqrsfRepository;
    }

    @Transactional
    public PQRSF guardarPQRSF(PQRSF pqrsf) {
        // Asignación de valores iniciales (el usuario ya viene asignado del controlador)
        pqrsf.setCreateAt(LocalDateTime.now());
        pqrsf.setEstado("PENDIENTE"); 
        pqrsf.setUpdateAt(LocalDateTime.now());
        
        return pqrsfRepository.save(pqrsf);
    }

    /**
     * Retorna todas las solicitudes PQRSF (usando JOIN FETCH).
     */
    public List<PQRSF> obtenerTodos() {
        // Usamos la nueva consulta del repositorio
        return pqrsfRepository.findAllWithUsuario(); 
    }

    // ⭐ MÉTODO AÑADIDO: Filtrar PQRSF por Usuario (para la vista del cliente) ⭐
    /**
     * Retorna todas las solicitudes PQRSF creadas por un usuario específico.
     */
    public List<PQRSF> findByUsuario(User usuario) {
        // Llama al método que definimos en el PqrsfRepository
        return pqrsfRepository.findByUsuarioOrderByCreateAtDesc(usuario);
    }


    /**
     * Retorna una solicitud PQRSF por su ID.
     */
    public Optional<PQRSF> findById(Long id) {
        return pqrsfRepository.findById(id);
    }

    @Transactional
    public PQRSF marcarComoLeida(Long id) {
        Optional<PQRSF> pqrsfOptional = pqrsfRepository.findById(id);

        if (pqrsfOptional.isEmpty()) {
            // Es preferible usar EntityNotFoundException, pero mantendremos el que usas
            throw new RuntimeException("PQRSF con ID " + id + " no encontrado."); 
        }

        PQRSF pqrsf = pqrsfOptional.get();
        
        // Solo actualiza si el estado actual es 'false'
        if (!pqrsf.isLeida()) { 
            pqrsf.setLeida(true);
            pqrsf.setUpdateAt(LocalDateTime.now()); 
            // Como el método es @Transactional, el cambio se guardará, 
            // pero explícitamente podemos hacer save para asegurar la persistencia.
            return pqrsfRepository.save(pqrsf); 
        }
        
        return pqrsf;
    }
    @Transactional
    public PQRSF actualizarEstado(Long id, String nuevoEstado) {
        
        Optional<PQRSF> pqrsfOptional = pqrsfRepository.findById(id);

        if (pqrsfOptional.isEmpty()) {
            throw new RuntimeException("PQRSF con ID " + id + " no encontrado.");
        }

        PQRSF pqrsf = pqrsfOptional.get();
        pqrsf.setEstado(nuevoEstado);
        pqrsf.setUpdateAt(LocalDateTime.now()); 

        return pqrsfRepository.save(pqrsf);
    }
}