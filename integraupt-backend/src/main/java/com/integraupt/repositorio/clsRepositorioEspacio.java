package com.integraupt.repositorio;

import com.integraupt.entidad.clsEntidadEspacio;
import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface clsRepositorioEspacio extends JpaRepository<clsEntidadEspacio, Integer> {
    
    // Buscar por código
    Optional<clsEntidadEspacio> findByCodigo(String codigo);
    
    // Buscar por tipo de espacio
    List<clsEntidadEspacio> findByTipo(clsEntidadEspacio.TipoEspacio tipo);
    
    // Buscar por facultad
    List<clsEntidadEspacio> findByFacultadId(Integer facultadId);
    
    // Buscar por escuela
    List<clsEntidadEspacio> findByEscuelaId(Integer escuelaId);
    
    // Buscar por estado
    List<clsEntidadEspacio> findByEstado(Integer estado);
    
    // Buscar espacios disponibles (estado = 1)
    List<clsEntidadEspacio> findByEstadoOrderByNombreAsc(Integer estado);
    
    // Verificar si existe un código (para evitar duplicados)
    boolean existsByCodigo(String codigo);
    
    // Buscar por código ignorando mayúsculas/minúsculas
    Optional<clsEntidadEspacio> findByCodigoIgnoreCase(String codigo);

    // Buscar por escuela y estado
    List<clsEntidadEspacio> findByEscuelaIdAndEstado(Integer escuelaId, Integer estado);

}