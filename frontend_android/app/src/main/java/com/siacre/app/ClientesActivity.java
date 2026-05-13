package com.siacre.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.siacre.modelo.Cliente;
import java.lang.reflect.Type;
import java.util.List;

public class ClientesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientes);

        // --- LÓGICA DE NAVEGACIÓN ---
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.nav_portfolio);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_portfolio) return true;

            if (id == R.id.nav_home) {
                startActivity(new Intent(this, DashboardActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_person) {
                startActivity(new Intent(this, UsuariosActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });

        // --- LÓGICA DE RECYCLERVIEW ---
        RecyclerView rv = findViewById(R.id.rvClients);
        rv.setLayoutManager(new LinearLayoutManager(this));

        String url = "http://10.0.2.2:8080/clientes";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        Type listType = new TypeToken<List<Cliente>>(){}.getType();
                        List<Cliente> lista = new Gson().fromJson(response.toString(), listType);
                        rv.setAdapter(new ClienteAdapter(lista));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Error de red", Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(this).add(request);

        // Dentro del onCreate de ClientesActivity.java
        findViewById(R.id.btnPlusHeader).setOnClickListener(v -> {
            Intent intent = new Intent(this, NuevoClienteActivity.class);
            startActivity(intent);
        });
    }
}