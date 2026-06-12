package ui.panels;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Painel que desenha um teclado ABNT2 completo.
 * Chame highlightForText(String) para pintar de verde
 * todas as teclas necessárias para digitar aquela frase.
 */
public class KeyboardPanel extends JPanel {

    // ----------------------------------------------------------------
    //  Cores
    // ----------------------------------------------------------------
    private static final Color COL_KEY_BG        = new Color(240, 240, 240);
    private static final Color COL_KEY_BORDER     = new Color(180, 180, 180);
    private static final Color COL_KEY_FG         = new Color( 40,  40,  40);
    private static final Color COL_HIGHLIGHT_BG   = new Color(120, 210, 120);   // verde
    private static final Color COL_HIGHLIGHT_FG   = new Color( 20,  80,  20);
    private static final Color COL_SPECIAL_BG     = new Color(210, 210, 210);   // cinza especial
    private static final Color COL_SHIFT_BG       = new Color(255, 220, 100);   // amarelo = Shift necessário
    private static final Color COL_SHIFT_FG       = new Color( 90,  60,   0);
    private static final Color COL_CAPS_BG        = new Color(255, 180,  80);   // laranja = CapsLock
    private static final Color COL_CAPS_FG        = new Color( 90,  40,   0);

    // ----------------------------------------------------------------
    //  Estrutura de uma tecla
    // ----------------------------------------------------------------
    private static class Key {
        String label;        // texto principal exibido
        String shiftLabel;   // texto secundário (shift)
        float  x, y, w, h;  // posição/tamanho em unidades de célula
        boolean isSpecial;   // backspace, tab, caps, shift, enter, etc.

        // Estado de pintura (calculado por highlightForText)
        boolean highlightNormal; // precisa dessa tecla sem shift
        boolean highlightShift;  // precisa com shift
        boolean highlightCaps;   // precisa de capslock

        Key(String label, String shiftLabel, float x, float y, float w, float h, boolean isSpecial) {
            this.label = label; this.shiftLabel = shiftLabel;
            this.x = x; this.y = y; this.w = w; this.h = h;
            this.isSpecial = isSpecial;
        }
    }

    // ----------------------------------------------------------------
    //  Layout ABNT2 completo
    // ----------------------------------------------------------------
    private static final Key[] KEYS;

    static {
        // Cada "unidade" = KEY_UNIT px (calculado em paint).
        // Linhas: y=0 (números), y=1 (QWERTY), y=2 (ASDF), y=3 (ZXCV), y=4 (barra de espaço)
        float U = 1f; // alias legível
        KEYS = new Key[]{
                // ------- Linha 0: números -------
                new Key("'","\"",  0,0, U,U, false),
                new Key("1","!",   1,0, U,U, false),
                new Key("2","@",   2,0, U,U, false),
                new Key("3","#",   3,0, U,U, false),
                new Key("4","$",   4,0, U,U, false),
                new Key("5","%",   5,0, U,U, false),
                new Key("6","¨",   6,0, U,U, false),
                new Key("7","&",   7,0, U,U, false),
                new Key("8","*",   8,0, U,U, false),
                new Key("9","(",   9,0, U,U, false),
                new Key("0",")",  10,0, U,U, false),
                new Key("-","_",  11,0, U,U, false),
                new Key("=","+",  12,0, U,U, false),
                new Key("⌫","",   13,0, 2f,U, true),   // Backspace

                // ------- Linha 1: QWERTY -------
                new Key("Tab","",  0,1, 1.5f,U, true),
                new Key("Q","",   1.5f,1, U,U, false),
                new Key("W","",   2.5f,1, U,U, false),
                new Key("E","",   3.5f,1, U,U, false),
                new Key("R","",   4.5f,1, U,U, false),
                new Key("T","",   5.5f,1, U,U, false),
                new Key("Y","",   6.5f,1, U,U, false),
                new Key("U","",   7.5f,1, U,U, false),
                new Key("I","",   8.5f,1, U,U, false),
                new Key("O","",   9.5f,1, U,U, false),
                new Key("P","",  10.5f,1, U,U, false),
                new Key("´","`", 11.5f,1, U,U, false),
                new Key("[","{", 12.5f,1, U,U, false),
                new Key("↵","",  13.5f,1, 1.5f,2f, true),  // Enter — 2 linhas de altura

                // ------- Linha 2: ASDF -------
                new Key("Caps","",  0,2, 1.8f,U, true),
                new Key("A","",   1.8f,2, U,U, false),
                new Key("S","",   2.8f,2, U,U, false),
                new Key("D","",   3.8f,2, U,U, false),
                new Key("F","",   4.8f,2, U,U, false),
                new Key("G","",   5.8f,2, U,U, false),
                new Key("H","",   6.8f,2, U,U, false),
                new Key("J","",   7.8f,2, U,U, false),
                new Key("K","",   8.8f,2, U,U, false),
                new Key("L","",   9.8f,2, U,U, false),
                new Key("Ç","",  10.8f,2, U,U, false),
                new Key("~","^", 11.8f,2, U,U, false),
                new Key("]","}",  12.8f,2, U,U, false),

                // ------- Linha 3: ZXCV -------
                new Key("⇧","",   0,3, 1.3f,U, true),   // Shift esquerdo
                new Key("|","\\",  1.3f,3, U,U, false),
                new Key("Z","",   2.3f,3, U,U, false),
                new Key("X","",   3.3f,3, U,U, false),
                new Key("C","",   4.3f,3, U,U, false),
                new Key("V","",   5.3f,3, U,U, false),
                new Key("B","",   6.3f,3, U,U, false),
                new Key("N","",   7.3f,3, U,U, false),
                new Key("M","",   8.3f,3, U,U, false),
                new Key(",","<",  9.3f,3, U,U, false),
                new Key(".",">" , 10.3f,3, U,U, false),
                new Key(";",":",  11.3f,3, U,U, false),
                new Key("/","?",  12.3f,3, U,U, false),
                new Key("⇧","",  13.3f,3, 1.7f,U, true), // Shift direito

                // ------- Linha 4: espaço -------
                new Key("Ctrl","",  0,4, 1.3f,U, true),
                new Key("Win","",  1.3f,4, 1.2f,U, true),
                new Key("Alt","",  2.5f,4, 1.2f,U, true),
                new Key("",   "",  3.7f,4, 6.3f,U, false),  // Espaço
                new Key("AltGr","",10.0f,4, 1.2f,U, true),
                new Key("Win","",  11.2f,4,1.0f,U, true),
                new Key("Menu","",12.2f,4,0.9f,U, true),
                new Key("Ctrl","",13.1f,4,0.9f,U, true),
        };
    }

