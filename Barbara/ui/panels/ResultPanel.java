package ui.panels;

import model.Player;
import ui.TypingGame;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ResultPanel extends BasePanel {

    private JLabel lblExerciseName;
    private JLabel lblStars;
    private JLabel lblMessage;
    private JLabel lblXP;
    private JLabel lblAchievement;
    private JProgressBar xpBar;

    // 6 stat cards  (2 linhas × 3 colunas)
    private JLabel lblLivesValue;
    private JLabel lblScoreValue;
    private JLabel lblSequenceValue;
    private JLabel lblLevelValue;
    private JLabel lblTimeValue;
    private JLabel lblWpmValue;

    private JButton btnContinue;
    private JButton btnMenu;

    public ResultPanel(TypingGame game) {
        super(game);
    }

    @Override
    protected void initialize() {
        setLayout(new BorderLayout());
        add(buildHeader("Resultado"), BorderLayout.NORTH);
        add(buildContent(),           BorderLayout.CENTER);
        add(buildButtons(),           BorderLayout.SOUTH);
    }

    // ================================================================
    //  Content
    // ================================================================
    private JPanel buildContent() {
        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(COLOR_BG);
        content.setBorder(BorderFactory.createEmptyBorder(12, 48, 8, 48));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx  = 0;
        c.fill   = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(5, 0, 5, 0);

        // Exercise name
        c.gridy = 0;
        lblExerciseName = label("--", new Font("Arial", Font.ITALIC, 17),
                new Color(100, 100, 90), SwingConstants.CENTER);
        content.add(lblExerciseName, c);

        // Stars
        c.gridy = 1;
        lblStars = label("☆ ☆ ☆", FONT_STARS, COLOR_GOLD, SwingConstants.CENTER);
        content.add(lblStars, c);

        // Message
        c.gridy = 2;
        lblMessage = label("--", new Font("Arial", Font.BOLD, 22), COLOR_SUCCESS, SwingConstants.CENTER);
        content.add(lblMessage, c);

        // XP earned
        c.gridy = 3;
        lblXP = label("+0 pontos", new Font("Arial", Font.BOLD, 19), COLOR_PRIMARY, SwingConstants.CENTER);
        content.add(lblXP, c);

        // XP progress bar
        c.gridy = 4;
        xpBar = new JProgressBar(0, 100);
        xpBar.setForeground(COLOR_PRIMARY);
        xpBar.setBackground(new Color(195, 195, 175));
        xpBar.setBorderPainted(false);
        xpBar.setPreferredSize(new Dimension(0, 12));
        content.add(xpBar, c);

        // Separator
        c.gridy = 5;
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(180, 180, 160));
        content.add(sep, c);

        // Stats grid 2×3
        c.gridy = 6;
        content.add(buildStatsGrid(), c);

        // Achievement banner
        c.gridy = 7;
        lblAchievement = label("", new Font("Arial", Font.BOLD, 16),
                new Color(175, 125, 0), SwingConstants.CENTER);
        content.add(lblAchievement, c);

        return content;
    }

    // 2 linhas × 3 colunas
    private JPanel buildStatsGrid() {
        JPanel grid = new JPanel(new GridLayout(2, 3, 12, 10));
        grid.setBackground(COLOR_BG);
        // Altura fixa generosa: (190-10)/2 = 90px por card — sobra para qualquer fonte
        grid.setPreferredSize(new Dimension(0, 190));

        lblLivesValue    = statValue("--", new Color(210, 50, 50));
        lblScoreValue    = statValue("--", COLOR_GOLD);
        lblSequenceValue = statValue("--", COLOR_SUCCESS);
        lblLevelValue    = statValue("--", COLOR_PRIMARY);
        lblTimeValue     = statValue("--", new Color(110, 80, 170));
        lblWpmValue      = statValue("--", new Color(180, 100, 0));

        grid.add(statCard("Vidas",      lblLivesValue));
        grid.add(statCard("Pontuação",  lblScoreValue));
        grid.add(statCard("Sequência",  lblSequenceValue));
        grid.add(statCard("Nível",      lblLevelValue));
        grid.add(statCard("Tempo",      lblTimeValue));
        grid.add(statCard("PPM",        lblWpmValue));

        return grid;
    }

    private JPanel statCard(String title, JLabel valueLabel) {
        // BoxLayout com vertical glue: respeita a altura preferida dos labels
        // e nunca corta texto independente do tamanho da janela
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(COLOR_CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));

        JLabel titleLbl = label(title, new Font("Arial", Font.PLAIN, 13),
                new Color(100, 100, 90), SwingConstants.CENTER);
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalGlue());
        card.add(titleLbl);
        card.add(Box.createVerticalStrut(5));
        card.add(valueLabel);
        card.add(Box.createVerticalGlue());
        return card;
    }

    private JLabel statValue(String text, Color color) {
        return label(text, new Font("Arial", Font.BOLD, 19), color, SwingConstants.CENTER);
    }

    // ================================================================
    //  Buttons
    // ================================================================
    private JPanel buildButtons() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 12));
        bar.setBackground(COLOR_FOOTER_BG);

        btnMenu = createButton("< Menu", COLOR_BTN_BACK);
        btnMenu.addActionListener(e -> game.returnToMenu());

        btnContinue = createButton("Próximo  >", COLOR_PRIMARY);
        btnContinue.setFont(new Font("Arial", Font.BOLD, 22));
        btnContinue.setPreferredSize(new Dimension(240, 56));
        btnContinue.addActionListener(e -> game.continueGame());

        bar.add(btnMenu);
        bar.add(btnContinue);
        return bar;
    }

    // ================================================================
    //  Atualização pública — chamada pelo TypingGame
    // ================================================================
    public void showResult(Player player, int stars, int xpEarned,
                           String exerciseName, int wpm, int timeSeconds) {
        lblExerciseName.setText(exerciseName);

        lblStars.setText(starsString(stars));
        lblStars.setForeground(stars > 0 ? COLOR_GOLD : new Color(170, 170, 150));

        String msg; Color col;
        switch (stars) {
            case 3:  msg = "Excelente!  Você foi perfeito!";      col = COLOR_SUCCESS;         break;
            case 2:  msg = "Muito bem!  Continue praticando!";     col = new Color(0, 140, 80); break;
            case 1:  msg = "Bom início!  Você está evoluindo!";    col = COLOR_WARNING;         break;
            default: msg = "Não desista!  Cada tentativa conta!";  col = COLOR_DANGER;          break;
        }
        lblMessage.setText(msg);
        lblMessage.setForeground(col);

        if (xpEarned > 0) {
            lblXP.setText("+" + xpEarned + " pontos ganhos!");
            lblXP.setForeground(COLOR_PRIMARY);
        } else {
            lblXP.setText("Sem pontos desta vez.  Tente de novo!");
            lblXP.setForeground(new Color(160, 100, 0));
        }

        xpBar.setValue(Math.min(100, player.getXpProgress()));

        // Stat cards
        lblLivesValue.setText(heartsString(player.getLives()));
        lblLivesValue.setForeground(player.getLives() <= 1 ? COLOR_DANGER : new Color(210, 50, 50));

        lblScoreValue.setText(player.getTotalScore() + " pts");
        lblSequenceValue.setText(player.getStreak() + "x");
        lblLevelValue.setText(player.getLevelName());

        // Tempo formatado como m:ss
        int min = timeSeconds / 60;
        int sec = timeSeconds % 60;
        lblTimeValue.setText(min + ":" + String.format("%02d", sec));

        // PPM
        lblWpmValue.setText(wpm > 0 ? wpm + " ppm" : "—");

        // Conquistas
        List<String> ach = player.getAchievements();
        lblAchievement.setText(ach.isEmpty() ? "" :
            "  Conquista desbloqueada:  " + ach.get(ach.size() - 1) + "!");

        // Game over
        if (player.isGameOver()) {
            btnContinue.setEnabled(false);
            btnContinue.setText("Sem vidas!");
            btnContinue.setBackground(new Color(160, 160, 155));
            lblMessage.setText("Você ficou sem vidas!  Volte ao menu e tente novamente!");
            lblMessage.setForeground(COLOR_DANGER);
        } else {
            btnContinue.setEnabled(true);
            btnContinue.setText("Próximo  >");
            btnContinue.setBackground(COLOR_PRIMARY);
        }
    }
}
