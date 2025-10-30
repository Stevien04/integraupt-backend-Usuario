package com.integraupt.servicio;

import com.integraupt.dto.clsDTOReserva;
import com.integraupt.dto.clsDTOReservaUsuarioRequest;
import com.integraupt.entidad.clsEntidadBloqueHorario;
import com.integraupt.entidad.clsEntidadEspacio_Reserva;
import com.integraupt.entidad.clsEntidadHorario;
import com.integraupt.entidad.clsEntidadReserva;
import com.integraupt.entidad.clsEntidadUsuario_Reserva;
import com.integraupt.repositorio.clsRepositorioBloqueHorario;
import com.integraupt.repositorio.clsRepositorioEspacioReserva;
import com.integraupt.repositorio.clsRepositorioHorario;
import com.integraupt.repositorio.clsRepositorioReserva;
import com.integraupt.repositorio.clsRepositorioUsuarioReserva;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Servicio de negocio para la administración de reservas.
 */
@Service
public class clsServicioReserva {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault());
    private static final Locale LOCALE_ES = new Locale("es", "ES");
    private static final Pattern DIACRITICS = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    private final clsRepositorioReserva repositorioReserva;
    private final clsRepositorioHorario repositorioHorario;
    private final clsRepositorioUsuarioReserva repositorioUsuarioReserva;
    private final clsRepositorioEspacioReserva repositorioEspacioReserva;
    private final clsRepositorioBloqueHorario repositorioBloqueHorario;

    public clsServicioReserva(clsRepositorioReserva repositorioReserva,
                              clsRepositorioHorario repositorioHorario,
                              clsRepositorioUsuarioReserva repositorioUsuarioReserva,
                              clsRepositorioEspacioReserva repositorioEspacioReserva,
                              clsRepositorioBloqueHorario repositorioBloqueHorario) {
        this.repositorioReserva = repositorioReserva;
        this.repositorioHorario = repositorioHorario;
        this.repositorioUsuarioReserva = repositorioUsuarioReserva;
        this.repositorioEspacioReserva = repositorioEspacioReserva;
        this.repositorioBloqueHorario = repositorioBloqueHorario;
    }

    @Transactional(readOnly = true)
    public List<clsDTOReserva> obtenerReservasPorEstado(String estadoSolicitado) {
        String estadoNormalizado = normalizarEstado(estadoSolicitado);
        List<clsEntidadReserva> reservas;
        
        if (estadoNormalizado == null || estadoNormalizado.isEmpty()) {
            reservas = repositorioReserva.findAll();
        } else {
            reservas = repositorioReserva.findAllByEstadoIgnoreCaseOrderByFechaSolicitudAsc(estadoNormalizado);
        }
        
        return reservas.stream().map(this::mapearReserva).collect(Collectors.toList());
    }

    @Transactional
    public clsDTOReserva crearReservaUsuario(clsDTOReservaUsuarioRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La solicitud de reserva es obligatoria");
        }

        clsEntidadUsuario_Reserva usuario = repositorioUsuarioReserva.findById(request.getUsuario())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario indicado no existe"));

        clsEntidadEspacio_Reserva espacio = repositorioEspacioReserva.findById(request.getEspacio())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El espacio indicado no existe"));

        clsEntidadBloqueHorario bloque = repositorioBloqueHorario.findById(request.getBloque())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El bloque horario indicado no existe"));

        LocalDate fechaReserva = validarFechaReserva(request.getFechaReserva());

        String descripcion = request.getDescripcion() != null ? request.getDescripcion().trim() : null;
        if (descripcion == null || descripcion.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La descripción de la reserva es obligatoria");
        }

        List<String> estadosBloqueados = List.of("Pendiente", "Aprobada");
        boolean existeReserva = repositorioReserva.existsByEspacioAndFechaReservaAndBloqueAndEstadoIn(
                espacio, fechaReserva, bloque, estadosBloqueados);
        if (existeReserva) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe una reserva pendiente o aprobada para el espacio, bloque y fecha seleccionados");
        }

        clsEntidadHorario.DiaSemana diaSemanaEnum = obtenerDiaSemanaEnum(fechaReserva);
        if (diaSemanaEnum != null) {
            Optional<clsEntidadHorario> horario = repositorioHorario.findByEspacioIdAndBloqueIdAndDiaSemana(
                    espacio.getId(),
                    bloque.getId(),
                    diaSemanaEnum
            );
            if (horario.isPresent() && Boolean.TRUE.equals(horario.get().getOcupado())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "El horario seleccionado ya se encuentra ocupado");
            }
        }

        clsEntidadReserva reserva = new clsEntidadReserva();
        reserva.setUsuario(usuario);
        reserva.setEspacio(espacio);
        reserva.setBloque(bloque);
        reserva.setFechaReserva(fechaReserva);
        reserva.setFechaSolicitud(LocalDateTime.now());
        reserva.setEstado("Pendiente");
        reserva.setDescripcion(descripcion);

        String motivo = request.getMotivo();
        if (motivo != null && !motivo.isBlank()) {
            reserva.setMotivo(motivo.trim());
        } else {
            reserva.setMotivo(null);
        }

        clsEntidadReserva guardada = repositorioReserva.save(reserva);
        return mapearReserva(guardada);
    }

    @Transactional
    public clsDTOReserva aprobarReserva(Integer id) {
        clsEntidadReserva reserva = obtenerReserva(id);
        reserva.setEstado("Aprobada");
        reserva.setMotivo(null);
        clsEntidadReserva actualizada = repositorioReserva.save(reserva);
        actualizarOcupacionHorario(actualizada, true);
        return mapearReserva(actualizada);
    }

    @Transactional
    public clsDTOReserva rechazarReserva(Integer id, String motivo) {
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El motivo es obligatorio");
        }
        clsEntidadReserva reserva = obtenerReserva(id);
        reserva.setEstado("Rechazada");
        reserva.setMotivo(motivo.trim());
        clsEntidadReserva actualizada = repositorioReserva.save(reserva);
        actualizarOcupacionHorario(actualizada, false);
        return mapearReserva(actualizada);
    }

    private clsEntidadReserva obtenerReserva(Integer id) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El identificador es obligatorio");
        }
        Optional<clsEntidadReserva> reserva = repositorioReserva.findById(id);
        return reserva.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "La reserva solicitada no existe"));
    }

    private clsDTOReserva mapearReserva(clsEntidadReserva reserva) {
        clsEntidadUsuario_Reserva usuario = reserva.getUsuario();
        clsEntidadEspacio_Reserva espacio = reserva.getEspacio();
        clsEntidadBloqueHorario bloque = reserva.getBloque();

        return new clsDTOReserva(
                reserva.getIdReserva(),
                reserva.getEstado(),
                reserva.getFechaReserva() != null ? DATE_FORMAT.format(reserva.getFechaReserva()) : null,
                reserva.getFechaSolicitud() != null ? DATE_TIME_FORMAT.format(reserva.getFechaSolicitud()) : null,
                reserva.getDescripcion(),
                reserva.getMotivo(),
                usuario != null ? usuario.getNombreCompleto() : null,
                usuario != null ? usuario.getCodigo() : null,
                usuario != null ? usuario.getCorreo() : null,
                espacio != null ? espacio.getNombre() : null,
                espacio != null ? espacio.getCodigo() : null,
                espacio != null ? espacio.getTipo() : null,
                bloque != null ? bloque.getNombre() : null,
                bloque != null && bloque.getHoraInicio() != null ? TIME_FORMAT.format(bloque.getHoraInicio()) : null,
                bloque != null && bloque.getHoraFin() != null ? TIME_FORMAT.format(bloque.getHoraFin()) : null
        );
    }
    private LocalDate validarFechaReserva(String fechaReserva) {
        if (fechaReserva == null || fechaReserva.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha de reserva es obligatoria");
        }

        try {
            LocalDate fecha = LocalDate.parse(fechaReserva, DATE_FORMAT);
            if (fecha.isBefore(LocalDate.now())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "La fecha de reserva debe ser igual o posterior a la fecha actual");
            }
            return fecha;
        } catch (DateTimeParseException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La fecha de reserva debe tener el formato yyyy-MM-dd");
        }
    }


    private String normalizarEstado(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            return null; // Retornar null para obtener todas las reservas
        }
        
        String valor = estado.trim().toLowerCase(Locale.getDefault());
        switch (valor) {
            case "pendiente":
            case "pendientes":
                return "Pendiente";
            case "aprobada":
            case "aprobadas":
            case "aprobado":
            case "aprobados":
                return "Aprobada";
            case "rechazada":
            case "rechazadas":
            case "rechazado":
            case "rechazados":
                return "Rechazada";
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado no soportado: " + estado);
        }
    }

    private void actualizarOcupacionHorario(clsEntidadReserva reserva, boolean ocupado) {
        if (reserva == null) {
            return;
        }

        clsEntidadEspacio_Reserva espacio = reserva.getEspacio();
        clsEntidadBloqueHorario bloque = reserva.getBloque();

        if (espacio == null || bloque == null || reserva.getFechaReserva() == null) {
            return;
        }

        // Convertir a clsEntidadHorario.DiaSemana
        clsEntidadHorario.DiaSemana diaSemanaEnum = obtenerDiaSemanaEnum(reserva.getFechaReserva());
        if (diaSemanaEnum == null) {
            return;
        }

        // Buscar el horario usando el método del repositorio
        Optional<clsEntidadHorario> horario = repositorioHorario.findByEspacioIdAndBloqueIdAndDiaSemana(
                espacio.getId().intValue(), // Convertir Long a Integer
                bloque.getId(),
                diaSemanaEnum
        );

        horario.ifPresent(h -> {
            h.setOcupado(ocupado);
            repositorioHorario.save(h);
        });
    }

    private clsEntidadHorario.DiaSemana obtenerDiaSemanaEnum(LocalDate fecha) {
        if (fecha == null) {
            return null;
        }

        String nombreDia = fecha.getDayOfWeek().getDisplayName(TextStyle.FULL, LOCALE_ES);
        if (nombreDia == null || nombreDia.isBlank()) {
            return null;
        }

        String capitalizado = nombreDia.substring(0, 1).toUpperCase(LOCALE_ES)
                + nombreDia.substring(1).toLowerCase(LOCALE_ES);
        String sinTildes = Normalizer.normalize(capitalizado, Normalizer.Form.NFD);
        sinTildes = DIACRITICS.matcher(sinTildes).replaceAll("");

        // Mapear a clsEntidadHorario.DiaSemana
        switch (sinTildes) {
            case "Lunes":
                return clsEntidadHorario.DiaSemana.Lunes;
            case "Martes":
                return clsEntidadHorario.DiaSemana.Martes;
            case "Miercoles":
                return clsEntidadHorario.DiaSemana.Miercoles;
            case "Jueves":
                return clsEntidadHorario.DiaSemana.Jueves;
            case "Viernes":
                return clsEntidadHorario.DiaSemana.Viernes;
            case "Sabado":
                return clsEntidadHorario.DiaSemana.Sabado;
            default:
                return null;
        }
    }
}