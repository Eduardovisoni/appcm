package com.example.coordinacion.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface PersonaDao {
    @Insert
    void insert(Persona persona);

    @Update
    void update(Persona persona);

    @Delete
    void delete(Persona persona);

    // Obtener personas filtradas por grupo
    @Query("SELECT * FROM personas WHERE grupo = :grupo")
    List<Persona> getPersonasPorGrupo(String grupo);

    // Obtener una persona por su ID
    @Query("SELECT * FROM personas WHERE id = :id LIMIT 1")
    Persona getPersonaPorId(int id);

    // Verificar si existe una persona por nombre y grupo (para validación)
    @Query("SELECT * FROM personas WHERE nombre = :nombre AND grupo = :grupo LIMIT 1")
    Persona buscarPorNombreYGrupo(String nombre, String grupo);

    // Eliminar solo registros del grupo actual
    @Query("DELETE FROM personas WHERE grupo = :grupo")
    void eliminarPorGrupo(String grupo);
}