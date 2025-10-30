package com.integraupt.entidad;

import jakarta.persistence.*;

@Entity
@Table(name = "espacio")
public class clsEntidadEspacio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdEspacio")
    private Integer id;

    @Column(name = "Codigo", nullable = false, length = 20)
    private String codigo;

    @Column(name = "Nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "Ubicacion", length = 200)
    private String ubicacion;

    @Column(name = "Tipo", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoEspacio tipo;

    @Column(name = "Capacidad", nullable = false)
    private Integer capacidad;

    @Column(name = "Equipamiento", columnDefinition = "TEXT")
    private String equipamiento;

    @Column(name = "Facultad", nullable = false)
    private Integer facultadId;

    @Column(name = "Escuela", nullable = false)
    private Integer escuelaId;

    @Column(name = "Estado", nullable = false)
    private Integer estado;

    // Enums para el tipo de espacio
    public enum TipoEspacio {
        Laboratorio, Salon
    }

    // Constructores
    public clsEntidadEspacio() {}

    public clsEntidadEspacio(Integer id, String codigo, String nombre, String ubicacion, 
                           TipoEspacio tipo, Integer capacidad, String equipamiento, 
                           Integer facultadId, Integer escuelaId, Integer estado) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.tipo = tipo;
        this.capacidad = capacidad;
        this.equipamiento = equipamiento;
        this.facultadId = facultadId;
        this.escuelaId = escuelaId;
        this.estado = estado;
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public TipoEspacio getTipo() { return tipo; }
    public void setTipo(TipoEspacio tipo) { this.tipo = tipo; }

    public Integer getCapacidad() { return capacidad; }
    public void setCapacidad(Integer capacidad) { this.capacidad = capacidad; }

    public String getEquipamiento() { return equipamiento; }
    public void setEquipamiento(String equipamiento) { this.equipamiento = equipamiento; }

    public Integer getFacultadId() { return facultadId; }
    public void setFacultadId(Integer facultadId) { this.facultadId = facultadId; }

    public Integer getEscuelaId() { return escuelaId; }
    public void setEscuelaId(Integer escuelaId) { this.escuelaId = escuelaId; }

    public Integer getEstado() { return estado; }
    public void setEstado(Integer estado) { this.estado = estado; }
}