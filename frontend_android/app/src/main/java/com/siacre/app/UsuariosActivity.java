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
import com.siacre.modelo.Usuario;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

public class UsuariosActivity extends AppCompatActivity implements UsuarioAdapter.OnUsuarioClickListener {

    private RecyclerView rvUsers;
    private UsuarioAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuarios);

        // --- Navegación inferior ---
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.nav_person);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_person) return true;

            if (id == R.id.nav_home) {
                startActivity(new Intent(this, DashboardActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_portfolio) {
                startActivity(new Intent(this, ClientesActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });

        // --- RecyclerView de usuarios ---
        rvUsers = findViewById(R.id.rvUsers);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));

        cargarUsuarios();

        // --- Botón crear usuario ---
        findViewById(R.id.btnCreateUser).setOnClickListener(v -> mostrarDialogoCrear());
    }

    // Cargar lista de usuarios desde el backend
    private void cargarUsuarios() {
        String url = "http://10.0.2.2:8080/usuarios";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    Type listType = new TypeToken<List<Usuario>>() {}.getType();
                    List<Usuario> lista = new Gson().fromJson(response.toString(), listType);
                    adapter = new UsuarioAdapter(lista, this);
                    rvUsers.setAdapter(adapter);
                },
                error -> Toast.makeText(this, "Error al cargar usuarios", Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(this).add(request);
    }

    // Mostrar diálogo para crear nuevo usuario
    private void mostrarDialogoCrear() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Crear Usuario");

        View viewInflado = LayoutInflater.from(this).inflate(R.layout.dialog_usuario_form, null);
        EditText etNombres = viewInflado.findViewById(R.id.etDialogNombres);
        EditText etApellidos = viewInflado.findViewById(R.id.etDialogApellidos);
        EditText etCorreo = viewInflado.findViewById(R.id.etDialogCorreo);
        EditText etClave = viewInflado.findViewById(R.id.etDialogClave);

        builder.setView(viewInflado);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nombres = etNombres.getText().toString().trim();
            String apellidos = etApellidos.getText().toString().trim();
            String correo = etCorreo.getText().toString().trim();
            String clave = etClave.getText().toString().trim();

            if (nombres.isEmpty() || apellidos.isEmpty() || correo.isEmpty() || clave.isEmpty()) {
                Toast.makeText(UsuariosActivity.this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            crearUsuario(nombres, apellidos, correo, clave);
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    // Método para crear usuario vía POST
    private void crearUsuario(String nombres, String apellidos, String correo, String clave) {
        String url = "http://10.0.2.2:8080/usuarios";
        JSONObject json = new JSONObject();
        try {
            json.put("nombres", nombres);
            json.put("apellidos", apellidos);
            json.put("correo", correo);
            json.put("clave", clave);
            json.put("id_rol", 2); // Rol por defecto (2: usuario normal)
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, json,
                response -> {
                    Toast.makeText(this, "Usuario creado", Toast.LENGTH_SHORT).show();
                    cargarUsuarios(); // Recargar lista
                },
                error -> Toast.makeText(this, "Error al crear usuario", Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(this).add(request);
    }

    // Mostrar diálogo para editar usuario (precargando datos)
    private void mostrarDialogoEditar(Usuario usuario) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar Usuario");

        View viewInflado = LayoutInflater.from(this).inflate(R.layout.dialog_usuario_form, null);
        EditText etNombres = viewInflado.findViewById(R.id.etDialogNombres);
        EditText etApellidos = viewInflado.findViewById(R.id.etDialogApellidos);
        EditText etCorreo = viewInflado.findViewById(R.id.etDialogCorreo);
        EditText etClave = viewInflado.findViewById(R.id.etDialogClave);

        // Precargar datos
        etNombres.setText(usuario.getNombres());
        etApellidos.setText(usuario.getApellidos());
        etCorreo.setText(usuario.getCorreo());
        etClave.setHint("Dejar en blanco para no cambiar");

        builder.setView(viewInflado);

        builder.setPositiveButton("Actualizar", (dialog, which) -> {
            String nombres = etNombres.getText().toString().trim();
            String apellidos = etApellidos.getText().toString().trim();
            String correo = etCorreo.getText().toString().trim();

            if (nombres.isEmpty() || apellidos.isEmpty() || correo.isEmpty()) {
                Toast.makeText(UsuariosActivity.this, "Nombres, apellidos y correo obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }
            String clave = etClave.getText().toString().trim();
            editarUsuario(usuario.getId(), nombres, apellidos, correo, clave);
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    // Método para editar usuario vía PUT
    private void editarUsuario(int id, String nombres, String apellidos, String correo, String clave) {
        String url = "http://10.0.2.2:8080/usuarios/" + id;
        JSONObject json = new JSONObject();
        try {
            json.put("nombres", nombres);
            json.put("apellidos", apellidos);
            json.put("correo", correo);
            if(clave != null && !clave.isEmpty()){
                json.put("clave", clave);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, json,
                response -> {
                    Toast.makeText(this, "Usuario actualizado", Toast.LENGTH_SHORT).show();
                    cargarUsuarios();
                },
                error -> Toast.makeText(this, "Error al actualizar usuario", Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(this).add(request);
    }

    // Método para eliminar usuario vía DELETE (con confirmación)
    private void confirmarEliminar(Usuario usuario) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar usuario")
                .setMessage("¿Estás seguro de eliminar a " + usuario.getNombres() + " " + usuario.getApellidos() + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarUsuario(usuario.getId()))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarUsuario(int id) {
        String url = "http://10.0.2.2:8080/usuarios/" + id;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null,
                response -> {
                    Toast.makeText(this, "Usuario eliminado", Toast.LENGTH_SHORT).show();
                    cargarUsuarios();
                },
                error -> Toast.makeText(this, "Error al eliminar usuario", Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(this).add(request);
    }

    // --- Implementación de la interfaz del adaptador ---
    @Override
    public void onEditarClick(Usuario usuario) {
        mostrarDialogoEditar(usuario);
    }

    @Override
    public void onEliminarClick(Usuario usuario) {
        confirmarEliminar(usuario);
    }
}