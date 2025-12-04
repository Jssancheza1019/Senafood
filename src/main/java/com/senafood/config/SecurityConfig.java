package com.senafood.config;

import com.senafood.service.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Inyección del Manejador de Redirección por Rol
    // Spring inyectará automáticamente el bean de CustomRole.
    // Este handler será responsable de redirigir al usuario
    // según su rol al momento de iniciar sesión.
    private final CustomRole customRoleHandler;

    public SecurityConfig(CustomRole customRoleHandler) {
        this.customRoleHandler = customRoleHandler;
    }
    
    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserServiceImpl userService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userService);
        auth.setPasswordEncoder(passwordEncoder);
        return auth;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, DaoAuthenticationProvider authenticationProvider) throws Exception {

        http.authenticationProvider(authenticationProvider);

        http
            .authorizeHttpRequests(authorize -> authorize
                // Rutas públicas
                .requestMatchers("/", "/css/**", "/js/**", "/img/**", "/uploads/**", "/register", "/login").permitAll()
                
                // Rutas públicas accesibles sin autenticación
                .requestMatchers("/", "/css/**", "/js/**", "/img/**", "/register", "/login").permitAll()
                
                // Rutas a las que solo puede acceder un usuario con rol ADMINISTRADOR
                .requestMatchers("/admin/**").hasRole("ADMINISTRADOR")

                // RUTAS ACTUALIZADAS PARA PROVEEDORES: Acceso para ADMINISTRADOR y VENDEDOR.
                // Usamos "proveedores/**" para coincidir con la ruta base del controlador (en plural).
                // Esto incluye: /proveedores, /proveedores/form, /proveedores/save, /proveedores/delete/{id}
                .requestMatchers("/proveedores/**").hasAnyRole("ADMINISTRADOR", "VENDEDOR")

                // RUTAS PARA ÓRDENES DE COMPRA: Acceso para ADMINISTRADOR y VENDEDOR.
                // Esto incluye: /ordenescompra, /ordenescompra/form, /ordenescompra/save, /ordenescompra/reporte/**
                .requestMatchers("/ordenescompra/**").hasAnyRole("ADMINISTRADOR", "VENDEDOR")
                
                // Rutas de producto accesibles para varios roles
                .requestMatchers("/producto/**").hasAnyRole("ADMINISTRADOR", "VENDEDOR")
                
                // El catálogo puede ser público o para clientes
                .requestMatchers("/producto/catalogo").permitAll() // o .hasAnyRole("CLIENTE", "ADMINISTRADOR", "VENDEDOR")
                
                // Cualquier otra solicitud requiere autenticación
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/authenticate")
                .successHandler(customRoleHandler)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
}