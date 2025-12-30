package com.example.coordinacion.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.coordinacion.R;
import com.example.coordinacion.database.AppDatabase;
import com.example.coordinacion.database.Persona;
import com.example.coordinacion.database.PersonaDao;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AgregarPersonaActivity extends AppCompatActivity {

    private EditText etNombre, etEdad, etDireccion, etNotas;
    private Spinner spOrganizacion, spAsistencias, spTiempoEnsenando;
    private RadioGroup rgGenero;
    private RadioButton rbHombre, rbMujer;
    private Button btnGuardarPersona;

    private String grupoSeleccionado;
    private String categoriaSeleccionada;
    private PersonaDao personaDao;

    private int personaId = -1; // -1 indica que estamos agregando, no editando
    private boolean esModoEdicion = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_persona);

        personaDao = AppDatabase.getDatabase(getApplicationContext()).personaDao();

        // Vincular vistas
        etNombre = findViewById(R.id.etNombre);
        etEdad = findViewById(R.id.etEdad);
        spTiempoEnsenando = findViewById(R.id.spTiempoEnsenando);
        etDireccion = findViewById(R.id.etDireccion);
        etNotas = findViewById(R.id.etNotas);
        spOrganizacion = findViewById(R.id.spOrganizacion);
        spAsistencias = findViewById(R.id.spAsistencias);
        rgGenero = findViewById(R.id.rgGenero);
        rbHombre = findViewById(R.id.rbHombre);
        rbMujer = findViewById(R.id.rbMujer);
        btnGuardarPersona = findViewById(R.id.btnGuardarPersona);

        configurarSpinners();
        configurarLogicaAutomatica();

        // Recuperar datos del Intent
        grupoSeleccionado = getIntent().getStringExtra(getString(R.string.intent_key_group));
        categoriaSeleccionada = getIntent().getStringExtra(getString(R.string.intent_key_category));
        personaId = getIntent().getIntExtra("persona_id", -1);

        if (personaId != -1) {
            esModoEdicion = true;
            setTitle("Editar Persona");
            btnGuardarPersona.setText("Actualizar Datos");
            cargarDatosPersona(personaId);
        } else {
            setTitle(R.string.agregar_persona_activity_title);
        }

        if (grupoSeleccionado == null || categoriaSeleccionada == null) {
            Toast.makeText(this, "Error: Faltan datos de grupo o categoría", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        btnGuardarPersona.setOnClickListener(v -> guardarPersona());
    }

    private void cargarDatosPersona(int id) {
        Persona persona = personaDao.getPersonaPorId(id);
        if (persona != null) {
            etNombre.setText(persona.nombre);
            etEdad.setText(String.valueOf(persona.edad));
            etDireccion.setText(persona.direccion);
            etNotas.setText(persona.notas);

            if ("Hombre".equals(persona.genero)) {
                rbHombre.setChecked(true);
            } else if ("Mujer".equals(persona.genero)) {
                rbMujer.setChecked(true);
            }

            // Seleccionar en Spinners
            seleccionarEnSpinner(spOrganizacion, persona.organizacion);

            String asistStr = (persona.asistencias == -1) ? "N/A" : (persona.asistencias == 11 ? "+10" : String.valueOf(persona.asistencias));
            seleccionarEnSpinner(spAsistencias, asistStr);

            String tiempoStr = (persona.tiempoEnsenando == -1) ? "N/A" : (persona.tiempoEnsenando == 6 ? "+5" : String.valueOf(persona.tiempoEnsenando));
            seleccionarEnSpinner(spTiempoEnsenando, tiempoStr);
        }
    }

    private void seleccionarEnSpinner(Spinner spinner, String valor) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        int position = adapter.getPosition(valor);
        if (position >= 0) {
            spinner.setSelection(position);
        }
    }

    private void guardarPersona() {
        boolean esValido = true;
        String mensajeError = null;

        String nombre = etNombre.getText().toString().trim();
        if (TextUtils.isEmpty(nombre)) {
            mensajeError = "El nombre es obligatorio";
            esValido = false;
        } else {
            String edadStr = etEdad.getText().toString().trim();
            if (TextUtils.isEmpty(edadStr)) {
                mensajeError = "La edad es obligatoria";
                esValido = false;
            } else if (rgGenero.getCheckedRadioButtonId() == -1) {
                mensajeError = "El género es obligatorio";
                esValido = false;
            } else {
                String organizacion = spOrganizacion.getSelectedItem().toString();
                if (organizacion.equals("Ninguna")) {
                    mensajeError = "La organización es obligatoria";
                    esValido = false;
                }
            }
        }
        
        if (!esValido) {
            Snackbar.make(findViewById(android.R.id.content), mensajeError, Snackbar.LENGTH_SHORT).show();
            return; 
        }

        // Si no es edición, validar que el nombre no esté repetido
        if (!esModoEdicion) {
            Persona existente = personaDao.buscarPorNombreYGrupo(nombre, grupoSeleccionado);
            if (existente != null) {
                Snackbar.make(findViewById(android.R.id.content), R.string.error_person_exists, Snackbar.LENGTH_LONG).show();
                return;
            }
        }
        
        String edadStr = etEdad.getText().toString().trim();
        String organizacion = spOrganizacion.getSelectedItem().toString();
        String genero = rbHombre.isChecked() ? "Hombre" : "Mujer";
        String tiempoEnsenandoStr = spTiempoEnsenando.getSelectedItem().toString();
        String direccion = etDireccion.getText().toString().trim();
        String notas = etNotas.getText().toString().trim();
        String asistenciasStr = spAsistencias.getSelectedItem().toString();

        int edad = Integer.parseInt(edadStr);

        int tiempoEnsenando;
        if (tiempoEnsenandoStr.equals("N/A")) {
            tiempoEnsenando = -1;
        } else if (tiempoEnsenandoStr.equals("+5")) {
            tiempoEnsenando = 6;
        } else {
            tiempoEnsenando = Integer.parseInt(tiempoEnsenandoStr);
        }
        
        int asistencias;
        if (asistenciasStr.equals("N/A")) {
            asistencias = -1;
        } else if (asistenciasStr.equals("+10")) {
            asistencias = 11;
        } else {
            asistencias = Integer.parseInt(asistenciasStr);
        }

        if (esModoEdicion) {
            Persona personaEditada = new Persona(nombre, edad, categoriaSeleccionada, organizacion, asistencias, tiempoEnsenando, direccion, grupoSeleccionado, notas, genero);
            personaEditada.id = personaId; // Mantener el mismo ID para la actualización
            personaDao.update(personaEditada);
            Toast.makeText(this, "Datos actualizados exitosamente", Toast.LENGTH_SHORT).show();
        } else {
            Persona nuevaPersona = new Persona(nombre, edad, categoriaSeleccionada, organizacion, asistencias, tiempoEnsenando, direccion, grupoSeleccionado, notas, genero);
            personaDao.insert(nuevaPersona);
            Toast.makeText(this, R.string.person_saved_successfully, Toast.LENGTH_SHORT).show();
        }

        finish();
    }
    
    private void configurarSpinners() {
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

        List<String> asistencias = new ArrayList<>();
        for (int i = 0; i <= 10; i++) {
            asistencias.add(String.valueOf(i));
        }
        asistencias.add("+10");
        asistencias.add("N/A");

        ArrayAdapter<String> asistAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, asistencias);
        asistAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spAsistencias.setAdapter(asistAdapter);

        List<String> tiempos = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            tiempos.add(String.valueOf(i));
        }
        tiempos.add("+5");
        tiempos.add("N/A");

        ArrayAdapter<String> tiempoAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, tiempos);
        tiempoAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spTiempoEnsenando.setAdapter(tiempoAdapter);
    }

    private void configurarLogicaAutomatica() {
        etEdad.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override public void afterTextChanged(Editable s) { 
                if (!esModoEdicion) { // Solo sugerir si no estamos editando (para no molestar al usuario)
                    actualizarOrganizacionSugerida(); 
                }
            }
        });

        rgGenero.setOnCheckedChangeListener((group, checkedId) -> {
            if (!esModoEdicion) {
                actualizarOrganizacionSugerida();
            }
        });
    }

    private void actualizarOrganizacionSugerida() {
        String edadStr = etEdad.getText().toString().trim();
        if (edadStr.isEmpty()) return; 

        try {
            int edad = Integer.parseInt(edadStr);
            String organizacionSugerida = "Ninguna";
            boolean esHombre = rbHombre.isChecked();
            boolean esMujer = rbMujer.isChecked();

            if (edad >= 0 && edad <= 11) {
                organizacionSugerida = "Primaria";
            } else if (edad >= 12 && edad <= 17) {
                if (esHombre) organizacionSugerida = "Hombres Jóvenes";
                else if (esMujer) organizacionSugerida = "Mujeres Jóvenes";
            } else if (edad >= 18) {
                if (esHombre) organizacionSugerida = "Cuórum de Élderes";
                else if (esMujer) organizacionSugerida = "Sociedad de Socorro";
            }

            if (!organizacionSugerida.equals("Ninguna")) {
                seleccionarEnSpinner(spOrganizacion, organizacionSugerida);
            }
        } catch (NumberFormatException e) { }
    }
}