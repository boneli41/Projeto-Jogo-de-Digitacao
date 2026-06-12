import ui.TypingGame;
import javax.swing.SwingUtilities;
// Try to initialize FlatLaf if available at runtime to keep compile-time
// compatibility when the library is not on the classpath.

public class Main {
    public static void main(String[] args) {
        try {
            // Inicializa o FlatLaf se a biblioteca estiver disponível em tempo de execução
            Class.forName("com.formdev.flatlaf.FlatLightLaf")
                    .getMethod("setup")
                    .invoke(null);
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new TypingGame().setVisible(true));
    }
}
