package com.integraupt.dto;

import java.time.LocalTime;

public class clsDTOBloqueDisponible {

    private Integer bloqueId;
    private String nombre;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private boolean disponible;

    public clsDTOBloqueDisponible(Integer bloqueId, String nombre, LocalTime horaInicio, LocalTime horaFin, boolean disponible) {
        this.bloqueId = bloqueId;
        this.nombre = nombre;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.disponible = disponible;
    }

    public Integer getBloqueId() {
        return bloqueId;
    }

    public void setBloqueId(Integer bloqueId) {
        this.bloqueId = bloqueId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
}