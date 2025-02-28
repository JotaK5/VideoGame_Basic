package com.example.videogame_basic;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;

import android.widget.ImageButton;

// Importar MediaPlayer y SoundPool
import android.media.MediaPlayer;
import android.media.AudioAttributes;
import android.media.SoundPool;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    public Juego juego;
    private final Handler handler = new Handler();
    private boolean isGamePaused = false; // Variable para el estado de pausa

    public Button pauseButton; // Declarar pauseButton
    private Button resumeButton;
    private Button restartButton;
    private Button homeButton; // Añadir homeButton

    private MediaPlayer mediaPlayer;
    private SoundPool soundPool;
    public int explosionSoundId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        setContentView(R.layout.activity_main);
        juego = findViewById(R.id.Pantalla);
        ViewTreeObserver obs = juego.getViewTreeObserver();
        obs.addOnGlobalLayoutListener(() -> {
            // Sólo se puede averiguar el ancho y alto una vez ya se ha pintado el layout. Por eso se calcula en este listener
            juego.ancho = juego.getWidth();
            juego.alto = juego.getHeight();
            juego.posX = juego.ancho - 100; // Ajuste para el movimiento solo vertical
            juego.posY = juego.alto / 2;
            juego.radio = 50;
        });

        // Configurar el escuchador de teclas
        juego.setFocusableInTouchMode(true);
        juego.requestFocus();

        // Crear el objeto MediaPlayer y asignar el archivo de música
        mediaPlayer = MediaPlayer.create(this, R.raw.game_background_music);
        mediaPlayer.setLooping(true); // Repetir la música indefinidamente
        mediaPlayer.start(); // Iniciar la música


        // Inicializar SoundPool
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(10)
                .setAudioAttributes(audioAttributes)
                .build();

        explosionSoundId = soundPool.load(this, R.raw.explosion_nave_enemiga, 1);

        // Inicializar los botones de pausa, reanudar, reiniciar y home
        pauseButton = findViewById(R.id.pause_button); // Inicializar pauseButton
        resumeButton = findViewById(R.id.resume_button);
        restartButton = findViewById(R.id.restart_button);
        homeButton = findViewById(R.id.home_button);

        // Configurar el botón de pausa
        pauseButton.setOnClickListener(v -> pauseGame());

        // Configurar el botón de reanudar
        resumeButton.setOnClickListener(v -> resumeGame());

        // Configurar el botón de reiniciar
        restartButton.setOnClickListener(v -> restartGame());

        // Configurar el botón de home
        homeButton.setOnClickListener(v -> {
            // Detener la música antes de ir a la pantalla principal
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            goHome();
        });

        resumeButton.setVisibility(View.GONE); // Ocultar el botón de reanudar al inicio
        restartButton.setVisibility(View.GONE); // Ocultar el botón de reiniciar al inicio
        homeButton.setVisibility(View.GONE); // Ocultar el botón de home al inicio

        // Inicializar el botón de disparo
        ImageButton botonDisparar = findViewById(R.id.boton_disparar);
        botonDisparar.setOnClickListener(v -> juego.disparar());

        startGameLoop(); // Iniciar el bucle del juego

        // Ejecutamos cada 20 milisegundos
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> {
                    if (!isGamePaused) {
                        juego.invalidate();
                    }
                });
            }
        }, 0, 20);

        // Incrementar la velocidad de las naves enemigas cada 5 segundos
        TimerTask incrementarVelocidad = new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> {
                    if (!isGamePaused) {
                        juego.velocidadEnemiga += 1; // Incrementar la velocidad
                    }
                });
            }
        };
        timer.schedule(incrementarVelocidad, 0, 5000); // Incrementar cada 5 segundos
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release(); // Liberar recursos del MediaPlayer
            mediaPlayer = null;
        }
        if (soundPool != null) {
            soundPool.release(); // Liberar recursos del SoundPool
            soundPool = null;
        }
    }


    // Metodo para pausar el juego
    public void pauseGame() {
        isGamePaused = true;
        handler.removeCallbacksAndMessages(null);
        pauseButton.setVisibility(View.GONE);
        resumeButton.setVisibility(View.VISIBLE);
        restartButton.setVisibility(View.VISIBLE);
        homeButton.setVisibility(View.VISIBLE);
    }

    // Metodo para reanudar el juego
    private void resumeGame() {
        isGamePaused = false;
        pauseButton.setVisibility(View.VISIBLE);
        resumeButton.setVisibility(View.GONE);
        restartButton.setVisibility(View.GONE);
        homeButton.setVisibility(View.GONE);
        startGameLoop();
    }

    // Metodo para reiniciar el juego
    public void restartGame() {
        pauseButton.setVisibility(View.VISIBLE);
        @SuppressLint("UnsafeIntentLaunch") Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    // Metodo para ir a la pantalla principal
    private void goHome() {
        Intent homeIntent = new Intent(this, MainMenuActivity.class); // Cambiar a MainActivity
        startActivity(homeIntent);
        finish();
    }

    // Metodo para iniciar el bucle del juego
    private void startGameLoop() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!isGamePaused) {
                    juego.invalidate();
                    handler.postDelayed(this, 20);
                }
            }
        });
    }
}
