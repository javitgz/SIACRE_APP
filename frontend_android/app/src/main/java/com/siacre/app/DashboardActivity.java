package com.siacre.app;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);

        // Quita el filtro de color para ver iconos originales si lo deseas
        bottomNav.setItemIconTintList(null);
        bottomNav.setSelectedItemId(R.id.nav_home);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) return true;

            if (id == R.id.nav_portfolio) {
                startActivity(new Intent(this, ClientesActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_person) {
                startActivity(new Intent(this, UsuariosActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }
}