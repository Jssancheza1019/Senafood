package com.senafood.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

/**
 * Clase encargada de redirigir al usuario después de un login exitoso
 * basado en el rol que tiene asignado.
 */
@Component
public class CustomRole implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                        HttpServletResponse response, 
                                        Authentication authentication) 
                                        throws IOException, ServletException {

        // Obtiene la colección de roles (Authorities) del usuario autenticado
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String redirectUrl = "/dashboard"; // URL de seguridad por defecto para el cliente

        // Iteramos sobre los roles. Spring Security añade automáticamente el prefijo "ROLE_"
        for (GrantedAuthority authority : authorities) {
            String roleName = authority.getAuthority();

            if (roleName.equals("ROLE_ADMINISTRADOR")) {
                // Si tiene rol de administrador, redirigir al panel de control de administración
                redirectUrl = "/admin/panel"; 
                break; 
            } 
            // Otros roles como ROLE_CLIENTE o ROLE_MESERO caerán en la URL por defecto (/dashboard)
        }

        // Ejecutar la redirección
        response.sendRedirect(redirectUrl);
    }
}
