package com.integraupt.controlador;

import com.integraupt.dto.clsDTOActualizarEstadoReserva;
import com.integraupt.dto.clsDTOReserva;
import com.integraupt.servicio.clsServicioReserva;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST para el módulo administrativo de reservas.
 */
@RestController
@RequestMapping("/api/admin/reservas")
public class clsControladorReserva {

    private final clsServicioReserva servicioReserva;

    public clsControladorReserva(clsServicioReserva servicioReserva) {
        this.servicioReserva = servicioReserva;
    }

    @GetMapping("/pendientes")
    public List<clsDTOReserva> listarPendientes() {
        return servicioReserva.obtenerPorEstado(clsServicioReserva.ESTADO_PENDIENTE);
    }

    @GetMapping("/aprobadas")
    public List<clsDTOReserva> listarAprobadas() {
        return servicioReserva.obtenerPorEstado(clsServicioReserva.ESTADO_APROBADO);
    }

    @GetMapping("/rechazadas")
    public List<clsDTOReserva> listarRechazadas() {
        return servicioReserva.obtenerPorEstado(clsServicioReserva.ESTADO_RECHAZADO);
    }

    @GetMapping("/resumen")
    public Map<String, Long> obtenerResumen() {
        return servicioReserva.obtenerTotalesPorEstado();
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<clsDTOReserva> actualizarEstado(
            @PathVariable String id,
            @Valid @RequestBody clsDTOActualizarEstadoReserva request) {
        clsDTOReserva resultado = servicioReserva.cambiarEstado(
                id,
                request.getEstado(),
                request.getMotivo(),
                request.getAprobadoPor());
        return ResponseEntity.ok(resultado);
    }
}