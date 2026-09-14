import java.awt.*;
import javax.swing.*;

public class PrestamoDialog extends JDialog{
    
    public PrestamoDialog(Window owner, Biblioteca biblioteca, Libro libro, Runnable onSave) {
        super(owner, "Prestar libro", ModalityType.APPLICATION_MODAL);
        setSize(440, 330);
        setLocationRelativeTo(owner);
    }
}
