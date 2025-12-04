package com.senafood.service;

import com.senafood.model.Promocion;
import com.senafood.model.Producto; 
import com.senafood.repository.PromocionRepository;
import com.senafood.repository.ProductoRepository; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PromocionService {

    @Autowired
    private PromocionRepository promocionRepository;

    @Autowired
    private ProductoRepository productoRepository; // Necesario para el formulario

    public List<Promocion> listarTodas() {
        return promocionRepository.findAll();
    }

    public Promocion guardar(Promocion promocion) {
        // Establecer fechas de auditoría
        if (promocion.getIdPromocion() == null) {
            promocion.setCreateAt(LocalDateTime.now());
        }
        promocion.setUpdateAt(LocalDateTime.now());
        return promocionRepository.save(promocion);
    }

    public Optional<Promocion> buscarPorId(Integer id) {
        return promocionRepository.findById(id);
    }

    public void eliminar(Integer id) {
        promocionRepository.deleteById(id);
    }
    
    // Método para obtener la lista de productos (para el select del formulario)
    public List<Producto> listarTodosLosProductos() {
        return productoRepository.findAll();
    }
}