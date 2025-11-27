package com.senafood.config;

import com.senafood.service.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // Indica que esta clase contiene configuraciones de Spring
@EnableWebSecurity // Activa la seguridad web de Spring Security
public class SecurityConfig {

    // Inyección del Manejador de Redirección por Rol
    // Spring inyectará automáticamente el bean de CustomRole.
    // Este handler será responsable de redirigir al usuario
    // según su rol al momento de iniciar sesión.
    
    private final CustomRole customRoleHandler; 

    // Constructor para la inyección de dependencias.
    // CustomRole es pasado automáticamente por Spring.
    public SecurityConfig(CustomRole customRoleHandler) {
        this.customRoleHandler = customRoleHandler;
    }
    
    //Bean: Configura el proveedor de autenticación basado en DAO.
    // Este proveedor usa un UserDetailsService (UserServiceImpl)
    // y un PasswordEncoder para validar credenciales.
    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserServiceImpl userService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userService); // Servicio que carga usuarios desde la BD
        auth.setPasswordEncoder(passwordEncoder); // Encriptador de contraseñas
        return auth;
    }

    // Configuración de las reglas y filtros de seguridad
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, DaoAuthenticationProvider authenticationProvider) throws Exception {

        // Registrar nuestro proveedor de autenticación personalizado
        http.authenticationProvider(authenticationProvider);

        http
            .authorizeHttpRequests(authorize -> authorize
                
                // Rutas públicas accesibles sin autenticación
                .requestMatchers("/", "/css/**", "/js/**", "/images/**", "/register", "/login").permitAll()
                
                // Rutas a las que solo puede acceder un usuario con rol ADMINISTRADOR
                .requestMatchers("/admin/**").hasRole("ADMINISTRADOR")
                
                // Cualquier otra solicitud requiere que el usuario esté autenticado
                .anyRequest().authenticated()
            )

            .formLogin(form -> form
                .loginPage("/login")               // Página personalizada de login
                .loginProcessingUrl("/authenticate") // URL que procesa el formulario de login
                
                // ----------------------------------------------------
                // Reemplazar defaultSuccessUrl por successHandler
                // El successHandler permite redirigir según el rol (CustomRole)
                // ----------------------------------------------------
                .successHandler(customRoleHandler) 
                .permitAll()
            )

            // Habilita logout para todos los usuarios
            .logout(logout -> logout.permitAll())

            // Deshabilita CSRF para evitar conflictos (especialmente útil durante desarrollo)
            .csrf(csrf -> csrf.disable());

        return http.build(); // Construye y retorna la cadena de filtros de seguridad
    }
}
