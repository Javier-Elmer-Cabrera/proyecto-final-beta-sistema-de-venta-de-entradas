package model;

import java.util.ArrayList;
import java.util.List;

/**
 * MODELO
 * Nodo del arbol binario de busqueda (BST).
 * Cada nodo almacena una clave compuesta (genero|horario|sala) y una lista
 * de peliculas que comparten esa misma combinacion.
 */
public class NodoArbol {
    public String clave;
    public List<Pelicula> peliculas;
    public NodoArbol izquierdo, derecho;

    public NodoArbol(String clave, Pelicula p) {
        this.clave = clave;
        this.peliculas = new ArrayList<>();
        this.peliculas.add(p);
    }
}
