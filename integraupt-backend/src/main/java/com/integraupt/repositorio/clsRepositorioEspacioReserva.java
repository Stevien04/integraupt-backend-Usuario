package com.integraupt.repositorio;

import com.integraupt.entidad.clsEntidadEspacio_Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para acceder a los espacios reservables.
 */
@Repository
public interface clsRepositorioEspacioReserva extends JpaRepository<clsEntidadEspacio_Reserva, Integer> {
}