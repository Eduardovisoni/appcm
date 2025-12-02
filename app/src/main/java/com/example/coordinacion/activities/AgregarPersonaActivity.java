package com.example.coordinacion.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.coordinacion.R;
import com.example.coordinacion.database.AppDatabase;
import com.example.coordinacion.database.Persona;
import com.example.coordinacion.database.PersonaDao;

public class AgregarPersonaActivity extends AppCompatActivity {

    private EditText etNombre, etEdad, etOrganizacion, etAsistencias, etTiempoEnsenando, etDireccion;
    private Button btnGuardarPersona;

    private String grupoSeleccionado;
    private String categoriaSeleccionada;

    private PersonaDao personaDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_persona);
        setTitle(R.string.agregar_persona_activity_title);

        // 1. Obtener la instancia del DAO de Room
        personaDao = AppDatabase.getDatabase(getApplicationContext()).personaDao();

        // 2. Recuperar los datos pasados desde la actividad anterior
        grupoSeleccionado = getIntent().getStringExtra(getString(R.string.intent_key_group));
        categoriaSeleccionada = getIntent().getStringExtra(getString(R.string.intent_key_category));

        if (grupoSeleccionado == null || categoriaSeleccionada == null) {
            Toast.makeText(this, "Error: Faltan datos de grupo o categoría", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // 3. Vincular las vistas del layout
        etNombre = findViewById(R.id.etNombre);
        etEdad = findViewById(R.id.etEdad);
        etOrganizacion = findViewById(R.id.etOrganizacion);
        etAsistencias = findViewById(R.id.etAsistencias);
        etTiempoEnsenando = findViewById(R.id.etTiempoEnsenando);
        etDireccion = findViewById(R.id.etDireccion);
        btnGuardarPersona = findViewById(R.id.btnGuardarPersona);

        // 4. Configurar el listener para el botón de guardar
        btnGuardarPersona.setOnClickListener(v -> guardarPersona());
    }

    private void guardarPersona() {
        // 5. Obtener los textos de los campos de entrada
        String nombre = etNombre.getText().toString().trim();
        String edadStr = etEdad.getText().toString().trim();
        String organizacion = etOrganizacion.getText().toString().trim();
        String asistenciasStr = etAsistencias.getText().toString().trim();
        String tiempoEnsenandoStr = etTiempoEnsenando.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();

        // 6. Validar que el nombre no esté vacío
        if (TextUtils.isEmpty(nombre)) {
            etNombre.setError(getString(R.string.error_empty_field));
            etNombre.requestFocus();
            return;
        }

        // 7. Validar que la persona no exista ya en el mismo grupo
        Persona existente = personaDao.buscarPorNombreYGrupo(nombre, grupoSeleccionado);
        if (existente != null) {
            Toast.makeText(this, R.string.error_person_exists, Toast.LENGTH_LONG).show();
            return;
        }

        // 8. Convertir los campos numéricos, manejando valores vacíos
        int edad = TextUtils.isEmpty(edadStr) ? 0 : Integer.parseInt(edadStr);
        int asistencias = TextUtils.isEmpty(asistenciasStr) ? 0 : Integer.parseInt(asistenciasStr);
        int tiempoEnsenando = TextUtils.isEmpty(tiempoEnsenandoStr) ? 0 : Integer.parseInt(tiempoEnsenandoStr);

        // 9. Crear el objeto Persona
        Persona nuevaPersona = new Persona(nombre, edad, categoriaSeleccionada, organizacion, asistencias, tiempoEnsenando, direccion, grupoSeleccionado);

        // 10. Insertar en la base de datos
        personaDao.insert(nuevaPersona);

        // 11. Notificar al usuario y cerrar la actividad
        Toast.makeText(this, R.string.person_saved_successfully, Toast.LENGTH_SHORT).show();
        finish(); // Regresa a la pantalla anterior
    }
}