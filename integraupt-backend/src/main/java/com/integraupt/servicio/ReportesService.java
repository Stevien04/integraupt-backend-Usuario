package com.integraupt.servicio;

import com.integraupt.dto.ReporteEstadisticasDTO;
import com.integraupt.dto.ReservasMesDTO;
import com.integraupt.dto.UsoEspacioDTO;
import com.integraupt.entidad.clsEntidadUsuario;
import com.integraupt.entidad.clsEntidadReserva;
import com.integraupt.entidad.clsEntidadEspacio;
import com.integraupt.entidad.clsEntidadHorario;
import com.integraupt.repositorio.clsRepositorioUsuario;
import com.integraupt.repositorio.clsRepositorioReserva;
import com.integraupt.repositorio.clsRepositorioEspacio;
import com.integraupt.repositorio.clsRepositorioHorario;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportesService {

    private final clsRepositorioUsuario usuarioRepository;
    private final clsRepositorioReserva reservaRepository;
    private final clsRepositorioEspacio espacioRepository;
    private final clsRepositorioHorario horarioRepository;

    public ReportesService(clsRepositorioUsuario usuarioRepository,
                          clsRepositorioReserva reservaRepository,
                          clsRepositorioEspacio espacioRepository,
                          clsRepositorioHorario horarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.reservaRepository = reservaRepository;
        this.espacioRepository = espacioRepository;
        this.horarioRepository = horarioRepository;
    }

    public ReporteEstadisticasDTO obtenerEstadisticasGenerales() {
        try {
            // Obtener todos los usuarios y filtrar por rol manualmente
            List<clsEntidadUsuario> todosUsuarios = usuarioRepository.findAll();
            Long totalEstudiantes = todosUsuarios.stream()
                .filter(usuario -> usuario.getRolId() != null && usuario.getRolId().equals(2))
                .count();
            
            Long totalDocentes = todosUsuarios.stream()
                .filter(usuario -> usuario.getRolId() != null && usuario.getRolId().equals(1))
                .count();
            
            // Obtener todas las reservas y filtrar manualmente
            List<clsEntidadReserva> todasReservas = reservaRepository.findAll();
            
            // Reservas activas (aprobadas con fecha hoy o futura)
            Long reservasActivas = todasReservas.stream()
                .filter(reserva -> "Aprobada".equals(reserva.getEstado()) && 
                                  reserva.getFechaReserva() != null &&
                                  !reserva.getFechaReserva().isBefore(LocalDate.now()))
                .count();
            
            // Tasa de uso
            Long totalReservasAprobadas = todasReservas.stream()
                .filter(reserva -> "Aprobada".equals(reserva.getEstado()))
                .count();
            
            List<clsEntidadHorario> todosHorarios = horarioRepository.findAll();
            Long totalHorariosOcupados = todosHorarios.stream()
                .filter(clsEntidadHorario::getOcupado)
                .count();
            
            BigDecimal tasaUso = totalHorariosOcupados > 0 ? 
                BigDecimal.valueOf(totalReservasAprobadas * 100.0 / totalHorariosOcupados).setScale(1, BigDecimal.ROUND_HALF_UP) :
                BigDecimal.ZERO;
            
            // Reservas este mes
            LocalDate inicioMes = YearMonth.now().atDay(1);
            LocalDate finMes = YearMonth.now().atEndOfMonth();
            Long reservasEsteMes = todasReservas.stream()
                .filter(reserva -> reserva.getFechaSolicitud() != null &&
                                  !reserva.getFechaSolicitud().isBefore(inicioMes.atStartOfDay()) &&
                                  !reserva.getFechaSolicitud().isAfter(finMes.atTime(23, 59, 59)))
                .count();
            
            // Reservas mes anterior
            YearMonth mesAnterior = YearMonth.now().minusMonths(1);
            LocalDate inicioMesAnterior = mesAnterior.atDay(1);
            LocalDate finMesAnterior = mesAnterior.atEndOfMonth();
            Long reservasMesAnterior = todasReservas.stream()
                .filter(reserva -> reserva.getFechaSolicitud() != null &&
                                  !reserva.getFechaSolicitud().isBefore(inicioMesAnterior.atStartOfDay()) &&
                                  !reserva.getFechaSolicitud().isAfter(finMesAnterior.atTime(23, 59, 59)))
                .count();
            
            return new ReporteEstadisticasDTO(totalEstudiantes, totalDocentes, reservasActivas, tasaUso, reservasEsteMes, reservasMesAnterior);
            
        } catch (Exception e) {
            // En caso de error, retornar valores por defecto
            return new ReporteEstadisticasDTO(0L, 0L, 0L, BigDecimal.ZERO, 0L, 0L);
        }
    }

    public List<UsoEspacioDTO> obtenerUsoEspacios() {
        try {
            List<clsEntidadEspacio> espacios = espacioRepository.findAll();
            List<clsEntidadReserva> todasReservas = reservaRepository.findAll();
            
            Long totalReservasAprobadas = todasReservas.stream()
                .filter(reserva -> "Aprobada".equals(reserva.getEstado()))
                .count();
            
            return espacios.stream().map(espacio -> {
                // Contar reservas aprobadas para este espacio manualmente
                Long reservasEspacio = todasReservas.stream()
                    .filter(reserva -> "Aprobada".equals(reserva.getEstado()) &&
                                      reserva.getEspacio() != null &&
                                      espacio.getId().equals(reserva.getEspacio().getId()))
                    .count();
                
                // Calcular porcentaje de uso
                BigDecimal porcentajeUso = totalReservasAprobadas > 0 ? 
                    BigDecimal.valueOf(reservasEspacio * 100.0 / totalReservasAprobadas).setScale(1, BigDecimal.ROUND_HALF_UP) :
                    BigDecimal.ZERO;
                
                // Convertir el tipo de espacio a String
                String tipoEspacioString = espacio.getTipo().toString();
                
                return new UsoEspacioDTO(
                    espacio.getNombre(),
                    espacio.getCodigo(),
                    tipoEspacioString,
                    reservasEspacio,
                    porcentajeUso
                );
            }).sorted((a, b) -> Long.compare(b.getTotalReservas(), a.getTotalReservas()))
              .collect(Collectors.toList());
            
        } catch (Exception e) {
            return List.of();
        }
    }

    public List<ReservasMesDTO> obtenerReservasPorMes() {
        try {
            List<clsEntidadReserva> todasReservas = reservaRepository.findAll();
            
            // Obtener los últimos 6 meses
            List<ReservasMesDTO> resultado = new java.util.ArrayList<>();
            
            for (int i = 5; i >= 0; i--) {
                YearMonth mes = YearMonth.now().minusMonths(i);
                LocalDate inicioMes = mes.atDay(1);
                LocalDate finMes = mes.atEndOfMonth();
                
                Long totalReservas = todasReservas.stream()
                    .filter(reserva -> "Aprobada".equals(reserva.getEstado()) &&
                                      reserva.getFechaSolicitud() != null &&
                                      !reserva.getFechaSolicitud().isBefore(inicioMes.atStartOfDay()) &&
                                      !reserva.getFechaSolicitud().isAfter(finMes.atTime(23, 59, 59)))
                    .count();
                
                resultado.add(new ReservasMesDTO(
                    obtenerNombreMes(mes.getMonthValue()),
                    mes.getYear(),
                    totalReservas
                ));
            }
            
            return resultado;
            
        } catch (Exception e) {
            return List.of();
        }
    }
    
    private String obtenerNombreMes(int mes) {
        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                         "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        return meses[mes - 1];
    }
}