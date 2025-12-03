package com.tienda_sm.TiendaRest.service;

import com.tienda_sm.TiendaRest.model.EmpleadoModel;
import java.util.List;
import java.util.Optional;

public interface EmpleadoService {
    List<EmpleadoModel> findAll();
    List<EmpleadoModel> findAllCustom();
    Optional<EmpleadoModel> findById(long id);
    EmpleadoModel add(EmpleadoModel p);
    EmpleadoModel update(EmpleadoModel p);
    EmpleadoModel delete(EmpleadoModel p);
}
