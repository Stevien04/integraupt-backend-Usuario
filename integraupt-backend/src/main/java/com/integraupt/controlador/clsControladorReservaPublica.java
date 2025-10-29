package com.integraupt.controlador;

import com.integraupt.dto.clsDTOCrearReserva;
import com.integraupt.dto.clsDTOReserva;
import com.integraupt.servicio.clsServicioReserva;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador público para que los estudiantes registren y consulten sus reservas.
 */
@RestController
@RequestMapping("/api/reservas")
public class clsControladorReservaPublica {

    private final clsServicioReserva servicioReserva;

    public clsControladorReservaPublica(clsServicioReserva servicioReserva) {
        this.servicioReserva = servicioReserva;
    }

    @PostMapping
    public ResponseEntity<clsDTOReserva> registrar(@Valid @RequestBody clsDTOCrearReserva request) {
        clsDTOReserva creada = servicioReserva.crear(request);
        return ResponseEntity.ok(creada);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<clsDTOReserva> listarPorUsuario(@PathVariable String usuarioId) {
        return servicioReserva.obtenerPorUsuario(usuarioId);
    }
}