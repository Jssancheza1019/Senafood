package com.senafood.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "rol")
@Data
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 🧹 LIMPIO: Eliminar @Column(name = "idRol"). Hibernate mapea idRol -> id_rol automáticamente.
    private Long idRol;

    // 🧹 LIMPIO: Eliminar @Column(name = "nombreRol"). Hibernate mapea nombreRol -> nombre_rol automáticamente.
    private String nombreRol; 

    // Constructor vacío requerido por JPA
    public Role() {}

    // Constructor para inicialización manual
    public Role(String nombreRol) {
        this.nombreRol = nombreRol;
    }

    /**
     * MÉTODO DE SPRING SECURITY
     * Debe devolver el rol en formato ROLE_XXXX
     */
    @Override
    public String getAuthority() {
        return "ROLE_" + nombreRol.toUpperCase();
    }

    @Override
    public String toString() {
        return nombreRol;
    }
}