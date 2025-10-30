package com.integraupt.servicio;

import com.integraupt.dto.clsDTOLoginRequest;
import com.integraupt.dto.clsDTOLoginResponse;
import com.integraupt.dto.clsDTOLoginResponse.PerfilDTO;
import com.integraupt.entidad.clsEntidadUsuario;
import com.integraupt.repositorio.clsRepositorioAuth;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.Locale;
import java.util.Map;

/**
 * Servicio encargado de manejar la lógica de autenticación de usuarios.
 */
@Service
public class clsServicioAuth {

    private static final Logger LOGGER = LoggerFactory.getLogger(clsServicioAuth.class);

    private static final Map<Integer, String> ROL_NOMBRES = Map.of(
            1, "Profesor",
            2, "Estudiante",
            3, "Administrador"
    );

    private static final Map<Integer, String> FACULTAD_NOMBRES = Map.of(
            1, "FAING",
            2, "FADE",
            3, "FACEM",
            4, "FAEDCOH",
            5, "FACSA",
            6, "FAU"
    );

    private static final Map<Integer, String> ESCUELA_NOMBRES = Map.ofEntries(
            Map.entry(1, "Ing. Civil"),
            Map.entry(2, "Ing. de Sistemas"),
            Map.entry(3, "Ing. Electronica"),
            Map.entry(4, "Ing. Agroindustrial"),
            Map.entry(5, "Ing. Ambiental"),
            Map.entry(6, "Ing. Industrial"),
            Map.entry(7, "Derecho"),
            Map.entry(8, "Ciencias Contables y Financieras"),
            Map.entry(9, "Economia y Microfinanzas"),
            Map.entry(10, "Administracion"),
            Map.entry(11, "Administracion Turistico-Hotel"),
            Map.entry(12, "Administracion de Negocios Internacionales"),
            Map.entry(13, "Educacion"),
            Map.entry(14, "Ciencias de la Comunicacion"),
            Map.entry(15, "Humanidades - Psicologia"),
            Map.entry(16, "Medicina Humana"),
            Map.entry(17, "Odontologia"),
            Map.entry(18, "Tecnologia Medica"),
            Map.entry(19, "Arquitectura")
    );


    private final clsRepositorioAuth repositorioAuth;

    public clsServicioAuth(clsRepositorioAuth repositorioAuth) {
        this.repositorioAuth = repositorioAuth;
    }

    /**
     * Autentica a un usuario con las credenciales proporcionadas.
     *
     * @param request datos del login
     * @return respuesta con el resultado del proceso de autenticación
     */
    @Transactional(readOnly = true)
    public clsDTOLoginResponse autenticarUsuario(clsDTOLoginRequest request) {
        if (request == null) {
            return clsDTOLoginResponse.error("La solicitud es inválida");
        }

        String identificador = normalizar(request.getCodigoOEmail());
        String password = normalizar(request.getPassword());
        String tipoLogin = normalizar(request.getTipoLogin());

        if (!StringUtils.hasText(identificador) || !StringUtils.hasText(password)) {
            return clsDTOLoginResponse.error("Debe proporcionar usuario y contraseña");
        }

        Optional<clsEntidadUsuario> usuarioOpt = buscarUsuarioPorIdentificador(identificador, tipoLogin);

        if (usuarioOpt.isEmpty()) {
            LOGGER.warn("Intento de acceso con identificador no encontrado: {}", identificador);
            return clsDTOLoginResponse.error("Credenciales inválidas");
        }

        clsEntidadUsuario usuario = usuarioOpt.get();

        if (!validarPassword(password, usuario.getPassword())) {
            LOGGER.warn("Contraseña incorrecta para el usuario: {}", identificador);
            return clsDTOLoginResponse.error("Credenciales inválidas");
        }

        PerfilDTO perfilDTO = construirPerfil(usuario, tipoLogin);
        String token = generarTokenBasico(usuario);

        return clsDTOLoginResponse.success("Inicio de sesión exitoso", perfilDTO, token);
    }

