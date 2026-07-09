package model;

/**
 * Nodo para la Cola de Reservas.
 * Contiene la informacion de la reserva y el enlace al siguiente nodo.
 */
public class NodoCola {
    public Reserva reserva;
    public NodoCola siguiente;

    public NodoCola(Reserva reserva) {
        this.reserva = reserva;
        this.siguiente = null;
    }
}
