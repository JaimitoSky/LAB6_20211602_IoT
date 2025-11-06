package com.example.lab6_20211602_iot.ui.tareas;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab6_20211602_iot.R;
import com.example.lab6_20211602_iot.adapter.TareasAdapter;
import com.example.lab6_20211602_iot.model.Tarea;
import com.example.lab6_20211602_iot.repository.TareaRepository;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TareasFragment extends Fragment implements TareasAdapter.Listener {

    private RecyclerView rv;
    private View fab;
    private TareasAdapter adapter;
    private TareaRepository repo;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tareas, container, false);
        rv = v.findViewById(R.id.rv);
        fab = v.findViewById(R.id.fabAdd);

        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new TareasAdapter(this);
        rv.setAdapter(adapter);

        repo = new TareaRepository();
        fab.setOnClickListener(x -> showAddOrEditDialog(null));

        subscribeRealtime();
        return v;
    }

    private void subscribeRealtime() {
        repo.ref().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Tarea> list = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Tarea t = child.getValue(Tarea.class);
                    if (t != null) list.add(t);
                }
                list.sort(Comparator.comparingLong(o -> o.fechaLimite));
                adapter.setItems(list);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void showAddOrEditDialog(@Nullable Tarea edit) {
        View content = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_tarea, null, false);
        EditText etTitulo = content.findViewById(R.id.etTitulo);
        EditText etDesc   = content.findViewById(R.id.etDesc);
        EditText etFecha  = content.findViewById(R.id.etFecha);
        CheckBox chk      = content.findViewById(R.id.chkEstado);

        final Calendar cal = Calendar.getInstance();
        etFecha.setOnClickListener(v -> new DatePickerDialog(requireContext(), (dp, y, m, d) -> {
            cal.set(y, m, d, 0, 0, 0);
            etFecha.setText(sdf.format(new Date(cal.getTimeInMillis())));
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show());

        if (edit != null) {
            etTitulo.setText(edit.titulo);
            etDesc.setText(edit.descripcion);
            etFecha.setText(sdf.format(new Date(edit.fechaLimite)));
            cal.setTimeInMillis(edit.fechaLimite);
            chk.setChecked(edit.estado);
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(edit == null ? "Nueva tarea" : "Editar tarea")
                .setView(content)
                .setPositiveButton(edit == null ? "Guardar" : "Actualizar", (d, w) -> {
                    String titulo = etTitulo.getText().toString().trim();
                    String desc   = etDesc.getText().toString().trim();
                    String fStr   = etFecha.getText().toString().trim();
                    boolean estado = chk.isChecked();

                    if (TextUtils.isEmpty(titulo) || TextUtils.isEmpty(fStr)) {
                        Snackbar.make(requireView(), "Completa título y fecha", Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    long fechaMs = cal.getTimeInMillis();

                    if (edit == null) {
                        Tarea t = new Tarea(null, titulo, desc, fechaMs, estado);
                        repo.add(t).addOnSuccessListener(x ->
                                        Snackbar.make(requireView(), "Tarea registrada", Snackbar.LENGTH_SHORT).show())
                                .addOnFailureListener(e ->
                                        Snackbar.make(requireView(), "Error: " + e.getMessage(), Snackbar.LENGTH_LONG).show());
                    } else {
                        edit.titulo = titulo;
                        edit.descripcion = desc;
                        edit.fechaLimite = fechaMs;
                        edit.estado = estado;
                        repo.update(edit).addOnSuccessListener(x ->
                                        Snackbar.make(requireView(), "Tarea actualizada", Snackbar.LENGTH_SHORT).show())
                                .addOnFailureListener(e ->
                                        Snackbar.make(requireView(), "Error: " + e.getMessage(), Snackbar.LENGTH_LONG).show());
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onToggle(Tarea t) {
        t.estado = !t.estado;
        repo.update(t);
    }

    @Override
    public void onEdit(Tarea t) {
        showAddOrEditDialog(t);
    }

    @Override
    public void onDelete(Tarea t) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Eliminar tarea")
                .setMessage("¿Seguro que deseas eliminarla?")
                .setPositiveButton("Eliminar", (d, w) -> repo.delete(t.id)
                        .addOnSuccessListener(x ->
                                Snackbar.make(requireView(), "Tarea eliminada", Snackbar.LENGTH_SHORT).show())
                        .addOnFailureListener(e ->
                                Snackbar.make(requireView(), "Error: " + e.getMessage(), Snackbar.LENGTH_LONG).show()))
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
