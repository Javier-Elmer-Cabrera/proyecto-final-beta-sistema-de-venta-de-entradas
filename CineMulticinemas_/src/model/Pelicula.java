package model;

/**
 * MODELO
 * Representa una pelicula en cartelera, con su sala asociada.
 */
public class Pelicula {
    public int id;
    public String titulo, genero, horario;
    public Sala sala;

    public Pelicula(int id, String titulo, String genero, String horario, String nombreSala) {
        this.id = id;
        this.titulo = titulo;
        this.genero = genero;
        this.horario = horario;
        this.sala = new Sala(nombreSala);
    }

    /**
     * Genera la clave compuesta usada por el BST para ordenar:
     * genero|horario|sala (orden lexicografico).
     */
    public String getClaveBST() {
        return genero + "|" + horario + "|" + sala.getNombre();
    }

    @Override
    public String toString() {
        return "[" + id + "] " + titulo + " | " + genero + " | " + horario + " | " + sala.getNombre();
    }
}
