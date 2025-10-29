package com.integraupt.entidad;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad que representa una reserva de espacios dentro del sistema.
 */
@Entity
@Table(name = "reserva")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class clsEntidadReserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdReserva")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario", nullable = false)
    private clsEntidadUsuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "espacio", nullable = false)
    private clsEntidadEspacio espacio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bloque", nullable = false)
    private clsEntidadBloqueHorario bloque;

    @Column(name = "fechaReserva", nullable = false)
    private LocalDate fechaReserva;

    @Column(name = "fechaSolicitud", nullable = false)
    private LocalDateTime fechaSolicitud;

    @Column(name = "estado", nullable = false, length = 50)
    private String estado;

    @Column(name = "Descripcion", nullable = false, columnDefinition = "tinytext")
    private String descripcion;

    @Column(name = "Motivo", columnDefinition = "tinytext")
    private String motivo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public clsEntidadUsuario getUsuario() {
        return usuario;
    }

    public void setUsuario(clsEntidadUsuario usuario) {
        this.usuario = usuario;
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

    public LocalDate getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(LocalDate fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDateTime fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
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