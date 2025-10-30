package com.integraupt.repositorio;

import com.integraupt.entidad.clsEntidadHorarioCurso;
import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface clsRepositorioHorarioCurso extends JpaRepository<clsEntidadHorarioCurso, Integer> {
    
    // Buscar horarios de cursos activos
    List<clsEntidadHorarioCurso> findByEstadoTrue();
    
    // Buscar por curso
    List<clsEntidadHorarioCurso> findByCursoContainingIgnoreCase(String curso);
    
    // Buscar por docente
    List<clsEntidadHorarioCurso> findByDocenteId(Integer docenteId);
    
    // Buscar por espacio
    List<clsEntidadHorarioCurso> findByEspacioId(Integer espacioId);
}