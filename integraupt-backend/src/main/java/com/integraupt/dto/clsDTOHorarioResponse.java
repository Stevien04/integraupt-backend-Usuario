package com.integraupt.dto;

public class clsDTOHorarioResponse {

    private boolean success;
    private String message;
    private HorarioDTO horario;

    // Constructores
    public clsDTOHorarioResponse() {}

    public clsDTOHorarioResponse(boolean success, String message, HorarioDTO horario) {
        this.success = success;
        this.message = message;
        this.horario = horario;
    }

    // Métodos estáticos para crear respuestas
    public static clsDTOHorarioResponse success(String message, HorarioDTO horario) {
        return new clsDTOHorarioResponse(true, message, horario);
    }

    public static clsDTOHorarioResponse error(String message) {
        return new clsDTOHorarioResponse(false, message, null);
    }

    // Getters y Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public HorarioDTO getHorario() { return horario; }
    public void setHorario(HorarioDTO horario) { this.horario = horario; }

    // DTO interno para los datos del horario
    public static class HorarioDTO {
        private Integer id;
        private Integer espacioId;
        private String espacioNombre;
        private String espacioCodigo;
        private Integer bloqueId;
        private String bloqueNombre;
        private String horaInicio;
        private String horaFinal;
        private String diaSemana;
        private Boolean ocupado;

        // Constructor por defecto
        public HorarioDTO() {}

        // Constructor completo
        public HorarioDTO(Integer id, Integer espacioId, String espacioNombre, String espacioCodigo,
                         Integer bloqueId, String bloqueNombre, String horaInicio, String horaFinal,
                         String diaSemana, Boolean ocupado) {
            this.id = id;
            this.espacioId = espacioId;
            this.espacioNombre = espacioNombre;
            this.espacioCodigo = espacioCodigo;
            this.bloqueId = bloqueId;
            this.bloqueNombre = bloqueNombre;
            this.horaInicio = horaInicio;
            this.horaFinal = horaFinal;
            this.diaSemana = diaSemana;
            this.ocupado = ocupado;
        }

        // Getters y Setters
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }

        public Integer getEspacioId() { return espacioId; }
        public void setEspacioId(Integer espacioId) { this.espacioId = espacioId; }

        public String getEspacioNombre() { return espacioNombre; }
        public void setEspacioNombre(String espacioNombre) { this.espacioNombre = espacioNombre; }

        public String getEspacioCodigo() { return espacioCodigo; }
        public void setEspacioCodigo(String espacioCodigo) { this.espacioCodigo = espacioCodigo; }

        public Integer getBloqueId() { return bloqueId; }
        public void setBloqueId(Integer bloqueId) { this.bloqueId = bloqueId; }

        public String getBloqueNombre() { return bloqueNombre; }
        public void setBloqueNombre(String bloqueNombre) { this.bloqueNombre = bloqueNombre; }

        public String getHoraInicio() { return horaInicio; }
        public void setHoraInicio(String horaInicio) { this.horaInicio = horaInicio; }

        public String getHoraFinal() { return horaFinal; }
        public void setHoraFinal(String horaFinal) { this.horaFinal = horaFinal; }

        public String getDiaSemana() { return diaSemana; }
        public void setDiaSemana(String diaSemana) { this.diaSemana = diaSemana; }

        public Boolean getOcupado() { return ocupado; }
        public void setOcupado(Boolean ocupado) { this.ocupado = ocupado; }
    }
}