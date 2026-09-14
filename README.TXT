------------------------------------------------------------------------
PROJECT TITLE: Biblioteca Mini 5
PURPOSE OF PROJECT: Version reducida y simplificada del proyecto
                    "Taller 10.3". Sistema minimo de prestamo de libros
                    con 5 clases de dominio, pensado para que se
                    entienda de una sola lectura.
VERSION or DATE: 1.0
AUTHORS: (tu nombre)
------------------------------------------------------------------------

ESTRUCTURA
----------
  CategoriaLibro.java  Enum con las categorias posibles de un libro.
  Libro.java           Datos del libro (titulo, autor, ISBN, categoria)
                       y si esta prestado o disponible.
  Usuario.java         Datos del usuario (nombre, ID) y la lista de
                       libros que tiene prestados.
  Prestamo.java        Registro de que un usuario se llevo un libro y
                       en que fecha. Estado ACTIVO o DEVUELTO.
  Biblioteca.java      Guarda libros, usuarios y prestamos, y coordina
                       las busquedas, los prestamos y las devoluciones.
  Main.java            Programa de demostracion (no es parte del
                       dominio).

  Relaciones:
      Main --> Biblioteca --> Libro --> CategoriaLibro
                          --> Usuario --> Libro
                          --> Prestamo --> Usuario
                                       --> Libro

FUNCIONALIDAD
-------------
  - Registrar libros y usuarios.
  - Buscar libros por titulo (sin distinguir mayusculas).
  - Buscar libros por categoria.
  - Buscar un libro por ISBN y un usuario por ID.
  - Prestar un libro a un usuario (se crea su Prestamo).
  - Devolver un libro (se cierra su Prestamo).
  - Listar libros disponibles, libros prestados e historial de
    prestamos.

REGLA IMPORTANTE
----------------
  Solo Biblioteca decide si un prestamo o una devolucion son validos.
  Libro, Usuario y Prestamo nada mas guardan su propio estado. Asi el
  libro, la lista del usuario y el registro de prestamos nunca se
  quedan desincronizados.

  El historial nunca se borra: un Prestamo devuelto se queda en la
  lista marcado como DEVUELTO, con su fecha.

COMO EJECUTARLO EN BlueJ
------------------------
  1. Abrir BlueJ  ->  Project  ->  Open Project...
  2. Elegir esta carpeta ("Biblioteca Mini 5").
  3. Clic derecho sobre la clase Main  ->  void main(String[] args).

COMO EJECUTARLO DESDE LA TERMINAL
---------------------------------
  Situarse en esta carpeta y escribir:

      javac -encoding UTF-8 *.java
      java Main

QUE SE QUITO RESPECTO AL PROYECTO ORIGINAL
------------------------------------------
  Interfaz grafica (BibliotecaGUI, DialogoNuevoLibro, DialogoUbicacion),
  graficas estadisticas (GraficoPastel, GraficoLineal, ...), la herencia
  (Persona, Empleado, LibroDigital), PrestamoCubi, las multas por
  retraso, los vencimientos, la lista de espera, el historial de estados
  del libro y las pruebas JUnit.

DIFERENCIA CON LA CARPETA "Biblioteca Mini"
-------------------------------------------
  "Biblioteca Mini" tiene 3 clases de dominio (Libro, Usuario,
  Biblioteca). Esta version agrega Prestamo y CategoriaLibro.
