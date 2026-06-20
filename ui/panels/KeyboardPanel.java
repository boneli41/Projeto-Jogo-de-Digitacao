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
        String label;        // texto principal exibido (caractere produzido SEM shift)
        String shiftLabel;   // texto secundário (caractere produzido COM shift)
        float  x, y, w, h;
        boolean isSpecial;
        boolean isDeadKey;   // tecla-morta de acento (´ ` ~ ^)

        boolean highlightNormal;
        boolean highlightShift;
        boolean highlightCaps;

        Key(String label, String shiftLabel, float x, float y, float w, float h, boolean isSpecial) {
            this(label, shiftLabel, x, y, w, h, isSpecial, false);
        }

        Key(String label, String shiftLabel, float x, float y, float w, float h, boolean isSpecial, boolean isDeadKey) {
            this.label = label; this.shiftLabel = shiftLabel;
            this.x = x; this.y = y; this.w = w; this.h = h;
            this.isSpecial = isSpecial;
            this.isDeadKey = isDeadKey;
        }
    }

    // ----------------------------------------------------------------
    //  Layout ABNT2 completo
    // ----------------------------------------------------------------
    private static final Key[] KEYS;

    // Índices das teclas-mortas de acento, guardados para o highlight de letras acentuadas
    private static int idxAcuteGrave = -1;  // tecla ´ `  (agudo / grave)
    private static int idxTildeCirc  = -1;  // tecla ~ ^  (til / circunflexo)
    private static int idxTrema      = -1;  // tecla 6 ¨  (trema, raramente usado em pt-BR)

    static {
        float U = 1f;
        KEYS = new Key[]{
                // ------- Linha 0: números -------
                new Key("'","\"",  0,0, U,U, false),
                new Key("1","!",   1,0, U,U, false),
                new Key("2","@",   2,0, U,U, false),
                new Key("3","#",   3,0, U,U, false),
                new Key("4","$",   4,0, U,U, false),
                new Key("5","%",   5,0, U,U, false),
                new Key("6","¨",   6,0, U,U, false),   // ¨ é tecla-morta de trema (raro em pt-BR)
                new Key("7","&",   7,0, U,U, false),
                new Key("8","*",   8,0, U,U, false),
                new Key("9","(",   9,0, U,U, false),
                new Key("0",")",  10,0, U,U, false),
                new Key("-","_",  11,0, U,U, false),
                new Key("=","+",  12,0, U,U, false),
                new Key("⌫","",   13,0, 2f,U, true),

                // ------- Linha 1: QWERTY -------
                new Key("Tab","",  0,1, 1.5f,U, true),
                new Key("q","Q",   1.5f,1, U,U, false),
                new Key("w","W",   2.5f,1, U,U, false),
                new Key("e","E",   3.5f,1, U,U, false),
                new Key("r","R",   4.5f,1, U,U, false),
                new Key("t","T",   5.5f,1, U,U, false),
                new Key("y","Y",   6.5f,1, U,U, false),
                new Key("u","U",   7.5f,1, U,U, false),
                new Key("i","I",   8.5f,1, U,U, false),
                new Key("o","O",   9.5f,1, U,U, false),
                new Key("p","P",  10.5f,1, U,U, false),
                new Key("´","`",  11.5f,1, U,U, false, true),  // tecla-morta agudo/grave
                new Key("[","{",  12.5f,1, U,U, false),
                new Key("↵","",   13.5f,1, 1.5f,2f, true),

                // ------- Linha 2: ASDF -------
                new Key("Caps","",  0,2, 1.8f,U, true),
                new Key("a","A",   1.8f,2, U,U, false),
                new Key("s","S",   2.8f,2, U,U, false),
                new Key("d","D",   3.8f,2, U,U, false),
                new Key("f","F",   4.8f,2, U,U, false),
                new Key("g","G",   5.8f,2, U,U, false),
                new Key("h","H",   6.8f,2, U,U, false),
                new Key("j","J",   7.8f,2, U,U, false),
                new Key("k","K",   8.8f,2, U,U, false),
                new Key("l","L",   9.8f,2, U,U, false),
                new Key("ç","Ç",  10.8f,2, U,U, false),       // ç é tecla normal; Ç precisa de Shift
                new Key("~","^",  11.8f,2, U,U, false, true), // tecla-morta til/circunflexo
                new Key("]","}",  12.8f,2, U,U, false),

                // ------- Linha 3: ZXCV -------
                new Key("⇧","",   0,3, 1.3f,U, true),
                new Key("\\","|",  1.3f,3, U,U, false),
                new Key("z","Z",   2.3f,3, U,U, false),
                new Key("x","X",   3.3f,3, U,U, false),
                new Key("c","C",   4.3f,3, U,U, false),
                new Key("v","V",   5.3f,3, U,U, false),
                new Key("b","B",   6.3f,3, U,U, false),
                new Key("n","N",   7.3f,3, U,U, false),
                new Key("m","M",   8.3f,3, U,U, false),
                new Key(",","<",  9.3f,3, U,U, false),
                new Key(".",">" , 10.3f,3, U,U, false),
                new Key(";",":",  11.3f,3, U,U, false),
                new Key("/","?",  12.3f,3, U,U, false),
                new Key("⇧","",  13.3f,3, 1.7f,U, true),

                // ------- Linha 4: espaço -------
                new Key("Ctrl","",  0,4, 1.3f,U, true),
                new Key("Win","",  1.3f,4, 1.2f,U, true),
                new Key("Alt","",  2.5f,4, 1.2f,U, true),
                new Key("",   "",  3.7f,4, 6.3f,U, false),
                new Key("AltGr","",10.0f,4, 1.2f,U, true),
                new Key("Win","",  11.2f,4,1.0f,U, true),
                new Key("Menu","",12.2f,4,0.9f,U, true),
                new Key("Ctrl","",13.1f,4,0.9f,U, true),
        };
    }

    // ----------------------------------------------------------------
    //  Mapeamento caractere → índice de tecla
    // ----------------------------------------------------------------
    private final java.util.Map<Character, Integer> charToKeyIndex = new java.util.HashMap<>();
    private final Set<Character> needsShift = new HashSet<>();

    // Conjuntos de letras acentuadas por tecla-morta (pt-BR)
    // Agudo (´): á é í ó ú  | Á É Í Ó Ú (com shift na letra)
    // Grave (`): à (raro em pt-BR moderno, mas existe)
    // Til (~):   ã õ | Ã Õ
    // Circunflexo (^): â ê î ô û | Â Ê Î Ô Û
    private static final String ACUTE_LOWER = "áéíóú";
    private static final String ACUTE_UPPER = "ÁÉÍÓÚ";
    private static final String GRAVE_LOWER = "à";
    private static final String GRAVE_UPPER = "À";
    private static final String TILDE_LOWER = "ãõ";
    private static final String TILDE_UPPER = "ÃÕ";
    private static final String CIRC_LOWER  = "âêîôû";
    private static final String CIRC_UPPER  = "ÂÊÎÔÛ";

    private int idxShiftL = -1, idxShiftR = -1, idxCaps = -1;

    public KeyboardPanel() {
        setOpaque(false);
        setPreferredSize(new Dimension(0, 110));
        buildCharMap();
    }

    private void buildCharMap() {
        for (int i = 0; i < KEYS.length; i++) {
            Key k = KEYS[i];

            if (k.isSpecial) {
                if (k.label.equals("⇧") && idxShiftL < 0) idxShiftL = i;
                else if (k.label.equals("⇧"))              idxShiftR = i;
                else if (k.label.equals("Caps"))           idxCaps   = i;
                continue;
            }

            // Marca as teclas-mortas de acento para uso posterior
            if (k.isDeadKey) {
                if (k.label.equals("´")) idxAcuteGrave = i;       // ´ / `
                if (k.label.equals("~")) idxTildeCirc  = i;       // ~ / ^
                continue; // tecla-morta não produz caractere sozinha — não mapeia direto
            }

            // label = caractere produzido SEM shift
            if (!k.label.isEmpty()) {
                char c = k.label.charAt(0);
                charToKeyIndex.put(c, i);
            }

            // shiftLabel = caractere produzido COM shift
            if (!k.shiftLabel.isEmpty()) {
                char c = k.shiftLabel.charAt(0);
                charToKeyIndex.put(c, i);
                needsShift.add(c);
            }
        }

        // Tecla "6" também é tecla-morta de trema (raro, mas existe em pt-BR antigo)
        // não usado ativamente aqui, mantido apenas como nota.

        // Espaço
        for (int i = 0; i < KEYS.length; i++) {
            if (KEYS[i].y == 4 && KEYS[i].w > 4) {
                charToKeyIndex.put(' ', i);
                break;
            }
        }

        // ------------------------------------------------------------
        //  Mapeia as letras acentuadas para a tecla-morta correspondente
        //  + a tecla da letra base (ex: 'á' precisa de ´ e depois 'a')
        // ------------------------------------------------------------
        mapAccentGroup(ACUTE_LOWER, idxAcuteGrave, false);
        mapAccentGroup(ACUTE_UPPER, idxAcuteGrave, true);
        mapAccentGroup(GRAVE_LOWER, idxAcuteGrave, false); // mesma tecla física, posição shift
        mapAccentGroup(GRAVE_UPPER, idxAcuteGrave, true);
        mapAccentGroup(TILDE_LOWER, idxTildeCirc, false);
        mapAccentGroup(TILDE_UPPER, idxTildeCirc, true);
        mapAccentGroup(CIRC_LOWER, idxTildeCirc, false);
        mapAccentGroup(CIRC_UPPER, idxTildeCirc, true);
    }

    /**
     * Para cada letra acentuada do grupo, registra que ela "pertence"
     * à tecla-morta informada (para fins de destaque no teclado).
     * A letra acentuada em si não tem uma tecla própria — destacamos
     * a tecla-morta E a tecla da letra-base correspondente.
     */
    private void mapAccentGroup(String chars, int deadKeyIdx, boolean shiftNeeded) {
        if (deadKeyIdx < 0) return;
        for (char c : chars.toCharArray()) {
            charToKeyIndex.put(c, deadKeyIdx);
            if (shiftNeeded) needsShift.add(c);
        }
    }

    // ----------------------------------------------------------------
    //  API pública: pintar teclas para uma frase
    // ----------------------------------------------------------------
    public void highlightForText(String text) {
        for (Key k : KEYS) {
            k.highlightNormal = false;
            k.highlightShift  = false;
            k.highlightCaps   = false;
        }

        for (char c : text.toCharArray()) {
            if (c == '\n' || c == '\r') continue;

            Integer idx = charToKeyIndex.get(c);
            if (idx == null) continue;

            Key k = KEYS[idx];

            if (needsShift.contains(c)) {
                k.highlightShift = true;
                if (idxShiftL >= 0) KEYS[idxShiftL].highlightShift = true;
                if (idxShiftR >= 0) KEYS[idxShiftR].highlightShift = true;
            } else {
                k.highlightNormal = true;
            }

            // Se a letra é acentuada, também destaca a tecla da letra-base
            // (ex: para 'á', destaca também a tecla 'a')
            char base = removeAccent(c);
            if (base != c) {
                Integer baseIdx = charToKeyIndex.get(Character.isUpperCase(c) ? Character.toUpperCase(base) : base);
                // Quando a letra é minúscula acentuada, a base é a tecla normal (sem shift)
                // Quando é maiúscula acentuada, a base também precisa de shift
                char baseLookup = Character.isUpperCase(c) ? Character.toUpperCase(base) : base;
                baseIdx = charToKeyIndex.get(baseLookup);
                if (baseIdx != null && baseIdx != idx) {
                    Key bk = KEYS[baseIdx];
                    if (needsShift.contains(baseLookup)) {
                        bk.highlightShift = true;
                        if (idxShiftL >= 0) KEYS[idxShiftL].highlightShift = true;
                        if (idxShiftR >= 0) KEYS[idxShiftR].highlightShift = true;
                    } else {
                        bk.highlightNormal = true;
                    }
                }
            }
        }

        repaint();
    }

    /** Remove o acento de uma letra para achar a tecla-base (á → a, Ê → E, etc.) */
    private char removeAccent(char c) {
        switch (c) {
            case 'á': case 'à': case 'â': case 'ã': return 'a';
            case 'Á': case 'À': case 'Â': case 'Ã': return 'A';
            case 'é': case 'ê': return 'e';
            case 'É': case 'Ê': return 'E';
            case 'í': case 'î': return 'i';
            case 'Í': case 'Î': return 'I';
            case 'ó': case 'ô': case 'õ': return 'o';
            case 'Ó': case 'Ô': case 'Õ': return 'O';
            case 'ú': case 'û': return 'u';
            case 'Ú': case 'Û': return 'U';
            default: return c;
        }
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

        float totalUnits = 15f;
        int   padding    = 6;
        float unit       = (getWidth() - padding * 2) / totalUnits;
        float keyH       = (getHeight() - padding * 2 - 4 * 3) / 5f;
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

            Color bg, fg;
            if (k.highlightShift) {
                bg = COL_SHIFT_BG; fg = COL_SHIFT_FG;
            } else if (k.highlightCaps) {
                bg = COL_CAPS_BG;  fg = COL_CAPS_FG;
            } else if (k.highlightNormal) {
                bg = COL_HIGHLIGHT_BG; fg = COL_HIGHLIGHT_FG;
            } else if (k.isSpecial || k.isDeadKey) {
                bg = COL_SPECIAL_BG;   fg = COL_KEY_FG;
            } else {
                bg = COL_KEY_BG;       fg = COL_KEY_FG;
            }

            g2.setColor(new Color(0, 0, 0, 30));
            g2.fillRoundRect((int)px + 1, (int)py + 2, (int)pw, (int)ph, arc, arc);

            g2.setColor(bg);
            g2.fillRoundRect((int)px, (int)py, (int)pw, (int)ph, arc, arc);

            g2.setColor(k.highlightNormal || k.highlightShift
                    ? COL_HIGHLIGHT_BG.darker()
                    : COL_KEY_BORDER);
            g2.drawRoundRect((int)px, (int)py, (int)pw, (int)ph, arc, arc);

            g2.setColor(fg);
            g2.setFont(k.isSpecial ? fontSpecial : fontNormal);
            drawCentered(g2, k.label, (int)px, (int)py, (int)pw, (int)ph);

            if (!k.shiftLabel.isEmpty() && !k.isSpecial) {
                g2.setColor(new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), 160));
                g2.setFont(fontShift);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(k.shiftLabel, (int)px + 3, (int)py + fm.getAscent() + 1);
            }
        }

        g2.setFont(new Font("Poppins", Font.PLAIN, 10));
        drawLegendItem(g2, COL_HIGHLIGHT_BG, "Tecla normal", padding, getHeight() - 14);
        drawLegendItem(g2, COL_SHIFT_BG,     "Shift necessário", padding + 120, getHeight() - 14);

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