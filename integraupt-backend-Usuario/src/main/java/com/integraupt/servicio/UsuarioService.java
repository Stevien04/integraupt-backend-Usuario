package com.integraupt.servicio;

import com.integraupt.entidad.clsEntidadUsuario;
import com.integraupt.excepcion.RecursoNoEncontradoException;
import com.integraupt.repositorio.UsuarioRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Servicio que encapsula la lógica de negocio relacionada con usuarios.
 */
@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<clsEntidadUsuario> listarUsuarios(Integer estado) {
        if (estado == null) {
            return usuarioRepository.findAll();
        }
        return usuarioRepository.findByEstado(estado);
    }

    public clsEntidadUsuario obtenerPorId(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con id " + id));
    }

    public clsEntidadUsuario crearUsuario(clsEntidadUsuario usuario) {
        if (usuario.getEstado() == null) {
            usuario.setEstado(1);
        }
        if (usuario.getSesion() == null) {
            usuario.setSesion(Boolean.FALSE);
        }
        validarCamposCadena(usuario);
        return usuarioRepository.save(usuario);
    }

    public clsEntidadUsuario actualizarUsuario(Integer id, clsEntidadUsuario datosActualizados) {
        clsEntidadUsuario existente = obtenerPorId(id);
        existente.setNombre(datosActualizados.getNombre());
        existente.setApellido(datosActualizados.getApellido());
        existente.setCodigoU(datosActualizados.getCodigoU());
        existente.setCorreoU(datosActualizados.getCorreoU());
        existente.setTipoDoc(datosActualizados.getTipoDoc());
        existente.setNumDoc(datosActualizados.getNumDoc());
        existente.setRol(datosActualizados.getRol());
        existente.setFacultad(datosActualizados.getFacultad());
        existente.setEscuela(datosActualizados.getEscuela());
        existente.setCelular(datosActualizados.getCelular());
        existente.setGenero(datosActualizados.getGenero());
        existente.setPassword(datosActualizados.getPassword());
        if (datosActualizados.getEstado() != null) {
            validarEstado(datosActualizados.getEstado());
            existente.setEstado(datosActualizados.getEstado());
        }
        if (datosActualizados.getSesion() != null) {
            existente.setSesion(datosActualizados.getSesion());
        }
        validarCamposCadena(existente);
        return usuarioRepository.save(existente);
    }

    public clsEntidadUsuario actualizarEstado(Integer id, Integer nuevoEstado) {
        validarEstado(nuevoEstado);
        clsEntidadUsuario usuario = obtenerPorId(id);
        usuario.setEstado(nuevoEstado);
        return usuarioRepository.save(usuario);
    }

    public void eliminarLogicamente(Integer id) {
        clsEntidadUsuario usuario = obtenerPorId(id);
        usuario.setEstado(0);
        usuarioRepository.save(usuario);
    }

    private void validarEstado(Integer estado) {
        if (estado == null || (estado != 0 && estado != 1)) {
            throw new IllegalArgumentException("El estado debe ser 0 (inactivo) o 1 (activo).");
        }
    }

    private void validarCamposCadena(clsEntidadUsuario usuario) {
        if (!StringUtils.hasText(usuario.getNombre()) || !StringUtils.hasText(usuario.getApellido())) {
            throw new IllegalArgumentException("El nombre y apellido son obligatorios.");
        }
        if (!StringUtils.hasText(usuario.getCodigoU())) {
            throw new IllegalArgumentException("El código de usuario es obligatorio.");
        }
        if (!StringUtils.hasText(usuario.getCorreoU())) {
            throw new IllegalArgumentException("El correo es obligatorio.");
        }
        if (!StringUtils.hasText(usuario.getTipoDoc()) || !StringUtils.hasText(usuario.getNumDoc())) {
            throw new IllegalArgumentException("El tipo y número de documento son obligatorios.");
        }
        if (!StringUtils.hasText(usuario.getPassword())) {
            throw new IllegalArgumentException("La contraseña es obligatoria.");
        }
    }
}