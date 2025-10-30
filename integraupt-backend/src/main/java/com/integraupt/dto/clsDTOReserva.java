package com.integraupt.dto;

/**
 * DTO que expone la información relevante de una reserva para la administración.
 */
public class clsDTOReserva {

    private Integer id;
    private String estado;
    private String fechaReserva;
    private String fechaSolicitud;
    private String descripcion;
    private String motivo;
    private String solicitante;
    private String codigoSolicitante;
    private String correoSolicitante;
    private String espacio;
    private String codigoEspacio;
    private String tipoEspacio;
    private String bloque;
    private String horaInicio;
    private String horaFin;

    public clsDTOReserva(Integer id, String estado, String fechaReserva, String fechaSolicitud, String descripcion,
                         String motivo, String solicitante, String codigoSolicitante, String correoSolicitante,
                         String espacio, String codigoEspacio, String tipoEspacio, String bloque,
                         String horaInicio, String horaFin) {
        this.id = id;
        this.estado = estado;
        this.fechaReserva = fechaReserva;
        this.fechaSolicitud = fechaSolicitud;
        this.descripcion = descripcion;
        this.motivo = motivo;
        this.solicitante = solicitante;
        this.codigoSolicitante = codigoSolicitante;
        this.correoSolicitante = correoSolicitante;
        this.espacio = espacio;
        this.codigoEspacio = codigoEspacio;
        this.tipoEspacio = tipoEspacio;
        this.bloque = bloque;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public Integer getId() {
        return id;
    }

    public String getEstado() {
        return estado;
    }

    public String getFechaReserva() {
        return fechaReserva;
    }

    public String getFechaSolicitud() {
        return fechaSolicitud;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getMotivo() {
        return motivo;
    }

    public String getSolicitante() {
        return solicitante;
    }

    public String getCodigoSolicitante() {
        return codigoSolicitante;
    }

    public String getCorreoSolicitante() {
        return correoSolicitante;
    }

    public String getEspacio() {
        return espacio;
    }

    public String getCodigoEspacio() {
        return codigoEspacio;
    }

    public String getTipoEspacio() {
        return tipoEspacio;
    }

    public String getBloque() {
        return bloque;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }
}