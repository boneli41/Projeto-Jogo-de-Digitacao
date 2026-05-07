package ui.panels;

import model.Player;
import ui.TypingGame;
import javax.swing.*;
import java.awt.*;

public class MenuPanel extends BasePanel {

    private JTextField nameField;

    public MenuPanel(TypingGame game) {
        super(game);
    }

    @Override
    protected void initialize() {
        setLayout(new BorderLayout());
        add(buildHeader("  TeclaFácil  —  Aprenda a Digitar"), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
    }

    private JPanel buildCenter() {
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(COLOR_BG);
        center.setBorder(BorderFactory.createEmptyBorder(15, 50, 15, 50));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.fill  = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(8, 10, 8, 10);

        // Subtitle
        c.gridy = 0;
        JLabel sub = label(
            "Aprenda a usar o teclado de forma fácil e divertida!",
            FONT_SUBTITLE, COLOR_TEXT, SwingConstants.CENTER
        );
        center.add(sub, c);

        // Separator
        c.gridy = 1;
        JSeparator sep = new JSeparator();
        sep.setForeground(COLOR_PRIMARY);
        sep.setPreferredSize(new Dimension(500, 2));
        center.add(sep, c);

        // Name label
        c.gridy = 2;
        center.add(label("Como você se chama?", FONT_BODY, COLOR_TEXT, SwingConstants.CENTER), c);

        // Name field
        c.gridy = 3;
        nameField = new JTextField(18);
        nameField.setFont(FONT_INPUT);
        nameField.setHorizontalAlignment(JTextField.CENTER);
        nameField.setPreferredSize(new Dimension(360, 52));
        nameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_PRIMARY, 2),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        center.add(nameField, c);

        // Info cards
        c.gridy = 4;
        center.add(buildInfoCards(), c);

        // Start button
        c.gridy = 5;
        JButton startBtn = createButton("  COMEÇAR  ", COLOR_SUCCESS);
        startBtn.setFont(new Font("Arial", Font.BOLD, 24));
        startBtn.setPreferredSize(new Dimension(300, 62));
        startBtn.addActionListener(e -> startGame());
        nameField.addActionListener(e -> startGame());
        center.add(startBtn, c);

        return center;
    }

    private JPanel buildInfoCards() {
        JPanel row = new JPanel(new GridLayout(1, 3, 16, 0));
        row.setBackground(COLOR_BG);
        row.setPreferredSize(new Dimension(620, 130));
        row.add(infoCard("Exercícios",  "Do básico ao avançado, no seu ritmo"));
        row.add(infoCard("Estrelas",    "Ganhe pontos e suba de nível"));
        row.add(infoCard("Conquistas",  "Desbloqueie medalhas especiais"));
        return row;
    }

    // HTML permite quebra de linha automática dentro do card
    private JPanel infoCard(String title, String desc) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(COLOR_CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        JLabel t = label(
            "<html><center>" + title + "</center></html>",
            FONT_INFO, COLOR_PRIMARY, SwingConstants.CENTER
        );
        JLabel d = label(
            "<html><center>" + desc + "</center></html>",
            FONT_SMALL, new Color(90, 90, 90), SwingConstants.CENTER
        );
        card.add(t, BorderLayout.NORTH);
        card.add(d, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(COLOR_FOOTER_BG);
        footer.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        footer.add(label(
            "Dica: Use as duas mãos ao digitar — mão esquerda nas teclas A S D F  e a direita em J K L",
            FONT_SMALL, new Color(80, 80, 70), SwingConstants.CENTER
        ));
        return footer;
    }

    private void startGame() {
        String raw  = nameField.getText().trim();
        String name = raw.isEmpty() ? "Amigo" : raw;
        game.startGame(new Player(name));
    }
}
