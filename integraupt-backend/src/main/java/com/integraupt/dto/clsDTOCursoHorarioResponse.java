package com.integraupt.dto;

public class clsDTOCursoHorarioResponse {

    private boolean success;
    private String message;
    private CursoHorarioDTO cursoHorario;

    // Constructores
    public clsDTOCursoHorarioResponse() {}

    public clsDTOCursoHorarioResponse(boolean success, String message, CursoHorarioDTO cursoHorario) {
        this.success = success;
        this.message = message;
        this.cursoHorario = cursoHorario;
    }

    // Métodos estáticos para crear respuestas
    public static clsDTOCursoHorarioResponse success(String message, CursoHorarioDTO cursoHorario) {
        return new clsDTOCursoHorarioResponse(true, message, cursoHorario);
    }

    public static clsDTOCursoHorarioResponse error(String message) {
        return new clsDTOCursoHorarioResponse(false, message, null);
    }

    // Getters y Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public CursoHorarioDTO getCursoHorario() { return cursoHorario; }
    public void setCursoHorario(CursoHorarioDTO cursoHorario) { this.cursoHorario = cursoHorario; }

    // DTO interno para los datos del horario de curso
    public static class CursoHorarioDTO {
        private Integer id;
        private String curso;
        private String docente;
        private String dias;
        private String horario;
        private String ubicacion;
        private Integer estudiantes;
        private Boolean estado;

        // Constructor por defecto
        public CursoHorarioDTO() {}

        // Constructor completo
        public CursoHorarioDTO(Integer id, String curso, String docente, String dias, 
                              String horario, String ubicacion, Integer estudiantes, Boolean estado) {
            this.id = id;
            this.curso = curso;
            this.docente = docente;
            this.dias = dias;
            this.horario = horario;
            this.ubicacion = ubicacion;
            this.estudiantes = estudiantes;
            this.estado = estado;
        }

        // Getters y Setters
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }

        public String getCurso() { return curso; }
        public void setCurso(String curso) { this.curso = curso; }

        public String getDocente() { return docente; }
        public void setDocente(String docente) { this.docente = docente; }

        public String getDias() { return dias; }
        public void setDias(String dias) { this.dias = dias; }

        public String getHorario() { return horario; }
        public void setHorario(String horario) { this.horario = horario; }

        public String getUbicacion() { return ubicacion; }
        public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

        public Integer getEstudiantes() { return estudiantes; }
        public void setEstudiantes(Integer estudiantes) { this.estudiantes = estudiantes; }

        public Boolean getEstado() { return estado; }
        public void setEstado(Boolean estado) { this.estado = estado; }
    }
}