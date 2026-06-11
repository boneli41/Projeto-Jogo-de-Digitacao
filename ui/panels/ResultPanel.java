package ui.panels;

import model.Player;
import ui.TypingGame;
import javax.swing.*;
import java.awt.*;

public class ResultPanel extends BasePanel {

    private JLabel lblStars;
    private JLabel lblMessage;
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
        setOpaque(false);

        // Estrutura principal da página com imagem de fundo
        JPanel content = new JPanel(new BorderLayout()) {
            private final Image bgImage = new ImageIcon("ui/assets/background-image-gamepanel.png").getImage();
            @Override
            protected void paintComponent(Graphics g) {
                g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        add(content, BorderLayout.CENTER);

        content.add(createHeader(),           BorderLayout.NORTH);
        content.add(buildContent(),           BorderLayout.CENTER);
        content.add(buildButtons(),           BorderLayout.SOUTH);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 180));
        header.setBorder(BorderFactory.createEmptyBorder(55, 0, 0, 0)); // Margem no topo da tela aumentada

        ImageIcon headerIcon = new ImageIcon("ui/assets/header-resultado.png");
        if (headerIcon.getIconWidth() > 0) {
            // Definimos limites máximos para a arte se encaixar no topo
            int maxW = 850; 
            int maxH = 140;
            
            int imgW = headerIcon.getIconWidth();
            int imgH = headerIcon.getIconHeight();
            
            // Calculamos a proporção para caber nos limites sem distorcer
            double scale = Math.min(1.0, Math.min((double) maxW / imgW, (double) maxH / imgH));
            int finalW = (int) (imgW * scale);
            int finalH = (int) (imgH * scale);

            Image scaledImg = headerIcon.getImage().getScaledInstance(finalW, finalH, Image.SCALE_SMOOTH);
            JLabel imgLabel = new JLabel(new ImageIcon(scaledImg));
            imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
            header.add(imgLabel, BorderLayout.CENTER);
        }
        return header;
    }

    private JPanel buildContent() {
        JPanel container = new JPanel(new GridBagLayout()); container.setOpaque(false);
        container.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30));
        
        RoundedPanel card = new RoundedPanel(40, Color.WHITE);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx  = 0;
        c.fill   = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(12, 0, 12, 0); // Espaçamento entre as linhas aumentado

        // Stars
        c.gridy = 0;
        lblStars = label("\u2606 \u2606 \u2606", FONT_STARS, COLOR_GOLD, SwingConstants.CENTER);
        card.add(lblStars, c);

        // Message
        c.gridy = 1;
        lblMessage = label("--", FONT_SUBTITLE, COLOR_SUCCESS, SwingConstants.CENTER);
        card.add(lblMessage, c);

        // Stats grid 2×3
        c.gridy = 2;
        card.add(buildStatsGrid(), c);

        container.add(card);

        return container;
    }

    // 2 linhas × 3 colunas
    private JPanel buildStatsGrid() {
        JPanel grid = new JPanel(new GridLayout(2, 3, 10, 8));
        grid.setOpaque(false);
        // Altura reduzida para evitar que o card fique muito longo verticalmente
        grid.setPreferredSize(new Dimension(850, 150));

        lblLivesValue    = statValue("--", Color.WHITE);
        lblLivesValue.setFont(FONT_HEARTS);
        lblScoreValue    = statValue("--", Color.WHITE);
        lblSequenceValue = statValue("--", Color.WHITE);
        lblLevelValue    = statValue("--", Color.WHITE);
        lblTimeValue     = statValue("--", Color.WHITE);
        lblWpmValue      = statValue("--", Color.WHITE);

        grid.add(statCard("Vidas",      lblLivesValue));
        grid.add(statCard("Pontuação",  lblScoreValue));
        grid.add(statCard("Sequência",  lblSequenceValue));
        grid.add(statCard("Nível",      lblLevelValue));
        grid.add(statCard("Tempo",      lblTimeValue));
        grid.add(statCard("PPM",        lblWpmValue));

        return grid;
    }

    private JPanel statCard(String title, JLabel valueLabel) {
        RoundedPanel card = new RoundedPanel(20, COLOR_ACCENT); // Azul para os mini-cards
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

    private JPanel buildButtons() {
        // Rodapé em azul sólido com as mesmas margens do GamePanel
        GradientPanel bar = new GradientPanel(COLOR_ACCENT, COLOR_ACCENT, false);
        bar.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 15));
        bar.setPreferredSize(new Dimension(0, 130)); // Altura aumentada para comportar a margem maior
        bar.setBorder(BorderFactory.createEmptyBorder(10, 40, 60, 40)); // Margem inferior aumentada para 60

        btnMenu = createModernButton("Finalizar Sessão", COLOR_BTN_BACK);
        btnMenu.setPreferredSize(new Dimension(220, 50));
        btnMenu.addActionListener(e -> game.returnToMenu());

        btnContinue = createModernButton("PRÓXIMO DESAFIO  >", COLOR_SUCCESS);
        btnContinue.setPreferredSize(new Dimension(300, 60));
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
        lblStars.setText(starsString(stars));
        lblStars.setForeground(stars > 0 ? COLOR_GOLD : new Color(170, 170, 150));

        String msg; Color col;
        switch (stars) {
            case 3:  msg = "Excelente!  Você foi perfeito!";      col = COLOR_SUCCESS;         break;
            case 2:  msg = "Muito bem!  Continue praticando!";     col = new Color(0, 140, 80); break;
            case 1:  msg = "Bom início!  Você está evoluindo!";    col = COLOR_WARNING;         break;
            default: msg = "Não desista!  Cada tentativa conta!";  col = COLOR_DANGER;          break;
        }
        lblMessage.setText(msg); // Mantém a mensagem principal
        lblMessage.setForeground(col); // Mantém a cor da mensagem

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
