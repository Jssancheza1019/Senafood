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
 * según el rol que tenga asignado dentro del sistema.
 */
@Component // Permite que Spring detecte esta clase como componente y pueda inyectarla donde se necesite
public class CustomRole implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
                                        throws IOException, ServletException {

        // Obtiene la lista de roles/autoridades que tiene el usuario autenticado
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // URL a la que será redirigido un usuario sin rol especial 
        String redirectUrl = "/dashboard";

        // Recorre los roles para verificar si el usuario tiene el rol de administrador
        // Spring Security agrega automáticamente el prefijo "ROLE_" a los roles declarados
        for (GrantedAuthority authority : authorities) {
            String roleName = authority.getAuthority();

            if (roleName.equals("ROLE_ADMINISTRADOR")) {
                // Si el rol encontrado es administrador, cambia la URL de redirección
                redirectUrl = "/admin/panel";
                break; // Sale del ciclo porque ya encontró el rol
                        } else if (roleName.equals("ROLE_VENDEDOR")) {
                redirectUrl = "/vendedor/dashboard";
                break;
            } else if (roleName.equals("ROLE_CLIENTE")) {
                redirectUrl = "/dashboard"; // Redirigir al catálogo
                break;
            }
            // Si el rol es ROLE_CLIENTE u otro, se mantiene la URL por defecto
        }

        // Realiza la redirección final dependiendo del rol encontrado
        response.sendRedirect(redirectUrl);
    }
}
