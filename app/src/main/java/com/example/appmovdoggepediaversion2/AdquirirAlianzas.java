package com.example.appmovdoggepediaversion2;

public class AdquirirAlianzas {
    private String Calle, Colonia, Empresa, Municipio, Telefono1, Telefono2;

    public AdquirirAlianzas() {}

    public AdquirirAlianzas(String calle, String colonia, String empresa, String municipio, String telefono1, String telefono2) {
        Calle = calle;
        Colonia = colonia;
        Empresa = empresa;
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

    public String getColonia() {
        return Colonia;
    }

    public void setColonia(String colonia) {
        Colonia = colonia;
    }

    public String getEmpresa() {
        return Empresa;
    }

    public void setEmpresa(String empresa) {
        Empresa = empresa;
    }

    public String getMunicipio() {
        return Municipio;
    }

    public void setMunicipio(String municipio) {
        Municipio = municipio;
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
        return Empresa;
    }
}
