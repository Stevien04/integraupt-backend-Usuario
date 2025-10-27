package com.integraupt.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Entidad JPA que representa la tabla {@code usuario}.
 * Incluye los campos necesarios para administrar estados activos/inactivos.
 */
@Entity
@Table(name = "usuario")
public class clsEntidadUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdUsuario")
    private Integer idUsuario;

    @NotBlank
    @Size(max = 30)
    @Column(name = "Nombre", nullable = false, length = 30)
    private String nombre;

    @NotBlank
    @Size(max = 30)
    @Column(name = "Apellido", nullable = false, length = 30)
    private String apellido;

    @NotBlank
    @Size(max = 20)
    @Column(name = "CodigoU", nullable = false, length = 20)
    private String codigoU;

    @NotBlank
    @Email
    @Size(max = 30)
    @Column(name = "CorreoU", nullable = false, length = 30)
    private String correoU;

    @NotBlank
    @Size(max = 30)
    @Column(name = "TipoDoc", nullable = false, length = 30)
    private String tipoDoc;

    @NotBlank
    @Size(max = 20)
    @Column(name = "NumDoc", nullable = false, length = 20)
    private String numDoc;

    @NotNull
    @Column(name = "Rol", nullable = false)
    private Integer rol;

    @NotNull
    @Column(name = "Facultad", nullable = false)
    private Integer facultad;

    @NotNull
    @Column(name = "Escuela", nullable = false)
    private Integer escuela;

    @Size(max = 11)
    @Column(name = "Celular", length = 11)
    private String celular;

    @Column(name = "Genero")
    private Boolean genero;

    @NotBlank
    @Size(max = 255)
    @Column(name = "Password", nullable = false, length = 255)
    private String password;

    @Column(name = "Estado", nullable = false)
    private Integer estado;

    @Column(name = "Sesion", nullable = false)
    private Boolean sesion;

    public clsEntidadUsuario() {
        // Constructor por defecto requerido por JPA
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCodigoU() {
        return codigoU;
    }

    public void setCodigoU(String codigoU) {
        this.codigoU = codigoU;
    }

    public String getCorreoU() {
        return correoU;
    }

    public void setCorreoU(String correoU) {
        this.correoU = correoU;
    }

    public String getTipoDoc() {
        return tipoDoc;
    }

    public void setTipoDoc(String tipoDoc) {
        this.tipoDoc = tipoDoc;
    }

    public String getNumDoc() {
        return numDoc;
    }

    public void setNumDoc(String numDoc) {
        this.numDoc = numDoc;
    }

    public Integer getRol() {
        return rol;
    }

    public void setRol(Integer rol) {
        this.rol = rol;
    }

    public Integer getFacultad() {
        return facultad;
    }

    public void setFacultad(Integer facultad) {
        this.facultad = facultad;
    }

    public Integer getEscuela() {
        return escuela;
    }

    public void setEscuela(Integer escuela) {
        this.escuela = escuela;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public Boolean getGenero() {
        return genero;
    }

    public void setGenero(Boolean genero) {
        this.genero = genero;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public Boolean getSesion() {
        return sesion;
    }

    public void setSesion(Boolean sesion) {
        this.sesion = sesion;
    }
}