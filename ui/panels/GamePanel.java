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

    // ===== PAUSE =====
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

    // -------- Barra superior --------
    private JLabel lblPlayerName;
    private JLabel lblLevel;
    private JLabel lblLives;
    private JLabel lblScore;
    private JLabel lblTimer;

    // -------- Barra lateral --------
    private JLabel lblSideScore;
    private JLabel lblSideStreak;
    private JLabel lblSideExercise;
    private JLabel lblCategoryHint;     // mostra a categoria atual + progresso dentro dela

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

    private static final Font FONT_EXERCISE_BIG = new Font("Nunito Sans", Font.PLAIN, 32);
    private static final Font FONT_INPUT_BIG    = new Font("Poppins",     Font.PLAIN, 26);

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
        lblLevel      = label("Categoria",      FONT_SMALL, new Color(200,225,255), SwingConstants.LEFT);
        left.add(lblPlayerName);
        left.add(lblLevel);

        lblLives = label("\u2764 \u2764 \u2764", FONT_HEARTS, new Color(255,110,110), SwingConstants.CENTER);

        JPanel right = new JPanel(new GridLayout(3, 1, 0, 2));
        right.setOpaque(false);
        lblScore = label("0 pts", FONT_INFO,      COLOR_GOLD,  SwingConstants.RIGHT);
        lblTimer = label("--s",   FONT_TIMER_BIG, Color.WHITE, SwingConstants.RIGHT);

        btnPause = createModernButton("Pausar", new Color(255, 255, 255, 50));
        btnPause.setForeground(Color.WHITE);
        btnPause.addActionListener(e -> pausarJogo());

        right.add(lblScore);
        right.add(lblTimer);
        right.add(btnPause);

        bar.add(left,     BorderLayout.WEST);
        bar.add(lblLives, BorderLayout.CENTER);
        bar.add(right,    BorderLayout.EAST);
        return bar;
    }

    // ================================================================
    //  Barra Lateral (sem XP — mostra pontuação, sequência e progresso de categoria)
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
        side.add(sidebarCard("Pontuação",
                lblSideScore    = label("0",  FONT_SUBTITLE, Color.WHITE, SwingConstants.CENTER), COLOR_ACCENT));
        side.add(Box.createVerticalStrut(15));
        side.add(sidebarCard("Sequência",
                lblSideStreak   = label("0x", FONT_SUBTITLE, Color.WHITE, SwingConstants.CENTER), COLOR_ACCENT));
        side.add(Box.createVerticalStrut(15));
        side.add(sidebarCard("Exercício",
                lblSideExercise = label("1",  FONT_SUBTITLE, Color.WHITE, SwingConstants.CENTER), COLOR_ACCENT));
        side.add(Box.createVerticalStrut(20));

        // Card de progresso da categoria atual (substitui o antigo card de XP)
        RoundedPanel catCard = new RoundedPanel(20, COLOR_ACCENT);
        catCard.setLayout(new BoxLayout(catCard, BoxLayout.Y_AXIS));
        catCard.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        catCard.setMaximumSize(new Dimension(210, 60));

        lblCategoryHint = label("Categoria: --", FONT_SMALL, Color.WHITE, SwingConstants.CENTER);
        lblCategoryHint.setAlignmentX(Component.CENTER_ALIGNMENT);
        catCard.add(lblCategoryHint);
        side.add(catCard);

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
        JPanel center = new JPanel(new BorderLayout(0, 6));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(12, 40, 18, 20));

        // --- Topo: categoria + instruções ---
        JPanel infoRow = new JPanel(new GridLayout(2, 1, 0, 3));
        infoRow.setOpaque(false);
        lblCategory     = label("Categoria",  FONT_INFO,  COLOR_PRIMARY,          SwingConstants.CENTER);
        lblInstructions = label("Instruções", FONT_SMALL, new Color(80, 80, 80),   SwingConstants.CENTER);
        infoRow.add(lblCategory);
        infoRow.add(lblInstructions);
        center.add(infoRow, BorderLayout.NORTH);

        // --- Centro: cartão do exercício (REDUZIDO em altura para abrir espaço pro teclado) ---
        RoundedPanel card = new RoundedPanel(40, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(14, 40, 8, 40));

        txtExercise = new JTextPane();
        txtExercise.setEditable(false);
        txtExercise.setFont(FONT_EXERCISE_BIG);
        txtExercise.setBackground(COLOR_WHITE);
        txtExercise.setFocusable(false);
        // Altura reduzida de 130 para 88 — "puxa pra cima" como pedido
        txtExercise.setPreferredSize(new Dimension(0, 88));

        lblFeedback = label(" ", new Font("Nunito Sans", Font.BOLD, 20),
                COLOR_SUCCESS, SwingConstants.CENTER);
        lblFeedback.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));

        JPanel cardSouth = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        cardSouth.setOpaque(false);
        cardSouth.add(lblFeedback);

        card.add(txtExercise, BorderLayout.CENTER);
        card.add(cardSouth,   BorderLayout.SOUTH);
        center.add(card, BorderLayout.CENTER);

        // --- Sul: teclado (AUMENTADO) + campo de digitação ---
        JPanel southSection = new JPanel(new BorderLayout(0, 6));
        southSection.setOpaque(false);

        // Teclado visual — altura aumentada de 115 para 150
        keyboardPanel = new KeyboardPanel();
        keyboardPanel.setPreferredSize(new Dimension(0, 150));
        southSection.add(keyboardPanel, BorderLayout.CENTER);

        // Linha de digitação
        JPanel inputRow = new JPanel(new BorderLayout(10, 0));
        inputRow.setOpaque(false);
        inputRow.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

        JLabel promptLabel = label("Digite aqui:  ", FONT_BODY, COLOR_TEXT, SwingConstants.LEFT);
        promptLabel.setPreferredSize(new Dimension(165, 56));

        txtInput = new JTextField();
        txtInput.setFont(FONT_INPUT_BIG);
        txtInput.setPreferredSize(new Dimension(0, 56));
        txtInput.putClientProperty("JTextField.placeholderText", "Comece a digitar aqui...");
        txtInput.putClientProperty("JComponent.roundRect", true);

        // Ao concluir a frase, vai direto pra tela de resultado (sem espera local aqui).
        // Espaço nunca aciona nada de navegação — só digitação normal.
        txtInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
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

    /** Inicia a campanha completa, do nível 1 ao 5. */
    public void startGame(Player p) {
        startGame(p, 1);
    }

    /** Inicia a partir de um nível específico (1 a 5), seguindo até o final. */
    public void startGame(Player p, int startLevel) {
        this.player = p;
        this.exercises = ExerciseFactory.createCampaignFromLevel(startLevel);
        this.exerciseIndex = 0;
        loadExercise();
    }

    public boolean nextExercise() {
        exerciseIndex++;
        if (exerciseIndex >= exercises.size()) {
            game.showFinalLevelResult(player);
            return false;
        }
        loadExercise();
        return true;
    }

    // ================================================================
    //  Carregamento do Exercício
    // ================================================================
    private void loadExercise() {
        if (exercises == null || exercises.isEmpty()) return;

        current  = exercises.get(exerciseIndex);
        timeLeft = current.getTimeLimit();
        active   = true;

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

        keyboardPanel.highlightForText(current.getTargetText());

        if (countdown != null && countdown.isRunning()) countdown.stop();

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
        int    points      = current.calculateScore(correct, target.length(), elapsed);
        int    timeSeconds = (int)(elapsed / 1000);
        int    words       = target.trim().split("\\s+").length;
        double minutes     = Math.max(elapsed / 60000.0, 1.0 / 60.0);
        int    wpm         = (int) Math.round(words / minutes);

        String msg; Color col;
        if      (stars == 3) { msg = "Perfeito! Você arrasou!";       col = COLOR_SUCCESS;         }
        else if (stars == 2) { msg = "Muito bem! Ótimo trabalho!";    col = new Color(0, 140, 80); }
        else if (stars == 1) { msg = "Bom início! Vai melhorar!";     col = COLOR_WARNING;         }
        else                 { msg = "Tente novamente, não desista!"; col = COLOR_DANGER;          }

        finishExercise(msg, col, stars, points, wpm, timeSeconds);
    }

    private void finishExercise(String msg, Color col, int stars, int points, int wpm, int timeSeconds) {
        active = false;
        if (countdown != null) countdown.stop();

        setFeedback(msg, col);

        if (stars == 0) player.loseLife();
        else { player.addScore(points); player.completeExercise(); }

        refreshTopBar();
        player.saveToFile("ranking.txt");

        // Vai direto para a tela de resultado — sem timer local nem botão "Avançar" aqui.
        // O Enter/botão de avançar agora vivem na própria tela de resultado.
        txtInput.setEnabled(false);
        game.showResult(player, stars, points, current.getDescription(), wpm, timeSeconds);
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
            StyleConstants.setFontSize(as, 32);

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

        // Nível agora é calculado pela posição real do exercício na campanha
        int level = ExerciseFactory.getLevelForIndex(exerciseIndex);
        String levelName = ExerciseFactory.getLevelName(level);
        lblLevel.setText("Nível " + level + " — " + levelName);

        lblScore.setText(player.getTotalScore() + " pts");
        lblLives.setText(heartsString(player.getLives()));
        lblLives.setForeground(player.getLives() <= 1 ? COLOR_DANGER : new Color(255, 110, 110));
        lblTimer.setText(timeLeft + "s");
        lblTimer.setForeground(Color.WHITE);

        lblSideScore.setText(String.valueOf(player.getTotalScore()));
        lblSideExercise.setText(String.valueOf(exerciseIndex + 1));
        lblSideStreak.setText(player.getStreak() + "x");

        // Progresso dentro da categoria atual (ex: "Acentos: 3 de 8")
        int posInCategory = (exerciseIndex % ExerciseFactory.EXERCISES_PER_SESSION) + 1;
        lblCategoryHint.setText(levelName + ": " + posInCategory + "/" + ExerciseFactory.EXERCISES_PER_SESSION);
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