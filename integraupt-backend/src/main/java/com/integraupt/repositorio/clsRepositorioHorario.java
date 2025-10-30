package com.integraupt.repositorio;

import com.integraupt.entidad.clsEntidadHorario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface clsRepositorioHorario extends JpaRepository<clsEntidadHorario, Integer> {
    
    Optional<clsEntidadHorario> findByEspacioIdAndBloqueIdAndDiaSemana(Integer espacioId, Integer bloqueId, String diaSemana);

    // Buscar por espacio
    List<clsEntidadHorario> findByEspacioId(Integer espacioId);
    
    // Buscar por bloque
    List<clsEntidadHorario> findByBloqueId(Integer bloqueId);
    
    // Buscar por día de la semana
    List<clsEntidadHorario> findByDiaSemana(clsEntidadHorario.DiaSemana diaSemana);
    
    // Buscar por estado de ocupación
    List<clsEntidadHorario> findByOcupado(Boolean ocupado);
    
    // Buscar horarios específicos por espacio, bloque y día
    Optional<clsEntidadHorario> findByEspacioIdAndBloqueIdAndDiaSemana(
        Integer espacioId, Integer bloqueId, clsEntidadHorario.DiaSemana diaSemana);
    
    // Verificar si existe un horario para espacio, bloque y día específicos
    boolean existsByEspacioIdAndBloqueIdAndDiaSemana(
        Integer espacioId, Integer bloqueId, clsEntidadHorario.DiaSemana diaSemana);
    
    // Buscar horarios ocupados
    @Query("SELECT h FROM clsEntidadHorario h WHERE h.ocupado = true")
    List<clsEntidadHorario> findHorariosOcupados();
    
    // Buscar horarios disponibles
    @Query("SELECT h FROM clsEntidadHorario h WHERE h.ocupado = false")
    List<clsEntidadHorario> findHorariosDisponibles();
    
    // Buscar horarios por espacio y día
    List<clsEntidadHorario> findByEspacioIdAndDiaSemana(Integer espacioId, clsEntidadHorario.DiaSemana diaSemana);
    
    // Contar horarios ocupados por espacio
    @Query("SELECT COUNT(h) FROM clsEntidadHorario h WHERE h.espacioId = :espacioId AND h.ocupado = true")
    Integer countHorariosOcupadosByEspacio(@Param("espacioId") Integer espacioId);

    Long countByOcupado(Boolean ocupado);
}