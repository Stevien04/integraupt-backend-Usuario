package com.integraupt.dto;

public class clsDTOEspacioResponse {

    private boolean success;
    private String message;
    private EspacioDTO espacio;

    // Constructores
    public clsDTOEspacioResponse() {}

    public clsDTOEspacioResponse(boolean success, String message, EspacioDTO espacio) {
        this.success = success;
        this.message = message;
        this.espacio = espacio;
    }

    // Métodos estáticos para crear respuestas
    public static clsDTOEspacioResponse success(String message, EspacioDTO espacio) {
        return new clsDTOEspacioResponse(true, message, espacio);
    }

    public static clsDTOEspacioResponse error(String message) {
        return new clsDTOEspacioResponse(false, message, null);
    }

    // Getters y Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public EspacioDTO getEspacio() { return espacio; }
    public void setEspacio(EspacioDTO espacio) { this.espacio = espacio; }

    // DTO interno para los datos del espacio
    public static class EspacioDTO {
        private Integer id;
        private String codigo;
        private String nombre;
        private String ubicacion;
        private String tipo;
        private Integer capacidad;
        private String equipamiento;
        private String facultad;
        private String escuela;
        private Integer estado;

        // Constructor por defecto
        public EspacioDTO() {}

        // Constructor completo
        public EspacioDTO(Integer id, String codigo, String nombre, String ubicacion, 
                         String tipo, Integer capacidad, String equipamiento, 
                         String facultad, String escuela, Integer estado) {
            this.id = id;
            this.codigo = codigo;
            this.nombre = nombre;
            this.ubicacion = ubicacion;
            this.tipo = tipo;
            this.capacidad = capacidad;
            this.equipamiento = equipamiento;
            this.facultad = facultad;
            this.escuela = escuela;
            this.estado = estado;
        }

        // Getters y Setters
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }

        public String getCodigo() { return codigo; }
        public void setCodigo(String codigo) { this.codigo = codigo; }

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }

        public String getUbicacion() { return ubicacion; }
        public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }

        public Integer getCapacidad() { return capacidad; }
        public void setCapacidad(Integer capacidad) { this.capacidad = capacidad; }

        public String getEquipamiento() { return equipamiento; }
        public void setEquipamiento(String equipamiento) { this.equipamiento = equipamiento; }

        public String getFacultad() { return facultad; }
        public void setFacultad(String facultad) { this.facultad = facultad; }

        public String getEscuela() { return escuela; }
        public void setEscuela(String escuela) { this.escuela = escuela; }

        public Integer getEstado() { return estado; }
public void setEstado(Integer estado) { this.estado = estado; }
    }
}