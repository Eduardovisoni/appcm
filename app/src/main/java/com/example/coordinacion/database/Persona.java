package com.example.coordinacion.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "personas")
public class Persona {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String nombre;
    public int edad;
    public String categoria; 
    public String organizacion;
    public int asistencias;
    public int tiempoEnsenando;
    public String direccion;
    public String grupo; 
    public String notas; 
    public String genero; // Nuevo campo

    // Constructor actualizado
    public Persona(String nombre, int edad, String categoria, String organizacion, int asistencias, int tiempoEnsenando, String direccion, String grupo, String notas, String genero) {
        this.nombre = nombre;
        this.edad = edad;
        this.categoria = categoria;
        this.organizacion = organizacion;
        this.asistencias = asistencias;
        this.tiempoEnsenando = tiempoEnsenando;
        this.direccion = direccion;
        this.grupo = grupo;
        this.notas = notas;
        this.genero = genero;
    }
}