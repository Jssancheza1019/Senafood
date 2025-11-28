package com.senafood.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity 
@Table(name = "pqrsf")
@Data 
public class PQRSF {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pqrsf")
    private Long id;

    @Column(name = "tipo", nullable = false)
    private String tipo;

    @Column(name = "descripcion", columnDefinition = "TEXT", nullable = false)
    private String descripcion;

    @Column(name = "estado", nullable = false)
    private String estado;

    // --- MODIFICACIÓN CLAVE ---
    // Eliminamos el Long idUsuario
    // @Column(name = "id_usuario", nullable = false)
    // private Long idUsuario; 

    // Añadimos la relación ManyToOne con el objeto User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false) // Usa la columna de clave foránea existente
    private User usuario; 
    // -------------------------

    @Column(name = "create_at", updatable = false)
    private LocalDateTime createAt; 

    @Column(name = "update_at")
    private LocalDateTime updateAt; 

    @PrePersist
    protected void onCreate() {
        createAt = LocalDateTime.now();
        updateAt = LocalDateTime.now(); 
    }

    @PreUpdate
    protected void onUpdate() {
        updateAt = LocalDateTime.now();
    }

    @Column(nullable = false)
    private boolean leida = false; // Mapea al TINYINT(1) de MySQL, 0 es false (No Leída)

    // Genera el getter y setter:
    public boolean isLeida() {
        return leida;
    }

    public void setLeida(boolean leida) {
        this.leida = leida;
    }
}