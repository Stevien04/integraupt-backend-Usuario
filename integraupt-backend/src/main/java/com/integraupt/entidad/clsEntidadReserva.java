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
    private Integer idReserva;

    @ManyToOne
    @JoinColumn(name = "usuario", nullable = false)
    private clsEntidadUsuario_Reserva usuario; // Cambiado a clsEntidadUsuario_Reserva

    @ManyToOne
    @JoinColumn(name = "espacio", nullable = false)
    private clsEntidadEspacio_Reserva espacio; // Cambiado a clsEntidadEspacio_Reserva

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
    public Integer getIdReserva() { return idReserva; }
    public void setIdReserva(Integer idReserva) { this.idReserva = idReserva; }

    public clsEntidadUsuario_Reserva getUsuario() { return usuario; } // Cambiado
    public void setUsuario(clsEntidadUsuario_Reserva usuario) { this.usuario = usuario; } // Cambiado

    public clsEntidadEspacio_Reserva getEspacio() { return espacio; } // Cambiado
    public void setEspacio(clsEntidadEspacio_Reserva espacio) { this.espacio = espacio; } // Cambiado

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