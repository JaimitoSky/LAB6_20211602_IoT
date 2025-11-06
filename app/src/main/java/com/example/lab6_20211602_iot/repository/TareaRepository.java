package com.example.lab6_20211602_iot.repository;

import androidx.annotation.NonNull;
import com.example.lab6_20211602_iot.model.Tarea;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TareaRepository {

    private final DatabaseReference userRef;

    public TareaRepository() {
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        userRef = FirebaseDatabase.getInstance().getReference("tareas").child(uid);
    }

    public Task<Void> add(@NonNull Tarea t) {
        String key = userRef.push().getKey();
        t.id = key;
        return userRef.child(key).setValue(t);
    }

    public Task<Void> update(@NonNull Tarea t) {
        Map<String, Object> map = new HashMap<>();
        map.put("titulo", t.titulo);
        map.put("descripcion", t.descripcion);
        map.put("fechaLimite", t.fechaLimite);
        map.put("estado", t.estado);
        return userRef.child(t.id).updateChildren(map);
    }

    public Task<Void> delete(@NonNull String id) {
        return userRef.child(id).removeValue();
    }

    public DatabaseReference ref() {
        return userRef;
    }
}
