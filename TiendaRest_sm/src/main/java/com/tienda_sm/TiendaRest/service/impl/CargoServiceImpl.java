package com.tienda_sm.TiendaRest.service.impl;

import com.tienda_sm.TiendaRest.model.CargoModel;
import com.tienda_sm.TiendaRest.repository.CargoRepository;
import com.tienda_sm.TiendaRest.service.CargoService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CargoServiceImpl implements CargoService {

    @Autowired
    private CargoRepository repositorio;

    @Override
    public List<CargoModel> findAll() {
        return repositorio.findAll();
    }

    @Override
    public List<CargoModel> findAllCustom() {
        return repositorio.findAllCustom();
    }

    @Override
    public Optional<CargoModel> findById(long id) {
        return repositorio.findById(id);
    }

    @Override
    public CargoModel add(CargoModel c) {
        return repositorio.save(c);
    }

    @Override
    public CargoModel update(CargoModel c) {
        CargoModel objcargo = repositorio.getById(c.getIdcargo());
        BeanUtils.copyProperties(c, objcargo);
        return repositorio.save(objcargo);
    }

    @Override
    public CargoModel delete(CargoModel c) {
        CargoModel objcargo = repositorio.getById(c.getIdcargo());
        objcargo.setEstado(0);
        return repositorio.save(objcargo);
    }
}
