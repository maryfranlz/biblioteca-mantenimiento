import java.awt.*;
import java.util.function.BiConsumer;

import javax.swing.*;

public class EditorBoton extends DefaultCellEditor {
    private final JButton boton = new JButton();
    private final BiConsumer<Integer, Integer> action;
    private int fila;
    private int columna;

    public EditorBoton(JCheckBox checkBox, BiConsumer<Integer, Integer> action){
        super(checkBox);
        this.action = action;
        boton.setOpaque(true);
        boton.setFont(new Font("Arial", Font.PLAIN, 10));
        boton.addActionListener(e -> {
            fireEditingStopped();
            action.accept(fila, columna);
        });
    }
    
    public Component getTableCellEditorComponent(JTable table, Object value, boolean selected, int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
        String texto = value == null ? "" : value.toString();
        boton.setText(texto);
        boolean esEliminar = "Eliminar".equalsIgnoreCase(texto);
        boton.setBackground(esEliminar ? new Color(220, 100, 90) : Recursos.COLOR_BOTON);
        boton.setForeground(esEliminar ? Color.WHITE : Color.BLACK);
        return boton;
    }

    public Object getCellEditorValue() { return boton.getText(); }
}
