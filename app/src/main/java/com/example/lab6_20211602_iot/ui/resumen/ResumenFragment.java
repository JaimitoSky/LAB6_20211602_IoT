package com.example.lab6_20211602_iot.ui.resumen;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lab6_20211602_iot.R;
import com.example.lab6_20211602_iot.model.Tarea;
import com.example.lab6_20211602_iot.repository.TareaRepository;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ResumenFragment extends Fragment {

    private TextView tvTotal, tvPend, tvComp;
    private PieChart pie;
    private TareaRepository repo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_resumen, container, false);

        tvTotal = v.findViewById(R.id.tvTotal);
        tvPend  = v.findViewById(R.id.tvPend);
        tvComp  = v.findViewById(R.id.tvComp);
        pie     = v.findViewById(R.id.pieChart);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Snackbar.make(v, "Sesión inválida. Inicia sesión nuevamente.", Snackbar.LENGTH_LONG).show();
            return v;
        }

        repo = new TareaRepository();
        setupChart();
        subscribeCounts(v);

        return v;
    }

    private void setupChart() {
        pie.getDescription().setEnabled(false);
        pie.setUsePercentValues(false);
        pie.setDrawHoleEnabled(true);
        pie.setHoleRadius(45f);
        pie.setTransparentCircleRadius(50f);
        pie.setEntryLabelColor(Color.DKGRAY);
        pie.setEntryLabelTextSize(12f);

        Legend l = pie.getLegend();
        l.setEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setWordWrapEnabled(true);
    }

    private void subscribeCounts(View root) {
        repo.ref().addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total = 0, comp = 0, pend = 0;
                for (DataSnapshot child : snapshot.getChildren()) {
                    Tarea t = child.getValue(Tarea.class);
                    if (t == null) continue;
                    total++;
                    if (t.estado) comp++; else pend++;
                }
                updateUI(total, comp, pend);
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {
                if (getView() != null) {
                    Snackbar.make(root, "DB error: " + error.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void updateUI(int total, int comp, int pend) {
        tvTotal.setText("Total: " + total);
        tvComp.setText("Completadas: " + comp);
        tvPend.setText("Pendientes: " + pend);

        List<PieEntry> entries = new ArrayList<>();
        if (pend > 0) entries.add(new PieEntry(pend, "Pendientes"));
        if (comp > 0) entries.add(new PieEntry(comp, "Completadas"));
        if (entries.isEmpty()) {
            entries.add(new PieEntry(1f, "Sin tareas"));
        }

        PieDataSet ds = new PieDataSet(entries, "");
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#FF9800"));
        colors.add(Color.parseColor("#4CAF50"));
        ds.setColors(colors);
        ds.setSliceSpace(3f);
        ds.setValueTextColor(Color.WHITE);
        ds.setValueTextSize(12f);

        PieData data = new PieData(ds);
        pie.setData(data);
        pie.highlightValues(null);
        pie.animateY(600, Easing.EaseInOutQuad);
        pie.invalidate();
    }
}
