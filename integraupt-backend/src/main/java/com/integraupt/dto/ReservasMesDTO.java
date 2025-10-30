package com.integraupt.dto;

public class ReservasMesDTO {
    private String mes;
    private Integer anio;
    private Long totalReservas;

    // Constructor vacío
    public ReservasMesDTO() {
    }

    // Constructor con parámetros
    public ReservasMesDTO(String mes, Integer anio, Long totalReservas) {
        this.mes = mes;
        this.anio = anio;
        this.totalReservas = totalReservas;
    }

    // Getters y Setters
    public String getMes() { return mes; }
    public void setMes(String mes) { this.mes = mes; }

    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { this.anio = anio; }

    public Long getTotalReservas() { return totalReservas; }
    public void setTotalReservas(Long totalReservas) { this.totalReservas = totalReservas; }
}