package com.gosport.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gosport.demo.model.Pago;
import com.gosport.demo.model.Reserva;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class WompiService {

    @Value("${wompi.public.key}")
    private String publicKey;

    @Value("${wompi.private.key}")
    private String privateKey;

    @Value("${wompi.api.url}")
    private String apiUrl;

    @Value("${app.base.url}")
    private String baseUrl;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public WompiService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    /**
     * Crear transacción de pago en Wompi
     */
    public Map<String, Object> crearTransaccion(Reserva reserva, Pago.MetodoPago metodoPago) {
        try {
            // Preparar datos de la transacción
            Map<String, Object> transaction = new HashMap<>();
            
            // Monto en centavos (Wompi trabaja con centavos)
            BigDecimal montoEnCentavos = reserva.getPrecioTotal().multiply(new BigDecimal("100"));
            transaction.put("amount_in_cents", montoEnCentavos.intValue());
            transaction.put("currency", "COP");
            transaction.put("customer_email", reserva.getUsuario().getEmail());
            
            // Referencia única
            String referencia = generarReferenciaUnica(reserva);
            transaction.put("reference", referencia);
            
            // URLs de redirección
            transaction.put("redirect_url", baseUrl + "/pagos/confirmacion");
            
            // Información del cliente
            Map<String, Object> customerData = new HashMap<>();
            customerData.put("email", reserva.getUsuario().getEmail());
            customerData.put("full_name", reserva.getUsuario().getName());
            customerData.put("phone_number", reserva.getUsuario().getTelefono());
            transaction.put("customer_data", customerData);
            
            // Configurar método de pago específico
            configurarMetodoPago(transaction, metodoPago);
            
            // Firma de integridad
            String integritySignature = generarFirmaIntegridad(referencia, montoEnCentavos.intValue());
            
            // Hacer petición a Wompi
            String response = webClient.post()
                .uri(apiUrl + "/transactions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + publicKey)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(transaction)
                .retrieve()
                .bodyToMono(String.class)
                .block();
            
            // Parsear respuesta
            JsonNode jsonResponse = objectMapper.readTree(response);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("transaction_id", jsonResponse.get("data").get("id").asText());
            result.put("payment_link", jsonResponse.get("data").get("payment_link_url").asText());
            result.put("referencia", referencia);
            result.put("integrity_signature", integritySignature);
            
            return result;
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return error;
        }
    }

    /**
     * Consultar estado de transacción
     */
    public Map<String, Object> consultarEstadoTransaccion(String transactionId) {
        try {
            String response = webClient.get()
                .uri(apiUrl + "/transactions/" + transactionId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + publicKey)
                .retrieve()
                .bodyToMono(String.class)
                .block();
            
            JsonNode jsonResponse = objectMapper.readTree(response);
            JsonNode data = jsonResponse.get("data");
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("status", data.get("status").asText());
            result.put("payment_method", data.get("payment_method_type").asText());
            result.put("reference", data.get("reference").asText());
            result.put("amount", data.get("amount_in_cents").asInt() / 100.0);
            
            return result;
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return error;
        }
    }

    /**
     * Generar referencia única para el pago
     */
    private String generarReferenciaUnica(Reserva reserva) {
        long timestamp = Instant.now().toEpochMilli();
        return "RES-" + reserva.getId() + "-" + timestamp;
    }

    /**
     * Generar firma de integridad (para validar webhooks)
     */
    private String generarFirmaIntegridad(String referencia, int montoCentavos) {
        try {
            String cadena = referencia + montoCentavos + "COP" + privateKey;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(cadena.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
            
        } catch (Exception e) {
            throw new RuntimeException("Error al generar firma de integridad", e);
        }
    }

    /**
     * Configurar método de pago específico
     */
    private void configurarMetodoPago(Map<String, Object> transaction, Pago.MetodoPago metodoPago) {
        Map<String, Object> paymentMethod = new HashMap<>();
        
        switch (metodoPago) {
            case PSE:
                paymentMethod.put("type", "PSE");
                paymentMethod.put("user_type", "0"); // 0 = Persona natural
                break;
            case NEQUI:
                paymentMethod.put("type", "NEQUI");
                break;
            case BANCOLOMBIA:
                paymentMethod.put("type", "BANCOLOMBIA_TRANSFER");
                break;
            case CARD:
                paymentMethod.put("type", "CARD");
                paymentMethod.put("installments", 1); // Sin cuotas por defecto
                break;
            default:
                paymentMethod.put("type", "CARD");
        }
        
        transaction.put("payment_method", paymentMethod);
    }

    /**
     * Verificar firma de webhook
     */
    public boolean verificarFirmaWebhook(String signature, String timestamp, String eventData) {
        try {
            String cadena = timestamp + "." + eventData;
            String generatedSignature = generarFirmaIntegridad(cadena, 0);
            return generatedSignature.equals(signature);
        } catch (Exception e) {
            return false;
        }
    }
}