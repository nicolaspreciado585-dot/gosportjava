package com.gosport.demo.service;

import com.gosport.demo.model.Reserva;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${gosports.mail.from:GoSports <noreply@gosports.com>}")
    private String fromEmail;

    @Value("${gosports.mail.enabled:true}")
    private boolean emailEnabled;

    // ====================================
    // EMAIL: NUEVA RESERVA CREADA
    // ====================================
    public void enviarEmailNuevaReserva(Reserva reserva) {
        if (!emailEnabled) {
            System.out.println("📧 Email deshabilitado. No se envió email de nueva reserva.");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(reserva.getUsuario().getEmail());
            helper.setSubject("✅ Reserva Creada - GoSports");
            helper.setText(construirHtmlNuevaReserva(reserva), true);

            mailSender.send(message);
            System.out.println("✅ Email de nueva reserva enviado a: " + reserva.getUsuario().getEmail());

        } catch (MessagingException e) {
            System.err.println("❌ Error al enviar email de nueva reserva: " + e.getMessage());
        }
    }

    // ====================================
    // EMAIL: RESERVA CONFIRMADA
    // ====================================
    public void enviarEmailReservaConfirmada(Reserva reserva) {
        if (!emailEnabled) return;

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(reserva.getUsuario().getEmail());
            helper.setSubject("🎉 Reserva Confirmada - GoSports");
            helper.setText(construirHtmlReservaConfirmada(reserva), true);

            mailSender.send(message);
            System.out.println("✅ Email de confirmación enviado a: " + reserva.getUsuario().getEmail());

        } catch (MessagingException e) {
            System.err.println("❌ Error al enviar email de confirmación: " + e.getMessage());
        }
    }

    // ====================================
    // EMAIL: RESERVA CANCELADA
    // ====================================
    public void enviarEmailReservaCancelada(Reserva reserva) {
        if (!emailEnabled) return;

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(reserva.getUsuario().getEmail());
            helper.setSubject("❌ Reserva Cancelada - GoSports");
            helper.setText(construirHtmlReservaCancelada(reserva), true);

            mailSender.send(message);
            System.out.println("✅ Email de cancelación enviado a: " + reserva.getUsuario().getEmail());

        } catch (MessagingException e) {
            System.err.println("❌ Error al enviar email de cancelación: " + e.getMessage());
        }
    }

    // ====================================
    // HTML: NUEVA RESERVA
    // ====================================
    private String construirHtmlNuevaReserva(Reserva reserva) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", new Locale("es", "CO"));
        String fechaFormateada = reserva.getFecha().format(dateFormatter);

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f8f9fa; padding: 30px; border-radius: 0 0 10px 10px; }
                    .info-box { background: white; padding: 20px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #667eea; }
                    .info-row { display: flex; justify-content: space-between; padding: 10px 0; border-bottom: 1px solid #eee; }
                    .info-label { font-weight: bold; color: #666; }
                    .info-value { color: #333; }
                    .button { display: inline-block; padding: 12px 30px; background: #667eea; color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                    .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
                    .status-badge { display: inline-block; padding: 5px 15px; background: #ffc107; color: #000; border-radius: 20px; font-weight: bold; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>¡Reserva Creada Exitosamente!</h1>
                        <p>Tu reserva ha sido registrada en GoSports</p>
                    </div>
                    
                    <div class="content">
                        <p>Hola <strong>%s</strong>,</p>
                        <p>Tu reserva ha sido creada exitosamente y está <span class="status-badge">PENDIENTE DE CONFIRMACIÓN</span></p>
                        
                        <div class="info-box">
                            <h3 style="margin-top: 0; color: #667eea;">📋 Detalles de la Reserva</h3>
                            
                            <div class="info-row">
                                <span class="info-label">Código de Reserva:</span>
                                <span class="info-value"><strong>%s</strong></span>
                            </div>
                            
                            <div class="info-row">
                                <span class="info-label">🏟️ Cancha:</span>
                                <span class="info-value">%s</span>
                            </div>
                            
                            <div class="info-row">
                                <span class="info-label">⚽ Deporte:</span>
                                <span class="info-value">%s</span>
                            </div>
                            
                            <div class="info-row">
                                <span class="info-label">📅 Fecha:</span>
                                <span class="info-value">%s</span>
                            </div>
                            
                            <div class="info-row">
                                <span class="info-label">🕐 Hora:</span>
                                <span class="info-value">%s</span>
                            </div>
                            
                            <div class="info-row">
                                <span class="info-label">⏱️ Duración:</span>
                                <span class="info-value">%.1f hora(s)</span>
                            </div>
                            
                            <div class="info-row">
                                <span class="info-label">💰 Precio Total:</span>
                                <span class="info-value" style="color: #28a745; font-size: 18px;"><strong>$%,d COP</strong></span>
                            </div>
                        </div>
                        
                        <div style="background: #fff3cd; padding: 15px; border-radius: 5px; margin: 20px 0;">
                            <strong>⚠️ Importante:</strong>
                            <p style="margin: 5px 0;">Tu reserva está pendiente de confirmación por parte del administrador. Recibirás un correo cuando sea confirmada.</p>
                        </div>
                        
                        <div style="text-align: center;">
                            <a href="http://localhost:8080/reservas/detalle/%d" class="button">
                                Ver Detalle de Reserva
                            </a>
                        </div>
                        
                        <div style="background: white; padding: 15px; border-radius: 5px; margin-top: 20px;">
                            <h4 style="color: #667eea;">📍 Ubicación de la Cancha</h4>
                            <p style="margin: 5px 0;"><strong>Dirección:</strong> %s</p>
                            <p style="margin: 5px 0;"><strong>Barrio:</strong> %s</p>
                            <p style="margin: 5px 0;"><strong>Localidad:</strong> %s</p>
                        </div>
                    </div>
                    
                    <div class="footer">
                        <p>Este es un correo automático, por favor no respondas a este mensaje.</p>
                        <p>© 2025 GoSports - Plataforma de Reserva de Canchas Deportivas</p>
                        <p>Si tienes alguna pregunta, contáctanos en: soporte@gosports.com</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                reserva.getUsuario().getName(),
                reserva.getCodigoReserva(),
                reserva.getCancha().getNombre(),
                reserva.getCancha().getDeporte().getNombre(),
                fechaFormateada,
                reserva.getHoraInicio().toString(),
                reserva.getDuracion(),
                reserva.getPrecioTotal().intValue(),
                reserva.getId(),
                reserva.getCancha().getDireccion(),
                reserva.getCancha().getBarrio(),
                reserva.getCancha().getLocalidad()
        );
    }

    // ====================================
    // HTML: RESERVA CONFIRMADA
    // ====================================
    private String construirHtmlReservaConfirmada(Reserva reserva) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", new Locale("es", "CO"));
        String fechaFormateada = reserva.getFecha().format(dateFormatter);

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #28a745 0%%, #20c997 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f8f9fa; padding: 30px; border-radius: 0 0 10px 10px; }
                    .info-box { background: white; padding: 20px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #28a745; }
                    .button { display: inline-block; padding: 12px 30px; background: #28a745; color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                    .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
                    .success-icon { font-size: 60px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="success-icon">✅</div>
                        <h1>¡Reserva Confirmada!</h1>
                        <p>Tu reserva ha sido aprobada</p>
                    </div>
                    
                    <div class="content">
                        <p>Hola <strong>%s</strong>,</p>
                        <p><strong>¡Excelentes noticias!</strong> Tu reserva ha sido confirmada por el administrador.</p>
                        
                        <div class="info-box">
                            <h3 style="margin-top: 0; color: #28a745;">📋 Tu Reserva Confirmada</h3>
                            <p><strong>Código:</strong> %s</p>
                            <p><strong>Cancha:</strong> %s</p>
                            <p><strong>Fecha:</strong> %s</p>
                            <p><strong>Hora:</strong> %s</p>
                            <p><strong>Precio:</strong> $%,d COP</p>
                        </div>
                        
                        <div style="background: #d1ecf1; padding: 15px; border-radius: 5px; margin: 20px 0; border-left: 4px solid #0c5460;">
                            <strong>📌 Recordatorio:</strong>
                            <p style="margin: 5px 0;">Por favor llega 10 minutos antes de tu hora reservada.</p>
                            <p style="margin: 5px 0;">Presenta tu código de reserva al llegar.</p>
                        </div>
                        
                        <div style="text-align: center;">
                            <a href="http://localhost:8080/reservas/detalle/%d" class="button">
                                Ver Código QR
                            </a>
                        </div>
                    </div>
                    
                    <div class="footer">
                        <p>© 2025 GoSports - ¡Nos vemos en la cancha! 🏆</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                reserva.getUsuario().getName(),
                reserva.getCodigoReserva(),
                reserva.getCancha().getNombre(),
                fechaFormateada,
                reserva.getHoraInicio().toString(),
                reserva.getPrecioTotal().intValue(),
                reserva.getId()
        );
    }

    // ====================================
    // HTML: RESERVA CANCELADA
    // ====================================
    private String construirHtmlReservaCancelada(Reserva reserva) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #dc3545 0%%, #c82333 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f8f9fa; padding: 30px; border-radius: 0 0 10px 10px; }
                    .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
                    .button { display: inline-block; padding: 12px 30px; background: #667eea; color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Reserva Cancelada</h1>
                    </div>
                    
                    <div class="content">
                        <p>Hola <strong>%s</strong>,</p>
                        <p>Tu reserva con código <strong>%s</strong> ha sido cancelada.</p>
                        
                        <div style="background: white; padding: 20px; border-radius: 8px; margin: 20px 0;">
                            <p><strong>Cancha:</strong> %s</p>
                            <p><strong>Fecha:</strong> %s</p>
                            <p><strong>Hora:</strong> %s</p>
                        </div>
                        
                        <p>Si tienes alguna pregunta, no dudes en contactarnos.</p>
                        
                        <div style="text-align: center;">
                            <a href="http://localhost:8080/canchas" class="button">
                                Hacer Nueva Reserva
                            </a>
                        </div>
                    </div>
                    
                    <div class="footer">
                        <p>© 2025 GoSports</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                reserva.getUsuario().getName(),
                reserva.getCodigoReserva(),
                reserva.getCancha().getNombre(),
                reserva.getFecha().toString(),
                reserva.getHoraInicio().toString()
        );
    }
}