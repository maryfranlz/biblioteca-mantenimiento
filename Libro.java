/**
 * Representa un libro de la biblioteca.
 *
 * Un libro guarda sus datos básicos (título, autor, ISBN, categoría,
 * precio) y sabe si está prestado o disponible. No conoce a los
 * usuarios ni a la biblioteca: sólo cuida su propio estado.
 */
public class Libro
{
    private String titulo;
    private String autor;
    private String isbn;
    private CategoriaLibro categoria;
    private boolean prestado;
    private double precio;
    private boolean vendido;

    /**
     * Crea un libro disponible.
     *
     * @param titulo    título del libro
     * @param autor     autor del libro
     * @param isbn      identificador único del libro
     * @param categoria categoría a la que pertenece
     */
    public Libro(String titulo, String autor, String isbn, CategoriaLibro categoria, double precio)
    {
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.categoria = categoria;
        this.prestado = false;
        this.precio = precio;
        this.vendido = false;
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

    public double getPrecio()
    {
        return precio;
    }

    public boolean isVendido()
    {
        return vendido;
    }

    public void setTitulo(String titulo)
    {
        this.titulo = titulo;
    }

    public void setAutor(String autor)
    {
        this.autor = autor;
    }

    public void setIsbn(String isbn)
    {
        this.isbn = isbn;
    }

    public void setCategoria(CategoriaLibro categoria)
    {
        this.categoria = categoria;
    }

    public void setPrecio(double precio)
    {
        this.precio = precio;
    }

    /**
     * Marca el libro como prestado.
     *
     * @return true si se pudo prestar, false si ya estaba prestado
     */
    public boolean prestar()
    {
        if (prestado || vendido) {
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

    public boolean vender()
    {
        if(prestado || vendido){
            return false;
        }
        return true;
    }

    public String getEstadoTexto()
    {
        if(vendido){
            return "Vendido";
        }

        if(prestado){
            return "Prestado";
        } else {
            return "Disponible";
        }
    }

    public String toString()
    {
        String estado = getEstadoTexto();
        return titulo + " - " + autor + " (" + categoria + ", ISBN: " + isbn + ") [" + estado + "]";
    }
}
