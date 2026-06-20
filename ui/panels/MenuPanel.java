package ui.panels;

import factory.ExerciseFactory;
import model.Player;
import ui.TypingGame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import java.util.List;

public class MenuPanel extends BasePanel {

    private JTextField nameField;
    private JComboBox<String> levelSelector; // seletor de nível inicial

    // Painel do ranking — atualizado toda vez que o menu é exibido
    private JPanel rankingContent;

    public MenuPanel(TypingGame game) {
        super(game);
    }

    @Override
    protected void initialize() {
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel content = new JPanel(new BorderLayout()) {
            private final Image bgImage = new ImageIcon("ui/assets/background-image.png").getImage();
            @Override
            protected void paintComponent(Graphics g) {
                g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        add(content, BorderLayout.CENTER);

        content.add(createHeader(),      BorderLayout.NORTH);
        content.add(createMainContent(), BorderLayout.CENTER);
        content.add(createFooter(),      BorderLayout.SOUTH);
    }

    /**
     * Chamado pelo TypingGame toda vez que o menu é exibido,
     * para refrescar o ranking com os dados mais recentes do arquivo.
     */
    public void refreshRanking() {
        if (rankingContent == null) return;
        rankingContent.removeAll();

        JLabel rankTitle = label("🏆 Ranking", FONT_INFO, COLOR_TEXT, SwingConstants.CENTER);
        rankTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        rankingContent.add(rankTitle);
        rankingContent.add(Box.createVerticalStrut(8));

        List<String[]> ranking = Player.loadRanking("ranking.txt");

        if (ranking.isEmpty()) {
            JLabel empty = label("Nenhum jogador ainda!", FONT_SMALL, COLOR_SECONDARY, SwingConstants.CENTER);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            rankingContent.add(empty);
        } else {
            int max = Math.min(ranking.size(), 5);
            String[] medals = {"🥇", "🥈", "🥉", "4º", "5º"};
            for (int i = 0; i < max; i++) {
                String[] parts = ranking.get(i);
                String name  = parts[0];
                String score = parts[1] + " pts";
                rankingContent.add(rankLine(medals[i], name, score));
                rankingContent.add(Box.createVerticalStrut(4));
            }
        }

        rankingContent.revalidate();
        rankingContent.repaint();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 60));
        header.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 50));
        JLabel headerIcons = label("", FONT_TITLE, COLOR_PRIMARY, SwingConstants.RIGHT);
        headerIcons.setFont(new Font(Font.DIALOG, Font.BOLD, 36));
        header.add(headerIcons, BorderLayout.EAST);
        return header;
    }

    private JPanel createMainContent() {
        JPanel main = new JPanel(new GridBagLayout());
        main.setOpaque(false);
        main.setBorder(BorderFactory.createEmptyBorder(0, 40, 30, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.weighty = 1.0;

        // ---- Coluna Esquerda ----
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        ImageIcon logoIcon = new ImageIcon("ui/assets/logo-digita-comigo.png");
        int w = 380, h = 240;
        if (logoIcon.getIconWidth() > 0) {
            h = (w * logoIcon.getIconHeight()) / logoIcon.getIconWidth();
        }
        Image scaledLogo = logoIcon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        left.add(logoLabel);
        left.add(Box.createVerticalStrut(8));

        RoundedPanel welcome = new RoundedPanel(30, Color.WHITE);
        welcome.setLayout(new BoxLayout(welcome, BoxLayout.Y_AXIS));
        welcome.setMaximumSize(new Dimension(400, 160));
        welcome.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        welcome.setAlignmentX(Component.LEFT_ALIGNMENT);
        welcome.add(label("Bem-vindo(a)!", FONT_SUBTITLE, COLOR_PRIMARY, SwingConstants.LEFT));
        welcome.add(label("<html><body style='width:250px'>Um jogo feito para ajudar você a aprender de forma leve e divertida.</body></html>",
                FONT_BODY, COLOR_TEXT, SwingConstants.LEFT));
        welcome.setOpaque(false);
        left.add(welcome);

        // Seção "Sua Jornada nos Níveis" removida — XP/cadeados não fazem
        // mais sentido com o sistema atual. left.add(buildJourneySection());

        left.add(Box.createVerticalGlue());

        gbc.gridx = 0; gbc.weightx = 0.28;
        gbc.insets = new Insets(-60, 15, 10, 15);
        main.add(left, gbc);

        // ---- Coluna Central ----
        gbc.insets = new Insets(10, 15, 10, 15);
        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        GridBagConstraints centerGbc = new GridBagConstraints();
        centerGbc.gridx = 0; centerGbc.gridy = 0;
        centerGbc.fill = GridBagConstraints.HORIZONTAL;
        centerGbc.anchor = GridBagConstraints.CENTER;

        RoundedPanel card = new RoundedPanel(40, Color.WHITE);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(35, 0, 0, 0));

        GridBagConstraints cc = new GridBagConstraints();
        cc.gridx = 0; cc.fill = GridBagConstraints.HORIZONTAL;

        cc.gridy = 0; cc.insets = new Insets(0, 60, 5, 60);
        card.add(label("Como você se chama?", FONT_SUBTITLE, COLOR_TEXT, SwingConstants.CENTER), cc);

        nameField = new JTextField();
        nameField.setFont(FONT_INPUT);
        nameField.setHorizontalAlignment(JTextField.CENTER);
        nameField.setPreferredSize(new Dimension(450, 70));
        nameField.putClientProperty("JTextField.placeholderText", "Ex: João");
        nameField.addActionListener(e -> startGame());
        cc.gridy = 1; cc.insets = new Insets(5, 60, 20, 60);
        card.add(nameField, cc);

        JButton btn = createModernButton("INICIAR", COLOR_SUCCESS);
        btn.setFont(FONT_BUTTON);
        btn.setPreferredSize(new Dimension(320, 65));
        btn.addActionListener(e -> startGame());
        cc.gridy = 2; cc.insets = new Insets(0, 60, 10, 60);
        card.add(btn, cc);

        // ---- Seletor de nível inicial (abaixo do botão Iniciar) ----
        JPanel levelRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        levelRow.setOpaque(false);

        JLabel levelLabel = label("ou comece direto no:", FONT_SMALL, COLOR_SECONDARY, SwingConstants.CENTER);
        levelRow.add(levelLabel);

        String[] levelOptions = new String[ExerciseFactory.LEVEL_NAMES.length];
        for (int i = 0; i < levelOptions.length; i++) {
            levelOptions[i] = "Nível " + (i + 1) + " — " + ExerciseFactory.LEVEL_NAMES[i];
        }
        levelSelector = new JComboBox<>(levelOptions);
        levelSelector.setFont(FONT_SMALL);
        levelSelector.setPreferredSize(new Dimension(200, 32));
        levelRow.add(levelSelector);

        cc.gridy = 3; cc.insets = new Insets(0, 60, 25, 60);
        card.add(levelRow, cc);

        ImageIcon illustrationIcon = new ImageIcon("ui/assets/image-menupanel.png");
        int maxW = 570, maxH = 340, finalW = maxW, finalH = maxH;
        if (illustrationIcon.getIconWidth() > 0) {
            double scale = Math.min((double)maxW / illustrationIcon.getIconWidth(),
                    (double)maxH / illustrationIcon.getIconHeight());
            finalW = (int)(illustrationIcon.getIconWidth()  * scale);
            finalH = (int)(illustrationIcon.getIconHeight() * scale);
        }
        Image scaledImg = illustrationIcon.getImage().getScaledInstance(finalW, finalH, Image.SCALE_SMOOTH);
        cc.gridy = 4; cc.weighty = 1.0;
        cc.anchor = GridBagConstraints.SOUTH;
        cc.insets = new Insets(0, 0, 0, 0);
        card.add(new JLabel(new ImageIcon(scaledImg)), cc);

        center.add(card, centerGbc);
        gbc.gridx = 1; gbc.weightx = 0.44;
        main.add(center, gbc);

        // ---- Coluna Direita ----
        JPanel right = new JPanel(new GridBagLayout());
        right.setOpaque(false);
        GridBagConstraints rc = new GridBagConstraints();
        rc.fill = GridBagConstraints.BOTH;
        rc.gridx = 0; rc.weightx = 1;
        rc.insets = new Insets(0, 0, 12, 0);

        // Ranking carregado do arquivo
        RoundedPanel rankCard = new RoundedPanel(30, Color.WHITE);
        rankingContent = new JPanel();
        rankingContent.setLayout(new BoxLayout(rankingContent, BoxLayout.Y_AXIS));
        rankingContent.setOpaque(false);
        rankCard.setLayout(new BorderLayout());
        rankCard.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        rankCard.add(rankingContent, BorderLayout.CENTER);
        refreshRanking();
        rc.gridy = 0;
        right.add(rankCard, rc);

        // Card "Por que aprender"
        RoundedPanel why = new RoundedPanel(30, Color.WHITE);
        why.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        why.add(label("<html><b>POR QUE APRENDER?</b><br>• Autonomia Digital<br>• Confiança no computador</html>",
                FONT_SMALL, COLOR_PRIMARY, SwingConstants.LEFT));
        rc.gridy = 1;
        right.add(why, rc);

        // Card "Conheça o Teclado" com link clicável
        RoundedPanel keyboard = new RoundedPanel(30, Color.WHITE);
        keyboard.setLayout(new BoxLayout(keyboard, BoxLayout.Y_AXIS));
        keyboard.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel kbTitle = label("⌨️  Conheça o Teclado", FONT_INFO, COLOR_PRIMARY, SwingConstants.LEFT);
        kbTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel kbDesc = label("<html><body style='width:160px'>Veja onde cada tecla fica antes de começar!</body></html>",
                FONT_SMALL, COLOR_TEXT, SwingConstants.LEFT);
        kbDesc.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel kbLink = new JLabel("<html><u>🔗 Abrir imagem do teclado</u></html>");
        kbLink.setFont(FONT_SMALL);
        kbLink.setForeground(COLOR_ACCENT);
        kbLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        kbLink.setAlignmentX(Component.LEFT_ALIGNMENT);
        kbLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(
                            "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0c/" +
                                    "QWERTY_keyboard_diagram.svg/1200px-QWERTY_keyboard_diagram.svg.png"
                    ));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MenuPanel.this,
                            "Não foi possível abrir o navegador.",
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        keyboard.add(kbTitle);
        keyboard.add(Box.createVerticalStrut(6));
        keyboard.add(kbDesc);
        keyboard.add(Box.createVerticalStrut(8));
        keyboard.add(kbLink);

        rc.gridy = 2;
        right.add(keyboard, rc);

        gbc.gridx = 2; gbc.weightx = 0.28;
        gbc.insets = new Insets(10, 15, 10, 15);
        main.add(right, gbc);

        return main;
    }

    /** Linha do ranking com 3 colunas: medalha | nome | pontos */
    private JPanel rankLine(String medal, String name, String score) {
        JPanel line = new JPanel(new GridLayout(1, 3, 4, 0));
        line.setOpaque(false);
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        line.add(label(medal, FONT_SMALL, COLOR_PRIMARY,   SwingConstants.CENTER));
        line.add(label(name,  FONT_SMALL, COLOR_TEXT,      SwingConstants.LEFT));
        line.add(label(score, FONT_SMALL, COLOR_SECONDARY, SwingConstants.CENTER));
        return line;
    }

    private JPanel createFooter() {
        GradientPanel footer = new GradientPanel(COLOR_ACCENT, COLOR_ACCENT, true);
        footer.setLayout(new GridBagLayout());
        footer.setPreferredSize(new Dimension(0, 60));
        JLabel footerText = label("Aqui todo mundo aprende! Respeite seu tempo e divirta-se!",
                FONT_BODY, Color.WHITE, SwingConstants.CENTER);
        footerText.setFont(new Font(Font.DIALOG, Font.PLAIN, 18));
        footer.add(footerText);
        return footer;
    }

    private void startGame() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            nameField.setBorder(BorderFactory.createLineBorder(COLOR_DANGER, 2));
            nameField.requestFocusInWindow();
            return;
        }

        // Nível selecionado no combo (índice 0 = Nível 1, índice 4 = Nível 5)
        int selectedLevel = levelSelector.getSelectedIndex() + 1;
        game.startGame(new Player(name), selectedLevel);
    }
}