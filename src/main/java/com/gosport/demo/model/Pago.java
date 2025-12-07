package com.gosport.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
@Data
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con la reserva
    @OneToOne
    @JoinColumn(name = "reserva_id", nullable = false, unique = true)
    private Reserva reserva;

    // Información del pago
    @Column(name = "referencia_pago", unique = true, nullable = false)
    private String referenciaPago; // ID único del pago

    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "moneda", nullable = false)
    private String moneda = "COP";

    // Método de pago
    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false)
    private MetodoPago metodoPago;

    // Estado del pago
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoPago estado = EstadoPago.PENDIENTE;

    // IDs de Wompi
    @Column(name = "wompi_transaction_id", unique = true)
    private String wompiTransactionId; // ID de transacción de Wompi

    @Column(name = "wompi_payment_link")
    private String wompiPaymentLink; // Link de pago de Wompi

    // Información adicional
    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "email_pagador")
    private String emailPagador;

    @Column(name = "nombre_pagador")
    private String nombrePagador;

    @Column(columnDefinition = "TEXT")
    private String metadata; // JSON con datos adicionales

    // Auditoría
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago; // Cuando se completó el pago

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ====================================
    // ENUMS
    // ====================================

    public enum MetodoPago {
        PSE("PSE - Débito Bancario"),
        NEQUI("Nequi"),
        DAVIPLATA("Daviplata"),
        BANCOLOMBIA("Transferencia Bancolombia"),
        CARD("Tarjeta de Crédito/Débito");

        private final String displayName;

        MetodoPago(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum EstadoPago {
        PENDIENTE("Pendiente", "warning"),
        PROCESANDO("Procesando", "info"),
        APROBADO("Aprobado", "success"),
        RECHAZADO("Rechazado", "danger"),
        ERROR("Error", "danger"),
        CANCELADO("Cancelado", "secondary");

        private final String displayName;
        private final String colorClass;

        EstadoPago(String displayName, String colorClass) {
            this.displayName = displayName;
            this.colorClass = colorClass;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getColorClass() {
            return colorClass;
        }
    }

    // ====================================
    // MÉTODOS DE UTILIDAD
    // ====================================

    public boolean isPendiente() {
        return estado == EstadoPago.PENDIENTE || estado == EstadoPago.PROCESANDO;
    }

    public boolean isAprobado() {
        return estado == EstadoPago.APROBADO;
    }

    public boolean isFallido() {
        return estado == EstadoPago.RECHAZADO || estado == EstadoPago.ERROR;
    }
}