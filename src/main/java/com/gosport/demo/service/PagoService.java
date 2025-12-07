package com.gosport.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gosport.demo.model.Pago;
import com.gosport.demo.model.Reserva;
import com.gosport.demo.repository.PagoRepository;
import com.gosport.demo.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class PagoService {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private WompiService wompiService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Iniciar proceso de pago para una reserva
     */
    public Pago iniciarPago(Reserva reserva, Pago.MetodoPago metodoPago) {
        // Verificar que la reserva no tenga un pago existente
        Optional<Pago> pagoExistente = pagoRepository.findByReservaId(reserva.getId());
        if (pagoExistente.isPresent()) {
            throw new RuntimeException("Esta reserva ya tiene un pago asociado");
        }

        // Crear transacción en Wompi
        Map<String, Object> transactionResult = wompiService.crearTransaccion(reserva, metodoPago);
        
        if (!(boolean) transactionResult.get("success")) {
            throw new RuntimeException("Error al crear transacción: " + transactionResult.get("error"));
        }

        // Crear registro de pago
        Pago pago = new Pago();
        pago.setReserva(reserva);
        pago.setReferenciaPago((String) transactionResult.get("referencia"));
        pago.setMonto(reserva.getPrecioTotal());
        pago.setMoneda("COP");
        pago.setMetodoPago(metodoPago);
        pago.setEstado(Pago.EstadoPago.PENDIENTE);
        pago.setWompiTransactionId((String) transactionResult.get("transaction_id"));
        pago.setWompiPaymentLink((String) transactionResult.get("payment_link"));
        pago.setEmailPagador(reserva.getUsuario().getEmail());
        pago.setNombrePagador(reserva.getUsuario().getName());
        pago.setDescripcion("Pago de reserva - " + reserva.getCancha().getNombre());

        // Guardar metadata como JSON
        try {
            String metadata = objectMapper.writeValueAsString(Map.of(
                "cancha", reserva.getCancha().getNombre(),
                "fecha", reserva.getFecha().toString(),
                "hora", reserva.getHoraInicio().toString(),
                "duracion", reserva.getDuracion()
            ));
            pago.setMetadata(metadata);
        } catch (Exception e) {
            // Ignorar error de metadata
        }

        return pagoRepository.save(pago);
    }

    /**
     * Procesar confirmación de pago (webhook de Wompi)
     */
    public void procesarConfirmacionPago(String transactionId, String status) {
        Optional<Pago> pagoOpt = pagoRepository.findByWompiTransactionId(transactionId);
        
        if (pagoOpt.isEmpty()) {
            throw new RuntimeException("Pago no encontrado: " + transactionId);
        }

        Pago pago = pagoOpt.get();
        Reserva reserva = pago.getReserva();

        // Actualizar estado según respuesta de Wompi
        switch (status.toUpperCase()) {
            case "APPROVED":
                pago.setEstado(Pago.EstadoPago.APROBADO);
                pago.setFechaPago(LocalDateTime.now());
                
                // Actualizar estado de la reserva a CONFIRMADA
                reserva.setEstado(Reserva.EstadoReserva.CONFIRMADA);
                reservaRepository.save(reserva);
                
                // Enviar email de confirmación
                emailService.enviarEmailReservaConfirmada(reserva);
                break;
                
            case "PENDING":
                pago.setEstado(Pago.EstadoPago.PROCESANDO);
                break;
                
            case "DECLINED":
            case "VOIDED":
                pago.setEstado(Pago.EstadoPago.RECHAZADO);
                
                // Cancelar la reserva
                reserva.setEstado(Reserva.EstadoReserva.CANCELADA);
                reservaRepository.save(reserva);
                break;
                
            case "ERROR":
                pago.setEstado(Pago.EstadoPago.ERROR);
                break;
        }

        pagoRepository.save(pago);
    }

    /**
     * Consultar estado de pago
     */
    public Pago consultarEstadoPago(String referenciaPago) {
        return pagoRepository.findByReferenciaPago(referenciaPago)
            .orElseThrow(() -> new RuntimeException("Pago no encontrado"));
    }

    /**
     * Obtener pago por reserva
     */
    public Optional<Pago> obtenerPagoPorReserva(Long reservaId) {
        return pagoRepository.findByReservaId(reservaId);
    }

    /**
     * Obtener pagos del usuario
     */
    public List<Pago> obtenerPagosPorUsuario(Long usuarioId) {
        return pagoRepository.findByUsuarioId(usuarioId);
    }

    /**
     * Sincronizar estado con Wompi (consultar directamente a la API)
     */
    public void sincronizarEstadoPago(String transactionId) {
        Map<String, Object> result = wompiService.consultarEstadoTransaccion(transactionId);
        
        if ((boolean) result.get("success")) {
            String status = (String) result.get("status");
            procesarConfirmacionPago(transactionId, status);
        }
    }
}