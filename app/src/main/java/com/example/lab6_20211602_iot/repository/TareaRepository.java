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
        // Obtener el UID del usuario autenticado (requerido por tus reglas)
        String uid = Objects.requireNonNull(
                FirebaseAuth.getInstance().getCurrentUser(),
                "No hay usuario autenticado. Inicia sesión antes de acceder a tareas."
        ).getUid();

        // Ruta segura en Realtime Database: /tareas/{uid}
        userRef = FirebaseDatabase.getInstance()
                .getReference("tareas")
                .child(uid);
    }

    /** Agrega una nueva tarea */
    public Task<Void> add(@NonNull Tarea t) {
        String key = userRef.push().getKey();
        if (key == null) {
            throw new IllegalStateException("No se pudo generar un ID para la tarea.");
        }
        t.id = key;
        return userRef.child(key).setValue(t);
    }

    /** Actualiza los campos principales de una tarea existente */
    public Task<Void> update(@NonNull Tarea t) {
        if (t.id == null || t.id.isEmpty()) {
            throw new IllegalArgumentException("El ID de la tarea no puede ser nulo o vacío.");
        }

        Map<String, Object> map = new HashMap<>();
        map.put("titulo", t.titulo);
        map.put("descripcion", t.descripcion);
        map.put("fechaLimite", t.fechaLimite);
        map.put("estado", t.estado);

        return userRef.child(t.id).updateChildren(map);
    }

    /** Elimina una tarea por ID */
    public Task<Void> delete(@NonNull String id) {
        if (id.isEmpty()) {
            throw new IllegalArgumentException("El ID de la tarea no puede ser vacío.");
        }
        return userRef.child(id).removeValue();
    }

    /** Retorna la referencia completa del usuario para escuchas en tiempo real */
    public DatabaseReference ref() {
        return userRef;
    }
}
