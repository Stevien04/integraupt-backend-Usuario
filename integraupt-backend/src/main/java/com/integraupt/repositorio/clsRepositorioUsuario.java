package com.integraupt.repositorio;

import com.integraupt.entidad.clsEntidadUsuario;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface clsRepositorioUsuario extends JpaRepository<clsEntidadUsuario, Integer> {

    Optional<clsEntidadUsuario> findByCodigoIgnoreCase(String codigo);

    boolean existsByCodigoIgnoreCase(String codigo);

    boolean existsByCodigoIgnoreCaseAndIdNot(String codigo, Integer id);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Integer id);

    boolean existsByNumeroDocumento(String numeroDocumento);

    boolean existsByNumeroDocumentoAndIdNot(String numeroDocumento, Integer id);

    List<clsEntidadUsuario> findByRolId(Integer rolId);

    Long countByRolId(Integer rolId);

}