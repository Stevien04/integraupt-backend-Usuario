package com.integraupt.controlador;

import com.integraupt.dto.clsDTOLoginRequest;
import com.integraupt.dto.clsDTOLoginResponse;
import com.integraupt.servicio.clsServicioAuth;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST que expone los endpoints relacionados con autenticación.
 */
@RestController
@RequestMapping("/api/auth")
public class clsControladorAuth {

    private final clsServicioAuth servicioAuth;

    public clsControladorAuth(clsServicioAuth servicioAuth) {
        this.servicioAuth = servicioAuth;
    }

    @PostMapping("/login")
    public ResponseEntity<clsDTOLoginResponse> login(@Valid @RequestBody clsDTOLoginRequest request) {
        clsDTOLoginResponse response = servicioAuth.autenticarUsuario(request);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;
        return ResponseEntity.status(status).body(response);
    }
}