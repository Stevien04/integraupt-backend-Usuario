package com.integraupt.servicio;

import com.integraupt.dto.clsDTOEspacioRequest;
import com.integraupt.dto.clsDTOEspacioResponse;
import com.integraupt.entidad.clsEntidadEspacio;
import com.integraupt.repositorio.clsRepositorioEspacio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class clsServicioEspacio {

    private final clsRepositorioEspacio repositorioEspacio;
    private final clsServicioCatalogos servicioCatalogos;

    public clsServicioEspacio(clsRepositorioEspacio repositorioEspacio, 
                             clsServicioCatalogos servicioCatalogos) {
        this.repositorioEspacio = repositorioEspacio;
        this.servicioCatalogos = servicioCatalogos;
    }

    /**
     * Obtener todos los espacios
     */
    @Transactional(readOnly = true)
    public List<clsDTOEspacioResponse.EspacioDTO> obtenerTodosLosEspacios(Integer escuelaId) {
        List<clsEntidadEspacio> espacios =
                escuelaId != null
                        ? repositorioEspacio.findByEscuelaId(escuelaId)
                        : repositorioEspacio.findAll();
        return espacios.stream()
                .sorted(Comparator.comparing(
                        clsEntidadEspacio::getNombre,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .map(this::convertirEntidadADTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener espacio por ID
     */
    @Transactional(readOnly = true)
    public clsDTOEspacioResponse obtenerEspacioPorId(Integer id) {
        Optional<clsEntidadEspacio> espacioOpt = repositorioEspacio.findById(id);
        
        if (espacioOpt.isEmpty()) {
            return clsDTOEspacioResponse.error("Espacio no encontrado");
        }
        
        clsDTOEspacioResponse.EspacioDTO espacioDTO = convertirEntidadADTO(espacioOpt.get());
        return clsDTOEspacioResponse.success("Espacio encontrado", espacioDTO);
    }

    /**
     * Crear nuevo espacio
     */
    @Transactional
    public clsDTOEspacioResponse crearEspacio(clsDTOEspacioRequest request) {
        // Validar que el código no exista
        if (repositorioEspacio.existsByCodigo(request.getCodigo())) {
            return clsDTOEspacioResponse.error("Ya existe un espacio con el código: " + request.getCodigo());
        }

        // Crear nueva entidad
        clsEntidadEspacio nuevoEspacio = new clsEntidadEspacio();
        nuevoEspacio.setCodigo(request.getCodigo());
        nuevoEspacio.setNombre(request.getNombre());
        nuevoEspacio.setUbicacion(request.getUbicacion());
        nuevoEspacio.setTipo(request.getTipo());
        nuevoEspacio.setCapacidad(request.getCapacidad());
        nuevoEspacio.setEquipamiento(request.getEquipamiento());
        nuevoEspacio.setFacultadId(request.getFacultadId());
        nuevoEspacio.setEscuelaId(request.getEscuelaId());
        nuevoEspacio.setEstado(request.getEstado());

        // Guardar en BD
        clsEntidadEspacio espacioGuardado = repositorioEspacio.save(nuevoEspacio);
        
        // Convertir a DTO de respuesta
        clsDTOEspacioResponse.EspacioDTO espacioDTO = convertirEntidadADTO(espacioGuardado);
        return clsDTOEspacioResponse.success("Espacio creado exitosamente", espacioDTO);
    }

    /**
     * Actualizar espacio existente
     */
    @Transactional
    public clsDTOEspacioResponse actualizarEspacio(Integer id, clsDTOEspacioRequest request) {
        // Verificar que el espacio existe
        Optional<clsEntidadEspacio> espacioOpt = repositorioEspacio.findById(id);
        if (espacioOpt.isEmpty()) {
            return clsDTOEspacioResponse.error("Espacio no encontrado");
        }

        clsEntidadEspacio espacio = espacioOpt.get();

        // Validar que el código no esté duplicado (si cambió)
        if (!espacio.getCodigo().equals(request.getCodigo()) && 
            repositorioEspacio.existsByCodigo(request.getCodigo())) {
            return clsDTOEspacioResponse.error("Ya existe otro espacio con el código: " + request.getCodigo());
        }

        // Actualizar campos
        espacio.setCodigo(request.getCodigo());
        espacio.setNombre(request.getNombre());
        espacio.setUbicacion(request.getUbicacion());
        espacio.setTipo(request.getTipo());
        espacio.setCapacidad(request.getCapacidad());
        espacio.setEquipamiento(request.getEquipamiento());
        espacio.setFacultadId(request.getFacultadId());
        espacio.setEscuelaId(request.getEscuelaId());
        espacio.setEstado(request.getEstado());

        // Guardar cambios
        clsEntidadEspacio espacioActualizado = repositorioEspacio.save(espacio);
        
        // Convertir a DTO de respuesta
        clsDTOEspacioResponse.EspacioDTO espacioDTO = convertirEntidadADTO(espacioActualizado);
        return clsDTOEspacioResponse.success("Espacio actualizado exitosamente", espacioDTO);
    }

    /**
     * Eliminar espacio
     */
    @Transactional
    public clsDTOEspacioResponse eliminarEspacio(Integer id) {
        // Verificar que el espacio existe
        if (!repositorioEspacio.existsById(id)) {
            return clsDTOEspacioResponse.error("Espacio no encontrado");
        }

        // Eliminar de BD
        repositorioEspacio.deleteById(id);
        return clsDTOEspacioResponse.success("Espacio eliminado exitosamente", null);
    }

    /**
     * Convertir entidad a DTO para respuesta
     */
    private clsDTOEspacioResponse.EspacioDTO convertirEntidadADTO(clsEntidadEspacio entidad) {
        // Obtener nombres de facultad y escuela
        String nombreFacultad = servicioCatalogos.obtenerNombreFacultad(entidad.getFacultadId());
        String nombreEscuela = servicioCatalogos.obtenerNombreEscuela(entidad.getEscuelaId());
        
        // Convertir estado numérico a texto
        Integer estado = entidad.getEstado();
        
        // Convertir tipo enum a string
        String tipoTexto = entidad.getTipo() == clsEntidadEspacio.TipoEspacio.Laboratorio ? "Laboratorio" : "Salon";

        return new clsDTOEspacioResponse.EspacioDTO(
            entidad.getId(),
            entidad.getCodigo(),
            entidad.getNombre(),
            entidad.getUbicacion(),
            tipoTexto,
            entidad.getCapacidad(),
            entidad.getEquipamiento(),
            nombreFacultad,
            nombreEscuela,
            estado 
        );
    }

    /**
     * Obtener espacios por tipo
     */
    @Transactional(readOnly = true)
    public List<clsDTOEspacioResponse.EspacioDTO> obtenerEspaciosPorTipo(clsEntidadEspacio.TipoEspacio tipo) {
        List<clsEntidadEspacio> espacios = repositorioEspacio.findByTipo(tipo);
        return espacios.stream()
                .map(this::convertirEntidadADTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener espacios disponibles
     */
    @Transactional(readOnly = true)
    public List<clsDTOEspacioResponse.EspacioDTO> obtenerEspaciosDisponibles(Integer escuelaId) {
        List<clsEntidadEspacio> espacios =
                escuelaId != null
                        ? repositorioEspacio.findByEscuelaIdAndEstado(escuelaId, 1)
                        : repositorioEspacio.findByEstadoOrderByNombreAsc(1);
        return espacios.stream()
                .sorted(Comparator.comparing(
                        clsEntidadEspacio::getNombre,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .map(this::convertirEntidadADTO)
                .collect(Collectors.toList());
    }
}