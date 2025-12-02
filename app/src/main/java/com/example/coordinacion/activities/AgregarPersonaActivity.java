package com.example.coordinacion.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.coordinacion.R;
import com.example.coordinacion.database.AppDatabase;
import com.example.coordinacion.database.Persona;
import com.example.coordinacion.database.PersonaDao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AgregarPersonaActivity extends AppCompatActivity {

    private EditText etNombre, etEdad, etTiempoEnsenando, etDireccion;
    private Spinner spOrganizacion, spAsistencias;
    private Button btnGuardarPersona;

    private String grupoSeleccionado;
    private String categoriaSeleccionada;

    private PersonaDao personaDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_persona);
        setTitle(R.string.agregar_persona_activity_title);

        personaDao = AppDatabase.getDatabase(getApplicationContext()).personaDao();

        grupoSeleccionado = getIntent().getStringExtra(getString(R.string.intent_key_group));
        categoriaSeleccionada = getIntent().getStringExtra(getString(R.string.intent_key_category));

        if (grupoSeleccionado == null || categoriaSeleccionada == null) {
            Toast.makeText(this, "Error: Faltan datos de grupo o categoría", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        etNombre = findViewById(R.id.etNombre);
        etEdad = findViewById(R.id.etEdad);
        etTiempoEnsenando = findViewById(R.id.etTiempoEnsenando);
        etDireccion = findViewById(R.id.etDireccion);
        
        // Nuevos Spinners
        spOrganizacion = findViewById(R.id.spOrganizacion);
        spAsistencias = findViewById(R.id.spAsistencias);
        
        btnGuardarPersona = findViewById(R.id.btnGuardarPersona);

        configurarSpinners();

        btnGuardarPersona.setOnClickListener(v -> guardarPersona());
    }

    private void configurarSpinners() {
        // Configurar Spinner de Organización
        List<String> organizaciones = Arrays.asList(
                "Ninguna",
                "Primaria",
                "Hombres Jóvenes",
                "Mujeres Jóvenes",
                "Sociedad de Socorro",
                "Cuórum de Élderes",
                "Escuela Dominical"
        );
        ArrayAdapter<String> orgAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, organizaciones);
        // Usamos nuestro layout personalizado para la lista desplegable también
        orgAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spOrganizacion.setAdapter(orgAdapter);

        // Configurar Spinner de Asistencias
        List<String> asistencias = new ArrayList<>();
        for (int i = 0; i <= 10; i++) {
            asistencias.add(String.valueOf(i));
        }
        asistencias.add("+10");

        ArrayAdapter<String> asistAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, asistencias);
        // Usamos nuestro layout personalizado para la lista desplegable también
        asistAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spAsistencias.setAdapter(asistAdapter);
    }

    private void guardarPersona() {
        String nombre = etNombre.getText().toString().trim();
        String edadStr = etEdad.getText().toString().trim();
        String tiempoEnsenandoStr = etTiempoEnsenando.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();

        // Obtener valores de los Spinners
        String organizacion = spOrganizacion.getSelectedItem().toString();
        String asistenciasStr = spAsistencias.getSelectedItem().toString();

        if (TextUtils.isEmpty(nombre)) {
            etNombre.setError(getString(R.string.error_empty_field));
            etNombre.requestFocus();
            return;
        }

        Persona existente = personaDao.buscarPorNombreYGrupo(nombre, grupoSeleccionado);
        if (existente != null) {
            Toast.makeText(this, R.string.error_person_exists, Toast.LENGTH_LONG).show();
            return;
        }

        int edad = TextUtils.isEmpty(edadStr) ? 0 : Integer.parseInt(edadStr);
        int tiempoEnsenando = TextUtils.isEmpty(tiempoEnsenandoStr) ? 0 : Integer.parseInt(tiempoEnsenandoStr);
        
        // Convertir asistencias a entero. Si es "+10", guardamos 11 (u otro valor lógico)
        int asistencias = 0;
        if (asistenciasStr.equals("+10")) {
            asistencias = 11; 
        } else {
            asistencias = Integer.parseInt(asistenciasStr);
        }

        Persona nuevaPersona = new Persona(nombre, edad, categoriaSeleccionada, organizacion, asistencias, tiempoEnsenando, direccion, grupoSeleccionado);

        personaDao.insert(nuevaPersona);

        Toast.makeText(this, R.string.person_saved_successfully, Toast.LENGTH_SHORT).show();
        finish(); 
    }
}