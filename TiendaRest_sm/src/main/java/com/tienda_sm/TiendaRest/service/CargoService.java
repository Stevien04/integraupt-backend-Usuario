package com.tienda_sm.TiendaRest.service;

import com.tienda_sm.TiendaRest.model.CargoModel;
import java.util.List;
import java.util.Optional;

public interface CargoService {
    List<CargoModel> findAll();
    List<CargoModel> findAllCustom();
    Optional<CargoModel> findById(long id);
    CargoModel add(CargoModel c);
    CargoModel update(CargoModel c);
    CargoModel delete(CargoModel c);
}