    // ----------------------------------------------------------------
    //  Mapeamento caractere → índice de tecla
    // ----------------------------------------------------------------
    // Preenchido no construtor
    private final java.util.Map<Character, Integer> charToKeyIndex = new java.util.HashMap<>();
    // Caracteres que precisam de Shift
    private final Set<Character> needsShift = new HashSet<>();
    // Caracteres que precisam de CapsLock (letras maiúsculas A-Z)
    private final Set<Character> needsCaps  = new HashSet<>();

    // Índices de teclas especiais de modificador
    private int idxShiftL = -1, idxShiftR = -1, idxCaps = -1;

    public KeyboardPanel() {
        setOpaque(false);
        setPreferredSize(new Dimension(0, 110));
        buildCharMap();
    }

    private void buildCharMap() {
        for (int i = 0; i < KEYS.length; i++) {
            Key k = KEYS[i];

            // Guarda índices dos modificadores
            if (k.isSpecial) {
                if (k.label.equals("⇧") && idxShiftL < 0) idxShiftL = i;
                else if (k.label.equals("⇧"))              idxShiftR = i;
                else if (k.label.equals("Caps"))           idxCaps   = i;
                continue;
            }

            // Tecla normal: label é o caractere sem shift
            if (!k.label.isEmpty()) {
                char c = k.label.charAt(0);
                charToKeyIndex.put(c, i);

                // As teclas de letras são definidas com maiúscula ("A","Q"...),
                // mas sem shift produzem minúscula. Mapeia os dois sentidos:
                // maiúscula → mesma tecla + needsShift
                // minúscula → mesma tecla (sem shift)
                if (c >= 'A' && c <= 'Z') {
                    char lower = Character.toLowerCase(c);
                    charToKeyIndex.put(lower, i);       // minúscula = tecla normal
                    charToKeyIndex.put(c, i);            // maiúscula = mesma tecla
                    needsShift.add(c);                   // maiúscula requer Shift
                } else if (c >= 'a' && c <= 'z') {
                    char upper = Character.toUpperCase(c);
                    charToKeyIndex.put(upper, i);
                    needsShift.add(upper);
                }
            }

            // shiftLabel = caractere com Shift pressionado
            if (!k.shiftLabel.isEmpty()) {
                char c = k.shiftLabel.charAt(0);
                charToKeyIndex.put(c, i);
                needsShift.add(c);
            }
        }

        // Espaço: tecla com label="" e largura > 4 na linha 4
        for (int i = 0; i < KEYS.length; i++) {
            if (KEYS[i].y == 4 && KEYS[i].w > 4) {
                charToKeyIndex.put(' ', i);
                break;
            }
        }
    }

