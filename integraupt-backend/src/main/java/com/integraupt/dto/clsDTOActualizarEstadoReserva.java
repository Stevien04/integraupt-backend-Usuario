package com.integraupt.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO utilizado para actualizar el estado de una reserva por parte del administrador.
 */
public class clsDTOActualizarEstadoReserva {

    @NotBlank
    private String estado;

    private String motivo;

    private String aprobadoPor;

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getAprobadoPor() {
        return aprobadoPor;
    }

    public void setAprobadoPor(String aprobadoPor) {
        this.aprobadoPor = aprobadoPor;
    }
}