package com.integraupt.servicio;

import com.integraupt.dto.clsDTOUsuarioRequest;
import com.integraupt.dto.clsDTOUsuarioResponse;
import com.integraupt.dto.clsDTOUsuarioResponse.UsuarioDTO;
import com.integraupt.entidad.clsEntidadUsuario;
import com.integraupt.repositorio.clsRepositorioUsuario;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Servicio que contiene la lógica de negocio para la gestión de usuarios.
 */
@Service
public class clsServicioUsuario {

    private static final Map<String, Integer> ROL_TO_ID = Map.of(
            "DOCENTE", 1,
            "ESTUDIANTE", 2,
            "ADMIN", 3,
            "ADMINISTRATIVO", 3,
            "ADMINISTRADOR", 3
    );

    private static final Map<Integer, String> ID_TO_ROL = Map.of(
            1, "DOCENTE",
            2, "ESTUDIANTE",
            3, "ADMINISTRATIVO"
    );

    private static final Map<String, Boolean> GENERO_TO_BOOL = Map.of(
            "MASCULINO", Boolean.TRUE,
            "FEMENINO", Boolean.FALSE
    );

    private final clsRepositorioUsuario repositorioUsuario;
    private final clsServicioCatalogos servicioCatalogos;
    private final clsServicioPassword servicioPassword;

    public clsServicioUsuario(clsRepositorioUsuario repositorioUsuario,
                              clsServicioCatalogos servicioCatalogos,
                              clsServicioPassword servicioPassword) {
        this.repositorioUsuario = repositorioUsuario;
        this.servicioCatalogos = servicioCatalogos;
        this.servicioPassword = servicioPassword;
    }

