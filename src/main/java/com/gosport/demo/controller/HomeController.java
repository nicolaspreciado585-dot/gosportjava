package com.gosport.demo.controller;

import com.gosport.demo.repository.CanchaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final CanchaRepository canchaRepository;

    public HomeController(CanchaRepository canchaRepository) {
        this.canchaRepository = canchaRepository;
    }

    /**
     * Página de inicio pública (landing page)
     */
    @GetMapping("/")
    public String index(Model model) {
        // Obtener las 3 canchas más recientes para mostrar en el landing
        var canchas = canchaRepository.findAll().stream()
            .limit(3)
            .toList();
        model.addAttribute("canchasDestacadas", canchas);
        return "index"; // Vista: index.html
    }

    /**
     * Página principal para usuarios autenticados
     */
    @GetMapping("/home")
    public String home(Model model, Authentication auth) {
        // Obtener el nombre del usuario autenticado
        String email = auth.getName();
        
        // Obtener los roles del usuario
        String roles = auth.getAuthorities().toString();
        
        model.addAttribute("userEmail", email);
        model.addAttribute("userRoles", roles);
        
        return "home"; // Vista: home.html
    }
}