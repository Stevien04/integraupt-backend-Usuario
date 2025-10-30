package com.integraupt.dto;

import com.integraupt.entidad.clsEntidadHorarioCurso;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class clsDTOCursoHorarioRequest {

    @NotNull(message = "El nombre del curso es obligatorio")
    private String curso;

    @NotNull(message = "El ID del docente es obligatorio")
    private Integer docenteId;

    @NotNull(message = "El ID del espacio es obligatorio")
    private Integer espacioId;

    @NotNull(message = "El ID del bloque es obligatorio")
    private Integer bloqueId;

    @NotNull(message = "El día de la semana es obligatorio")
    private clsEntidadHorarioCurso.DiaSemana diaSemana;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate fechaFin;

    @NotNull(message = "El estado es obligatorio")
    private Boolean estado;

    public clsDTOCursoHorarioRequest() {}

    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }

    public Integer getDocenteId() { return docenteId; }
    public void setDocenteId(Integer docenteId) { this.docenteId = docenteId; }

    public Integer getEspacioId() { return espacioId; }
    public void setEspacioId(Integer espacioId) { this.espacioId = espacioId; }

    public Integer getBloqueId() { return bloqueId; }
    public void setBloqueId(Integer bloqueId) { this.bloqueId = bloqueId; }

    public clsEntidadHorarioCurso.DiaSemana getDiaSemana() { return diaSemana; }
    public void setDiaSemana(clsEntidadHorarioCurso.DiaSemana diaSemana) { this.diaSemana = diaSemana; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }
}