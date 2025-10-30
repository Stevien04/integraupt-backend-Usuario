package com.integraupt.servicio;

import com.integraupt.dto.clsDTOHorarioRequest;
import com.integraupt.dto.clsDTOHorarioResponse;
import com.integraupt.entidad.clsEntidadHorario;
import com.integraupt.repositorio.clsRepositorioHorario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class clsServicioHorario {

    private final clsRepositorioHorario repositorioHorario;
    private final clsServicioEspacio servicioEspacio;
    private final clsServicioCatalogos servicioCatalogos;

    public clsServicioHorario(clsRepositorioHorario repositorioHorario,
                             clsServicioEspacio servicioEspacio,
                             clsServicioCatalogos servicioCatalogos) {
        this.repositorioHorario = repositorioHorario;
        this.servicioEspacio = servicioEspacio;
        this.servicioCatalogos = servicioCatalogos;
    }

    /**
     * Obtener todos los horarios
     */
    @Transactional(readOnly = true)
    public List<clsDTOHorarioResponse.HorarioDTO> obtenerTodosLosHorarios() {
        List<clsEntidadHorario> horarios = repositorioHorario.findAll();
        return horarios.stream()
                .map(this::convertirEntidadADTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener horario por ID
     */
    @Transactional(readOnly = true)
    public clsDTOHorarioResponse obtenerHorarioPorId(Integer id) {
        Optional<clsEntidadHorario> horarioOpt = repositorioHorario.findById(id);
        
        if (horarioOpt.isEmpty()) {
            return clsDTOHorarioResponse.error("Horario no encontrado");
        }
        
        clsDTOHorarioResponse.HorarioDTO horarioDTO = convertirEntidadADTO(horarioOpt.get());
        return clsDTOHorarioResponse.success("Horario encontrado", horarioDTO);
    }

    /**
     * Crear nuevo horario
     */
    @Transactional
    public clsDTOHorarioResponse crearHorario(clsDTOHorarioRequest request) {
        // Validar que no exista un horario duplicado
        if (repositorioHorario.existsByEspacioIdAndBloqueIdAndDiaSemana(
                request.getEspacioId(), request.getBloqueId(), request.getDiaSemana())) {
            return clsDTOHorarioResponse.error("Ya existe un horario para este espacio, bloque y día");
        }

        // Crear nueva entidad
        clsEntidadHorario nuevoHorario = new clsEntidadHorario();
        nuevoHorario.setEspacioId(request.getEspacioId());
        nuevoHorario.setBloqueId(request.getBloqueId());
        nuevoHorario.setDiaSemana(request.getDiaSemana());
        nuevoHorario.setOcupado(request.getOcupado());

        // Guardar en BD
        clsEntidadHorario horarioGuardado = repositorioHorario.save(nuevoHorario);
        
        // Convertir a DTO de respuesta
        clsDTOHorarioResponse.HorarioDTO horarioDTO = convertirEntidadADTO(horarioGuardado);
        return clsDTOHorarioResponse.success("Horario creado exitosamente", horarioDTO);
    }

    /**
     * Actualizar horario existente
     */
    @Transactional
    public clsDTOHorarioResponse actualizarHorario(Integer id, clsDTOHorarioRequest request) {
        // Verificar que el horario existe
        Optional<clsEntidadHorario> horarioOpt = repositorioHorario.findById(id);
        if (horarioOpt.isEmpty()) {
            return clsDTOHorarioResponse.error("Horario no encontrado");
        }

        clsEntidadHorario horario = horarioOpt.get();

        // Validar que no exista duplicado (si cambió espacio, bloque o día)
        if ((!horario.getEspacioId().equals(request.getEspacioId()) ||
             !horario.getBloqueId().equals(request.getBloqueId()) ||
             !horario.getDiaSemana().equals(request.getDiaSemana())) &&
            repositorioHorario.existsByEspacioIdAndBloqueIdAndDiaSemana(
                request.getEspacioId(), request.getBloqueId(), request.getDiaSemana())) {
            return clsDTOHorarioResponse.error("Ya existe otro horario para este espacio, bloque y día");
        }

        // Actualizar campos
        horario.setEspacioId(request.getEspacioId());
        horario.setBloqueId(request.getBloqueId());
        horario.setDiaSemana(request.getDiaSemana());
        horario.setOcupado(request.getOcupado());

        // Guardar cambios
        clsEntidadHorario horarioActualizado = repositorioHorario.save(horario);
        
        // Convertir a DTO de respuesta
        clsDTOHorarioResponse.HorarioDTO horarioDTO = convertirEntidadADTO(horarioActualizado);
        return clsDTOHorarioResponse.success("Horario actualizado exitosamente", horarioDTO);
    }

    /**
     * Eliminar horario
     */
    @Transactional
    public clsDTOHorarioResponse eliminarHorario(Integer id) {
        // Verificar que el horario existe
        if (!repositorioHorario.existsById(id)) {
            return clsDTOHorarioResponse.error("Horario no encontrado");
        }

        // Eliminar de BD
        repositorioHorario.deleteById(id);
        return clsDTOHorarioResponse.success("Horario eliminado exitosamente", null);
    }

    /**
     * Convertir entidad a DTO para respuesta
     */
    private clsDTOHorarioResponse.HorarioDTO convertirEntidadADTO(clsEntidadHorario entidad) {
        // Obtener información del espacio
        String espacioNombre = "Espacio no encontrado";
        String espacioCodigo = "N/A";
        try {
            var espacioResponse = servicioEspacio.obtenerEspacioPorId(entidad.getEspacioId());
            if (espacioResponse.isSuccess() && espacioResponse.getEspacio() != null) {
                espacioNombre = espacioResponse.getEspacio().getNombre();
                espacioCodigo = espacioResponse.getEspacio().getCodigo();
            }
        } catch (Exception e) {
            System.err.println("Error al obtener información del espacio: " + e.getMessage());
        }

        // Obtener información del bloque horario
        String bloqueNombre = "Bloque no encontrado";
        String horaInicio = "N/A";
        String horaFinal = "N/A";
        try {
            var bloqueInfo = servicioCatalogos.obtenerBloqueHorario(entidad.getBloqueId());
            if (bloqueInfo != null) {
                bloqueNombre = bloqueInfo.getNombre();
                horaInicio = bloqueInfo.getHoraInicio();
                horaFinal = bloqueInfo.getHoraFinal();
            }
        } catch (Exception e) {
            System.err.println("Error al obtener información del bloque: " + e.getMessage());
        }

        return new clsDTOHorarioResponse.HorarioDTO(
            entidad.getId(),
            entidad.getEspacioId(),
            espacioNombre,
            espacioCodigo,
            entidad.getBloqueId(),
            bloqueNombre,
            horaInicio,
            horaFinal,
            entidad.getDiaSemana().toString(),
            entidad.getOcupado()
        );
    }

    /**
     * Obtener horarios por espacio
     */
    @Transactional(readOnly = true)
    public List<clsDTOHorarioResponse.HorarioDTO> obtenerHorariosPorEspacio(Integer espacioId) {
        List<clsEntidadHorario> horarios = repositorioHorario.findByEspacioId(espacioId);
        return horarios.stream()
                .map(this::convertirEntidadADTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener horarios por bloque
     */
    @Transactional(readOnly = true)
    public List<clsDTOHorarioResponse.HorarioDTO> obtenerHorariosPorBloque(Integer bloqueId) {
        List<clsEntidadHorario> horarios = repositorioHorario.findByBloqueId(bloqueId);
        return horarios.stream()
                .map(this::convertirEntidadADTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener horarios por día de la semana
     */
    @Transactional(readOnly = true)
    public List<clsDTOHorarioResponse.HorarioDTO> obtenerHorariosPorDia(String diaSemana) {
        try {
            clsEntidadHorario.DiaSemana dia = clsEntidadHorario.DiaSemana.valueOf(diaSemana);
            List<clsEntidadHorario> horarios = repositorioHorario.findByDiaSemana(dia);
            return horarios.stream()
                    .map(this::convertirEntidadADTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Día de la semana no válido: " + diaSemana);
        }
    }

    /**
     * Obtener horarios disponibles (no ocupados)
     */
    @Transactional(readOnly = true)
    public List<clsDTOHorarioResponse.HorarioDTO> obtenerHorariosDisponibles() {
        List<clsEntidadHorario> horarios = repositorioHorario.findByOcupado(false);
        return horarios.stream()
                .map(this::convertirEntidadADTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener horarios ocupados
     */
    @Transactional(readOnly = true)
    public List<clsDTOHorarioResponse.HorarioDTO> obtenerHorariosOcupados() {
        List<clsEntidadHorario> horarios = repositorioHorario.findByOcupado(true);
        return horarios.stream()
                .map(this::convertirEntidadADTO)
                .collect(Collectors.toList());
    }

    /**
     * Actualizar estado de ocupación de un horario
     */
    @Transactional
    public clsDTOHorarioResponse actualizarOcupacion(Integer id, Boolean ocupado) {
        Optional<clsEntidadHorario> horarioOpt = repositorioHorario.findById(id);
        if (horarioOpt.isEmpty()) {
            return clsDTOHorarioResponse.error("Horario no encontrado");
        }

        clsEntidadHorario horario = horarioOpt.get();
        horario.setOcupado(ocupado);
        
        clsEntidadHorario horarioActualizado = repositorioHorario.save(horario);
        clsDTOHorarioResponse.HorarioDTO horarioDTO = convertirEntidadADTO(horarioActualizado);
        
        String mensaje = ocupado ? "Horario marcado como ocupado" : "Horario marcado como disponible";
        return clsDTOHorarioResponse.success(mensaje, horarioDTO);
    }
}