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
    private int contadorUsuarios = 0;

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

    public boolean agregarLibro(Libro libro)
    {
        if (libro == null || libro.getIsbn() == null || buscarLibroPorIsbn(libro.getIsbn()) != null) {
            return false;
        }
        return libros.add(libro);
    }

    public boolean agregarUsuario(Usuario usuario)
    {
        if (usuario == null || usuario.getId() == null || buscarUsuarioPorId(usuario.getId()) != null) {
            return false;
        }
        return usuarios.add(usuario);
    }

    public List<Libro> getLibros()
    {
        return new ArrayList<>(libros);
    }

    public List<Usuario> getUsuarios()
    {
        return new ArrayList<>(usuarios);
    }

    public List<Prestamo> getPrestamos()
    {
        return new ArrayList<>(prestamos);
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
        if (texto == null) {
            return resultados;
        }

        String busqueda = texto.toLowerCase().trim();
        for (Libro libro : libros) {
            if (libro.getTitulo() != null && libro.getTitulo().toLowerCase().contains(busqueda)) {
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

        if(categoria == null) {
            return getLibros();
        }

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
     * Búsqueda global de libros
     * @param texto puede ser título, autor, isbn o categoría
     * @return lista de libros con coincidencia en cualquier entidad comparada
     */
    public List<Libro> buscarLibros(String texto)
    {
        List<Libro> resultados = new ArrayList<>();
        String busqueda = "";

        if(texto != null){
            busqueda = texto.toLowerCase().trim();
        }

        for(Libro libro : libros) {
            boolean coincidencia = false;

            String isbn = libro.getIsbn() != null ? libro.getIsbn().toLowerCase() : "";
            String titulo = libro.getTitulo() != null ? libro.getTitulo().toLowerCase() : "";
            String autor = libro.getAutor() != null ? libro.getAutor().toLowerCase() : "";
            String cat = libro.getCategoria() != null ? libro.getCategoria().toString().toLowerCase() : "";

            if(isbn.contains(busqueda) || titulo.contains(busqueda) || autor.contains(busqueda) || cat.contains(busqueda)){
                coincidencia = true;
            }

            if(coincidencia) {
                resultados.add(libro);
            }
        }

        return resultados;
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

        Prestamo prestamo = buscarPrestamoActivo(libro);

        if (prestamo != null && prestamo.estaActivo()) {
            prestamo.cerrar();
        }

        libro.devolver();

        return true;
    }

    /**
     * 
     * @param isbn
     * @param idUsuario
     * @return true si se concretó la extensión del préstamo,
     * false si el préstamo no existe o si el usuario no ha pedido
     * prestado ese libro; también false si ya se utilizó la extensión
     * o si ya se devolvió el libro
     */
    public boolean extenderPrestamo(String isbn, String idUsuario)
    {
        Libro libro = buscarLibroPorIsbn(isbn);
        Prestamo prestamo = buscarPrestamoActivo(libro);

        if(prestamo == null) {
            return false;
        }

        if(!prestamo.getUsuario().getId().equals(idUsuario)){
            return false;
        }

        return prestamo.extender();
    }

    /**
     * Vende un ejemplar del libro
     * @param isbn
     * @return true si se completó la venta, false si no existe el 
     * libro, si está prestado o si ya fue vendido
     */
    public boolean venderLibro(String isbn)
    {
        Libro libro = buscarLibroPorIsbn(isbn);
        if(libro == null){
            return false;
        }

        return libro.vender();
    }

    /**
     * @return el préstamo activo de ese libro, o null si no hay ninguno
     */
    public Prestamo buscarPrestamoActivo(Libro libro)
    {
        if(libro == null){
            return null;
        }
        
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
            if (libro.isPrestado() == prestados && !libro.isVendido()) {
                resultados.add(libro);
            }
        }

        return resultados;
    }

    public boolean eliminarLibro(String isbn)
    {
        Libro libro = buscarLibroPorIsbn(isbn);
        if(libro == null || libro.isPrestado()){
            return false;
        }

        return libros.remove(libro);
    }

    public String generarIdUsuario(){
        int maxId = 0;
        for (Usuario u : usuarios) {
            if (u.getId() != null && u.getId().matches("U\\d+")) {
                try {
                    int num = Integer.parseInt(u.getId().substring(1));
                    if (num > maxId) {
                        maxId = num;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        contadorUsuarios = Math.max(contadorUsuarios, maxId) + 1;
        return String.format("U%03d", contadorUsuarios);
    }

    public boolean eliminarUsuario(String idUsuario)
    {
        Usuario usuario = buscarUsuarioPorId(idUsuario);
        if(usuario == null || !usuario.getLibrosPrestados().isEmpty()){
            return false;
        }
        return usuarios.remove(usuario);
    }

    public String toString()
    {
        String nombreBiblioteca = (nombre != null && nombre.toLowerCase().startsWith("biblioteca")) ? nombre : "Biblioteca " + nombre;
        return nombreBiblioteca
             + "\nLibros registrados: " + libros.size()
             + "\nDisponibles: " + getLibros(false).size()
             + "\nPrestados: " + getLibros(true).size()
             + "\nUsuarios registrados: " + usuarios.size()
             + "\nPréstamos en el historial: " + prestamos.size();
    }
}