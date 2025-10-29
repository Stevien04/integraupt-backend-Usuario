package com.integraupt.servicio;

import com.integraupt.dto.clsDTOReserva;
import com.integraupt.dto.clsDTOCrearReserva;
import com.integraupt.entidad.clsEntidadReserva;
import com.integraupt.repositorio.clsRepositorioReserva;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio encargado de las operaciones de negocio sobre las reservas.
 */
@Service
public class clsServicioReserva {

    public static final String ESTADO_PENDIENTE = "Pendiente";
    public static final String ESTADO_APROBADO = "Aprobado";
    public static final String ESTADO_RECHAZADO = "Rechazado";

    private final clsRepositorioReserva repositorio;

    public clsServicioReserva(clsRepositorioReserva repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * Obtiene todas las reservas para un estado específico.
     */
    @Transactional(readOnly = true)
    public List<clsDTOReserva> obtenerPorEstado(String estado) {
        String normalizado = normalizarEstado(estado);
        return repositorio.findAllByEstadoIgnoreCaseOrderByFechaAscHoraInicioAsc(normalizado)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todas las reservas registradas ordenadas cronológicamente.
     */
    @Transactional(readOnly = true)
    public List<clsDTOReserva> obtenerTodas() {
        return repositorio.findAllByOrderByFechaAscHoraInicioAsc()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un resumen con el total de reservas por estado.
     */
    @Transactional(readOnly = true)
    public Map<String, Long> obtenerTotalesPorEstado() {
        return repositorio.findAll().stream()
                .collect(Collectors.groupingBy(
                        reserva -> normalizarEstado(reserva.getEstado()),
                        Collectors.counting()
                ));
    }

    /**
     * Obtiene todas las reservas asociadas a un usuario específico.
     */
    @Transactional(readOnly = true)
    public List<clsDTOReserva> obtenerPorUsuario(String usuarioId) {
        return repositorio.findAllByUsuarioIdOrderByFechaDescHoraInicioAsc(usuarioId)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Registra una nueva reserva enviada desde el portal de autoservicio.
     */
    @Transactional
    public clsDTOReserva crear(clsDTOCrearReserva request) {
        clsEntidadReserva entidad = new clsEntidadReserva();
        entidad.setId(UUID.randomUUID().toString());
        entidad.setUsuarioId(request.getUsuarioId());
        entidad.setEspacioId(request.getEspacioId());
        if (StringUtils.hasText(request.getTipo())) {
            entidad.setTipo(StringUtils.capitalize(request.getTipo().trim().toLowerCase(Locale.getDefault())));
        } else {
            entidad.setTipo(null);
        }
        entidad.setEstado(ESTADO_PENDIENTE);
        entidad.setCurso(request.getCurso());
        entidad.setCiclo(request.getCiclo());
        entidad.setMotivo(request.getMotivo());
        entidad.setMotivoRechazo(null);
        entidad.setFecha(request.getFecha());
        entidad.setHoraInicio(request.getHoraInicio());
        entidad.setHoraFin(request.getHoraFin());
        entidad.setCreadoEn(LocalDateTime.now());
        entidad.setActualizadoEn(LocalDateTime.now());

        clsEntidadReserva guardada = repositorio.save(entidad);
        return convertirADTO(guardada);
    }


    /**
     * Aprueba una reserva.
     */
    @Transactional
    public clsDTOReserva aprobar(String id, String aprobadoPor) {
        return actualizarEstado(id, ESTADO_APROBADO, null, aprobadoPor);
    }

    /**
     * Rechaza una reserva indicando el motivo.
     */
    @Transactional
    public clsDTOReserva rechazar(String id, String motivo, String aprobadoPor) {
        if (!StringUtils.hasText(motivo)) {
            throw new IllegalArgumentException("El motivo de rechazo es obligatorio");
        }
        return actualizarEstado(id, ESTADO_RECHAZADO, motivo, aprobadoPor);
    }

    /**
     * Cambia el estado de la reserva validando el flujo permitido.
     */
    @Transactional
    public clsDTOReserva cambiarEstado(String id, String estado, String motivo, String aprobadoPor) {
        if (ESTADO_APROBADO.equalsIgnoreCase(estado)) {
            return aprobar(id, aprobadoPor);
        }
        if (ESTADO_RECHAZADO.equalsIgnoreCase(estado)) {
            return rechazar(id, motivo, aprobadoPor);
        }
        if (ESTADO_PENDIENTE.equalsIgnoreCase(estado)) {
            return actualizarEstado(id, ESTADO_PENDIENTE, null, aprobadoPor);
        }
        throw new IllegalArgumentException("Estado no soportado: " + estado);
    }

    /**
     * Método genérico para actualizar el estado de una reserva.
     */
    private clsDTOReserva actualizarEstado(String id, String nuevoEstado, String motivoRechazo, String aprobadoPor) {
        clsEntidadReserva reserva = repositorio.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró la reserva con id " + id));

        String estadoNormalizado = normalizarEstado(nuevoEstado);
        reserva.setEstado(estadoNormalizado);
        reserva.setMotivoRechazo(ESTADO_RECHAZADO.equals(estadoNormalizado) ? motivoRechazo : null);
        reserva.setAprobadoPor(aprobadoPor);
        reserva.setActualizadoEn(LocalDateTime.now());

        clsEntidadReserva guardada = repositorio.save(reserva);
        return convertirADTO(guardada);
    }

    private clsDTOReserva convertirADTO(clsEntidadReserva entidad) {
        clsDTOReserva dto = new clsDTOReserva();
        dto.setId(entidad.getId());
        dto.setUsuarioId(entidad.getUsuarioId());
        dto.setAprobadoPor(entidad.getAprobadoPor());
        dto.setEspacioId(entidad.getEspacioId());
        dto.setTipo(entidad.getTipo());
        dto.setEstado(normalizarEstado(entidad.getEstado()));
        dto.setCurso(entidad.getCurso());
        dto.setCiclo(entidad.getCiclo());
        dto.setMotivo(entidad.getMotivo());
        dto.setMotivoRechazo(entidad.getMotivoRechazo());
        dto.setFecha(entidad.getFecha());
        dto.setHoraInicio(entidad.getHoraInicio());
        dto.setHoraFin(entidad.getHoraFin());
        dto.setCreadoEn(entidad.getCreadoEn());
        dto.setActualizadoEn(entidad.getActualizadoEn());
        return dto;
    }

    private String normalizarEstado(String estado) {
        if (!StringUtils.hasText(estado)) {
            return ESTADO_PENDIENTE;
        }
        String valor = estado.trim().toLowerCase(Locale.getDefault());
        if (valor.startsWith("pend")) {
            return ESTADO_PENDIENTE;
        }
        if (valor.startsWith("aprob")) {
            return ESTADO_APROBADO;
        }
        if (valor.startsWith("rech")) {
            return ESTADO_RECHAZADO;
        }
        return StringUtils.capitalize(valor);
    }
}