package com.tienda_sm.TiendaRest.service.impl;

import com.tienda_sm.TiendaRest.model.EmpleadoModel;
import com.tienda_sm.TiendaRest.repository.EmpleadoRepository;
import com.tienda_sm.TiendaRest.service.EmpleadoService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmpleadoServiceImpl implements EmpleadoService {

    @Autowired
    private EmpleadoRepository repositorio;

    @Override
    public List<EmpleadoModel> findAll() {
        return repositorio.findAll();
    }

    @Override
    public List<EmpleadoModel> findAllCustom() {
        return repositorio.findAllCustom();
    }

    @Override
    public Optional<EmpleadoModel> findById(long id) {
        return repositorio.findById(id);
    }

    @Override
    public EmpleadoModel add(EmpleadoModel e) {
        return repositorio.save(e);
    }

    @Override
    public EmpleadoModel update(EmpleadoModel e) {
        EmpleadoModel objempleado = repositorio.getById(e.getIdEmpleado());
        BeanUtils.copyProperties(e, objempleado);
        return repositorio.save(objempleado);
    }

    @Override
    public EmpleadoModel delete(EmpleadoModel e) {
        EmpleadoModel objempleado = repositorio.getById(e.getIdEmpleado());
        objempleado.setEstado(0);
        return repositorio.save(objempleado);
    }
}
