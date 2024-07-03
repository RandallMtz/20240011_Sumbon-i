package com.example.appmovdoggepediaversion2;

public class AdquirirInstituciones {
    private int IdInstitucion;
    private String Nombre;

    public AdquirirInstituciones() {}

    public int getIdInstitucion() {
        return IdInstitucion;
    }

    public void setIdInstitucion(int idInstitucion) {
        IdInstitucion = idInstitucion;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    @Override
    public String toString(){
        return Nombre;
    }
}
