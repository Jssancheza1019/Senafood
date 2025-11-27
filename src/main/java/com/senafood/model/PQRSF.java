package com.senafood.model;

import java.time.LocalDateTime;

/**
 * Entidad de Peticiones, Quejas, Reclamos, Sugerencias y Felicitaciones (PQRSF).
 * Mapea directamente a la tabla PQRSF.
 * NOTA: Esta clase asume que Spring Data JPA se encargará de la persistencia 
 * en una aplicación real (usando Long para los IDs INT(11) de la base de datos).
 */
public class PQRSF {
    
    // Corresponde a id_pqrsf (PK, Auto Incrementable)
    private Long id; 
    
    // Corresponde a tipo (varchar(20))
    private String tipo; 
    
    // Corresponde a descripcion (text) - Contenido principal
    private String descripcion; 
    
    // Corresponde a estado (varchar(20))
    private String estado;   
    
    // Corresponde a id_usuario (FK, Not Null) - El ID del usuario creador
    private Long idUsuario;  
    
    // Corresponde a id_carrito (FK, puede ser Null) - Opcional, para asociar a una compra
    private Long idCarrito; 
    
    // Corresponde a create_at (datetime)
    private LocalDateTime createAt;
    
    // Corresponde a update_at (datetime)
    private LocalDateTime updateAt;

    // Constructor vacío (necesario para formularios y serialización)
    public PQRSF() {
        this.estado = "Pendiente"; 
        this.createAt = LocalDateTime.now();
        // El updateAt se inicializará al mismo tiempo que createAt por defecto
        this.updateAt = this.createAt;
    }

    // Constructor con campos básicos para simulación
    public PQRSF(Long id, String tipo, String descripcion, String estado, Long idUsuario, Long idCarrito) {
        this.id = id;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.estado = estado;
        this.idUsuario = idUsuario;
        this.idCarrito = idCarrito;
        this.createAt = LocalDateTime.now();
        this.updateAt = this.createAt;
    }
    
    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Long getIdCarrito() {
        return idCarrito;
    }

    public void setIdCarrito(Long idCarrito) {
        this.idCarrito = idCarrito;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }
}