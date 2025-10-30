package com.integraupt.controlador;

import com.integraupt.dto.clsDTOUsuarioRequest;
import com.integraupt.dto.clsDTOUsuarioResponse;
import com.integraupt.dto.clsDTOUsuarioResponse.UsuarioDTO;
import com.integraupt.servicio.clsServicioUsuario;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para la gestión de usuarios.
 */
@RestController
@RequestMapping("/api/usuarios")
public class clsControladorUsuario {

    private final clsServicioUsuario servicioUsuario;

    public clsControladorUsuario(clsServicioUsuario servicioUsuario) {
        this.servicioUsuario = servicioUsuario;
    }

    /**
     * Obtener todos los usuarios.
     */
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios() {
        try {
            List<UsuarioDTO> usuarios = servicioUsuario.listarUsuarios();
            return ResponseEntity.ok(usuarios);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtener usuario por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<clsDTOUsuarioResponse> obtenerUsuarioPorId(@PathVariable Integer id) {
        try {
            clsDTOUsuarioResponse respuesta = servicioUsuario.obtenerUsuarioPorId(id);
            HttpStatus status = respuesta.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
            return ResponseEntity.status(status).body(respuesta);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(clsDTOUsuarioResponse.error("Error interno al buscar el usuario"));
        }
    }

    /**
     * Crear un nuevo usuario.
     */
    @PostMapping
    public ResponseEntity<clsDTOUsuarioResponse> crearUsuario(@Valid @RequestBody clsDTOUsuarioRequest request) {
        try {
            clsDTOUsuarioResponse respuesta = servicioUsuario.crearUsuario(request);
            HttpStatus status = respuesta.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(respuesta);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(clsDTOUsuarioResponse.error("Error interno al crear el usuario"));
        }
    }

    /**
     * Actualizar un usuario existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<clsDTOUsuarioResponse> actualizarUsuario(
            @PathVariable Integer id,
            @Valid @RequestBody clsDTOUsuarioRequest request) {
        try {
            clsDTOUsuarioResponse respuesta = servicioUsuario.actualizarUsuario(id, request);
            HttpStatus status = respuesta.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(respuesta);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(clsDTOUsuarioResponse.error("Error interno al actualizar el usuario"));
        }
    }

    /**
     * Eliminar un usuario.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<clsDTOUsuarioResponse> eliminarUsuario(@PathVariable Integer id) {
        try {
            clsDTOUsuarioResponse respuesta = servicioUsuario.eliminarUsuario(id);
            HttpStatus status = respuesta.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
            return ResponseEntity.status(status).body(respuesta);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(clsDTOUsuarioResponse.error("Error interno al eliminar el usuario"));
        }
    }

    @GetMapping("/docentes")
    public ResponseEntity<List<clsDTOUsuarioResponse.UsuarioDTO>> obtenerTodosLosDocentes() {
        try {
            List<clsDTOUsuarioResponse.UsuarioDTO> docentes = servicioUsuario.obtenerUsuariosPorRol(1); // Rol 1 = Profesor
            return ResponseEntity.ok(docentes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}