package model;

import java.util.Arrays;

/**
 * MODELO
 * Representa una sala de cine y su matriz de asientos.
 * Estructura de datos requerida: MATRIZ (char[][]) para el mapa de butacas.
 * 'O' = asiento libre | 'X' = asiento ocupado
 */
public class Sala {
    private String nombre;
    private char[][] asientos;
    private static final int FILAS = 5;
    private static final int COLUMNAS = 6;

    public Sala(String nombre) {
        this.nombre = nombre;
        this.asientos = new char[FILAS][COLUMNAS];
        for (int i = 0; i < FILAS; i++) Arrays.fill(asientos[i], 'O');
    }

    public String getNombre() { return nombre; }
    public int getFILAS() { return FILAS; }
    public int getCOLUMNAS() { return COLUMNAS; }
    public char getAsiento(int f, int c) { return asientos[f][c]; }
    public boolean verificarAsiento(int f, int c) { return asientos[f][c] == 'O'; }
    public void ocuparAsiento(int f, int c) { asientos[f][c] = 'X'; }

    /** Devuelve estadisticas de ocupacion: [vendidos, disponibles, total] */
    public int[] getEstadisticas() {
        int vendidos = 0;
        for (int i = 0; i < FILAS; i++)
            for (int j = 0; j < COLUMNAS; j++)
                if (asientos[i][j] == 'X') vendidos++;
        return new int[]{ vendidos, FILAS * COLUMNAS - vendidos, FILAS * COLUMNAS };
    }
}
