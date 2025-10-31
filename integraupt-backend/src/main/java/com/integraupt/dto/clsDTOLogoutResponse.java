package com.integraupt.dto;

/**
 * DTO de respuesta para el cierre de sesión.
 */
public class clsDTOLogoutResponse {

    private boolean success;
    private String message;

    public clsDTOLogoutResponse() {
    }

    public clsDTOLogoutResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static clsDTOLogoutResponse success(String message) {
        return new clsDTOLogoutResponse(true, message);
    }

    public static clsDTOLogoutResponse error(String message) {
        return new clsDTOLogoutResponse(false, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}