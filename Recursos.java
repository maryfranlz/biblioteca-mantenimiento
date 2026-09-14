import java.awt.*;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class Recursos {
    public static final Color COLOR_FONDO = new Color(245, 245, 245);
    public static final Color COLOR_NARANJA = new Color(239, 128, 20);
    public static final Color COLOR_CAFE = new Color(39, 3, 0);
    public static final Color COLOR_BOTON = new Color(230, 176, 157);
    public static final Color COLOR_MENU = new Color(255, 255, 255);
    public static final Color COLOR_ROJO_OSCURO = new Color(143, 41, 4);

    private Recursos() {

    }

    public static JButton crearBoton(String texto, String iconoFile) {
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
        boton.setPreferredSize(new Dimension(145, 34));
        boton.setMaximumSize(new Dimension(145, 34));
        boton.setBorder(new EmptyBorder(5,10,5,10));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return boton;
    }
}
