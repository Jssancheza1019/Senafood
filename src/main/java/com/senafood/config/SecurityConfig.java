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
                
                // Rutas de administrador
                .requestMatchers("/admin/**").hasRole("ADMINISTRADOR")
                
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