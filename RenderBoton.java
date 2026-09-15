import java.awt.*;
import javax.swing.*;

public class RenderBoton extends JButton implements javax.swing.table.TableCellRenderer {

    public RenderBoton() {
        setOpaque(true);
        setFont(new Font("Arial", Font.PLAIN, 10));
        setFocusPainted(false);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean selected, boolean focused, int row, int column) {
        String texto = value == null ? "" : value.toString();
        setText(texto);
        boolean esEliminar = "Eliminar".equalsIgnoreCase(texto);
        setBackground(esEliminar ? new Color(220, 100, 90) : Recursos.COLOR_BOTON);
        setForeground(esEliminar ? Color.WHITE : Color.BLACK);
        return this;
    }
}
