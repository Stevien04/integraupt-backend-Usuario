package com.integraupt.servicio;

import com.integraupt.entidad.clsEntidadBloqueHorario;
import com.integraupt.entidad.clsEntidadEspacio;
import com.integraupt.entidad.clsEntidadReserva;
import com.integraupt.entidad.clsEntidadUsuario;
import com.integraupt.dto.clsDTOReserva;
import com.integraupt.repositorio.clsRepositorioReserva;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.integraupt.entidad.clsEntidadHorario;
import com.integraupt.repositorio.clsRepositorioHorario;
import java.text.Normalizer;
import java.util.regex.Pattern;
import java.time.format.TextStyle;
import java.time.LocalDate;
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

    public clsServicioReserva(clsRepositorioReserva repositorioReserva,
                              clsRepositorioHorario repositorioHorario) {
        this.repositorioReserva = repositorioReserva;
        this.repositorioHorario = repositorioHorario;
    }

    @Transactional(readOnly = true)
    public List<clsDTOReserva> obtenerReservasPorEstado(String estadoSolicitado) {
        String estadoNormalizado = normalizarEstado(estadoSolicitado);
        List<clsEntidadReserva> reservas =
                repositorioReserva.findAllByEstadoIgnoreCaseOrderByFechaSolicitudAsc(estadoNormalizado);
        return reservas.stream().map(this::mapearReserva).collect(Collectors.toList());
    }

    @Transactional
    public clsDTOReserva aprobarReserva(Long id) {
        clsEntidadReserva reserva = obtenerReserva(id);
        reserva.setEstado("Aprobada");
        reserva.setMotivo(null);
        clsEntidadReserva actualizada = repositorioReserva.save(reserva);
        actualizarOcupacionHorario(actualizada, true);
        return mapearReserva(actualizada);
    }

    @Transactional
    public clsDTOReserva rechazarReserva(Long id, String motivo) {
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

    private clsEntidadReserva obtenerReserva(Long id) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El identificador es obligatorio");
        }
        Optional<clsEntidadReserva> reserva = repositorioReserva.findById(id);
        return reserva.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "La reserva solicitada no existe"));
    }

    private clsDTOReserva mapearReserva(clsEntidadReserva reserva) {
        clsEntidadUsuario usuario = reserva.getUsuario();
        clsEntidadEspacio espacio = reserva.getEspacio();
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

    private String normalizarEstado(String estado) {
        String valor = estado == null ? "" : estado.trim().toLowerCase(Locale.getDefault());
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
            case "":
                return "Pendiente";
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado no soportado: " + estado);
        }
    }
    private void actualizarOcupacionHorario(clsEntidadReserva reserva, boolean ocupado) {
        if (reserva == null) {
            return;
        }

        clsEntidadEspacio espacio = reserva.getEspacio();
        clsEntidadBloqueHorario bloque = reserva.getBloque();

        if (espacio == null || bloque == null || reserva.getFechaReserva() == null) {
            return;
        }

        String diaSemana = obtenerDiaSemana(reserva.getFechaReserva());
        if (diaSemana == null) {
            return;
        }

        Optional<clsEntidadHorario> horario =
                repositorioHorario.findByEspacioIdAndBloqueIdAndDiaSemana(
                        espacio.getId(), bloque.getId(), diaSemana);

        horario.ifPresent(h -> {
            h.setOcupado(ocupado);
            repositorioHorario.save(h);
        });
    }

    private String obtenerDiaSemana(LocalDate fecha) {
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

        switch (sinTildes) {
            case "Lunes":
            case "Martes":
            case "Miercoles":
            case "Jueves":
            case "Viernes":
            case "Sabado":
                return sinTildes;
            default:
                return null;
        }
    }
}