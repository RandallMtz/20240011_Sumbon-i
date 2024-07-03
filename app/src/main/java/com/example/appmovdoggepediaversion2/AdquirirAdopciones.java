package com.example.appmovdoggepediaversion2;

public class AdquirirAdopciones {
    private int IdAdopcion, Telefono1, Telefono2;
    private String ApeMat, ApePat, Calle, CodigoAdop, Correo, Fecha, Nombre, NumExt, NumInt, Situacion, Usuario;

    public AdquirirAdopciones() {
    }

    public AdquirirAdopciones(int idAdopcion, String codigoAdop) {
        IdAdopcion = idAdopcion;
        CodigoAdop = codigoAdop;
    }

    public AdquirirAdopciones(int idAdopcion, int telefono1, int telefono2, String apeMat, String apePat, String calle, String codigoAdop, String correo, String fecha, String nombre, String numExt, String numInt, String situacion, String usuario) {
        IdAdopcion = idAdopcion;
        Telefono1 = telefono1;
        Telefono2 = telefono2;
        ApeMat = apeMat;
        ApePat = apePat;
        Calle = calle;
        CodigoAdop = codigoAdop;
        Correo = correo;
        Fecha = fecha;
        Nombre = nombre;
        NumExt = numExt;
        NumInt = numInt;
        Situacion = situacion;
        Usuario = usuario;
    }

    public int getIdAdopcion() {
        return IdAdopcion;
    }

    public void setIdAdopcion(int idAdopcion) {
        IdAdopcion = idAdopcion;
    }

    public int getTelefono1() {
        return Telefono1;
    }

    public void setTelefono1(int telefono1) {
        Telefono1 = telefono1;
    }

    public int getTelefono2() {
        return Telefono2;
    }

    public void setTelefono2(int telefono2) {
        Telefono2 = telefono2;
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

    public String getCalle() {
        return Calle;
    }

    public void setCalle(String calle) {
        Calle = calle;
    }

    public String getCodigoAdop() {
        return CodigoAdop;
    }

    public void setCodigoAdop(String codigoAdop) {
        CodigoAdop = codigoAdop;
    }

    public String getCorreo() {
        return Correo;
    }

    public void setCorreo(String correo) {
        Correo = correo;
    }

    public String getFecha() {
        return Fecha;
    }

    public void setFecha(String fecha) {
        Fecha = fecha;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getNumExt() {
        return NumExt;
    }

    public void setNumExt(String numExt) {
        NumExt = numExt;
    }

    public String getNumInt() {
        return NumInt;
    }

    public void setNumInt(String numInt) {
        NumInt = numInt;
    }

    public String getSituacion() {
        return Situacion;
    }

    public void setSituacion(String situacion) {
        Situacion = situacion;
    }

    public String getUsuario() {
        return Usuario;
    }

    public void setUsuario(String usuario) {
        Usuario = usuario;
    }

    @Override
    public String toString(){
        return CodigoAdop;
    }
}
