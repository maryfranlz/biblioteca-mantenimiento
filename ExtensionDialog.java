import java.awt.*;
import javax.swing.*;

public class ExtensionDialog extends JDialog{
    
    public ExtensionDialog(Window owner, Biblioteca biblioteca, Libro libro, Runnable onSave) {
        super(owner, "Extender préstamo", ModalityType.APPLICATION_MODAL);
        setSize(440, 300);
        setLocationRelativeTo(owner);
    }
}
