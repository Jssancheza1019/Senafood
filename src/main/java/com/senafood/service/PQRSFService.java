package com.senafood.service;

import com.senafood.model.PQRSF;
import com.senafood.model.User;
import com.senafood.repository.PqrsfRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PQRSFService {

    private final PqrsfRepository pqrsfRepository;

    public PQRSFService(PqrsfRepository pqrsfRepository) {
        this.pqrsfRepository = pqrsfRepository;
    }

    @Transactional
    public PQRSF guardarPQRSF(PQRSF pqrsf) {
        pqrsf.setCreateAt(LocalDateTime.now());
        pqrsf.setEstado("PENDIENTE");
        pqrsf.setUpdateAt(LocalDateTime.now());
        return pqrsfRepository.save(pqrsf);
    }

    /**
     * Retorna una página de solicitudes PQRSF.
     */
    public Page<PQRSF> obtenerTodos(Pageable pageable) {
        return pqrsfRepository.findAllWithUsuario(pageable);
    }

    /**
     * Retorna todas las solicitudes PQRSF creadas por un usuario específico.
     */
    public List<PQRSF> findByUsuario(User usuario) {
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
        PQRSF pqrsf = pqrsfRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PQRSF con ID " + id + " no encontrado."));

        if (!pqrsf.isLeida()) {
            pqrsf.setLeida(true);
            pqrsf.setUpdateAt(LocalDateTime.now());
            return pqrsfRepository.save(pqrsf);
        }

        return pqrsf;
    }

    @Transactional
    public PQRSF actualizarEstado(Long id, String nuevoEstado) {
        PQRSF pqrsf = pqrsfRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PQRSF con ID " + id + " no encontrado."));

        pqrsf.setEstado(nuevoEstado);
        pqrsf.setUpdateAt(LocalDateTime.now());

        return pqrsfRepository.save(pqrsf);
    }
}
