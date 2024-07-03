package com.example.appmovdoggepediaversion2;

public class AdquirirCasosExitosos {

    private String ApePat, ApeMat, Fecha, FotografiaUno, FotografiaDos, FotografiaTres, Nombre,  NombreAnimal;

    public AdquirirCasosExitosos() {
    }

    public AdquirirCasosExitosos(String apePat, String apeMat, String fecha, String fotografiaUno, String fotografiaDos, String fotografiaTres, String nombre, String nombreAnimal) {
        ApePat = apePat;
        ApeMat = apeMat;
        Fecha = fecha;
        FotografiaUno = fotografiaUno;
        FotografiaDos = fotografiaDos;
        FotografiaTres = fotografiaTres;
        Nombre = nombre;
        NombreAnimal = nombreAnimal;
    }

    public String getApePat() {return ApePat;}

    public void setApePat(String apePat) {ApePat = apePat;}

    public String getApeMat() {return ApeMat;}

    public void setApeMat(String apeMat) {ApeMat = apeMat;}

    public String getFecha() {return Fecha;}

    public void setFecha(String fecha) {Fecha = fecha;}

    public String getFotografiaUno() {return FotografiaUno;}

    public void setFotografiaUno(String fotografiaUno) {FotografiaUno = fotografiaUno;}

    public String getFotografiaDos() {return FotografiaDos;}

    public void setFotografiaDos(String fotografiaDos) {FotografiaDos = fotografiaDos;}

    public String getFotografiaTres() {return FotografiaTres;}

    public void setFotografiaTres(String fotografiaTres) {FotografiaTres = fotografiaTres;}

    public String getNombre() {return Nombre;}

    public void setNombre(String nombre) {Nombre = nombre;}

    public String getNombreAnimal() {return NombreAnimal;}

    public void setNombreAnimal(String nombreAnimal) {NombreAnimal = nombreAnimal;}
}
