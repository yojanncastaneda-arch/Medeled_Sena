package com.sena.modelo;

public class Usuario {

    private int idUsuario;
    private String usuario;
    // Se guarda el hash BCrypt de la contraseña, nunca la contraseña original
    private String contrasenaHash;

    // CONSTRUCTOR (Para crear el usuario)
    public Usuario(int idUsuario, String usuario, String contrasenaHash) {
        this.idUsuario = idUsuario;
        this.usuario = usuario;
        this.contrasenaHash = contrasenaHash;
    }

    // GETTERS Y SETTERS (Para leer y modificar los datos)
    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasenaHash() {
        return contrasenaHash;
    }

    public void setContrasenaHash(String contrasenaHash) {
        this.contrasenaHash = contrasenaHash;
    }
}
