package com.gosport.demo.controller;

import com.gosport.demo.model.Cancha;
import com.gosport.demo.repository.CanchaRepository;
import com.gosport.demo.repository.DeporteRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/canchas")
public class CanchaPublicController {

    private final CanchaRepository canchaRepository;
    private final DeporteRepository deporteRepository;

    public CanchaPublicController(CanchaRepository canchaRepository, DeporteRepository deporteRepository) {
        this.canchaRepository = canchaRepository;
        this.deporteRepository = deporteRepository;
    }

    // ===============================
    // VISTA PÚBLICA: LISTA DE CANCHAS
    // ===============================
    @GetMapping
    public String listarCanchasPublicas(
            @RequestParam(value = "deporte", required = false) Long deporteId,
            @RequestParam(value = "search", required = false) String search,
            Model model) {
        
        List<Cancha> canchas;
        
        if (deporteId != null) {
            // Filtrar por deporte
            canchas = canchaRepository.findByDeporte(
                deporteRepository.findById(deporteId).orElseThrow()
            );
        } else if (search != null && !search.trim().isEmpty()) {
            // Buscar por nombre o localidad
            canchas = canchaRepository.findByNombreContainingIgnoreCaseOrLocalidadContainingIgnoreCase(
                search, search
            );
        } else {
            // Mostrar solo canchas disponibles
            canchas = canchaRepository.findByEstadoOrderByNombreAsc("disponible");
        }
        
        model.addAttribute("canchas", canchas);
        model.addAttribute("deportes", deporteRepository.findAll());
        model.addAttribute("deporteSeleccionado", deporteId);
        model.addAttribute("searchQuery", search);
        
        return "canchas/lista-publica";
    }

    // ===============================
    // DETALLE DE CANCHA
    // ===============================
    @GetMapping("/{id}")
    public String verDetalle(@PathVariable Long id, Model model) {
        Cancha cancha = canchaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cancha no encontrada"));
        
        model.addAttribute("cancha", cancha);
        return "canchas/detalle";
    }
}