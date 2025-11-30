package com.gosport.demo.config;

import com.gosport.demo.model.User;
import com.gosport.demo.model.Deporte;
import com.gosport.demo.model.Cancha;
import com.gosport.demo.repository.UserRepository;
import com.gosport.demo.repository.DeporteRepository;
import com.gosport.demo.repository.CanchaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initDatabase(UserRepository userRepo, 
                                          DeporteRepository deporteRepo,
                                          CanchaRepository canchaRepo,
                                          PasswordEncoder passwordEncoder) {
        return args -> {
            // ========================================
            // 1. CREAR USUARIO ADMIN
            // ========================================
            if (userRepo.findByEmail("admin@gosport.com") == null) {
                User admin = new User();
                admin.setName("Administrador GoSport");
                admin.setEmail("admin@gosport.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRol("ADMIN");
                admin.setActivo(true);
                admin.setTelefono("3001234567");
                admin.setTipoDocumento("CC");
                admin.setNumeroIdentificacion("1234567890");
                admin.setGenero("Otro");
                admin.setCreatedAt(LocalDateTime.now());
                admin.setUpdatedAt(LocalDateTime.now());
                
                userRepo.save(admin);
                System.out.println("✅ Usuario ADMIN creado exitosamente");
            } else {
                System.out.println("ℹ️ Usuario ADMIN ya existe");
            }

            // ========================================
            // 2. CREAR DEPORTES
            // ========================================
            String[] deportes = {"Fútbol", "Baloncesto", "Tenis", "Voleibol"};
            String[] iconos = {"bi-trophy-fill", "bi-dribbble", "bi-circle", "bi-circle-fill"};
            
            for (int i = 0; i < deportes.length; i++) {
                if (deporteRepo.findByNombre(deportes[i]).isEmpty()) {
                    Deporte deporte = new Deporte();
                    deporte.setNombre(deportes[i]);
                    deporte.setDescripcion("Deporte de " + deportes[i]);
                    deporte.setIcono(iconos[i]);
                    deporteRepo.save(deporte);
                    System.out.println("✅ Deporte creado: " + deportes[i]);
                }
            }

            // ========================================
            // 3. CREAR CANCHAS DE EJEMPLO
            // ========================================
            if (canchaRepo.count() == 0) {
                Deporte futbol = deporteRepo.findByNombre("Fútbol").orElseThrow();
                Deporte baloncesto = deporteRepo.findByNombre("Baloncesto").orElseThrow();
                Deporte tenis = deporteRepo.findByNombre("Tenis").orElseThrow();

                // Cancha Bosa 1 - Fútbol
                Cancha cancha1 = new Cancha();
                cancha1.setNombre("Cancha Bosa 1");
                cancha1.setDeporte(futbol);
                cancha1.setDescripcion("Cancha de fútbol sintético con iluminación LED");
                cancha1.setDireccion("Calle 56 Sur #80D-23");
                cancha1.setBarrio("Bosa Centro");
                cancha1.setLocalidad("Bosa");
                cancha1.setFotoUrl("/images/canchas/bosa1.jpg");
                cancha1.setPrecioHora(new BigDecimal("80000"));
                cancha1.setPrecioMediaHora(new BigDecimal("45000"));
                cancha1.setHoraApertura(LocalTime.of(8, 0));
                cancha1.setHoraCierre(LocalTime.of(22, 0));
                cancha1.setEstado("disponible");
                cancha1.setTieneIluminacion(true);
                cancha1.setTieneGraderias(false);
                cancha1.setTechada(false);
                cancha1.setCapacidadJugadores(10);
                canchaRepo.save(cancha1);

                // Cancha Bosa 2 - Fútbol
                Cancha cancha2 = new Cancha();
                cancha2.setNombre("Cancha Bosa 2");
                cancha2.setDeporte(futbol);
                cancha2.setDescripcion("Cancha de fútbol 7 con grama sintética");
                cancha2.setDireccion("Carrera 87 #63-12 Sur");
                cancha2.setBarrio("San Bernardino");
                cancha2.setLocalidad("Bosa");
                cancha2.setFotoUrl("/images/canchas/bosa2.jpg");
                cancha2.setPrecioHora(new BigDecimal("100000"));
                cancha2.setHoraApertura(LocalTime.of(7, 0));
                cancha2.setHoraCierre(LocalTime.of(23, 0));
                cancha2.setEstado("disponible");
                cancha2.setTieneIluminacion(true);
                cancha2.setTieneGraderias(true);
                cancha2.setTechada(false);
                cancha2.setCapacidadJugadores(14);
                canchaRepo.save(cancha2);

                // Tenis Bosa 1
                Cancha cancha3 = new Cancha();
                cancha3.setNombre("Tenis Bosa 1");
                cancha3.setDeporte(tenis);
                cancha3.setDescripcion("Cancha de tenis profesional con superficie rápida");
                cancha3.setDireccion("Calle 70 Sur #89A-45");
                cancha3.setBarrio("La Libertad");
                cancha3.setLocalidad("Bosa");
                cancha3.setFotoUrl("/images/canchas/tenis1.jpg");
                cancha3.setPrecioHora(new BigDecimal("60000"));
                cancha3.setHoraApertura(LocalTime.of(6, 0));
                cancha3.setHoraCierre(LocalTime.of(20, 0));
                cancha3.setEstado("disponible");
                cancha3.setTieneIluminacion(true);
                cancha3.setTieneGraderias(false);
                cancha3.setTechada(false);
                cancha3.setCapacidadJugadores(4);
                canchaRepo.save(cancha3);

                // Basket 1 - Baloncesto
                Cancha cancha4 = new Cancha();
                cancha4.setNombre("Basket 1");
                cancha4.setDeporte(baloncesto);
                cancha4.setDescripcion("Cancha de baloncesto techada con tableros profesionales");
                cancha4.setDireccion("Transversal 78K #65A-30 Sur");
                cancha4.setBarrio("El Porvenir");
                cancha4.setLocalidad("Bosa");
                cancha4.setFotoUrl("/images/canchas/basket1.jpg");
                cancha4.setPrecioHora(new BigDecimal("70000"));
                cancha4.setHoraApertura(LocalTime.of(8, 0));
                cancha4.setHoraCierre(LocalTime.of(21, 0));
                cancha4.setEstado("disponible");
                cancha4.setTieneIluminacion(true);
                cancha4.setTieneGraderias(true);
                cancha4.setTechada(true);
                cancha4.setCapacidadJugadores(10);
                canchaRepo.save(cancha4);

                System.out.println("✅ Canchas de ejemplo creadas exitosamente");
            } else {
                System.out.println("ℹ️ Ya existen canchas en la base de datos");
            }
        };
    }
}