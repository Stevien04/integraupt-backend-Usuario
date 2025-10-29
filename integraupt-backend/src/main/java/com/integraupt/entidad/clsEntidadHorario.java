package com.integraupt.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entidad que representa la ocupación de un bloque horario para un espacio específico.
 */
@Entity
@Table(name = "horarios")
public class clsEntidadHorario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdHorario")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "espacio", nullable = false)
    private clsEntidadEspacio espacio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bloque", nullable = false)
    private clsEntidadBloqueHorario bloque;

    @Column(name = "diaSemana", nullable = false)
    private String diaSemana;

    @Column(name = "ocupado", nullable = false)
    private boolean ocupado;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public clsEntidadEspacio getEspacio() {
        return espacio;
    }

    public void setEspacio(clsEntidadEspacio espacio) {
        this.espacio = espacio;
    }

    public clsEntidadBloqueHorario getBloque() {
        return bloque;
    }

    public void setBloque(clsEntidadBloqueHorario bloque) {
        this.bloque = bloque;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }

    public boolean isOcupado() {
        return ocupado;
    }

    public void setOcupado(boolean ocupado) {
        this.ocupado = ocupado;
    }
}