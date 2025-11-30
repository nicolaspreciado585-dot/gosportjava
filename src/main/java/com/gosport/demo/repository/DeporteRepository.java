package com.gosport.demo.repository;

import com.gosport.demo.model.Deporte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeporteRepository extends JpaRepository<Deporte, Long> {
    
    Optional<Deporte> findByNombre(String nombre);
}