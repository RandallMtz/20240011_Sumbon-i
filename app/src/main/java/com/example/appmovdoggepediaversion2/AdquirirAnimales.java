package com.example.appmovdoggepediaversion2;

public class AdquirirAnimales {
    private int Edad, IdAnimal;
    private String CodigoAnimal, Especie, Fotografia, Institucion, Nombre, RazaCanina, RazaMinina, Sexo, Tamaño;

    public AdquirirAnimales() {}

    public AdquirirAnimales(int edad, String codigoAnimal, String fotografia, String institucion, String nombre, String razaCanina, String razaMinina, String sexo, String tamaño) {
        Edad = edad;
        CodigoAnimal = codigoAnimal;
        Fotografia = fotografia;
        Institucion = institucion;
        Nombre = nombre;
        RazaCanina = razaCanina;
        RazaMinina = razaMinina;
        Sexo = sexo;
        Tamaño = tamaño;
    }

    public int getEdad() {
        return Edad;
    }

    public void setEdad(int edad) {
        Edad = edad;
    }

    public int getIdAnimal() {
        return IdAnimal;
    }

    public void setIdAnimal(int idAnimal) {
        IdAnimal = idAnimal;
    }

    public String getCodigoAnimal() {
        return CodigoAnimal;
    }

    public void setCodigoAnimal(String codigoAnimal) {
        CodigoAnimal = codigoAnimal;
    }

    public String getEspecie() {
        return Especie;
    }

    public void setEspecie(String especie) {
        Especie = especie;
    }

    public String getFotografia() {
        return Fotografia;
    }

    public void setFotografia(String fotografia) {
        Fotografia = fotografia;
    }

    public String getInstitucion() {
        return Institucion;
    }

    public void setInstitucion(String institucion) {
        Institucion = institucion;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getRazaCanina() {
        return RazaCanina;
    }

    public void setRazaCanina(String razaCanina) {
        RazaCanina = razaCanina;
    }

    public String getRazaMinina() {
        return RazaMinina;
    }

    public void setRazaMinina(String razaMinina) {
        RazaMinina = razaMinina;
    }

    public String getSexo() {
        return Sexo;
    }

    public void setSexo(String sexo) {
        Sexo = sexo;
    }

    public String getTamaño() {
        return Tamaño;
    }

    public void setTamaño(String tamaño) {
        Tamaño = tamaño;
    }

    @Override
    public String toString(){
        return CodigoAnimal;
    }
}
