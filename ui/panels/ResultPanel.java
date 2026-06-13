package ui.panels;
import model.Player;
import ui.TypingGame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
public class ResultPanel extends BasePanel {
    private JLabel lblStars;
    private JLabel lblMessage;
    private JLabel lblLivesValue;
    private JLabel lblScoreValue;
    private JLabel lblSequenceValue;
    private JLabel lblLevelValue;
    private JLabel lblTimeValue;
    private JLabel lblWpmValue;
    private JButton btnContinue;
    private JButton btnMenu;
    // Guarda o player atual para uso nos métodos internos
    private Player currentPlayer;
    public ResultPanel(TypingGame game) {
        super(game);
    }
    @Override
    protected void initialize() {
        setLayout(new BorderLayout());
        setOpaque(false);
        JPanel content = new JPanel(new BorderLayout()) {
            private final Image bgImage =
                    new ImageIcon("ui/assets/background-image-gamepanel.png").getImage();
            @Override protected void paintComponent(Graphics g) {
                g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        add(content, BorderLayout.CENTER);
        content.add(createHeader(),  BorderLayout.NORTH);
        content.add(buildContent(),  BorderLayout.CENTER);
        content.add(buildButtons(),  BorderLayout.SOUTH);
        registerEnterAction();
    }
    // ================================================================
    //  Enter global no painel
    // ================================================================
    private void registerEnterAction() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(e -> {
                    if (!isShowing()) return false;
                    if (e.getID() != KeyEvent.KEY_PRESSED) return false;
                    if (e.getKeyCode() != KeyEvent.VK_ENTER) return false;
                    if (btnContinue != null && btnContinue.isEnabled()) {
                        btnContinue.doClick();
                        return true;
                    }
                    return false;
                });
    }
    // ================================================================
    //  Header
    // ================================================================
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 180));
        header.setBorder(BorderFactory.createEmptyBorder(55, 0, 0, 0));
        ImageIcon headerIcon = new ImageIcon("ui/assets/header-resultado.png");
        if (headerIcon.getIconWidth() > 0) {
            int maxW = 850, maxH = 140;
            int imgW = headerIcon.getIconWidth(), imgH = headerIcon.getIconHeight();
            double scale = Math.min(1.0, Math.min((double) maxW / imgW, (double) maxH / imgH));
            Image scaledImg = headerIcon.getImage()
                    .getScaledInstance((int)(imgW * scale), (int)(imgH * scale), Image.SCALE_SMOOTH);
            JLabel imgLabel = new JLabel(new ImageIcon(scaledImg));
            imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
            header.add(imgLabel, BorderLayout.CENTER);
        }
        return header;
    }
    // ================================================================
    //  Conteúdo central
    // ================================================================
    private JPanel buildContent() {
        JPanel container = new JPanel(new GridBagLayout());
        container.setOpaque(false);
        container.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30));
        RoundedPanel card = new RoundedPanel(40, Color.WHITE);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(12, 0, 12, 0);
        c.gridy = 0;
        lblStars = label("\u2606 \u2606 \u2606", FONT_STARS, COLOR_GOLD, SwingConstants.CENTER);
        card.add(lblStars, c);
        c.gridy = 1;
        lblMessage = label("--", FONT_SUBTITLE, COLOR_SUCCESS, SwingConstants.CENTER);
        card.add(lblMessage, c);
        c.gridy = 2;
        card.add(buildStatsGrid(), c);
        c.gridy = 3;
        JLabel hint = label("Pressione Enter para continuar", FONT_SMALL,
                new Color(150, 150, 150), SwingConstants.CENTER);
        card.add(hint, c);
        container.add(card);
        return container;
    }
    private JPanel buildStatsGrid() {
        JPanel grid = new JPanel(new GridLayout(2, 3, 10, 8));
        grid.setOpaque(false);
        grid.setPreferredSize(new Dimension(850, 150));
        lblLivesValue    = statValue("--", Color.WHITE);
        lblLivesValue.setFont(FONT_HEARTS);
        lblScoreValue    = statValue("--", Color.WHITE);
        lblSequenceValue = statValue("--", Color.WHITE);
        lblLevelValue    = statValue("--", Color.WHITE);
        lblTimeValue     = statValue("--", Color.WHITE);
        lblWpmValue      = statValue("--", Color.WHITE);
        grid.add(statCard("Vidas",     lblLivesValue));
        grid.add(statCard("Pontuação", lblScoreValue));
        grid.add(statCard("Sequência", lblSequenceValue));
        grid.add(statCard("Nível",     lblLevelValue));
        grid.add(statCard("Tempo",     lblTimeValue));
        grid.add(statCard("PPM",       lblWpmValue));
        return grid;
    }
    private JPanel statCard(String title, JLabel valueLabel) {
        RoundedPanel card = new RoundedPanel(20, COLOR_ACCENT);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel titleLbl = label(title, FONT_SMALL, Color.WHITE, SwingConstants.CENTER);
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(Box.createVerticalGlue());
        card.add(titleLbl);
        card.add(Box.createVerticalStrut(2));
        card.add(valueLabel);
        card.add(Box.createVerticalGlue());
        return card;
    }
    private JLabel statValue(String text, Color color) {
        return label(text, FONT_INFO, color, SwingConstants.CENTER);
    }
    // ================================================================
    //  Botões do rodapé
    // ================================================================
    private JPanel buildButtons() {
        GradientPanel bar = new GradientPanel(COLOR_ACCENT, COLOR_ACCENT, false);
        bar.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 20));
        bar.setPreferredSize(new Dimension(0, 110));
        bar.setBorder(BorderFactory.createEmptyBorder(5, 40, 40, 40));
        btnMenu = createModernButton("Voltar ao Menu", COLOR_BTN_BACK);
        btnMenu.setPreferredSize(new Dimension(220, 55));
        btnMenu.addActionListener(e -> game.returnToMenu());
        btnContinue = createModernButton("Próximo  ▶", COLOR_SUCCESS);
        btnContinue.setPreferredSize(new Dimension(260, 55));
        btnContinue.setToolTipText("Enter para continuar");
        btnContinue.addActionListener(e -> game.continueGame());
        btnContinue.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "goMenu");
        btnContinue.getActionMap().put("goMenu", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { game.returnToMenu(); }
        });
        bar.add(btnMenu);
        bar.add(btnContinue);
        return bar;
    }
    // ================================================================
    //  Tela entre níveis (pequena mudança: removeActionListeners)
    // ================================================================
    public void showResult(Player player, int stars, int xpEarned,
                           String exerciseName, int wpm, int timeSeconds) {
        this.currentPlayer = player;
        lblStars.setText(starsString(stars));
        lblStars.setForeground(stars > 0 ? COLOR_GOLD : new Color(170, 170, 150));
        String msg; Color col;
        switch (stars) {
            case 3:  msg = "Excelente!  Você foi perfeito!";      col = COLOR_SUCCESS;         break;
            case 2:  msg = "Muito bem!  Continue praticando!";    col = new Color(0, 140, 80); break;
            case 1:  msg = "Bom início!  Você está evoluindo!";   col = COLOR_WARNING;         break;
            default: msg = "Não desista!  Cada tentativa conta!"; col = COLOR_DANGER;          break;
        }
        lblMessage.setText(msg);
        lblMessage.setForeground(col);
        lblLivesValue.setText(heartsString(player.getLives()));
        lblLivesValue.setForeground(player.getLives() <= 1 ? COLOR_DANGER : new Color(210, 50, 50));
        lblScoreValue.setText(player.getTotalScore() + " pts");
        lblSequenceValue.setText(player.getStreak() + "x");
        lblLevelValue.setText(player.getLevelName());
        int min = timeSeconds / 60, sec = timeSeconds % 60;
        lblTimeValue.setText(min + ":" + String.format("%02d", sec));
        lblWpmValue.setText(wpm > 0 ? wpm + " ppm" : "—");
        // Garante que o botão está com o listener certo,
        // caso o jogador venha de uma tela final anterior
        removeActionListeners(btnContinue);
        btnContinue.addActionListener(e -> game.continueGame());
        if (player.isGameOver()) {
            btnContinue.setEnabled(false);
            btnContinue.setText("Sem vidas!");
            lblMessage.setText("Você ficou sem vidas!  Volte ao menu e tente novamente!");
            lblMessage.setForeground(COLOR_DANGER);
        } else {
            btnContinue.setEnabled(true);
            btnContinue.setText("Próximo  ▶");
        }
        SwingUtilities.invokeLater(() -> {
            if (btnContinue.isEnabled()) btnContinue.requestFocusInWindow();
            else                         btnMenu.requestFocusInWindow();
        });
    }
    // ================================================================
    //  Tela final — jornada completa
    // ================================================================
    public void showFinalResult(Player player) {
        this.currentPlayer = player;
        // Três estrelas — merece ouro total
        lblStars.setText(starsString(3));
        lblStars.setForeground(COLOR_GOLD);
        lblMessage.setText("🎉  Parabéns, " + player.getName() + "!  Jornada concluída!");
        lblMessage.setForeground(COLOR_SUCCESS);
        lblLivesValue.setText(heartsString(player.getLives()));
        lblLivesValue.setForeground(player.getLives() <= 1 ? COLOR_DANGER : new Color(210, 50, 50));
        lblScoreValue.setText(player.getTotalScore() + " pts");
        lblSequenceValue.setText(player.getStreak() + "x");
        lblLevelValue.setText(player.getLevelName());
        // Tempo e PPM não fazem sentido no agregado
        lblTimeValue.setText("—");
        lblWpmValue.setText("—");
        // Troca o botão para "Ver Ranking"
        removeActionListeners(btnContinue);
        btnContinue.setEnabled(true);
        btnContinue.setText("Ver Ranking  🏆");
        btnContinue.addActionListener(e -> showRankingDialog());
        SwingUtilities.invokeLater(() -> btnContinue.requestFocusInWindow());
    }
    // ================================================================
    //  Diálogo de ranking
    // ================================================================
    private void showRankingDialog() {
        JDialog dialog = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "🏆  Ranking", true);
        dialog.setSize(480, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));
        JLabel title = new JLabel("🏆  Ranking de Jogadores");
        title.setFont(FONT_SUBTITLE);
        title.setForeground(COLOR_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(16));
        List<String[]> ranking = Player.loadRanking("ranking.txt");
        if (ranking.isEmpty()) {
            JLabel empty = new JLabel("Nenhum jogador ainda!");
            empty.setFont(FONT_BODY);
            empty.setForeground(COLOR_SECONDARY);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(empty);
        } else {
            String[] medals = {"🥇", "🥈", "🥉", "4º", "5º", "6º", "7º", "8º", "9º", "10º"};
            int max = Math.min(ranking.size(), medals.length);
            for (int i = 0; i < max; i++) {
                String[] parts = ranking.get(i);
                JPanel row = new JPanel(new GridLayout(1, 4, 8, 0));
                row.setOpaque(false);
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
                row.add(label(medals[i],                                    FONT_BODY, COLOR_PRIMARY,   SwingConstants.CENTER));
                row.add(label(parts[0],                                     FONT_BODY, COLOR_TEXT,      SwingConstants.LEFT));
                row.add(label(parts[1] + " pts",                            FONT_BODY, COLOR_SECONDARY, SwingConstants.CENTER));
                row.add(label(parts.length >= 3 ? "Nív. " + parts[2] : "", FONT_BODY, COLOR_ACCENT,    SwingConstants.CENTER));
                panel.add(row);
                panel.add(Box.createVerticalStrut(6));
            }
        }
        panel.add(Box.createVerticalStrut(20));
        JButton closeBtn = createModernButton("Voltar ao Menu", COLOR_BTN_BACK);
        closeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeBtn.setMaximumSize(new Dimension(220, 48));
        closeBtn.addActionListener(e -> {
            dialog.dispose();
            game.returnToMenu();
        });
        panel.add(closeBtn);
        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        dialog.add(scroll);
        dialog.setVisible(true);
    }
    // ================================================================
    //   Helper — remove listeners antes de reatribuir
    // ================================================================
    private void removeActionListeners(JButton btn) {
        for (ActionListener al : btn.getActionListeners()) {
            btn.removeActionListener(al);
        }
    }
}