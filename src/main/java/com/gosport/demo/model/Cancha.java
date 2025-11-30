package com.gosport.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "canchas")
@Data
public class Cancha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cancha")
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre; // Ej: "Cancha Bosa 1"

    @ManyToOne
    @JoinColumn(name = "id_deporte", nullable = false)
    private Deporte deporte;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    // Dirección
    @Column(length = 200)
    private String direccion;

    @Column(length = 100)
    private String barrio;

    @Column(length = 100)
    private String localidad;

    // Foto principal
    @Column(name = "foto_url", length = 500)
    private String fotoUrl;

    // Precios
    @Column(name = "precio_hora", precision = 10, scale = 2)
    private BigDecimal precioHora; // Precio por hora

    @Column(name = "precio_media_hora", precision = 10, scale = 2)
    private BigDecimal precioMediaHora; // Precio por media hora (opcional)

    // Horarios
    @Column(name = "hora_apertura")
    private LocalTime horaApertura; // Ej: 08:00

    @Column(name = "hora_cierre")
    private LocalTime horaCierre; // Ej: 22:00

    // Estado
    @Column(nullable = false, length = 20)
    private String estado = "disponible"; // disponible, mantenimiento, no_disponible

    // Características adicionales
    @Column(name = "tiene_iluminacion")
    private Boolean tieneIluminacion = false;

    @Column(name = "tiene_graderias")
    private Boolean tieneGraderias = false;

    @Column(name = "techada")
    private Boolean techada = false;

    @Column(name = "capacidad_jugadores")
    private Integer capacidadJugadores; // Ej: 10 para fútbol 5

    // Auditoría
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