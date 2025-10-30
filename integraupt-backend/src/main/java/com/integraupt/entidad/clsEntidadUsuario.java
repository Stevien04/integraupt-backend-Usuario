package com.integraupt.entidad;

import jakarta.persistence.*;

/**
 * Entidad que representa a los usuarios registrados en la base de datos.
 */
@Entity
@Table(name = "usuario")
public class clsEntidadUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdUsuario") // <- igual que en la tabla real
    private Integer id;

    @Column(name = "Nombre", nullable = false, length = 30)
    private String nombres;

    @Column(name = "Apellido", nullable = false, length = 30)
    private String apellidos;

    @Column(name = "CodigoU", nullable = false, length = 20)
    private String codigo;

    @Column(name = "CorreoU", nullable = false, length = 30)
    private String email;

    @Column(name = "TipoDoc", nullable = false, length = 30)
    private String tipoDocumento;

    @Column(name = "NumDoc", nullable = false, length = 20)
    private String numeroDocumento;

    @Column(name = "Rol", nullable = false)
    private Integer rolId;

    @Column(name = "Facultad", nullable = false)
    private Integer facultadId;

    @Column(name = "Escuela", nullable = false)
    private Integer escuelaId;

    @Column(name = "Celular", length = 11)
    private String celular;

    @Column(name = "Genero")
    private Boolean genero;

    @Column(name = "Password", nullable = false, length = 255)
    private String password;

    @Column(name = "Estado", nullable = false)
    private Integer estado;

    @Column(name = "Sesion", nullable = false)
    private Boolean sesion;

    public clsEntidadUsuario() {}

    // Getters y Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public Integer getRolId() {
        return rolId;
    }

    public void setRolId(Integer rolId) {
        this.rolId = rolId;
    }

    public Integer getFacultadId() {
        return facultadId;
    }

    public void setFacultadId(Integer facultadId) {
        this.facultadId = facultadId;
    }

    public Integer getEscuelaId() {
        return escuelaId;
    }

    public void setEscuelaId(Integer escuelaId) {
        this.escuelaId = escuelaId;
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