import javax.swing.*;
import java.awt.*;

public class DevolucionDialog extends JDialog{
    public DevolucionDialog(Window owner, Biblioteca biblioteca, Libro libro, Runnable onSave) {
        super(owner, "Devolver libro", ModalityType.APPLICATION_MODAL);
        setSize(440, 330);
        setLocationRelativeTo(owner);
        
    }
}
