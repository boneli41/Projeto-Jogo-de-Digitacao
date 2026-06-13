package ui;

import model.Player;
import ui.panels.GamePanel;
import ui.panels.MenuPanel;
import ui.panels.ResultPanel;

import javax.swing.*;
import java.awt.*;

public class TypingGame extends JFrame {

    public static final String PANEL_MENU   = "MENU";
    public static final String PANEL_GAME   = "GAME";
    public static final String PANEL_RESULT = "RESULT";

    private final CardLayout cardLayout;
    private final JPanel     container;

    private final MenuPanel   menuPanel;
    private final GamePanel   gamePanel;
    private final ResultPanel resultPanel;

    public TypingGame() {
        setTitle("Digita Comigo  -  Aprenda a Digitar");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 850);
        setMinimumSize(new Dimension(1200, 800));
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        container  = new JPanel(cardLayout);
        add(container);

        menuPanel   = new MenuPanel(this);
        gamePanel   = new GamePanel(this);
        resultPanel = new ResultPanel(this);

        container.add(menuPanel,   PANEL_MENU);
        container.add(gamePanel,   PANEL_GAME);
        container.add(resultPanel, PANEL_RESULT);

        show(PANEL_MENU);
    }

    private void show(String name) {
        cardLayout.show(container, name);
    }

    public void startGame(Player player) {
        gamePanel.startGame(player);
        show(PANEL_GAME);
    }

    public void showResult(Player player, int stars, int xpEarned,
                           String exerciseName, int wpm, int timeSeconds) {
        resultPanel.showResult(player, stars, xpEarned, exerciseName, wpm, timeSeconds);
        show(PANEL_RESULT);
    }

    public void showFinalLevelResult(Player player) {
        player.saveToFile("ranking.txt");
        resultPanel.showFinalResult(player);
        show(PANEL_RESULT);
    }

    public void continueGame() {
        gamePanel.nextExercise();
        show(PANEL_GAME);
    }

    public void returnToMenu() {
        // Atualiza o ranking no menu antes de exibir
        menuPanel.refreshRanking();
        show(PANEL_MENU);
    }
}