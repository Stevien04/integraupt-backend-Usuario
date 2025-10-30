package com.integraupt.dto;

/**
 * Respuesta genérica para el proceso de inicio de sesión.
 */
public class clsDTOLoginResponse {

    private boolean success;
    private String message;
    private PerfilDTO perfil;
    private String token;

    public clsDTOLoginResponse() {
        // Constructor por defecto
    }

    public clsDTOLoginResponse(boolean success, String message, PerfilDTO perfil, String token) {
        this.success = success;
        this.message = message;
        this.perfil = perfil;
        this.token = token;
    }

    public static clsDTOLoginResponse success(String message, PerfilDTO perfil, String token) {
        return new clsDTOLoginResponse(true, message, perfil, token);
    }

    public static clsDTOLoginResponse error(String message) {
        return new clsDTOLoginResponse(false, message, null, null);
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

    public PerfilDTO getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfilDTO perfil) {
        this.perfil = perfil;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    /**
     * DTO con la información relevante del perfil del usuario que inició sesión.
     */
    public static class PerfilDTO {
        private String id;
        private String codigo;
        private String nombres;
        private String apellidos;
        private String email;
        private String rol;
        private String tipoLogin;
        private String avatarUrl;
        private String estado;
        private String celular;
        private String escuela;
        private String facultad;
        private String genero;
        private String numeroDocumento;

        public PerfilDTO() {
            // Constructor por defecto
        }

        public PerfilDTO(String id, String codigo, String nombres, String apellidos, String email,
                         String rol, String tipoLogin, String avatarUrl, String estado, String celular,
                         String escuela, String facultad, String genero, String numeroDocumento) {
            this.id = id;
            this.codigo = codigo;
            this.nombres = nombres;
            this.apellidos = apellidos;
            this.email = email;
            this.rol = rol;
            this.tipoLogin = tipoLogin;
            this.avatarUrl = avatarUrl;
            this.estado = estado;
            this.celular = celular;
            this.escuela = escuela;
            this.facultad = facultad;
            this.genero = genero;
            this.numeroDocumento = numeroDocumento;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCodigo() {
            return codigo;
        }

        public void setCodigo(String codigo) {
            this.codigo = codigo;
        }

        public String getNombres() {
            return nombres;
        }

        public void setNombres(String nombres) {
            this.nombres = nombres;
        }

        public String getApellidos() {
            return apellidos;
        }

        public void setApellidos(String apellidos) {
            this.apellidos = apellidos;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getRol() {
            return rol;
        }

        public void setRol(String rol) {
            this.rol = rol;
        }

        public String getTipoLogin() {
            return tipoLogin;
        }

        public void setTipoLogin(String tipoLogin) {
            this.tipoLogin = tipoLogin;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public String getEstado() {
            return estado;
        }

        public void setEstado(String estado) {
            this.estado = estado;
        }

        public String getCelular() {
            return celular;
        }

        public void setCelular(String celular) {
            this.celular = celular;
        }

        public String getEscuela() {
            return escuela;
        }

        public void setEscuela(String escuela) {
            this.escuela = escuela;
        }

        public String getFacultad() {
            return facultad;
        }

        public void setFacultad(String facultad) {
            this.facultad = facultad;
        }

        public String getGenero() {
            return genero;
        }

        public void setGenero(String genero) {
            this.genero = genero;
        }

        public String getNumeroDocumento() {
            return numeroDocumento;
        }

        public void setNumeroDocumento(String numeroDocumento) {
            this.numeroDocumento = numeroDocumento;
        }
    }
}