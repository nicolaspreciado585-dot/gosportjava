package com.gosport.demo.repository;

import com.gosport.demo.model.Cancha;
import com.gosport.demo.model.Deporte;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CanchaRepository extends JpaRepository<Cancha, Long> {
    
    // Buscar canchas por estado
    List<Cancha> findByEstado(String estado);
    
    // Buscar canchas por deporte
    List<Cancha> findByDeporte(Deporte deporte);
    
    // Buscar canchas por nombre o localidad
    List<Cancha> findByNombreContainingIgnoreCaseOrLocalidadContainingIgnoreCase(
        String nombre, String localidad);
    
    // Buscar con paginación
    Page<Cancha> findAll(Pageable pageable);
    
    // Buscar canchas disponibles
    List<Cancha> findByEstadoOrderByNombreAsc(String estado);
    
    // Contar canchas por deporte
    @Query("SELECT c.deporte.nombre, COUNT(c) FROM Cancha c GROUP BY c.deporte.nombre")
    List<Object[]> contarCanchasPorDeporte();
}