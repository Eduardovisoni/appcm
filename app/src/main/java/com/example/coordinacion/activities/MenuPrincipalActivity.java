package com.example.coordinacion.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.coordinacion.R;
import com.example.coordinacion.database.AppDatabase;
import com.example.coordinacion.database.Persona;
import com.example.coordinacion.database.PersonaDao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MenuPrincipalActivity extends AppCompatActivity {

    private String grupoSeleccionado;
    private PersonaDao personaDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);
        setTitle(R.string.menu_principal_activity_title);

        personaDao = AppDatabase.getDatabase(getApplicationContext()).personaDao();
        grupoSeleccionado = getIntent().getStringExtra(getString(R.string.intent_key_group));

        if (grupoSeleccionado == null || grupoSeleccionado.isEmpty()) {
            Toast.makeText(this, "Error: Grupo no especificado.", Toast.LENGTH_SHORT).show();
            Log.e("MenuPrincipal", "No se recibió el grupo seleccionado.");
            finish();
            return;
        }

        // ... (resto de los listeners de botones)
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
            exportarYCompartirReporteHTML();
        });
    }

    private void exportarYCompartirReporteHTML() {
        List<Persona> todasLasPersonas = personaDao.getPersonasPorGrupo(grupoSeleccionado);

        if (todasLasPersonas.isEmpty()) {
            Toast.makeText(this, "No hay datos para exportar en este grupo.", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder html = new StringBuilder();
        html.append("<html><head><meta charset=\"UTF-8\"><style>");
        
        html.append("body { font-family: Arial, sans-serif; }");
        html.append("h1 { text-align: center; margin-bottom: 20px; }");
        html.append("table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }");
        html.append("th, td { border: 1px solid #000; padding: 8px; text-align: center; vertical-align: middle; }"); 
        html.append(".category-header { background-color: #4A5C6A; color: white; text-align: center; font-size: 18px; font-weight: bold; }");
        html.append(".column-header { background-color: #f2f2f2; font-weight: bold; }");
        
        html.append("</style></head><body>");
        html.append("<h1>Reporte de ").append(grupoSeleccionado).append("</h1>");

        String[] categorias = { getString(R.string.category_friend), getString(R.string.category_new_convert), getString(R.string.category_inactive_member), getString(R.string.category_member) };

        for (String categoria : categorias) {
            List<Persona> personasCategoria = new ArrayList<>();
            for (Persona p : todasLasPersonas) {
                if (p.categoria != null && p.categoria.equals(categoria)) {
                    personasCategoria.add(p);
                }
            }
            if (!personasCategoria.isEmpty()) {
                html.append("<table><tr><td colspan='8' class='category-header'>").append(categoria).append("</td></tr>");
                html.append("<tr class='column-header'><th>Nombre</th><th>Edad</th><th>Género</th><th>Organización</th><th>Asistencias</th><th>Tiempo Ens.</th><th>Dirección</th><th>Notas</th></tr>");
                
                for (Persona p : personasCategoria) {
                    String edadTexto = (p.edad == 0) ? "-" : String.valueOf(p.edad);
                    String generoTexto = (p.genero != null && !p.genero.isEmpty()) ? p.genero : "-";
                    String orgTexto = (p.organizacion != null && !p.organizacion.isEmpty() && !p.organizacion.equals("Ninguna")) ? p.organizacion : "-";
                    String asistenciasTexto = (p.asistencias == 11) ? "+10" : String.valueOf(p.asistencias);
                    String tiempoTexto = (p.tiempoEnsenando == 0) ? "-" : p.tiempoEnsenando + " sem.";
                    String dirTexto = (p.direccion != null && !p.direccion.isEmpty()) ? p.direccion : "-";
                    String notaTexto = (p.notas != null && !p.notas.isEmpty()) ? p.notas : "-";

                    html.append("<tr>")
                        .append("<td>").append(p.nombre).append("</td>")
                        .append("<td>").append(edadTexto).append("</td>")
                        .append("<td>").append(generoTexto).append("</td>")
                        .append("<td>").append(orgTexto).append("</td>")
                        .append("<td>").append(asistenciasTexto).append("</td>")
                        .append("<td>").append(tiempoTexto).append("</td>")
                        .append("<td>").append(dirTexto).append("</td>")
                        .append("<td>").append(notaTexto).append("</td>")
                        .append("</tr>");
                }
                html.append("</table><br>");
            }
        }
        html.append("</body></html>");

        try {
            File cachePath = new File(getCacheDir(), "files");
            cachePath.mkdirs(); 
            File file = new File(cachePath, "Reporte_" + grupoSeleccionado + ".html");
            FileOutputStream stream = new FileOutputStream(file);
            stream.write(html.toString().getBytes());
            stream.close();

            Uri contentUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);

            if (contentUri != null) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); 
                shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Reporte de " + grupoSeleccionado);
                startActivity(Intent.createChooser(shareIntent, "Compartir reporte vía..."));
            }

        } catch (IOException e) {
            Log.e("ShareHTML", "Error al crear o compartir el archivo HTML", e);
            Toast.makeText(this, "Error al compartir el archivo.", Toast.LENGTH_LONG).show();
        }
    }
}