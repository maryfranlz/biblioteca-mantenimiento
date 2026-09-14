import java.util.ArrayList;
import java.util.List;

/**
 * Coordina los libros, los usuarios y los préstamos de la biblioteca.
 *
 * Es la única clase que decide si un préstamo o una devolución son
 * válidos, para que el estado del libro, la lista del usuario y el
 * registro de préstamos nunca queden desincronizados.
 */
public class Biblioteca
{
    private String nombre;
    private List<Libro> libros;
    private List<Usuario> usuarios;
    private List<Prestamo> prestamos;

    /**
     * Crea una biblioteca vacía.
     *
     * @param nombre nombre de la biblioteca
     */
    public Biblioteca(String nombre)
    {
        this.nombre = nombre;
        this.libros = new ArrayList<>();
        this.usuarios = new ArrayList<>();
        this.prestamos = new ArrayList<>();
    }

    public String getNombre()
    {
        return nombre;
    }

    public void agregarLibro(Libro libro)
    {
        libros.add(libro);
    }

    public void agregarUsuario(Usuario usuario)
    {
        usuarios.add(usuario);
    }

    /**
     * Busca los libros cuyo título contiene el texto indicado.
     *
     * @param texto texto a buscar (no distingue mayúsculas)
     * @return lista con los libros encontrados
     */
    public List<Libro> buscarPorTitulo(String texto)
    {
        List<Libro> resultados = new ArrayList<>();

        for (Libro libro : libros) {
            if (libro.getTitulo().toLowerCase().contains(texto.toLowerCase())) {
                resultados.add(libro);
            }
        }

        return resultados;
    }

    /**
     * Busca los libros de una categoría.
     *
     * @param categoria categoría a filtrar
     * @return lista con los libros de esa categoría
     */
    public List<Libro> buscarPorCategoria(CategoriaLibro categoria)
    {
        List<Libro> resultados = new ArrayList<>();

        for (Libro libro : libros) {
            if (libro.getCategoria() == categoria) {
                resultados.add(libro);
            }
        }

        return resultados;
    }

    /**
     * @return el libro con ese ISBN, o null si no existe
     */
    public Libro buscarLibroPorIsbn(String isbn)
    {
        for (Libro libro : libros) {
            if (libro.getIsbn().equals(isbn)) {
                return libro;
            }
        }

        return null;
    }

    /**
     * @return el usuario con ese ID, o null si no existe
     */
    public Usuario buscarUsuarioPorId(String id)
    {
        for (Usuario usuario : usuarios) {
            if (usuario.getId().equals(id)) {
                return usuario;
            }
        }

        return null;
    }

    /**
     * Presta un libro a un usuario y guarda el préstamo en el registro.
     *
     * @return true si el préstamo se realizó, false si el libro o el
     *         usuario no existen, o si el libro ya estaba prestado
     */
    public boolean prestarLibro(String isbn, String idUsuario)
    {
        Libro libro = buscarLibroPorIsbn(isbn);
        Usuario usuario = buscarUsuarioPorId(idUsuario);

        if (libro == null || usuario == null) {
            return false;
        }

        if (!libro.prestar()) {
            return false;
        }

        usuario.agregarLibro(libro);
        prestamos.add(new Prestamo(usuario, libro));

        return true;
    }

    /**
     * Devuelve un libro y cierra su préstamo activo.
     *
     * @return true si la devolución se realizó, false si el libro o el
     *         usuario no existen, o si ese usuario no tenía el libro
     */
    public boolean devolverLibro(String isbn, String idUsuario)
    {
        Libro libro = buscarLibroPorIsbn(isbn);
        Usuario usuario = buscarUsuarioPorId(idUsuario);

        if (libro == null || usuario == null || !usuario.quitarLibro(libro)) {
            return false;
        }

        libro.devolver();

        Prestamo prestamo = buscarPrestamoActivo(libro);

        if (prestamo != null && prestamo.estaActivo()) {
            prestamo.cerrar();
        }

        return true;
    }

    /**
     * @return el préstamo activo de ese libro, o null si no hay ninguno
     */
    private Prestamo buscarPrestamoActivo(Libro libro)
    {
        for (Prestamo prestamo : prestamos) {
            if (prestamo.getLibro() == libro && prestamo.estaActivo()) {
                return prestamo;
            }
        }

        return null;
    }

    /**
     * @param prestados true para los libros prestados,
     *                  false para los disponibles
     * @return lista con los libros que están en ese estado
     */
    public List<Libro> getLibros(boolean prestados)
    {
        List<Libro> resultados = new ArrayList<>();

        for (Libro libro : libros) {
            if (libro.isPrestado() == prestados) {
                resultados.add(libro);
            }
        }

        return resultados;
    }

    /**
     * @return una copia del historial completo de préstamos
     */
    public List<Prestamo> getPrestamos()
    {
        return new ArrayList<>(prestamos);
    }

    public String toString()
    {
        return "Biblioteca " + nombre
             + "\nLibros registrados: " + libros.size()
             + "\nDisponibles: " + getLibros(false).size()
             + "\nPrestados: " + getLibros(true).size()
             + "\nUsuarios registrados: " + usuarios.size()
             + "\nPréstamos en el historial: " + prestamos.size();
    }
}