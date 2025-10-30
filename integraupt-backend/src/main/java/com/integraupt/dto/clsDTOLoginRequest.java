package com.integraupt.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Representa los datos necesarios para ejecutar el proceso de autenticación.
 */
public class clsDTOLoginRequest {

    @NotBlank(message = "El código o correo es obligatorio")
    private String codigoOEmail;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    private String tipoLogin;

    public clsDTOLoginRequest() {
        // Constructor por defecto
    }

    public String getCodigoOEmail() {
        return codigoOEmail;
    }

    public void setCodigoOEmail(String codigoOEmail) {
        this.codigoOEmail = codigoOEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTipoLogin() {
        return tipoLogin;
    }

    public void setTipoLogin(String tipoLogin) {
        this.tipoLogin = tipoLogin;
    }
}