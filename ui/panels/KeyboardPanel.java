package ui.panels;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class KeyboardPanel extends JPanel {

    // ----------------------------------------------------------------
    //  Cores
    // ----------------------------------------------------------------
    private static final Color COL_KEY_BG      = new Color(240, 240, 240);
    private static final Color COL_KEY_BORDER  = new Color(180, 180, 180);
    private static final Color COL_KEY_FG      = new Color( 40,  40,  40);
    private static final Color COL_NORMAL_BG   = new Color(120, 210, 120);  // verde = sem shift
    private static final Color COL_NORMAL_FG   = new Color( 20,  80,  20);
    private static final Color COL_SHIFT_BG    = new Color(255, 220, 100);  // amarelo = shift
    private static final Color COL_SHIFT_FG    = new Color( 90,  60,   0);
    private static final Color COL_SPECIAL_BG  = new Color(210, 210, 210);

    /** Altura reservada para a legenda abaixo do teclado (px). */
    private static final int LEGEND_H = 18;

    // ----------------------------------------------------------------
    //  Estrutura de uma tecla
    // ----------------------------------------------------------------
    private static class Key {
        String label;       // caractere/texto sem Shift
        String shiftLabel;  // caractere/texto com Shift
        float  x, y, w, h;
        boolean isSpecial;

        boolean needNormal; // frase usa esta tecla SEM shift
        boolean needShift;  // frase usa esta tecla COM shift

        Key(String label, String shiftLabel,
            float x, float y, float w, float h, boolean isSpecial) {
            this.label = label; this.shiftLabel = shiftLabel;
            this.x = x; this.y = y; this.w = w; this.h = h;
            this.isSpecial = isSpecial;
        }
    }

    // ----------------------------------------------------------------
    //  Layout ABNT2
    // ----------------------------------------------------------------
    private static final Key[] KEYS;
    static {
        float U = 1f;
        KEYS = new Key[]{
                // Linha 0: números / símbolos
                new Key("'", "\"", 0,0,  U,  U, false),
                new Key("1", "!",  1,0,  U,  U, false),
                new Key("2", "@",  2,0,  U,  U, false),
                new Key("3", "#",  3,0,  U,  U, false),
                new Key("4", "$",  4,0,  U,  U, false),
                new Key("5", "%",  5,0,  U,  U, false),
                new Key("6", "¨",  6,0,  U,  U, false),
                new Key("7", "&",  7,0,  U,  U, false),
                new Key("8", "*",  8,0,  U,  U, false),
                new Key("9", "(",  9,0,  U,  U, false),
                new Key("0", ")", 10,0,  U,  U, false),
                new Key("-", "_", 11,0,  U,  U, false),
                new Key("=", "+", 12,0,  U,  U, false),
                new Key("⌫", "",  13,0, 2f,  U, true),

                // Linha 1: QWERTY
                new Key("Tab","",   0,  1, 1.5f, U, true),
                new Key("Q",  "",   1.5f,1, U,   U, false),
                new Key("W",  "",   2.5f,1, U,   U, false),
                new Key("E",  "",   3.5f,1, U,   U, false),
                new Key("R",  "",   4.5f,1, U,   U, false),
                new Key("T",  "",   5.5f,1, U,   U, false),
                new Key("Y",  "",   6.5f,1, U,   U, false),
                new Key("U",  "",   7.5f,1, U,   U, false),
                new Key("I",  "",   8.5f,1, U,   U, false),
                new Key("O",  "",   9.5f,1, U,   U, false),
                new Key("P",  "",  10.5f,1, U,   U, false),
                new Key("\u00b4","`",11.5f,1,U,  U, false),  // ´ / `
                new Key("[",  "{", 12.5f,1, U,   U, false),
                new Key("↵",  "",  13.5f,1,1.5f,2f, true),

                // Linha 2: ASDF
                new Key("Caps","",  0,  2,1.8f, U, true),
                new Key("A",  "",   1.8f,2, U,  U, false),
                new Key("S",  "",   2.8f,2, U,  U, false),
                new Key("D",  "",   3.8f,2, U,  U, false),
                new Key("F",  "",   4.8f,2, U,  U, false),
                new Key("G",  "",   5.8f,2, U,  U, false),
                new Key("H",  "",   6.8f,2, U,  U, false),
                new Key("J",  "",   7.8f,2, U,  U, false),
                new Key("K",  "",   8.8f,2, U,  U, false),
                new Key("L",  "",   9.8f,2, U,  U, false),
                new Key("\u00c7","",10.8f,2,U,  U, false),  // Ç
                new Key("~",  "^", 11.8f,2, U,  U, false),
                new Key("]",  "}", 12.8f,2, U,  U, false),

                // Linha 3: ZXCV
                new Key("⇧",  "",   0,  3,1.3f, U, true),
                new Key("|",  "\\", 1.3f,3, U,  U, false),
                new Key("Z",  "",   2.3f,3, U,  U, false),
                new Key("X",  "",   3.3f,3, U,  U, false),
                new Key("C",  "",   4.3f,3, U,  U, false),
                new Key("V",  "",   5.3f,3, U,  U, false),
                new Key("B",  "",   6.3f,3, U,  U, false),
                new Key("N",  "",   7.3f,3, U,  U, false),
                new Key("M",  "",   8.3f,3, U,  U, false),
                new Key(",",  "<",  9.3f,3, U,  U, false),
                new Key(".",  ">", 10.3f,3, U,  U, false),
                new Key(";",  ":", 11.3f,3, U,  U, false),
                new Key("/",  "?", 12.3f,3, U,  U, false),
                new Key("⇧",  "",  13.3f,3,1.7f,U, true),

                // Linha 4: espaço
                new Key("Ctrl","",  0,  4,1.3f, U, true),
                new Key("Win", "",  1.3f,4,1.2f, U, true),
                new Key("Alt", "",  2.5f,4,1.2f, U, true),
                new Key("",    "",  3.7f,4,6.3f, U, false),  // espaço
                new Key("AltGr","",10.0f,4,1.2f, U, true),
                new Key("Win", "", 11.2f,4,1.0f, U, true),
                new Key("Menu","", 12.2f,4,0.9f, U, true),
                new Key("Ctrl","", 13.1f,4,0.9f, U, true),
        };
    }

    // ----------------------------------------------------------------
    //  Mapeamento: char → índice de tecla
    // ----------------------------------------------------------------
    private final Map<Character, Integer> charToKey  = new HashMap<>();
    private final Set<Character>          needsShift = new HashSet<>();
    private int idxShiftL = -1, idxShiftR = -1;

    public KeyboardPanel() {
        setOpaque(false);
        buildCharMap();
    }

    private void buildCharMap() {
        for (int i = 0; i < KEYS.length; i++) {
            Key k = KEYS[i];
            if (k.isSpecial) {
                if ("⇧".equals(k.label) && idxShiftL < 0) idxShiftL = i;
                else if ("⇧".equals(k.label))             idxShiftR = i;
                continue;
            }

            // --- label (sem Shift) ---
            if (!k.label.isEmpty()) {
                char c = k.label.charAt(0);

                if (c >= 'A' && c <= 'Z') {
                    // Letras: maiúscula precisa shift, minúscula não
                    char lo = Character.toLowerCase(c);
                    charToKey.put(lo, i);   // minúscula → tecla normal
                    charToKey.put(c,  i);   // maiúscula → mesma tecla
                    needsShift.add(c);
                } else {
                    charToKey.put(c, i);

                    // Acentos mortos: a tecla ´ (U+00B4) produz acentos combinados.
                    // Mapeamos os caracteres acentuados mais comuns para a mesma tecla.
                    // Acento agudo (´): á é í ó ú Á É Í Ó Ú
                    if (c == '\u00b4') {
                        for (char ac : "áéíóúàèìòùâêîôûãõ".toCharArray()) {
                            charToKey.put(ac, i);
                            // acentuados precisam da tecla ´ (sem Shift no mais comum)
                        }
                        for (char ac : "ÁÉÍÓÚÀÈÌÒÙÂÊÎÔÛÃÕ".toCharArray()) {
                            charToKey.put(ac, i);
                            needsShift.add(ac);
                        }
                    }
                    // Til (~): ã õ Ã Õ ñ
                    if (c == '~') {
                        for (char ac : "ãõñ".toCharArray()) charToKey.put(ac, i);
                        for (char ac : "ÃÕÑ".toCharArray()) {
                            charToKey.put(ac, i);
                            needsShift.add(ac);
                        }
                    }
                }
            }

            // --- shiftLabel (com Shift) ---
            if (!k.shiftLabel.isEmpty()) {
                char c = k.shiftLabel.charAt(0);
                charToKey.put(c, i);
                needsShift.add(c);
            }
        }

        // Espaço
        for (int i = 0; i < KEYS.length; i++) {
            if (KEYS[i].y == 4 && KEYS[i].w > 4) { charToKey.put(' ', i); break; }
        }

        // Ç (já está no label da tecla Ç; mapeia minúscula também)
        for (int i = 0; i < KEYS.length; i++) {
            if ("\u00c7".equals(KEYS[i].label)) {
                charToKey.put('\u00e7', i);  // ç minúsculo → mesma tecla, sem Shift
                break;
            }
        }
    }

    // ----------------------------------------------------------------
    //  API pública
    // ----------------------------------------------------------------
    public void highlightForText(String text) {
        for (Key k : KEYS) { k.needNormal = false; k.needShift = false; }

        for (char c : text.toCharArray()) {
            if (c == '\n' || c == '\r') continue;
            Integer idx = charToKey.get(c);
            if (idx == null) continue;
            Key k = KEYS[idx];
            if (needsShift.contains(c)) k.needShift  = true;
            else                         k.needNormal = true;
        }

        // Propaga shift para as teclas Shift físicas
        boolean anyShift = false;
        for (Key k : KEYS) if (!k.isSpecial && k.needShift) { anyShift = true; break; }
        if (anyShift) {
            if (idxShiftL >= 0) KEYS[idxShiftL].needShift = true;
            if (idxShiftR >= 0) KEYS[idxShiftR].needShift = true;
        }

        repaint();
    }

    // ----------------------------------------------------------------
    //  Renderização
    // ----------------------------------------------------------------
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int    pad       = 4;
        float  totalU    = 15f;
        // Área do teclado: exclui padding e espaço da legenda
        int    kbH       = getHeight() - LEGEND_H - pad * 2;
        float  unit      = (getWidth()  - pad * 2) / totalU;
        float  rowH      = (kbH        - 4 * 3)   / 5f;    // 5 linhas, 3px de gap
        float  gap       = 2f;
        int    arc       = 5;

        Font fNormal  = new Font("Poppins", Font.BOLD,  Math.max(8, (int)(unit * 0.30f)));
        Font fSpecial = new Font("Poppins", Font.PLAIN, Math.max(7, (int)(unit * 0.22f)));
        Font fSmall   = new Font("Poppins", Font.BOLD,  Math.max(6, (int)(unit * 0.19f)));

        for (Key k : KEYS) {
            float px = pad + k.x * unit + gap;
            float py = pad + k.y * (rowH + 3) + gap;
            float pw = k.w * unit - gap * 2;
            float ph = k.h * rowH + (k.h > 1 ? (k.h - 1) * 3 : 0) - gap * 2;

            boolean mixed = k.needNormal && k.needShift;

            // -- Sombra --
            g2.setColor(new Color(0, 0, 0, 25));
            g2.fillRoundRect((int)px+1, (int)py+2, (int)pw, (int)ph, arc, arc);

            if (mixed) {
                // Metade esquerda verde (normal), metade direita amarela (shift)
                int half = (int)(pw / 2);
                // Lado normal (verde) — clip left
                g2.setClip((int)px, (int)py, half, (int)ph);
                g2.setColor(COL_NORMAL_BG);
                g2.fillRoundRect((int)px, (int)py, (int)pw, (int)ph, arc, arc);
                // Lado shift (amarelo) — clip right
                g2.setClip((int)px + half, (int)py, (int)pw - half + 1, (int)ph);
                g2.setColor(COL_SHIFT_BG);
                g2.fillRoundRect((int)px, (int)py, (int)pw, (int)ph, arc, arc);
                g2.setClip(null);
                // Borda
                g2.setColor(COL_NORMAL_BG.darker());
                g2.drawRoundRect((int)px, (int)py, (int)pw, (int)ph, arc, arc);
                // Linha divisória central
                g2.setColor(new Color(120, 120, 120, 180));
                g2.drawLine((int)px + half, (int)py + 2, (int)px + half, (int)py + (int)ph - 2);
                // Texto: label (normal) no lado esquerdo, shiftLabel no lado direito
                g2.setFont(fSmall);
                g2.setColor(COL_NORMAL_FG);
                drawInRegion(g2, k.label,      (int)px,        (int)py, half,           (int)ph);
                g2.setColor(COL_SHIFT_FG);
                drawInRegion(g2, k.shiftLabel, (int)px + half, (int)py, (int)pw - half, (int)ph);

            } else {
                // Cor sólida normal
                Color bg, fg;
                if      (k.needShift)   { bg = COL_SHIFT_BG;   fg = COL_SHIFT_FG;  }
                else if (k.needNormal)  { bg = COL_NORMAL_BG;  fg = COL_NORMAL_FG; }
                else if (k.isSpecial)   { bg = COL_SPECIAL_BG; fg = COL_KEY_FG;    }
                else                    { bg = COL_KEY_BG;      fg = COL_KEY_FG;    }

                g2.setColor(bg);
                g2.fillRoundRect((int)px, (int)py, (int)pw, (int)ph, arc, arc);
                g2.setColor((k.needNormal || k.needShift) ? bg.darker() : COL_KEY_BORDER);
                g2.drawRoundRect((int)px, (int)py, (int)pw, (int)ph, arc, arc);

                // Texto principal centrado
                g2.setColor(fg);
                g2.setFont(k.isSpecial ? fSpecial : fNormal);
                drawCentered(g2, k.label, (int)px, (int)py, (int)pw, (int)ph);

                // shiftLabel no canto superior esquerdo (pequeno)
                if (!k.shiftLabel.isEmpty() && !k.isSpecial) {
                    g2.setFont(fSmall);
                    g2.setColor(new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), 150));
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(k.shiftLabel, (int)px + 3, (int)py + fm.getAscent() + 1);
                }
            }
        }

        // ---- Legenda (abaixo do teclado, fora da área de teclas) ----
        int legendY = getHeight() - LEGEND_H + 2;
        g2.setFont(new Font("Poppins", Font.PLAIN, 10));

        drawLegendItem(g2, COL_NORMAL_BG, "Tecla normal (sem Shift)",  pad,       legendY);
        drawLegendItem(g2, COL_SHIFT_BG,  "Com Shift",                 pad + 175, legendY);
        // Legenda mista
        int mx = pad + 280;
        int mw = 22; int mh = 10;
        g2.setClip(mx, legendY, mw/2, mh);
        g2.setColor(COL_NORMAL_BG);
        g2.fillRoundRect(mx, legendY, mw, mh, 3, 3);
        g2.setClip(mx + mw/2, legendY, mw/2 + 1, mh);
        g2.setColor(COL_SHIFT_BG);
        g2.fillRoundRect(mx, legendY, mw, mh, 3, 3);
        g2.setClip(null);
        g2.setColor(COL_KEY_BORDER);
        g2.drawRoundRect(mx, legendY, mw, mh, 3, 3);
        g2.setColor(new Color(60, 60, 60));
        g2.drawString("Ambos", mx + mw + 4, legendY + 9);

        g2.dispose();
    }

    private void drawCentered(Graphics2D g2, String text, int x, int y, int w, int h) {
        if (text == null || text.isEmpty()) return;
        FontMetrics fm = g2.getFontMetrics();
        int tx = x + (w - fm.stringWidth(text)) / 2;
        int ty = y + (h - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(text, tx, ty);
    }

    private void drawInRegion(Graphics2D g2, String text, int x, int y, int w, int h) {
        if (text == null || text.isEmpty()) return;
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