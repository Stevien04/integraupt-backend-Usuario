package com.integraupt.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO para recibir la solicitud de cierre de sesión.
 */
public class clsDTOLogoutRequest {

    @NotNull(message = "El identificador del usuario es obligatorio")
    private Integer usuarioId;

    public clsDTOLogoutRequest() {
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }
}