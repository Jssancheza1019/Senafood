package com.senafood.model;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Clase que representa la entidad Orden de Compra.
 * Se mapea a la tabla "ordencompra".
 */
@Entity
@Table(name = "ordencompra")
public class OrdenCompra {

    @Id
    @Column(name = "id_orden") 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 

    @DateTimeFormat(pattern = "yyyy-MM-dd") // Asegura el formato correcto para el input type="date"
    private LocalDate fecha; 
    
    private String producto; 
    // Usar Integer para campos NULLABLE int(11)
    private Integer cantidad; 
    
    @Column(name = "precioUnitario")
    private BigDecimal precioUnitario; 
    
    private BigDecimal total; 

    private String estado; 

    // Relación ManyToOne con Proveedor
    @ManyToOne(fetch = FetchType.LAZY)
    // Mapeo de la llave foránea: id_proveedor en BD
    @JoinColumn(name = "id_proveedor", nullable = false) 
    // NOTA: El campo 'proveedor' en el modelo reemplaza a 'id_proveedor' en la tabla.
    private Proveedor proveedor;
    
    // NOTA: No se han incluido los campos 'id_usuario', 'create_at' y 'update_at' de la BD.

    // Constructor vacío (necesario para JPA)
    public OrdenCompra() {
        // Inicialización por defecto solo si es nulo (para evitar sobrescribir en edición si es necesario)
        if (this.fecha == null) {
            this.fecha = LocalDate.now(); 
        }
        if (this.estado == null) {
            this.estado = "Pendiente"; 
        }
        if (this.total == null) {
            this.total = BigDecimal.ZERO;
        }
    }

    // --- Getters y Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }
}