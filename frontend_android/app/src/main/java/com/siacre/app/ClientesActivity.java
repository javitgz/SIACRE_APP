package com.siacre.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.siacre.modelo.Cliente;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

public class ClientesActivity extends AppCompatActivity implements ClienteAdapter.OnClienteClickListener {

    private RecyclerView rv;
    private ClienteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientes);

        // --- Navegación ---
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

        // --- RecyclerView ---
        rv = findViewById(R.id.rvClients);
        rv.setLayoutManager(new LinearLayoutManager(this));

        cargarClientes();

        // --- Botón + ---
        findViewById(R.id.btnPlusHeader).setOnClickListener(v -> {
            Intent intent = new Intent(this, NuevoClienteActivity.class);
            startActivity(intent);
        });
    }

    private void cargarClientes() {
        String url = "http://10.0.2.2:8080/clientes";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    Type listType = new TypeToken<List<Cliente>>(){}.getType();
                    List<Cliente> lista = new Gson().fromJson(response.toString(), listType);
                    adapter = new ClienteAdapter(lista, this);
                    rv.setAdapter(adapter);
                },
                error -> Toast.makeText(this, "Error de red", Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(this).add(request);
    }

    // --- Editar cliente ---
    private void mostrarDialogoEditar(Cliente cliente) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar Cliente");

        View viewInflado = LayoutInflater.from(this).inflate(R.layout.dialog_cliente_form, null);
        EditText etNombres = viewInflado.findViewById(R.id.etDialogNombres);
        EditText etApellidos = viewInflado.findViewById(R.id.etDialogApellidos);
        EditText etDocumento = viewInflado.findViewById(R.id.etDialogDocumento);
        EditText etCorreo = viewInflado.findViewById(R.id.etDialogCorreo);
        EditText etTelefono = viewInflado.findViewById(R.id.etDialogTelefono);

        etNombres.setText(cliente.getNombres());
        etApellidos.setText(cliente.getApellidos());
        etDocumento.setText(String.valueOf(cliente.getDocumento()));
        etCorreo.setText(cliente.getCorreo());
        etTelefono.setText(cliente.getTelefono());

        builder.setView(viewInflado);

        builder.setPositiveButton("Actualizar", (dialog, which) -> {
            String nombres = etNombres.getText().toString().trim();
            String apellidos = etApellidos.getText().toString().trim();
            String documento = etDocumento.getText().toString().trim();
            String correo = etCorreo.getText().toString().trim();
            String telefono = etTelefono.getText().toString().trim();

            if (nombres.isEmpty() || apellidos.isEmpty() || documento.isEmpty()) {
                Toast.makeText(this, "Campos obligatorios vacíos", Toast.LENGTH_SHORT).show();
                return;
            }

            editarCliente(cliente.getId(), nombres, apellidos, documento, correo, telefono);
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void editarCliente(int id, String nombres, String apellidos, String documento, String correo, String telefono) {
        String url = "http://10.0.2.2:8080/clientes/" + id;
        JSONObject json = new JSONObject();
        try {
            json.put("nombres", nombres);
            json.put("apellidos", apellidos);
            json.put("documento", Integer.parseInt(documento));
            json.put("correo", correo);
            json.put("telefono", telefono);
            json.put("tipo_documento", "CC"); // Simplificado, luego puedes mejorarlo
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, json,
                response -> {
                    Toast.makeText(this, "Cliente actualizado", Toast.LENGTH_SHORT).show();
                    cargarClientes();
                },
                error -> Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(this).add(request);
    }

    // --- Eliminar cliente ---
    private void confirmarEliminar(Cliente cliente) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar cliente")
                .setMessage("¿Estás seguro de eliminar a " + cliente.getNombres() + " " + cliente.getApellidos() + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarCliente(cliente.getId()))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarCliente(int id) {
        String url = "http://10.0.2.2:8080/clientes/" + id;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null,
                response -> {
                    Toast.makeText(this, "Cliente eliminado", Toast.LENGTH_SHORT).show();
                    cargarClientes();
                },
                error -> Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(this).add(request);
    }

    // --- Implementación de la interfaz ---
    @Override
    public void onEditarClick(Cliente cliente) {
        mostrarDialogoEditar(cliente);
    }

    @Override
    public void onEliminarClick(Cliente cliente) {
        confirmarEliminar(cliente);
    }
}