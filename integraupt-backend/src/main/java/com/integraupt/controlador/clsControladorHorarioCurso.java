package com.integraupt.controlador;

import com.integraupt.dto.clsDTOCursoHorarioRequest;
import com.integraupt.dto.clsDTOCursoHorarioResponse;
import com.integraupt.servicio.clsServicioHorarioCurso;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/horarios-cursos")
public class clsControladorHorarioCurso {

    private final clsServicioHorarioCurso servicioHorarioCurso;

    public clsControladorHorarioCurso(clsServicioHorarioCurso servicioHorarioCurso) {
        this.servicioHorarioCurso = servicioHorarioCurso;
    }

    @GetMapping
    public ResponseEntity<List<clsDTOCursoHorarioResponse.CursoHorarioDTO>> obtenerTodosLosHorariosCursos() {
        try {
            List<clsDTOCursoHorarioResponse.CursoHorarioDTO> horariosCursos = 
                servicioHorarioCurso.obtenerTodosLosHorariosCursos();
            return ResponseEntity.ok(horariosCursos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<clsDTOCursoHorarioResponse.CursoHorarioDTO>> buscarHorariosCursos(
            @RequestParam String curso) {
        try {
            List<clsDTOCursoHorarioResponse.CursoHorarioDTO> horariosCursos = 
                servicioHorarioCurso.buscarPorCurso(curso);
            return ResponseEntity.ok(horariosCursos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // CRUD
    @PostMapping
    public ResponseEntity<clsDTOCursoHorarioResponse> crearHorarioCurso(
            @Valid @RequestBody clsDTOCursoHorarioRequest request) {
        try {
            clsDTOCursoHorarioResponse response = servicioHorarioCurso.crearHorarioCurso(request);
            HttpStatus status = response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(clsDTOCursoHorarioResponse.error("Error interno del servidor al crear el horario de curso"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<clsDTOCursoHorarioResponse> actualizarHorarioCurso(
            @PathVariable Integer id, 
            @Valid @RequestBody clsDTOCursoHorarioRequest request) {
        try {
            clsDTOCursoHorarioResponse response = servicioHorarioCurso.actualizarHorarioCurso(id, request);
            HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(clsDTOCursoHorarioResponse.error("Error interno del servidor al actualizar el horario de curso"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<clsDTOCursoHorarioResponse> eliminarHorarioCurso(@PathVariable Integer id) {
        try {
            clsDTOCursoHorarioResponse response = servicioHorarioCurso.eliminarHorarioCurso(id);
            HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
            return ResponseEntity.status(status).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(clsDTOCursoHorarioResponse.error("Error interno del servidor al eliminar el horario de curso"));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Servicio de Horarios de Cursos - OK");
    }
}