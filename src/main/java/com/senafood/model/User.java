package com.senafood.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "usuario")
@Data
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //Hibernate mapea idUsuario -> id_usuario automáticamente.
    private Long idUsuario;

    private String nombre;
    private String apellido;

    @Column(unique = true, nullable = false)
    private String email;

    //Mantenemos el mapeo para la columna con el carácter 'ñ'
    @Column(name = "contraseña", nullable = false)
    private String password;

    private String telefono;

    //Hibernate mapea tipoIdentificacion -> tipo_identificacion automáticamente.
    private String tipoIdentificacion;

    //Hibernate mapea numeroIdentificacion -> numero_identificacion automáticamente.
    private String numeroIdentificacion;

    /**
     * RELACIÓN CON ROL
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rol", nullable = false)//Hibernate mapea rol -> id_rol automáticamente.
    private Role rol;

    /**
     * CAMPOS DE FECHAS
     */
    @Column(name = "create_at")
    private LocalDateTime createdAt;

    @Column(name = "update_at")
    private LocalDateTime updatedAt;

    /**
     * MÉTODOS DE SPRING SECURITY
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
            new SimpleGrantedAuthority("ROLE_" + rol.getNombreRol().toUpperCase())
        );
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Inicializar fechas automáticamente
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}