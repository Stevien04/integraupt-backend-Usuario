package com.integraupt.repositorio;

import com.integraupt.entidad.clsEntidadReserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio JPA para acceder a la tabla de reservas.
 */
public interface clsRepositorioReserva extends JpaRepository<clsEntidadReserva, String> {

    List<clsEntidadReserva> findAllByEstadoIgnoreCaseOrderByFechaAscHoraInicioAsc(String estado);
    List<clsEntidadReserva> findAllByOrderByFechaAscHoraInicioAsc();

    List<clsEntidadReserva> findAllByUsuarioIdOrderByFechaDescHoraInicioAsc(String usuarioId);
}