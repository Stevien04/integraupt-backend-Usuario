package com.integraupt.dto;

import java.math.BigDecimal;

public class UsoEspacioDTO {
    private String nombreEspacio;
    private String codigoEspacio;
    private String tipoEspacio;
    private Long totalReservas;
    private BigDecimal porcentajeUso;

    // Constructor vacío
    public UsoEspacioDTO() {
    }

    // Constructor con parámetros
    public UsoEspacioDTO(String nombreEspacio, String codigoEspacio, String tipoEspacio, 
                        Long totalReservas, BigDecimal porcentajeUso) {
        this.nombreEspacio = nombreEspacio;
        this.codigoEspacio = codigoEspacio;
        this.tipoEspacio = tipoEspacio;
        this.totalReservas = totalReservas;
        this.porcentajeUso = porcentajeUso;
    }

    // Getters y Setters
    public String getNombreEspacio() { return nombreEspacio; }
    public void setNombreEspacio(String nombreEspacio) { this.nombreEspacio = nombreEspacio; }

    public String getCodigoEspacio() { return codigoEspacio; }
    public void setCodigoEspacio(String codigoEspacio) { this.codigoEspacio = codigoEspacio; }

    public String getTipoEspacio() { return tipoEspacio; }
    public void setTipoEspacio(String tipoEspacio) { this.tipoEspacio = tipoEspacio; }

    public Long getTotalReservas() { return totalReservas; }
    public void setTotalReservas(Long totalReservas) { this.totalReservas = totalReservas; }

    public BigDecimal getPorcentajeUso() { return porcentajeUso; }
    public void setPorcentajeUso(BigDecimal porcentajeUso) { this.porcentajeUso = porcentajeUso; }
}