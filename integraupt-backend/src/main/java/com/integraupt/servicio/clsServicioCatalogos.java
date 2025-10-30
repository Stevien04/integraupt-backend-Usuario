package com.integraupt.servicio;

import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class clsServicioCatalogos {

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

    // Mapeo de bloques horarios (ID -> Información)
    private static final Map<Integer, BloqueHorarioInfo> BLOQUES_HORARIOS = Map.of(
        10, new BloqueHorarioInfo("B1", "08:00:00", "08:50:00"),
        11, new BloqueHorarioInfo("B2", "08:50:00", "09:40:00"),
        13, new BloqueHorarioInfo("B3", "09:40:00", "10:30:00")
        // Agregar más bloques según sea necesario
    );

    /**
     * Clase interna para información de bloques horarios
     */
    public static class BloqueHorarioInfo {
        private String nombre;
        private String horaInicio;
        private String horaFinal;

        public BloqueHorarioInfo(String nombre, String horaInicio, String horaFinal) {
            this.nombre = nombre;
            this.horaInicio = horaInicio;
            this.horaFinal = horaFinal;
        }

        // Getters
        public String getNombre() { return nombre; }
        public String getHoraInicio() { return horaInicio; }
        public String getHoraFinal() { return horaFinal; }
    }

    /**
     * Obtener información de bloque horario por ID
     */
    public BloqueHorarioInfo obtenerBloqueHorario(Integer bloqueId) {
        if (bloqueId == null) {
            return new BloqueHorarioInfo("Bloque no especificado", "N/A", "N/A");
        }
        return BLOQUES_HORARIOS.getOrDefault(bloqueId, 
            new BloqueHorarioInfo("Bloque " + bloqueId, "N/A", "N/A"));
    }

    /**
     * Obtener nombre de bloque horario por ID
     */
    public String obtenerNombreBloque(Integer bloqueId) {
        BloqueHorarioInfo bloque = obtenerBloqueHorario(bloqueId);
        return bloque.getNombre();
    }

    /**
     * Obtener todos los bloques horarios disponibles
     */
    public Map<Integer, BloqueHorarioInfo> obtenerTodosLosBloques() {
        return BLOQUES_HORARIOS;
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
        // Filtramos las escuelas que pertenecen a la facultad
        // Esto asume que las escuelas 1-6 son de FAING (1), 7 de FADE (2), etc.
        // Ajustar según la relación real en tu BD
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
}