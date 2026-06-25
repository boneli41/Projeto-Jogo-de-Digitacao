package ui.panels;

import model.Player;
import ui.TypingGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import java.util.List;

public class MenuPanel extends BasePanel {

    private JTextField nameField;
    private JPanel rankingContent;   // top-3 exibido no card do menu
    private static final String ICON_ARROW = ">";
    private static final String NAME_PLACEHOLDER = "Entre com nome e sobrenome";
    private static final Font FONT_PLACEHOLDER = new Font("Poppins", Font.PLAIN, 16);
    private static final Color COLOR_PLACEHOLDER = new Color(156, 163, 175);

    // Nomes e ícones dos 5 módulos
    private static final String[] MODULE_LABELS = {
            "Minúsculas", "Maiúsculas", "Números", "Acentos", "Pontuação"
    };
    private static final String[] MODULE_ICONS = {
            "abc", "ABC", "123", "áéí", ".,!"
    };
    private static final String[] MODULE_DESCRIPTIONS = {
            "letras pequenas", "letras grandes", "números", "acentos", "pontos e vírgulas"
    };

    public MenuPanel(TypingGame game) {
        super(game);
    }

    @Override
    protected void initialize() {
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel content = backgroundPanel("ui/assets/background-image.png");
        add(content, BorderLayout.CENTER);

        content.add(createHeader(),      BorderLayout.NORTH);
        content.add(createMainContent(), BorderLayout.CENTER);
        content.add(createFooter(),      BorderLayout.SOUTH);
    }

    // ================================================================
    //  Ranking (top-3 visível, clique abre dialog completo)
    // ================================================================

