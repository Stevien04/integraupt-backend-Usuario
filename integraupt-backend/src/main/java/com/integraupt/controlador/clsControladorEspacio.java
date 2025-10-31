package com.integraupt.controlador;

import com.integraupt.dto.clsDTOEspacioRequest;
import com.integraupt.dto.clsDTOEspacioResponse;
import com.integraupt.entidad.clsEntidadEspacio;
import com.integraupt.servicio.clsServicioEspacio;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST para la gestión de espacios (aulas y laboratorios)
 */
@RestController
@RequestMapping("/api/espacios")
@CrossOrigin(origins = "*")
public class clsControladorEspacio {

    private final clsServicioEspacio servicioEspacio;

    public clsControladorEspacio(clsServicioEspacio servicioEspacio) {
        this.servicioEspacio = servicioEspacio;
    }

    /**
     * Obtener todos los espacios
     */
    @GetMapping
    public ResponseEntity<List<clsDTOEspacioResponse.EspacioDTO>> obtenerTodosLosEspacios(
            @RequestParam(value = "escuelaId", required = false) Integer escuelaId) {
        try {
            List<clsDTOEspacioResponse.EspacioDTO> espacios = servicioEspacio.obtenerTodosLosEspacios(escuelaId);
            return ResponseEntity.ok(espacios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtener espacio por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<clsDTOEspacioResponse> obtenerEspacioPorId(@PathVariable Integer id) {
        try {
            clsDTOEspacioResponse response = servicioEspacio.obtenerEspacioPorId(id);
            HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
            return ResponseEntity.status(status).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(clsDTOEspacioResponse.error("Error interno del servidor"));
        }
    }

    /**
     * Crear nuevo espacio
     */
    @PostMapping
    public ResponseEntity<clsDTOEspacioResponse> crearEspacio(@Valid @RequestBody clsDTOEspacioRequest request) {
        try {
            clsDTOEspacioResponse response = servicioEspacio.crearEspacio(request);
            HttpStatus status = response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(clsDTOEspacioResponse.error("Error interno del servidor al crear el espacio"));
        }
    }

    /**
     * Actualizar espacio existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<clsDTOEspacioResponse> actualizarEspacio(
            @PathVariable Integer id, 
            @Valid @RequestBody clsDTOEspacioRequest request) {
        try {
            clsDTOEspacioResponse response = servicioEspacio.actualizarEspacio(id, request);
            HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(clsDTOEspacioResponse.error("Error interno del servidor al actualizar el espacio"));
        }
    }

    /**
     * Eliminar espacio
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<clsDTOEspacioResponse> eliminarEspacio(@PathVariable Integer id) {
        try {
            clsDTOEspacioResponse response = servicioEspacio.eliminarEspacio(id);
            HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
            return ResponseEntity.status(status).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(clsDTOEspacioResponse.error("Error interno del servidor al eliminar el espacio"));
        }
    }

    /**
     * Obtener espacios por tipo
     */
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<clsDTOEspacioResponse.EspacioDTO>> obtenerEspaciosPorTipo(
            @PathVariable clsEntidadEspacio.TipoEspacio tipo) {
        try {
            List<clsDTOEspacioResponse.EspacioDTO> espacios = servicioEspacio.obtenerEspaciosPorTipo(tipo);
            return ResponseEntity.ok(espacios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtener espacios disponibles
     */
    @GetMapping("/disponibles")
    public ResponseEntity<List<clsDTOEspacioResponse.EspacioDTO>> obtenerEspaciosDisponibles(
            @RequestParam(value = "escuelaId", required = false) Integer escuelaId) {
        try {
            List<clsDTOEspacioResponse.EspacioDTO> espacios = servicioEspacio.obtenerEspaciosDisponibles(escuelaId);
            return ResponseEntity.ok(espacios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint de salud del servicio
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of("status", "OK", "service", "Gestión de Espacios"));
    }
}