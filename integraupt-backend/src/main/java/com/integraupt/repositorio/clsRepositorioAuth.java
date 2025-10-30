package com.integraupt.repositorio;

import com.integraupt.entidad.clsEntidadUsuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para acceder a los usuarios registrados.
 */
@Repository
public interface clsRepositorioAuth extends JpaRepository<clsEntidadUsuario, Integer> {
    Optional<clsEntidadUsuario> findFirstByCodigoIgnoreCase(String codigo);

    Optional<clsEntidadUsuario> findFirstByEmailIgnoreCase(String email);

}
