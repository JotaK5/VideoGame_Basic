package com.example.videogame_basic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.util.Log;

// Imports para .gif
import pl.droidsonroids.gif.GifDrawable;
import java.io.IOException;

import android.graphics.Path;

// Imports para formas hitbox
import androidx.annotation.NonNull;

import android.graphics.Region;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Juego extends View {
    public int ancho, alto;
    public int posX, posY, radio;
    private final Random random = new Random();
    public List<RectF> navesEnemigas = new ArrayList<>();
    public int velocidadEnemiga = 5;
    private final Paint paintNaveJugador = new Paint();
    private final Paint paintNavesEnemigas = new Paint();

    private Bitmap naveEnemigaPng;

    // Variables Proyectiles
    private final List<Proyectil> proyectiles = new ArrayList<>();
    private final Paint paintProyectil = new Paint();

    // Variables Puntos
    private final Paint paintPuntos = new Paint();

    private boolean isGameOver = false;
    private boolean haDisparado = false;
    private GifDrawable naveJugadorGif;

    public void setGameOver(boolean gameOver) {
        isGameOver = gameOver;
        invalidate(); // Refrescar la pantalla para aplicar los cambios
    }

    private final long startTime;

    public Juego(Context context) {
        super(context);
        init();
        startTime = System.currentTimeMillis(); // Iniciar el contador de tiempo
    }

    public Juego(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        startTime = System.currentTimeMillis(); // Iniciar el contador de tiempo
    }

    public Juego(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        startTime = System.currentTimeMillis(); // Iniciar el contador de tiempo
    }

    private void init() {
        paintNaveJugador.setColor(Color.YELLOW);
        paintNaveJugador.setStyle(Paint.Style.FILL_AND_STROKE);

        naveEnemigaPng = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.nave_enemigo), 56, 96, true);
        paintNavesEnemigas.setColor(Color.RED);
        paintNavesEnemigas.setStyle(Paint.Style.FILL_AND_STROKE);

        paintProyectil.setColor(Color.GREEN); // Asegúrate de que este color es visible en el fondo
        paintProyectil.setStyle(Paint.Style.FILL);

        paintPuntos.setColor(Color.WHITE);
        paintPuntos.setTextAlign(Paint.Align.RIGHT);
        paintPuntos.setTextSize(100);

        try {
            naveJugadorGif = new GifDrawable(getResources(), R.drawable.nave_jugador);
        } catch (IOException e) {
            Log.e("Juego", "Error al cargar el GIF de la nave del jugador", e);
        }

        posX = 200; // Ajusta este valor según sea necesario
    }


    public void disparar() {
        // Log.d("Juego", "Disparar llamado");
        int offsetX = 230; // Ajuste para alinear con la nueva posición de la nave del jugador
        Proyectil proyectil = new Proyectil(posX - offsetX, posY, getContext());
        proyectiles.add(proyectil);
        // Log.d("Juego", "Proyectiles actuales: " + proyectiles.size());
    }


    private void generarNaveEnemiga() {
        int enemigoX = 0;
        int enemigoY = random.nextInt(alto);
        // Ajustar la posición del rectángulo para que el círculo se mueva hacia arriba
        RectF nuevaNave = new RectF(enemigoX + 50, enemigoY - 55, enemigoX + 100, enemigoY + 5);
        navesEnemigas.add(nuevaNave);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isGameOver) {
            // Reiniciar el juego si se toca la pantalla en estado de Game Over
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                ((MainActivity) getContext()).restartGame();
                return true;
            }
        } else {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // Disparar ya no se realiza aquí
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                case MotionEvent.ACTION_MOVE:
                    posY = (int) event.getY();
                    this.invalidate(); // Refrescar la pantalla
                    break;
            }
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_A) {
                if (!haDisparado) {
                    haDisparado = true;
                    Log.d("Sí", "Barra espaciadora pulsada");
                    disparar(); // Disparar cuando se presiona la tecla Espacio
                }
            }
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    posY -= 10;
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    posY += 10;
                    break;
            }
        }

        invalidate();
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_A) {
            Log.d("Sí", "Barra espaciadora soltada");
            haDisparado = false;
        }
        return true;
    }

    private boolean detectarColisionTriangulo(RectF naveEnemiga, Path pathTriangulo) {
        Region regionNave = new Region();
        regionNave.setPath(pathTriangulo, new Region((int)naveEnemiga.left, (int)naveEnemiga.top, (int)naveEnemiga.right, (int)naveEnemiga.bottom));

        return !regionNave.isEmpty();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        // Log.d("Juego", "onDraw llamado");
        super.onDraw(canvas);

        if (isGameOver) {
            Paint textPaint = new Paint();
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setTextSize(200);
            textPaint.setColor(Color.RED); // Cambiar el color del texto a rojo
            canvas.drawText("Game Over", (float) ancho / 2, (float) alto / 2, textPaint);
            return;
        }

        // Dibujar el contador de tiempo solo si el juego no ha terminado
        long currentTime = System.currentTimeMillis();
        int segundos = (int) ((currentTime - startTime) / 1000);
        @SuppressLint("DefaultLocale") String tiempo = String.format("%02d:%02d", segundos / 60, segundos % 60);
        Paint segundosPaint = new Paint();
        segundosPaint.setTextAlign(Paint.Align.CENTER);
        segundosPaint.setTextSize(60);
        segundosPaint.setColor(Color.WHITE);
        canvas.drawText(tiempo, (float) ancho / 2, 100, segundosPaint);

        // Dibujar la nave del jugador
        int nuevoRadio = radio * 2;
        int offsetX = 200;
        naveJugadorGif.setBounds(posX - nuevoRadio - offsetX, posY - nuevoRadio, posX + nuevoRadio - offsetX, posY + nuevoRadio);
        naveJugadorGif.draw(canvas);

        // Dibujar los enemigos y detectar colisiones
        for (int i = 0; i < navesEnemigas.size(); i++) {
            RectF nave = navesEnemigas.get(i);
            nave.offset(velocidadEnemiga, 0);
            if (nave.left > ancho) {
                navesEnemigas.remove(i);
                i--;
            } else {
                canvas.drawBitmap(naveEnemigaPng, nave.left, nave.top - naveEnemigaPng.getHeight() / 6f, null);
            }

            // Detectar colisión utilizando la nueva hitbox triangular
            Path pathTriangulo = new Path();
            pathTriangulo.moveTo(posX - radio * 1f - offsetX, posY);
            pathTriangulo.lineTo(posX + radio * 0.8f - offsetX, posY + radio * 1.4f);
            pathTriangulo.lineTo(posX + radio * 0.8f - offsetX, posY - radio * 1.4f);
            pathTriangulo.close();

            // Dibujar Hitbox triangulo nave jugador
//            Paint paintTriangulo = new Paint();
//            paintTriangulo.setStyle(Paint.Style.STROKE);
//            paintTriangulo.setColor(Color.RED);
//            canvas.drawPath(pathTriangulo, paintTriangulo);

            if (detectarColisionTriangulo(nave, pathTriangulo)) {
                setGameOver(true); // Establecer estado de "Game Over"
                return;
            }
        }

        if (random.nextInt(100) < 5) {
            generarNaveEnemiga();
        }

        // Actualizar y dibujar los proyectiles al final
        for (int i = 0; i < proyectiles.size(); i++) {
            Proyectil proyectil = proyectiles.get(i);
            proyectil.mover();
            // Log.d("Juego", "Dibujando proyectil en x: " + proyectil.x + " y: " + proyectil.y); // Log para verificar
            proyectil.dibujar(canvas);

            // Detectar colisiones entre proyectiles y enemigos
            for (int j = 0; j < navesEnemigas.size(); j++) {
                RectF nave = navesEnemigas.get(j);
                if (RectF.intersects(nave, new RectF(proyectil.x - 20, proyectil.y - 20, proyectil.x + 20, proyectil.y + 20))) {
                    // Eliminar la nave enemiga y el proyectil al detectar una colisión
                    navesEnemigas.remove(j);
                    proyectiles.remove(i);
                    i--;
                    break;
                }
            }

            // Eliminar el proyectil si sale de la pantalla
            if (proyectil.x < 0 || proyectil.x > ancho) {
                proyectiles.remove(i);
                i--;
            }
        }

        // Dibujar la puntuación
        int puntuacion = 0;
        canvas.drawText(Integer.toString(puntuacion), 150, 150, paintPuntos);
    }
}


// Dibujar la hitbox triangular (opcional, solo para depuración)
//        Paint paintTriangulo = new Paint();
//        paintTriangulo.setStyle(Paint.Style.STROKE);
//        paintTriangulo.setColor(Color.RED);
//        canvas.drawPath(pathTriangulo, paintTriangulo);