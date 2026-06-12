package ui.panels;

import factory.ExerciseFactory;
import model.Exercise;
import model.Player;
import ui.TypingGame;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class GamePanel extends BasePanel {

    // -------- Estado do Modelo --------
    private Player player;
    private List<Exercise> exercises;
    private int exerciseIndex;
    private Exercise current;
    private boolean active;
    private long startTime;
    private int timeLeft;

    // -------- Controle de auto-avançar --------
    private Timer autoAdvanceTimer;
    private boolean exerciseDone;
    private int pendingStars, pendingXp, pendingWpm, pendingTime;

    // -------- Barra superior --------
    private JLabel lblPlayerName;
    private JLabel lblLevel;
    private JLabel lblLives;
    private JLabel lblScore;
    private JLabel lblTimer;

    // -------- Barra lateral --------
    private JLabel lblSideXP;
    private JLabel lblSideLevel;
    private JLabel lblSideStreak;
    private JLabel lblNextLevelHint;
    private JProgressBar xpBar;

    // -------- Centro --------
    private JLabel lblCategory;
    private JLabel lblInstructions;
    private JTextPane txtExercise;
    private JTextField txtInput;
    private JLabel lblFeedback;
    private JLabel lblAutoAdvance;
    private KeyboardPanel keyboardPanel;   // ← teclado visual
    private JButton btnAdvance;            // ← botão "Avançar" visível após concluir

    // -------- Rodapé --------
    private JProgressBar progressBar;
    private JLabel lblProgress;

    // -------- Timer countdown --------
    private Timer countdown;

    // -------- Cores --------
    private static final Color COL_CORRECT_FG = new Color( 27, 135,  50);
    private static final Color COL_CORRECT_BG = new Color(220, 255, 220);
    private static final Color COL_WRONG_FG   = new Color(180,  20,  20);
    private static final Color COL_WRONG_BG   = new Color(255, 220, 220);
    private static final Color COL_CURSOR_BG  = new Color(180, 220, 255);
    private static final Color COL_NORMAL_FG  = new Color( 60,  60,  60);

    private static final Font FONT_EXERCISE_BIG = new Font("Nunito Sans", Font.PLAIN, 36);
    private static final Font FONT_INPUT_BIG    = new Font("Poppins",     Font.PLAIN, 28);

    public GamePanel(TypingGame game) {
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

        content.add(buildTopBar(),          BorderLayout.NORTH);
        content.add(buildSidebar(),         BorderLayout.EAST);
        content.add(buildCenter(),          BorderLayout.CENTER);
        content.add(createFooterProgress(), BorderLayout.SOUTH);
    }

    // ================================================================
    //  Barra Superior
    // ================================================================
    private JPanel buildTopBar() {
        GradientPanel bar = new GradientPanel(COLOR_PRIMARY, COLOR_PRIMARY, true);
        bar.setPreferredSize(new Dimension(0, 100));
        bar.setLayout(new BorderLayout(25, 0));
        bar.setBorder(BorderFactory.createEmptyBorder(8, 40, 8, 40));

        JPanel left = new JPanel(new GridLayout(2, 1, 0, 1));
        left.setOpaque(false);
        lblPlayerName = label("Jogador",        FONT_INFO,  Color.WHITE,             SwingConstants.LEFT);
        lblLevel      = label("Nível 1 — Iniciante", FONT_SMALL, new Color(200,225,255), SwingConstants.LEFT);
        left.add(lblPlayerName);
        left.add(lblLevel);

        lblLives = label("\u2764 \u2764 \u2764", FONT_HEARTS, new Color(255,110,110), SwingConstants.CENTER);

        JPanel right = new JPanel(new GridLayout(2, 1, 0, 1));
        right.setOpaque(false);
        lblScore = label("0 pts", FONT_INFO,      COLOR_GOLD,  SwingConstants.RIGHT);
        lblTimer = label("--s",   FONT_TIMER_BIG, Color.WHITE, SwingConstants.RIGHT);
        right.add(lblScore);
        right.add(lblTimer);

        bar.add(left,     BorderLayout.WEST);
        bar.add(lblLives, BorderLayout.CENTER);
        bar.add(right,    BorderLayout.EAST);
        return bar;
    }

    // ================================================================
    //  Barra Lateral
    // ================================================================
    private JPanel buildSidebar() {
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setOpaque(false);
        side.setPreferredSize(new Dimension(250, 0));
        side.setBorder(BorderFactory.createEmptyBorder(16, 15, 25, 40));

        JLabel title = label("SEU STATUS", FONT_SMALL, COLOR_SECONDARY, SwingConstants.LEFT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        side.add(title);
        side.add(Box.createVerticalStrut(20));
        side.add(sidebarCard("Pontos XP",
                lblSideXP     = label("0",  FONT_SUBTITLE, Color.WHITE, SwingConstants.CENTER), COLOR_ACCENT));
        side.add(Box.createVerticalStrut(15));
        side.add(sidebarCard("Sequência",
                lblSideStreak = label("0x", FONT_SUBTITLE, Color.WHITE, SwingConstants.CENTER), COLOR_ACCENT));
        side.add(Box.createVerticalStrut(15));
        side.add(sidebarCard("Exercício",
                lblSideLevel  = label("1",  FONT_SUBTITLE, Color.WHITE, SwingConstants.CENTER), COLOR_ACCENT));
        side.add(Box.createVerticalStrut(20));

        // Card XP com barra de progresso
        RoundedPanel xpCard = new RoundedPanel(20, COLOR_ACCENT);
        xpCard.setLayout(new BoxLayout(xpCard, BoxLayout.Y_AXIS));
        xpCard.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        xpCard.setMaximumSize(new Dimension(210, 80));

        lblNextLevelHint = label("Nível 1 → 2", FONT_SMALL, Color.WHITE, SwingConstants.CENTER);
        lblNextLevelHint.setAlignmentX(Component.CENTER_ALIGNMENT);

        xpBar = new JProgressBar(0, 100);
        xpBar.setValue(0);
        xpBar.setForeground(COLOR_GOLD);
        xpBar.setBackground(new Color(255, 255, 255, 60));
        xpBar.setBorderPainted(false);
        xpBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 14));

        xpCard.add(lblNextLevelHint);
        xpCard.add(Box.createVerticalStrut(6));
        xpCard.add(xpBar);
        side.add(xpCard);

        return side;
    }

    private JPanel sidebarCard(String title, JLabel valueLabel, Color bg) {
        RoundedPanel card = new RoundedPanel(25, bg);
        card.setLayout(new BorderLayout(0, 5));
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        card.add(label(title, FONT_SMALL, Color.WHITE, SwingConstants.CENTER), BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    // ================================================================
    //  Área Central
    // ================================================================
    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(0, 8));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(14, 40, 20, 20));

        // --- Topo: categoria + instruções ---
        JPanel infoRow = new JPanel(new GridLayout(2, 1, 0, 4));
        infoRow.setOpaque(false);
        lblCategory     = label("Categoria",  FONT_INFO,  COLOR_PRIMARY,          SwingConstants.CENTER);
        lblInstructions = label("Instruções", FONT_SMALL, new Color(80, 80, 80),   SwingConstants.CENTER);
        infoRow.add(lblCategory);
        infoRow.add(lblInstructions);
        center.add(infoRow, BorderLayout.NORTH);

        // --- Centro: cartão do exercício ---
        RoundedPanel card = new RoundedPanel(40, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(24, 40, 16, 40));

        txtExercise = new JTextPane();
        txtExercise.setEditable(false);
        txtExercise.setFont(FONT_EXERCISE_BIG);
        txtExercise.setBackground(COLOR_WHITE);
        txtExercise.setFocusable(false);
        txtExercise.setPreferredSize(new Dimension(0, 130));

        lblFeedback = label(" ", new Font("Nunito Sans", Font.BOLD, 22),
                COLOR_SUCCESS, SwingConstants.CENTER);
        lblFeedback.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

        // Label do contador de auto-avance
        lblAutoAdvance = label(" ", new Font("Poppins", Font.PLAIN, 14),
                COLOR_PRIMARY, SwingConstants.CENTER);

        // Botão Avançar (oculto durante o jogo, aparece ao concluir)
        btnAdvance = createModernButton("Avançar  ▶", COLOR_SUCCESS);
        btnAdvance.setFont(new Font("Poppins SemiBold", Font.BOLD, 16));
        btnAdvance.setPreferredSize(new Dimension(200, 40));
        btnAdvance.setVisible(false);
        btnAdvance.addActionListener(e -> advanceNow());

        JPanel cardSouth = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        cardSouth.setOpaque(false);
        cardSouth.add(lblFeedback);
        cardSouth.add(lblAutoAdvance);
        cardSouth.add(btnAdvance);

        card.add(txtExercise, BorderLayout.CENTER);
        card.add(cardSouth,   BorderLayout.SOUTH);
        center.add(card, BorderLayout.CENTER);

        // --- Sul: teclado + campo de digitação ---
        JPanel southSection = new JPanel(new BorderLayout(0, 6));
        southSection.setOpaque(false);

        // Teclado visual
        keyboardPanel = new KeyboardPanel();
        keyboardPanel.setPreferredSize(new Dimension(0, 115));
        southSection.add(keyboardPanel, BorderLayout.CENTER);

        // Linha de digitação
        JPanel inputRow = new JPanel(new BorderLayout(10, 0));
        inputRow.setOpaque(false);
        inputRow.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

        JLabel promptLabel = label("Digite aqui:  ", FONT_BODY, COLOR_TEXT, SwingConstants.LEFT);
        promptLabel.setPreferredSize(new Dimension(165, 60));

        txtInput = new JTextField();
        txtInput.setFont(FONT_INPUT_BIG);
        txtInput.setPreferredSize(new Dimension(0, 60));
        txtInput.putClientProperty("JTextField.placeholderText", "Comece a digitar aqui...");
        txtInput.putClientProperty("JComponent.roundRect", true);

        // ----------------------------------------------------------------
        //  KeyListener: Enter avança, Espaço só avança se exerciseDone
        //  (evita o bug de voltar ao menu com espaço)
        // ----------------------------------------------------------------
        txtInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (exerciseDone && e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();   // consome antes de qualquer outra ação
                    advanceNow();
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
                if (exerciseDone) return;  // bloqueia digitação após concluir
                if (active) onInput();
            }
        });

        // Registra Enter também via InputMap para garantir (foco pode variar)
        txtInput.getInputMap(JComponent.WHEN_FOCUSED)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "advanceAction");
        txtInput.getActionMap().put("advanceAction", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                if (exerciseDone) advanceNow();
            }
        });

        inputRow.add(promptLabel, BorderLayout.WEST);
        inputRow.add(txtInput,    BorderLayout.CENTER);

        southSection.add(inputRow, BorderLayout.SOUTH);
        center.add(southSection, BorderLayout.SOUTH);

        return center;
    }

    // ================================================================
    //  Rodapé
    // ================================================================
    private JPanel createFooterProgress() {
        GradientPanel bar = new GradientPanel(COLOR_ACCENT, COLOR_ACCENT, false);
        bar.setPreferredSize(new Dimension(0, 85));
        bar.setLayout(new BorderLayout(15, 0));
        bar.setBorder(BorderFactory.createEmptyBorder(10, 40, 25, 40));

        lblProgress = label("Exercicio 1 de 47", FONT_SMALL, Color.WHITE, SwingConstants.LEFT);
        lblProgress.setPreferredSize(new Dimension(200, 24));

        progressBar = new JProgressBar(0, 100);
        progressBar.setForeground(Color.WHITE);
        progressBar.setBackground(new Color(255, 255, 255, 60));
        progressBar.setBorderPainted(false);
        progressBar.setPreferredSize(new Dimension(0, 18));

        JButton menuBtn = createModernButton("Sair", new Color(255, 255, 255, 40));
        menuBtn.setForeground(Color.WHITE);
        menuBtn.setPreferredSize(new Dimension(100, 40));
        menuBtn.addActionListener(e -> goMenu());

        bar.add(lblProgress, BorderLayout.WEST);
        bar.add(progressBar, BorderLayout.CENTER);
        bar.add(menuBtn,     BorderLayout.EAST);
        return bar;
    }

    // ================================================================
    //  API Pública
    // ================================================================
    public void startGame(Player p) {
        this.player = p;
        this.exercises = ExerciseFactory.createCampaign();
        this.exerciseIndex = 0;
        loadExercise();
    }

    public void nextExercise() {
        exerciseIndex++;
        if (exerciseIndex >= exercises.size()) {
            game.showFinalLevelResult(player);
            return;
        }
        loadExercise();
    }

    // ================================================================
    //  Carregamento do Exercício
    // ================================================================
    private void loadExercise() {
        if (exercises == null || exercises.isEmpty()) return;

        current      = exercises.get(exerciseIndex);
        timeLeft     = current.getTimeLimit();
        active       = true;
        exerciseDone = false;

        startTime = System.currentTimeMillis();

        refreshTopBar();
        lblCategory.setText(current.getCategory() + "  -  " + current.getDescription());
        lblInstructions.setText(current.getInstructions());

        int total = exercises.size();
        lblProgress.setText("Exercicio " + (exerciseIndex + 1) + " de " + total);
        progressBar.setValue((int)((double) exerciseIndex / total * 100));

        txtInput.setText("");
        txtInput.setEnabled(true);
        setFeedback(" ", COLOR_SUCCESS);
        lblAutoAdvance.setText(" ");
        btnAdvance.setVisible(false);
        renderTarget("");

        // Atualiza o teclado com as teclas desta frase
        keyboardPanel.highlightForText(current.getTargetText());

        if (autoAdvanceTimer != null && autoAdvanceTimer.isRunning()) autoAdvanceTimer.stop();
        if (countdown         != null && countdown.isRunning())        countdown.stop();

        countdown = new Timer(1000, e -> tickTimer());
        countdown.start();

        SwingUtilities.invokeLater(() -> txtInput.requestFocusInWindow());
    }

    // ================================================================
    //  Cronômetro
    // ================================================================
    private void tickTimer() {
        timeLeft--;
        lblTimer.setText(timeLeft + "s");
        lblTimer.setForeground(timeLeft <= 10 ? COLOR_DANGER
                : timeLeft <= 20 ? COLOR_WARNING
                : Color.WHITE);
        if (timeLeft <= 0) onTimeUp();
    }

    private void onTimeUp() {
        if (!active) return;
        long elapsed = System.currentTimeMillis() - startTime;
        finishExercise("Tempo esgotado! Não desanime!", COLOR_DANGER, 0, 0, 0, (int)(elapsed / 1000));
    }

    // ================================================================
    //  Processamento da Digitação
    // ================================================================
    private void onInput() {
        String typed  = txtInput.getText();
        String target = current.getTargetText();

        if (typed.length() > target.length()) {
            typed = typed.substring(0, target.length());
            txtInput.setText(typed);
        }

        renderTarget(typed);

        if (!typed.isEmpty()) {
            char last     = typed.charAt(typed.length() - 1);
            char expected = target.charAt(typed.length() - 1);
            setFeedback(
                    last == expected ? "Muito bem!  Continue assim!" : "Ops! Procure a tecla certa com calma.",
                    last == expected ? COLOR_SUCCESS : COLOR_DANGER
            );
        } else {
            setFeedback(" ", COLOR_SUCCESS);
        }

        if (typed.length() == target.length()) checkCompletion(typed, target);
    }

    private void checkCompletion(String typed, String target) {
        if (!active) return;
        active = false;
        if (countdown != null) countdown.stop();

        long   elapsed     = System.currentTimeMillis() - startTime;
        int    correct     = countCorrect(typed, target);
        double acc         = (double) correct / target.length();
        int    stars       = current.calculateStars(acc, elapsed);
        int    xp          = current.calculateScore(correct, target.length(), elapsed);
        int    timeSeconds = (int)(elapsed / 1000);
        int    words       = target.trim().split("\\s+").length;
        double minutes     = Math.max(elapsed / 60000.0, 1.0 / 60.0);
        int    wpm         = (int) Math.round(words / minutes);

        String msg; Color col;
        if      (stars == 3) { msg = "Perfeito! Você arrasou!";       col = COLOR_SUCCESS;         }
        else if (stars == 2) { msg = "Muito bem! Ótimo trabalho!";    col = new Color(0, 140, 80); }
        else if (stars == 1) { msg = "Bom início! Vai melhorar!";     col = COLOR_WARNING;         }
        else                 { msg = "Tente novamente, não desista!"; col = COLOR_DANGER;          }

        finishExercise(msg, col, stars, xp, wpm, timeSeconds);
    }

    private void finishExercise(String msg, Color col, int stars, int xp, int wpm, int timeSeconds) {
        active = false;
        if (countdown != null) countdown.stop();

        // NÃO desativa o campo — precisamos que ele receba Enter
        setFeedback(msg, col);

        if (stars == 0) player.loseLife();
        else { player.addXP(xp); player.completeExercise(); }

        refreshTopBar();
        player.saveToFile("ranking.txt");

        exerciseDone  = true;
        pendingStars  = stars;
        pendingXp     = xp;
        pendingWpm    = wpm;
        pendingTime   = timeSeconds;

        startAutoAdvance();
    }

    // ================================================================
    //  Auto-avançar
    // ================================================================
    private void startAutoAdvance() {
        final int[] secs = {5};

        txtInput.setText("");
        txtInput.setEnabled(true);   // campo ativo para receber Enter

        btnAdvance.setVisible(true); // botão visível
        lblAutoAdvance.setText("Próximo em " + secs[0] + "s");
        lblAutoAdvance.setForeground(COLOR_PRIMARY);

        SwingUtilities.invokeLater(() -> txtInput.requestFocusInWindow());

        autoAdvanceTimer = new Timer(1000, null);
        autoAdvanceTimer.addActionListener(e -> {
            secs[0]--;
            if (secs[0] <= 0) {
                autoAdvanceTimer.stop();
                advanceNow();
            } else {
                lblAutoAdvance.setText("Próximo em " + secs[0] + "s");
            }
        });
        autoAdvanceTimer.start();
    }

    private void advanceNow() {
        if (autoAdvanceTimer != null && autoAdvanceTimer.isRunning()) autoAdvanceTimer.stop();
        exerciseDone = false;
        btnAdvance.setVisible(false);
        lblAutoAdvance.setText(" ");
        game.showResult(player, pendingStars, pendingXp,
                current.getDescription(), pendingWpm, pendingTime);
    }

    // ================================================================
    //  Renderização Colorida
    // ================================================================
    private void renderTarget(String typed) {
        StyledDocument doc = txtExercise.getStyledDocument();
        try { doc.remove(0, doc.getLength()); } catch (BadLocationException ex) { /* ignorado */ }

        String target = current.getTargetText();

        for (int i = 0; i < target.length(); i++) {
            SimpleAttributeSet as = new SimpleAttributeSet();
            StyleConstants.setFontFamily(as, "Nunito Sans");
            StyleConstants.setFontSize(as, 36);

            if (i < typed.length()) {
                if (typed.charAt(i) == target.charAt(i)) {
                    StyleConstants.setForeground(as, COL_CORRECT_FG);
                    StyleConstants.setBackground(as, COL_CORRECT_BG);
                } else {
                    StyleConstants.setForeground(as, COL_WRONG_FG);
                    StyleConstants.setBackground(as, COL_WRONG_BG);
                }
            } else if (i == typed.length()) {
                StyleConstants.setForeground(as, COL_NORMAL_FG);
                StyleConstants.setBackground(as, COL_CURSOR_BG);
            } else {
                StyleConstants.setForeground(as, COL_NORMAL_FG);
                StyleConstants.setBackground(as, COLOR_WHITE);
            }

            try {
                doc.insertString(doc.getLength(), String.valueOf(target.charAt(i)), as);
            } catch (BadLocationException ex) { /* ignorado */ }
        }

        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
    }

    // ================================================================
    //  Helpers
    // ================================================================
    private void refreshTopBar() {
        if (player == null) return;
        lblPlayerName.setText(player.getName());
        lblLevel.setText("Nível " + player.getGameLevel() + " — " + player.getLevelName());
        lblScore.setText(player.getTotalScore() + " pts");
        lblLives.setText(heartsString(player.getLives()));
        lblLives.setForeground(player.getLives() <= 1 ? COLOR_DANGER : new Color(255, 110, 110));
        lblTimer.setText(timeLeft + "s");
        lblTimer.setForeground(Color.WHITE);

        lblSideXP.setText(String.valueOf(player.getTotalScore()));
        lblSideLevel.setText(String.valueOf(exerciseIndex + 1));
        lblSideStreak.setText(player.getStreak() + "x");

        int level    = player.getGameLevel();
        int progress = player.getXpProgress();
        xpBar.setValue(progress);

        if (level >= 5) {
            lblNextLevelHint.setText("Nível máximo atingido!");
        } else {
            int xpNeeded = player.getXpForNextLevel() - player.getXp();
            lblNextLevelHint.setText("Nível " + level + " → " + (level + 1) + "  (" + xpNeeded + " XP)");
        }
    }

    private void setFeedback(String msg, Color color) {
        lblFeedback.setText(msg);
        lblFeedback.setForeground(color);
    }

    private int countCorrect(String typed, String target) {
        int n = 0;
        for (int i = 0; i < Math.min(typed.length(), target.length()); i++) {
            if (typed.charAt(i) == target.charAt(i)) n++;
        }
        return n;
    }

    private void goMenu() {
        if (autoAdvanceTimer != null && autoAdvanceTimer.isRunning()) autoAdvanceTimer.stop();
        if (countdown         != null && countdown.isRunning())        countdown.stop();
        active = false;
        exerciseDone = false;
        game.returnToMenu();
    }
}