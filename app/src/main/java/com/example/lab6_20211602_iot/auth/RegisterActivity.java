package com.example.lab6_20211602_iot.auth;


import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.example.lab6_20211602_iot.R;
import com.example.lab6_20211602_iot.util.Toaster;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;


public class RegisterActivity extends AppCompatActivity {


    private FirebaseAuth auth;
    private EditText etEmail, etPassword;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        auth = FirebaseAuth.getInstance();
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);


        findViewById(R.id.btnRegister).setOnClickListener(v -> onRegister());
        findViewById(R.id.btnGoLogin).setOnClickListener(v -> finish());
    }


    private void onRegister() {
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
            Toaster.show(this, "Completa correo y contraseña");
            return;
        }
        findViewById(R.id.btnRegister).setEnabled(false);
        auth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(task -> {
                    findViewById(R.id.btnRegister).setEnabled(true);
                    if (task.isSuccessful()) {
                        Toaster.show(this, "Cuenta creada. Ya puedes iniciar sesión");
                        finish();
                    } else {
                        Toaster.show(this, "Error: " + Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
    }
}