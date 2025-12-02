package com.example.coordinacion.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.example.coordinacion.database.Persona;
import com.example.coordinacion.database.PersonaDao;
import android.content.Context;
import androidx.room.Room;

@Database(entities = {Persona.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PersonaDao personaDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "coordinacion_database")
                            .allowMainThreadQueries() // Permitir consultas en hilo principal por simplicidad en este demo
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}