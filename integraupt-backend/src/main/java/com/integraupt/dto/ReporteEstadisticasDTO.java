package com.integraupt.dto;

import java.math.BigDecimal;

public class ReporteEstadisticasDTO {
    private Long totalEstudiantes;
    private Long totalDocentes;
    private Long reservasActivas;
    private BigDecimal tasaUso;
    private Long reservasEsteMes;
    private Long reservasMesAnterior;
    private String variacionReservas;

    // Constructor vacío
    public ReporteEstadisticasDTO() {
    }

    // Constructor con parámetros
    public ReporteEstadisticasDTO(Long totalEstudiantes, Long totalDocentes, 
                                 Long reservasActivas, BigDecimal tasaUso,
                                 Long reservasEsteMes, Long reservasMesAnterior) {
        this.totalEstudiantes = totalEstudiantes;
        this.totalDocentes = totalDocentes;
        this.reservasActivas = reservasActivas;
        this.tasaUso = tasaUso;
        this.reservasEsteMes = reservasEsteMes;
        this.reservasMesAnterior = reservasMesAnterior;
        this.variacionReservas = calcularVariacion(reservasEsteMes, reservasMesAnterior);
    }

    private String calcularVariacion(Long actual, Long anterior) {
        if (anterior == null || anterior == 0) return "Nueva";
        double variacion = ((actual - anterior) / (double) anterior) * 100;
        if (variacion > 0) return "↑ " + String.format("%.0f", variacion) + "%";
        else if (variacion < 0) return "↓ " + String.format("%.0f", Math.abs(variacion)) + "%";
        else return "→ 0%";
    }

    // Getters y Setters
    public Long getTotalEstudiantes() { return totalEstudiantes; }
    public void setTotalEstudiantes(Long totalEstudiantes) { this.totalEstudiantes = totalEstudiantes; }

    public Long getTotalDocentes() { return totalDocentes; }
    public void setTotalDocentes(Long totalDocentes) { this.totalDocentes = totalDocentes; }

    public Long getReservasActivas() { return reservasActivas; }
    public void setReservasActivas(Long reservasActivas) { this.reservasActivas = reservasActivas; }

    public BigDecimal getTasaUso() { return tasaUso; }
    public void setTasaUso(BigDecimal tasaUso) { this.tasaUso = tasaUso; }

    public Long getReservasEsteMes() { return reservasEsteMes; }
    public void setReservasEsteMes(Long reservasEsteMes) { this.reservasEsteMes = reservasEsteMes; }

    public Long getReservasMesAnterior() { return reservasMesAnterior; }
    public void setReservasMesAnterior(Long reservasMesAnterior) { this.reservasMesAnterior = reservasMesAnterior; }

    public String getVariacionReservas() { return variacionReservas; }
    public void setVariacionReservas(String variacionReservas) { this.variacionReservas = variacionReservas; }
}