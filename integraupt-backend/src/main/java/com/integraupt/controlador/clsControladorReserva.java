package com.integraupt.controlador;

import com.integraupt.dto.clsDTOActualizarEstadoReserva;
import com.integraupt.dto.clsDTOReserva;
import com.integraupt.dto.clsDTOReservaUsuarioRequest;
import com.integraupt.servicio.clsServicioReserva;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para la administración de reservas.
 */
@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "*")
public class clsControladorReserva {

    private final clsServicioReserva servicioReserva;

    public clsControladorReserva(clsServicioReserva servicioReserva) {
        this.servicioReserva = servicioReserva;
    }

    @GetMapping
    public List<clsDTOReserva> listarPorEstado(@RequestParam(value = "estado", required = false) String estado) {
        return servicioReserva.obtenerReservasPorEstado(estado);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public clsDTOReserva crear(@Valid @RequestBody clsDTOReservaUsuarioRequest request) {
        return servicioReserva.crearReservaUsuario(request);
    }

    @PutMapping("/{id}/aprobar")
    public clsDTOReserva aprobar(@PathVariable("id") Integer id) {
        return servicioReserva.aprobarReserva(id);
    }

    @PutMapping("/{id}/rechazar")
    @ResponseStatus(HttpStatus.OK)
    public clsDTOReserva rechazar(@PathVariable("id") Integer id,
                                  @Valid @RequestBody clsDTOActualizarEstadoReserva dto) {
        return servicioReserva.rechazarReserva(id, dto.getMotivo());
    }
}