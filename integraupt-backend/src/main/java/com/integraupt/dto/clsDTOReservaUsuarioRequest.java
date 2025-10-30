package com.integraupt.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO para recibir las solicitudes de reserva realizadas por usuarios.
 */
public class clsDTOReservaUsuarioRequest {

    @NotNull(message = "El identificador del usuario es obligatorio")
    @Min(value = 1, message = "El identificador del usuario debe ser mayor a cero")
    private Integer usuario;

    @NotNull(message = "El identificador del espacio es obligatorio")
    @Min(value = 1, message = "El identificador del espacio debe ser mayor a cero")
    private Integer espacio;

    @NotNull(message = "El identificador del bloque horario es obligatorio")
    @Min(value = 1, message = "El identificador del bloque horario debe ser mayor a cero")
    private Integer bloque;

    @NotBlank(message = "La fecha de reserva es obligatoria")
    private String fechaReserva;

    @NotBlank(message = "La descripción de la reserva es obligatoria")
    @Size(max = 255, message = "La descripción no debe exceder los 255 caracteres")
    private String descripcion;

    @Size(max = 255, message = "El motivo no debe exceder los 255 caracteres")
    private String motivo;

    public clsDTOReservaUsuarioRequest() {
    }

    public Integer getUsuario() {
        return usuario;
    }

    public void setUsuario(Integer usuario) {
        this.usuario = usuario;
    }

    public Integer getEspacio() {
        return espacio;
    }

    public void setEspacio(Integer espacio) {
        this.espacio = espacio;
    }

    public Integer getBloque() {
        return bloque;
    }

    public void setBloque(Integer bloque) {
        this.bloque = bloque;
    }

    public String getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(String fechaReserva) {
        this.fechaReserva = fechaReserva;
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