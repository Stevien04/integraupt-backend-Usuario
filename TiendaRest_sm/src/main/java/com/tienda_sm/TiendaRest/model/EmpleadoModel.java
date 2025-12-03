package com.tienda_sm.TiendaRest.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "EmpleadoModel")
@Table(name="tbempleado")
public class EmpleadoModel implements Serializable {
    private static final long serialVersionUID=1L;
    @Id
    @Column(name="idempleado") 
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long idEmpleado;
    @Column(name="Nombre")
    private String nombre;
    @Column(name="Apellido")
    private String apellido;
    @Column(name="Usuario")
    private String usuario;
    @Column(name="Clave")
    private String clave;
    @Column(name="idtipodocumento")
    private long idtipodocumento;
    @Column(name="numerodocumento")
    private String numerodocumento;
    @Column(name="telefono")
    private String telefono;
    @Column(name="Estado")
    private int estado;

    @ManyToOne
    @JoinColumn(name="idcargo", nullable=false)

    private CargoModel idcargo;
}
