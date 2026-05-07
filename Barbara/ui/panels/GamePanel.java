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

    // -------- Model state --------
    private Player player;
    private List<Exercise> exercises;
    private int exerciseIndex;
    private Exercise current;
    private boolean active;
    private long startTime;
    private int timeLeft;

    // -------- Top bar labels --------
    private JLabel lblPlayerName;
    private JLabel lblLevel;
    private JLabel lblLives;
    private JLabel lblScore;
    private JLabel lblTimer;

    // -------- Exercise area --------
    private JLabel lblCategory;
    private JLabel lblInstructions;
    private JTextPane txtExercise;
    private JTextField txtInput;
    private JLabel lblFeedback;

    // -------- Bottom bar --------
    private JProgressBar progressBar;
    private JLabel lblProgress;

    // -------- Timer --------
    private Timer countdown;

    // -------- Colors for character feedback --------
    private static final Color COL_CORRECT_FG = new Color( 27, 135,  50);
    private static final Color COL_CORRECT_BG = new Color(220, 255, 220);
    private static final Color COL_WRONG_FG   = new Color(180,  20,  20);
    private static final Color COL_WRONG_BG   = new Color(255, 220, 220);
    private static final Color COL_CURSOR_BG  = new Color(180, 220, 255);
    private static final Color COL_NORMAL_FG  = new Color( 60,  60,  60);

    public GamePanel(TypingGame game) {
        super(game);
    }

    @Override
    protected void initialize() {
        setLayout(new BorderLayout(0, 0));
        add(buildTopBar(),    BorderLayout.NORTH);
        add(buildCenter(),    BorderLayout.CENTER);
        add(buildBottomBar(), BorderLayout.SOUTH);
    }

    // ================================================================
    //  Top bar
    // ================================================================
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout(16, 0));
        bar.setBackground(COLOR_HEADER);
        bar.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));

        // Esquerda: nome (linha 1) + nível (linha 2) — empilhados para não cortar
        JPanel left = new JPanel(new GridLayout(2, 1, 0, 1));
        left.setOpaque(false);
        lblPlayerName = label("Jogador", FONT_INFO, Color.WHITE, SwingConstants.LEFT);
        lblLevel      = label("Nível 1 — Iniciante",
                new Font("Arial", Font.PLAIN, 14), new Color(200, 225, 255), SwingConstants.LEFT);
        left.add(lblPlayerName);
        left.add(lblLevel);

        // Centro: vidas
        lblLives = label("♥ ♥ ♥", FONT_HEARTS, new Color(255, 110, 110), SwingConstants.CENTER);

        // Direita: pontos (linha 1) + tempo (linha 2) — sem ícone Unicode problemático
        JPanel right = new JPanel(new GridLayout(2, 1, 0, 1));
        right.setOpaque(false);
        lblScore = label("0 pts", FONT_INFO, COLOR_GOLD, SwingConstants.RIGHT);
        lblTimer = label("--s",   FONT_INFO, Color.WHITE, SwingConstants.RIGHT);
        right.add(lblScore);
        right.add(lblTimer);

        bar.add(left,     BorderLayout.WEST);
        bar.add(lblLives, BorderLayout.CENTER);
        bar.add(right,    BorderLayout.EAST);
        return bar;
    }

    // ================================================================
    //  Center area
    // ================================================================
    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setBackground(COLOR_BG);
        center.setBorder(BorderFactory.createEmptyBorder(16, 36, 10, 36));

        // Category + instructions
        JPanel infoRow = new JPanel(new GridLayout(2, 1, 0, 4));
        infoRow.setOpaque(false);
        lblCategory     = label("Categoria", FONT_INFO, COLOR_PRIMARY, SwingConstants.CENTER);
        lblInstructions = label("Instruções", FONT_SMALL, new Color(80, 80, 80), SwingConstants.CENTER);
        infoRow.add(lblCategory);
        infoRow.add(lblInstructions);
        center.add(infoRow, BorderLayout.NORTH);

        // Exercise display card
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(COLOR_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_CARD_BORDER, 2),
            BorderFactory.createEmptyBorder(24, 28, 16, 28)
        ));

        txtExercise = new JTextPane();
        txtExercise.setEditable(false);
        txtExercise.setFont(FONT_EXERCISE);
        txtExercise.setBackground(COLOR_WHITE);
        txtExercise.setFocusable(false);
        txtExercise.setPreferredSize(new Dimension(0, 130));

        lblFeedback = label(" ", new Font("Arial", Font.BOLD, 20), COLOR_SUCCESS, SwingConstants.CENTER);
        lblFeedback.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        card.add(txtExercise, BorderLayout.CENTER);
        card.add(lblFeedback, BorderLayout.SOUTH);
        center.add(card, BorderLayout.CENTER);

        // Input row
        JPanel inputRow = new JPanel(new BorderLayout(10, 0));
        inputRow.setOpaque(false);
        inputRow.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        JLabel promptLabel = label("Digite aqui:  ", FONT_BODY, COLOR_TEXT, SwingConstants.LEFT);
        promptLabel.setPreferredSize(new Dimension(165, 52));

        txtInput = new JTextField();
        txtInput.setFont(FONT_INPUT);
        txtInput.setPreferredSize(new Dimension(0, 52));
        txtInput.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_PRIMARY, 2),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        txtInput.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) {
                if (active) onInput();
            }
        });

        inputRow.add(promptLabel, BorderLayout.WEST);
        inputRow.add(txtInput,    BorderLayout.CENTER);
        center.add(inputRow, BorderLayout.SOUTH);

        return center;
    }

    // ================================================================
    //  Bottom bar
    // ================================================================
    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new BorderLayout(10, 0));
        bar.setBackground(COLOR_FOOTER_BG);
        bar.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));

        lblProgress = label("Exercicio 1 de 47", FONT_SMALL, COLOR_TEXT, SwingConstants.LEFT);
        lblProgress.setPreferredSize(new Dimension(200, 24));

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(false);
        progressBar.setForeground(COLOR_PRIMARY);
        progressBar.setBackground(new Color(195, 195, 175));
        progressBar.setBorderPainted(false);
        progressBar.setPreferredSize(new Dimension(0, 18));

        JButton menuBtn = createButton("< Menu", COLOR_BTN_BACK);
        menuBtn.setPreferredSize(new Dimension(110, 36));
        menuBtn.setFont(new Font("Arial", Font.BOLD, 15));
        menuBtn.addActionListener(e -> goMenu());

        bar.add(lblProgress,  BorderLayout.WEST);
        bar.add(progressBar,  BorderLayout.CENTER);
        bar.add(menuBtn,      BorderLayout.EAST);
        return bar;
    }

    // ================================================================
    //  Public API called by TypingGame
    // ================================================================
    public void startGame(Player p) {
        this.player = p;
        this.exercises = ExerciseFactory.createAllExercises();
        this.exerciseIndex = 0;
        loadExercise();
    }

    public void nextExercise() {
        exerciseIndex++;
        if (exerciseIndex >= exercises.size()) exerciseIndex = 0;
        loadExercise();
    }

    // ================================================================
    //  Exercise loading
    // ================================================================
    private void loadExercise() {
        if (exercises == null || exercises.isEmpty()) return;

        current   = exercises.get(exerciseIndex);
        timeLeft  = current.getTimeLimit();
        active    = true;
        startTime = System.currentTimeMillis();

        refreshTopBar();
        lblCategory.setText(current.getCategory()    + "  -  " + current.getDescription());
        lblInstructions.setText(current.getInstructions());

        int total = exercises.size();
        lblProgress.setText("Exercicio " + (exerciseIndex + 1) + " de " + total);
        progressBar.setValue((int)((double) exerciseIndex / total * 100));

        txtInput.setText("");
        txtInput.setEnabled(true);
        setFeedback(" ", COLOR_SUCCESS);
        renderTarget("");

        if (countdown != null && countdown.isRunning()) countdown.stop();
        countdown = new Timer(1000, e -> tickTimer());
        countdown.start();

        SwingUtilities.invokeLater(() -> txtInput.requestFocusInWindow());
    }

    // ================================================================
    //  Timer
    // ================================================================
    private void tickTimer() {
        timeLeft--;
        Color timerColor = timeLeft <= 10 ? COLOR_DANGER
                         : timeLeft <= 20 ? COLOR_WARNING
                         : Color.WHITE;
        lblTimer.setText(timeLeft + "s");
        lblTimer.setForeground(timerColor);
        if (timeLeft <= 0) onTimeUp();
    }

    private void onTimeUp() {
        if (!active) return;
        long elapsed = System.currentTimeMillis() - startTime;
        finishExercise("Tempo esgotado! Não desanime!", COLOR_DANGER, 0, 0, 0, (int)(elapsed / 1000));
    }

    // ================================================================
    //  Input handling
    // ================================================================
    private void onInput() {
        String typed  = txtInput.getText();
        String target = current.getTargetText();

        // Prevent typing beyond target length
        if (typed.length() > target.length()) {
            typed = typed.substring(0, target.length());
            txtInput.setText(typed);
        }

        renderTarget(typed);

        if (!typed.isEmpty()) {
            char last     = typed.charAt(typed.length() - 1);
            char expected = target.charAt(typed.length() - 1);
            if (last == expected) {
                setFeedback("Muito bem!  Continue assim!", COLOR_SUCCESS);
            } else {
                setFeedback("Ops! Procure a tecla certa com calma.", COLOR_DANGER);
            }
        } else {
            setFeedback(" ", COLOR_SUCCESS);
        }

        if (typed.length() == target.length()) {
            checkCompletion(typed, target);
        }
    }

    private void checkCompletion(String typed, String target) {
        if (!active) return;
        active = false;
        if (countdown != null) countdown.stop();

        long elapsed    = System.currentTimeMillis() - startTime;
        int  correct    = countCorrect(typed, target);
        double acc      = (double) correct / target.length();
        int  stars      = current.calculateStars(acc, elapsed);
        int  xp         = current.calculateScore(correct, target.length(), elapsed);
        int  timeSeconds = (int)(elapsed / 1000);
        int  words      = target.trim().split("\\s+").length;
        double minutes  = Math.max(elapsed / 60000.0, 1.0 / 60.0);
        int  wpm        = (int) Math.round(words / minutes);

        String msg; Color col;
        if (stars == 3) { msg = "Perfeito! Você arrasou!";         col = COLOR_SUCCESS;         }
        else if (stars == 2) { msg = "Muito bem! Ótimo trabalho!"; col = new Color(0, 140, 80); }
        else if (stars == 1) { msg = "Bom início! Vai melhorar!";  col = COLOR_WARNING;         }
        else                 { msg = "Tente novamente, não desista!"; col = COLOR_DANGER;        }

        finishExercise(msg, col, stars, xp, wpm, timeSeconds);
    }

    private void finishExercise(String msg, Color col, int stars, int xp, int wpm, int timeSeconds) {
        active = false;
        if (countdown != null) countdown.stop();
        txtInput.setEnabled(false);
        setFeedback(msg, col);

        if (stars == 0) {
            player.loseLife();
        } else {
            player.addXP(xp);
            player.completeExercise();
        }

        refreshTopBar();

        int fs = stars, fx = xp, fw = wpm, ft = timeSeconds;
        Timer delay = new Timer(900, e ->
            game.showResult(player, fs, fx, current.getDescription(), fw, ft)
        );
        delay.setRepeats(false);
        delay.start();
    }

    // ================================================================
    //  Styled text rendering (character-by-character coloring)
    // ================================================================
    private void renderTarget(String typed) {
        StyledDocument doc = txtExercise.getStyledDocument();
        try {
            doc.remove(0, doc.getLength());
        } catch (BadLocationException ex) { /* ignored */ }

        String target = current.getTargetText();

        for (int i = 0; i < target.length(); i++) {
            SimpleAttributeSet as = new SimpleAttributeSet();
            StyleConstants.setFontFamily(as, "Courier New");
            StyleConstants.setFontSize(as, 28);

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
            } catch (BadLocationException ex) { /* ignored */ }
        }

        // Center all text
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
        if (countdown != null && countdown.isRunning()) countdown.stop();
        active = false;
        game.returnToMenu();
    }
}
