package com.example.appmovdoggepediaversion2;

public class AdquirirColonias {
    private int IdCodigoPostal;
    private String Colonia;

    public AdquirirColonias(){}

    public int getIdCodigoPostal() {
        return IdCodigoPostal;
    }

    public void setIdCodigoPostal(int idCodigoPostal) {
        IdCodigoPostal = idCodigoPostal;
    }

    public String getColonia() {
        return Colonia;
    }

    public void setColonia(String colonia) {
        Colonia = colonia;
    }

    @Override
    public String toString(){
        return Colonia;
    }
}
