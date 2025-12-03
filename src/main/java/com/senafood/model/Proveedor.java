package com.senafood.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**x|
 * Clase que representa la entidad Proveedor en la base de datos.
 * Se mapea a la tabla "proveedor".
 * ATENCIÓN: Se han añadido anotaciones @Column para mapear los nombres
 * exactos de las columnas de la base de datos (Ej: id_proveedor, NIT).
 */
@Entity
@Table(name = "proveedor") // Mapeo explícito de la tabla
public class Proveedor {

    @Id
    // Mapeamos la llave primaria al nombre correcto en la tabla: id_proveedor
    @Column(name = "id_proveedor") 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 

    // Mapeamos al nombre de columna correcto: NIT (en mayúsculas)
    @Column(name = "nit") 
    private String nit; 

    private String nombre; // Coincide con el nombre en BD
    
    // Mapeamos al nombre de columna correcto: contacto
    @Column(name = "contacto")
    private String contacto; 
    
    private String telefono; // Coincide con el nombre en BD
    private String direccion; // Coincide con el nombre en BD

    // --- Campos de Auditoría (Añadidos) ---
    // Mapeamos al nombre de columna correcto: create_at
    @Column(name = "create_at")
    private LocalDateTime createAt;
    
    // Mapeamos al nombre de columna correcto: update_at
    @Column(name = "update_at")
    private LocalDateTime updateAt;

    /**
     * Establece la fecha de creación antes de persistir la entidad por primera vez.
     */
    @PrePersist
    protected void onCreate() {
        this.createAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now(); // Se inicializa con la misma fecha
    }

    /**
     * Actualiza la fecha de modificación antes de actualizar la entidad.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updateAt = LocalDateTime.now();
    }


    // Constructor vacío (necesario para JPA)
    public Proveedor() {
    }

    // --- Getters y Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
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

    // Getters y Setters para los nuevos campos
    public LocalDateTime getCreateAt() {
        return createAt;
    }

    // No se necesita un setter público para createAt si se maneja con @PrePersist

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    // No se necesita un setter público para updateAt si se maneja con @PreUpdate
}