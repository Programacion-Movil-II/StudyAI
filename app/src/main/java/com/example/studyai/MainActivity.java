
package com.example.studyai;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Componentes de la interfaz
    private EditText etPregunta;
    private Button btnEnviar;
    private LinearLayout layoutChat;
    private ScrollView scrollChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Conectar los componentes Java con el XML
        etPregunta = findViewById(R.id.etPregunta);
        btnEnviar = findViewById(R.id.btnEnviar);
        layoutChat = findViewById(R.id.layoutChat);
        scrollChat = findViewById(R.id.scrollChat);

        // Evento del botón Enviar
        btnEnviar.setOnClickListener(v -> enviarMensaje());
    }

    // Enviar el mensaje escrito por el usuario
    private void enviarMensaje() {

        String pregunta = etPregunta.getText()
                .toString()
                .trim();

        // Validar que el mensaje no esté vacío
        if (pregunta.isEmpty()) {
            etPregunta.setError("Escribe un mensaje");
            return;
        }

        // Mostrar la pregunta en el chat
        agregarMensaje(pregunta, true);

        // Limpiar el campo de texto
        etPregunta.setText("");

        // Respuesta temporal de prueba
        // Después conectaremos aquí la API de Gemini
        agregarMensaje(
                "¡Hola! Recibí tu pregunta: " + pregunta
                        + "\n\nPronto podré responderte usando Gemini.",
                false
        );
    }

    // Agregar mensajes al chat
    private void agregarMensaje(String mensaje, boolean esUsuario) {

        // Crear un TextView para el mensaje
        TextView tvMensaje = new TextView(this);

        tvMensaje.setText(mensaje);
        tvMensaje.setTextSize(16);
        tvMensaje.setPadding(32, 24, 32, 24);
        tvMensaje.setMaxWidth(
                (int) (getResources().getDisplayMetrics().widthPixels * 0.80f)
        );

        // Crear el fondo redondeado del mensaje
        GradientDrawable fondo = new GradientDrawable();
        fondo.setCornerRadius(32);

        if (esUsuario) {

            // Mensaje del usuario: violeta
            fondo.setColor(Color.parseColor("#7149C6"));
            tvMensaje.setTextColor(Color.WHITE);

        } else {

            // Mensaje de StudyAI: blanco
            fondo.setColor(Color.WHITE);
            tvMensaje.setTextColor(Color.parseColor("#24183B"));
        }

        tvMensaje.setBackground(fondo);

        // Configurar posición y márgenes
        LinearLayout.LayoutParams parametros =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        parametros.setMargins(0, 8, 0, 16);

        if (esUsuario) {
            parametros.gravity = Gravity.END;
        } else {
            parametros.gravity = Gravity.START;
        }

        tvMensaje.setLayoutParams(parametros);

        // Agregar el mensaje al contenedor
        layoutChat.addView(tvMensaje);

        // Desplazar el chat hacia el último mensaje
        scrollChat.post(() ->
                scrollChat.fullScroll(ScrollView.FOCUS_DOWN)
        );
    }
}
