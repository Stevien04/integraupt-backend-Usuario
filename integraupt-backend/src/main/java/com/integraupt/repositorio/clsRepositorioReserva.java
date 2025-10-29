package com.integraupt.repositorio;

import com.integraupt.entidad.clsEntidadReserva;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para las reservas.
 */
@Repository
public interface clsRepositorioReserva extends JpaRepository<clsEntidadReserva, Long> {

    List<clsEntidadReserva> findAllByEstadoIgnoreCaseOrderByFechaSolicitudAsc(String estado);
}