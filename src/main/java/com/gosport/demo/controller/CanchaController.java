package com.gosport.demo.controller;

import com.gosport.demo.model.Cancha;
import com.gosport.demo.model.Deporte;
import com.gosport.demo.repository.CanchaRepository;
import com.gosport.demo.repository.DeporteRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin/canchas")
public class CanchaController {

    private final CanchaRepository canchaRepository;
    private final DeporteRepository deporteRepository;
    
    // Carpeta donde se guardarán las fotos
    private static final String UPLOAD_DIR = "src/main/resources/static/images/canchas/";

    public CanchaController(CanchaRepository canchaRepository, DeporteRepository deporteRepository) {
        this.canchaRepository = canchaRepository;
        this.deporteRepository = deporteRepository;
    }

    // ===============================
    // LISTAR CANCHAS
    // ===============================
    @GetMapping
    public String listarCanchas(Model model) {
        List<Cancha> canchas = canchaRepository.findAll();
        model.addAttribute("canchas", canchas);
        model.addAttribute("totalCanchas", canchas.size());
        return "admin/canchas/lista";
    }

    // ===============================
    // MOSTRAR FORMULARIO NUEVO
    // ===============================
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("cancha", new Cancha());
        model.addAttribute("deportes", deporteRepository.findAll());
        model.addAttribute("accion", "Crear");
        return "admin/canchas/form";
    }

    // ===============================
    // GUARDAR CANCHA
    // ===============================
    @PostMapping("/guardar")
    public String guardarCancha(@ModelAttribute Cancha cancha,
                                @RequestParam("fotoFile") MultipartFile fotoFile,
                                RedirectAttributes redirectAttributes) {
        try {
            // Subir foto si se proporcionó
            if (!fotoFile.isEmpty()) {
                String fotoUrl = guardarFoto(fotoFile);
                cancha.setFotoUrl(fotoUrl);
            }
            
            canchaRepository.save(cancha);
            redirectAttributes.addFlashAttribute("successMessage", "Cancha guardada correctamente.");
            return "redirect:/admin/canchas";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
            return "redirect:/admin/canchas/nuevo";
        }
    }

    // ===============================
    // MOSTRAR FORMULARIO EDITAR
    // ===============================
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Cancha cancha = canchaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cancha no encontrada"));
        
        model.addAttribute("cancha", cancha);
        model.addAttribute("deportes", deporteRepository.findAll());
        model.addAttribute("accion", "Editar");
        return "admin/canchas/form";
    }

    // ===============================
    // ELIMINAR CANCHA
    // ===============================
    @GetMapping("/eliminar/{id}")
    public String eliminarCancha(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            canchaRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Cancha eliminada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/admin/canchas";
    }

    // ===============================
    // CAMBIAR ESTADO
    // ===============================
    @GetMapping("/cambiar-estado/{id}")
    public String cambiarEstado(@PathVariable Long id, 
                                @RequestParam String nuevoEstado,
                                RedirectAttributes redirectAttributes) {
        try {
            Cancha cancha = canchaRepository.findById(id).orElseThrow();
            cancha.setEstado(nuevoEstado);
            canchaRepository.save(cancha);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Estado cambiado a: " + nuevoEstado);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/admin/canchas";
    }

    // ===============================
    // MÉTODO AUXILIAR: GUARDAR FOTO
    // ===============================
    private String guardarFoto(MultipartFile file) throws IOException {
        // Crear directorio si no existe
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generar nombre único para el archivo
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        // Guardar archivo
        Files.copy(file.getInputStream(), filePath);

        // Retornar la URL relativa
        return "/images/canchas/" + fileName;
    }
}