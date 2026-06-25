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

    private JButton btnPause;
    private boolean paused = false;
    private boolean isProcessingPause = false;

    private void pausarJogo() {
        if (isProcessingPause) return;
        isProcessingPause = true;
        paused = !paused;
        btnPause.setText(paused ? "Continuar" : "Pausar");
        if (paused) {
            if (countdown != null) countdown.stop();
            txtInput.setEnabled(false);
            setFeedback("JOGO PAUSADO", Color.ORANGE);
        } else {
            if (countdown != null) countdown.start();
            txtInput.setEnabled(true);
            txtInput.requestFocus();
            setFeedback("", COLOR_SUCCESS);
        }
        isProcessingPause = false;
    }

    // -------- Estado do Modelo --------
    private Player player;
    private List<Exercise> exercises;
    private int exerciseIndex;
    private Exercise current;
    private boolean active;
    private long startTime;
    private int timeLeft;

    // -------- Nível de campanha atual (1–5) --------
    private int campaignLevel;

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
    private KeyboardPanel keyboardPanel;

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

    private static final java.util.Random RANDOM = new java.util.Random();

    private static final String[] MSGS_CORRECT = {
            "Muito bem!  Continue assim!",
            "Isso aí!  Você está indo bem!",
            "Perfeito!  Próxima letra!",
            "Ótimo!  Continue no seu ritmo!",
            "Show!  Você acertou!"
    };

    private static final String[] MSGS_WRONG = {
            "Ops! Procure a tecla certa com calma.",
            "Quase lá! Olhe o teclado e tente de novo.",
            "Sem pressa, procure a tecla certinha.",
            "Não desanime, respire e tente outra vez."
    };

    private static final String[] MSGS_3_STARS = {
            "Perfeito! Você arrasou!",
            "Excelente! Você é demais!",
            "Incrível! Continue assim!",
            "Sensacional! Está cada vez melhor!"
    };

    private static final String[] MSGS_2_STARS = {
            "Muito bem! Ótimo trabalho!",
            "Muito bom! Você está evoluindo!",
            "Parabéns! Boa digitação!",
            "Ótimo! Continue praticando!"
    };

    private static final String[] MSGS_1_STAR = {
            "Bom início! Vai melhorar!",
            "Você conseguiu! Continue tentando!",
            "Foi um começo! Vamos praticar mais!",
            "Está no caminho certo!"
    };

    private static final String[] MSGS_0_STARS = {
            "Tente novamente, não desista!",
            "Calma, vamos tentar de novo!",
            "Não desanime, a prática leva à perfeição!",
            "Respire e tente outra vez, você consegue!"
    };

    private static String pickRandom(String[] bank) {
        return bank[RANDOM.nextInt(bank.length)];
    }

    public GamePanel(TypingGame game) { super(game); }

    @Override
    protected void initialize() {
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel content = backgroundPanel("ui/assets/background-image-gamepanel.png");
        add(content, BorderLayout.CENTER);

        content.add(buildTopBar(),          BorderLayout.NORTH);
        content.add(buildSidebar(),         BorderLayout.EAST);
        content.add(buildCenter(),          BorderLayout.CENTER);
        content.add(createFooterProgress(), BorderLayout.SOUTH);
    }

    private JPanel buildTopBar() {
        GradientPanel bar = new GradientPanel(COLOR_PRIMARY, COLOR_PRIMARY, true);
        bar.setPreferredSize(new Dimension(0, 112));
        bar.setLayout(new BorderLayout(25, 0));
        bar.setBorder(BorderFactory.createEmptyBorder(12, 40, 12, 40));

        JPanel left = new JPanel(new GridLayout(2, 1, 0, 1));
        left.setOpaque(false);
        lblPlayerName = label("Jogador",          FONT_INFO,  Color.WHITE,             SwingConstants.LEFT);
        lblLevel      = label("Nível 1 — Iniciante", FONT_SMALL, new Color(200,225,255), SwingConstants.LEFT);
        left.add(lblPlayerName);
        left.add(lblLevel);

        lblLives = label("\u2764 \u2764 \u2764", FONT_HEARTS, new Color(255,110,110), SwingConstants.CENTER);

        JPanel right = new JPanel(new GridBagLayout());
        right.setOpaque(false);
        right.setPreferredSize(new Dimension(370, 0));
        lblScore = label("0 pts", FONT_INFO,      COLOR_GOLD,  SwingConstants.RIGHT);
        lblTimer = label("--s",   FONT_TIMER_BIG, Color.WHITE, SwingConstants.RIGHT);
        lblTimer.setPreferredSize(new Dimension(120, 44));
        lblScore.setPreferredSize(new Dimension(110, 34));

        btnPause = createModernButton("Pausar", new Color(255, 255, 255, 50));
        btnPause.setForeground(Color.WHITE);
        btnPause.setPreferredSize(new Dimension(110, 34));
        btnPause.addActionListener(e -> pausarJogo());

        GridBagConstraints rc = new GridBagConstraints();
        rc.gridy = 0;
        rc.fill = GridBagConstraints.NONE;
        rc.anchor = GridBagConstraints.EAST;
        rc.weightx = 0;
        rc.gridx = 0; rc.insets = new Insets(0, 0, 0, 14);
        right.add(lblScore, rc);
        rc.gridx = 1; rc.insets = new Insets(0, 0, 0, 14);
        right.add(lblTimer, rc);
        rc.gridx = 2; rc.insets = new Insets(0, 0, 0, 0);
        right.add(btnPause, rc);

        bar.add(left,     BorderLayout.WEST);
        bar.add(lblLives, BorderLayout.CENTER);
        bar.add(right,    BorderLayout.EAST);
        return bar;
    }

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

        RoundedPanel xpCard = new RoundedPanel(20, COLOR_ACCENT);
        xpCard.setLayout(new BoxLayout(xpCard, BoxLayout.Y_AXIS));
        xpCard.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        xpCard.setMaximumSize(new Dimension(210, 80));

        lblNextLevelHint = label("Nível 1 > 2", FONT_SMALL, Color.WHITE, SwingConstants.CENTER);
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

    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(0, 8));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(14, 40, 20, 20));

        JPanel infoRow = new JPanel(new GridLayout(2, 1, 0, 4));
        infoRow.setOpaque(false);
        lblCategory     = label("Categoria",  FONT_INFO,  COLOR_PRIMARY,        SwingConstants.CENTER);
        lblInstructions = label("Instruções", FONT_SMALL, new Color(80, 80, 80), SwingConstants.CENTER);
        infoRow.add(lblCategory);
        infoRow.add(lblInstructions);
        center.add(infoRow, BorderLayout.NORTH);

        RoundedPanel card = new RoundedPanel(40, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(24, 40, 16, 40));

        txtExercise = new JTextPane();
        txtExercise.setEditable(false);
        txtExercise.setFont(FONT_EXERCISE_BIG);
        txtExercise.setBackground(COLOR_WHITE);
        txtExercise.setFocusable(false);
        txtExercise.setPreferredSize(new Dimension(0, 90));

        lblFeedback = label(" ", new Font("Nunito Sans", Font.BOLD, 22),
                COLOR_SUCCESS, SwingConstants.CENTER);
        lblFeedback.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

        JPanel cardSouth = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        cardSouth.setOpaque(false);
        cardSouth.add(lblFeedback);

        card.add(txtExercise, BorderLayout.CENTER);
        card.add(cardSouth,   BorderLayout.SOUTH);
        center.add(card, BorderLayout.CENTER);

        JPanel southSection = new JPanel(new BorderLayout(0, 6));
        southSection.setOpaque(false);

        keyboardPanel = new KeyboardPanel();
        keyboardPanel.setPreferredSize(new Dimension(0, 165));
        southSection.add(keyboardPanel, BorderLayout.CENTER);

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

        txtInput.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) {
                if (paused) return;
                if (active) onInput();
            }
        });

        inputRow.add(promptLabel, BorderLayout.WEST);
        inputRow.add(txtInput,    BorderLayout.CENTER);
        southSection.add(inputRow, BorderLayout.SOUTH);
        center.add(southSection, BorderLayout.SOUTH);

        return center;
    }

    private JPanel createFooterProgress() {
        GradientPanel bar = new GradientPanel(COLOR_ACCENT, COLOR_ACCENT, false);
        bar.setPreferredSize(new Dimension(0, 85));
        bar.setLayout(new BorderLayout(15, 0));
        bar.setBorder(BorderFactory.createEmptyBorder(10, 40, 25, 40));

        lblProgress = label("Exercicio 1 de 8", FONT_SMALL, Color.WHITE, SwingConstants.LEFT);
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

    public void startGame(Player p, int campaignLvl) {
        this.player        = p;
        this.campaignLevel = campaignLvl;
        // Encadeia todos os módulos a partir do escolhido até o 5
        this.exercises = new java.util.ArrayList<>();
        for (int lvl = campaignLvl; lvl <= 5; lvl++) {
            this.exercises.addAll(ExerciseFactory.createExercisesForLevel(lvl));
        }
        this.exerciseIndex = 0;
        loadExercise();
    }
    public void startGame(Player p) { startGame(p, 1); }

    public boolean nextExercise() {
        exerciseIndex++;
        if (exerciseIndex >= exercises.size()) {
            game.showFinalLevelResult(player);
            return false;
        }
        loadExercise();
        return true;
    }

    private void loadExercise() {
        if (exercises == null || exercises.isEmpty()) return;

        current      = exercises.get(exerciseIndex);
        timeLeft     = current.getTimeLimit();
        active       = true;

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
        renderTarget("");

        if (!current.getTargetText().isEmpty()) {
            char firstCh = current.getTargetText().charAt(0);
            keyboardPanel.highlightCurrentKey(firstCh, baseVowel(firstCh), false);
        }

        if (countdown != null && countdown.isRunning()) countdown.stop();

        countdown = new Timer(1000, e -> tickTimer());
        countdown.start();

        SwingUtilities.invokeLater(() -> txtInput.requestFocusInWindow());
    }

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
            boolean correct = (last == expected);

            setFeedback(
                    correct ? pickRandom(MSGS_CORRECT) : pickRandom(MSGS_WRONG),
                    correct ? COLOR_SUCCESS : COLOR_DANGER
            );

            if (!correct) {
                keyboardPanel.highlightCurrentKey('\0', '\0', true);
            } else if (typed.length() < target.length()) {
                char nextCh = target.charAt(typed.length());
                keyboardPanel.highlightCurrentKey(nextCh, baseVowel(nextCh), false);
            }
        } else {
            setFeedback(" ", COLOR_SUCCESS);
            if (!target.isEmpty()) {
                char firstCh = target.charAt(0);
                keyboardPanel.highlightCurrentKey(firstCh, baseVowel(firstCh), false);
            }
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
        if      (stars == 3) { msg = pickRandom(MSGS_3_STARS); col = COLOR_SUCCESS;         }
        else if (stars == 2) { msg = pickRandom(MSGS_2_STARS); col = new Color(0, 140, 80); }
        else if (stars == 1) { msg = pickRandom(MSGS_1_STAR);  col = COLOR_WARNING;         }
        else                 { msg = pickRandom(MSGS_0_STARS); col = COLOR_DANGER;          }

        finishExercise(msg, col, stars, xp, wpm, timeSeconds);
    }

    private void finishExercise(String msg, Color col, int stars, int xp, int wpm, int timeSeconds) {
        active = false;
        if (countdown != null) countdown.stop();

        setFeedback(msg, col);

        if (stars == 0) player.loseLife();
        else { player.addXP(xp); player.completeExercise(); }

        refreshTopBar();
        player.saveToFile("ranking.txt");
        txtInput.setEnabled(false);
        game.showResult(player, stars, xp, current.getDescription(), wpm, timeSeconds);
    }

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

        int progress = player.getXpProgress();
        xpBar.setValue(progress);

        int xpLevel = player.getGameLevel();
        if (xpLevel >= 5) {
            lblNextLevelHint.setText("Nível máximo atingido!");
        } else {
            int xpNeeded = player.getXpForNextLevel() - player.getXp();
            lblNextLevelHint.setText("Nível " + xpLevel + " > " + (xpLevel + 1) + "  (" + xpNeeded + " XP)");
        }
    }

    private void setFeedback(String msg, Color color) {
        lblFeedback.setText(msg);
        lblFeedback.setForeground(color);
    }

    private char baseVowel(char c) {
        switch (c) {
            case '\u00e1': case '\u00c1': return 'a'; // á Á
            case '\u00e9': case '\u00c9': return 'e'; // é É
            case '\u00ed': case '\u00cd': return 'i'; // í Í
            case '\u00f3': case '\u00d3': return 'o'; // ó Ó
            case '\u00fa': case '\u00da': return 'u'; // ú Ú
            case '\u00e0': case '\u00c0': return 'a'; // à À
            case '\u00e8': case '\u00c8': return 'e'; // è È
            case '\u00ec': case '\u00cc': return 'i'; // ì Ì
            case '\u00f2': case '\u00d2': return 'o'; // ò Ò
            case '\u00f9': case '\u00d9': return 'u'; // ù Ù
            case '\u00e2': case '\u00c2': return 'a'; // â Â
            case '\u00ea': case '\u00ca': return 'e'; // ê Ê
            case '\u00ee': case '\u00ce': return 'i'; // î Î
            case '\u00f4': case '\u00d4': return 'o'; // ô Ô
            case '\u00fb': case '\u00db': return 'u'; // û Û
            case '\u00e3': case '\u00c3': return 'a'; // ã Ã
            case '\u00f5': case '\u00d5': return 'o'; // õ Õ
            case '\u00f1': case '\u00d1': return 'n'; // ñ Ñ
            default: return '\0';
        }
    }

    private int countCorrect(String typed, String target) {
        int n = 0;
        for (int i = 0; i < Math.min(typed.length(), target.length()); i++) {
            if (typed.charAt(i) == target.charAt(i)) n++;
        }
        return n;
    }

    private void goMenu() {
        if (countdown != null && countdown.isRunning()) countdown.stop();
        active = false;
        game.returnToMenu();
    }
}
