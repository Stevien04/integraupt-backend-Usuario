package com.integraupt.servicio;

import com.integraupt.dto.ReporteEstadisticasDTO;
import com.integraupt.dto.ReservasMesDTO;
import com.integraupt.dto.UsoEspacioDTO;

// Importaciones para PDF
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

// Importaciones para Excel
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExportacionService {

    // Usar la Font de iTextPDF completamente calificada
    private static final com.itextpdf.text.Font TITLE_FONT = 
        new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD);
    
    private static final com.itextpdf.text.Font SUBTITLE_FONT = 
        new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 14, com.itextpdf.text.Font.BOLD);
    
    private static final com.itextpdf.text.Font NORMAL_FONT = 
        new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10);

    public byte[] generarReportePDF(ReporteEstadisticasDTO estadisticas, 
                                   List<UsoEspacioDTO> usoEspacios, 
                                   List<ReservasMesDTO> reservasPorMes) throws DocumentException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, baos);

        document.open();

        // Título del reporte
        Paragraph title = new Paragraph("REPORTE DE ESTADÍSTICAS - INTEGRAUPT", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Fecha de generación
        String fechaGeneracion = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        Paragraph fecha = new Paragraph("Generado el: " + fechaGeneracion, NORMAL_FONT);
        fecha.setSpacingAfter(20);
        document.add(fecha);

        // Estadísticas generales
        agregarEstadisticasGenerales(document, estadisticas);
        
        // Uso de espacios
        agregarUsoEspacios(document, usoEspacios);
        
        // Reservas por mes
        agregarReservasPorMes(document, reservasPorMes);

        document.close();
        return baos.toByteArray();
    }

    public byte[] generarReporteExcel(ReporteEstadisticasDTO estadisticas, 
                                     List<UsoEspacioDTO> usoEspacios, 
                                     List<ReservasMesDTO> reservasPorMes) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            
            // Estilos - Aquí usamos la Font de Apache POI
            CellStyle headerStyle = crearEstiloHeader(workbook);
            CellStyle dataStyle = crearEstiloDatos(workbook);

            // Hoja 1: Estadísticas Generales
            Sheet sheetEstadisticas = workbook.createSheet("Estadísticas Generales");
            crearHojaEstadisticas(sheetEstadisticas, estadisticas, headerStyle, dataStyle);

            // Hoja 2: Uso de Espacios
            Sheet sheetUsoEspacios = workbook.createSheet("Uso de Espacios");
            crearHojaUsoEspacios(sheetUsoEspacios, usoEspacios, headerStyle, dataStyle);

            // Hoja 3: Reservas por Mes
            Sheet sheetReservasMes = workbook.createSheet("Reservas por Mes");
            crearHojaReservasMes(sheetReservasMes, reservasPorMes, headerStyle, dataStyle);

            workbook.write(baos);
            return baos.toByteArray();
        }
    }

    private void agregarEstadisticasGenerales(Document document, ReporteEstadisticasDTO estadisticas) throws DocumentException {
        Paragraph subtitle = new Paragraph("ESTADÍSTICAS GENERALES", SUBTITLE_FONT);
        subtitle.setSpacingAfter(10);
        document.add(subtitle);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        agregarFilaTabla(table, "Total Estudiantes", estadisticas.getTotalEstudiantes().toString());
        agregarFilaTabla(table, "Total Docentes", estadisticas.getTotalDocentes().toString());
        agregarFilaTabla(table, "Reservas Activas", estadisticas.getReservasActivas().toString());
        agregarFilaTabla(table, "Tasa de Uso", estadisticas.getTasaUso() + "%");
        agregarFilaTabla(table, "Reservas Este Mes", estadisticas.getReservasEsteMes().toString());
        agregarFilaTabla(table, "Variación", estadisticas.getVariacionReservas());

        document.add(table);
        document.add(new Paragraph(" "));
    }

    private void agregarUsoEspacios(Document document, List<UsoEspacioDTO> usoEspacios) throws DocumentException {
        Paragraph subtitle = new Paragraph("USO DE ESPACIOS", SUBTITLE_FONT);
        subtitle.setSpacingAfter(10);
        document.add(subtitle);

        if (usoEspacios.isEmpty()) {
            document.add(new Paragraph("No hay datos de uso de espacios disponibles.", NORMAL_FONT));
            return;
        }

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);

        // Encabezados
        agregarCeldaHeader(table, "Espacio");
        agregarCeldaHeader(table, "Tipo");
        agregarCeldaHeader(table, "Total Reservas");
        agregarCeldaHeader(table, "Porcentaje Uso");

        // Datos
        for (UsoEspacioDTO espacio : usoEspacios) {
            table.addCell(new PdfPCell(new Phrase(espacio.getNombreEspacio(), NORMAL_FONT)));
            table.addCell(new PdfPCell(new Phrase(espacio.getTipoEspacio(), NORMAL_FONT)));
            table.addCell(new PdfPCell(new Phrase(espacio.getTotalReservas().toString(), NORMAL_FONT)));
            table.addCell(new PdfPCell(new Phrase(espacio.getPorcentajeUso() + "%", NORMAL_FONT)));
        }

        document.add(table);
        document.add(new Paragraph(" "));
    }

    private void agregarReservasPorMes(Document document, List<ReservasMesDTO> reservasPorMes) throws DocumentException {
        Paragraph subtitle = new Paragraph("RESERVAS POR MES", SUBTITLE_FONT);
        subtitle.setSpacingAfter(10);
        document.add(subtitle);

        if (reservasPorMes.isEmpty()) {
            document.add(new Paragraph("No hay datos de reservas por mes disponibles.", NORMAL_FONT));
            return;
        }

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);

        // Encabezados
        agregarCeldaHeader(table, "Mes");
        agregarCeldaHeader(table, "Año");
        agregarCeldaHeader(table, "Total Reservas");

        // Datos
        for (ReservasMesDTO mes : reservasPorMes) {
            table.addCell(new PdfPCell(new Phrase(mes.getMes(), NORMAL_FONT)));
            table.addCell(new PdfPCell(new Phrase(mes.getAnio().toString(), NORMAL_FONT)));
            table.addCell(new PdfPCell(new Phrase(mes.getTotalReservas().toString(), NORMAL_FONT)));
        }

        document.add(table);
    }

    private void agregarFilaTabla(PdfPTable table, String titulo, String valor) {
        table.addCell(new PdfPCell(new Phrase(titulo, NORMAL_FONT)));
        table.addCell(new PdfPCell(new Phrase(valor, NORMAL_FONT)));
    }

    private void agregarCeldaHeader(PdfPTable table, String texto) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, NORMAL_FONT));
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(cell);
    }

    // Métodos para Excel - Aquí usamos Apache POI normalmente
    private CellStyle crearEstiloHeader(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        org.apache.poi.ss.usermodel.Font font = workbook.createFont(); // POI Font
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle crearEstiloDatos(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private void crearHojaEstadisticas(Sheet sheet, ReporteEstadisticasDTO estadisticas, CellStyle headerStyle, CellStyle dataStyle) {
        String[] headers = {"Métrica", "Valor"};
        Row headerRow = sheet.createRow(0);
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        Object[][] data = {
            {"Total Estudiantes", estadisticas.getTotalEstudiantes()},
            {"Total Docentes", estadisticas.getTotalDocentes()},
            {"Reservas Activas", estadisticas.getReservasActivas()},
            {"Tasa de Uso", estadisticas.getTasaUso() + "%"},
            {"Reservas Este Mes", estadisticas.getReservasEsteMes()},
            {"Variación", estadisticas.getVariacionReservas()}
        };

        int rowNum = 1;
        for (Object[] rowData : data) {
            Row row = sheet.createRow(rowNum++);
            for (int i = 0; i < rowData.length; i++) {
                Cell cell = row.createCell(i);
                if (rowData[i] instanceof Number) {
                    cell.setCellValue(((Number) rowData[i]).doubleValue());
                } else {
                    cell.setCellValue(rowData[i].toString());
                }
                cell.setCellStyle(dataStyle);
            }
        }

        // Autoajustar columnas
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void crearHojaUsoEspacios(Sheet sheet, List<UsoEspacioDTO> usoEspacios, CellStyle headerStyle, CellStyle dataStyle) {
        String[] headers = {"Espacio", "Código", "Tipo", "Total Reservas", "Porcentaje Uso"};
        Row headerRow = sheet.createRow(0);
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (UsoEspacioDTO espacio : usoEspacios) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(espacio.getNombreEspacio());
            row.createCell(1).setCellValue(espacio.getCodigoEspacio());
            row.createCell(2).setCellValue(espacio.getTipoEspacio());
            row.createCell(3).setCellValue(espacio.getTotalReservas());
            row.createCell(4).setCellValue(espacio.getPorcentajeUso().doubleValue());
            
            for (int i = 0; i < headers.length; i++) {
                row.getCell(i).setCellStyle(dataStyle);
            }
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void crearHojaReservasMes(Sheet sheet, List<ReservasMesDTO> reservasPorMes, CellStyle headerStyle, CellStyle dataStyle) {
        String[] headers = {"Mes", "Año", "Total Reservas"};
        Row headerRow = sheet.createRow(0);
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (ReservasMesDTO mes : reservasPorMes) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(mes.getMes());
            row.createCell(1).setCellValue(mes.getAnio());
            row.createCell(2).setCellValue(mes.getTotalReservas());
            
            for (int i = 0; i < headers.length; i++) {
                row.getCell(i).setCellStyle(dataStyle);
            }
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}