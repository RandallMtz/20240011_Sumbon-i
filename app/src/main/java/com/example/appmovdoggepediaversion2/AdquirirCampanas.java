package com.example.appmovdoggepediaversion2;

public class AdquirirCampanas {
    private String Calle, CodigoCampaña, Colonia, Descripcion, FechaRealizacion, Finalidad, FotoPromocional, Municipio, NumExt, NumInt, Telefono1, Telefono2;

    public AdquirirCampanas() {}

    public AdquirirCampanas(String calle, String descripcion, String fechaRealizacion, String finalidad, String fotoPromocional, String municipio, String telefono1, String telefono2) {
        Calle = calle;
        Descripcion = descripcion;
        FechaRealizacion = fechaRealizacion;
        Finalidad = finalidad;
        FotoPromocional = fotoPromocional;
        Municipio = municipio;
        Telefono1 = telefono1;
        Telefono2 = telefono2;
    }

    public String getCalle() {
        return Calle;
    }

    public void setCalle(String calle) {
        Calle = calle;
    }

    public String getCodigoCampaña() {
        return CodigoCampaña;
    }

    public void setCodigoCampaña(String codigoCampaña) {
        CodigoCampaña = codigoCampaña;
    }

    public String getColonia() {
        return Colonia;
    }

    public void setColonia(String colonia) {
        Colonia = colonia;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public String getFechaRealizacion() {
        return FechaRealizacion;
    }

    public void setFechaRealizacion(String fechaRealizacion) {
        FechaRealizacion = fechaRealizacion;
    }

    public String getFinalidad() {
        return Finalidad;
    }

    public void setFinalidad(String finalidad) {
        Finalidad = finalidad;
    }

    public String getFotoPromocional() {
        return FotoPromocional;
    }

    public void setFotoPromocional(String fotoPromocional) {
        FotoPromocional = fotoPromocional;
    }

    public String getMunicipio() {
        return Municipio;
    }

    public void setMunicipio(String municipio) {
        Municipio = municipio;
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

    public String getTelefono1() {
        return Telefono1;
    }

    public void setTelefono1(String telefono1) {
        Telefono1 = telefono1;
    }

    public String getTelefono2() {
        return Telefono2;
    }

    public void setTelefono2(String telefono2) {
        Telefono2 = telefono2;
    }

    @Override
    public String toString(){
        return CodigoCampaña;
    }
}