    private Optional<clsEntidadUsuario> buscarUsuarioPorIdentificador(String identificador, String tipoLogin) {
        Optional<clsEntidadUsuario> usuario = repositorioAuth.findFirstByCodigoIgnoreCase(identificador)
                .or(() -> repositorioAuth.findFirstByEmailIgnoreCase(identificador));
        if (usuario.isEmpty()) {
            return usuario;
            }


        if (!StringUtils.hasText(tipoLogin)) {
            return usuario;
        }

        return usuario.filter(value -> coincideConTipoLogin(value, tipoLogin));
    }

    private String normalizar(String valor) {
        return valor != null ? valor.trim() : null;
    }

    private boolean validarPassword(String passwordIngresada, String passwordAlmacenada) {
        if (!StringUtils.hasText(passwordAlmacenada)) {
            return false;
        }

        if (passwordAlmacenada.startsWith("$2a$") || passwordAlmacenada.startsWith("$2b$")
                || passwordAlmacenada.startsWith("$2y$")) {
            try {
                org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder =
                        new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
                return encoder.matches(passwordIngresada, passwordAlmacenada);
            } catch (Exception ex) {
                LOGGER.error("Error validando contraseña encriptada", ex);
                return false;
            }
        }

        return passwordAlmacenada.equals(passwordIngresada);
    }

    private PerfilDTO construirPerfil(clsEntidadUsuario usuario, String tipoLoginSolicitado) {
        return new PerfilDTO(
                usuario.getId() != null ? usuario.getId().toString() : null,
                usuario.getCodigo(),
                usuario.getNombres(),
                usuario.getApellidos(),
                usuario.getEmail(),
                mapRol(usuario.getRolId()),
                determinarTipoLogin(usuario, tipoLoginSolicitado),
                null,
                mapEstado(usuario.getEstado()),
                usuario.getCelular(),
                mapEscuela(usuario.getEscuelaId()),
                mapFacultad(usuario.getFacultadId()),
                mapGenero(usuario.getGenero()),
                usuario.getNumeroDocumento()
        );
    }

    private String generarTokenBasico(clsEntidadUsuario usuario) {
        String payload = String.valueOf(usuario.getId()) + ":" + Instant.now();
        return Base64.getEncoder().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
    }

    private boolean coincideConTipoLogin(clsEntidadUsuario usuario, String tipoLogin) {
        String normalizado = tipoLogin.toLowerCase(Locale.ROOT);
        Integer rolId = usuario.getRolId();

        if (rolId == null) {
            return true;
        }

        return switch (normalizado) {
            case "academic", "academico", "académico" -> rolId == 1 || rolId == 2;
            case "administrative", "administrativo" -> rolId == 3;
            default -> true;
        };
    }

    private String determinarTipoLogin(clsEntidadUsuario usuario, String tipoLoginSolicitado) {
        if (StringUtils.hasText(tipoLoginSolicitado)) {
            return tipoLoginSolicitado.trim().toLowerCase(Locale.ROOT);
        }

        Integer rolId = usuario.getRolId();
        if (rolId == null) {
            return null;
        }

        return (rolId == 3) ? "administrative" : "academic";
    }

    private String mapRol(Integer rolId) {
        if (rolId == null) {
            return null;
        }
        return ROL_NOMBRES.getOrDefault(rolId, rolId.toString());
    }

    private String mapEstado(Integer estado) {
        if (estado == null) {
            return null;
        }
        return estado == 1 ? "Activo" : "Inactivo";
    }

    private String mapGenero(Boolean genero) {
        if (genero == null) {
            return null;
        }
        return genero ? "Masculino" : "Femenino";
    }

    private String mapFacultad(Integer facultadId) {
        if (facultadId == null) {
            return null;
        }
        return FACULTAD_NOMBRES.getOrDefault(facultadId, facultadId.toString());
    }

    private String mapEscuela(Integer escuelaId) {
        if (escuelaId == null) {
            return null;
        }
        return ESCUELA_NOMBRES.getOrDefault(escuelaId, escuelaId.toString());
    }
}