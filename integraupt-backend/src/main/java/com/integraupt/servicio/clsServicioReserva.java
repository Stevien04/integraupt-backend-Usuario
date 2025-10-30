package com.integraupt.servicio;

import com.integraupt.dto.clsDTOBloqueDisponible;
import com.integraupt.dto.clsDTOReserva;
import com.integraupt.entidad.clsEntidadBloqueHorario;
import com.integraupt.entidad.clsEntidadEspacio;
import com.integraupt.entidad.clsEntidadHorario;
import com.integraupt.entidad.clsEntidadReserva;
import com.integraupt.repositorio.clsRepositorioBloqueHorario;
import com.integraupt.repositorio.clsRepositorioEspacio;
import com.integraupt.repositorio.clsRepositorioHorario;
import com.integraupt.repositorio.clsRepositorioReserva;
import com.integraupt.repositorio.clsRepositorioUsuario;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.integraupt.dto.clsDTOActualizarEstadoReserva;
import com.integraupt.dto.clsDTOActualizarReserva;
import com.integraupt.dto.clsDTOReservaUsuario;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Objects;

@Service
public class clsServicioReserva {

    private static final List<String> ESTADOS_OCUPA_BLOQUE = List.of("Pendiente", "Aprobada");

    private final clsRepositorioReserva repositorioReserva;
    private final clsRepositorioBloqueHorario repositorioBloqueHorario;
    private final clsRepositorioHorario repositorioHorario;
    private final clsRepositorioEspacio repositorioEspacio;
    private final clsRepositorioUsuario repositorioUsuario;

    public clsServicioReserva(clsRepositorioReserva repositorioReserva,
                              clsRepositorioBloqueHorario repositorioBloqueHorario,
                              clsRepositorioHorario repositorioHorario,
                              clsRepositorioEspacio repositorioEspacio,
                              clsRepositorioUsuario repositorioUsuario) {
        this.repositorioReserva = repositorioReserva;
        this.repositorioBloqueHorario = repositorioBloqueHorario;
        this.repositorioHorario = repositorioHorario;
        this.repositorioEspacio = repositorioEspacio;
        this.repositorioUsuario = repositorioUsuario;
    }

    public List<clsDTOReservaUsuario> listarReservasPorUsuario(Integer usuarioId) {
        return repositorioReserva.findByUsuarioIdOrderByFechaReservaDesc(usuarioId)
                .stream()
                .map(reserva -> mapearReservaParaUsuario(reserva, null, null))
                .toList();
    }

    public List<clsEntidadEspacio> listarEspaciosActivos() {
        return repositorioEspacio.findByEstado(1);
    }

