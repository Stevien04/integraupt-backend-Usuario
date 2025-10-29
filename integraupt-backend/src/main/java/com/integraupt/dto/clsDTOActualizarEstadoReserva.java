package com.integraupt.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para recibir el motivo al rechazar una reserva.
 */
public class clsDTOActualizarEstadoReserva {

    @NotBlank(message = "El motivo es obligatorio")
    private String motivo;

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}