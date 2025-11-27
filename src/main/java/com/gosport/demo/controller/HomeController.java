package com.gosport.demo.controller;

import com.gosport.demo.model.User;
import com.gosport.demo.service.UserService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    // Inyección de la dependencia UserService (Spring busca UserServiceImpl)
    private final UserService userService;

    public HomeController(UserService userService) {
        // El constructor recibe la instancia del servicio que Spring Boot ya inicializó
        this.userService = userService; 
    }

    /**
     * Mapea la petición GET a la URL principal (/).
     * Obtiene una lista de usuarios del servicio y la pasa a la vista.
     */
    @GetMapping("/")
    public String index(Model model) {
        
        // 1. Llama al servicio para obtener todos los usuarios
        List<User> users = userService.findAllUsers();
        
        // 2. Adjunta la lista al objeto 'Model' para que la vista (Thymeleaf) la pueda usar
        model.addAttribute("users", users);
        
        // 3. Retorna el nombre del archivo de la vista (Spring busca 'src/main/resources/templates/index.html')
        return "index"; 
    }
}