import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Registra que un usuario se llevó un libro y cuándo lo hizo.
 *
 * Un préstamo nace ACTIVO y pasa a DEVUELTO cuando se cierra. Sirve
 * como historial: la Biblioteca crea uno por cada préstamo y nunca lo
 * borra, aunque el libro ya haya regresado.
 * 
 * Se agrega RETRASO. Cuando un libro se devuelve después de la fecha límite
 * (14 días naturales). Se marca de esta manera para cobrar un recargo de
 * $10 pesos por día extra.
 * 
 * El préstamo inicia con un recargo en cero y una única extensión.
 * 
 * Se puede extender el préstamo siete días.
 */
public class Prestamo
{
    public static final int ACTIVO = 0;
    public static final int DEVUELTO = 1;
    public static final int RETRASO = 2;

    private Usuario usuario;
    private Libro libro;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;
    private LocalDate fechaLimiteDevolucion;
    private int estado;
    private double recargo;
    private boolean extensionUtilizada;

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
        this.fechaLimiteDevolucion = fechaPrestamo.plusDays(14);
        this.estado = ACTIVO;
        this.recargo = 0.0;
        this.extensionUtilizada = false;
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

    public LocalDate getFechaLimiteDevolucion()
    {
        return fechaLimiteDevolucion;
    }

    public int getEstado()
    {
        actualizarEstado();
        return estado;
    }

    public Double getRecargo()
    {
        return recargo;
    }

    public boolean isExtensionUtilizada()
    {
        return extensionUtilizada;
    }

    /**
     * @return true si el libro todavía no se devuelve
     */
    public boolean estaActivo()
    {
        actualizarEstado();
        return estado == ACTIVO || estado == RETRASO;
    }

    /**
     * Cierra el préstamo: lo marca como DEVUELTO con la fecha de hoy.
     * Si se devuelve después de la fecha límite, se cobra $10 por
     * cada día de retraso
     */
    public void cerrar()
    {
        actualizarEstado();

        fechaDevolucion = LocalDate.now();

        if(fechaDevolucion.isAfter(fechaLimiteDevolucion)){
            long diasRetraso = ChronoUnit.DAYS.between(fechaLimiteDevolucion, fechaDevolucion);
            recargo = diasRetraso * 10.0;
        }

        estado = DEVUELTO;
    }

    /**
     * Actualiza el estado dependiendo de la fecha actual
     */
    public void actualizarEstado()
    {
        if(estado == DEVUELTO){
            return;
        }

        if(LocalDate.now().isAfter(fechaLimiteDevolucion)){
            estado = RETRASO;
        } else {
            estado = ACTIVO;
        }
    }

    /**
     * @return false si ya se utlilizó la extensión o si ya se devolvió el libro
     */
    public boolean extender()
    {
        if(!estaActivo() || isExtensionUtilizada()){
            return false;
        }

        fechaLimiteDevolucion = fechaLimiteDevolucion.plusDays(7);
        extensionUtilizada = true;
        actualizarEstado();

        return true;
    }

    public String getEstadoTexto()
    {
        actualizarEstado();

        switch(estado) {
            case ACTIVO:
                return "ACTIVO";
            case DEVUELTO:
                return "DEVUELTO";
            case RETRASO:
                return "RETRASO";
            default:
                return "";
        }
    }

    public String toString()
    {
        String texto = usuario.getNombre() + " -> " + libro.getTitulo()
                     + " (prestado el " + fechaPrestamo + ")";
        if (estaActivo()) {
            return texto + " [" + getEstadoTexto() + "]";
        }
        return texto + " [DEVUELTO el " + fechaDevolucion + "]";
    }
}
