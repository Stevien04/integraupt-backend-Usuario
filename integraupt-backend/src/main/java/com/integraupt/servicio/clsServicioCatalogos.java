package com.integraupt.servicio;

import com.integraupt.dto.clsDTOBloqueHorario;
import com.integraupt.entidad.clsEntidadBloqueHorario;
import com.integraupt.repositorio.clsRepositorioBloqueHorario;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class clsServicioCatalogos {

    private static final DateTimeFormatter HORA_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final clsRepositorioBloqueHorario repositorioBloqueHorario;

    public clsServicioCatalogos(clsRepositorioBloqueHorario repositorioBloqueHorario) {
        this.repositorioBloqueHorario = repositorioBloqueHorario;
    }


    // Mapeo de facultades (ID -> Nombre)
    private static final Map<Integer, String> FACULTADES = Map.of(
        1, "FAING",
        2, "FADE", 
        3, "FACEM",
        4, "FAEDCOH",
        5, "FACSA",
        6, "FAU"
    );

    // Mapeo de escuelas (ID -> Nombre)
    private static final Map<Integer, String> ESCUELAS = Map.ofEntries(
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



    /**
     * Clase interna para información de bloques horarios
     */
    public static class BloqueHorarioInfo {
        private final Integer id;
        private final Integer orden;
        private final String nombre;
        private final String horaInicio;
        private final String horaFinal;

        public BloqueHorarioInfo(Integer id, Integer orden, String nombre, String horaInicio, String horaFinal) {
            this.id = id;
            this.orden = orden;
            this.nombre = nombre;
            this.horaInicio = horaInicio;
            this.horaFinal = horaFinal;
        }

        // Getters
        public Integer getId() { return id; }
        public Integer getOrden() { return orden; }
        public String getNombre() { return nombre; }
        public String getHoraInicio() { return horaInicio; }
        public String getHoraFinal() { return horaFinal; }
    }

    /**
     * Obtener información de bloque horario por ID
     */
    public BloqueHorarioInfo obtenerBloqueHorario(Integer bloqueId) {
        if (bloqueId == null) {
            return new BloqueHorarioInfo(null, null, "Bloque no especificado", "N/A", "N/A");
        }
        return repositorioBloqueHorario.findById(bloqueId)
                .map(this::mapearABloqueInfo)
                .orElseGet(() -> new BloqueHorarioInfo(bloqueId, null, "Bloque " + bloqueId, "N/A", "N/A"));
    }

    /**
     * Obtener nombre de bloque horario por ID
     */
    public String obtenerNombreBloque(Integer bloqueId) {
        return Optional.ofNullable(obtenerBloqueHorario(bloqueId))
                .map(BloqueHorarioInfo::getNombre)
                .orElse("Bloque " + bloqueId);
    }

    /**
     * Obtener todos los bloques horarios disponibles
     */
    public Map<Integer, BloqueHorarioInfo> obtenerTodosLosBloques() {
        return repositorioBloqueHorario.findAll(Sort.by(Sort.Direction.ASC, "orden")).stream()
                .collect(Collectors.toMap(
                        clsEntidadBloqueHorario::getId,
                        this::mapearABloqueInfo,
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));
    }

    /**
     * Obtener bloques horarios en orden como lista DTO
     */
    public List<clsDTOBloqueHorario> obtenerBloquesHorariosOrdenados() {
        return repositorioBloqueHorario.findAll(Sort.by(Sort.Direction.ASC, "orden")).stream()
                .map(bloque -> new clsDTOBloqueHorario(
                        bloque.getId(),
                        bloque.getOrden(),
                        bloque.getNombre(),
                        formatearHora(bloque.getHoraInicio()),
                        formatearHora(bloque.getHoraFin())
                ))
                .collect(Collectors.toList());
    }

    /**
     * Obtener nombre de facultad por ID
     */
    public String obtenerNombreFacultad(Integer facultadId) {
        if (facultadId == null) {
            return "No especificado";
        }
        return FACULTADES.getOrDefault(facultadId, "Facultad " + facultadId);
    }

    /**
     * Obtener nombre de escuela por ID
     */
    public String obtenerNombreEscuela(Integer escuelaId) {
        if (escuelaId == null) {
            return "No especificado";
        }
        return ESCUELAS.getOrDefault(escuelaId, "Escuela " + escuelaId);
    }

    /**
     * Obtener todas las facultades (para selects)
     */
    public Map<Integer, String> obtenerTodasLasFacultades() {
        return FACULTADES;
    }

    /**
     * Obtener todas las escuelas (para selects)
     */
    public Map<Integer, String> obtenerTodasLasEscuelas() {
        return ESCUELAS;
    }

    /**
     * Obtener escuelas por facultad
     */
    public Map<Integer, String> obtenerEscuelasPorFacultad(Integer facultadId) {

        return ESCUELAS.entrySet().stream()
                .filter(entry -> escuelaPerteneceAFacultad(entry.getKey(), facultadId))
                .collect(java.util.stream.Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Determinar si una escuela pertenece a una facultad
     * (Ajustar esta lógica según las relaciones reales en tu BD)
     */
    private boolean escuelaPerteneceAFacultad(Integer escuelaId, Integer facultadId) {
        // Mapeo simplificado - ajustar según tu estructura real
        if (facultadId == 1) return escuelaId >= 1 && escuelaId <= 6; // FAING
        if (facultadId == 2) return escuelaId == 7; // FADE
        if (facultadId == 3) return escuelaId >= 8 && escuelaId <= 12; // FACEM
        if (facultadId == 4) return escuelaId >= 13 && escuelaId <= 15; // FAEDCOH
        if (facultadId == 5) return escuelaId >= 16 && escuelaId <= 18; // FACSA
        if (facultadId == 6) return escuelaId == 19; // FAU
        return false;
    }
    private BloqueHorarioInfo mapearABloqueInfo(clsEntidadBloqueHorario bloque) {
        return new BloqueHorarioInfo(
                bloque.getId(),
                bloque.getOrden(),
                bloque.getNombre(),
                formatearHora(bloque.getHoraInicio()),
                formatearHora(bloque.getHoraFin())
        );
    }

    private String formatearHora(LocalTime hora) {
        if (hora == null) {
            return "N/A";
        }
        return hora.format(HORA_FORMATTER);
    }
}