package com.integraupt.repositorio;

import com.integraupt.entidad.clsEntidadBloqueHorario;
import com.integraupt.entidad.clsEntidadEspacio;
import com.integraupt.entidad.clsEntidadEspacio_Reserva;
import com.integraupt.entidad.clsEntidadReserva;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface clsRepositorioReserva extends JpaRepository<clsEntidadReserva, Integer> {
    List<clsEntidadReserva> findAllByEstadoIgnoreCaseOrderByFechaSolicitudAsc(String estado);

    Long countByEstado(String estado);
    Long countByEstadoAndFechaReservaGreaterThanEqual(String estado, LocalDate fecha);
    Long countByFechaSolicitudBetween(LocalDateTime inicio, LocalDateTime fin);
    Long countByFechaSolicitudBetweenAndEstado(LocalDateTime inicio, LocalDateTime fin, String estado);
    Long countByEspacioAndEstado(clsEntidadEspacio espacio, String estado);


boolean existsByEspacioAndFechaReservaAndBloqueAndEstadoIn(
        clsEntidadEspacio_Reserva espacio,
        LocalDate fechaReserva,
        clsEntidadBloqueHorario bloque,
        List<String> estados);
}