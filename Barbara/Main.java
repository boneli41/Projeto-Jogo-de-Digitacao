import ui.TypingGame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        // Metal L&F respeita setBackground nos botoes; o L&F do Windows ignora
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new TypingGame().setVisible(true));
    }
}
