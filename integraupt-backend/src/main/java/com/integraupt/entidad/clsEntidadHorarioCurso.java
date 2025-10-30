package com.integraupt.entidad;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "horario_curso")
public class clsEntidadHorarioCurso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdHorarioCurso")
    private Integer id;

    @Column(name = "Curso", nullable = false, length = 100)
    private String curso;

    @Column(name = "Docente", nullable = false)
    private Integer docenteId;

    @Column(name = "Espacio", nullable = false)
    private Integer espacioId;

    @Column(name = "Bloque", nullable = false)
    private Integer bloqueId;

    @Column(name = "DiaSemana", nullable = false)
    @Enumerated(EnumType.STRING)
    private DiaSemana diaSemana;

    @Column(name = "FechaInicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "FechaFin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "Estado", nullable = false)
    private Boolean estado;

    // Enums para los días de la semana
    public enum DiaSemana {
        Lunes, Martes, Miercoles, Jueves, Viernes, Sabado
    }

    // Constructores
    public clsEntidadHorarioCurso() {}

    public clsEntidadHorarioCurso(Integer id, String curso, Integer docenteId, Integer espacioId, 
                                 Integer bloqueId, DiaSemana diaSemana, LocalDate fechaInicio, 
                                 LocalDate fechaFin, Boolean estado) {
        this.id = id;
        this.curso = curso;
        this.docenteId = docenteId;
        this.espacioId = espacioId;
        this.bloqueId = bloqueId;
        this.diaSemana = diaSemana;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }

    public Integer getDocenteId() { return docenteId; }
    public void setDocenteId(Integer docenteId) { this.docenteId = docenteId; }

    public Integer getEspacioId() { return espacioId; }
    public void setEspacioId(Integer espacioId) { this.espacioId = espacioId; }

    public Integer getBloqueId() { return bloqueId; }
    public void setBloqueId(Integer bloqueId) { this.bloqueId = bloqueId; }

    public DiaSemana getDiaSemana() { return diaSemana; }
    public void setDiaSemana(DiaSemana diaSemana) { this.diaSemana = diaSemana; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }
}