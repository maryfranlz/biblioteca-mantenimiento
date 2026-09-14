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
        setText(value == null ? "" : value.toString());
        setBackground(column == 9 ? new Color(220, 100, 90) : Recursos.COLOR_BOTON);
        setForeground(column == 9 ? Color.WHITE : Color.BLACK);
        return this;
    }
}
