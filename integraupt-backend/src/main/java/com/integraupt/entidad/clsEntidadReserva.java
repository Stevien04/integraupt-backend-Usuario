package com.integraupt.entidad;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reserva")
public class clsEntidadReserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdReserva")
    private Long idReserva;

    @ManyToOne
    @JoinColumn(name = "usuario", nullable = false)
    private clsEntidadUsuario usuario;

    @ManyToOne
    @JoinColumn(name = "espacio", nullable = false)
    private clsEntidadEspacio espacio;

    @ManyToOne
    @JoinColumn(name = "bloque", nullable = false)
    private clsEntidadBloqueHorario bloque;

    @Column(name = "fechaReserva")
    private LocalDate fechaReserva;

    @Column(name = "estado")
    private String estado;

    @Column(name = "fechaSolicitud")
    private LocalDateTime fechaSolicitud;

    @Column(name = "Descripcion")
    private String descripcion;

    @Column(name = "Motivo")
    private String motivo;

    // Getters y Setters
    public Long getIdReserva() { return idReserva; }
    public void setIdReserva(Long idReserva) { this.idReserva = idReserva; }

    public clsEntidadUsuario getUsuario() { return usuario; }
    public void setUsuario(clsEntidadUsuario usuario) { this.usuario = usuario; }

    public clsEntidadEspacio getEspacio() { return espacio; }
    public void setEspacio(clsEntidadEspacio espacio) { this.espacio = espacio; }

    public clsEntidadBloqueHorario getBloque() { return bloque; }
    public void setBloque(clsEntidadBloqueHorario bloque) { this.bloque = bloque; }

    public LocalDate getFechaReserva() { return fechaReserva; }
    public void setFechaReserva(LocalDate fechaReserva) { this.fechaReserva = fechaReserva; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(LocalDateTime fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
