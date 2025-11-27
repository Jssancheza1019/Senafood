// src/main/java/com/senafood/service/UserServiceImpl.java

package com.senafood.service;

import com.senafood.model.Role;
import com.senafood.model.User;
import com.senafood.repository.RoleRepository;
import com.senafood.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder; 
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional; //  Se agrega la importación para Optional

@Service
public class UserServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository; 
    private final PasswordEncoder passwordEncoder; 

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Método para Login (Usado por Spring Security)
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));
        return user; 
    }

    /**
     * Método para verificar si el email ya existe (Usado por AuthController)
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email); 
    }

    /**
     * Método para registrar un nuevo usuario (SAVE)
     */
    public User save(User registrationUser) {
        
        // Se usa getPassword() y setPassword() para ser consistentes con User.java
        String encodedPassword = passwordEncoder.encode(registrationUser.getPassword()); 
        registrationUser.setPassword(encodedPassword); 

        //  Asignar el Rol por defecto (Cliente)
        Role defaultRole = roleRepository.findByNombreRol("Cliente")
                .orElseThrow(() -> new RuntimeException("Error: Rol 'Cliente' no encontrado en la DB."));
        
        registrationUser.setRol(defaultRole);

        //  Guardar el objeto User encriptado en la DB
        return userRepository.save(registrationUser);
    }
}