    // ----------------------------------------------------------------
    //  API pública: pintar teclas para uma frase
    // ----------------------------------------------------------------
    public void highlightForText(String text) {
        // Limpa estado anterior
        for (Key k : KEYS) {
            k.highlightNormal = false;
            k.highlightShift  = false;
            k.highlightCaps   = false;
        }

        boolean hasCaps = false;

        for (char c : text.toCharArray()) {
            if (c == '\n' || c == '\r') continue;

            Integer idx = charToKeyIndex.get(c);
            if (idx == null) continue;

            Key k = KEYS[idx];

            if (needsShift.contains(c)) {
                k.highlightShift = true;
                // Marca os dois shifts
                if (idxShiftL >= 0) KEYS[idxShiftL].highlightShift = true;
                if (idxShiftR >= 0) KEYS[idxShiftR].highlightShift = true;
            } else {
                k.highlightNormal = true;
            }
        }

        // Se há maiúsculas — poderia ser CapsLock, mas deixamos Shift por padrão
        // (mais comum e mais simples para o usuário idoso)

        repaint();
    }

    // ----------------------------------------------------------------
    //  Renderização
    // ----------------------------------------------------------------
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,   RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Calcula unidade de célula para caber na largura disponível
        float totalUnits = 15f;  // largura total do teclado em unidades
        int   padding    = 6;
        float unit       = (getWidth() - padding * 2) / totalUnits;
        float keyH       = (getHeight() - padding * 2 - 4 * 3) / 5f; // 5 linhas, 3px gap
        float gap        = 2f;
        int   arc        = 6;

        Font fontNormal  = new Font("Poppins", Font.BOLD,  (int)(unit * 0.30f));
        Font fontSpecial = new Font("Poppins", Font.PLAIN, (int)(unit * 0.22f));
        Font fontShift   = new Font("Poppins", Font.BOLD,  (int)(unit * 0.20f));

        for (Key k : KEYS) {
            float px = padding + k.x * unit + gap;
            float py = padding + k.y * (keyH + 3) + gap;
            float pw = k.w * unit - gap * 2;
            float ph = k.h * keyH + (k.h > 1 ? (k.h - 1) * 3 : 0) - gap * 2;

            // Cor de fundo
            Color bg, fg;
            if (k.highlightShift) {
                bg = COL_SHIFT_BG; fg = COL_SHIFT_FG;
            } else if (k.highlightCaps) {
                bg = COL_CAPS_BG;  fg = COL_CAPS_FG;
            } else if (k.highlightNormal) {
                bg = COL_HIGHLIGHT_BG; fg = COL_HIGHLIGHT_FG;
            } else if (k.isSpecial) {
                bg = COL_SPECIAL_BG;   fg = COL_KEY_FG;
            } else {
                bg = COL_KEY_BG;       fg = COL_KEY_FG;
            }

            // Sombra
            g2.setColor(new Color(0, 0, 0, 30));
            g2.fillRoundRect((int)px + 1, (int)py + 2, (int)pw, (int)ph, arc, arc);

            // Corpo
            g2.setColor(bg);
            g2.fillRoundRect((int)px, (int)py, (int)pw, (int)ph, arc, arc);

            // Borda
            g2.setColor(k.highlightNormal || k.highlightShift
                    ? COL_HIGHLIGHT_BG.darker()
                    : COL_KEY_BORDER);
            g2.drawRoundRect((int)px, (int)py, (int)pw, (int)ph, arc, arc);

            // Texto principal
            g2.setColor(fg);
            g2.setFont(k.isSpecial ? fontSpecial : fontNormal);
            drawCentered(g2, k.label, (int)px, (int)py, (int)pw, (int)ph);

            // Texto shift (canto superior esquerdo, menor)
            if (!k.shiftLabel.isEmpty() && !k.isSpecial) {
                g2.setColor(new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), 160));
                g2.setFont(fontShift);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(k.shiftLabel,
                        (int)px + 3,
                        (int)py + fm.getAscent() + 1);
            }
        }

        // Legenda
        g2.setFont(new Font("Poppins", Font.PLAIN, 10));

        drawLegendItem(g2, COL_HIGHLIGHT_BG, "Tecla normal",
                padding, getHeight() - 14);
        drawLegendItem(g2, COL_SHIFT_BG,     "Shift necessário",
                padding + 120, getHeight() - 14);

        g2.dispose();
    }

    private void drawCentered(Graphics2D g2, String text, int x, int y, int w, int h) {
        if (text.isEmpty()) return;
        FontMetrics fm = g2.getFontMetrics();
        int tx = x + (w - fm.stringWidth(text)) / 2;
        int ty = y + (h - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(text, tx, ty);
    }

    private void drawLegendItem(Graphics2D g2, Color color, String text, int x, int y) {
        g2.setColor(color);
        g2.fillRoundRect(x, y, 12, 10, 3, 3);
        g2.setColor(COL_KEY_BORDER);
        g2.drawRoundRect(x, y, 12, 10, 3, 3);
        g2.setColor(new Color(60, 60, 60));
        g2.drawString(text, x + 16, y + 9);
    }
}