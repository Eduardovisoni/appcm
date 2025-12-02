package com.example.coordinacion.activities;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coordinacion.R;
import com.example.coordinacion.adapters.PersonaAdapter;
import com.example.coordinacion.database.AppDatabase;
import com.example.coordinacion.database.Persona;
import com.example.coordinacion.database.PersonaDao;

import java.util.List;

public class ListaPersonasActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PersonaAdapter adapter;
    private PersonaDao personaDao;
    private String grupoSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_personas);
        setTitle(R.string.lista_personas_activity_title);

        // Recuperar grupo
        grupoSeleccionado = getIntent().getStringExtra(getString(R.string.intent_key_group));
        if (grupoSeleccionado == null) {
            Toast.makeText(this, "Error: Grupo no especificado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Inicializar RecyclerView
        recyclerView = findViewById(R.id.recyclerViewPersonas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar Base de Datos
        personaDao = AppDatabase.getDatabase(getApplicationContext()).personaDao();

        // Cargar datos
        cargarPersonas();
    }

    private void cargarPersonas() {
        // Obtener lista filtrada por grupo
        List<Persona> listaPersonas = personaDao.getPersonasPorGrupo(grupoSeleccionado);
        
        // Configurar Adapter
        adapter = new PersonaAdapter(listaPersonas);
        recyclerView.setAdapter(adapter);
    }
}