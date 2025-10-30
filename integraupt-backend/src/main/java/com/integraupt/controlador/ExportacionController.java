package com.integraupt.controlador;

import java.util.List;

import com.integraupt.dto.ReporteEstadisticasDTO;
import com.integraupt.dto.ReservasMesDTO;
import com.integraupt.dto.UsoEspacioDTO;
import com.integraupt.servicio.ExportacionService;
import com.integraupt.servicio.ReportesService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@RestController
@RequestMapping("/api/exportacion")
@CrossOrigin(origins = "*")
public class ExportacionController {

    private final ExportacionService exportacionService;
    private final ReportesService reportesService;

    public ExportacionController(ExportacionService exportacionService, ReportesService reportesService) {
        this.exportacionService = exportacionService;
        this.reportesService = reportesService;
    }

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> descargarReportePDF() {
        try {
            ReporteEstadisticasDTO estadisticas = reportesService.obtenerEstadisticasGenerales();
            List<UsoEspacioDTO> usoEspacios = reportesService.obtenerUsoEspacios();
            List<ReservasMesDTO> reservasPorMes = reportesService.obtenerReservasPorMes();

            byte[] pdfBytes = exportacionService.generarReportePDF(estadisticas, usoEspacios, reservasPorMes);

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "reporte_estadisticas_" + timestamp + ".pdf";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/excel")
    public ResponseEntity<byte[]> descargarReporteExcel() {
        try {
            ReporteEstadisticasDTO estadisticas = reportesService.obtenerEstadisticasGenerales();
            List<UsoEspacioDTO> usoEspacios = reportesService.obtenerUsoEspacios();
            List<ReservasMesDTO> reservasPorMes = reportesService.obtenerReservasPorMes();

            byte[] excelBytes = exportacionService.generarReporteExcel(estadisticas, usoEspacios, reservasPorMes);

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "reporte_estadisticas_" + timestamp + ".xlsx";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(excelBytes);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}