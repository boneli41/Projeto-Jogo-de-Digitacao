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

    private final CardLayout  cardLayout;
    private final JPanel      container;

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

    private void show(String name) { cardLayout.show(container, name); }

    // ----------------------------------------------------------------
    // Inicia o jogo num módulo específico (escolha livre do menu)
    // ----------------------------------------------------------------
    public void startGame(Player player, int campaignLevel) {
        gamePanel.startGame(player, campaignLevel);
        show(PANEL_GAME);
    }

    // Mantém compatibilidade: sem escolha = começa no nível 1
    public void startGame(Player player) {
        startGame(player, 1);
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
        if (gamePanel.nextExercise()) show(PANEL_GAME);
    }

    public void returnToMenu() {
        menuPanel.refreshRanking();
        show(PANEL_MENU);
    }

    /** Abre o dialog de ranking centralizado no MenuPanel. */
    public void showRankingDialog() {
        menuPanel.showRankingDialog();
    }
}