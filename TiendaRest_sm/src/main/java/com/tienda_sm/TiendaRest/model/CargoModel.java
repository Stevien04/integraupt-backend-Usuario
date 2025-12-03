package com.tienda_sm.TiendaRest.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Entity(name = "CargoModel")
@Table(name="tbcargo")
public class CargoModel implements Serializable {
    private static final long serialVersionUID=1L;

    @Id
    @Column(name="idcargo") //NOMBRE DE LA COLUMNA EN LA BD
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idcargo;

    @Column(name="nombre")
    private String nombre;

    @Column(name="estado")
    private int estado;
}
