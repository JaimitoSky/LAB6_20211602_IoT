package com.example.lab6_20211602_iot.auth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lab6_20211602_iot.R;
import com.example.lab6_20211602_iot.ui.MainActivity;
import com.example.lab6_20211602_iot.util.Toaster;

// GOOGLE
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import com.facebook.FacebookSdk;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.auth.FacebookAuthProvider;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText etEmail, etPassword;
    private GoogleSignInClient googleClient;

    // FACEBOOK (DESACTIVADO)
    // private CallbackManager callbackManager;

    private final ActivityResultLauncher<Intent> googleLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getData() == null) return;
                try {
                    GoogleSignInAccount account =
                            GoogleSignIn.getSignedInAccountFromIntent(result.getData())
                                    .getResult(ApiException.class);
                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    Toaster.show(this, "Error en Google Sign-In");
                }
            });

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        findViewById(R.id.btnLogin).setOnClickListener(this::onLogin);
        findViewById(R.id.btnGoRegister).setOnClickListener(
                v -> startActivity(new Intent(this, RegisterActivity.class)));

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleClient = GoogleSignIn.getClient(this, gso);
        findViewById(R.id.btnGoogle).setOnClickListener(
                v -> googleLauncher.launch(googleClient.getSignInIntent()));

        View btnFacebook = findViewById(R.id.btnFacebook);
        if (btnFacebook != null) {
            // Botón oculto
            btnFacebook.setVisibility(View.GONE);
        }

        // // FACEBOOK (DESACTIVADO):
        // if (!FacebookSdk.isInitialized()) {
        //     FacebookSdk.setApplicationId(getString(R.string.facebook_app_id));
        //     FacebookSdk.setAutoInitEnabled(true);
        //     FacebookSdk.sdkInitialize(getApplicationContext());
        //     FacebookSdk.fullyInitialize();
        // }
        //
        // callbackManager = CallbackManager.Factory.create();
        // findViewById(R.id.btnFacebook).setOnClickListener(v ->
        //         LoginManager.getInstance().logInWithReadPermissions(
        //                 this, Arrays.asList("email", "public_profile")));
        //
        // LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
        //     @Override public void onSuccess(LoginResult loginResult) {
        //         handleFacebookAccessToken(loginResult.getAccessToken());
        //     }
        //     @Override public void onCancel() {}
        //     @Override public void onError(FacebookException error) {
        //         Toaster.show(LoginActivity.this, "Error en Facebook Login");
        //     }
        // });
    }

    //
    // @Override
    // protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    //     super.onActivityResult(requestCode, resultCode, data);
    //     if (callbackManager != null) {
    //         callbackManager.onActivityResult(requestCode, resultCode, data);
    //     }
    // }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser current = auth.getCurrentUser();
        if (current != null) goToMain();
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
                    if (task.isSuccessful()) goToMain();
                    else Toaster.show(this, "Error: " + task.getException().getMessage());
                });
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnSuccessListener(r -> goToMain())
                .addOnFailureListener(e -> Toaster.show(this, "Error: " + e.getMessage()));
    }

    //
    // private void handleFacebookAccessToken(AccessToken token) {
    //     AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
    //     auth.signInWithCredential(credential)
    //             .addOnSuccessListener(r -> goToMain())
    //             .addOnFailureListener(e -> Toaster.show(this, "Error: " + e.getMessage()));
    // }

    private void goToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
