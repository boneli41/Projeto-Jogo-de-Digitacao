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

    // -------- Colors --------

    private static final Color COL_CORRECT_FG =
            new Color(27, 135, 50);

    private static final Color COL_CORRECT_BG =
            new Color(220, 255, 220);

    private static final Color COL_WRONG_FG =
            new Color(180, 20, 20);

    private static final Color COL_WRONG_BG =
            new Color(255, 220, 220);

    private static final Color COL_CURSOR_BG =
            new Color(180, 220, 255);

    private static final Color COL_NORMAL_FG =
            new Color(60, 60, 60);

    public GamePanel(TypingGame game) {
        super(game);
    }

    @Override
    protected void initialize() {

        setLayout(new BorderLayout(0, 0));

        add(buildTopBar(),
                BorderLayout.NORTH);

        add(buildCenter(),
                BorderLayout.CENTER);

        add(buildBottomBar(),
                BorderLayout.SOUTH);
    }

    // ================================================================
    // Top Bar
    // ================================================================

    private JPanel buildTopBar() {

        JPanel bar =
                new JPanel(new BorderLayout(16, 0));

        bar.setBackground(COLOR_HEADER);

        bar.setBorder(
                BorderFactory.createEmptyBorder(
                        8, 18, 8, 18
                )
        );

        JPanel left =
                new JPanel(new GridLayout(2, 1, 0, 1));

        left.setOpaque(false);

        lblPlayerName =
                label(
                        "Jogador",
                        FONT_INFO,
                        Color.WHITE,
                        SwingConstants.LEFT
                );

        lblLevel =
                label(
                        "Nível 1",
                        new Font("Arial", Font.PLAIN, 14),
                        new Color(200, 225, 255),
                        SwingConstants.LEFT
                );

        left.add(lblPlayerName);
        left.add(lblLevel);

        lblLives =
                label(
                        "♥ ♥ ♥",
                        FONT_HEARTS,
                        new Color(255, 110, 110),
                        SwingConstants.CENTER
                );

        JPanel right =
                new JPanel(new GridLayout(2, 1, 0, 1));

        right.setOpaque(false);

        lblScore =
                label(
                        "0 pts",
                        FONT_INFO,
                        COLOR_GOLD,
                        SwingConstants.RIGHT
                );

        lblTimer =
                label(
                        "--s",
                        FONT_INFO,
                        Color.WHITE,
                        SwingConstants.RIGHT
                );

        right.add(lblScore);
        right.add(lblTimer);

        bar.add(left, BorderLayout.WEST);
        bar.add(lblLives, BorderLayout.CENTER);
        bar.add(right, BorderLayout.EAST);

        return bar;
    }

    // ================================================================
    // Center
    // ================================================================

    private JPanel buildCenter() {

        JPanel center =
                new JPanel(new BorderLayout(0, 10));

        center.setBackground(COLOR_BG);

        center.setBorder(
                BorderFactory.createEmptyBorder(
                        16, 36, 10, 36
                )
        );

        JPanel infoRow =
                new JPanel(new GridLayout(2, 1, 0, 4));

        infoRow.setOpaque(false);

        lblCategory =
                label(
                        "Categoria",
                        FONT_INFO,
                        COLOR_PRIMARY,
                        SwingConstants.CENTER
                );

        lblInstructions =
                label(
                        "Instruções",
                        FONT_SMALL,
                        new Color(80, 80, 80),
                        SwingConstants.CENTER
                );

        infoRow.add(lblCategory);
        infoRow.add(lblInstructions);

        center.add(infoRow, BorderLayout.NORTH);

        JPanel card =
                new JPanel(new BorderLayout());

        card.setBackground(COLOR_WHITE);

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                COLOR_CARD_BORDER,
                                2
                        ),
                        BorderFactory.createEmptyBorder(
                                24, 28, 16, 28
                        )
                )
        );

        txtExercise = new JTextPane();

        txtExercise.setEditable(false);

        txtExercise.setFont(FONT_EXERCISE);

        txtExercise.setBackground(COLOR_WHITE);

        txtExercise.setFocusable(false);

        txtExercise.setPreferredSize(
                new Dimension(0, 130)
        );

        lblFeedback =
                label(
                        " ",
                        new Font("Arial", Font.BOLD, 20),
                        COLOR_SUCCESS,
                        SwingConstants.CENTER
                );

        lblFeedback.setBorder(
                BorderFactory.createEmptyBorder(
                        8, 0, 0, 0
                )
        );

        card.add(txtExercise, BorderLayout.CENTER);
        card.add(lblFeedback, BorderLayout.SOUTH);

        center.add(card, BorderLayout.CENTER);

        JPanel inputRow =
                new JPanel(new BorderLayout(10, 0));

        inputRow.setOpaque(false);

        inputRow.setBorder(
                BorderFactory.createEmptyBorder(
                        12, 0, 0, 0
                )
        );

        JLabel promptLabel =
                label(
                        "Digite aqui:  ",
                        FONT_BODY,
                        COLOR_TEXT,
                        SwingConstants.LEFT
                );

        promptLabel.setPreferredSize(
                new Dimension(165, 52)
        );

        txtInput = new JTextField();

        txtInput.setFont(FONT_INPUT);

        txtInput.setPreferredSize(
                new Dimension(0, 52)
        );

        txtInput.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                COLOR_PRIMARY,
                                2
                        ),
                        BorderFactory.createEmptyBorder(
                                6, 12, 6, 12
                        )
                )
        );

        txtInput.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {

                if (active) {
                    onInput();
                }
            }
        });

        inputRow.add(promptLabel, BorderLayout.WEST);
        inputRow.add(txtInput, BorderLayout.CENTER);

        center.add(inputRow, BorderLayout.SOUTH);

        return center;
    }

    // ================================================================
    // Bottom Bar
    // ================================================================

    private JPanel buildBottomBar() {

        JPanel bar =
                new JPanel(new BorderLayout(10, 0));

        bar.setBackground(COLOR_FOOTER_BG);

        bar.setBorder(
                BorderFactory.createEmptyBorder(
                        8, 18, 8, 18
                )
        );

        lblProgress =
                label(
                        "Exercicio 1",
                        FONT_SMALL,
                        COLOR_TEXT,
                        SwingConstants.LEFT
                );

        lblProgress.setPreferredSize(
                new Dimension(200, 24)
        );

        progressBar =
                new JProgressBar(0, 100);

        progressBar.setStringPainted(false);

        progressBar.setForeground(COLOR_PRIMARY);

        progressBar.setBackground(
                new Color(195, 195, 175)
        );

        progressBar.setBorderPainted(false);

        JButton menuBtn =
                createButton("< Menu", COLOR_BTN_BACK);

        menuBtn.setPreferredSize(
                new Dimension(110, 36)
        );

        menuBtn.addActionListener(e -> goMenu());

        bar.add(lblProgress, BorderLayout.WEST);
        bar.add(progressBar, BorderLayout.CENTER);
        bar.add(menuBtn, BorderLayout.EAST);

        return bar;
    }

    // ================================================================
    // PUBLIC API
    // ================================================================

    public void startGame(Player p) {

        this.player = p;

        this.exercises =
                ExerciseFactory.createExercisesForLevel(
                        player.getGameLevel()
                );

        this.exerciseIndex = 0;

        loadExercise();
    }

    public void nextExercise() {

        exerciseIndex++;

        if (exerciseIndex >= exercises.size()) {

            int currentLevel = player.getGameLevel();

            if (currentLevel < player.getMaxLevel()) {

                player.setGameLevel(currentLevel + 1);

                exercises =
                        ExerciseFactory.createExercisesForLevel(
                                player.getGameLevel()
                        );

                exerciseIndex = 0;

            } else {

                // Finished EASY -> ask for MEDIUM

                if (player.getMaxLevel() == 4) {

                    int option = JOptionPane.showConfirmDialog(
                            this,
                            "Você terminou o Fácil!\nDeseja continuar para o Médio?",
                            "Próxima dificuldade",
                            JOptionPane.YES_NO_OPTION
                    );

                    if (option == JOptionPane.YES_OPTION) {

                        player.setGameLevel(5);
                        player.setMaxLevel(6);
                        player.setDifficultyName("Médio");

                        exercises =
                                ExerciseFactory.createExercisesForLevel(
                                        5
                                );

                        exerciseIndex = 0;

                        loadExercise();

                        return;
                    }
                }

                // Finished MEDIUM -> ask for HARD

                else if (player.getMaxLevel() == 6) {

                    int option = JOptionPane.showConfirmDialog(
                            this,
                            "Você terminou o Médio!\nDeseja continuar para o Difícil?",
                            "Próxima dificuldade",
                            JOptionPane.YES_NO_OPTION
                    );

                    if (option == JOptionPane.YES_OPTION) {

                        player.setGameLevel(7);
                        player.setMaxLevel(8);
                        player.setDifficultyName("Difícil");

                        exercises =
                                ExerciseFactory.createExercisesForLevel(
                                        7
                                );

                        exerciseIndex = 0;

                        loadExercise();

                        return;
                    }
                }

                // Final ending after HARD

                utils.ResultSaver.saveFinalResult(
                        player,
                        player.getDifficultyName()
                );

                JOptionPane.showMessageDialog(
                        this,
                        "Parabéns!\nResultado salvo em resultado_final.txt"
                );

                game.returnToMenu();

                return;
            }
        }

        loadExercise();
    }

    // ================================================================
    // Exercise loading
    // ================================================================

    private void loadExercise() {

        if (exercises == null
                || exercises.isEmpty()) {

            return;
        }

        current = exercises.get(exerciseIndex);

        timeLeft = current.getTimeLimit();

        active = true;

        startTime = System.currentTimeMillis();

        refreshTopBar();

        lblCategory.setText(
                current.getCategory()
                        + "  -  "
                        + current.getDescription()
        );

        lblInstructions.setText(
                current.getInstructions()
        );

        int total = exercises.size();

        lblProgress.setText(
                "Exercicio "
                        + (exerciseIndex + 1)
                        + " de "
                        + total
        );

        progressBar.setValue(
                (int)((double) exerciseIndex
                        / total * 100)
        );

        txtInput.setText("");

        txtInput.setEnabled(true);

        setFeedback(" ", COLOR_SUCCESS);

        renderTarget("");

        if (countdown != null
                && countdown.isRunning()) {

            countdown.stop();
        }

        countdown =
                new Timer(1000, e -> tickTimer());

        countdown.start();

        SwingUtilities.invokeLater(
                () -> txtInput.requestFocusInWindow()
        );
    }

    // ================================================================
    // Timer
    // ================================================================

    private void tickTimer() {

        timeLeft--;

        lblTimer.setText(timeLeft + "s");

        if (timeLeft <= 0) {
            onTimeUp();
        }
    }

    private void onTimeUp() {

        if (!active) return;

        long elapsed =
                System.currentTimeMillis()
                        - startTime;

        finishExercise(
                "Tempo esgotado!",
                COLOR_DANGER,
                0,
                0,
                0,
                (int)(elapsed / 1000)
        );
    }

    // ================================================================
    // Input
    // ================================================================

    private void onInput() {

        String typed =
                txtInput.getText();

        String target =
                current.getTargetText();

        if (typed.length()
                > target.length()) {

            typed =
                    typed.substring(
                            0,
                            target.length()
                    );

            txtInput.setText(typed);
        }

        renderTarget(typed);

        if (typed.length() == target.length()) {

            checkCompletion(typed, target);
        }
    }

    private void checkCompletion(
            String typed,
            String target
    ) {

        if (!active) return;

        active = false;

        if (countdown != null) {
            countdown.stop();
        }

        long elapsed =
                System.currentTimeMillis()
                        - startTime;

        int correct =
                countCorrect(typed, target);

        double acc =
                (double) correct
                        / target.length();

        int stars =
                current.calculateStars(
                        acc,
                        elapsed
                );

        int xp =
                current.calculateScore(
                        correct,
                        target.length(),
                        elapsed
                );

        int timeSeconds =
                (int)(elapsed / 1000);

        int words =
                target.trim()
                        .split("\\s+").length;

        double minutes =
                Math.max(
                        elapsed / 60000.0,
                        1.0 / 60.0
                );

        int wpm =
                (int)Math.round(words / minutes);

        finishExercise(
                "Exercício concluído!",
                COLOR_SUCCESS,
                stars,
                xp,
                wpm,
                timeSeconds
        );
    }

    private void finishExercise(
            String msg,
            Color col,
            int stars,
            int xp,
            int wpm,
            int timeSeconds
    ) {

        active = false;

        if (countdown != null) {
            countdown.stop();
        }

        txtInput.setEnabled(false);

        setFeedback(msg, col);

        if (stars == 0) {

            player.loseLife();

        } else {

            player.addXP(xp);

            player.completeExercise();

        }

        refreshTopBar();

        Timer delay =
                new Timer(
                        900,
                        e -> game.showResult(
                                player,
                                stars,
                                xp,
                                current.getDescription(),
                                wpm,
                                timeSeconds
                        )
                );

        delay.setRepeats(false);

        delay.start();
    }

    // ================================================================
    // Render target
    // ================================================================

    private void renderTarget(String typed) {

        StyledDocument doc =
                txtExercise.getStyledDocument();

        try {

            doc.remove(0, doc.getLength());

        } catch (BadLocationException ex) {}

        String target =
                current.getTargetText();

        for (int i = 0; i < target.length(); i++) {

            SimpleAttributeSet as =
                    new SimpleAttributeSet();

            StyleConstants.setFontFamily(
                    as,
                    "Courier New"
            );

            StyleConstants.setFontSize(as, 28);

            if (i < typed.length()) {

                if (typed.charAt(i)
                        == target.charAt(i)) {

                    StyleConstants.setForeground(
                            as,
                            COL_CORRECT_FG
                    );

                    StyleConstants.setBackground(
                            as,
                            COL_CORRECT_BG
                    );

                } else {

                    StyleConstants.setForeground(
                            as,
                            COL_WRONG_FG
                    );

                    StyleConstants.setBackground(
                            as,
                            COL_WRONG_BG
                    );
                }

            } else {

                StyleConstants.setForeground(
                        as,
                        COL_NORMAL_FG
                );
            }

            try {

                doc.insertString(
                        doc.getLength(),
                        String.valueOf(
                                target.charAt(i)
                        ),
                        as
                );

            } catch (BadLocationException ex) {}
        }
    }

    // ================================================================
    // Helpers
    // ================================================================

    private void refreshTopBar() {

        if (player == null) return;

        lblPlayerName.setText(
                player.getName()
        );

        lblLevel.setText(
                "Nível "
                        + player.getGameLevel()
                        + " — "
                        + player.getLevelName()
        );

        lblScore.setText(
                player.getTotalScore()
                        + " pts"
        );

        lblLives.setText(
                heartsString(player.getLives())
        );

        lblTimer.setText(timeLeft + "s");
    }

    private void setFeedback(
            String msg,
            Color color
    ) {

        lblFeedback.setText(msg);

        lblFeedback.setForeground(color);
    }

    private int countCorrect(
            String typed,
            String target
    ) {

        int n = 0;

        for (int i = 0;
             i < Math.min(
                     typed.length(),
                     target.length()
             );
             i++) {

            if (typed.charAt(i)
                    == target.charAt(i)) {

                n++;
            }
        }

        return n;
    }

    private void goMenu() {

        if (countdown != null
                && countdown.isRunning()) {

            countdown.stop();
        }

        active = false;

        game.returnToMenu();
    }
}