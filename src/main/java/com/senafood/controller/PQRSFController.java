package com.senafood.controller;

import com.senafood.model.PQRSF;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador REST para manejar las operaciones de PQRSF (Peticiones, Quejas, Reclamos, Sugerencias y Felicitaciones).
Esta implementación simula el comportamiento de un controlador REST
en Spring Boot. En un entorno real, usaría una interfaz Service y Repository.
 */
@RestController
@RequestMapping("/api/pqrsf")
public class PQRSFController {

    // Simulación de un servicio/repositorio para la persistencia
    // En una aplicación real, inyectarías @Autowired private PQRSFService pqrsfService;
    private final List<PQRSF> simulatedRepository = new ArrayList<>();
    private Long nextId = 1L;

    /**
     * Endpoint para obtener todas las PQRSF (Solo para demostración/Administradores).
     * @return Lista de PQRSF.
     */
    @GetMapping
    public ResponseEntity<List<PQRSF>> getAllPqrsf() {
        // En una aplicación real: return ResponseEntity.ok(pqrsfService.findAll());
        return ResponseEntity.ok(simulatedRepository);
    }

    /**
     * Endpoint para crear una nueva PQRSF.
     * Simula la obtención del idUsuario del contexto de seguridad.
     * * @param pqrsf La entidad PQRSF enviada por el cliente.
     * @return Respuesta HTTP con la PQRSF creada.
     */
    @PostMapping
    public ResponseEntity<PQRSF> createPqrsf(@RequestBody PQRSF pqrsf) {
        //SIMULACIÓN DE SEGURIDAD: Obtener el ID del usuario autenticado
        Long simulatedUserId = 101L; // Usamos un ID de usuario fijo para la simulación

        // Asignar metadatos que el cliente no debería enviar
        pqrsf.setId(nextId++);
        pqrsf.setIdUsuario(simulatedUserId); // Asignado desde el contexto de seguridad
        // El constructor de la entidad ya inicializa createAt y updateAt
        
        // El idCarrito puede ser null si es una sugerencia o felicitación general.
        if (pqrsf.getIdCarrito() == null || pqrsf.getIdCarrito() == 0) {
            pqrsf.setIdCarrito(null);
        }

        // Persistir (simulado)
        simulatedRepository.add(pqrsf);
        
        System.out.println("PQRSF Creada. ID: " + pqrsf.getId() + ", Tipo: " + pqrsf.getTipo() + ", Usuario: " + pqrsf.getIdUsuario());

        //  Devolver la respuesta
        return new ResponseEntity<>(pqrsf, HttpStatus.CREATED);
    }
    
    /**
     * Endpoint para actualizar el estado de una PQRSF.
     * @param id ID de la PQRSF a actualizar.
     * @param newStatus Nuevo estado (e.g., "En proceso", "Resuelta").
     * @return Respuesta HTTP.
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<PQRSF> updatePqrsfStatus(@PathVariable Long id, @RequestParam String newStatus) {
        
        //  Simular búsqueda en el repositorio
        PQRSF existingPqrsf = simulatedRepository.stream()
            .filter(p -> p.getId().equals(id))
            .findFirst()
            .orElse(null);

        if (existingPqrsf == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        //  Actualizar campos
        existingPqrsf.setEstado(newStatus);
        existingPqrsf.setUpdateAt(LocalDateTime.now());
        
        System.out.println("PQRSF Actualizada. ID: " + id + ", Nuevo Estado: " + newStatus);

        // Persistir y devolver 
        return ResponseEntity.ok(existingPqrsf);
    }
}