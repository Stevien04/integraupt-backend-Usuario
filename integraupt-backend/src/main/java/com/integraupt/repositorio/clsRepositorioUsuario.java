package com.integraupt.repositorio;

import com.integraupt.entidad.clsEntidadUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface clsRepositorioUsuario extends JpaRepository<clsEntidadUsuario, Integer> {
}