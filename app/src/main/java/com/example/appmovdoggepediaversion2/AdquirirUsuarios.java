package com.example.appmovdoggepediaversion2;

public class AdquirirUsuarios {
    private String ApeMat, ApePat, Correo, Nombre, Telefono, Usuario;

    public AdquirirUsuarios() {}

    public AdquirirUsuarios(String apeMat, String apePat, String correo, String nombre, String telefono, String usuario) {
        ApeMat = apeMat;
        ApePat = apePat;
        Correo = correo;
        Nombre = nombre;
        Telefono = telefono;
        Usuario = usuario;
    }

    public String getApeMat() {
        return ApeMat;
    }

    public void setApeMat(String apeMat) {
        ApeMat = apeMat;
    }

    public String getApePat() {
        return ApePat;
    }

    public void setApePat(String apePat) {
        ApePat = apePat;
    }

    public String getCorreo() {
        return Correo;
    }

    public void setCorreo(String correo) {
        Correo = correo;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getTelefono() {
        return Telefono;
    }

    public void setTelefono(String telefono) {
        Telefono = telefono;
    }

    public String getUsuario() {
        return Usuario;
    }

    public void setUsuario(String usuario) {
        Usuario = usuario;
    }

    @Override
    public String toString(){
        return Usuario;
    }
}
