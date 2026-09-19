import java.time.LocalDate;

public class Venta {
    private Libro libro;
    private String nombreComprador;
    private String identificacion;
    private String telefono;
    private String correo;
    private LocalDate fecha;
    private double precio;

    public Venta(Libro libro, String nombreComprador, String identificacion, String telefono, String correo, double precio) {
        this.libro = libro;
        this.nombreComprador = nombreComprador;
        this.identificacion = identificacion;
        this.telefono = telefono;
        this.correo = correo;
        this.fecha = LocalDate.now();
        this.precio = precio;
    }

    public Libro getLibro() {
        return libro;
    }

    public String getNombreComprador() {
        return nombreComprador;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public double getPrecio() {
        return precio;
    }
}