    /**
     * Lista todos los usuarios ordenados por apellido y nombre.
     */
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarUsuarios() {
        return repositorioUsuario.findAll(Sort.by(Sort.Direction.ASC, "apellidos", "nombres"))
                .stream()
                .map(this::convertirEntidadADTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un usuario por su identificador.
     */
    @Transactional(readOnly = true)
    public clsDTOUsuarioResponse obtenerUsuarioPorId(Integer id) {
        Optional<clsEntidadUsuario> usuarioOpt = repositorioUsuario.findById(id);
        if (usuarioOpt.isEmpty()) {
            return clsDTOUsuarioResponse.error("Usuario no encontrado");
        }
        UsuarioDTO usuarioDTO = convertirEntidadADTO(usuarioOpt.get());
        return clsDTOUsuarioResponse.success("Usuario encontrado", usuarioDTO);
    }

    /**
     * Crea un nuevo usuario validando duplicados y catálogos.
     */
    @Transactional
    public clsDTOUsuarioResponse crearUsuario(clsDTOUsuarioRequest request) {
        String password = normalizar(request.getPassword());
        if (!StringUtils.hasText(password)) {
            return clsDTOUsuarioResponse.error("La contraseña es obligatoria");
        }
        if (password.length() < 6) {
            return clsDTOUsuarioResponse.error("La contraseña debe tener al menos 6 caracteres");
        }

        String codigo = normalizar(request.getCodigo());
        String email = normalizar(request.getEmail());
        String numeroDocumento = normalizar(request.getNumeroDocumento());

        clsDTOUsuarioResponse errorCatalogo = validarCatalogos(request.getFacultadId(), request.getEscuelaId());
        if (errorCatalogo != null) {
            return errorCatalogo;
        }

        if (repositorioUsuario.existsByCodigoIgnoreCase(codigo)) {
            return clsDTOUsuarioResponse.error("Ya existe un usuario con el código " + codigo);
        }
        if (repositorioUsuario.existsByEmailIgnoreCase(email)) {
            return clsDTOUsuarioResponse.error("Ya existe un usuario con el correo " + email);
        }
        if (repositorioUsuario.existsByNumeroDocumento(numeroDocumento)) {
            return clsDTOUsuarioResponse.error("Ya existe un usuario con el número de documento " + numeroDocumento);
        }

        Integer rolId = mapearRolId(request.getRol());
        if (rolId == null) {
            return clsDTOUsuarioResponse.error("El rol especificado no es válido");
        }
        Boolean genero = mapearGenero(request.getGenero());

        clsEntidadUsuario entidad = new clsEntidadUsuario();
        asignarDatosBasicos(entidad, request, rolId, genero);
        entidad.setPassword(servicioPassword.encriptar(password));
        entidad.setSesion(Boolean.FALSE);

        clsEntidadUsuario guardado = repositorioUsuario.save(entidad);
        UsuarioDTO dto = convertirEntidadADTO(guardado);
        return clsDTOUsuarioResponse.success("Usuario creado correctamente", dto);
    }

    /**
     * Actualiza un usuario existente.
     */
    @Transactional
    public clsDTOUsuarioResponse actualizarUsuario(Integer id, clsDTOUsuarioRequest request) {
        Optional<clsEntidadUsuario> usuarioOpt = repositorioUsuario.findById(id);
        if (usuarioOpt.isEmpty()) {
            return clsDTOUsuarioResponse.error("Usuario no encontrado");
        }

        clsDTOUsuarioResponse errorCatalogo = validarCatalogos(request.getFacultadId(), request.getEscuelaId());
        if (errorCatalogo != null) {
            return errorCatalogo;
        }

        clsEntidadUsuario entidad = usuarioOpt.get();

        String codigo = normalizar(request.getCodigo());
        String email = normalizar(request.getEmail());
        String numeroDocumento = normalizar(request.getNumeroDocumento());

        if (repositorioUsuario.existsByCodigoIgnoreCaseAndIdNot(codigo, id)) {
            return clsDTOUsuarioResponse.error("Ya existe otro usuario con el código " + codigo);
        }
        if (repositorioUsuario.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            return clsDTOUsuarioResponse.error("Ya existe otro usuario con el correo " + email);
        }
        if (repositorioUsuario.existsByNumeroDocumentoAndIdNot(numeroDocumento, id)) {
            return clsDTOUsuarioResponse.error("Ya existe otro usuario con el número de documento " + numeroDocumento);
        }

        Integer rolId = mapearRolId(request.getRol());
        if (rolId == null) {
            return clsDTOUsuarioResponse.error("El rol especificado no es válido");
        }
        Boolean genero = mapearGenero(request.getGenero());

        asignarDatosBasicos(entidad, request, rolId, genero);

        String password = normalizar(request.getPassword());
        if (StringUtils.hasText(password)) {
            if (password.length() < 6) {
                return clsDTOUsuarioResponse.error("La contraseña debe tener al menos 6 caracteres");
            }
            entidad.setPassword(servicioPassword.encriptar(password));
        }

        clsEntidadUsuario actualizado = repositorioUsuario.save(entidad);
        UsuarioDTO dto = convertirEntidadADTO(actualizado);
        return clsDTOUsuarioResponse.success("Usuario actualizado correctamente", dto);
    }

    /**
     * Elimina un usuario por su identificador.
     */
    @Transactional
    public clsDTOUsuarioResponse eliminarUsuario(Integer id) {
        Optional<clsEntidadUsuario> usuarioOpt = repositorioUsuario.findById(id);
        if (usuarioOpt.isEmpty()) {
            return clsDTOUsuarioResponse.error("Usuario no encontrado");
        }
        repositorioUsuario.deleteById(id);
        return clsDTOUsuarioResponse.success("Usuario eliminado correctamente", null);
    }

    private void asignarDatosBasicos(clsEntidadUsuario entidad,
                                     clsDTOUsuarioRequest request,
                                     Integer rolId,
                                     Boolean genero) {
        entidad.setCodigo(normalizar(request.getCodigo()));
        entidad.setNombres(normalizarNombre(request.getNombres()));
        entidad.setApellidos(normalizarNombre(request.getApellidos()));
        entidad.setEmail(normalizar(request.getEmail()));
        entidad.setTipoDocumento(normalizar(request.getTipoDocumento()));
        entidad.setNumeroDocumento(normalizar(request.getNumeroDocumento()));
        entidad.setRolId(rolId);
        entidad.setFacultadId(request.getFacultadId());
        entidad.setEscuelaId(request.getEscuelaId());
        entidad.setCelular(normalizar(request.getCelular()));
        entidad.setGenero(genero);
        entidad.setEstado(request.getEstado());
        if (entidad.getSesion() == null) {
            entidad.setSesion(Boolean.FALSE);
        }
    }

    private clsDTOUsuarioResponse validarCatalogos(Integer facultadId, Integer escuelaId) {
        if (facultadId == null || !servicioCatalogos.obtenerTodasLasFacultades().containsKey(facultadId)) {
            return clsDTOUsuarioResponse.error("La facultad seleccionada no es válida");
        }
        if (escuelaId == null || !servicioCatalogos.obtenerTodasLasEscuelas().containsKey(escuelaId)) {
            return clsDTOUsuarioResponse.error("La escuela seleccionada no es válida");
        }
        return null;
    }

    private Integer mapearRolId(String rol) {
        if (!StringUtils.hasText(rol)) {
            return null;
        }
        String clave = rol.trim().toUpperCase(Locale.ROOT);
        return ROL_TO_ID.get(clave);
    }

    private Boolean mapearGenero(String genero) {
        if (!StringUtils.hasText(genero)) {
            return null;
        }
        String clave = genero.trim().toUpperCase(Locale.ROOT);
        if ("OTRO".equals(clave)) {
            return null;
        }
        return GENERO_TO_BOOL.get(clave);
    }

    private UsuarioDTO convertirEntidadADTO(clsEntidadUsuario entidad) {
        String facultadNombre = servicioCatalogos.obtenerNombreFacultad(entidad.getFacultadId());
        String escuelaNombre = servicioCatalogos.obtenerNombreEscuela(entidad.getEscuelaId());
        String rol = ID_TO_ROL.getOrDefault(entidad.getRolId(), "ESTUDIANTE");
        String genero = convertirGenero(entidad.getGenero());

        String passwordVisible;
        try {
            passwordVisible = servicioPassword.desencriptar(entidad.getPassword());
        } catch (IllegalStateException ex) {
            passwordVisible = entidad.getPassword();
        }


        return new UsuarioDTO(
                entidad.getId(),
                entidad.getCodigo(),
                entidad.getNombres(),
                entidad.getApellidos(),
                entidad.getEmail(),
                entidad.getTipoDocumento(),
                entidad.getNumeroDocumento(),
                entidad.getCelular(),
                entidad.getFacultadId(),
                facultadNombre,
                entidad.getEscuelaId(),
                escuelaNombre,
                rol,
                genero,
                entidad.getEstado(),
                null,
                passwordVisible
        );
    }

    private String convertirGenero(Boolean genero) {
        if (genero == null) {
            return "OTRO";
        }
        return genero ? "MASCULINO" : "FEMENINO";
    }

    private String normalizar(String valor) {
        return valor != null ? valor.trim() : null;
    }

    private String normalizarNombre(String valor) {
        if (!StringUtils.hasText(valor)) {
            return valor;
        }
        return valor.trim();
    }

    public List<clsDTOUsuarioResponse.UsuarioDTO> obtenerUsuariosPorRol(Integer rolId) {
        List<clsEntidadUsuario> usuarios = repositorioUsuario.findByRolId(rolId);
        return usuarios.stream()
                .map(this::convertirEntidadADTO)
                .collect(Collectors.toList());
}
}