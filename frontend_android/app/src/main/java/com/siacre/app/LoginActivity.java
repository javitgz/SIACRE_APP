package com.siacre.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private EditText etCorreo, etPassword;
    private Button btnLogin;
    private CardView cardNotification;
    private LinearLayout layoutNotif;
    private TextView tvNotifMessage;
    private ImageView ivNotifIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializar vistas
        etCorreo = findViewById(R.id.etCorreo);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        cardNotification = findViewById(R.id.cardNotification);
        layoutNotif = findViewById(R.id.layoutNotif);
        tvNotifMessage = findViewById(R.id.tvNotifMessage);
        ivNotifIcon = findViewById(R.id.ivNotifIcon);

        btnLogin.setOnClickListener(v -> ejecutarLogin());
    }

    private void ejecutarLogin() {
        String url = "http://10.0.2.2:8080/login";
        JSONObject params = new JSONObject();
        try {
            params.put("correo", etCorreo.getText().toString());
            params.put("clave", etPassword.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, params,
                response -> {
                    mostrarAlerta("Bienvenido Administrador", true);
                    // Esperar 1.5 segundos para que vea el mensaje verde antes de pasar
                    new Handler().postDelayed(() -> {
                        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                        startActivity(intent);
                        finish();
                    }, 1500);
                },
                error -> {
                    mostrarAlerta("¡Error al ingresar!", false);
                }
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void mostrarAlerta(String mensaje, boolean esExito) {
        tvNotifMessage.setText(mensaje);
        if (esExito) {
            layoutNotif.setBackgroundColor(Color.parseColor("#4CAF50")); // Verde
            ivNotifIcon.setImageResource(android.R.drawable.ic_dialog_info);
        } else {
            layoutNotif.setBackgroundColor(Color.parseColor("#D32F2F")); // Rojo
            ivNotifIcon.setImageResource(android.R.drawable.ic_dialog_alert);
        }

        cardNotification.setVisibility(View.VISIBLE);

        // Desaparecer automáticamente después de 3 segundos
        new Handler().postDelayed(() -> cardNotification.setVisibility(View.GONE), 3000);
    }
}