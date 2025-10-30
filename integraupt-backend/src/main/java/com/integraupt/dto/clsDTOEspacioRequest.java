package com.integraupt.dto;

import com.integraupt.entidad.clsEntidadEspacio;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class clsDTOEspacioRequest {

    @NotBlank(message = "El código es obligatorio")
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String ubicacion;

    @NotNull(message = "El tipo es obligatorio")
    private clsEntidadEspacio.TipoEspacio tipo;

    @NotNull(message = "La capacidad es obligatoria")
    @Positive(message = "La capacidad debe ser mayor a 0")
    private Integer capacidad;

    private String equipamiento;

    @NotNull(message = "La facultad es obligatoria")
    private Integer facultadId;

    @NotNull(message = "La escuela es obligatoria")
    private Integer escuelaId;

    @NotNull(message = "El estado es obligatorio")
    private Integer estado;

    // Constructor por defecto
    public clsDTOEspacioRequest() {}

    // Getters y Setters
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public clsEntidadEspacio.TipoEspacio getTipo() { return tipo; }
    public void setTipo(clsEntidadEspacio.TipoEspacio tipo) { this.tipo = tipo; }

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