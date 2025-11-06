package com.example.lab6_20211602_iot.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab6_20211602_iot.R;
import com.example.lab6_20211602_iot.model.Tarea;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TareasAdapter extends RecyclerView.Adapter<TareasAdapter.VH> {

    public interface Listener {
        void onToggle(Tarea t);
        void onEdit(Tarea t);
        void onDelete(Tarea t);
    }

    private final List<Tarea> data = new ArrayList<>();
    private final Listener listener;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public TareasAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setItems(List<Tarea> items) {
        data.clear();
        if (items != null) data.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tarea, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Tarea t = data.get(position);
        h.tvTitulo.setText(t.titulo + (t.estado ? "  ✓" : ""));
        h.tvFecha.setText("Vence: " + sdf.format(new Date(t.fechaLimite)));

        h.btnToggle.setOnClickListener(v -> listener.onToggle(t));
        h.btnEdit.setOnClickListener(v -> listener.onEdit(t));
        h.btnDelete.setOnClickListener(v -> listener.onDelete(t));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvFecha;
        View btnToggle, btnEdit, btnDelete;

        VH(@NonNull View v) {
            super(v);
            tvTitulo  = v.findViewById(R.id.tvTitulo);
            tvFecha   = v.findViewById(R.id.tvFecha);
            btnToggle = v.findViewById(R.id.btnToggle);
            btnEdit   = v.findViewById(R.id.btnEdit);
            btnDelete = v.findViewById(R.id.btnDelete);
        }
    }
}
