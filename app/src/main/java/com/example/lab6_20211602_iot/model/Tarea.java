package com.example.lab6_20211602_iot.model;


public class Tarea {
    public String id;
    public String titulo;
    public String descripcion;
    public long fechaLimite;
    public boolean estado;


    public Tarea() { }


    public Tarea(String id, String titulo, String descripcion, long fechaLimite, boolean estado) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaLimite = fechaLimite;
        this.estado = estado;
    }
}