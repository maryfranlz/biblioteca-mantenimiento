import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class BibliotecaUI{

    private Biblioteca biblioteca;
    private CardLayout cardLayout = new CardLayout();
    private JPanel contenido = new JPanel(cardLayout);
    private InicioPanel inicioPanel;

    public BibliotecaUI() {
        crearBiblioteca();
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

    private void crearVentana() {
        JFrame frame = new JFrame("Biblioteca Central");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        ImageIcon logo = new ImageIcon("library-icon.png");
        frame.setIconImage(logo.getImage());

        JPanel side = crearSideMenu();
        JPanel header = crearHeader();

        inicioPanel = new InicioPanel(biblioteca);
        LibrosPanel librosPanel = new LibrosPanel(biblioteca, inicioPanel);
        //UsuariosPanel usuariosPanel = new UsuariosPanel(biblioteca);
        //PrestamosPanel prestamosPanel = new PrestamosPanel(biblioteca);

        contenido.setBackground(Recursos.COLOR_FONDO);
        contenido.add(inicioPanel, "INICIO");
        contenido.add(librosPanel, "LIBROS");
        //contenido.add(usuariosPanel, "USUARIOS");
        //contenido.add(prestamosPanel, "PRESTAMOS");

        frame.add(header, BorderLayout.NORTH);
        frame.add(side, BorderLayout.WEST);
        frame.add(contenido, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private JPanel crearHeader() {
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setPreferredSize(new Dimension(0, 60));

        JLabel frasePrincipito = new JLabel("'Sólo con el corazón se puede ver bien, lo esencial es invisible para los ojos'");
        frasePrincipito.setFont(new Font("Bell MT", Font.ITALIC, 16));
        frasePrincipito.setForeground(Color.DARK_GRAY);
        frasePrincipito.setHorizontalAlignment(SwingConstants.RIGHT);
        frasePrincipito.setBorder(new EmptyBorder(8,20,8,20));

        pnlHeader.add(frasePrincipito, BorderLayout.CENTER);

        return pnlHeader;
    }

    private JPanel crearSideMenu() {
        JPanel pnlSideMenu = new JPanel();
        pnlSideMenu.setLayout(new BoxLayout(pnlSideMenu, BoxLayout.Y_AXIS));
        pnlSideMenu.setBackground(Color.WHITE);
        pnlSideMenu.setBorder(new EmptyBorder(20, 14, 20, 14));
        pnlSideMenu.setPreferredSize(new Dimension(180, 0));

        JLabel lblLogo = new JLabel("<html>Biblioteca<br>Central<html>");
        lblLogo.setFont(new Font("Arial", Font.BOLD, 17));
        lblLogo.setForeground(Recursos.COLOR_ROJO_OSCURO);
        lblLogo.setBorder(new EmptyBorder(0,6,24,0));

        ImageIcon libraryIcon = new ImageIcon("library-icon.png");
        Image smallerLibraryIcon = libraryIcon.getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
        ImageIcon hdrIcon = new ImageIcon(smallerLibraryIcon);

        lblLogo.setIcon(hdrIcon);
        lblLogo.setHorizontalTextPosition(JLabel.RIGHT);
        lblLogo.setIconTextGap(15);

        JButton btnInicio = Recursos.crearBoton("Inicio", "dashboard.png");
        JButton btnLibros = Recursos.crearBoton("Libros", "books.png");
        JButton btnUsuarios = Recursos.crearBoton("Usuarios", "user.png");
        JButton btnPrestamos = Recursos.crearBoton("Prestamos", "exchange.png");
        
        pnlSideMenu.add(lblLogo);
        pnlSideMenu.add(btnInicio);
        pnlSideMenu.add(Box.createVerticalStrut(10));
        pnlSideMenu.add(btnLibros);
        pnlSideMenu.add(Box.createVerticalStrut(10));
        pnlSideMenu.add(btnUsuarios);
        pnlSideMenu.add(Box.createVerticalStrut(10));
        pnlSideMenu.add(btnPrestamos);

        btnInicio.addActionListener(e -> cardLayout.show(contenido, "INICIO"));
        btnLibros.addActionListener(e -> cardLayout.show(contenido, "LIBROS"));
        btnUsuarios.addActionListener(e -> cardLayout.show(contenido, "USUARIOS"));
        btnPrestamos.addActionListener(e -> cardLayout.show(contenido, "PRESTAMOS"));

        return pnlSideMenu;
    }
    
    public static void main(String[] args){
        new BibliotecaUI();
    }
}
