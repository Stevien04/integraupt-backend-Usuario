package com.integraupt.repositorio;

import com.integraupt.entidad.clsEntidadEspacio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface clsRepositorioEspacio extends JpaRepository<clsEntidadEspacio, Integer> {

    List<clsEntidadEspacio> findByEstado(Integer estado);
}