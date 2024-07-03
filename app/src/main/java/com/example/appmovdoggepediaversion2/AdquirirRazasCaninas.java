package com.example.appmovdoggepediaversion2;

public class AdquirirRazasCaninas {
    private int IdRazaCanina;
    private String Raza;

    public AdquirirRazasCaninas(){}

    public int getIdRazaCanina() {
        return IdRazaCanina;
    }

    public void setIdRazaCanina(int idRazaCanina) {
        IdRazaCanina = idRazaCanina;
    }

    public String getRaza() {
        return Raza;
    }

    public void setRaza(String raza) {
        Raza = raza;
    }

    @Override
    public String toString(){
        return Raza;
    }
}
