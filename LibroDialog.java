import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;

import javax.swing.*;

public class LibroDialog extends JDialog{
    
    public LibroDialog(Window owner, Biblioteca biblioteca, Libro libro, Runnable onSave){
        super(owner, libro == null ? "Nuevo libro" : "Editar libro", ModalityType.APPLICATION_MODAL);
        setSize(420, 360);
        setLocationRelativeTo(owner);

        JPanel panel = new JPanel(new GridLayout(0,2,8,8));
        panel.setBorder(BorderFactory.createEmptyBorder(18,18,18,18));

        JTextField txtTitulo = new JTextField(libro == null ? "" : libro.getTitulo());
        JTextField txtAutor = new JTextField(libro == null ? "" : libro.getAutor());
        JTextField txtIsbn = new JTextField(libro == null ? "" : libro.getIsbn());
        JComboBox<CategoriaLibro> categoria = new JComboBox<>(CategoriaLibro.values());
        JTextField txtPrecio = new JTextField(libro == null ? "0" : String.valueOf(libro.getPrecio()));

        if(libro != null) {
            categoria.setSelectedItem(libro.getCategoria());
        }

        panel.add(new JLabel("Título"));
        panel.add(txtTitulo);
        panel.add(new JLabel("Autor"));
        panel.add(txtAutor);
        panel.add(new JLabel("ISBN"));
        panel.add(txtIsbn);
        panel.add(new JLabel("Categoría"));
        panel.add(categoria);
        panel.add(new JLabel("Precio"));
        panel.add(txtPrecio);

        JButton btnCancelar = new JButton("Cancelar");
        JButton btnGuardar = new JButton(libro == null ? "Agregar libro" : "Guardar cambios");

        btnCancelar.addActionListener(e -> dispose());
        btnGuardar.addActionListener(e -> {
            try {
                if(txtTitulo.getText().isBlank() || txtAutor.getText().isBlank() || txtIsbn.getText().isBlank()) {
                    throw new IllegalArgumentException("Completa los campos obligatorios");
                }
                if(libro == null){
                    if(biblioteca.buscarLibroPorIsbn(txtIsbn.getText().trim()) != null) {
                        throw new IllegalArgumentException("El ISBN ya existe");
                    }
                    biblioteca.agregarLibro(new Libro(
                        txtTitulo.getText().trim(),
                        txtAutor.getText().trim(),
                        txtIsbn.getText().trim(),
                        (CategoriaLibro) categoria.getSelectedItem(),
                        Double.parseDouble(txtPrecio.getText())
                    ));
                } else {
                    libro.setTitulo(txtTitulo.getText().trim());
                    libro.setAutor(txtAutor.getText().trim());
                    libro.setIsbn(txtIsbn.getText().trim());
                    libro.setCategoria((CategoriaLibro) categoria.getSelectedItem());
                    libro.setPrecio(Double.parseDouble(txtPrecio.getText()));
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
