package com.integraupt.repositorio;

import com.integraupt.entidad.clsEntidadBloqueHorario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface clsRepositorioBloqueHorario extends JpaRepository<clsEntidadBloqueHorario, Integer> {

    List<clsEntidadBloqueHorario> findByHoraInicioGreaterThanEqualAndHoraFinalLessThanEqual(LocalTime horaInicio,
                                                                                            LocalTime horaFinal);

    Optional<clsEntidadBloqueHorario> findByHoraInicioAndHoraFinal(LocalTime horaInicio, LocalTime horaFinal);
}