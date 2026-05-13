package com.siacre.app;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NuevoClienteActivity extends AppCompatActivity {

    private int currentStep = 1;
    private LinearLayout layout1, layout2, layout3, layout4;
    private TextView tvTitle;
    private Button btnNext;
    private ImageButton btnBack;

    // Spinners
    private Spinner spTipoDoc, spTipoPersona, spTipoContrato, spSector, spNivelEdu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_cliente);

        // Vincular vistas
        layout1 = findViewById(R.id.layoutStep1);
        layout2 = findViewById(R.id.layoutStep2);
        layout3 = findViewById(R.id.layoutStep3);
        layout4 = findViewById(R.id.layoutStep4);
        tvTitle = findViewById(R.id.tvWizardTitle);
        btnNext = findViewById(R.id.btnNextWizard);
        btnBack = findViewById(R.id.btnBackWizard);

        poblarSpinners();

        btnBack.setOnClickListener(v -> {
            if (currentStep > 1) {
                currentStep--;
                gestionarPasos();
            } else {
                finish();
            }
        });

        btnNext.setOnClickListener(v -> {
            if (currentStep < 4) {
                currentStep++;
                gestionarPasos();
            } else {
                guardarInformacion();
            }
        });
    }

    private void poblarSpinners() {
        String[] docs = {"CC", "TI", "NIT", "CE"};
        String[] personas = {"natural", "juridica"};
        String[] contratos = {"termino fijo", "termino indefinido", "obra labor", "prestacion de servicios"};
        String[] sectores = {"oil", "comercio", "financiero", "mineria", "ingenieria", "agricultrua", "salud", "educacion", "manufactura"};
        String[] estudios = {"sin estudios", "primaria", "secundaria", "pregrado", "posgrado"};

        configurarSpinner(R.id.spTipoDoc, docs);
        configurarSpinner(R.id.spTipoPersona, personas);
        configurarSpinner(R.id.spTipoContrato, contratos);
        configurarSpinner(R.id.spSector, sectores);
        configurarSpinner(R.id.spNivelEdu, estudios);
    }

    private void configurarSpinner(int id, String[] datos) {
        Spinner spinner = findViewById(id);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, datos);
        spinner.setAdapter(adapter);
    }

    private void gestionarPasos() {
        layout1.setVisibility(View.GONE);
        layout2.setVisibility(View.GONE);
        layout3.setVisibility(View.GONE);
        layout4.setVisibility(View.GONE);

        switch (currentStep) {
            case 1:
                layout1.setVisibility(View.VISIBLE);
                tvTitle.setText("Creación de clientes");
                btnNext.setText("Siguiente");
                break;
            case 2:
                layout2.setVisibility(View.VISIBLE);
                tvTitle.setText("Información laboral");
                btnNext.setText("Siguiente");
                break;
            case 3:
                layout3.setVisibility(View.VISIBLE);
                tvTitle.setText("Información financiera Año 1");
                btnNext.setText("Siguiente");
                break;
            case 4:
                layout4.setVisibility(View.VISIBLE);
                tvTitle.setText("Información financiera Año 2");
                btnNext.setText("Finalizar");
                break;
        }
    }

    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private void guardarInformacion() {
        // --- Paso 1: Cliente ---
        Spinner spTipoDoc = findViewById(R.id.spTipoDoc);
        EditText etDocumento = findViewById(R.id.etDocumento);
        EditText etNombres = findViewById(R.id.etNombres);
        EditText etApellidos = findViewById(R.id.etApellidos);
        EditText etTelefono = findViewById(R.id.etTelefono);
        EditText etCorreo = findViewById(R.id.etCorreo);
        EditText etMonto = findViewById(R.id.etMonto);

        // --- Paso 2: Info laboral ---
        Spinner spTipoPersona = findViewById(R.id.spTipoPersona);
        Spinner spTipoContrato = findViewById(R.id.spTipoContrato);
        Spinner spSector = findViewById(R.id.spSector);
        Spinner spNivelEdu = findViewById(R.id.spNivelEdu);
        EditText etFechaInicio = findViewById(R.id.etFechaInicio);

        // --- Paso 3: Año 1 ---
        EditText etActivos1 = findViewById(R.id.etActivos1);
        EditText etActivosNC1 = findViewById(R.id.etActivosNC1);
        EditText etPasivos1 = findViewById(R.id.etPasivos1);
        EditText etPasivosNC1 = findViewById(R.id.etPasivosNC1);
        EditText etPatrimonio1 = findViewById(R.id.etPatrimonio1);
        EditText etIngresos1 = findViewById(R.id.etIngresos1);
        EditText etCostos1 = findViewById(R.id.etCostos1);
        EditText etGastos1 = findViewById(R.id.etGastos1);

        // --- Paso 4: Año 2 ---
        EditText etActivos2 = findViewById(R.id.etActivos2);
        EditText etActivosNC2 = findViewById(R.id.etActivosNC2);
        EditText etPasivos2 = findViewById(R.id.etPasivos2);
        EditText etPasivosNC2 = findViewById(R.id.etPasivosNC2);
        EditText etPatrimonio2 = findViewById(R.id.etPatrimonio2);
        EditText etIngresos2 = findViewById(R.id.etIngresos2);
        EditText etCostos2 = findViewById(R.id.etCostos2);
        EditText etGastos2 = findViewById(R.id.etGastos2);

        // --- Construir datos_evaluacion (JSON con toda la info adicional) ---
        Map<String, String> evaluacion = new HashMap<>();
        evaluacion.put("tipo_persona", spTipoPersona.getSelectedItem().toString());
        evaluacion.put("tipo_contrato", spTipoContrato.getSelectedItem().toString());
        evaluacion.put("sector", spSector.getSelectedItem().toString());
        evaluacion.put("nivel_educativo", spNivelEdu.getSelectedItem().toString());
        evaluacion.put("fecha_inicio", etFechaInicio.getText().toString().trim());

        // Año 1
        evaluacion.put("activos_corrientes_1", etActivos1.getText().toString().trim());
        evaluacion.put("activos_no_corrientes_1", etActivosNC1.getText().toString().trim());
        evaluacion.put("pasivos_corrientes_1", etPasivos1.getText().toString().trim());
        evaluacion.put("pasivos_no_corrientes_1", etPasivosNC1.getText().toString().trim());
        evaluacion.put("patrimonio_1", etPatrimonio1.getText().toString().trim());
        evaluacion.put("ingresos_1", etIngresos1.getText().toString().trim());
        evaluacion.put("costos_1", etCostos1.getText().toString().trim());
        evaluacion.put("gastos_1", etGastos1.getText().toString().trim());

        // Año 2
        evaluacion.put("activos_corrientes_2", etActivos2.getText().toString().trim());
        evaluacion.put("activos_no_corrientes_2", etActivosNC2.getText().toString().trim());
        evaluacion.put("pasivos_corrientes_2", etPasivos2.getText().toString().trim());
        evaluacion.put("pasivos_no_corrientes_2", etPasivosNC2.getText().toString().trim());
        evaluacion.put("patrimonio_2", etPatrimonio2.getText().toString().trim());
        evaluacion.put("ingresos_2", etIngresos2.getText().toString().trim());
        evaluacion.put("costos_2", etCostos2.getText().toString().trim());
        evaluacion.put("gastos_2", etGastos2.getText().toString().trim());

        String datosEvaluacionJson = new Gson().toJson(evaluacion);

        // --- Calcular puntaje simple (basado en año 1) ---
        double ingresos1 = parseDouble(etIngresos1.getText().toString());
        double gastos1 = parseDouble(etGastos1.getText().toString());
        double activos1 = parseDouble(etActivos1.getText().toString());
        double pasivos1 = parseDouble(etPasivos1.getText().toString());

        double score = 0;
        if (ingresos1 + gastos1 > 0) {
            score += (ingresos1 / (ingresos1 + gastos1)) * 500;
        }
        if (activos1 + pasivos1 > 0) {
            score += (activos1 / (activos1 + pasivos1)) * 500;
        }
        score = Math.min(score, 1000);
        score = Math.round(score * 100.0) / 100.0;

        double monto = parseDouble(etMonto.getText().toString());

        // --- Construir JSON para el backend ---
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("tipo_documento", spTipoDoc.getSelectedItem().toString());
            jsonBody.put("documento", Integer.parseInt(etDocumento.getText().toString()));
            jsonBody.put("nombres", etNombres.getText().toString().trim());
            jsonBody.put("apellidos", etApellidos.getText().toString().trim());
            jsonBody.put("correo", etCorreo.getText().toString().trim());
            jsonBody.put("telefono", etTelefono.getText().toString().trim());
            jsonBody.put("id_usuario", 1); // ID temporal del usuario logueado
            jsonBody.put("monto", monto);
            jsonBody.put("datos_evaluacion", datosEvaluacionJson);
            jsonBody.put("puntaje_score", score);
        } catch (JSONException e) {
            e.printStackTrace();
            mostrarAlerta("Error al armar los datos", false);
            return;
        }

        // --- Enviar al servidor ---
        String url = "http://10.0.2.2:8080/clientes/guardar_completo";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    mostrarAlerta("Cliente creado exitosamente", true);
                    new Handler().postDelayed(this::finish, 1500);
                },
                error -> {
                    mostrarAlerta("Error al guardar", false);
                }
        );

        Volley.newRequestQueue(this).add(request);
    }

    // Método para mostrar una alerta flotante superior
    private void mostrarAlerta(String mensaje, boolean esExito) {
        CardView cardNotification = findViewById(R.id.cardNotification);
        LinearLayout layoutNotif = findViewById(R.id.layoutNotif);
        TextView tvNotifMessage = findViewById(R.id.tvNotifMessage);
        ImageView ivNotifIcon = findViewById(R.id.ivNotifIcon);

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