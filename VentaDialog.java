import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class VentaDialog extends JDialog {

    public VentaDialog(Window owner, Biblioteca biblioteca, Libro libro, Runnable onSave) {
        super(owner, "Venta de Libro", ModalityType.APPLICATION_MODAL);
        setSize(480, 440);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        // Panel superior: Resumen del libro y precio
        JPanel pnlResumen = new JPanel(new GridLayout(0, 1, 4, 4));
        pnlResumen.setBackground(new Color(248, 249, 250));
        pnlResumen.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(14, 16, 6, 16),
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Recursos.COLOR_NARANJA, 1),
                "Información del Libro",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12),
                Recursos.COLOR_ROJO_OSCURO
            )
        ));

        JLabel lblTitulo = new JLabel("  Título: " + libro.getTitulo());
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 13));

        JLabel lblIsbn = new JLabel("  ISBN: " + libro.getIsbn() + "  |  Categoría: " + (libro.getCategoria() != null ? libro.getCategoria().name() : "N/A"));
        lblIsbn.setFont(new Font("Arial", Font.PLAIN, 12));
        lblIsbn.setForeground(Color.DARK_GRAY);

        JLabel lblPrecio = new JLabel(String.format("  Total a cobrar: $%.2f", libro.getPrecio()));
        lblPrecio.setFont(new Font("Arial", Font.BOLD, 14));
        lblPrecio.setForeground(new Color(40, 167, 69)); // Verde

        pnlResumen.add(lblTitulo);
        pnlResumen.add(lblIsbn);
        pnlResumen.add(lblPrecio);

        // Panel central: Formulario de datos del comprador
        JPanel pnlFormulario = new JPanel(new GridLayout(0, 2, 8, 12));
        pnlFormulario.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(4, 16, 10, 16),
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                "Datos del Comprador",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12),
                Color.BLACK
            )
        ));

        JTextField txtNombre = new JTextField();
        JTextField txtIdentificacion = new JTextField();
        JTextField txtTelefono = new JTextField();
        JTextField txtCorreo = new JTextField();

        pnlFormulario.add(new JLabel("  Nombre completo:*"));
        pnlFormulario.add(txtNombre);
        pnlFormulario.add(new JLabel("  Identificación / DNI:*"));
        pnlFormulario.add(txtIdentificacion);
        pnlFormulario.add(new JLabel("  Teléfono:*"));
        pnlFormulario.add(txtTelefono);
        pnlFormulario.add(new JLabel("  Correo electrónico:*"));
        pnlFormulario.add(txtCorreo);

        // Panel inferior: Botones de acción
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setFocusPainted(false);
        btnCancelar.addActionListener(e -> dispose());

        JButton btnConfirmar = new JButton("Confirmar Venta");
        btnConfirmar.setBackground(Recursos.COLOR_ROJO_OSCURO);
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.setFocusPainted(false);
        btnConfirmar.setFont(new Font("Arial", Font.BOLD, 12));

        btnConfirmar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String identificacion = txtIdentificacion.getText().trim();
            String telefono = txtTelefono.getText().trim();
            String correo = txtCorreo.getText().trim();

            if (nombre.isBlank() || identificacion.isBlank() || telefono.isBlank() || correo.isBlank()) {
                JOptionPane.showMessageDialog(
                    this,
                    "Todos los campos marcados con (*) son obligatorios.",
                    "Campos incompletos",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            if (!correo.contains("@") || !correo.contains(".")) {
                JOptionPane.showMessageDialog(
                    this,
                    "Por favor ingresa un correo electrónico válido.",
                    "Correo inválido",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            if (biblioteca.registrarVenta(libro.getIsbn(), nombre, identificacion, telefono, correo)) {
                String comprobante = String.format(
                    "¡Venta registrada con éxito!\n\n" +
                    "--- Comprobante de Venta ---\n" +
                    "Libro: %s\n" +
                    "ISBN: %s\n" +
                    "Total cobrado: $%.2f\n\n" +
                    "--- Datos del Comprador ---\n" +
                    "Nombre: %s\n" +
                    "Identificación / DNI: %s\n" +
                    "Teléfono: %s\n" +
                    "Correo: %s\n" +
                    "-----------------------------------",
                    libro.getTitulo(),
                    libro.getIsbn(),
                    libro.getPrecio(),
                    nombre,
                    identificacion,
                    telefono,
                    correo
                );

                JOptionPane.showMessageDialog(
                    this,
                    comprobante,
                    "Venta Exitosa",
                    JOptionPane.INFORMATION_MESSAGE
                );

                if (onSave != null) {
                    onSave.run();
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "No se pudo completar la venta. El libro podría estar prestado o ya vendido.",
                    "Error en la venta",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        pnlBotones.setBorder(new EmptyBorder(0, 10, 10, 16));
        pnlBotones.add(btnCancelar);
        pnlBotones.add(btnConfirmar);

        add(pnlResumen, BorderLayout.NORTH);
        add(pnlFormulario, BorderLayout.CENTER);
        add(pnlBotones, BorderLayout.SOUTH);
    }
}
