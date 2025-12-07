package com.gosport.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
    // Recursos públicos
    .requestMatchers(
        "/",
        "/login",
        "/registro",
        "/css/**",
        "/js/**",
        "/images/**",
        "/canchas",
        "/canchas/**"
    ).permitAll()
    
    // ⭐ NUEVO: API de reservas y webhook de pagos (público)
    .requestMatchers("/reservas/api/**", "/pagos/webhook").permitAll()
    
    // ⭐ NUEVO: Rutas de pagos (requieren autenticación)
    .requestMatchers("/pagos/**").authenticated()
    
    // Rutas protegidas (requieren autenticación)
    .requestMatchers("/home", "/reservas/**").authenticated()
    
    // Rutas de admin (solo ADMIN)
    .requestMatchers("/admin/**").hasRole("ADMIN")
    
    // Cualquier otra ruta requiere autenticación
    .anyRequest().authenticated()
)
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                // ⭐ CAMBIO CRÍTICO: Redirigir según el rol
                .successHandler((request, response, authentication) -> {
                    String role = authentication.getAuthorities().toString();
                    if (role.contains("ROLE_ADMIN")) {
                        response.sendRedirect("/admin/dashboard");
                    } else {
                        response.sendRedirect("/home");
                    }
                })
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                // ⭐ CAMBIO: Redirigir a la landing después del logout
                .logoutSuccessUrl("/?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(exception -> exception
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.sendRedirect("/login?denied=true");
                })
            )
            .sessionManagement(session -> session
                .invalidSessionUrl("/login?expired=true")
                .maximumSessions(1)
                .expiredUrl("/login?expired=true")
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}