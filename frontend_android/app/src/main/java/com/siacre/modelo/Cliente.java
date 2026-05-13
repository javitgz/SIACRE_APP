package com.siacre.modelo;

public class Cliente {
    private int id;
    private String tipo_documento;
    private int documento;
    private String nombres;
    private String apellidos;
    private String correo;
    private String telefono;
    private int id_usuario;

    public Cliente() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTipoDocumento() { return tipo_documento; }
    public void setTipoDocumento(String tipo_documento) { this.tipo_documento = tipo_documento; }

    public int getDocumento() { return documento; }
    public void setDocumento(int documento) { this.documento = documento; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public int getIdUsuario() { return id_usuario; }
    public void setIdUsuario(int id_usuario) { this.id_usuario = id_usuario; }
}