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
  Main.java            Programa de demostracion por consola.
  BibliotecaUI.java    Interfaz grafica completa construida en Java Swing.
  *Panel.java          Paneles de la interfaz: Inicio, Libros, Usuarios y Prestamos.
  *Dialog.java         Ventanas modales para altas, ediciones, prestamos, devoluciones y extensiones.

  Relaciones:
      BibliotecaUI / Main --> Biblioteca --> Libro --> CategoriaLibro
                                         --> Usuario --> Libro
                                         --> Prestamo --> Usuario
                                                      --> Libro

FUNCIONALIDAD
-------------
  - Registrar y editar libros y usuarios con validaciones de unicidad.
  - Catalogo interactivo con busqueda en tiempo real y filtrado por categorias.
  - Prestar libros a usuarios (con plazo de 14 dias).
  - Extension unica de prestamos (7 dias adicionales para prestamos al dia).
  - Devolucion de libros con calculo automatico de recargos por retraso ($10/dia).
  - Venta de libros y baja de inventario.
  - Dashboard de estadisticas (libros totales, disponibles, prestados, retrasos y saldo).
  - Listado de usuarios con conteo de libros en posesion y proteccion contra borrado.

REGLAS DE NEGOCIO
-----------------
  1. Solo Biblioteca coordina la validez de prestamos, devoluciones y bajas.
  2. No se permite eliminar un usuario si aun tiene libros prestados en su poder.
  3. No se permite prestar, vender o eliminar libros en estado no valido.
  4. Los prestamos con retraso no pueden ser extendidos; deben ser devueltos cubriendo el recargo.
  5. El historial nunca se borra: los prestamos devueltos se conservan para auditoria.

COMO EJECUTAR LA INTERFAZ GRAFICA (SWING)
------------------------------------------
  Desde la terminal:
      javac -encoding UTF-8 *.java
      java BibliotecaUI

COMO EJECUTAR LA DEMO EN CONSOLA
---------------------------------
  Desde la terminal:
      javac -encoding UTF-8 *.java
      java Main
