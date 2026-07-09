package model;

import java.util.ArrayList;
import java.util.List;

/**
 * MODELO
 * Estructura de datos requerida: ARBOL (BST) para organizar peliculas
 * por genero, horario y sala.
 *
 * Clave de ordenamiento: String compuesto "genero|horario|sala".
 * Comparacion lexicografica agrupa primero por genero, luego horario,
 * luego sala. El recorrido InOrder entrega la cartelera ordenada.
 *
 * Si dos peliculas comparten la misma clave, se almacenan en la lista
 * del mismo nodo (sin duplicar nodos).
 */
public class ArbolPeliculas {
    private NodoArbol raiz;

    /** Inserta una pelicula en el BST segun su clave genero|horario|sala. */
    public void insertar(Pelicula p) {
        raiz = insertarRec(raiz, p);
    }

    private NodoArbol insertarRec(NodoArbol nodo, Pelicula p) {
        String clave = p.getClaveBST();
        if (nodo == null) return new NodoArbol(clave, p);

        int cmp = clave.compareTo(nodo.clave);
        if (cmp < 0) nodo.izquierdo = insertarRec(nodo.izquierdo, p);
        else if (cmp > 0) nodo.derecho = insertarRec(nodo.derecho, p);
        else nodo.peliculas.add(p);
        return nodo;
    }

    /** Busca una pelicula por ID recorriendo todo el arbol. */
    public Pelicula buscarPorId(int id) {
        return buscarPorIdRec(raiz, id);
    }

    private Pelicula buscarPorIdRec(NodoArbol nodo, int id) {
        if (nodo == null) return null;
        for (Pelicula p : nodo.peliculas)
            if (p.id == id) return p;
        Pelicula resultado = buscarPorIdRec(nodo.izquierdo, id);
        if (resultado != null) return resultado;
        return buscarPorIdRec(nodo.derecho, id);
    }

    /** Recorrido InOrder: entrega la cartelera ordenada por genero/horario/sala. */
    public List<Pelicula> toList() {
        List<Pelicula> lista = new ArrayList<>();
        inOrder(raiz, lista);
        return lista;
    }

    private void inOrder(NodoArbol nodo, List<Pelicula> lista) {
        if (nodo == null) return;
        inOrder(nodo.izquierdo, lista);
        lista.addAll(nodo.peliculas);
        inOrder(nodo.derecho, lista);
    }

    /** Busca todas las peliculas de un genero especifico. */
    public List<Pelicula> buscarPorGenero(String genero) {
        List<Pelicula> resultado = new ArrayList<>();
        buscarPorGeneroRec(raiz, genero, resultado);
        return resultado;
    }

    private void buscarPorGeneroRec(NodoArbol nodo, String genero, List<Pelicula> resultado) {
        if (nodo == null) return;
        buscarPorGeneroRec(nodo.izquierdo, genero, resultado);
        for (Pelicula p : nodo.peliculas)
            if (p.genero.equalsIgnoreCase(genero)) resultado.add(p);
        buscarPorGeneroRec(nodo.derecho, genero, resultado);
    }

    /** Busca todas las peliculas de una sala especifica. */
    public List<Pelicula> buscarPorSala(String sala) {
        List<Pelicula> resultado = new ArrayList<>();
        buscarPorSalaRec(raiz, sala, resultado);
        return resultado;
    }

    private void buscarPorSalaRec(NodoArbol nodo, String sala, List<Pelicula> resultado) {
        if (nodo == null) return;
        buscarPorSalaRec(nodo.izquierdo, sala, resultado);
        for (Pelicula p : nodo.peliculas)
            if (p.sala.getNombre().equalsIgnoreCase(sala)) resultado.add(p);
        buscarPorSalaRec(nodo.derecho, sala, resultado);
    }

    /** Busca todas las peliculas en un horario especifico. */
    public List<Pelicula> buscarPorHorario(String horario) {
        List<Pelicula> resultado = new ArrayList<>();
        buscarPorHorarioRec(raiz, horario, resultado);
        return resultado;
    }

    private void buscarPorHorarioRec(NodoArbol nodo, String horario, List<Pelicula> resultado) {
        if (nodo == null) return;
        buscarPorHorarioRec(nodo.izquierdo, horario, resultado);
        for (Pelicula p : nodo.peliculas)
            if (p.horario.equalsIgnoreCase(horario)) resultado.add(p);
        buscarPorHorarioRec(nodo.derecho, horario, resultado);
    }
}
