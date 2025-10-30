package com.integraupt.dto;

import com.integraupt.entidad.clsEntidadHorario;
import jakarta.validation.constraints.NotNull;

public class clsDTOHorarioRequest {

    @NotNull(message = "El ID del espacio es obligatorio")
    private Integer espacioId;

    @NotNull(message = "El ID del bloque es obligatorio")
    private Integer bloqueId;

    @NotNull(message = "El día de la semana es obligatorio")
    private clsEntidadHorario.DiaSemana diaSemana;

    @NotNull(message = "El estado de ocupación es obligatorio")
    private Boolean ocupado;

    // Constructor por defecto
    public clsDTOHorarioRequest() {}

    // Getters y Setters
    public Integer getEspacioId() { return espacioId; }
    public void setEspacioId(Integer espacioId) { this.espacioId = espacioId; }

    public Integer getBloqueId() { return bloqueId; }
    public void setBloqueId(Integer bloqueId) { this.bloqueId = bloqueId; }

    public clsEntidadHorario.DiaSemana getDiaSemana() { return diaSemana; }
    public void setDiaSemana(clsEntidadHorario.DiaSemana diaSemana) { this.diaSemana = diaSemana; }

    public Boolean getOcupado() { return ocupado; }
    public void setOcupado(Boolean ocupado) { this.ocupado = ocupado; }
}