package com.integraupt.controlador;

import com.integraupt.dto.clsDTOHorarioRequest;
import com.integraupt.dto.clsDTOHorarioResponse;
import com.integraupt.servicio.clsServicioHorario;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST para la gestión de horarios
 */
@RestController
@RequestMapping("/api/horarios")
public class clsControladorHorario {

    private final clsServicioHorario servicioHorario;

    public clsControladorHorario(clsServicioHorario servicioHorario) {
        this.servicioHorario = servicioHorario;
    }

    /**
     * Obtener todos los horarios
     */
    @GetMapping
    public ResponseEntity<List<clsDTOHorarioResponse.HorarioDTO>> obtenerTodosLosHorarios() {
        try {
            List<clsDTOHorarioResponse.HorarioDTO> horarios = servicioHorario.obtenerTodosLosHorarios();
            return ResponseEntity.ok(horarios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtener horario por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<clsDTOHorarioResponse> obtenerHorarioPorId(@PathVariable Integer id) {
        try {
            clsDTOHorarioResponse response = servicioHorario.obtenerHorarioPorId(id);
            HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
            return ResponseEntity.status(status).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(clsDTOHorarioResponse.error("Error interno del servidor"));
        }
    }

    /**
     * Crear nuevo horario
     */
    @PostMapping
    public ResponseEntity<clsDTOHorarioResponse> crearHorario(@Valid @RequestBody clsDTOHorarioRequest request) {
        try {
            clsDTOHorarioResponse response = servicioHorario.crearHorario(request);
            HttpStatus status = response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(clsDTOHorarioResponse.error("Error interno del servidor al crear el horario"));
        }
    }

    /**
     * Actualizar horario existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<clsDTOHorarioResponse> actualizarHorario(
            @PathVariable Integer id, 
            @Valid @RequestBody clsDTOHorarioRequest request) {
        try {
            clsDTOHorarioResponse response = servicioHorario.actualizarHorario(id, request);
            HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(clsDTOHorarioResponse.error("Error interno del servidor al actualizar el horario"));
        }
    }

    /**
     * Eliminar horario
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<clsDTOHorarioResponse> eliminarHorario(@PathVariable Integer id) {
        try {
            clsDTOHorarioResponse response = servicioHorario.eliminarHorario(id);
            HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
            return ResponseEntity.status(status).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(clsDTOHorarioResponse.error("Error interno del servidor al eliminar el horario"));
        }
    }

    /**
     * Obtener horarios por espacio
     */
    @GetMapping("/espacio/{espacioId}")
    public ResponseEntity<List<clsDTOHorarioResponse.HorarioDTO>> obtenerHorariosPorEspacio(
            @PathVariable Integer espacioId) {
        try {
            List<clsDTOHorarioResponse.HorarioDTO> horarios = servicioHorario.obtenerHorariosPorEspacio(espacioId);
            return ResponseEntity.ok(horarios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtener horarios por bloque
     */
    @GetMapping("/bloque/{bloqueId}")
    public ResponseEntity<List<clsDTOHorarioResponse.HorarioDTO>> obtenerHorariosPorBloque(
            @PathVariable Integer bloqueId) {
        try {
            List<clsDTOHorarioResponse.HorarioDTO> horarios = servicioHorario.obtenerHorariosPorBloque(bloqueId);
            return ResponseEntity.ok(horarios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtener horarios por día de la semana
     */
    @GetMapping("/dia/{diaSemana}")
    public ResponseEntity<List<clsDTOHorarioResponse.HorarioDTO>> obtenerHorariosPorDia(
            @PathVariable String diaSemana) {
        try {
            List<clsDTOHorarioResponse.HorarioDTO> horarios = servicioHorario.obtenerHorariosPorDia(diaSemana);
            return ResponseEntity.ok(horarios);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtener horarios disponibles
     */
    @GetMapping("/disponibles")
    public ResponseEntity<List<clsDTOHorarioResponse.HorarioDTO>> obtenerHorariosDisponibles() {
        try {
            List<clsDTOHorarioResponse.HorarioDTO> horarios = servicioHorario.obtenerHorariosDisponibles();
            return ResponseEntity.ok(horarios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtener horarios ocupados
     */
    @GetMapping("/ocupados")
    public ResponseEntity<List<clsDTOHorarioResponse.HorarioDTO>> obtenerHorariosOcupados() {
        try {
            List<clsDTOHorarioResponse.HorarioDTO> horarios = servicioHorario.obtenerHorariosOcupados();
            return ResponseEntity.ok(horarios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Actualizar estado de ocupación
     */
    @PatchMapping("/{id}/ocupacion")
    public ResponseEntity<clsDTOHorarioResponse> actualizarOcupacion(
            @PathVariable Integer id, 
            @RequestBody Map<String, Boolean> request) {
        try {
            Boolean ocupado = request.get("ocupado");
            if (ocupado == null) {
                return ResponseEntity.badRequest()
                        .body(clsDTOHorarioResponse.error("El campo 'ocupado' es requerido"));
            }

            clsDTOHorarioResponse response = servicioHorario.actualizarOcupacion(id, ocupado);
            HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
            return ResponseEntity.status(status).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(clsDTOHorarioResponse.error("Error interno del servidor al actualizar la ocupación"));
        }
    }

    /**
     * Endpoint de salud del servicio
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of("status", "OK", "service", "Gestión de Horarios"));
    }
}