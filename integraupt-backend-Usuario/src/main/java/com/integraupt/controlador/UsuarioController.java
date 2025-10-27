package com.integraupt.controlador;

import com.integraupt.dto.EstadoUsuarioRequest;
import com.integraupt.entidad.clsEntidadUsuario;
import com.integraupt.servicio.UsuarioService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para operaciones sobre usuarios.
 */
@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<clsEntidadUsuario> listarUsuarios(@RequestParam(value = "estado", required = false) Integer estado) {
        return usuarioService.listarUsuarios(estado);
    }

    @GetMapping("/{id}")
    public clsEntidadUsuario obtenerUsuario(@PathVariable Integer id) {
        return usuarioService.obtenerPorId(id);
    }

    @PostMapping
    public ResponseEntity<clsEntidadUsuario> crearUsuario(@Valid @RequestBody clsEntidadUsuario usuario) {
        clsEntidadUsuario creado = usuarioService.crearUsuario(usuario);
        return ResponseEntity.created(URI.create("/api/usuarios/" + creado.getIdUsuario())).body(creado);
    }

    @PutMapping("/{id}")
    public clsEntidadUsuario actualizarUsuario(@PathVariable Integer id,
                                                @Valid @RequestBody clsEntidadUsuario usuario) {
        return usuarioService.actualizarUsuario(id, usuario);
    }

    @PatchMapping("/{id}/estado")
    public clsEntidadUsuario actualizarEstado(@PathVariable Integer id,
                                               @Valid @RequestBody EstadoUsuarioRequest request) {
        return usuarioService.actualizarEstado(id, request.getEstado());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Integer id) {
        usuarioService.eliminarLogicamente(id);
        return ResponseEntity.noContent().build();
    }
}