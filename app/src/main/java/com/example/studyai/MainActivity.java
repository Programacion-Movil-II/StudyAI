
package com.example.studyai;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.*;

import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    EditText etPregunta;
    Button btnEnviar;
    LinearLayout layoutChat;
    ScrollView scrollChat;
    ProgressBar progressBar;

    GeminiApiService api;

    String modelo = "gemini-3.5-flash-lite";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Conectar Java con XML
        etPregunta = findViewById(R.id.etPregunta);
        btnEnviar = findViewById(R.id.btnEnviar);
        layoutChat = findViewById(R.id.layoutChat);
        scrollChat = findViewById(R.id.scrollChat);
        progressBar = findViewById(R.id.progressBar);

        // Configurar Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://generativelanguage.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(GeminiApiService.class);

        btnEnviar.setOnClickListener(v -> enviarMensaje());
    }

    // Enviar pregunta del estudiante
    private void enviarMensaje() {

        String pregunta = etPregunta.getText().toString().trim();

        if (pregunta.isEmpty()) {
            etPregunta.setError("Escribe una pregunta");
            return;
        }

        agregarMensaje(pregunta, true);
        etPregunta.setText("");

        consultarGemini(pregunta);
    }

    // Consumir el servicio web de Gemini
    private void consultarGemini(String pregunta) {

        progressBar.setVisibility(View.VISIBLE);
        btnEnviar.setEnabled(false);

        // Crear JSON de la petición
        JsonObject parte = new JsonObject();
        parte.addProperty("text",
                "Eres StudyAI, un asistente educativo. " +
                        "Responde en español de manera clara y sencilla. " +
                        "Pregunta: " + pregunta);

        JsonArray partes = new JsonArray();
        partes.add(parte);

        JsonObject contenido = new JsonObject();
        contenido.add("parts", partes);

        JsonArray contenidos = new JsonArray();
        contenidos.add(contenido);

        JsonObject solicitud = new JsonObject();
        solicitud.add("contents", contenidos);

        // Petición HTTP POST
        api.generarRespuesta(
                modelo,
                BuildConfig.GEMINI_API_KEY,
                solicitud
        ).enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(
                    Call<JsonObject> call,
                    Response<JsonObject> response) {

                terminarCarga();

                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String respuesta = response.body()
                                .getAsJsonArray("candidates")
                                .get(0).getAsJsonObject()
                                .getAsJsonObject("content")
                                .getAsJsonArray("parts")
                                .get(0).getAsJsonObject()
                                .get("text").getAsString();

                        agregarMensaje(respuesta, false);

                    } catch (Exception e) {
                        agregarMensaje(
                                "No se pudo leer la respuesta.",
                                false);
                    }
                } else {
                    agregarMensaje(
                            "Error HTTP " + response.code(),
                            false);
                }
            }

            @Override
            public void onFailure(
                    Call<JsonObject> call, Throwable t) {

                terminarCarga();
                agregarMensaje(
                        "Error de conexión: " + t.getMessage(),
                        false);
            }
        });
    }

    // Ocultar indicador de carga
    private void terminarCarga() {
        progressBar.setVisibility(View.GONE);
        btnEnviar.setEnabled(true);
    }

    // Mostrar mensajes en el chat
    private void agregarMensaje(String texto, boolean usuario) {

        TextView mensaje = new TextView(this);
        mensaje.setText(texto);
        mensaje.setTextSize(16);
        mensaje.setPadding(32, 24, 32, 24);
        mensaje.setMaxWidth(
                (int) (getResources().getDisplayMetrics()
                        .widthPixels * 0.8));

        // Color de la burbuja
        GradientDrawable fondo = new GradientDrawable();
        fondo.setCornerRadius(32);
        fondo.setColor(Color.parseColor(
                usuario ? "#7149C6" : "#FFFFFF"));

        mensaje.setTextColor(Color.parseColor(
                usuario ? "#FFFFFF" : "#24183B"));

        mensaje.setBackground(fondo);

        // Posición del mensaje
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        -2, -2);

        params.gravity = usuario ? Gravity.END : Gravity.START;
        params.setMargins(0, 8, 0, 16);

        layoutChat.addView(mensaje, params);

        // Desplazar hacia el último mensaje
        scrollChat.post(() ->
                scrollChat.fullScroll(ScrollView.FOCUS_DOWN));
    }
}
