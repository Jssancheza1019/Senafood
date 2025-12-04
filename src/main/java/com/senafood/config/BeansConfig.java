package com.senafood.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration // Indica que esta clase contiene configuraciones de Spring
public class BeansConfig {

    // Define un Bean de tipo PasswordEncoder para su uso en toda la aplicación.
    // Este Bean permite encriptar contraseñas usando BCrypt.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Retorna una instancia del encriptador BCrypt
    }
}
