package com.integraupt.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO para recibir los datos de creación y actualización de usuarios.
 */
public class clsDTOUsuarioRequest {

    @NotBlank(message = "El código es obligatorio")
    @Size(max = 20, message = "El código no debe exceder 20 caracteres")
    private String codigo;

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 30, message = "Los nombres no deben exceder 30 caracteres")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 30, message = "Los apellidos no deben exceder 30 caracteres")
    private String apellidos;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    @Size(max = 30, message = "El correo no debe exceder 30 caracteres")
    private String email;

    @NotBlank(message = "El tipo de documento es obligatorio")
    @Size(max = 30, message = "El tipo de documento no debe exceder 30 caracteres")
    private String tipoDocumento;

    @NotBlank(message = "El número de documento es obligatorio")
    @Size(max = 20, message = "El número de documento no debe exceder 20 caracteres")
    private String numeroDocumento;

    @Size(max = 11, message = "El celular no debe exceder 11 caracteres")
    private String celular;

    @NotNull(message = "La facultad es obligatoria")
    private Integer facultadId;

    @NotNull(message = "La escuela es obligatoria")
    private Integer escuelaId;

    @NotBlank(message = "El rol es obligatorio")
    private String rol;

    @NotBlank(message = "El género es obligatorio")
    private String genero;

    private String password;

    @NotNull(message = "El estado es obligatorio")
    private Integer estado;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
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

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
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

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
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
}