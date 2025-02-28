package com.example.videogame_basic;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MainMenuActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        setContentView(R.layout.activity_main_menu);

        // Crear el objeto MediaPlayer y asignar el archivo de música
        mediaPlayer = MediaPlayer.create(this, R.raw.background_menu_music);
        mediaPlayer.setLooping(true); // Repetir la música indefinidamente
        mediaPlayer.start(); // Iniciar la música

        ImageView playImage = findViewById(R.id.img_play);
        playImage.setOnClickListener(v -> {
            // Detener la música antes de iniciar la nueva actividad
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }

            // Iniciar la actividad del juego
            Intent intent = new Intent(MainMenuActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release(); // Liberar recursos del MediaPlayer
            mediaPlayer = null;
        }
    }
}
