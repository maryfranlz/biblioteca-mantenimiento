/**
 * Demostración del proyecto: crea una biblioteca, presta un libro y
 * lo devuelve, mostrando el resultado de cada paso en la consola.
 */
public class Main
{
    public static void main(String[] args)
    {
        Biblioteca biblioteca = new Biblioteca("Biblioteca Central");

        biblioteca.agregarLibro(new Libro(
            "Don Quijote de la Mancha",
            "Miguel de Cervantes",
            "9788424922498",
            CategoriaLibro.FICCION,
            300.00,
            2
        ));

        biblioteca.agregarLibro(new Libro(
            "Cien años de soledad",
            "Gabriel García Márquez",
            "9780307474728",
            CategoriaLibro.NOVELA,
            200.00,
            3
        ));

        biblioteca.agregarLibro(new Libro(
            "El Principito",
            "Antoine de Saint-Exupéry",
            "9788498381498",
            CategoriaLibro.AVENTURA,
            125.00,
            5
        ));

        biblioteca.agregarUsuario(new Usuario("Ana López", "U001"));
        biblioteca.agregarUsuario(new Usuario("Carlos Ruiz", "U002"));

        System.out.println("--- Búsqueda por título: 'el' ---");

        for (Libro libro : biblioteca.buscarPorTitulo("el")) {
            System.out.println(libro);
        }

        System.out.println("\n--- Búsqueda por categoría: NOVELA ---");

        for (Libro libro : biblioteca.buscarPorCategoria(CategoriaLibro.NOVELA)) {
            System.out.println(libro);
        }

        System.out.println("\n--- Préstamo a Ana López ---");
        System.out.println(
            biblioteca.prestarLibro("9780307474728", "U001")
                ? "Préstamo realizado de Cien años de soledad"
                : "No se pudo prestar"
        );

        System.out.println("\n--- Préstamo a Carlos Ruiz ---");

        System.out.println(
            biblioteca.prestarLibro("9788424922498", "U002")
                ? "Préstamo realizado"
                : "No se pudo prestar: el libro ya está prestado"
        );

        System.out.println("\n--- Libros prestados ---");

        for (Libro libro : biblioteca.getLibros(true)) {
            System.out.println(libro);
        }

        System.out.println("\n--- Devolución de Ana López ---");

        System.out.println(
            biblioteca.devolverLibro("9788424922498", "U001")
                ? "Devolución realizada"
                : "No se pudo devolver"
        );

        System.out.println("\n--- Historial de préstamos ---");

        for (Prestamo prestamo : biblioteca.getPrestamos()) {
            System.out.println(prestamo);
        }

        System.out.println("\n--- Estado final ---");
        System.out.println(biblioteca);
    }
}