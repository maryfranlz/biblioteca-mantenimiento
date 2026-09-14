/**
 * Representa un libro de la biblioteca.
 *
 * Un libro guarda sus datos básicos (título, autor, ISBN, categoría) y
 * sabe si está prestado o disponible. No conoce a los usuarios ni a la
 * biblioteca: sólo cuida su propio estado.
 */
public class Libro
{
    private String titulo;
    private String autor;
    private String isbn;
    private CategoriaLibro categoria;
    private boolean prestado;

    /**
     * Crea un libro disponible.
     *
     * @param titulo    título del libro
     * @param autor     autor del libro
     * @param isbn      identificador único del libro
     * @param categoria categoría a la que pertenece
     */
    public Libro(String titulo, String autor, String isbn, CategoriaLibro categoria)
    {
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.categoria = categoria;
        this.prestado = false;
    }

    public String getTitulo()
    {
        return titulo;
    }

    public String getAutor()
    {
        return autor;
    }

    public String getIsbn()
    {
        return isbn;
    }

    public CategoriaLibro getCategoria()
    {
        return categoria;
    }

    /**
     * @return true si el libro está prestado en este momento
     */
    public boolean isPrestado()
    {
        return prestado;
    }

    /**
     * Marca el libro como prestado.
     *
     * @return true si se pudo prestar, false si ya estaba prestado
     */
    public boolean prestar()
    {
        if (prestado) {
            return false;
        }
        prestado = true;
        return true;
    }

    /**
     * Marca el libro como disponible otra vez.
     */
    public void devolver()
    {
        prestado = false;
    }

    public String toString()
    {
        String estado = prestado ? "Prestado" : "Disponible";
        return titulo + " - " + autor + " (" + categoria + ", ISBN: " + isbn + ") [" + estado + "]";
    }
}
