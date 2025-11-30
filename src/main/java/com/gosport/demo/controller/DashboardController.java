package com.gosport.demo.controller;

import com.gosport.demo.model.User;
import com.gosport.demo.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    private final UserRepository userRepository;

    public DashboardController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ===============================
    // MOSTRAR DASHBOARD
    // ===============================
    @GetMapping("/admin/dashboard")
    public String mostrarDashboard(Model model) {
        
        // 1. ESTADÍSTICAS GENERALES
        long totalUsuarios = userRepository.count();
        long usuariosActivos = userRepository.findAll().stream()
            .filter(User::getActivo).count();
        long administradores = userRepository.findAll().stream()
            .filter(u -> "ADMIN".equals(u.getRol())).count();
        
        // 2. USUARIOS RECIENTES (últimos 5)
        List<User> usuariosRecientes = userRepository.findAll().stream()
            .sorted(Comparator.comparing(User::getCreatedAt).reversed())
            .limit(5)
            .collect(Collectors.toList());
        
        // 3. CALCULAR CRECIMIENTO (comparar con mes anterior)
        LocalDateTime haceUnMes = LocalDateTime.now().minusMonths(1);
        long usuariosUltimoMes = userRepository.findAll().stream()
            .filter(u -> u.getCreatedAt().isAfter(haceUnMes))
            .count();
        
        // Pasar datos al modelo
        model.addAttribute("totalUsuarios", totalUsuarios);
        model.addAttribute("usuariosActivos", usuariosActivos);
        model.addAttribute("administradores", administradores);
        model.addAttribute("usuariosUltimoMes", usuariosUltimoMes);
        model.addAttribute("usuariosRecientes", usuariosRecientes);
        
        return "admin/dashboard";
    }

    // ===============================
    // API: DATOS PARA GRÁFICO DE USUARIOS POR MES
    // ===============================
    @GetMapping("/admin/dashboard/usuarios-por-mes")
    @ResponseBody
    public Map<String, Object> obtenerUsuariosPorMes() {
        List<User> usuarios = userRepository.findAll();
        
        // Agrupar usuarios por mes
        Map<String, Long> usuariosPorMes = usuarios.stream()
            .collect(Collectors.groupingBy(
                u -> u.getCreatedAt().getMonth().getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("es")) 
                     + " " + u.getCreatedAt().getYear(),
                Collectors.counting()
            ));
        
        // Preparar datos para Chart.js
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("labels", new ArrayList<>(usuariosPorMes.keySet()));
        resultado.put("data", new ArrayList<>(usuariosPorMes.values()));
        
        return resultado;
    }

    // ===============================
    // API: DISTRIBUCIÓN DE ROLES
    // ===============================
    @GetMapping("/admin/dashboard/distribucion-roles")
    @ResponseBody
    public Map<String, Object> obtenerDistribucionRoles() {
        List<User> usuarios = userRepository.findAll();
        
        long admins = usuarios.stream().filter(u -> "ADMIN".equals(u.getRol())).count();
        long users = usuarios.stream().filter(u -> "USER".equals(u.getRol())).count();
        
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("labels", Arrays.asList("Administradores", "Usuarios"));
        resultado.put("data", Arrays.asList(admins, users));
        
        return resultado;
    }

    // ===============================
    // API: INGRESOS MENSUALES (SIMULADO)
    // ===============================
    @GetMapping("/admin/dashboard/ingresos-mensuales")
    @ResponseBody
    public Map<String, Object> obtenerIngresosMensuales() {
        // DATOS SIMULADOS - Reemplazar cuando tengas el módulo de Reservas
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("labels", Arrays.asList("Ene", "Feb", "Mar", "Abr", "May", "Jun", 
                                               "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"));
        resultado.put("data", Arrays.asList(120000, 150000, 180000, 200000, 250000, 280000,
                                            300000, 320000, 350000, 380000, 400000, 450000));
        
        return resultado;
    }

    // ===============================
    // API: CANCHAS MÁS RESERVADAS (SIMULADO)
    // ===============================
    @GetMapping("/admin/dashboard/canchas-populares")
    @ResponseBody
    public Map<String, Object> obtenerCanchasPopulares() {
        // DATOS SIMULADOS - Reemplazar cuando tengas el módulo de Canchas
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("labels", Arrays.asList("Fútbol 5 - Norte", "Baloncesto Central", 
                                               "Tenis Premium", "Fútbol 7 - Sur", "Voleibol Playa"));
        resultado.put("data", Arrays.asList(45, 38, 32, 28, 22));
        
        return resultado;
    }
}