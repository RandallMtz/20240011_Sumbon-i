package com.example.appmovdoggepediaversion2;

public class AdquirirCondiciones {
    private int IdCondicion;
    private String Condicion;

    public AdquirirCondiciones(){}

    public int getIdCondicion() {
        return IdCondicion;
    }

    public void setIdCondicion(int idCondicion) {
        IdCondicion = idCondicion;
    }

    public String getCondicion() {
        return Condicion;
    }

    public void setCondicion(String condicion) {
        Condicion = condicion;
    }

    @Override
    public String toString(){
        return Condicion;
    }
}