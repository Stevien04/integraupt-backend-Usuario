package com.integraupt.servicio;

import com.integraupt.dto.clsDTOCursoHorarioRequest;
import com.integraupt.dto.clsDTOCursoHorarioResponse;
import com.integraupt.entidad.clsEntidadHorarioCurso;
import com.integraupt.repositorio.clsRepositorioHorarioCurso;
import com.integraupt.repositorio.clsRepositorioUsuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class clsServicioHorarioCurso {

    private final clsRepositorioHorarioCurso repositorioHorarioCurso;
    private final clsRepositorioUsuario repositorioUsuario;
    private final clsServicioEspacio servicioEspacio;
    private final clsServicioCatalogos servicioCatalogos;

    public clsServicioHorarioCurso(clsRepositorioHorarioCurso repositorioHorarioCurso,
                                  clsRepositorioUsuario repositorioUsuario,
                                  clsServicioEspacio servicioEspacio,
                                  clsServicioCatalogos servicioCatalogos) {
        this.repositorioHorarioCurso = repositorioHorarioCurso;
        this.repositorioUsuario = repositorioUsuario;
        this.servicioEspacio = servicioEspacio;
        this.servicioCatalogos = servicioCatalogos;
    }

    @Transactional(readOnly = true)
    public List<clsDTOCursoHorarioResponse.CursoHorarioDTO> obtenerTodosLosHorariosCursos() {
        List<clsEntidadHorarioCurso> horariosCursos = repositorioHorarioCurso.findByEstadoTrue();
        return horariosCursos.stream()
                .map(this::convertirEntidadADTO)
                .collect(Collectors.toList());
    }

    private clsDTOCursoHorarioResponse.CursoHorarioDTO convertirEntidadADTO(clsEntidadHorarioCurso entidad) {
        String nombreDocente = "Docente no encontrado";
        try {
            var docenteOpt = repositorioUsuario.findById(entidad.getDocenteId());
            if (docenteOpt.isPresent()) {
                var docente = docenteOpt.get();
                nombreDocente = docente.getNombres() + " " + docente.getApellidos();
            }
        } catch (Exception e) {
            System.err.println("Error al obtener información del docente: " + e.getMessage());
        }

        String ubicacion = "Ubicación no encontrada";
        try {
            var espacioResponse = servicioEspacio.obtenerEspacioPorId(entidad.getEspacioId());
            if (espacioResponse.isSuccess() && espacioResponse.getEspacio() != null) {
                var espacio = espacioResponse.getEspacio();
                ubicacion = espacio.getCodigo() + " - " + espacio.getNombre();
            }
        } catch (Exception e) {
            System.err.println("Error al obtener información del espacio: " + e.getMessage());
        }

        String horarioCompleto = "Horario no disponible";
        try {
            var bloqueInfo = servicioCatalogos.obtenerBloqueHorario(entidad.getBloqueId());
            if (bloqueInfo != null) {
                String horaInicio = formatearHora(bloqueInfo.getHoraInicio());
                String horaFinal = formatearHora(bloqueInfo.getHoraFinal());
                horarioCompleto = horaInicio + " - " + horaFinal;
            }
        } catch (Exception e) {
            System.err.println("Error al obtener información del bloque: " + e.getMessage());
        }

        String diasFormateados = formatearDias(entidad.getDiaSemana().toString());
        Integer numeroEstudiantes = 30;

        return new clsDTOCursoHorarioResponse.CursoHorarioDTO(
            entidad.getId(),
            entidad.getCurso(),
            nombreDocente,
            diasFormateados,
            horarioCompleto,
            ubicacion,
            numeroEstudiantes,
            entidad.getEstado()
        );
    }

    private String formatearHora(String hora) {
        if (hora == null) return "N/A";
        return hora.length() > 5 ? hora.substring(0, 5) : hora;
    }

    private String formatearDias(String diaSemana) {
        switch (diaSemana) {
            case "Lunes": return "Lun";
            case "Martes": return "Mar";
            case "Miercoles": return "Mié";
            case "Jueves": return "Jue";
            case "Viernes": return "Vie";
            case "Sabado": return "Sáb";
            default: return diaSemana;
        }
    }

    @Transactional(readOnly = true)
    public List<clsDTOCursoHorarioResponse.CursoHorarioDTO> buscarPorCurso(String curso) {
        List<clsEntidadHorarioCurso> horariosCursos = repositorioHorarioCurso.findByCursoContainingIgnoreCase(curso);
        return horariosCursos.stream()
                .map(this::convertirEntidadADTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public clsDTOCursoHorarioResponse crearHorarioCurso(clsDTOCursoHorarioRequest request) {
        try {
            clsEntidadHorarioCurso nuevoHorarioCurso = new clsEntidadHorarioCurso();
            nuevoHorarioCurso.setCurso(request.getCurso());
            nuevoHorarioCurso.setDocenteId(request.getDocenteId());
            nuevoHorarioCurso.setEspacioId(request.getEspacioId());
            nuevoHorarioCurso.setBloqueId(request.getBloqueId());
            nuevoHorarioCurso.setDiaSemana(request.getDiaSemana());
            nuevoHorarioCurso.setFechaInicio(request.getFechaInicio());
            nuevoHorarioCurso.setFechaFin(request.getFechaFin());
            nuevoHorarioCurso.setEstado(request.getEstado());

            clsEntidadHorarioCurso horarioGuardado = repositorioHorarioCurso.save(nuevoHorarioCurso);
            
            clsDTOCursoHorarioResponse.CursoHorarioDTO cursoHorarioDTO = convertirEntidadADTO(horarioGuardado);
            return clsDTOCursoHorarioResponse.success("Horario de curso creado exitosamente", cursoHorarioDTO);
        } catch (Exception e) {
            return clsDTOCursoHorarioResponse.error("Error al crear el horario de curso: " + e.getMessage());
        }
    }

    @Transactional
    public clsDTOCursoHorarioResponse actualizarHorarioCurso(Integer id, clsDTOCursoHorarioRequest request) {
        try {
            var horarioCursoOpt = repositorioHorarioCurso.findById(id);
            if (horarioCursoOpt.isEmpty()) {
                return clsDTOCursoHorarioResponse.error("Horario de curso no encontrado");
            }

            clsEntidadHorarioCurso horarioCurso = horarioCursoOpt.get();
            horarioCurso.setCurso(request.getCurso());
            horarioCurso.setDocenteId(request.getDocenteId());
            horarioCurso.setEspacioId(request.getEspacioId());
            horarioCurso.setBloqueId(request.getBloqueId());
            horarioCurso.setDiaSemana(request.getDiaSemana());
            horarioCurso.setFechaInicio(request.getFechaInicio());
            horarioCurso.setFechaFin(request.getFechaFin());
            horarioCurso.setEstado(request.getEstado());

            clsEntidadHorarioCurso horarioActualizado = repositorioHorarioCurso.save(horarioCurso);
            
            clsDTOCursoHorarioResponse.CursoHorarioDTO cursoHorarioDTO = convertirEntidadADTO(horarioActualizado);
            return clsDTOCursoHorarioResponse.success("Horario de curso actualizado exitosamente", cursoHorarioDTO);
        } catch (Exception e) {
            return clsDTOCursoHorarioResponse.error("Error al actualizar el horario de curso: " + e.getMessage());
        }
    }

    @Transactional
    public clsDTOCursoHorarioResponse eliminarHorarioCurso(Integer id) {
        try {
            if (!repositorioHorarioCurso.existsById(id)) {
                return clsDTOCursoHorarioResponse.error("Horario de curso no encontrado");
            }

            repositorioHorarioCurso.deleteById(id);
            return clsDTOCursoHorarioResponse.success("Horario de curso eliminado exitosamente", null);
        } catch (Exception e) {
            return clsDTOCursoHorarioResponse.error("Error al eliminar el horario de curso: " + e.getMessage());
        }
    }
}