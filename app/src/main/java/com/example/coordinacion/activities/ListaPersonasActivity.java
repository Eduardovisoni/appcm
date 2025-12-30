package com.example.coordinacion.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
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
    private TextView tvEmptyState;
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

        // Inicializar Vistas
        recyclerView = findViewById(R.id.recyclerViewPersonas);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar Base de Datos
        personaDao = AppDatabase.getDatabase(getApplicationContext()).personaDao();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cargar datos cada vez que se vuelve a la actividad (por si hubo ediciones)
        cargarPersonas();
    }

    private void cargarPersonas() {
        // Obtener lista filtrada por grupo
        List<Persona> listaPersonas = personaDao.getPersonasPorGrupo(grupoSeleccionado);

        // Lógica para mostrar mensaje si está vacío
        if (listaPersonas.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvEmptyState.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvEmptyState.setVisibility(View.GONE);
            
            // Configurar Adapter con listener de acciones
            adapter = new PersonaAdapter(listaPersonas, new PersonaAdapter.OnPersonaActionListener() {
                @Override
                public void onPersonaDelete(Persona persona) {
                    personaDao.delete(persona);
                    cargarPersonas();
                    Toast.makeText(ListaPersonasActivity.this, "Registro eliminado", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPersonaEdit(Persona persona) {
                    Intent intent = new Intent(ListaPersonasActivity.this, AgregarPersonaActivity.class);
                    intent.putExtra("persona_id", persona.id);
                    intent.putExtra(getString(R.string.intent_key_group), grupoSeleccionado);
                    intent.putExtra(getString(R.string.intent_key_category), persona.categoria);
                    startActivity(intent);
                }
            });
            recyclerView.setAdapter(adapter);
        }
    }
}