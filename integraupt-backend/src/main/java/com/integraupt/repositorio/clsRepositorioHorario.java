package com.integraupt.repositorio;

import com.integraupt.entidad.clsEntidadHorario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para consultar la ocupación de horarios.
 */
@Repository
public interface clsRepositorioHorario extends JpaRepository<clsEntidadHorario, Long> {

    Optional<clsEntidadHorario> findByEspacioIdAndBloqueIdAndDiaSemana(Long espacioId, Long bloqueId, String diaSemana);
}