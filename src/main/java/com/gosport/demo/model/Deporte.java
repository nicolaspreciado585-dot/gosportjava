package com.gosport.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "deportes")
@Data
public class Deporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_deporte")
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String nombre; // Fútbol, Baloncesto, Tenis, etc.

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "icono", length = 100)
    private String icono; // Nombre del icono (ej: "bi-soccer", "bi-basketball")

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}