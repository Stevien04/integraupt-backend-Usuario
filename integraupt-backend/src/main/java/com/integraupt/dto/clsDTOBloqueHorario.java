package com.integraupt.dto;

public class clsDTOBloqueHorario {

    private Integer id;
    private Integer orden;
    private String nombre;
    private String horaInicio;
    private String horaFinal;

    public clsDTOBloqueHorario() {
    }

    public clsDTOBloqueHorario(Integer id, Integer orden, String nombre, String horaInicio, String horaFinal) {
        this.id = id;
        this.orden = orden;
        this.nombre = nombre;
        this.horaInicio = horaInicio;
        this.horaFinal = horaFinal;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFinal() {
        return horaFinal;
    }

    public void setHoraFinal(String horaFinal) {
        this.horaFinal = horaFinal;
    }
}