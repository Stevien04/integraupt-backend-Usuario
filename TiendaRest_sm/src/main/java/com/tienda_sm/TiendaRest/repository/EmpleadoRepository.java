package com.tienda_sm.TiendaRest.repository;

import com.tienda_sm.TiendaRest.model.EmpleadoModel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EmpleadoRepository extends JpaRepository<EmpleadoModel,Long> {
    @Query("select e from EmpleadoModel e where e.estado = 1")
    List<EmpleadoModel> findAllCustom();
}
