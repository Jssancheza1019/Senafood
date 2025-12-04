package com.senafood.model;

import java.math.BigDecimal;

/**
 * Clase DTO (Data Transfer Object) utilizada para almacenar los datos
 * de un producto en el carrito de compras dentro de la Sesión HTTP.
 * * NO es una Entidad de base de datos.
 */
public class DetalleCarrito {

    private Long idProducto; // ID del producto
    private String nombreProducto; // Nombre del producto (para mostrar en la vista)
    private String imagen; // Nombre del archivo de imagen (para mostrar la miniatura)
    private BigDecimal precioUnitario; // Precio en el momento de la adición
    private Integer cantidad; // Cantidad de este producto en el carrito
    private BigDecimal subTotal; // subTotal = precioUnitario * cantidad

    // Constructor por defecto (necesario para Java Beans)
    public DetalleCarrito() {}

    // ------------------------------------
    // Getters y Setters
    // ------------------------------------
    
    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }
}