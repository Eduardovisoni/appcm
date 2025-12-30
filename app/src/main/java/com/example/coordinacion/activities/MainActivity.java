package com.example.coordinacion.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.coordinacion.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Asignar el título desde los recursos de strings
        setTitle(R.string.main_activity_title);

        Button btnElderes = findViewById(R.id.btnElderes);
        Button btnSisters = findViewById(R.id.btnSisters);

        // Listener para el botón de Elderes
        btnElderes.setOnClickListener(v -> {
            abrirMenu(getString(R.string.group_elders));
        });

        // Listener para el botón de Hermanas
        btnSisters.setOnClickListener(v -> {
            abrirMenu(getString(R.string.group_sisters));
        });
    }

    /**
     * Inicia MenuPrincipalActivity y le pasa el grupo seleccionado.
     * @param grupo El grupo seleccionado ("Elderes" o "Hermanas").
     */
    private void abrirMenu(String grupo) {
        Intent intent = new Intent(this, MenuPrincipalActivity.class);
        // Usar una clave definida en strings.xml para pasar el dato
        intent.putExtra(getString(R.string.intent_key_group), grupo);
        startActivity(intent);
    }
}