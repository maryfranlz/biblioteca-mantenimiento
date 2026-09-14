import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class BibliotecaUI{

    //Variables gráficas Inicio
    private JButton btnInicio, btnLibros, btnUsuarios, btnPrestamos;
    private JPanel pnlSideMenu, pnlHeader, pnlContenido, pnlTarjetas, pnlSuperior, pnlTabla;
    private JLabel lblFraseHeader, lblPrstmReci, lblVerTodos;
    private JTable tablaPrestamosRecientes;

    private final Color COLOR_FONDO = new Color(245, 245, 245);
    private final Color COLOR_NARANJA = new Color(239, 128, 20);
    private final Color COLOR_CAFE = new Color(39, 3, 0);
    private final Color COLOR_BOTON = new Color(230, 176, 157);
    private final Color COLOR_MENU = new Color(255, 255, 255);

    //Variables funcionales
    private Biblioteca biblioteca;

    public BibliotecaUI() {

        crearBiblioteca();
        crearComponentes();
        organizarPaneles();
        organizarContenido();
        crearVentana();
    }

    private void crearBiblioteca() {
        biblioteca = new Biblioteca("Biblioteca Central");

        biblioteca.agregarLibro(new Libro(
            "Don Quijote de la Mancha",
            "Miguel de Cervantes",
            "9788424922498",
            CategoriaLibro.FICCION,
            300.00
        ));

        biblioteca.agregarLibro(new Libro(
            "Cien años de soledad",
            "Gabriel García Márquez",
            "9780307474728",
            CategoriaLibro.NOVELA,
            200.00
        ));

        biblioteca.agregarLibro(new Libro(
            "El Principito",
            "Antoine de Saint-Exupéry",
            "9788498381498",
            CategoriaLibro.AVENTURA,
            125.00
        ));

        biblioteca.agregarUsuario(new Usuario("Ana López", "U001"));
        biblioteca.agregarUsuario(new Usuario("Carlos Ruiz", "U002"));

        biblioteca.prestarLibro("9780307474728", "U001");
        biblioteca.prestarLibro("9788424922498", "U002");
    }

    private void crearComponentes() {
        btnInicio = crearBoton("Inicio", "dashboard.png");
        btnLibros = crearBoton("Libros", "books.png");
        btnUsuarios = crearBoton("Usuarios", "user.png");
        btnPrestamos = crearBoton("Prestamos", "exchange.png");

        lblFraseHeader = new JLabel("Sólo con el corazón se puede ver bien, lo esencial es invicible para los ojos");
        lblPrstmReci = new JLabel("Préstamos Recientes");
        lblVerTodos = new JLabel("Ver todos");

        pnlSideMenu = new JPanel();
        pnlHeader = new JPanel();
        pnlContenido = new JPanel();
        pnlTarjetas = new JPanel();
        pnlSuperior = new JPanel();
        pnlTabla = new JPanel();
    }

    private void organizarPaneles() {
        pnlSideMenu.setBackground(COLOR_MENU);
        pnlSideMenu.setLayout(new BoxLayout(pnlSideMenu, BoxLayout.Y_AXIS));
        pnlSideMenu.setPreferredSize(new Dimension(170, 0));

        pnlHeader.setBackground(COLOR_MENU);
        pnlHeader.setLayout(new BorderLayout());
        pnlHeader.setPreferredSize(new Dimension(0, 80));

        pnlContenido.setBackground(COLOR_FONDO);
        pnlContenido.setLayout(new BorderLayout(0, 15));
        pnlContenido.setBorder(new EmptyBorder(24, 22, 25, 22));
    }

    private void organizarContenido() {
        JLabel lblLogo = new JLabel("<html>Biblioteca<br>Central<html>");
        lblLogo.setFont(new Font("Arial", Font.BOLD, 16));
        lblLogo.setBorder(new EmptyBorder(10, 20, 20, 20));
        
        pnlSideMenu.add(lblLogo);

        pnlSideMenu.add(btnInicio);
        pnlSideMenu.add(Box.createVerticalStrut(10));
        pnlSideMenu.add(btnLibros);
        pnlSideMenu.add(Box.createVerticalStrut(10));
        pnlSideMenu.add(btnUsuarios);
        pnlSideMenu.add(Box.createVerticalStrut(10));
        pnlSideMenu.add(btnPrestamos);

        lblFraseHeader.setFont(new Font("Serif", Font.ITALIC, 14));
        lblFraseHeader.setHorizontalAlignment(SwingConstants.RIGHT);
        lblFraseHeader.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        pnlHeader.add(lblLogo, BorderLayout.WEST);
        pnlHeader.add(lblFraseHeader, BorderLayout.CENTER);

        pnlTarjetas.setLayout(new GridLayout(1, 5, 12, 0));
        pnlTarjetas.add(crearTarjeta("Libros totales", String.valueOf(biblioteca.getLibros(true).size() + biblioteca.getLibros(false).size())));
        pnlTarjetas.add(crearTarjeta("Disponibles", String.valueOf(biblioteca.getLibros(false).size())));
        pnlTarjetas.add(crearTarjeta("Prestados", String.valueOf(biblioteca.getLibros(true).size())));
        pnlTarjetas.add(crearTarjeta("Libros con retraso", "0"));
        pnlTarjetas.add(crearTarjeta("Saldo por retardos", "$30.00"));

        pnlSuperior.setLayout(new BorderLayout(0, 18));
        pnlSuperior.setOpaque(false);
        pnlSuperior.add(pnlTarjetas, BorderLayout.NORTH);

        pnlTabla = crearPanelTabla();
        
        pnlSuperior.add(pnlTabla, BorderLayout.CENTER);
        pnlContenido.add(pnlSuperior, BorderLayout.CENTER);

    }

    private JButton crearBoton(String texto, String iconoFile) {
        ImageIcon iconoOg = new ImageIcon(iconoFile);
        Image imagen = iconoOg.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        ImageIcon icono = new ImageIcon(imagen);

        JButton boton = new JButton(texto, icono);

        boton.setFont(new Font("Arial", Font.PLAIN, 13));
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setIconTextGap(10);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setBackground(COLOR_BOTON);
        boton.setPreferredSize(new Dimension(135, 34));
        boton.setMaximumSize(new Dimension(135, 34));
        boton.setBorder(new EmptyBorder(5,10,5,10));
        
        return boton;
    }

    private JPanel crearTarjeta(String titulo, String valor) {
        JPanel tarjeta = new JPanel();
        
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBackground(COLOR_NARANJA);
        tarjeta.setBorder(new EmptyBorder(12, 12, 10, 12));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Arial", Font.PLAIN, 10));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Arial", Font.BOLD, 18));
        lblValor.setForeground(Color.WHITE);
        lblValor.setBorder(new EmptyBorder(6, 0, 0, 0));

        tarjeta.add(lblTitulo);
        tarjeta.add(lblValor);

        return tarjeta;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(COLOR_CAFE));
        
        JPanel pnlTitulo = new JPanel(new BorderLayout());
        pnlTitulo = new JPanel(new BorderLayout());
        pnlTitulo.setBackground(Color.WHITE);
        pnlTitulo.setBorder(new EmptyBorder(10, 15, 5, 15));

        lblPrstmReci.setFont(new Font("Arial", Font.BOLD, 11));
        lblVerTodos.setFont(new Font("Arial", Font.PLAIN, 10));
        lblVerTodos.setForeground(new Color(170, 70, 40));

        pnlTitulo.add(lblPrstmReci, BorderLayout.WEST);
        pnlTitulo.add(lblVerTodos, BorderLayout.EAST);

        panel.add(pnlTitulo, BorderLayout.NORTH);

        String[] columnas = {"Titulo", "Autor", "Usuario", "Fecha préstamo", "Fecha límite"};

        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override 
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for(Prestamo prestamo : biblioteca.getPrestamos()) {
            modelo.addRow(new Object[] {
                prestamo.getLibro().getTitulo(),
                prestamo.getLibro().getAutor(),
                prestamo.getUsuario().getNombre(),
                prestamo.getFechaPrestamo(),
                prestamo.getFechaDevolucion()
            });
        }

        tablaPrestamosRecientes = new JTable(modelo);
        tablaPrestamosRecientes.setFont(new Font("Arial", Font.PLAIN, 9));
        tablaPrestamosRecientes.setRowHeight(25);
        tablaPrestamosRecientes.setShowGrid(false);
        tablaPrestamosRecientes.setIntercellSpacing(new Dimension(0,0));

        tablaPrestamosRecientes.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 9));
        tablaPrestamosRecientes.getTableHeader().setBackground(Color.WHITE);
        tablaPrestamosRecientes.getTableHeader().setBorder(BorderFactory.createMatteBorder(0,0,1,0, new Color(220, 220, 220)));

        JScrollPane scroll = new JScrollPane(tablaPrestamosRecientes);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        panel.add(scroll, BorderLayout.CENTER);

        return panel;

    }

    private void crearVentana() {
        JFrame frame = new JFrame("Biblioteca Central");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setLayout(new BorderLayout());

        ImageIcon logo = new ImageIcon("library-icon.png");
        frame.setIconImage(logo.getImage());

        frame.add(pnlHeader, BorderLayout.NORTH);
        frame.add(pnlSideMenu, BorderLayout.WEST);
        frame.add(pnlContenido, BorderLayout.CENTER);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    public static void main(String[] args){
        new BibliotecaUI();
    }
}
