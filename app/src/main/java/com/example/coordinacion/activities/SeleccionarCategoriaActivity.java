package com.example.coordinacion.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.coordinacion.R;

public class SeleccionarCategoriaActivity extends AppCompatActivity {

    private String grupoSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_categoria);
        setTitle(R.string.seleccionar_categoria_activity_title);

        grupoSeleccionado = getIntent().getStringExtra(getString(R.string.intent_key_group));

        if (grupoSeleccionado == null) {
            Toast.makeText(this, "Error: Grupo no identificado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Button btnFriend = findViewById(R.id.btnCatFriend);
        Button btnNewConvert = findViewById(R.id.btnCatNewConvert);
        Button btnInactive = findViewById(R.id.btnCatInactive);
        Button btnMember = findViewById(R.id.btnCatMember);

        btnFriend.setOnClickListener(v -> abrirFormulario(getString(R.string.category_friend)));
        btnNewConvert.setOnClickListener(v -> abrirFormulario(getString(R.string.category_new_convert)));
        btnInactive.setOnClickListener(v -> abrirFormulario(getString(R.string.category_inactive_member)));
        btnMember.setOnClickListener(v -> abrirFormulario(getString(R.string.category_member)));
    }

    private void abrirFormulario(String categoria) {
        Intent intent = new Intent(this, AgregarPersonaActivity.class);
        intent.putExtra(getString(R.string.intent_key_group), grupoSeleccionado);
        intent.putExtra(getString(R.string.intent_key_category), categoria);
        startActivity(intent);
        // Finalizamos esta actividad para que al volver desde AgregarPersonaActivity,
        // se regrese directamente al menú principal.
        finish();
    }
}