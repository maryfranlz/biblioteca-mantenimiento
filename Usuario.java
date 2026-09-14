import java.util.ArrayList;
import java.util.List;

/**
 * Representa a una persona registrada en la biblioteca.
 *
 * Cada usuario guarda la lista de los libros que tiene en su poder.
 * Quien decide si un préstamo es válido es la clase Biblioteca; el
 * usuario sólo lleva la cuenta de lo que tiene prestado.
 */
public class Usuario
{
    private String nombre;
    private String id;
    private List<Libro> librosPrestados;

    /**
     * Crea un usuario sin libros prestados.
     *
     * @param nombre nombre completo del usuario
     * @param id     identificador único del usuario
     */
    public Usuario(String nombre, String id)
    {
        this.nombre = nombre;
        this.id = id;
        this.librosPrestados = new ArrayList<>();
    }

    public String getNombre()
    {
        return nombre;
    }

    public String getId()
    {
        return id;
    }

    /**
     * @return una copia de la lista de libros prestados
     */
    public List<Libro> getLibrosPrestados()
    {
        return librosPrestados;
    }

    /**
     * Agrega un libro a los préstamos del usuario.
     */
    public void agregarLibro(Libro libro)
    {
        librosPrestados.add(libro);
    }

    /**
     * Quita un libro de los préstamos del usuario.
     *
     * @return true si el usuario tenía ese libro, false si no
     */
    public boolean quitarLibro(Libro libro)
    {
        return librosPrestados.remove(libro);
    }

    public String toString()
    {
        return nombre + " (ID: " + id + ") - libros prestados: " + librosPrestados.size();
    }
}
