package com.senafood.model;

import jakarta.persistence.*;
import lombok.Data; // Importar Lombok Data
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pedido")
@Data // Genera Getters, Setters, toString, equals y hashCode
public class Pedido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Long idPedido;
    
    // Este campo puede o no ser redundante si usas createdAt
    // Mantengo tu campo original:
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    
    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;
    
    @Column(name = "metodo_pago")
    private String metodoPago;

    /**
     * RELACIÓN CON USUARIO (CLIENTE)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false) 
    private User usuario; 

    /**
     * RELACIÓN CON DETALLEPEDIDO
     * Se mantiene CascadeType.ALL para que los detalles se guarden con el pedido.
     */
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePedido> detalles;

    // **********************************************
    // INICIO: CAMPOS DE FECHAS ADICIONALES (CAUSA PROBABLE DEL ERROR)
    // Si estos campos son NOT NULL en la DB y no se configuran, falla la transacción.
    
    @Column(name = "create_at")
    private LocalDateTime createdAt;

    @Column(name = "update_at")
    private LocalDateTime updatedAt;

    // Inicializar fechas automáticamente
    @PrePersist
    protected void onCreate() {
        // Inicializa solo si no se ha hecho, por si el campo 'fecha_creacion' es redundante
        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    // FIN: CAMPOS DE FECHAS ADICIONALES
    // **********************************************
}