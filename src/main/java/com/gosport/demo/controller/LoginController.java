package com.gosport.demo.controller;

import com.gosport.demo.model.User;
import com.gosport.demo.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    private final UserService userService;

    // Inyección de dependencia (Spring inyecta UserServiceImpl)
    public LoginController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Muestra la vista de login (GET /login)
     */
    @GetMapping("/login")
    public String showLoginForm() {
        // Retorna el nombre del template: login.html
        return "login"; 
    }

    /**
     * Maneja la petición POST del formulario de login
     */
    @PostMapping("/login")
    public String processLogin(@RequestParam("email") String email,
                               @RequestParam("password") String password,
                               RedirectAttributes redirectAttributes,
                               Model model) {

        // Llama al servicio para autenticar al usuario (usa BCrypt internamente)
        User authenticatedUser = userService.authenticateUser(email, password);

        if (authenticatedUser != null) {
            
            // 1. ÉXITO: Usuario y contraseña válidos
            
            // TODO: En el siguiente paso, aquí estableceremos la sesión del usuario.
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "¡Bienvenido, " + authenticatedUser.getName() + "! Has iniciado sesión.");
            
            // Redirige a la página principal (index.html)
            return "redirect:/";

        } else {
            
            // 2. FALLO: Credenciales inválidas
            model.addAttribute("loginError", "Credenciales incorrectas. Verifique su email y contraseña.");
            
            // Permanece en la vista de login para mostrar el error
            return "login";
        }
    }
}