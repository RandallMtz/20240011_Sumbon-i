package com.example.appmovdoggepediaversion2;

public class AdquirirRazasMininas {
    private int IdRazaMinina;
    private String Raza;

    public AdquirirRazasMininas(){}

    public int getIdRazaMinina() {
        return IdRazaMinina;
    }

    public void setIdRazaMinina(int idRazaMinina) {
        IdRazaMinina = idRazaMinina;
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
