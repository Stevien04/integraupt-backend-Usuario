package com.integraupt.excepcion;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Manejo centralizado de excepciones para exponer respuestas consistentes.
 */
@ControllerAdvice
public class ManejadorGlobalExcepciones {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> manejarRecursoNoEncontrado(RecursoNoEncontradoException excepcion) {
        Map<String, Object> cuerpo = crearCuerpoError(excepcion.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(cuerpo);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> manejarArgumentoIlegal(IllegalArgumentException excepcion) {
        Map<String, Object> cuerpo = crearCuerpoError(excepcion.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cuerpo);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, Object> manejarValidaciones(MethodArgumentNotValidException excepcion) {
        Map<String, Object> cuerpo = crearCuerpoError("Datos de entrada no válidos");
        excepcion.getBindingResult().getFieldErrors()
                .forEach(error -> cuerpo.put(error.getField(), error.getDefaultMessage()));
        return cuerpo;
    }

    private Map<String, Object> crearCuerpoError(String mensaje) {
        Map<String, Object> cuerpo = new HashMap<>();
        cuerpo.put("timestamp", LocalDateTime.now());
        cuerpo.put("mensaje", mensaje);
        return cuerpo;
    }
}