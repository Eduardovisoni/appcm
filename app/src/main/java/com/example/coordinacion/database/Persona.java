package com.example.coordinacion.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "personas")
public class Persona {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String nombre;
    public int edad;
    public String categoria; // Amigo, Recién Converso, etc.
    public String organizacion;
    public int asistencias;
    public int tiempoEnsenando;
    public String direccion;
    public String grupo; // Elderes o Hermanas

    public Persona(String nombre, int edad, String categoria, String organizacion, int asistencias, int tiempoEnsenando, String direccion, String grupo) {
        this.nombre = nombre;
        this.edad = edad;
        this.categoria = categoria;
        this.organizacion = organizacion;
        this.asistencias = asistencias;
        this.tiempoEnsenando = tiempoEnsenando;
        this.direccion = direccion;
        this.grupo = grupo;
    }
}