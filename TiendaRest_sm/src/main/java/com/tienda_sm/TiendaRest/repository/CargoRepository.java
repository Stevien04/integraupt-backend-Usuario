package com.tienda_sm.TiendaRest.repository;

import com.tienda_sm.TiendaRest.model.CargoModel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CargoRepository extends JpaRepository<CargoModel,Long> {
    @Query("select c from CargoModel c where c.estado = 1")
    List<CargoModel> findAllCustom();
}
