# Sistema de Venta de Entradas - Cine Multicinemas

Proyecto Java Swing con arquitectura **MVC** y build system **Apache Ant**.

## Estructura
```
src/
├── App.java                         (arranque de la aplicacion)
├── model/                           (MODELO - estructuras de datos)
│   ├── Sala.java                    -> matriz de asientos (char[][])
│   ├── Pelicula.java
│   ├── NodoArbol.java               -> nodo BST (clave: genero|horario|sala)
│   ├── ArbolPeliculas.java          -> arbol BST de cartelera
│   └── Reserva.java
├── controller/                      (CONTROLADOR - logica de negocio)
│   └── Controlador.java             -> cola de reservas (Queue) + reglas
└── view/                            (VISTA - interfaz grafica Swing)
    └── Vista.java
```

## Compilar y ejecutar (Ant)
```
ant compile
ant run
ant jar
```

## Compilar y ejecutar (manual, sin Ant)
```
javac -d build/classes -encoding UTF-8 -sourcepath src src/App.java
java -cp build/classes App
```

Requiere JDK 17 o superior. No usa base de datos ni archivos en disco;
todos los datos se mantienen en memoria durante la ejecucion.
