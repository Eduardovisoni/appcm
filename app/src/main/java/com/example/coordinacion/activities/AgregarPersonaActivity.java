package com.example.coordinacion.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

    private EditText etNombre, etEdad, etTiempoEnsenando, etDireccion, etNotas; // Agregado etNotas
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
        etNotas = findViewById(R.id.etNotas); // Inicializar campo de notas
        
        spOrganizacion = findViewById(R.id.spOrganizacion);
        spAsistencias = findViewById(R.id.spAsistencias);
        
        btnGuardarPersona = findViewById(R.id.btnGuardarPersona);

        configurarSpinners();
        configurarLogicaEdadOrganizacion();

        btnGuardarPersona.setOnClickListener(v -> guardarPersona());
    }

    private void configurarSpinners() {
        // Lista de organizaciones actualizada (sin Escuela Dominical)
        List<String> organizaciones = Arrays.asList(
                "Ninguna",
                "Primaria",
                "Hombres Jóvenes",
                "Mujeres Jóvenes",
                "Sociedad de Socorro",
                "Cuórum de Élderes"
        );
        ArrayAdapter<String> orgAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, organizaciones);
        orgAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spOrganizacion.setAdapter(orgAdapter);

        // Configurar Spinner de Asistencias
        List<String> asistencias = new ArrayList<>();
        for (int i = 0; i <= 10; i++) {
            asistencias.add(String.valueOf(i));
        }
        asistencias.add("+10");

        ArrayAdapter<String> asistAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, asistencias);
        asistAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spAsistencias.setAdapter(asistAdapter);
    }

    private void configurarLogicaEdadOrganizacion() {
        etEdad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    seleccionarOrganizacion("Ninguna");
                    return;
                }

                try {
                    int edad = Integer.parseInt(s.toString());
                    String organizacionSugerida;

                    if (edad >= 0 && edad <= 11) {
                        organizacionSugerida = "Primaria";
                    } else if (edad >= 12 && edad <= 17) {
                        // Distinguir entre Hombres y Mujeres Jóvenes
                        if (grupoSeleccionado.equals(getString(R.string.group_elders))) {
                            organizacionSugerida = "Hombres Jóvenes";
                        } else {
                            organizacionSugerida = "Mujeres Jóvenes";
                        }
                    } else if (edad >= 18) {
                        // Distinguir entre Élderes y Sociedad de Socorro
                        if (grupoSeleccionado.equals(getString(R.string.group_elders))) {
                            organizacionSugerida = "Cuórum de Élderes";
                        } else {
                            organizacionSugerida = "Sociedad de Socorro";
                        }
                    } else {
                        organizacionSugerida = "Ninguna";
                    }
                    seleccionarOrganizacion(organizacionSugerida);
                } catch (NumberFormatException e) {
                    // Si el texto no es un número válido, no hacer nada o seleccionar "Ninguna"
                    seleccionarOrganizacion("Ninguna");
                }
            }
        });
    }

    private void seleccionarOrganizacion(String organizacion) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spOrganizacion.getAdapter();
        int posicion = adapter.getPosition(organizacion);
        if (posicion >= 0) {
            spOrganizacion.setSelection(posicion);
        }
    }

    private void guardarPersona() {
        String nombre = etNombre.getText().toString().trim();
        String edadStr = etEdad.getText().toString().trim();
        String tiempoEnsenandoStr = etTiempoEnsenando.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();
        String notas = etNotas.getText().toString().trim(); // Obtener texto de notas

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
        
        int asistencias = 0;
        if (asistenciasStr.equals("+10")) {
            asistencias = 11; 
        } else {
            asistencias = Integer.parseInt(asistenciasStr);
        }

        // Guardar con el nuevo campo de notas
        Persona nuevaPersona = new Persona(nombre, edad, categoriaSeleccionada, organizacion, asistencias, tiempoEnsenando, direccion, grupoSeleccionado, notas);

        personaDao.insert(nuevaPersona);

        Toast.makeText(this, R.string.person_saved_successfully, Toast.LENGTH_SHORT).show();
        finish(); 
    }
}