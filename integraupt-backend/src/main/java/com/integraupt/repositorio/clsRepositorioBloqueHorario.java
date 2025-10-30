package com.integraupt.repositorio;

import com.integraupt.entidad.clsEntidadBloqueHorario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para acceder a la información de los bloques horarios disponibles.
 */
@Repository
public interface clsRepositorioBloqueHorario extends JpaRepository<clsEntidadBloqueHorario, Integer> {
}