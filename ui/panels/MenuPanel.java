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

    // Nomes e ícones dos 5 módulos
    private static final String[] MODULE_LABELS = {
            "Minúsculas", "Maiúsculas", "Números", "Acentos", "Pontuação"
    };
    private static final String[] MODULE_ICONS = {
            "abc", "ABC", "123", "áéí", ".,!"
    };

    public MenuPanel(TypingGame game) {
        super(game);
    }

    @Override
    protected void initialize() {
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel content = new JPanel(new BorderLayout()) {
            private final Image bgImage = new ImageIcon("ui/assets/background-image.png").getImage();
            @Override protected void paintComponent(Graphics g) {
                g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
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

        // Título clicável — troféu + texto na mesma linha
        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        titleRow.setOpaque(false);
        titleRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleRow.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 30));
        titleRow.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel trophyLbl = new JLabel("🏆");
        trophyLbl.setFont(new Font(Font.DIALOG, Font.PLAIN, 18));
        trophyLbl.setForeground(COLOR_TEXT);

        JLabel rankTextLbl = new JLabel("<html><u>Ranking</u></html>");
        rankTextLbl.setFont(FONT_INFO);
        rankTextLbl.setForeground(COLOR_TEXT);

        titleRow.add(trophyLbl);
        titleRow.add(rankTextLbl);

        MouseAdapter rankClick = new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { showRankingDialog(); }
        };
        titleRow.addMouseListener(rankClick);
        trophyLbl.addMouseListener(rankClick);
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
            String[] medals = {"🥇", "🥈", "🥉"};
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
                "🏆  Ranking", true);
        dialog.setSize(480, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        JLabel title = new JLabel("🏆  Ranking de Jogadores");
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
            String[] medals = {"🥇","🥈","🥉","4º","5º","6º","7º","8º","9º","10º"};
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
        left.add(Box.createVerticalStrut(10));
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
        nameField.addActionListener(e -> startGame(1));
        cc.gridy = 1; cc.insets = new Insets(5, 60, 20, 60);
        card.add(nameField, cc);

        JButton btn = createModernButton("INICIAR", COLOR_SUCCESS);
        btn.setFont(FONT_BUTTON);
        btn.setPreferredSize(new Dimension(320, 65));
        btn.addActionListener(e -> startGame(1));
        cc.gridy = 2; cc.insets = new Insets(0, 60, 25, 60);
        card.add(btn, cc);

        ImageIcon illustrationIcon = new ImageIcon("ui/assets/image-menupanel.png");
        int maxW = 570, maxH = 380, finalW = maxW, finalH = maxH;
        if (illustrationIcon.getIconWidth() > 0) {
            double scale = Math.min((double)maxW / illustrationIcon.getIconWidth(),
                    (double)maxH / illustrationIcon.getIconHeight());
            finalW = (int)(illustrationIcon.getIconWidth()  * scale);
            finalH = (int)(illustrationIcon.getIconHeight() * scale);
        }
        Image scaledImg = illustrationIcon.getImage().getScaledInstance(finalW, finalH, Image.SCALE_SMOOTH);
        cc.gridy = 3; cc.weighty = 1.0;
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
        JLabel kbTitle = label("⌨️  Conheça o Teclado", FONT_INFO, COLOR_PRIMARY, SwingConstants.LEFT);
        kbTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel kbDesc = label("<html><body style='width:160px'>Veja onde cada tecla fica antes de começar!</body></html>",
                FONT_SMALL, COLOR_TEXT, SwingConstants.LEFT);
        kbDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel kbLink = new JLabel("<html><u>🔗 Ver teclado</u></html>");
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
    //  Dialog interno: teclado ABNT2 com legendas
    // ================================================================
    private void showKeyboardDialog() {
        JDialog dlg = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "", false);   // sem título na barra
        dlg.setUndecorated(false);
        dlg.getRootPane().putClientProperty("JRootPane.titleBarBackground", new Color(37, 99, 235));
        dlg.setSize(1200, 520);
        dlg.setLocationRelativeTo(this);
        dlg.setResizable(true);

        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBackground(new Color(245, 247, 251));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        // Cabeçalho com título e dica do Shift
        JPanel header = new JPanel(new BorderLayout(0, 4));
        header.setOpaque(false);

        JLabel title = new JLabel(
                "⌨️  Conheça o Teclado — posição das teclas especiais",
                SwingConstants.CENTER);
        title.setFont(FONT_INFO);
        title.setForeground(COLOR_PRIMARY);

        // Explicação do Shift
        JLabel shiftHint = new JLabel(
                "<html><center>"
                        + "<b style='color:#1e3a8a'>Como usar o SHIFT:</b> "
                        + "Pressione e <b>segure</b> a tecla "
                        + "<b style='color:#b45309'>SHIFT</b> (canto inferior esquerdo ou direito do teclado) "
                        + "e, enquanto a segura, pressione a outra tecla. "
                        + "Use para letras maiúsculas e símbolos como <b>! @ # $ % & *</b>"
                        + "</center></html>",
                SwingConstants.CENTER);
        shiftHint.setFont(FONT_SMALL);
        shiftHint.setForeground(new Color(60, 60, 60));
        shiftHint.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(209, 213, 219), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        shiftHint.setOpaque(true);
        shiftHint.setBackground(new Color(255, 251, 235));

        header.add(title, BorderLayout.NORTH);
        header.add(shiftHint, BorderLayout.CENTER);
        panel.add(header, BorderLayout.NORTH);

        // Teclado desenhado
        JPanel kbView = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawAnnotatedKeyboard((Graphics2D) g.create(), getWidth(), getHeight());
            }
        };
        kbView.setBackground(new Color(245, 247, 251));
        panel.add(kbView, BorderLayout.CENTER);

        dlg.add(panel);
        dlg.setVisible(true);
    }

    /**
     * Desenha um teclado ABNT2 com anotações nas teclas especiais
     * (Shift, Tab, Enter, Caps Lock, Backspace, Espaço).
     */
    private void drawAnnotatedKeyboard(Graphics2D g2, int W, int H) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // ---- Cores ----
        Color bgKey     = new Color(240, 240, 240);
        Color bgSpecial = new Color(200, 210, 230);   // azul claro para especiais
        Color bgSpace   = new Color(220, 235, 255);
        Color borderCol = new Color(160, 160, 160);
        Color shadowCol = new Color(0, 0, 0, 25);
        Color fgNormal  = new Color(40, 40, 40);
        Color fgSpecial = new Color(20, 50, 120);
        Color arrowCol  = new Color(220, 80, 30);     // laranja para setas/legendas

        // Totais de unidades: 15 cols × 5 linhas
        float totalU = 15f;
        int   pad    = 14;
        float unit   = (W - pad * 2) / totalU;
        float rowH   = (H - pad * 2 - 4 * 3) / 5f;
        float gap    = 2f;
        int   arc    = 5;

        Font fKey     = new Font("SansSerif", Font.BOLD,  Math.max(8,  (int)(unit * 0.28f)));
        Font fSpecial = new Font("SansSerif", Font.BOLD,  Math.max(7,  (int)(unit * 0.22f)));
        Font fTiny    = new Font("SansSerif", Font.PLAIN, Math.max(6,  (int)(unit * 0.18f)));
        Font fAnnot   = new Font("SansSerif", Font.BOLD,  Math.max(9,  (int)(unit * 0.24f)));

        // Definição inline das teclas (sem estado, só para desenho estático)
        // [label, shiftLabel, x, y, w, h, isSpecial]
        // Usamos a mesma posição do KeyboardPanel
        float U = 1f;
        Object[][] keys = {
                // linha 0
                {"'","\"",0f,0f,U,U,false},{"1","!",1f,0f,U,U,false},{"2","@",2f,0f,U,U,false},
            {"3","#",3f,0f,U,U,false},{"4","$",4f,0f,U,U,false},{"5","%",5f,0f,U,U,false},
            {"6","¨",6f,0f,U,U,false},{"7","&",7f,0f,U,U,false},{"8","*",8f,0f,U,U,false},
            {"9","(",9f,0f,U,U,false},{"0",")",10f,0f,U,U,false},{"-","_",11f,0f,U,U,false},
            {"=","+",12f,0f,U,U,false},{"⌫","",13f,0f,2f,U,true},
            // linha 1
            {"Tab","",0f,1f,1.5f,U,true},{"Q","",1.5f,1f,U,U,false},{"W","",2.5f,1f,U,U,false},
            {"E","",3.5f,1f,U,U,false},{"R","",4.5f,1f,U,U,false},{"T","",5.5f,1f,U,U,false},
            {"Y","",6.5f,1f,U,U,false},{"U","",7.5f,1f,U,U,false},{"I","",8.5f,1f,U,U,false},
            {"O","",9.5f,1f,U,U,false},{"P","",10.5f,1f,U,U,false},{"´","`",11.5f,1f,U,U,false},
            {"[","{",12.5f,1f,U,U,false},{"↵","",13.5f,1f,1.5f,2f,true},
            // linha 2
            {"Caps","",0f,2f,1.8f,U,true},{"A","",1.8f,2f,U,U,false},{"S","",2.8f,2f,U,U,false},
            {"D","",3.8f,2f,U,U,false},{"F","",4.8f,2f,U,U,false},{"G","",5.8f,2f,U,U,false},
            {"H","",6.8f,2f,U,U,false},{"J","",7.8f,2f,U,U,false},{"K","",8.8f,2f,U,U,false},
            {"L","",9.8f,2f,U,U,false},{"Ç","",10.8f,2f,U,U,false},{"~","^",11.8f,2f,U,U,false},
            {"]","}",12.8f,2f,U,U,false},
            // linha 3
            {"⇧","",0f,3f,1.3f,U,true},{"|","\\",1.3f,3f,U,U,false},{"Z","",2.3f,3f,U,U,false},
            {"X","",3.3f,3f,U,U,false},{"C","",4.3f,3f,U,U,false},{"V","",5.3f,3f,U,U,false},
            {"B","",6.3f,3f,U,U,false},{"N","",7.3f,3f,U,U,false},{"M","",8.3f,3f,U,U,false},
            {",","<",9.3f,3f,U,U,false},{".",">",10.3f,3f,U,U,false},{";",":",11.3f,3f,U,U,false},
            {"/","?",12.3f,3f,U,U,false},{"⇧","",13.3f,3f,1.7f,U,true},
            // linha 4
            {"Ctrl","",0f,4f,1.3f,U,true},{"Win","",1.3f,4f,1.2f,U,true},{"Alt","",2.5f,4f,1.2f,U,true},
            {"",""  ,3.7f,4f,6.3f,U,false},
            {"AltGr","",10.0f,4f,1.2f,U,true},{"Win","",11.2f,4f,1.0f,U,true},
            {"Menu","",12.2f,4f,0.9f,U,true},{"Ctrl","",13.1f,4f,0.9f,U,true},
        };

        // Nomes legíveis para as teclas especiais com legenda
        java.util.Map<String,String> specialNames = new java.util.LinkedHashMap<>();
        specialNames.put("Tab",   "TAB Mudar campo");
        specialNames.put("⌫",    "APAGAR Caractere");
        specialNames.put("Caps",  "CAPS Maiúsculas");
        specialNames.put("↵",    "ENTER Confirmar");
        specialNames.put("⇧",    "SHIFT Maiúscula/Símbolo");
        specialNames.put("Ctrl",  "CTRL");
        specialNames.put("Alt",   "ALT");
        specialNames.put("AltGr", "AltGr");
        specialNames.put("Win",   "WIN");
        specialNames.put("Menu",  "MENU");

        for (Object[] k : keys) {
            String lbl   = (String)  k[0];
            String shift = (String)  k[1];
            float  kx    = (float)   k[2];
            float  ky    = (float)   k[3];
            float  kw    = (float)   k[4];
            float  kh    = (float)   k[5];
            boolean spec = (boolean) k[6];

            float px = pad + kx * unit + gap;
            float py = pad + ky * (rowH + 3) + gap;
            float pw = kw * unit - gap * 2;
            float ph = kh * rowH + (kh > 1 ? (kh - 1) * 3 : 0) - gap * 2;

            boolean isEspaco = !spec && lbl.isEmpty();

            // Sombra
            g2.setColor(shadowCol);
            g2.fillRoundRect((int)px+1,(int)py+2,(int)pw,(int)ph,arc,arc);

            // Fundo
            Color bg = spec ? bgSpecial : (isEspaco ? bgSpace : bgKey);
            g2.setColor(bg);
            g2.fillRoundRect((int)px,(int)py,(int)pw,(int)ph,arc,arc);
            g2.setColor(borderCol);
            g2.drawRoundRect((int)px,(int)py,(int)pw,(int)ph,arc,arc);

            // Texto
            if (spec) {
                // Teclas especiais: label original pequeno em cima, nome legível embaixo
                g2.setFont(fSpecial);
                g2.setColor(fgSpecial);

                String name = specialNames.getOrDefault(lbl, lbl);
                // Para ⇧/Caps/Tab etc., escreve o label em cima e o nome embaixo
                if (lbl.equals("⇧") || lbl.equals("⌫") || lbl.equals("Tab") ||
                    lbl.equals("Caps") || lbl.equals("↵")) {
                    // símbolo pequeno no canto superior
                    g2.setFont(fTiny);
                    g2.setColor(fgSpecial);
                    FontMetrics fmt = g2.getFontMetrics();
                    g2.drawString(lbl, (int)px+3, (int)py+fmt.getAscent()+2);
                    // nome legível centrado
                    g2.setFont(fAnnot);
                    g2.setColor(arrowCol);
                    String[] lines2 = name.split("\n");
                    int lineH2 = g2.getFontMetrics().getHeight();
                    int startY = (int)(py + ph/2 - (lines2.length * lineH2)/2f + g2.getFontMetrics().getAscent());
                    for (String ln : lines2) {
                        FontMetrics fm2 = g2.getFontMetrics();
                        int tx2 = (int)px + ((int)pw - fm2.stringWidth(ln)) / 2;
                        g2.drawString(ln, tx2, startY);
                        startY += lineH2;
                    }
                } else {
                    // Ctrl, Alt, Win, Menu, AltGr: apenas o label centrado
                    g2.setFont(fSpecial);
                    g2.setColor(fgSpecial);
                    FontMetrics fm3 = g2.getFontMetrics();
                    int tx3 = (int)px + ((int)pw - fm3.stringWidth(lbl))/2;
                    int ty3 = (int)py + ((int)ph - fm3.getHeight())/2 + fm3.getAscent();
                    g2.drawString(lbl, tx3, ty3);
                }
            } else if (isEspaco) {
                // Barra de espaço
                g2.setFont(fAnnot);
                g2.setColor(arrowCol);
                String spaceTxt = "ESPAÇO";
                FontMetrics fmSp = g2.getFontMetrics();
                g2.drawString(spaceTxt,
                    (int)px + ((int)pw - fmSp.stringWidth(spaceTxt))/2,
                    (int)py + ((int)ph - fmSp.getHeight())/2 + fmSp.getAscent());
            } else {
                // Tecla normal: label centrado
                g2.setFont(fKey);
                g2.setColor(fgNormal);
                FontMetrics fmK = g2.getFontMetrics();
                int txK = (int)px + ((int)pw - fmK.stringWidth(lbl))/2;
                int tyK = (int)py + ((int)ph - fmK.getHeight())/2 + fmK.getAscent();
                g2.drawString(lbl, txK, tyK);
                // shift label canto superior esquerdo
                if (!shift.isEmpty()) {
                    g2.setFont(fTiny);
                    g2.setColor(new Color(100,100,100));
                    g2.drawString(shift, (int)px+2, (int)py+g2.getFontMetrics().getAscent()+1);
                }
            }
        }
        g2.dispose();
    }

    // ================================================================
    //  Seção "Escolha onde começar" — módulos clicáveis, sem cadeados
    // ================================================================
    private JPanel buildJourneySection() {
        RoundedPanel journey = new RoundedPanel(30, COLOR_ACCENT);
        journey.setLayout(new BorderLayout(0, 8));
        journey.setBorder(BorderFactory.createEmptyBorder(14, 16, 16, 16));
        journey.setMaximumSize(new Dimension(420, 155));
        journey.add(label("Escolha onde começar", FONT_INFO, Color.WHITE, SwingConstants.CENTER), BorderLayout.NORTH);

        // Grid com os 5 módulos clicáveis
        JPanel grid = new JPanel(new GridLayout(1, 5, 5, 0));
        grid.setOpaque(false);

        for (int i = 0; i < MODULE_LABELS.length; i++) {
            final int level = i + 1;
            final String lbl = MODULE_LABELS[i];
            final String icon = MODULE_ICONS[i];

            // Cada módulo é um RoundedPanel com fundo semi-transparente
            RoundedPanel cell = new RoundedPanel(12, new Color(255, 255, 255, 45));
            cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));
            cell.setBorder(BorderFactory.createEmptyBorder(8, 4, 8, 4));
            cell.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JLabel iconLbl = new JLabel(icon, SwingConstants.CENTER);
            iconLbl.setFont(new Font(Font.MONOSPACED, Font.BOLD, 13));
            iconLbl.setForeground(Color.WHITE);
            iconLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel nameLbl = new JLabel("<html><center>" + lbl + "</center></html>", SwingConstants.CENTER);
            nameLbl.setFont(new Font("Poppins", Font.PLAIN, 11));
            nameLbl.setForeground(Color.WHITE);
            nameLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

            cell.add(Box.createVerticalGlue());
            cell.add(iconLbl);
            cell.add(Box.createVerticalStrut(4));
            cell.add(nameLbl);
            cell.add(Box.createVerticalGlue());

            cell.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) { startGame(level); }
                @Override public void mouseEntered(MouseEvent e) {
                    cell.setBackground(new Color(255, 255, 255, 80));
                    cell.repaint();
                }
                @Override public void mouseExited(MouseEvent e) {
                    cell.setBackground(new Color(255, 255, 255, 45));
                    cell.repaint();
                }
            });

            grid.add(cell);
        }

        journey.add(grid, BorderLayout.CENTER);
        journey.setAlignmentX(Component.LEFT_ALIGNMENT);
        return journey;
    }

    // ================================================================
    //  Linha de ranking
    // ================================================================
    private JPanel rankLine(String medal, String name, String score) {
        JPanel line = new JPanel(new GridLayout(1, 3, 4, 0));
        line.setOpaque(false);
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        line.add(label(medal, FONT_SMALL, COLOR_PRIMARY,   SwingConstants.CENTER));
        line.add(label(name,  FONT_SMALL, COLOR_TEXT,      SwingConstants.LEFT));
        line.add(label(score, FONT_SMALL, COLOR_SECONDARY, SwingConstants.CENTER));
        return line;
    }

    // ================================================================
    //  Footer
    // ================================================================
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

    // ================================================================
    //  Iniciar jogo
    // ================================================================
    private void startGame(int level) {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            nameField.setBorder(BorderFactory.createLineBorder(COLOR_DANGER, 2));
            nameField.requestFocusInWindow();
            return;
        }
        game.startGame(new Player(name), level);
    }
}