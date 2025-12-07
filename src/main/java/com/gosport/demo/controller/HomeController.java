package com.gosport.demo.controller;

import com.gosport.demo.repository.CanchaRepository;
import com.gosport.demo.repository.DeporteRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    private final CanchaRepository canchaRepository;
    private final DeporteRepository deporteRepository;

    public HomeController(CanchaRepository canchaRepository, DeporteRepository deporteRepository) {
        this.canchaRepository = canchaRepository;
        this.deporteRepository = deporteRepository;
    }

    /**
     * ⭐ LANDING PAGE PÚBLICA (vista para usuarios NO autenticados)
     * Ruta: /
     */
    @GetMapping("/")
    public String landingPage(
            @RequestParam(required = false) String logout,
            Model model) {
        
        // Mensaje de logout exitoso
        if (logout != null) {
            model.addAttribute("logoutMessage", "Sesión cerrada correctamente.");
        }
        
        // Obtener 3 canchas destacadas para mostrar en el landing
        var canchasDestacadas = canchaRepository.findByEstadoOrderByNombreAsc("disponible")
            .stream()
            .limit(3)
            .toList();
        
        // Obtener deportes disponibles
        var deportes = deporteRepository.findAll();
        
        model.addAttribute("canchasDestacadas", canchasDestacadas);
        model.addAttribute("deportes", deportes);
        
        return "index"; // Vista: templates/index.html
    }

    /**
     * ⭐ HOME PROTEGIDO (vista para usuarios AUTENTICADOS)
     * Ruta: /home
     */
    @GetMapping("/home")
    public String homePage(Model model, Authentication auth) {
        // Obtener información del usuario autenticado
        String email = auth.getName();
        String roles = auth.getAuthorities().toString();
        
        model.addAttribute("userEmail", email);
        model.addAttribute("userRoles", roles);
        
        // Obtener canchas destacadas
        var canchasDestacadas = canchaRepository.findByEstadoOrderByNombreAsc("disponible")
            .stream()
            .limit(6)
            .toList();
        
        model.addAttribute("canchasDestacadas", canchasDestacadas);
        
        return "home"; // Vista: templates/home.html
    }
}