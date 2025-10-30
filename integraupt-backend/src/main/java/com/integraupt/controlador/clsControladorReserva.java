package com.integraupt.controlador;

import com.integraupt.dto.clsDTOBloqueDisponible;
import com.integraupt.dto.clsDTOReserva;
import com.integraupt.entidad.clsEntidadEspacio;

import com.integraupt.servicio.clsServicioReserva;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import com.integraupt.dto.clsDTOActualizarEstadoReserva;
import com.integraupt.dto.clsDTOActualizarReserva;
import com.integraupt.dto.clsDTOReservaUsuario;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class clsControladorReserva {

    private final clsServicioReserva servicioReserva;

    public clsControladorReserva(clsServicioReserva servicioReserva) {
        this.servicioReserva = servicioReserva;
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<clsDTOReservaUsuario> listarPorUsuario(@PathVariable Integer usuarioId) {
        return servicioReserva.listarReservasPorUsuario(usuarioId);
    }

    @GetMapping("/espacios")
    public List<clsEntidadEspacio> obtenerEspaciosActivos() {
        return servicioReserva.listarEspaciosActivos();
    }

    @GetMapping("/disponibilidad")
    public List<clsDTOBloqueDisponible> consultarDisponibilidad(@RequestParam Integer espacioId,
                                                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                                                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horaInicio,
                                                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horaFin) {
        return servicioReserva.buscarBloquesDisponibles(espacioId, fecha, horaInicio, horaFin);
    }

    @PostMapping
    public ResponseEntity<clsDTOReservaUsuario> crearReserva(@Valid @RequestBody clsDTOReserva dto) {
        clsDTOReservaUsuario reserva = servicioReserva.crearReservaParaUsuario(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(reserva);
    }
    @PutMapping("/{reservaId}")
    public ResponseEntity<clsDTOReservaUsuario> actualizarReserva(@PathVariable Integer reservaId,
                                                                  @Valid @RequestBody clsDTOActualizarReserva dto) {
        dto.setReservaId(reservaId);
        clsDTOReservaUsuario reserva = servicioReserva.actualizarReservaParaUsuario(dto);
        return ResponseEntity.ok(reserva);
    }

    @PutMapping("/{reservaId}/estado")
    public ResponseEntity<clsDTOReservaUsuario> actualizarEstado(@PathVariable Integer reservaId,
                                                                 @RequestParam(value = "usuarioId", required = false) Integer usuarioId,
                                                                 @Valid @RequestBody clsDTOActualizarEstadoReserva dto) {
        dto.setReservaId(reservaId);
        clsDTOReservaUsuario reserva = servicioReserva.actualizarEstadoReserva(dto, usuarioId);
        return ResponseEntity.ok(reserva);
    }

    @DeleteMapping("/{reservaId}")
    public ResponseEntity<Void> eliminarReserva(@PathVariable Integer reservaId,
                                                @RequestParam(value = "usuarioId", required = false) Integer usuarioId) {
        servicioReserva.eliminarReserva(reservaId, usuarioId);
        return ResponseEntity.noContent().build();
    }
}