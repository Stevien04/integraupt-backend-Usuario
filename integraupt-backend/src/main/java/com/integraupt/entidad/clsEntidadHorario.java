package com.integraupt.entidad;

import jakarta.persistence.*;

@Entity
@Table(name = "horarios")
public class clsEntidadHorario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdHorario")
    private Integer id;

    @Column(name = "espacio", nullable = false)
    private Integer espacioId;

    @Column(name = "bloque", nullable = false)
    private Integer bloqueId;

    @Column(name = "diaSemana", nullable = false)
    @Enumerated(EnumType.STRING)
    private DiaSemana diaSemana;

    @Column(name = "ocupado", nullable = false)
    private Boolean ocupado;

    // Enums para los días de la semana
    public enum DiaSemana {
        Lunes, Martes, Miercoles, Jueves, Viernes, Sabado
    }

    // Constructores
    public clsEntidadHorario() {}

    public clsEntidadHorario(Integer id, Integer espacioId, Integer bloqueId, 
                           DiaSemana diaSemana, Boolean ocupado) {
        this.id = id;
        this.espacioId = espacioId;
        this.bloqueId = bloqueId;
        this.diaSemana = diaSemana;
        this.ocupado = ocupado;
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getEspacioId() { return espacioId; }
    public void setEspacioId(Integer espacioId) { this.espacioId = espacioId; }

    public Integer getBloqueId() { return bloqueId; }
    public void setBloqueId(Integer bloqueId) { this.bloqueId = bloqueId; }

    public DiaSemana getDiaSemana() { return diaSemana; }
    public void setDiaSemana(DiaSemana diaSemana) { this.diaSemana = diaSemana; }

    public Boolean getOcupado() { return ocupado; }
    public void setOcupado(Boolean ocupado) { this.ocupado = ocupado; }
}