    public List<clsDTOBloqueDisponible> buscarBloquesDisponibles(Integer espacioId,
                                                                 LocalDate fecha,
                                                                 LocalTime horaInicio,
                                                                 LocalTime horaFin) {
        if (horaInicio.isAfter(horaFin) || horaInicio.equals(horaFin)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La hora de inicio debe ser menor a la hora fin");
        }

        clsEntidadEspacio espacio = repositorioEspacio.findById(espacioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El espacio seleccionado no existe"));

        List<clsEntidadBloqueHorario> bloques = new ArrayList<>(
                repositorioBloqueHorario.findByHoraInicioGreaterThanEqualAndHoraFinalLessThanEqual(horaInicio, horaFin)
        );

        if (bloques.isEmpty()) {
            repositorioBloqueHorario.findByHoraInicioAndHoraFinal(horaInicio, horaFin)
                    .ifPresent(bloques::add);
        }

        if (bloques.isEmpty()) {
            return List.of();
        }

        String diaSemana = mapearDiaSemana(fecha.getDayOfWeek());
        List<clsDTOBloqueDisponible> disponibles = new ArrayList<>();

        for (clsEntidadBloqueHorario bloque : bloques) {
            Optional<clsEntidadHorario> horarioOpt = repositorioHorario.findByEspacioIdAndBloqueIdAndDiaSemana(
                    espacio.getIdEspacio(), bloque.getIdBloque(), diaSemana
            );

            if (horarioOpt.isEmpty()) {
                continue;
            }

            boolean ocupado = horarioOpt.get().isOcupado() ||
                    repositorioReserva.existsByEspacioIdAndFechaReservaAndBloqueIdAndEstadoIn(
                            espacio.getIdEspacio(), fecha, bloque.getIdBloque(), ESTADOS_OCUPA_BLOQUE
                    );

            if (!ocupado) {
                disponibles.add(new clsDTOBloqueDisponible(
                        bloque.getIdBloque(),
                        bloque.getNombre(),
                        bloque.getHoraInicio(),
                        bloque.getHoraFinal(),
                        true
                ));
            }
        }

        return disponibles;
    }

    @Transactional
    public clsDTOReservaUsuario crearReservaParaUsuario(clsDTOReserva dto) {
        if (!repositorioUsuario.existsById(dto.getUsuarioId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario indicado no existe");
        }

        clsEntidadEspacio espacio = repositorioEspacio.findById(dto.getEspacioId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El espacio indicado no existe"));

        if (dto.getHoraInicio().isAfter(dto.getHoraFin()) || dto.getHoraInicio().equals(dto.getHoraFin())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La hora de inicio debe ser menor a la hora fin");
        }

        clsEntidadBloqueHorario bloque = repositorioBloqueHorario.findByHoraInicioAndHoraFinal(dto.getHoraInicio(), dto.getHoraFin())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró un bloque para el horario seleccionado"));

        String diaSemana = mapearDiaSemana(dto.getFechaReserva().getDayOfWeek());

        clsEntidadHorario horario = repositorioHorario.findByEspacioIdAndBloqueIdAndDiaSemana(
                        espacio.getIdEspacio(), bloque.getIdBloque(), diaSemana)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El bloque no está asignado al espacio en ese día"));

        boolean bloqueOcupado = horario.isOcupado() ||
                repositorioReserva.existsByEspacioIdAndFechaReservaAndBloqueIdAndEstadoIn(
                        espacio.getIdEspacio(), dto.getFechaReserva(), bloque.getIdBloque(), ESTADOS_OCUPA_BLOQUE
                );

        if (bloqueOcupado) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El bloque seleccionado ya tiene una reserva pendiente o aprobada");
        }

        clsEntidadReserva reserva = new clsEntidadReserva();
        reserva.setUsuarioId(dto.getUsuarioId());
        reserva.setEspacioId(espacio.getIdEspacio());
        reserva.setBloqueId(bloque.getIdBloque());
        reserva.setEstado("Pendiente");
        reserva.setFechaReserva(dto.getFechaReserva());
        reserva.setDescripcion(dto.getDescripcion());
        reserva.setMotivo(dto.getMotivo());

        clsEntidadReserva guardada = repositorioReserva.save(reserva);
        return mapearReservaParaUsuario(guardada, espacio, bloque);
    }

    @Transactional
    public clsDTOReservaUsuario actualizarReservaParaUsuario(clsDTOActualizarReserva dto) {
        if (!repositorioUsuario.existsById(dto.getUsuarioId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario indicado no existe");
        }

        clsEntidadReserva reserva = repositorioReserva.findById(dto.getReservaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La reserva indicada no existe"));

        if (!Objects.equals(reserva.getUsuarioId(), dto.getUsuarioId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tiene permisos para actualizar esta reserva");
        }

        clsEntidadEspacio espacio = repositorioEspacio.findById(dto.getEspacioId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El espacio indicado no existe"));

        if (dto.getHoraInicio().isAfter(dto.getHoraFin()) || dto.getHoraInicio().equals(dto.getHoraFin())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La hora de inicio debe ser menor a la hora fin");
        }

        clsEntidadBloqueHorario bloque = repositorioBloqueHorario.findByHoraInicioAndHoraFinal(dto.getHoraInicio(), dto.getHoraFin())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró un bloque para el horario seleccionado"));

        String diaSemana = mapearDiaSemana(dto.getFechaReserva().getDayOfWeek());

        clsEntidadHorario horario = repositorioHorario.findByEspacioIdAndBloqueIdAndDiaSemana(
                        espacio.getIdEspacio(), bloque.getIdBloque(), diaSemana)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El bloque no está asignado al espacio en ese día"));

        boolean requiereValidacionBloque = !Objects.equals(reserva.getEspacioId(), espacio.getIdEspacio()) ||
                !Objects.equals(reserva.getBloqueId(), bloque.getIdBloque()) ||
                !reserva.getFechaReserva().equals(dto.getFechaReserva());

        if (requiereValidacionBloque) {
            boolean bloqueOcupado = horario.isOcupado() ||
                    repositorioReserva.existsByEspacioIdAndFechaReservaAndBloqueIdAndEstadoIn(
                            espacio.getIdEspacio(), dto.getFechaReserva(), bloque.getIdBloque(), ESTADOS_OCUPA_BLOQUE
                    );

            if (bloqueOcupado) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El bloque seleccionado ya tiene una reserva pendiente o aprobada");
            }
        }

        reserva.setEspacioId(espacio.getIdEspacio());
        reserva.setBloqueId(bloque.getIdBloque());
        reserva.setFechaReserva(dto.getFechaReserva());
        reserva.setDescripcion(dto.getDescripcion());
        reserva.setMotivo(dto.getMotivo());
        reserva.setEstado("Pendiente");

        clsEntidadReserva actualizada = repositorioReserva.save(reserva);
        return mapearReservaParaUsuario(actualizada, espacio, bloque);
    }

    @Transactional
    public clsDTOReservaUsuario actualizarEstadoReserva(clsDTOActualizarEstadoReserva dto, Integer usuarioId) {
        clsEntidadReserva reserva = repositorioReserva.findById(dto.getReservaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La reserva indicada no existe"));

        if (usuarioId != null && !Objects.equals(reserva.getUsuarioId(), usuarioId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tiene permisos para modificar esta reserva");
        }

        reserva.setEstado(dto.getEstado());
        if (dto.getMotivo() != null && !dto.getMotivo().isBlank()) {
            reserva.setMotivo(dto.getMotivo());
        }

        clsEntidadReserva actualizada = repositorioReserva.save(reserva);
        return mapearReservaParaUsuario(actualizada, null, null);
    }

    public void eliminarReserva(Integer reservaId, Integer usuarioId) {
        clsEntidadReserva reserva = repositorioReserva.findById(reservaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La reserva indicada no existe"));

        if (usuarioId != null && !Objects.equals(reserva.getUsuarioId(), usuarioId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tiene permisos para eliminar esta reserva");
        }

        repositorioReserva.delete(reserva);
    }

    private clsDTOReservaUsuario mapearReservaParaUsuario(clsEntidadReserva reserva,
                                                          clsEntidadEspacio espacio,
                                                          clsEntidadBloqueHorario bloque) {
        clsDTOReservaUsuario dto = new clsDTOReservaUsuario();
        dto.setIdReserva(reserva.getIdReserva());
        dto.setUsuarioId(reserva.getUsuarioId());
        dto.setEstado(reserva.getEstado());
        dto.setFechaReserva(reserva.getFechaReserva());
        dto.setFechaSolicitud(reserva.getFechaSolicitud());
        dto.setDescripcion(reserva.getDescripcion());
        dto.setMotivo(reserva.getMotivo());

        if (espacio == null) {
            espacio = repositorioEspacio.findById(reserva.getEspacioId()).orElse(null);
        }

        if (espacio != null) {
            dto.setEspacioId(espacio.getIdEspacio());
            dto.setEspacioNombre(espacio.getNombre());
            dto.setEspacioCodigo(espacio.getCodigo());
        } else {
            dto.setEspacioId(reserva.getEspacioId());
        }

        if (bloque == null) {
            bloque = repositorioBloqueHorario.findById(reserva.getBloqueId()).orElse(null);
        }

        if (bloque != null) {
            dto.setBloqueId(bloque.getIdBloque());
            dto.setBloqueNombre(bloque.getNombre());
            dto.setHoraInicio(bloque.getHoraInicio());
            dto.setHoraFin(bloque.getHoraFinal());
        } else {
            dto.setBloqueId(reserva.getBloqueId());
        }

        return dto;
    }

    private String mapearDiaSemana(DayOfWeek dia) {
        return switch (dia) {
            case MONDAY -> "Lunes";
            case TUESDAY -> "Martes";
            case WEDNESDAY -> "Miercoles";
            case THURSDAY -> "Jueves";
            case FRIDAY -> "Viernes";
            case SATURDAY -> "Sabado";
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Las reservas solo están disponibles de lunes a sábado");
        };
    }
}