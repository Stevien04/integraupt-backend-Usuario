package com.tienda_sm.TiendaRest.controller;

import com.tienda_sm.TiendaRest.model.EmpleadoModel;
import com.tienda_sm.TiendaRest.service.EmpleadoService;
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
@RequestMapping("/empleado")
public class EmpleadoController {

    @Autowired
    private EmpleadoService servicio;

    @GetMapping
    public List<EmpleadoModel> findAll() {
        return servicio.findAll();
    }

    @GetMapping("/custom")
    public List<EmpleadoModel> findAllCustom() {
        return servicio.findAllCustom();
    }

    @GetMapping("/{id}")
    public Optional<EmpleadoModel> findById(@PathVariable long id) {
        return servicio.findById(id);
    }

    @PostMapping
    public EmpleadoModel add(@RequestBody EmpleadoModel e) {
        return servicio.add(e);
    }

    @PutMapping("/{id}")
    public EmpleadoModel update(@PathVariable long id, @RequestBody EmpleadoModel e) {
        e.setIdEmpleado(id);
        return servicio.update(e);
    }

    @DeleteMapping("/{id}")
    public EmpleadoModel delete(@PathVariable long id) {
        EmpleadoModel objempleado = new EmpleadoModel();
        objempleado.setIdEmpleado(id);
        objempleado.setEstado(0);
        return servicio.delete(EmpleadoModel.builder().idEmpleado(id).build());
    }
}
