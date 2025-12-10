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

    private EditText etNombre, etEdad, etTiempoEnsenando, etDireccion, etNotas;
    private Spinner spOrganizacion, spAsistencias;
    private RadioGroup rgGenero;
    private RadioButton rbHombre, rbMujer;
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

        // Vincular todas las vistas
        etNombre = findViewById(R.id.etNombre);
        etEdad = findViewById(R.id.etEdad);
        etTiempoEnsenando = findViewById(R.id.etTiempoEnsenando);
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

        btnGuardarPersona.setOnClickListener(v -> guardarPersona());
    }

    private void guardarPersona() {
        boolean esValido = true;
        String mensajeError = null;

        // 2. Validación de campos obligatorios
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
            // Usamos Snackbar en lugar de Toast para evitar el icono predeterminado
            Snackbar.make(findViewById(android.R.id.content), mensajeError, Snackbar.LENGTH_SHORT).show();
            return; // Si algún campo obligatorio falta, no continuar
        }

        // 3. Validación de persona existente
        Persona existente = personaDao.buscarPorNombreYGrupo(nombre, grupoSeleccionado);
        if (existente != null) {
            Snackbar.make(findViewById(android.R.id.content), R.string.error_person_exists, Snackbar.LENGTH_LONG).show();
            return;
        }
        
        // 4. Obtener el resto de los datos
        String edadStr = etEdad.getText().toString().trim();
        String organizacion = spOrganizacion.getSelectedItem().toString();
        String genero = rbHombre.isChecked() ? "Hombre" : "Mujer";
        String tiempoEnsenandoStr = etTiempoEnsenando.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();
        String notas = etNotas.getText().toString().trim();
        String asistenciasStr = spAsistencias.getSelectedItem().toString();

        // 5. Conversión de datos
        int edad = Integer.parseInt(edadStr);
        int tiempoEnsenando = TextUtils.isEmpty(tiempoEnsenandoStr) ? 0 : Integer.parseInt(tiempoEnsenandoStr);
        int asistencias = asistenciasStr.equals("+10") ? 11 : Integer.parseInt(asistenciasStr);

        // 6. Creación y guardado del objeto
        Persona nuevaPersona = new Persona(nombre, edad, categoriaSeleccionada, organizacion, asistencias, tiempoEnsenando, direccion, grupoSeleccionado, notas, genero);
        personaDao.insert(nuevaPersona);

        Toast.makeText(this, R.string.person_saved_successfully, Toast.LENGTH_SHORT).show();
        finish();
    }
    
    // --- Métodos de configuración (sin cambios) ---
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

        ArrayAdapter<String> asistAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, asistencias);
        asistAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spAsistencias.setAdapter(asistAdapter);
    }

    private void configurarLogicaAutomatica() {
        etEdad.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override public void afterTextChanged(Editable s) { actualizarOrganizacionSugerida(); }
        });

        rgGenero.setOnCheckedChangeListener((group, checkedId) -> actualizarOrganizacionSugerida());
    }

    private void actualizarOrganizacionSugerida() {
        String edadStr = etEdad.getText().toString().trim();
        if (edadStr.isEmpty()) {
            return; 
        }

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
                seleccionarOrganizacion(organizacionSugerida);
            }

        } catch (NumberFormatException e) { }
    }

    private void seleccionarOrganizacion(String organizacion) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spOrganizacion.getAdapter();
        int posicion = adapter.getPosition(organizacion);
        if (posicion >= 0) {
            spOrganizacion.setSelection(posicion);
        }
    }
}