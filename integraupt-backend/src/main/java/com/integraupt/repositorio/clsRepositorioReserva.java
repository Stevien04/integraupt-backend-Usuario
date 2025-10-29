package com.integraupt.repositorio;

import com.integraupt.entidad.clsEntidadReserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface clsRepositorioReserva extends JpaRepository<clsEntidadReserva, Integer> {

    List<clsEntidadReserva> findByUsuarioIdOrderByFechaReservaDesc(Integer usuarioId);

    boolean existsByEspacioIdAndFechaReservaAndBloqueIdAndEstadoIn(Integer espacioId,
                                                                   LocalDate fechaReserva,
                                                                   Integer bloqueId,
                                                                   Collection<String> estados);
}