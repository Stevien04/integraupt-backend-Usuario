package com.integraupt.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad que representa a los usuarios que pueden solicitar reservas.
 */
@Entity
@Table(name = "usuario")
public class clsEntidadUsuario_Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdUsuario")
    private Integer id;

    @Column(name = "Nombre", nullable = false, length = 30)
    private String nombre;

    @Column(name = "Apellido", nullable = false, length = 30)
    private String apellido;

    @Column(name = "CodigoU", nullable = false, length = 20)
    private String codigo;

    @Column(name = "CorreoU", nullable = false, length = 30)
    private String correo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getNombreCompleto() {
        StringBuilder builder = new StringBuilder();
        if (nombre != null) {
            builder.append(nombre.trim());
        }
        if (apellido != null) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(apellido.trim());
        }
        return builder.toString();
    }
}