    /**
     * Chamado pelo TypingGame sempre que o menu é exibido,
     * para atualizar o card de ranking com os dados mais recentes.
     */
    public void refreshRanking() {
        if (rankingContent == null) return;
        rankingContent.removeAll();

        // Título clicável
        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        titleRow.setOpaque(false);
        titleRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleRow.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 30));
        titleRow.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel rankTextLbl = new JLabel("<html><u>Ranking</u></html>");
        rankTextLbl.setFont(FONT_INFO);
        rankTextLbl.setForeground(COLOR_TEXT);

        titleRow.add(rankTextLbl);

        MouseAdapter rankClick = new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { showRankingDialog(); }
        };
        titleRow.addMouseListener(rankClick);
        rankTextLbl.addMouseListener(rankClick);
        titleRow.setToolTipText("Clique para ver o ranking completo");

        rankingContent.add(titleRow);
        rankingContent.add(Box.createVerticalStrut(8));

        List<String[]> ranking = Player.loadRanking("ranking.txt");

        if (ranking.isEmpty()) {
            JLabel empty = label("Nenhum jogador ainda!", FONT_SMALL, COLOR_SECONDARY, SwingConstants.CENTER);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            rankingContent.add(empty);
        } else {
            // Mostra apenas os 3 primeiros no card do menu
            int max = Math.min(ranking.size(), 3);
            String[] medals = {ICON_ARROW, ICON_ARROW, ICON_ARROW};
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

    /** Abre o dialog com o ranking completo (sem botão "Voltar ao Menu" interno). */
    public void showRankingDialog() {
        JDialog dialog = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Ranking", true);
        dialog.setSize(480, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        JLabel title = new JLabel("Ranking de Jogadores");
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
            String[] medals = {"1º", "2º", "3º", "4º", "5º", "6º", "7º", "8º", "9º", "10º"};
            int max = Math.min(ranking.size(), medals.length);
            for (int i = 0; i < max; i++) {
                String[] parts = ranking.get(i);
                JPanel row = new JPanel(new GridLayout(1, 3, 8, 0));
                row.setOpaque(false);
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
                row.add(label(medals[i],        FONT_BODY, COLOR_PRIMARY,   SwingConstants.CENTER));
                row.add(label(parts[0],         FONT_BODY, COLOR_TEXT,      SwingConstants.LEFT));
                row.add(label(parts[1] + " pts",FONT_BODY, COLOR_SECONDARY, SwingConstants.CENTER));
                panel.add(row);
                panel.add(Box.createVerticalStrut(6));
            }
        }

        // Sem botão "Voltar ao Menu" aqui — o X da janela fecha
        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        dialog.add(scroll);
        dialog.setVisible(true);
    }

    // ================================================================
    //  Header e conteúdo
    // ================================================================
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
        if (logoIcon.getIconWidth() > 0)
            h = (w * logoIcon.getIconHeight()) / logoIcon.getIconWidth();
        Image scaledLogo = logoIcon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        left.add(logoLabel);
        left.add(Box.createVerticalStrut(8));

        left.add(buildJourneySection()); // <<< seção de módulos clicáveis
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
        card.setBorder(BorderFactory.createEmptyBorder(28, 0, 0, 0));

        GridBagConstraints cc = new GridBagConstraints();
        cc.gridx = 0; cc.fill = GridBagConstraints.HORIZONTAL;

        cc.gridy = 0; cc.insets = new Insets(0, 60, 18, 60);
        card.add(label("<html><center>Olá! Que bom ver você por aqui!<br>O <b>Digita Comigo</b> é um jogo feito para ajudar você a aprender de forma leve e divertida.</center></html>",
                FONT_BODY, COLOR_TEXT, SwingConstants.CENTER), cc);

        cc.gridy = 1; cc.insets = new Insets(0, 60, 6, 60);
        card.add(label("COMO VOCÊ SE CHAMA?", FONT_INFO, COLOR_PRIMARY, SwingConstants.CENTER), cc);

        nameField = new JTextField();
        nameField.setFont(FONT_INPUT);
        nameField.setHorizontalAlignment(JTextField.CENTER);
        nameField.setPreferredSize(new Dimension(400, 58));
        showNamePlaceholder();
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("focusOwner", e -> {
            if (e.getNewValue() == nameField) {
                clearNamePlaceholder();
            } else if (e.getOldValue() == nameField) {
                showNamePlaceholder();
            }
        });
        nameField.addActionListener(e -> startGame(1));
        cc.gridy = 2; cc.insets = new Insets(4, 60, 14, 60);
        card.add(nameField, cc);

        JButton btn = createModernButton("INICIAR", COLOR_PRIMARY);
        btn.setFont(new Font("Poppins SemiBold", Font.BOLD, 16));
        btn.setPreferredSize(new Dimension(250, 48));
        btn.addActionListener(e -> startGame(1));
        cc.gridy = 3; cc.insets = new Insets(0, 60, 14, 60);
        card.add(btn, cc);

        ImageIcon illustrationIcon = new ImageIcon("ui/assets/image-menupanel.png");
        int maxW = 430, maxH = 250, finalW = maxW, finalH = maxH;
        if (illustrationIcon.getIconWidth() > 0) {
            double scale = Math.min((double)maxW / illustrationIcon.getIconWidth(),
                    (double)maxH / illustrationIcon.getIconHeight());
            finalW = (int)(illustrationIcon.getIconWidth()  * scale);
            finalH = (int)(illustrationIcon.getIconHeight() * scale);
        }
        Image scaledImg = illustrationIcon.getImage().getScaledInstance(finalW, finalH, Image.SCALE_SMOOTH);
        cc.gridy = 4; cc.weighty = 1.0;
        cc.fill = GridBagConstraints.NONE;
        cc.anchor = GridBagConstraints.SOUTH;
        cc.insets = new Insets(0, 56, 0, 56);
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

        // Card de ranking (top-3, clicável para ver mais)
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

        // Card "Conheça o Teclado" — abre dialog interno com desenho do teclado
        RoundedPanel keyboard = new RoundedPanel(30, Color.WHITE);
        keyboard.setLayout(new BoxLayout(keyboard, BoxLayout.Y_AXIS));
        keyboard.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        JLabel kbTitle = label("<html><b>CONHEÇA O TECLADO</html>", FONT_SMALL, COLOR_PRIMARY, SwingConstants.LEFT);
        kbTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel kbDesc = label("<html><body style='width:160px'>Entenda a anatômia do<br>seu teclado antes de começar!</body></html>",
                FONT_SMALL, COLOR_TEXT, SwingConstants.LEFT);
        kbDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel kbLink = new JLabel("<html><u>" + ICON_ARROW + " Ver teclado</u></html>");
        kbLink.setFont(FONT_SMALL);
        kbLink.setForeground(COLOR_ACCENT);
        kbLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        kbLink.setAlignmentX(Component.LEFT_ALIGNMENT);
        kbLink.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { showKeyboardDialog(); }
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

    // ================================================================
    //  Dialog interno: teclado centralizado, legendas esq/dir
    // ================================================================
    private void showKeyboardDialog() {
        JDialog dlg = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), "", false);
        dlg.setSize(1250, 580);
        dlg.setLocationRelativeTo(this);
        dlg.setResizable(false);

        JPanel root = new JPanel(new BorderLayout(0, 8));
        root.setBackground(new Color(245, 247, 251));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // ---- Cabeçalho ----
        JLabel title = new JLabel(
                "\u2328\ufe0f  Conhe\u00e7a o Teclado \u2014 posi\u00e7\u00e3o das teclas especiais",
                SwingConstants.CENTER);
        title.setFont(FONT_INFO);
        title.setForeground(COLOR_PRIMARY);

        JLabel shiftHint = new JLabel(
                "<html><center>"
                        + "<b style='color:#1e3a8a'>Como usar o SHIFT:</b> "
                        + "Pressione e <b>segure</b> a tecla "
                        + "<b style='color:#1e40af'>SHIFT</b> (canto inferior esquerdo ou direito do teclado) "
                        + "e, enquanto segura, pressione a outra tecla. "
                        + "Use para <b>letras mai\u00fasculas</b> e s\u00edmbolos como "
                        + "<b>! @ # $ % &amp; *</b>"
                        + "</center></html>",
                SwingConstants.CENTER);
        shiftHint.setFont(FONT_SMALL);
        shiftHint.setForeground(new Color(30, 58, 138));
        shiftHint.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(147, 197, 253), 1, true),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)));
        shiftHint.setOpaque(true);
        shiftHint.setBackground(new Color(239, 246, 255));

        JPanel header = new JPanel(new BorderLayout(0, 6));
        header.setOpaque(false);
        header.add(title, BorderLayout.NORTH);
        header.add(shiftHint, BorderLayout.CENTER);
        root.add(header, BorderLayout.NORTH);

        // ---- Corpo: legenda-esq | teclado | legenda-dir ----
        JPanel body = new JPanel(new BorderLayout(10, 0));
        body.setOpaque(false);

        body.add(buildLegendLeft(),  BorderLayout.WEST);
        body.add(buildLegendRight(), BorderLayout.EAST);

        // Teclado: ocupa o espaço central, desenhado via paintComponent
        JPanel kbView = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawAnnotatedKeyboard((Graphics2D) g.create(), getWidth(), getHeight());
            }
        };
        kbView.setBackground(new Color(243, 246, 252));
        kbView.setBorder(BorderFactory.createLineBorder(new Color(186, 205, 235), 1, true));
        body.add(kbView, BorderLayout.CENTER);

        root.add(body, BorderLayout.CENTER);
        dlg.add(root);
        dlg.setVisible(true);
    }

    // ---- Legenda ESQUERDA (Tab, Caps, Shift esq, Ctrl, Alt) ----
    private JPanel buildLegendLeft() {
        // Cada item: [nome curto, descricao, seta direção]
        // Seta aponta para a direita ( → ) porque a tecla está à direita da legenda
        String[][] items = {
                {"Tab",      "Avan\u00e7a de campo"},
                {"Caps Lock","Fixa letras mai\u00fasculas"},
                {"SHIFT",    "Segure + outra tecla\npara mai\u00fascula/s\u00edmbolo"},
                {"Ctrl",     "Atalhos (n\u00e3o usar no jogo)"},
                {"Alt",      "Atalhos (n\u00e3o usar no jogo)"},
        };
        return buildLegendPanel(items, true);
    }

    // ---- Legenda DIREITA (Backspace, Enter, Shift dir, Espaço, acentos) ----
    private JPanel buildLegendRight() {
        // Seta aponta para a esquerda ( ← ) porque a tecla está à esquerda da legenda
        String[][] items = {
                {"APAGAR \u232b", "Apaga o \u00faltimo caractere"},
                {"ENTER \u21b5",  "Confirmar / avan\u00e7ar linha"},
                {"SHIFT \u21e7",  "Segure + outra tecla\npara mai\u00fascula/s\u00edmbolo"},
                {"\u00b4  /  `",  "\u00b4 = agudo (\u00e1\u00e9\u00ed\u00f3\u00fa)\n` = crase (\u00e0)"},
                {"~  /  ^",       "~ = til (\u00e3\u00f5)\nShift+^ = circunflexo (\u00e2\u00ea\u00f4)"},
                {"Espa\u00e7o",   "Barra longa \u2014 linha de baixo"},
        };
        return buildLegendPanel(items, false);
    }

    /**
     * Monta um painel de legenda vertical.
     * leftSide=true  → seta "→" à direita de cada item (tecla está à direita)
     * leftSide=false → seta "←" à esquerda de cada item (tecla está à esquerda)
     */
    private JPanel buildLegendPanel(String[][] items, boolean leftSide) {
        Color blue      = new Color(37,  99, 235);   // azul padrão do projeto
        Color blueLight = new Color(219, 234, 254);  // fundo azul clarinho
        Color blueDark  = new Color(30,  58, 138);   // texto escuro
        Color textCol   = new Color(30,  58, 138);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(190, 0));

        String arrow = leftSide ? "\u2192" : "\u2190";   // → ou ←

        for (String[] item : items) {
            String keyName = item[0];
            String desc    = item[1];

            JPanel row = new JPanel();
            row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
            row.setOpaque(true);
            row.setBackground(blueLight);
            row.setAlignmentX(Component.CENTER_ALIGNMENT);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

            // Borda azul: esquerda se legenda esquerda, direita se legenda direita
            javax.swing.border.Border accent = leftSide
                    ? BorderFactory.createMatteBorder(0, 0, 0, 3, blue)
                    : BorderFactory.createMatteBorder(0, 3, 0, 0, blue);
            row.setBorder(BorderFactory.createCompoundBorder(
                    accent,
                    BorderFactory.createEmptyBorder(5, 8, 5, 8)));

            // Linha superior: [nome] e seta
            JPanel topLine = new JPanel(
                    leftSide ? new BorderLayout() : new BorderLayout());
            topLine.setOpaque(false);

            JLabel keyLbl = new JLabel(keyName);
            keyLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
            keyLbl.setForeground(blue);

            JLabel arrowLbl = new JLabel(" " + arrow);
            arrowLbl.setFont(new Font("SansSerif", Font.BOLD, 14));
            arrowLbl.setForeground(blue);

            if (leftSide) {
                // nome à esquerda, seta à direita
                topLine.add(keyLbl,   BorderLayout.CENTER);
                topLine.add(arrowLbl, BorderLayout.EAST);
            } else {
                // seta à esquerda, nome à direita
                topLine.add(arrowLbl, BorderLayout.WEST);
                topLine.add(keyLbl,   BorderLayout.CENTER);
            }

            // Descrição (pode ter \n)
            String descHtml = "<html><span style='color:#1e3a8a;font-size:10px'>"
                    + desc.replace("\n", "<br>") + "</span></html>";
            JLabel descLbl = new JLabel(descHtml);
            descLbl.setFont(new Font("SansSerif", Font.PLAIN, 10));
            descLbl.setForeground(textCol);

            topLine.setAlignmentX(Component.LEFT_ALIGNMENT);
            descLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

            row.add(topLine);
            row.add(descLbl);
            row.setAlignmentX(Component.LEFT_ALIGNMENT);

            panel.add(row);
            panel.add(Box.createVerticalStrut(5));
        }
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    /**
     * Desenha o teclado ABNT2 estático.
     * Teclas com legenda têm fundo azul claro; demais ficam cinza claro.
     */
    private void drawAnnotatedKeyboard(Graphics2D g2, int W, int H) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Teclas que têm legenda e devem ter fundo azul claro
        java.util.Set<String> highlighted = new java.util.HashSet<>(java.util.Arrays.asList(
                "Tab", "\u232b", "Caps", "\u21b5", "\u21e7", "Ctrl", "Alt",
                "\u00b4", "~"   // acentos
        ));

        Color bgKey   = new Color(240, 242, 248);          // cinza claro normal
        Color bgHigh  = new Color(219, 234, 254);          // azul claro para teclas com legenda
        Color bgSpace = new Color(219, 234, 254);          // espaço também azul claro
        Color border  = new Color(160, 175, 200);
        Color shadow  = new Color(0, 0, 0, 18);
        Color fgNorm  = new Color(30,  30,  30);
        Color fgSpec  = new Color(30,  58, 138);            // azul projeto para especiais
        Color fgShift = new Color(80,  80,  80);

        int   pad    = 6;
        float totalU = 15f;
        float unit   = (W - pad * 2) / totalU;
        float rowH   = (H - pad * 2 - 4 * 3) / 5f;
        float gap    = 2f;
        int   arc    = 5;

        Font fKey  = new Font("SansSerif", Font.BOLD,  Math.max(8,  (int)(unit * 0.28f)));
        Font fSpec = new Font("SansSerif", Font.BOLD,  Math.max(7,  (int)(unit * 0.22f)));
        Font fTiny = new Font("SansSerif", Font.PLAIN, Math.max(5,  (int)(unit * 0.17f)));

        float U = 1f;
        Object[][] keys = {
                // linha 0
                {"'","\"",0f,0f,U,U,false},{"1","!",1f,0f,U,U,false},{"2","@",2f,0f,U,U,false},
                {"3","#",3f,0f,U,U,false},{"4","$",4f,0f,U,U,false},{"5","%",5f,0f,U,U,false},
                {"6","\u00a8",6f,0f,U,U,false},{"7","&",7f,0f,U,U,false},{"8","*",8f,0f,U,U,false},
                {"9","(",9f,0f,U,U,false},{"0",")",10f,0f,U,U,false},{"-","_",11f,0f,U,U,false},
                {"=","+",12f,0f,U,U,false},{"\u232b","",13f,0f,2f,U,true},
                // linha 1
                {"Tab","",0f,1f,1.5f,U,true},{"Q","",1.5f,1f,U,U,false},{"W","",2.5f,1f,U,U,false},
                {"E","",3.5f,1f,U,U,false},{"R","",4.5f,1f,U,U,false},{"T","",5.5f,1f,U,U,false},
                {"Y","",6.5f,1f,U,U,false},{"U","",7.5f,1f,U,U,false},{"I","",8.5f,1f,U,U,false},
                {"O","",9.5f,1f,U,U,false},{"P","",10.5f,1f,U,U,false},{"\u00b4","`",11.5f,1f,U,U,false},
                {"[","{",12.5f,1f,U,U,false},{"\u21b5","",13.5f,1f,1.5f,2f,true},
                // linha 2
                {"Caps","",0f,2f,1.8f,U,true},{"A","",1.8f,2f,U,U,false},{"S","",2.8f,2f,U,U,false},
                {"D","",3.8f,2f,U,U,false},{"F","",4.8f,2f,U,U,false},{"G","",5.8f,2f,U,U,false},
                {"H","",6.8f,2f,U,U,false},{"J","",7.8f,2f,U,U,false},{"K","",8.8f,2f,U,U,false},
                {"L","",9.8f,2f,U,U,false},{"\u00c7","",10.8f,2f,U,U,false},{"~","^",11.8f,2f,U,U,false},
                {"]","}",12.8f,2f,U,U,false},
                // linha 3
                {"\u21e7","",0f,3f,1.3f,U,true},{"|","\\",1.3f,3f,U,U,false},{"Z","",2.3f,3f,U,U,false},
                {"X","",3.3f,3f,U,U,false},{"C","",4.3f,3f,U,U,false},{"V","",5.3f,3f,U,U,false},
                {"B","",6.3f,3f,U,U,false},{"N","",7.3f,3f,U,U,false},{"M","",8.3f,3f,U,U,false},
                {",","<",9.3f,3f,U,U,false},{".",">",10.3f,3f,U,U,false},{";",":",11.3f,3f,U,U,false},
                {"/","?",12.3f,3f,U,U,false},{"\u21e7","",13.3f,3f,1.7f,U,true},
                // linha 4
                {"Ctrl","",0f,4f,1.3f,U,true},{"Win","",1.3f,4f,1.2f,U,true},{"Alt","",2.5f,4f,1.2f,U,true},
                {"",""   ,3.7f,4f,6.3f,U,false},
                {"AltGr","",10.0f,4f,1.2f,U,true},{"Win","",11.2f,4f,1.0f,U,true},
                {"Menu","",12.2f,4f,0.9f,U,true},{"Ctrl","",13.1f,4f,0.9f,U,true},
        };

        for (Object[] k : keys) {
            String  lbl   = (String)  k[0];
            String  shift = (String)  k[1];
            float   kx    = (float)   k[2];
            float   ky    = (float)   k[3];
            float   kw    = (float)   k[4];
            float   kh    = (float)   k[5];
            boolean spec  = (boolean) k[6];
            boolean space = !spec && lbl.isEmpty();
            boolean isHL  = highlighted.contains(lbl) || space;

            float px = pad + kx * unit + gap;
            float py = pad + ky * (rowH + 3) + gap;
            float pw = kw * unit - gap * 2;
            float ph = kh * rowH + (kh > 1 ? (kh - 1) * 3 : 0) - gap * 2;

            // Sombra
            g2.setColor(shadow);
            g2.fillRoundRect((int)px+1,(int)py+2,(int)pw,(int)ph,arc,arc);

            // Fundo: azul claro para teclas com legenda, cinza para as demais
            Color bg = isHL ? bgHigh : bgKey;
            g2.setColor(bg);
            g2.fillRoundRect((int)px,(int)py,(int)pw,(int)ph,arc,arc);
            g2.setColor(isHL ? new Color(147,197,253) : border);
            g2.drawRoundRect((int)px,(int)py,(int)pw,(int)ph,arc,arc);

            // Texto
            if (space) {
                g2.setFont(fSpec); g2.setColor(fgSpec);
                FontMetrics fm = g2.getFontMetrics();
                String t = "ESPA\u00c7O";
                g2.drawString(t,
                        (int)px+((int)pw-fm.stringWidth(t))/2,
                        (int)py+((int)ph-fm.getHeight())/2+fm.getAscent());
            } else if (spec) {
                g2.setFont(fSpec); g2.setColor(fgSpec);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(lbl,
                        (int)px+((int)pw-fm.stringWidth(lbl))/2,
                        (int)py+((int)ph-fm.getHeight())/2+fm.getAscent());
            } else {
                // Label centrado
                g2.setFont(fKey); g2.setColor(isHL ? fgSpec : fgNorm);
                FontMetrics fmK = g2.getFontMetrics();
                g2.drawString(lbl,
                        (int)px+((int)pw-fmK.stringWidth(lbl))/2,
                        (int)py+((int)ph-fmK.getHeight())/2+fmK.getAscent());
                // shift-label canto superior esquerdo
                if (!shift.isEmpty()) {
                    g2.setFont(fTiny); g2.setColor(fgShift);
                    g2.drawString(shift, (int)px+2,
                            (int)py+g2.getFontMetrics().getAscent()+1);
                }
            }
        }
        g2.dispose();
    }

    // ================================================================
    //  Seção "Escolha onde começar" — níveis clicáveis
    // ================================================================
    private JPanel buildJourneySection() {
        RoundedPanel journey = new RoundedPanel(30, Color.WHITE);
        journey.setLayout(new BorderLayout(0, 8));
        journey.setBorder(BorderFactory.createEmptyBorder(12, 14, 14, 14));
        journey.setMaximumSize(new Dimension(430, 270));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel title = label("Escolha onde começar", FONT_INFO, COLOR_PRIMARY, SwingConstants.LEFT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel hint = label("Clique em um nível para começar.", FONT_SMALL, COLOR_TEXT, SwingConstants.LEFT);
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(hint);
        journey.add(header, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(MODULE_LABELS.length, 1, 0, 5));
        grid.setOpaque(false);

        for (int i = 0; i < MODULE_LABELS.length; i++) {
            final int level = i + 1;
            JButton levelButton = createLevelButton(level);
            levelButton.addActionListener(e -> startGame(level));
            grid.add(levelButton);
        }

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        JScrollBar verticalBar = scroll.getVerticalScrollBar();
        verticalBar.setUnitIncrement(12);
        verticalBar.setPreferredSize(new Dimension(6, 0));
        verticalBar.setOpaque(false);
        verticalBar.setBorder(null);
        verticalBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() {
                thumbColor = new Color(147, 197, 253, 120);
                trackColor = new Color(0, 0, 0, 0);
            }

            @Override protected JButton createDecreaseButton(int orientation) {
                return invisibleScrollButton();
            }

            @Override protected JButton createIncreaseButton(int orientation) {
                return invisibleScrollButton();
            }

            @Override protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                // Trilho invisível para manter o card limpo.
            }

            @Override protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) return;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(thumbColor);
                g2.fillRoundRect(
                        thumbBounds.x + 1, thumbBounds.y + 2,
                        thumbBounds.width - 2, thumbBounds.height - 4,
                        6, 6);
                g2.dispose();
            }

            private JButton invisibleScrollButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                button.setOpaque(false);
                button.setContentAreaFilled(false);
                button.setBorderPainted(false);
                return button;
            }
        });

        journey.add(scroll, BorderLayout.CENTER);
        journey.setAlignmentX(Component.LEFT_ALIGNMENT);
        return journey;
    }

    private JButton createLevelButton(int level) {
        int index = level - 1;
        Color accent = level == 1 ? COLOR_SUCCESS : COLOR_PRIMARY;
        String nextMark = level < MODULE_LABELS.length ? " &nbsp; &rarr;" : "";
        String text = "<html><body style='width:330px'>"
                + "<b>Nível " + level + "</b> &nbsp; " + MODULE_ICONS[index]
                + " &nbsp; " + MODULE_LABELS[index]
                + "<br><span style='font-size:10px;color:#4b5563'>"
                + MODULE_DESCRIPTIONS[index] + nextMark
                + "</span></body></html>";

        JButton button = new JButton(text);
        button.setFont(new Font("Poppins", Font.PLAIN, 15));
        button.setForeground(COLOR_TEXT);
        button.setBackground(new Color(239, 246, 255));
        button.setOpaque(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setContentAreaFilled(false);
        button.setBorderPainted(true);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(0, 38));
        button.setToolTipText("Começar no nível " + level + ": " + MODULE_LABELS[index]);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 6, 0, 1, accent),
                BorderFactory.createEmptyBorder(2, 12, 2, 10)));
        button.getModel().addChangeListener(e -> {
            ButtonModel model = button.getModel();
            button.setOpaque(model.isPressed() || model.isRollover());
            button.setContentAreaFilled(model.isPressed() || model.isRollover());
            button.setBackground(model.isPressed()
                    ? new Color(219, 234, 254)
                    : model.isRollover()
                    ? new Color(239, 246, 255)
                    : new Color(239, 246, 255));
        });
        return button;
    }

    // ================================================================
    //  Linha de ranking
    // ================================================================
    private JPanel rankLine(String medal, String name, String score) {
        JPanel line = new JPanel(new GridLayout(1, 2, 4, 0));
        line.setOpaque(false);
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        line.add(label(medal + " " + name,  FONT_SMALL, COLOR_TEXT,      SwingConstants.LEFT));
        line.add(label(score, FONT_SMALL, COLOR_SECONDARY, SwingConstants.CENTER));
        return line;
    }

    // ================================================================
    //  Footer
    // ================================================================
    private JPanel createFooter() {
        GradientPanel footer = new GradientPanel(COLOR_ACCENT, COLOR_ACCENT, true);
        footer.setLayout(new BorderLayout());
        footer.setPreferredSize(new Dimension(0, 60));
        JLabel footerText = label("Aqui todo mundo aprende! Respeite seu tempo e divirta-se!",
                FONT_BODY, Color.WHITE, SwingConstants.CENTER);
        footerText.setFont(new Font(Font.DIALOG, Font.PLAIN, 18));
        footer.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));
        footer.add(footerText, BorderLayout.CENTER);
        return footer;
    }

    // ================================================================
    //  Iniciar jogo
    // ================================================================
    private void startGame(int level) {
        String name = nameField.getText().trim();
        if (name.isEmpty() || NAME_PLACEHOLDER.equals(name)) {
            nameField.setBorder(BorderFactory.createLineBorder(COLOR_DANGER, 2));
            nameField.requestFocusInWindow();
            return;
        }
        game.startGame(new Player(name), level);
    }

    private void showNamePlaceholder() {
        if (nameField.getText().trim().isEmpty()) {
            nameField.setText(NAME_PLACEHOLDER);
            nameField.setFont(FONT_PLACEHOLDER);
            nameField.setForeground(COLOR_PLACEHOLDER);
        }
    }

    private void clearNamePlaceholder() {
        if (NAME_PLACEHOLDER.equals(nameField.getText())) {
            nameField.setText("");
            nameField.setFont(FONT_INPUT);
            nameField.setForeground(COLOR_TEXT);
        }
    }
}
