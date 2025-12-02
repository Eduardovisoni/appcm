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
        String detalles = "Edad: " + persona.edad + ", Asistencias: " + persona.asistencias;
        holder.tvDetalles.setText(detalles);
    }

    @Override
    public int getItemCount() {
        return personas != null ? personas.size() : 0;
    }

    static class PersonaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvCategoria, tvDetalles;

        public PersonaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvPersonaNombre);
            tvCategoria = itemView.findViewById(R.id.tvPersonaCategoria);
            tvDetalles = itemView.findViewById(R.id.tvPersonaDetalles);
        }
    }
}