package controller;

import model.ArbolPeliculas;
import model.Pelicula;
import model.Reserva;
import model.ColaReservas;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * CONTROLADOR
 * Contiene toda la logica de negocio del sistema de venta de entradas
 * y hace de puente entre el MODELO (estructuras de datos) y la VISTA
 * (interfaz Swing). No conoce detalles de Swing: solo expone datos y
 * operaciones que la vista puede invocar y mostrar.
 *
 * Estructuras de datos administradas aqui:
 *  - ArbolPeliculas (arbol BST)  -> Opcion 1: Consultar peliculas
 *  - ColaReservas (cola FIFO)    -> Opciones 2 y 3: Reservar / Confirmar compra
 *  - char[][] dentro de Sala     -> Opcion 2: Matriz de asientos
 */
public class Controlador {

    private final ArbolPeliculas cartelera    = new ArbolPeliculas();
    private final ColaReservas colaReservas   = new ColaReservas();
    private final List<Reserva>  historial    = new ArrayList<>();

    // Estado de la operacion de reserva en curso
    private Pelicula peliculaSeleccionada;
    private int      filaSeleccionada = -1;
    private int      colSeleccionada  = -1;

    public Controlador() {
        inicializarCartelera();
    }

    /** Carga datos iniciales de ejemplo en el arbol de peliculas. */
    private void inicializarCartelera() {
        cartelera.insertar(new Pelicula(1, "Rapidos y Furiosos", "Acción", "18:00", "Sala A"));
        cartelera.insertar(new Pelicula(2, "Mision Imposible", "Acción", "20:30", "Sala B"));
        cartelera.insertar(new Pelicula(3, "El Origen", "Ciencia Ficción", "15:00", "Sala C"));
        cartelera.insertar(new Pelicula(4, "Interestelar", "Ciencia Ficción", "17:30", "Sala A"));
        cartelera.insertar(new Pelicula(5, "El Conjuro", "Terror", "21:00", "Sala D"));
        cartelera.insertar(new Pelicula(6, "La Monja", "Terror", "22:30", "Sala C"));
        cartelera.insertar(new Pelicula(7, "Buscando a Nemo", "Animada", "14:00", "Sala B"));
        cartelera.insertar(new Pelicula(8, "Los Increibles", "Animada", "16:00", "Sala D"));
        cartelera.insertar(new Pelicula(9, "Juego de Gemelas", "Comedia", "15:30", "Sala A"));
        cartelera.insertar(new Pelicula(10, "Como Caido del Cielo", "Comedia", "19:00", "Sala B"));
        cartelera.insertar(new Pelicula(11, "El Secreto de sus Ojos", "Drama", "17:00", "Sala C"));
        cartelera.insertar(new Pelicula(12, "Roma", "Drama", "20:00", "Sala D"));
    }

    /** Devuelve la cartelera ordenada (recorrido InOrder del BST por genero/horario/sala). */
    public List<Pelicula> obtenerCartelera() {
        return cartelera.toList();
    }

    public Pelicula buscarPeliculaPorId(int id) {
        return cartelera.buscarPorId(id);
    }

    /** Filtra peliculas por genero usando el arbol BST. */
    public List<Pelicula> buscarPorGenero(String genero) {
        return cartelera.buscarPorGenero(genero);
    }

    /** Filtra peliculas por horario usando el arbol BST. */
    public List<Pelicula> buscarPorHorario(String horario) {
        return cartelera.buscarPorHorario(horario);
    }

    /** Devuelve la lista de generos unicos presentes en la cartelera. */
    public List<String> obtenerGeneros() {
        List<String> generos = new ArrayList<>();
        for (Pelicula p : cartelera.toList())
            if (!generos.contains(p.genero)) generos.add(p.genero);
        return generos;
    }

    /** Devuelve la lista de horarios unicos presentes en la cartelera. */
    public List<String> obtenerHorarios() {
        List<String> horarios = new ArrayList<>();
        for (Pelicula p : cartelera.toList())
            if (!horarios.contains(p.horario)) horarios.add(p.horario);
        java.util.Collections.sort(horarios);
        return horarios;
    }

    public void seleccionarPelicula(Pelicula p) {
        if (p == null) throw new IllegalArgumentException("La pelicula no puede ser nula.");
        this.peliculaSeleccionada = p;
        this.filaSeleccionada = -1;
        this.colSeleccionada  = -1;
    }

    public Pelicula getPeliculaSeleccionada() { return peliculaSeleccionada; }
    public int getFilaSeleccionada()          { return filaSeleccionada; }
    public int getColSeleccionada()           { return colSeleccionada; }

    /**
     * Selecciona un asiento libre de la matriz de la sala actual.
     * @throws IllegalStateException si no hay pelicula seleccionada
     * @throws IllegalArgumentException si el asiento ya esta ocupado
     */
    public void seleccionarAsiento(int fila, int columna) {
        if (peliculaSeleccionada == null)
            throw new IllegalStateException("Selecciona una pelicula primero.");
        if (!peliculaSeleccionada.sala.verificarAsiento(fila, columna))
            throw new IllegalArgumentException("El asiento ya esta ocupado.");
        this.filaSeleccionada = fila;
        this.colSeleccionada  = columna;
    }

    /**
     * Confirma la reserva del asiento seleccionado: marca el asiento como
     * ocupado en la matriz y encola la reserva (Opcion 2: encolar).
     * @throws IllegalStateException si falta pelicula/asiento
     * @throws IllegalArgumentException si el nombre del cliente es invalido
     */
    public Reserva confirmarReserva(String cliente) {
        if (peliculaSeleccionada == null)
            throw new IllegalStateException("Selecciona una pelicula primero.");
        if (filaSeleccionada < 0 || colSeleccionada < 0)
            throw new IllegalStateException("Selecciona un asiento en el mapa.");
        if (cliente == null || cliente.trim().isEmpty())
            throw new IllegalArgumentException("Ingresa el nombre del cliente.");

        peliculaSeleccionada.sala.ocuparAsiento(filaSeleccionada, colSeleccionada);
        Reserva reserva = new Reserva(cliente.trim(), peliculaSeleccionada, filaSeleccionada, colSeleccionada);
        colaReservas.encolar(reserva);
        historial.add(reserva);

        // Se limpia la seleccion de asiento (la pelicula se mantiene elegida)
        filaSeleccionada = -1;
        colSeleccionada  = -1;
        return reserva;
    }

    public ColaReservas obtenerColaReservas() { return colaReservas; }

    public boolean hayReservasPendientes() { return !colaReservas.estaVacia(); }

    /**
     * Desencola la reserva mas antigua (FIFO) y la marca como confirmada.
     * @throws NoSuchElementException si la cola esta vacia
     */
    public Reserva procesarPago() {
        if (colaReservas.estaVacia())
            throw new NoSuchElementException("No hay reservas pendientes en la cola.");
        Reserva reserva = colaReservas.desencolar();
        reserva.estado = "CONFIRMADA";
        return reserva;
    }

    /** Fila de reporte: {sala, pelicula, vendidos, disponibles, total, %ocupacion} */
    public List<Object[]> generarReporteOcupacion() {
        List<Object[]> filas = new ArrayList<>();
        for (Pelicula p : cartelera.toList()) {
            int[] estadisticas = p.sala.getEstadisticas();
            double porcentaje = (double) estadisticas[0] / estadisticas[2] * 100;
            filas.add(new Object[]{
                p.sala.getNombre(), p.titulo, estadisticas[0], estadisticas[1], estadisticas[2],
                String.format("%.1f%%", porcentaje)
            });
        }
        return filas;
    }
}
