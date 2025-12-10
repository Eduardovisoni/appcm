package com.example.coordinacion.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coordinacion.R;
import com.example.coordinacion.database.Persona;

import java.util.List;

public class PersonaAdapter extends RecyclerView.Adapter<PersonaAdapter.PersonaViewHolder> {

    private List<Persona> personas;

    public PersonaAdapter(List<Persona> personas) {
        this.personas = personas;
    }

    @NonNull
    @Override
    public PersonaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_persona, parent, false);
        return new PersonaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonaViewHolder holder, int position) {
        Persona persona = personas.get(position);
        
        holder.tvNombre.setText(persona.nombre);
        holder.tvCategoria.setText(persona.categoria);
        
        String generoTexto = (persona.genero != null && !persona.genero.isEmpty()) ? persona.genero : "-";
        String edadTexto = (persona.edad == 0) ? "-" : String.valueOf(persona.edad);
        holder.tvDetalles.setText("Edad: " + edadTexto + " | Género: " + generoTexto);
        
        String orgTexto = (persona.organizacion != null && !persona.organizacion.isEmpty() && !persona.organizacion.equals("Ninguna")) ? persona.organizacion : "-";
        holder.tvOrg.setText("Organización: " + orgTexto);

        // Asistencias y Tiempo Enseñando
        String asistenciasTexto = (persona.asistencias == 11) ? "+10" : String.valueOf(persona.asistencias);
        String tiempoTexto = (persona.tiempoEnsenando == 0) ? "-" : persona.tiempoEnsenando + " sem.";
        holder.tvStats.setText("Asistencias: " + asistenciasTexto + " | Tiempo Ens.: " + tiempoTexto);
        
        String dirTexto = (persona.direccion != null && !persona.direccion.isEmpty()) ? persona.direccion : "-";
        holder.tvDireccion.setText("Dirección: " + dirTexto);

        if (persona.notas == null || persona.notas.trim().isEmpty()) {
            holder.tvNotas.setText("Notas: -");
        } else {
            holder.tvNotas.setText("Notas: " + persona.notas);
        }
    }

    @Override
    public int getItemCount() {
        return personas != null ? personas.size() : 0;
    }

    static class PersonaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvCategoria, tvDetalles, tvOrg, tvStats, tvDireccion, tvNotas;

        public PersonaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvPersonaNombre);
            tvCategoria = itemView.findViewById(R.id.tvPersonaCategoria);
            tvDetalles = itemView.findViewById(R.id.tvPersonaDetalles);
            tvOrg = itemView.findViewById(R.id.tvPersonaOrg);
            tvStats = itemView.findViewById(R.id.tvPersonaStats);
            tvDireccion = itemView.findViewById(R.id.tvPersonaDireccion);
            tvNotas = itemView.findViewById(R.id.tvPersonaNotas);
        }
    }
}