package com.integraupt.repositorio;

import com.integraupt.entidad.clsEntidadUsuario_Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para acceder a los usuarios que pueden registrar reservas.
 */
@Repository
public interface clsRepositorioUsuarioReserva extends JpaRepository<clsEntidadUsuario_Reserva, Integer> {
}