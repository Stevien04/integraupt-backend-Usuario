package com.integraupt.controlador;

import com.integraupt.dto.clsDTOBloqueHorario;
import com.integraupt.servicio.clsServicioCatalogos;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/bloques-horarios")
public class clsControladorBloqueHorario {

    private final clsServicioCatalogos servicioCatalogos;

    public clsControladorBloqueHorario(clsServicioCatalogos servicioCatalogos) {
        this.servicioCatalogos = servicioCatalogos;
    }

    @GetMapping
    public ResponseEntity<List<clsDTOBloqueHorario>> obtenerBloquesHorarios() {
        List<clsDTOBloqueHorario> bloques = servicioCatalogos.obtenerBloquesHorariosOrdenados();
        return ResponseEntity.ok(bloques);
    }
}