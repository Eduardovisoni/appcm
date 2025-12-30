package com.example.coordinacion.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.coordinacion.R;
import com.example.coordinacion.database.AppDatabase;
import com.example.coordinacion.database.PersonaDao;

public class EliminarRegistrosActivity extends AppCompatActivity {

    private String grupoSeleccionado;
    private PersonaDao personaDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eliminar_registros);
        setTitle(R.string.eliminar_registros_activity_title);

        grupoSeleccionado = getIntent().getStringExtra(getString(R.string.intent_key_group));
        personaDao = AppDatabase.getDatabase(getApplicationContext()).personaDao();

        if (grupoSeleccionado == null) {
            Toast.makeText(this, "Error: Grupo no especificado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Button btnConfirmDelete = findViewById(R.id.btnConfirmDelete);
        btnConfirmDelete.setOnClickListener(v -> eliminarRegistros());
    }

    private void eliminarRegistros() {
        // Ejecutar el borrado en la base de datos filtrando por grupo
        personaDao.eliminarPorGrupo(grupoSeleccionado);

        Toast.makeText(this, R.string.records_deleted_successfully, Toast.LENGTH_LONG).show();
        finish();
    }
}