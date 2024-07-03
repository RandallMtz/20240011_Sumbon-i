package com.example.appmovdoggepediaversion2;

public class AdquirirMunicipios {
    private int IdCodigoPostal;
    private String Municipio;

    public AdquirirMunicipios(){}

    public int getIdCodigoPostal() {
        return IdCodigoPostal;
    }

    public void setIdCodigoPostal(int idCodigoPostal) {
        IdCodigoPostal = idCodigoPostal;
    }

    public String getMunicipio() {
        return Municipio;
    }

    public void setMunicipio(String municipio) {
        Municipio = municipio;
    }

    @Override
    public String toString(){
        return Municipio;
    }
}
