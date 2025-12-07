package com.gosport.demo.controller;

import com.gosport.demo.model.Pago;
import com.gosport.demo.model.Reserva;
import com.gosport.demo.service.PagoService;
import com.gosport.demo.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/pagos")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @Autowired
    private ReservaService reservaService;

    /**
     * Seleccionar método de pago
     */
    @GetMapping("/metodo/{reservaId}")
    public String seleccionarMetodoPago(
            @PathVariable Long reservaId,
            Model model,
            Authentication auth,
            RedirectAttributes redirectAttributes) {

        try {
            Reserva reserva = reservaService.obtenerPorId(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

            // Verificar que la reserva pertenece al usuario
            if (!reserva.getUsuario().getEmail().equals(auth.getName())) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "No tienes permiso para pagar esta reserva");
                return "redirect:/reservas/mis-reservas";
            }

            // Verificar si ya tiene un pago
            Optional<Pago> pagoExistente = pagoService.obtenerPagoPorReserva(reservaId);
            if (pagoExistente.isPresent()) {
                return "redirect:/pagos/estado/" + pagoExistente.get().getReferenciaPago();
            }

            model.addAttribute("reserva", reserva);
            model.addAttribute("metodosPago", Pago.MetodoPago.values());

            return "pagos/seleccionar-metodo";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/reservas/mis-reservas";
        }
    }

    /**
     * Procesar pago
     */
    @PostMapping("/procesar")
    public String procesarPago(
            @RequestParam Long reservaId,
            @RequestParam String metodoPago,
            Authentication auth,
            RedirectAttributes redirectAttributes) {

        try {
            Reserva reserva = reservaService.obtenerPorId(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

            // Verificar permisos
            if (!reserva.getUsuario().getEmail().equals(auth.getName())) {
                throw new RuntimeException("No tienes permiso para pagar esta reserva");
            }

            // Iniciar proceso de pago
            Pago.MetodoPago metodo = Pago.MetodoPago.valueOf(metodoPago);
            Pago pago = pagoService.iniciarPago(reserva, metodo);

            // Redirigir a Wompi para completar el pago
            return "redirect:" + pago.getWompiPaymentLink();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error al procesar el pago: " + e.getMessage());
            return "redirect:/pagos/metodo/" + reservaId;
        }
    }

    /**
     * Página de confirmación (después del pago)
     */
    @GetMapping("/confirmacion")
    public String confirmacionPago(
            @RequestParam(required = false) String id, // Transaction ID de Wompi
            Model model) {

        if (id != null) {
            try {
                // Sincronizar estado con Wompi
                pagoService.sincronizarEstadoPago(id);
                
                Optional<Pago> pagoOpt = pagoService.obtenerPagoPorReserva(null);
                if (pagoOpt.isPresent()) {
                    model.addAttribute("pago", pagoOpt.get());
                }
            } catch (Exception e) {
                model.addAttribute("errorMessage", "Error al verificar el pago");
            }
        }

        return "pagos/confirmacion";
    }

    /**
     * Consultar estado del pago
     */
    @GetMapping("/estado/{referencia}")
    public String estadoPago(
            @PathVariable String referencia,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            Pago pago = pagoService.consultarEstadoPago(referencia);
            
            // Sincronizar con Wompi antes de mostrar
            if (pago.getWompiTransactionId() != null) {
                pagoService.sincronizarEstadoPago(pago.getWompiTransactionId());
                // Recargar el pago actualizado
                pago = pagoService.consultarEstadoPago(referencia);
            }

            model.addAttribute("pago", pago);
            return "pagos/estado";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/reservas/mis-reservas";
        }
    }

    /**
     * Webhook de Wompi (recibe notificaciones de estado)
     */
    @PostMapping("/webhook")
    @ResponseBody
    public String webhookWompi(@RequestBody String payload) {
        try {
            // Aquí procesarías el webhook de Wompi
            // Por seguridad, debes verificar la firma del webhook
            
            System.out.println("Webhook recibido: " + payload);
            
            // TODO: Implementar validación de firma y procesamiento
            
            return "OK";
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
}