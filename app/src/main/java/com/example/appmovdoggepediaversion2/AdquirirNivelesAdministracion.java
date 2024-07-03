package com.example.appmovdoggepediaversion2;

public class AdquirirNivelesAdministracion {
    private int IdNivel;
    private String Nivel;

    public AdquirirNivelesAdministracion(){}

    public int getIdNivel() {
        return IdNivel;
    }

    public void setIdNivel(int idNivel) {
        IdNivel = idNivel;
    }

    public String getNivel() {
        return Nivel;
    }

    public void setNivel(String nivel) {
        Nivel = nivel;
    }

    @Override
    public String toString(){
        return Nivel;
    }
}
