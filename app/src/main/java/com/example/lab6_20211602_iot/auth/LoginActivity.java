package com.example.lab6_20211602_iot.auth;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.example.lab6_20211602_iot.R;
import com.example.lab6_20211602_iot.ui.MainActivity;
import com.example.lab6_20211602_iot.util.Toaster;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {


    private FirebaseAuth auth;
    private EditText etEmail, etPassword;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        auth = FirebaseAuth.getInstance();
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);


        findViewById(R.id.btnLogin).setOnClickListener(this::onLogin);
        findViewById(R.id.btnGoRegister).setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser current = auth.getCurrentUser();
        if (current != null) {
            goToMain();
        }
    }


    private void onLogin(View v) {
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
            Toaster.show(this, "Ingresa correo y contraseña");
            return;
        }
        findViewById(R.id.btnLogin).setEnabled(false);
        auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(task -> {
                    findViewById(R.id.btnLogin).setEnabled(true);
                    if (task.isSuccessful()) {
                        goToMain();
                    } else {
                        Toaster.show(this, "Error: " + task.getException().getMessage());
                    }
                });
    }


    private void goToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}