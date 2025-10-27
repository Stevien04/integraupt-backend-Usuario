package com.integraupt.repositorio;

import com.integraupt.entidad.clsEntidadUsuario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para operaciones CRUD sobre {@link clsEntidadUsuario}.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<clsEntidadUsuario, Integer> {

    List<clsEntidadUsuario> findByEstado(Integer estado);
}