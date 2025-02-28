package com.example.videogame_basic;

import android.content.Context;
import android.graphics.Canvas;
import pl.droidsonroids.gif.GifDrawable;
import android.util.Log;
import java.io.IOException;

public class Proyectil {
    public float x;
    public float y;
    public float velocidad;
    private GifDrawable gifDisparo;

    public Proyectil(float x, float y, Context context) {
        this.x = x;
        this.y = y;
        this.velocidad = 15; // Ajusta la velocidad del proyectil según sea necesario
        try {
            gifDisparo = new GifDrawable(context.getResources(), R.drawable.proyectil_jugador);
        } catch (IOException e) {
            Log.e("Proyectil", "Error al cargar el GIF del proyectil", e);
        }
    }

    public void mover() {
        x -= velocidad; // Cambia la dirección para moverse hacia la izquierda
    }

    public void dibujar(Canvas canvas) {
        if (gifDisparo != null) {
            gifDisparo.setBounds((int) (x - 100), (int) (y - 100), (int) (x + 100), (int) (y + 100));
            gifDisparo.draw(canvas);
        } else {
            Log.d("Proyectil", "GIF de disparo no cargado");
        }
    }
}
