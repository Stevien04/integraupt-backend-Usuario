package com.integraupt.repositorio;

import com.integraupt.entidad.clsEntidadHorario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface clsRepositorioHorario extends JpaRepository<clsEntidadHorario, Integer> {

    Optional<clsEntidadHorario> findByEspacioIdAndBloqueIdAndDiaSemana(Integer espacioId,
                                                                       Integer bloqueId,
                                                                       String diaSemana);
}