package model;

/**
 * MODELO
 * Representa una reserva de asiento realizada por un cliente.
 * Las instancias de esta clase son los elementos que circulan por
 * la COLA (Queue) de confirmacion de pagos.
 */
public class Reserva {
    public String cliente;
    public Pelicula pelicula;
    public int fila, columna;
    public String estado;

    public Reserva(String cliente, Pelicula pelicula, int fila, int columna) {
        this.cliente = cliente;
        this.pelicula = pelicula;
        this.fila = fila;
        this.columna = columna;
        this.estado = "PENDIENTE";
    }
}
