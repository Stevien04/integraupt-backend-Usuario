package com.tienda_sm.TiendaRest.controller;

import com.tienda_sm.TiendaRest.model.CargoModel;
import com.tienda_sm.TiendaRest.service.CargoService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cargo")
public class CargoController {

    @Autowired
    private CargoService servicio;

    @GetMapping
    public List<CargoModel> findAll() {
        return servicio.findAll();
    }

    @GetMapping("/custom")
    public List<CargoModel> findAllCustom() {
        return servicio.findAllCustom();
    }

    @GetMapping("/{id}")
    public Optional<CargoModel> findById(@PathVariable long id) {
        return servicio.findById(id);
    }

    @PostMapping
    public CargoModel add(@RequestBody CargoModel c) {
        return servicio.add(c);
    }

    @PutMapping("/{id}")
    public CargoModel update(@PathVariable long id, @RequestBody CargoModel c) {
        c.setIdcargo(id);
        return servicio.update(c);
    }

    @DeleteMapping("/{id}")
    public CargoModel delete(@PathVariable long id) {
        CargoModel objcargo = CargoModel.builder().idcargo(id).build();
        objcargo.setEstado(0);
        return servicio.delete(objcargo);
    }
}
