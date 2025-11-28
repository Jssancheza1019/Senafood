// Archivo: src/main/java/com/senafood/controller/PQRSFController.java

package com.senafood.controller;

import com.senafood.model.PQRSF; // Asegúrate de que la ruta del paquete sea correcta
import com.senafood.service.PQRSFService; // Asegúrate de que la ruta del paquete sea correcta
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional; 

@RestController // Indica que este controlador gestiona una API REST y devuelve datos (JSON)
@RequestMapping("/api/pqrsf") // Ruta base para el API
public class PQRSFController {

    private final PQRSFService pqrsfService;

    // Inyección de dependencia por constructor (Recomendada)
    public PQRSFController(PQRSFService pqrsfService) {
        this.pqrsfService = pqrsfService;
    }

    // 1. OBTENER TODAS LAS PQRSF
    @GetMapping
    public ResponseEntity<List<PQRSF>> getAllPqrsf() {
        // Llama a obtenerTodos(), que asume usará la consulta JOIN FETCH para cargar el User
        List<PQRSF> pqrsfList = pqrsfService.obtenerTodos(); 
        return ResponseEntity.ok(pqrsfList);
    }

    // 2. OBTENER PQRSF POR ID
    @GetMapping("/{id}")
    public ResponseEntity<PQRSF> getPqrsfById(@PathVariable Long id) {
        Optional<PQRSF> pqrsf = pqrsfService.findById(id); 
        // Si lo encuentra, devuelve 200 OK con el cuerpo; si no, devuelve 404 Not Found
        return pqrsf.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // 3. ACTUALIZAR ESTADO (PATCH)
    @PatchMapping("/estado/{id}")
    public ResponseEntity<Object> updatePqrsfEstado(
            @PathVariable Long id, 
            @RequestBody Map<String, String> requestBody) {
        
        String nuevoEstado = requestBody.get("nuevoEstado");

        if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
            return new ResponseEntity<>("El campo 'nuevoEstado' es requerido.", HttpStatus.BAD_REQUEST);
        }

        try {
            PQRSF pqrsfActualizada = pqrsfService.actualizarEstado(id, nuevoEstado);
            return ResponseEntity.ok(pqrsfActualizada);
            
        } catch (RuntimeException e) {
            // Maneja la excepción si el ID no existe (lanzada desde el Servicio)
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); 
        }
    }
    
    // 4. CREAR NUEVA PQRSF
    @PostMapping
    public ResponseEntity<PQRSF> createPqrsf(@RequestBody PQRSF pqrsf) {
        PQRSF savedPqrsf = pqrsfService.guardarPQRSF(pqrsf);
        return new ResponseEntity<>(savedPqrsf, HttpStatus.CREATED);
    }
}