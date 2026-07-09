import controller.Controlador;
import view.Vista;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Punto de entrada de la aplicacion.
 * Ensambla la arquitectura MVC: crea el Controlador (que inicializa el
 * Modelo) y se lo inyecta a la Vista.
 */
public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Si el Look and Feel del sistema no esta disponible, se usa el por defecto.
            }
            Controlador controller = new Controlador();
            new Vista(controller);
        });
    }
}
