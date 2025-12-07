package com.gosport.demo.repository;

import com.gosport.demo.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    // Buscar pago por referencia única
    Optional<Pago> findByReferenciaPago(String referenciaPago);

    // Buscar pago por ID de transacción de Wompi
    Optional<Pago> findByWompiTransactionId(String wompiTransactionId);

    // Buscar pago por reserva
    Optional<Pago> findByReservaId(Long reservaId);

    // Buscar pagos por estado
    List<Pago> findByEstado(Pago.EstadoPago estado);

    // Buscar pagos por método de pago
    List<Pago> findByMetodoPago(Pago.MetodoPago metodoPago);

    // Buscar pagos del usuario
    @Query("SELECT p FROM Pago p WHERE p.reserva.usuario.id = :usuarioId ORDER BY p.createdAt DESC")
    List<Pago> findByUsuarioId(@Param("usuarioId") Long usuarioId);

    // Calcular ingresos totales aprobados
    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM Pago p WHERE p.estado = 'APROBADO'")
    BigDecimal calcularIngresosTotales();

    // Calcular ingresos del mes
    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM Pago p " +
           "WHERE p.estado = 'APROBADO' " +
           "AND MONTH(p.fechaPago) = :mes AND YEAR(p.fechaPago) = :anio")
    BigDecimal calcularIngresosMes(@Param("mes") int mes, @Param("anio") int anio);

    // Contar pagos por estado
    long countByEstado(Pago.EstadoPago estado);
}