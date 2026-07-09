package model;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Implementacion de una Cola FIFO mediante nodos para manejar las reservas.
 */
public class ColaReservas {
    private NodoCola frente;
    
    public ColaReservas() {
        this.frente = null;
    }
    
    /**
     * Agrega una reserva al final de la cola.
     */
    public void encolar(Reserva reserva) {
        NodoCola nuevoNodo = new NodoCola(reserva);
        if (estaVacia()) {
            frente = nuevoNodo;
        } else {
            NodoCola actual = frente;
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }
            actual.siguiente = nuevoNodo;
        }
    }
    
    /**
     * Retira y devuelve la reserva al frente de la cola.
     * @return la reserva desencolada
     * @throws NoSuchElementException si la cola esta vacia
     */
    public Reserva desencolar() {
        if (estaVacia()) {
            throw new NoSuchElementException("La cola esta vacia.");
        }
        Reserva reserva = frente.reserva;
        frente = frente.siguiente;
        return reserva;
    }
    
    /**
     * Verifica si la cola esta vacia.
     */
    public boolean estaVacia() {
        return frente == null;
    }

    /**
     * Convierte la cola a una lista para facilitar su recorrido sin vaciarla
     * ni depender de la interfaz Iterable.
     */
    public List<Reserva> obtenerListaReservas() {
        List<Reserva> lista = new ArrayList<>();
        NodoCola actual = frente;
        while (actual != null) {
            lista.add(actual.reserva);
            actual = actual.siguiente;
        }
        return lista;
    }
}
