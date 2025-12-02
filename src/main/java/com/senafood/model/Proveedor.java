package com.senafood.model;

import jakarta.persistence.*;

/**
 * Entidad JPA para la tabla 'proveedores'.
 * Representa un proveedor con todos los datos requeridos.
 */
@Entity
@Table(name = "proveedores")
public class Proveedor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID autoincrementable

    @Column(nullable = false, length = 100)
    private String nombre; // Nombre del proveedor

    @Column(nullable = false, length = 15)
    private String nit; // Número de Identificación Tributaria (NIT)

    @Column(nullable = false, length = 50)
    private String contactoEmail; // Correo electrónico de contacto

    @Column(nullable = false, length = 20)
    private String telefono; // Teléfono de contacto

    @Column(nullable = false, length = 255)
    private String direccion; // Dirección física

    // --- Constructores ---
    public Proveedor() {
    }

    // --- Getters y Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getContactoEmail() {
        return contactoEmail;
    }

    public void setContactoEmail(String contactoEmail) {
        this.contactoEmail = contactoEmail;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}
