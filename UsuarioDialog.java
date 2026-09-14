import java.awt.*;
import javax.swing.*;

public class UsuarioDialog extends JDialog{
    
    public UsuarioDialog(Window owner, Biblioteca biblioteca, Usuario usuario, Runnable onSave){
        super(owner, usuario == null ? "Nuevo usuario" : "Editar usuario", ModalityType.APPLICATION_MODAL);
        setSize(300, 160);
        setLocationRelativeTo(owner);

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JTextField txtUsuario = new JTextField(usuario == null ? "" : usuario.getNombre());

        panel.add(new JLabel("Nombre del usuario"));
        panel.add(txtUsuario);

        JButton btnCancelar = new JButton("Cancelar");
        JButton btnGuardar = new JButton(usuario == null ? "Agregar usuario" : "Guardar cambios");

        btnCancelar.addActionListener(e -> dispose());
        btnGuardar.addActionListener(e -> {
            try {
                if(txtUsuario.getText().isBlank()) {
                    throw new IllegalArgumentException("Completa los campos obligatorios");
                }
                if(usuario == null){
                    biblioteca.agregarUsuario(new Usuario(txtUsuario.getText().trim(), biblioteca.generarIdUsuario()));
                } else {
                    usuario.setNombre(txtUsuario.getText().trim());
                }

                onSave.run();
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Datos inválidos", JOptionPane.WARNING_MESSAGE);

            }
        });

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(btnCancelar);
        botones.add(btnGuardar);
        add(panel, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
    }
}
