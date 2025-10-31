package com.integraupt.dto;

/**
 * DTO de respuesta para operaciones de gestión de usuarios.
 */
public class clsDTOUsuarioResponse {

    private boolean success;
    private String message;
    private UsuarioDTO usuario;

    public clsDTOUsuarioResponse() {
    }

    public clsDTOUsuarioResponse(boolean success, String message, UsuarioDTO usuario) {
        this.success = success;
        this.message = message;
        this.usuario = usuario;
    }

    public static clsDTOUsuarioResponse success(String message, UsuarioDTO usuario) {
        return new clsDTOUsuarioResponse(true, message, usuario);
    }

    public static clsDTOUsuarioResponse error(String message) {
        return new clsDTOUsuarioResponse(false, message, null);
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

    public UsuarioDTO getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioDTO usuario) {
        this.usuario = usuario;
    }

    /**
     * DTO interno que representa los datos expuestos de un usuario.
     */
    public static class UsuarioDTO {
        private Integer id;
        private String codigo;
        private String nombres;
        private String apellidos;
        private String email;
        private String tipoDocumento;
        private String numeroDocumento;
        private String celular;
        private Integer facultadId;
        private String facultadNombre;
        private Integer escuelaId;
        private String escuelaNombre;
        private String rol;
        private String genero;
        private Integer estado;
        private String fechaCreacion;
        private String password;

        public UsuarioDTO() {
        }

        public UsuarioDTO(Integer id, String codigo, String nombres, String apellidos, String email,
                          String tipoDocumento, String numeroDocumento, String celular,
                          Integer facultadId, String facultadNombre,
                          Integer escuelaId, String escuelaNombre,
                          String rol, String genero, Integer estado,
                          String fechaCreacion, String password) {
            this.id = id;
            this.codigo = codigo;
            this.nombres = nombres;
            this.apellidos = apellidos;
            this.email = email;
            this.tipoDocumento = tipoDocumento;
            this.numeroDocumento = numeroDocumento;
            this.celular = celular;
            this.facultadId = facultadId;
            this.facultadNombre = facultadNombre;
            this.escuelaId = escuelaId;
            this.escuelaNombre = escuelaNombre;
            this.rol = rol;
            this.genero = genero;
            this.estado = estado;
            this.fechaCreacion = fechaCreacion;
            this.password = password;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
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

        public String getTipoDocumento() {
            return tipoDocumento;
        }

        public void setTipoDocumento(String tipoDocumento) {
            this.tipoDocumento = tipoDocumento;
        }

        public String getNumeroDocumento() {
            return numeroDocumento;
        }

        public void setNumeroDocumento(String numeroDocumento) {
            this.numeroDocumento = numeroDocumento;
        }

        public String getCelular() {
            return celular;
        }

        public void setCelular(String celular) {
            this.celular = celular;
        }

        public Integer getFacultadId() {
            return facultadId;
        }

        public void setFacultadId(Integer facultadId) {
            this.facultadId = facultadId;
        }

        public String getFacultadNombre() {
            return facultadNombre;
        }

        public void setFacultadNombre(String facultadNombre) {
            this.facultadNombre = facultadNombre;
        }

        public Integer getEscuelaId() {
            return escuelaId;
        }

        public void setEscuelaId(Integer escuelaId) {
            this.escuelaId = escuelaId;
        }

        public String getEscuelaNombre() {
            return escuelaNombre;
        }

        public void setEscuelaNombre(String escuelaNombre) {
            this.escuelaNombre = escuelaNombre;
        }

        public String getRol() {
            return rol;
        }

        public void setRol(String rol) {
            this.rol = rol;
        }

        public String getGenero() {
            return genero;
        }

        public void setGenero(String genero) {
            this.genero = genero;
        }

        public Integer getEstado() {
            return estado;
        }

        public void setEstado(Integer estado) {
            this.estado = estado;
        }

        public String getFechaCreacion() {
            return fechaCreacion;
        }

        public void setFechaCreacion(String fechaCreacion) {
            this.fechaCreacion = fechaCreacion;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}