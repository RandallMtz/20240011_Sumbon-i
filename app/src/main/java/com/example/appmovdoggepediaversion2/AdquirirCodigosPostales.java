package com.example.appmovdoggepediaversion2;

public class AdquirirCodigosPostales {
    private int IdCodigoPostal;
    private String CodigoPostal;

    public AdquirirCodigosPostales(){}

    public int getIdCodigoPostal() {
        return IdCodigoPostal;
    }

    public void setIdCodigoPostal(int idCodigoPostal) {
        IdCodigoPostal = idCodigoPostal;
    }

    public String getCodigoPostal() {
        return CodigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        CodigoPostal = codigoPostal;
    }

    @Override
    public String toString(){
        return CodigoPostal;
    }
}
