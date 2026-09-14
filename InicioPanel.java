import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class InicioPanel extends JPanel{

    private Biblioteca biblioteca;
    private JPanel tarjetas = new JPanel(new GridLayout(1, 5, 12, 0));

    public InicioPanel(Biblioteca biblioteca) {
        this.biblioteca = biblioteca;
        setLayout(new BorderLayout(0, 18));
        setBackground(Recursos.COLOR_FONDO);
        setBorder(new EmptyBorder(24, 22, 25, 22));

        tarjetas.setOpaque(false);
        add(tarjetas, BorderLayout.NORTH);
        actualizar();
    }

    public void actualizar() {
        tarjetas.removeAll();

        int total = biblioteca.getLibros().size();
        int prestados = biblioteca.getLibros(true).size();
        int disponibles = total - prestados;
        long retrasos = biblioteca.getPrestamos().stream().filter(p -> p.getEstado() == Prestamo.RETRASO).count();
        double saldo = biblioteca.getPrestamos().stream().filter(p -> p.getRecargo() != null).mapToDouble(Prestamo::getRecargo).sum();

        tarjetas.add(tarjeta("Libros totales", String.valueOf(total)));
        tarjetas.add(tarjeta("Disponibles", String.valueOf(disponibles)));
        tarjetas.add(tarjeta("Prestados", String.valueOf(prestados)));
        tarjetas.add(tarjeta("Libros con retraso", String.valueOf(retrasos)));
        tarjetas.add(tarjeta("Saldo por retraso", String.format("$%.2f", saldo)));
    
        revalidate();
        repaint();
    }

    public JPanel tarjeta(String texto, String valor) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Recursos.COLOR_NARANJA);
        panel.setBorder(new EmptyBorder(12, 12, 10, 12));

        JLabel t = new JLabel(texto);
        t.setFont(new Font("Arial", Font.PLAIN, 15));
        t.setForeground(Color.WHITE);
        JLabel v = new JLabel(valor);
        v.setFont(new Font("Arial", Font.BOLD, 25));
        v.setForeground(Color.WHITE);
        v.setBorder(new EmptyBorder(6, 0, 0, 0));

        panel.add(t);
        panel.add(v);

        return panel;
    }
}
