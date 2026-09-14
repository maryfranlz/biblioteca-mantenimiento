import java.time.LocalDate;

/**
 * Registra que un usuario se llevó un libro y cuándo lo hizo.
 *
 * Un préstamo nace ACTIVO y pasa a DEVUELTO cuando se cierra. Sirve
 * como historial: la Biblioteca crea uno por cada préstamo y nunca lo
 * borra, aunque el libro ya haya regresado.
 */
public class Prestamo
{
    public static final int ACTIVO = 0;
    public static final int DEVUELTO = 1;

    private Usuario usuario;
    private Libro libro;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;
    private int estado;

    /**
     * Crea un préstamo ACTIVO con la fecha de hoy.
     *
     * @param usuario quien se lleva el libro
     * @param libro   libro prestado
     */
    public Prestamo(Usuario usuario, Libro libro)
    {
        this.usuario = usuario;
        this.libro = libro;
        this.fechaPrestamo = LocalDate.now();
        this.fechaDevolucion = null;
        this.estado = ACTIVO;
    }

    public Usuario getUsuario()
    {
        return usuario;
    }

    public Libro getLibro()
    {
        return libro;
    }

    public LocalDate getFechaPrestamo()
    {
        return fechaPrestamo;
    }

    /**
     * @return la fecha en que se devolvió el libro, o null si sigue prestado
     */
    public LocalDate getFechaDevolucion()
    {
        return fechaDevolucion;
    }

    public int getEstado()
    {
        return estado;
    }

    /**
     * @return true si el libro todavía no se devuelve
     */
    public boolean estaActivo()
    {
        return estado == ACTIVO;
    }

    /**
     * Cierra el préstamo: lo marca como DEVUELTO con la fecha de hoy.
     */
    public void cerrar()
    {
        estado = DEVUELTO;
        fechaDevolucion = LocalDate.now();
    }

    public String toString()
    {
        String texto = usuario.getNombre() + " -> " + libro.getTitulo()
                     + " (prestado el " + fechaPrestamo + ")";
        if (estaActivo()) {
            return texto + " [ACTIVO]";
        }
        return texto + " [DEVUELTO el " + fechaDevolucion + "]";
    }
}
