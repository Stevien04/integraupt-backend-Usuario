package com.integraupt.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class clsDTOReservaUsuario {

    private Integer idReserva;
    private Integer usuarioId;
    private Integer espacioId;
    private String espacioNombre;
    private String espacioCodigo;
    private Integer bloqueId;
    private String bloqueNombre;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String estado;
    private LocalDate fechaReserva;
    private LocalDateTime fechaSolicitud;
    private String descripcion;
    private String motivo;

    public Integer getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(Integer idReserva) {
        this.idReserva = idReserva;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Integer getEspacioId() {
        return espacioId;
    }

    public void setEspacioId(Integer espacioId) {
        this.espacioId = espacioId;
    }

    public String getEspacioNombre() {
        return espacioNombre;
    }

    public void setEspacioNombre(String espacioNombre) {
        this.espacioNombre = espacioNombre;
    }

    public String getEspacioCodigo() {
        return espacioCodigo;
    }

    public void setEspacioCodigo(String espacioCodigo) {
        this.espacioCodigo = espacioCodigo;
    }

    public Integer getBloqueId() {
        return bloqueId;
    }

    public void setBloqueId(Integer bloqueId) {
        this.bloqueId = bloqueId;
    }

    public String getBloqueNombre() {
        return bloqueNombre;
    }

    public void setBloqueNombre(String bloqueNombre) {
        this.bloqueNombre = bloqueNombre;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDate getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(LocalDate fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDateTime fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}