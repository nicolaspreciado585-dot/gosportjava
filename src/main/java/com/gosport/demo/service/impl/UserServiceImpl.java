package com.gosport.demo.service.impl;

import com.gosport.demo.model.User;
import com.gosport.demo.repository.UserRepository;
import com.gosport.demo.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder; // NECESARIO para cifrar

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    // 1. Inyección del PasswordEncoder (BCrypt)
    private final PasswordEncoder passwordEncoder; 

    // Constructor que inyecta ambos componentes
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder; 
    }
    
    // ----------------------------------------------------------------
    // MÉTODO DE REGISTRO (CIFRADO)
    // ----------------------------------------------------------------
    @Override
    public User saveUser(User user) {
        // Ciframos la contraseña antes de guardarla
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        
        return userRepository.save(user);
    }
    
    // ----------------------------------------------------------------
    // MÉTODO DE AUTENTICACIÓN (VERIFICACIÓN SEGURA)
    // ----------------------------------------------------------------
    @Override
    public User authenticateUser(String email, String password) {
        
        // 1. Buscamos el usuario por email
        User user = userRepository.findByEmail(email);

        if (user != null) {
            // 2. Verificación segura con BCrypt:
            // Compara la contraseña plana (password) con la cifrada (user.getPassword())
            if (passwordEncoder.matches(password, user.getPassword())) {
                return user; // Éxito: Contraseña válida
            }
        }
        
        // 3. Fallo: Usuario no encontrado o contraseña incorrecta
        return null;
    }

    // ----------------------------------------------------------------
    // OTROS MÉTODOS
    // ----------------------------------------------------------------
    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findUserById(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findUserById'");
    }
}