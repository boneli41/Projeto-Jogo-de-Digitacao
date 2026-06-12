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
        setOpaque(false); // Garante que o painel base não pinte fundo cinza

        // Estrutura principal da página com imagem de fundo
        JPanel content = new JPanel(new BorderLayout()) {
            private final Image bgImage = new ImageIcon("ui/assets/background-image.png").getImage();
            @Override
            protected void paintComponent(Graphics g) {
                // Não chamamos super.paintComponent(g) para evitar que o Swing pinte o fundo padrão
                g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        add(content, BorderLayout.CENTER);

        content.add(createHeader(), BorderLayout.NORTH);
        content.add(createMainContent(), BorderLayout.CENTER);
        content.add(createFooter(), BorderLayout.SOUTH);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false); // Remove o fundo sólido para herdar a cor de fundo da página
        header.setPreferredSize(new Dimension(0, 60));
        header.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 50));

        // Alteramos a cor dos ícones para COLOR_PRIMARY para manter o contraste com o fundo claro
        JLabel headerIcons = label("", FONT_TITLE, COLOR_PRIMARY, SwingConstants.RIGHT);
        headerIcons.setFont(new Font(Font.DIALOG, Font.BOLD, 36));
        header.add(headerIcons, BorderLayout.EAST);
        return header;
    }

    private JPanel createMainContent() {
        JPanel main = new JPanel(new GridBagLayout()); main.setOpaque(false);
        main.setBorder(BorderFactory.createEmptyBorder(0, 40, 30, 40));
        GridBagConstraints gbc = new GridBagConstraints(); gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 15, 10, 15); gbc.weighty = 1.0;

        // Coluna Esquerda
        JPanel left = new JPanel(); left.setOpaque(false);
        left.setOpaque(false); // Reforço de transparência
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        // Logo carregada e redimensionada proporcionalmente para a lateral
        ImageIcon logoIcon = new ImageIcon("ui/assets/logo-digita-comigo.png");
        int w = 380; int h = 240; // Altura fallback caso a imagem demore a carregar
        if (logoIcon.getIconWidth() > 0) {
            h = (w * logoIcon.getIconHeight()) / logoIcon.getIconWidth();
        }
        Image scaledLogo = logoIcon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        left.add(logoLabel);

        left.add(Box.createVerticalStrut(8)); // Espaçamento entre logo e o cartão

        RoundedPanel welcome = new RoundedPanel(30, Color.WHITE);
        welcome.setLayout(new BoxLayout(welcome, BoxLayout.Y_AXIS));
        welcome.setMaximumSize(new Dimension(400, 160));
        welcome.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25)); // Padding reduzido
        welcome.setAlignmentX(Component.LEFT_ALIGNMENT);

        welcome.add(label("Bem-vindo(a)!", FONT_SUBTITLE, COLOR_PRIMARY, SwingConstants.LEFT));
        welcome.add(label("<html><body style='width:250px'>Um jogo feito para ajudar você a aprender de forma leve e divertida.</body></html>", FONT_BODY, COLOR_TEXT, SwingConstants.LEFT));
        welcome.setOpaque(false);
        left.add(welcome);

        left.add(Box.createVerticalStrut(10)); // Espaço entre o bem-vindo e a jornada
        // Nova Seção da Jornada com Progressão Visual
        left.add(buildJourneySection());

        left.add(Box.createVerticalGlue()); // Empurra o conteúdo para cima, evitando que o cartão estique
        gbc.gridx = 0; gbc.weightx = 0.28;
        gbc.insets = new Insets(-60, 15, 10, 15); // Subimos menos (-60) para garantir que a base não suma
        main.add(left, gbc);

        // Coluna Central
        gbc.insets = new Insets(10, 15, 10, 15); // Restaura o espaçamento para as outras colunas
        JPanel center = new JPanel(new GridBagLayout()); center.setOpaque(false);
        GridBagConstraints centerGbc = new GridBagConstraints();
        centerGbc.gridx = 0; centerGbc.gridy = 0;
        centerGbc.fill = GridBagConstraints.HORIZONTAL; centerGbc.anchor = GridBagConstraints.CENTER;

        // Sessão principal aumentada com bordas internas maiores
        RoundedPanel card = new RoundedPanel(40, Color.WHITE);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(35, 0, 0, 0));

        GridBagConstraints cc = new GridBagConstraints();
        cc.gridx = 0; cc.fill = GridBagConstraints.HORIZONTAL;

        cc.gridy = 0;
        cc.insets = new Insets(0, 60, 5, 60);
        card.add(label("Como você se chama?", FONT_SUBTITLE, COLOR_TEXT, SwingConstants.CENTER), cc);

        nameField = new JTextField(); nameField.setFont(FONT_INPUT);
        nameField.setHorizontalAlignment(JTextField.CENTER); nameField.setPreferredSize(new Dimension(450, 70));
        nameField.putClientProperty("JTextField.placeholderText", "Ex: João");
        nameField.addActionListener(e -> startGame()); // Permite apertar ENTER
        cc.gridy = 1; cc.insets = new Insets(5, 60, 20, 60);
        card.add(nameField, cc);

        JButton btn = createModernButton("INICIAR", COLOR_SUCCESS);
        btn.setFont(FONT_BUTTON);
        btn.setPreferredSize(new Dimension(320, 65)); btn.addActionListener(e -> startGame());
        cc.gridy = 2; cc.insets = new Insets(0, 60, 25, 60);
        card.add(btn, cc);

        // Imagem agora ocupa toda a largura e fica colada na borda inferior
        ImageIcon illustrationIcon = new ImageIcon("ui/assets/image-menupanel.png");
        int maxW = 570; int maxH = 380; // Largura total útil da sessão branca
        int finalW = maxW; int finalH = maxH;

        if (illustrationIcon.getIconWidth() > 0) {
            double scale = Math.min((double)maxW / illustrationIcon.getIconWidth(),
                    (double)maxH / illustrationIcon.getIconHeight());
            finalW = (int) (illustrationIcon.getIconWidth() * scale);
            finalH = (int) (illustrationIcon.getIconHeight() * scale);
        }
        Image scaledImg = illustrationIcon.getImage().getScaledInstance(finalW, finalH, Image.SCALE_SMOOTH);
        cc.gridy = 3; cc.weighty = 1.0;
        cc.anchor = GridBagConstraints.SOUTH;
        // Insets zerados para colar a imagem na borda inferior e ocupar a largura total
        cc.insets = new Insets(0, 0, 0, 0);
        card.add(new JLabel(new ImageIcon(scaledImg)), cc);

        center.add(card, centerGbc);

        gbc.gridx = 1; gbc.weightx = 0.44; main.add(center, gbc);

        // Coluna Direita
        JPanel right = new JPanel(new GridBagLayout()); right.setOpaque(false);
        GridBagConstraints rc = new GridBagConstraints(); rc.fill = GridBagConstraints.BOTH; rc.gridx = 0; rc.weightx = 1;
        rc.insets = new Insets(0,0,20,0);
        RoundedPanel rank = new RoundedPanel(30, Color.WHITE); rank.setLayout(new BoxLayout(rank, BoxLayout.Y_AXIS));
        rank.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        JLabel rankTitle = label("Mini Ranking", FONT_INFO, COLOR_TEXT, SwingConstants.CENTER);
        rankTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        rank.add(rankTitle);
        rank.add(rankLine("1º", "Maria", "1500")); rank.add(rankLine("2º", "João", "1200"));
        right.add(rank, rc);

        RoundedPanel why = new RoundedPanel(30, Color.WHITE); why.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        why.add(label("<html><b>POR QUE APRENDER?</b><br>• Autonomia Digital<br>• Confiança</html>", FONT_SMALL, COLOR_PRIMARY, SwingConstants.LEFT));
        right.add(why, rc);

        // Novo quadro com as funcionalidades (Conheça o teclado, etc.)
        RoundedPanel features = new RoundedPanel(30, Color.WHITE);
        features.setLayout(new BoxLayout(features, BoxLayout.Y_AXIS));
        features.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        String[] items = {
                "•  Conheça o teclado",
                "•  Exercícios práticos",
                "•  Evolua sempre"
        };
        for (String i : items) {
            features.add(label(i, FONT_SMALL, COLOR_PRIMARY, SwingConstants.LEFT));
            features.add(Box.createVerticalStrut(5));
        }
        right.add(features, rc);

        gbc.gridx = 2; gbc.weightx = 0.28; main.add(right, gbc);

        return main;
    }

    private JPanel buildJourneySection() {
        RoundedPanel journey = new RoundedPanel(30, COLOR_ACCENT);
        journey.setLayout(new BorderLayout(0, 10));
        // Padding interno: o valor 30 na base garante que o conteúdo não chegue na curva
        journey.setBorder(BorderFactory.createEmptyBorder(15, 20, 30, 20));
        journey.setMaximumSize(new Dimension(420, 150)); // Aumentado para acomodar nomes maiores
        journey.add(label("Sua Jornada nos Níveis", FONT_INFO, Color.WHITE, SwingConstants.CENTER), BorderLayout.NORTH);

        // Mudamos para um painel que não força os componentes contra as bordas norte/sul
        JPanel flow = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 80));
                g2.setStroke(new BasicStroke(3f));
                int y = 18;
                g2.drawLine(40, y, getWidth() - 40, y);
                g2.dispose();
            }
        };
        flow.setLayout(new BoxLayout(flow, BoxLayout.Y_AXIS));
        flow.setOpaque(false);

        JPanel icons = new JPanel(new GridLayout(1, 4)); icons.setOpaque(false);
        String[] symb = {"\uD83D\uDD13", "\uD83D\uDD12", "\uD83D\uDD12", "\uD83D\uDD12"};
        for (String s : symb) {
            JLabel l = label(s, new Font(Font.DIALOG, Font.PLAIN, 22), Color.WHITE, SwingConstants.CENTER);
            icons.add(l);
        }

        JPanel labels = new JPanel(new GridLayout(1, 4)); labels.setOpaque(false);
        String[] texts = {"Minúsculas", "Maiúsculas", "Pontuação", "Acentos"};
        for (String t : texts) {
            labels.add(label(t, FONT_SMALL, Color.WHITE, SwingConstants.CENTER));
        }

        flow.add(icons);
        flow.add(Box.createVerticalStrut(8)); // Espaçamento fixo entre ícones e textos
        flow.add(labels);

        journey.add(flow, BorderLayout.CENTER);
        journey.setAlignmentX(Component.LEFT_ALIGNMENT);
        return journey;
    }

    private JPanel rankLine(String m, String n, String p) {
        JPanel line = new JPanel(new GridLayout(1, 3));
        line.setOpaque(false);
        line.add(label(m, FONT_SMALL, COLOR_PRIMARY, SwingConstants.CENTER));
        line.add(label(n, FONT_SMALL, COLOR_TEXT, SwingConstants.CENTER));
        line.add(label(p, FONT_SMALL, COLOR_SECONDARY, SwingConstants.CENTER));
        return line;
    }

    private JPanel createFooter() {
        // Usando uma cor sólida para um visual mais simples e limpo
        GradientPanel footer = new GradientPanel(COLOR_ACCENT, COLOR_ACCENT, true);
        footer.setLayout(new GridBagLayout());
        footer.setPreferredSize(new Dimension(0, 60));
        JLabel footerText = label("Aqui todo mundo aprende! Respeite seu tempo e divirta-se!", FONT_BODY, Color.WHITE, SwingConstants.CENTER);
        footerText.setFont(new Font(Font.DIALOG, Font.PLAIN, 18));
        footer.add(footerText);
        return footer;
    }

    private void startGame() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            // Impede o início e dá um feedback visual (borda vermelha)
            nameField.setBorder(BorderFactory.createLineBorder(COLOR_DANGER, 2));
            nameField.requestFocusInWindow();
            return;
        }
        game.startGame(new Player(name));
    }
}
