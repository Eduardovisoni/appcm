package com.example.coordinacion.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.coordinacion.R;
import com.example.coordinacion.database.AppDatabase;
import com.example.coordinacion.database.Persona;
import com.example.coordinacion.database.PersonaDao;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class MenuPrincipalActivity extends AppCompatActivity {

    private String grupoSeleccionado;
    private PersonaDao personaDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);
        setTitle(R.string.menu_principal_activity_title);

        // Inicializar DAO
        personaDao = AppDatabase.getDatabase(getApplicationContext()).personaDao();

        grupoSeleccionado = getIntent().getStringExtra(getString(R.string.intent_key_group));

        if (grupoSeleccionado == null || grupoSeleccionado.isEmpty()) {
            Toast.makeText(this, "Error: Grupo no especificado.", Toast.LENGTH_SHORT).show();
            Log.e("MenuPrincipal", "No se recibió el grupo seleccionado.");
            finish();
            return;
        }

        Button btnAddPerson = findViewById(R.id.btnAddPerson);
        Button btnViewPeople = findViewById(R.id.btnViewPeople);
        Button btnDeleteRecords = findViewById(R.id.btnDeleteRecords);
        Button btnExportCsv = findViewById(R.id.btnExportCsv);

        btnAddPerson.setOnClickListener(v -> {
            Intent intent = new Intent(this, SeleccionarCategoriaActivity.class);
            intent.putExtra(getString(R.string.intent_key_group), grupoSeleccionado);
            startActivity(intent); 
        });

        btnViewPeople.setOnClickListener(v -> {
            Intent intent = new Intent(this, ListaPersonasActivity.class);
            intent.putExtra(getString(R.string.intent_key_group), grupoSeleccionado);
            startActivity(intent);
        });

        btnDeleteRecords.setOnClickListener(v -> {
            Intent intent = new Intent(this, EliminarRegistrosActivity.class);
            intent.putExtra(getString(R.string.intent_key_group), grupoSeleccionado);
            startActivity(intent);
        });

        btnExportCsv.setOnClickListener(v -> {
            exportarCSV();
        });
    }

    private void exportarCSV() {
        List<Persona> personas = personaDao.getPersonasPorGrupo(grupoSeleccionado);

        if (personas.isEmpty()) {
            Toast.makeText(this, "No hay datos para exportar en este grupo.", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder csvData = new StringBuilder();
        csvData.append("id,nombre,edad,categoria,organizacion,asistencias,tiempoEnsenando,direccion,grupo\n");

        for (Persona p : personas) {
            csvData.append(p.id).append(",");
            csvData.append(p.nombre).append(",");
            csvData.append(p.edad).append(",");
            csvData.append(p.categoria).append(",");
            csvData.append(p.organizacion).append(",");
            csvData.append(p.asistencias).append(",");
            csvData.append(p.tiempoEnsenando).append(",");
            csvData.append(p.direccion).append(",");
            csvData.append(p.grupo).append("\n");
        }

        try {
            guardarArchivoCSV(csvData.toString());
            Toast.makeText(this, R.string.export_success, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Log.e("ExportCSV", "Error al guardar el archivo", e);
            Toast.makeText(this, R.string.export_failure, Toast.LENGTH_LONG).show();
        }
    }

    private void guardarArchivoCSV(String content) throws IOException {
        String fileName = "coordinacion_" + grupoSeleccionado + "_" + System.currentTimeMillis() + ".csv";

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "text/csv");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
        }

        Uri uri = getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);

        if (uri != null) {
            try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
                if (outputStream != null) {
                    outputStream.write(content.getBytes());
                }
            }
        } else {
            throw new IOException("No se pudo crear el archivo en MediaStore");
        }
    }
}