package com.gosport.demo.controller;

import com.gosport.demo.model.User;
import com.gosport.demo.service.UserService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import java.time.LocalDateTime; // Necesario para created_at/updated_at

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ------------------- 1. LOGIN -------------------

   
   
   
    
    // ------------------- 2. REGISTRO -------------------

    // GET /registro: Muestra el formulario de registro
    @GetMapping("/registro")
    public String showRegisterForm() {
        return "register";
    }

    // POST /registro: Procesa el registro
    @PostMapping("/registro")
    public String registerUser(
            // Nuevos campos del formulario de Laravel:
            @RequestParam String nombre,
            @RequestParam String apellidos,
            @RequestParam String telefono,
            @RequestParam(name = "tipo_documento") String tipoDocumento,
            @RequestParam(name = "numero_identificacion") String numeroIdentificacion,
            @RequestParam String genero,
            
            // Campos existentes:
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam(name = "password_confirmation") String passwordConfirmation,
            Model model) {
        
        // 1. Verificar que las contraseñas coincidan
        if (!password.equals(passwordConfirmation)) {
            model.addAttribute("errorMessage", "Las contraseñas no coinciden.");
            return "register";
        }
        
        try {
            // 2. Crear la nueva entidad User y asignar los campos
            User newUser = new User();
            
            // Asignar los campos
            newUser.setName(nombre + " " + apellidos); // Combinar nombre y apellido
            newUser.setEmail(email);
            newUser.setPassword(password); // SIN CIFRADO, solo para prueba inicial
            
            // Campos de detalle
            newUser.setTelefono(telefono);
            newUser.setTipoDocumento(tipoDocumento);
            newUser.setNumeroIdentificacion(numeroIdentificacion);
            newUser.setGenero(genero);
            
            // Timestamps (opcional)
            newUser.setCreatedAt(LocalDateTime.now());
            newUser.setUpdatedAt(LocalDateTime.now());

            // 3. Guardar en la base de datos
            userService.saveUser(newUser);

            // 4. Éxito: Redirigir al login con mensaje de éxito
            model.addAttribute("successMessage", "¡Registro exitoso! Ya puedes iniciar sesión.");
            return "login"; 
            
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // Manejar error de email/identificación duplicada
            model.addAttribute("errorMessage", "Error al registrar: El correo electrónico o el número de identificación ya están registrados.");
            return "register";
        } catch (Exception e) {
            // Error genérico
            model.addAttribute("errorMessage", "Error interno del servidor. Inténtalo de nuevo.");
            return "register";
        }
    }
}