package com.example.lab6_20211602_iot.ui;


import android.os.Bundle;
import android.view.MenuItem;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import com.example.lab6_20211602_iot.R;
import com.example.lab6_20211602_iot.ui.resumen.ResumenFragment;
import com.example.lab6_20211602_iot.ui.tareas.TareasFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        MaterialToolbar toolbar = findViewById(R.id.topBar);
        toolbar.setOnMenuItemClickListener(this::onToolbarItem);


        BottomNavigationView bottom = findViewById(R.id.bottomNav);
        bottom.setOnItemSelectedListener(item -> {
            Fragment f;
            if (item.getItemId() == R.id.nav_resumen) {
                f = new ResumenFragment();
            } else {
                f = new TareasFragment();
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, f)
                    .commit();
            return true;
        });
        bottom.setSelectedItemId(R.id.nav_tareas);
    }


    private boolean onToolbarItem(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            finish(); // vuelve a LoginActivity por stack
            return true;
        }
        return false;
    }
}