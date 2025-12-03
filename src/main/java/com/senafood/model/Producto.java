package com.senafood.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "producto")
public class Producto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long idProducto;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
    @Column(name = "nombre", nullable = false)
    private String nombre;
    
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    @Column(name = "descripcion", length = 500)
    private String descripcion;
    
    @NotNull(message = "El costo unitario es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El costo unitario debe ser mayor que 0")
    @Column(name = "costo_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal costoUnitario;
    
    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(name = "stock", nullable = false)
    private Integer stock;
    
    // CAMBIO IMPORTANTE: No usamos relación con inventario, solo guardamos un valor
    @Column(name = "id_inventario", nullable = false, columnDefinition = "int default 1")
    private Integer idInventario = 1;
    
    @NotNull(message = "La fecha de vencimiento es obligatoria")
    @Future(message = "La fecha de vencimiento debe ser futura")
    @Column(name = "fecha_vencimiento", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fechaVencimiento;
    
    @Size(max = 255, message = "La categoría no puede exceder 255 caracteres")
    @Column(name = "categoria")
    private String categoria;
    
    @Size(max = 255, message = "El código de barras no puede exceder 255 caracteres")
    @Column(name = "codigo_barras")
    private String codigoBarras;
    
    @Column(name = "estado", nullable = false)
    private String estado = "activo";
    
    @Column(name = "create_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createAt;
    
    @Column(name = "update_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateAt;
    
    @Column(name = "imagen")
    private String imagen;
    
    // Constructor por defecto
    public Producto() {
        this.createAt = new Date();
        this.updateAt = new Date();
        this.estado = "activo";
        this.idInventario = 1; // Valor por defecto para cumplir con la BD
    }
    
    // Getters y Setters
    public Long getIdProducto() {
        return idProducto;
    }
    
    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public BigDecimal getCostoUnitario() {
        return costoUnitario;
    }
    
    public void setCostoUnitario(BigDecimal costoUnitario) {
        this.costoUnitario = costoUnitario;
    }
    
    public Integer getStock() {
        return stock;
    }
    
    public void setStock(Integer stock) {
        this.stock = stock;
    }
    
    // IMPORTANTE: Getter y Setter para idInventario como Integer
    public Integer getIdInventario() {
        return idInventario;
    }
    
    public void setIdInventario(Integer idInventario) {
        this.idInventario = idInventario;
    }
    
    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }
    
    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }
    
    public String getCategoria() {
        return categoria;
    }
    
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    
    public String getCodigoBarras() {
        return codigoBarras;
    }
    
    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public Date getCreateAt() {
        return createAt;
    }
    
    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }
    
    public Date getUpdateAt() {
        return updateAt;
    }
    
    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }
    
    public String getImagen() {
        return imagen;
    }
    
    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
    
    // Método para actualizar la fecha antes de persistir
    @PreUpdate
    public void preUpdate() {
        this.updateAt = new Date();
    }
    
    // Método para establecer valores por defecto antes de persistir
    @PrePersist
    public void prePersist() {
        if (this.createAt == null) {
            this.createAt = new Date();
        }
        if (this.updateAt == null) {
            this.updateAt = new Date();
        }
        if (this.estado == null) {
            this.estado = "activo";
        }
        if (this.idInventario == null) {
            this.idInventario = 1;
        }
    }
}