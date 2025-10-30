package com.integraupt.controlador;

import com.integraupt.dto.ReporteEstadisticasDTO;
import com.integraupt.dto.ReservasMesDTO;
import com.integraupt.dto.UsoEspacioDTO;
import com.integraupt.servicio.ReportesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "*")
public class ReportesController {

    private final ReportesService reportesService;

    public ReportesController(ReportesService reportesService) {
        this.reportesService = reportesService;
    }

    @GetMapping("/estadisticas-generales")
    public ResponseEntity<ReporteEstadisticasDTO> obtenerEstadisticasGenerales() {
        ReporteEstadisticasDTO estadisticas = reportesService.obtenerEstadisticasGenerales();
        return ResponseEntity.ok(estadisticas);
    }

    @GetMapping("/uso-espacios")
    public ResponseEntity<List<UsoEspacioDTO>> obtenerUsoEspacios() {
        List<UsoEspacioDTO> usoEspacios = reportesService.obtenerUsoEspacios();
        return ResponseEntity.ok(usoEspacios);
    }

    @GetMapping("/reservas-mes")
    public ResponseEntity<List<ReservasMesDTO>> obtenerReservasPorMes() {
        List<ReservasMesDTO> reservasPorMes = reportesService.obtenerReservasPorMes();
        return ResponseEntity.ok(reservasPorMes);
    }
}