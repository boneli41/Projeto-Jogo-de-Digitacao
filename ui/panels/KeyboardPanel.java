package ui.panels;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * Teclado ABNT2 visual com dois modos:
 *
 *  1. highlightForText(String)  — pinta TODAS as teclas necessárias
 *     para a frase de uma vez (usado no loadExercise).
 *
 *  2. highlightCurrentKey(char expected, boolean hasError)
 *     — acende APENAS a próxima tecla esperada; se hasError=true,
 *       acende também o Backspace em vermelho. Chamado em onInput().
 *
 * Cores:
 *   Verde  = tecla sem Shift       Amarelo = tecla com Shift
 *   Misto  = ambos na mesma frase  Vermelho = Backspace (erro)
 *
 * Acentos ABNT2:
 *   Tecla ´/` (linha QWERTY, após P):
 *     – sem Shift → acento agudo morto  → á é í ó ú â ê î ô û à è ì ò ù
 *     – com Shift → crase              → à (e combinações)
 *   Tecla ~/^ (linha ASDF, após Ç):
 *     – sem Shift → til morto           → ã õ ñ
 *     – com Shift → circunflexo morto   → â ê î ô û
 */
public class KeyboardPanel extends JPanel {

    // ---------------------------------------------------------------- Cores
    private static final Color COL_KEY_BG     = new Color(239, 246, 255);
    private static final Color COL_KEY_BORDER = new Color(147, 197, 253);
    private static final Color COL_KEY_FG     = new Color( 30,  58, 138);
    private static final Color COL_NORMAL_BG  = new Color( 34, 197,  94);   // verde
    private static final Color COL_NORMAL_FG  = Color.WHITE;
    private static final Color COL_SHIFT_BG   = new Color(250, 204,  21);   // amarelo
    private static final Color COL_SHIFT_FG   = new Color( 69,  45,   0);
    private static final Color COL_ERROR_BG   = new Color(230,  60,  60);   // vermelho (erro)
    private static final Color COL_ERROR_FG   = Color.WHITE;
    private static final Color COL_SPECIAL_BG = new Color(219, 234, 254);

    private static final int LEGEND_H = 18;   // px reservados para a legenda

    // ---------------------------------------------------------------- Tecla
    private static class Key {
        String  label, shiftLabel;
        float   x, y, w, h;
        boolean isSpecial;
        // Estado pintado
        boolean needNormal, needShift, isError;

        Key(String l, String s, float x, float y, float w, float h, boolean sp) {
            label = l; shiftLabel = s;
            this.x = x; this.y = y; this.w = w; this.h = h; isSpecial = sp;
        }
    }

    // ---------------------------------------------------------------- Layout ABNT2
    private static final Key[] KEYS;
    static {
        float U = 1f;
        KEYS = new Key[]{
                // Linha 0: números
                new Key("'","\"",  0,0,  U,U, false),
                new Key("1","!",   1,0,  U,U, false),
                new Key("2","@",   2,0,  U,U, false),
                new Key("3","#",   3,0,  U,U, false),
                new Key("4","$",   4,0,  U,U, false),
                new Key("5","%",   5,0,  U,U, false),
                new Key("6","\u00a8", 6,0, U,U, false),   // 6 / ¨
                new Key("7","&",   7,0,  U,U, false),
                new Key("8","*",   8,0,  U,U, false),
                new Key("9","(",   9,0,  U,U, false),
                new Key("0",")",  10,0,  U,U, false),
                new Key("-","_",  11,0,  U,U, false),
                new Key("=","+",  12,0,  U,U, false),
                new Key("Apagar","",13,0,2f,U, true),

                // Linha 1: QWERTY
                new Key("Tab","",  0,  1,1.5f,U, true),
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
                // Acento agudo/crase:  sem-shift=´(agudo)  com-shift=`(crase/grave)
                new Key("\u00b4","`",11.5f,1,U,U, false),
                new Key("[","{",  12.5f,1, U,U, false),
                new Key("Enter","",13.5f,1,1.5f,2f, true),

                // Linha 2: ASDF
                new Key("Caps","", 0,  2,1.8f,U, true),
                new Key("A","",   1.8f,2, U,U, false),
                new Key("S","",   2.8f,2, U,U, false),
                new Key("D","",   3.8f,2, U,U, false),
                new Key("F","",   4.8f,2, U,U, false),
                new Key("G","",   5.8f,2, U,U, false),
                new Key("H","",   6.8f,2, U,U, false),
                new Key("J","",   7.8f,2, U,U, false),
                new Key("K","",   8.8f,2, U,U, false),
                new Key("L","",   9.8f,2, U,U, false),
                new Key("\u00c7","",10.8f,2,U,U, false),    // Ç
                // Til/Circunflexo: sem-shift=~(til)  com-shift=^(circunflexo)
                new Key("~","^",  11.8f,2, U,U, false),
                new Key("]","}",  12.8f,2, U,U, false),

                // Linha 3: ZXCV
                new Key("Shift","", 0,  3,1.3f,U, true),
                new Key("|","\\", 1.3f,3, U,U, false),
                new Key("Z","",   2.3f,3, U,U, false),
                new Key("X","",   3.3f,3, U,U, false),
                new Key("C","",   4.3f,3, U,U, false),
                new Key("V","",   5.3f,3, U,U, false),
                new Key("B","",   6.3f,3, U,U, false),
                new Key("N","",   7.3f,3, U,U, false),
                new Key("M","",   8.3f,3, U,U, false),
                new Key(",","<",  9.3f,3, U,U, false),
                new Key(".",">",  10.3f,3,U,U, false),
                new Key(";",":",  11.3f,3,U,U, false),
                new Key("/","?",  12.3f,3,U,U, false),
                new Key("Shift","",13.3f,3,1.7f,U, true),

                // Linha 4: espaço
                new Key("Ctrl","", 0,  4,1.3f,U, true),
                new Key("Win","",  1.3f,4,1.2f,U, true),
                new Key("Alt","",  2.5f,4,1.2f,U, true),
                new Key("",    "",  3.7f,4,6.3f,U, false),  // Espaço
                new Key("AltGr","",10.0f,4,1.2f,U, true),
                new Key("Win","",  11.2f,4,1.0f,U, true),
                new Key("Menu","", 12.2f,4,0.9f,U, true),
                new Key("Ctrl","", 13.1f,4,0.9f,U, true),
        };
    }

    // ---------------------------------------------------------------- Mapeamento char→tecla
    private final Map<Character,Integer> charToKey  = new HashMap<>();
    private final Set<Character>         needsShift = new HashSet<>();
    private int idxShiftL = -1, idxShiftR = -1, idxBackspace = -1;

    public KeyboardPanel() {
        setOpaque(false);
        buildCharMap();
    }

    private void buildCharMap() {
        for (int i = 0; i < KEYS.length; i++) {
            Key k = KEYS[i];

            if (k.isSpecial) {
                if ("Shift".equals(k.label) && idxShiftL < 0) idxShiftL = i;
                else if ("Shift".equals(k.label))             idxShiftR = i;
                else if ("Apagar".equals(k.label))            idxBackspace = i;
                continue;
            }

            // ----- label sem Shift -----
            if (!k.label.isEmpty()) {
                char c = k.label.charAt(0);

                if (c >= 'A' && c <= 'Z') {
                    // Letras maiúsculas no label → minúscula=normal, maiúscula=shift
                    charToKey.put(Character.toLowerCase(c), i);
                    charToKey.put(c, i);
                    needsShift.add(c);
                } else {
                    charToKey.put(c, i);

                    // -------- Acento AGUDO / GRAVE (tecla ´/`) --------
                    // sem Shift (´) produz: á é í ó ú â ê î ô û à è ì ò ù
                    // com Shift (`) produz: à (grave) — mas na prática
                    //   circunflexos vêm desta tecla SEM shift em muitos layouts BR.
                    // Estratégia conservadora: tudo que for acentuado com agudo/grave/
                    //   circunflexo → tecla ´ sem shift.
                    if (c == '\u00b4') {
                        // sem Shift (´) → agudo: á é í ó ú
                        for (char ac : "áéíóú".toCharArray()) charToKey.put(ac, i);
                        for (char ac : "ÁÉÍÓÚ".toCharArray()) {
                            charToKey.put(ac, i); needsShift.add(ac);
                        }
                        // com Shift (`) → grave/crase: à è ì ò ù
                        for (char ac : "àèìòù".toCharArray()) {
                            charToKey.put(ac, i); needsShift.add(ac);
                        }
                        for (char ac : "ÀÈÌÒÙ".toCharArray()) {
                            charToKey.put(ac, i); needsShift.add(ac);
                        }
                        // a crase isolada (`) também é shift
                        charToKey.put('`', i); needsShift.add('`');
                    }

                    // -------- TIL / CIRCUNFLEXO (tecla ~/^) --------
                    // sem Shift (~) produz: ã õ ñ
                    // com Shift (^) produz: â ê î ô û  ← CIRCUNFLEXO precisa SHIFT
                    if (c == '~') {
                        // ~ sem shift → til
                        for (char ac : "ãõñ".toCharArray()) charToKey.put(ac, i);
                        for (char ac : "ÃÕÑ".toCharArray()) {
                            charToKey.put(ac, i); needsShift.add(ac);
                        }
                        // ^ com shift → circunflexo
                        // Mapeamos â ê î ô û para esta mesma tecla, precisando shift
                        for (char ac : "âêîôû".toCharArray()) {
                            charToKey.put(ac, i); needsShift.add(ac);
                        }
                        for (char ac : "ÂÊÎÔÛ".toCharArray()) {
                            charToKey.put(ac, i); needsShift.add(ac);
                        }
                    }
                }
            }

            // ----- shiftLabel com Shift -----
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

        // Ç maiúsculo no label → ç minúsculo = mesma tecla sem shift
        for (int i = 0; i < KEYS.length; i++) {
            if ("\u00c7".equals(KEYS[i].label)) {
                charToKey.put('\u00e7', i);   // ç sem shift
                break;
            }
        }
    }

    // ================================================================
    //  API 1 – pinta todas as teclas da frase de uma vez
    // ================================================================
    public void highlightForText(String text) {
        clearState();
        for (char c : text.toCharArray()) markChar(c);
        propagateShifts();
        repaint();
    }

    // ================================================================
    //  API 2 – acende só a próxima tecla; backspace vermelho se erro
    //  vowelHint: para acentos mortos, também acende a vogal base
    // ================================================================
    public void highlightCurrentKey(char expected, char vowelHint, boolean hasError) {
        clearState();

        if (hasError) {
            if (idxBackspace >= 0) KEYS[idxBackspace].isError = true;
        } else {
            markChar(expected);
            if (vowelHint != '\0') markChar(vowelHint);
            propagateShifts();
        }
        repaint();
    }

    // ---------------------------------------------------------------- helpers internos
    private void clearState() {
        for (Key k : KEYS) { k.needNormal = false; k.needShift = false; k.isError = false; }
    }

    private void markChar(char c) {
        if (c == '\n' || c == '\r') return;
        Integer idx = charToKey.get(c);
        if (idx == null) return;
        Key k = KEYS[idx];
        if (needsShift.contains(c)) k.needShift  = true;
        else                         k.needNormal = true;
    }

    private void propagateShifts() {
        boolean any = false;
        for (Key k : KEYS) if (!k.isSpecial && k.needShift) { any = true; break; }
        if (any) {
            if (idxShiftL >= 0) KEYS[idxShiftL].needShift = true;
            if (idxShiftR >= 0) KEYS[idxShiftR].needShift = true;
        }
    }

    // ================================================================
    //  Renderização
    // ================================================================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int   pad   = 4;
        float totalU = 15f;
        int   kbH   = getHeight() - LEGEND_H - pad * 2;
        float unit  = (getWidth() - pad * 2) / totalU;
        float rowH  = (kbH - 4 * 3) / 5f;
        float gap   = 2f;
        int   arc   = 5;

        Font fNormal  = new Font("Poppins", Font.BOLD,  Math.max(8, (int)(unit * 0.23f)));
        Font fSymbol  = new Font("Poppins", Font.BOLD,  Math.max(8, (int)(unit * 0.19f)));
        Font fSpecial = new Font("Poppins", Font.BOLD,  Math.max(7, (int)(unit * 0.18f)));
        Font fSmall   = new Font("Poppins", Font.BOLD,  Math.max(6, (int)(unit * 0.16f)));

        for (Key k : KEYS) {
            float px = pad + k.x * unit + gap;
            float py = pad + k.y * (rowH + 3) + gap;
            float pw = k.w * unit - gap * 2;
            float ph = k.h * rowH + (k.h > 1 ? (k.h - 1) * 3 : 0) - gap * 2;

            // Sombra
            g2.setColor(new Color(0,0,0,25));
            g2.fillRoundRect((int)px+1,(int)py+2,(int)pw,(int)ph,arc,arc);

            if (k.isError) {
                // Backspace vermelho
                g2.setColor(COL_ERROR_BG);
                g2.fillRoundRect((int)px,(int)py,(int)pw,(int)ph,arc,arc);
                g2.setColor(COL_ERROR_BG.darker());
                g2.drawRoundRect((int)px,(int)py,(int)pw,(int)ph,arc,arc);
                g2.setFont(fSpecial); g2.setColor(COL_ERROR_FG);
                drawCenteredFit(g2, k.label,(int)px,(int)py,(int)pw,(int)ph);

            } else {
                // Cor sólida
                Color bg, fg;
                if      (k.needShift)  { bg = COL_SHIFT_BG;   fg = COL_SHIFT_FG;  }
                else if (k.needNormal) { bg = COL_NORMAL_BG;  fg = COL_NORMAL_FG; }
                else if (k.isSpecial)  { bg = COL_SPECIAL_BG; fg = COL_KEY_FG;    }
                else                   { bg = COL_KEY_BG;      fg = COL_KEY_FG;    }

                g2.setColor(bg);
                g2.fillRoundRect((int)px,(int)py,(int)pw,(int)ph,arc,arc);
                g2.setColor((k.needNormal||k.needShift) ? bg.darker() : COL_KEY_BORDER);
                g2.drawRoundRect((int)px,(int)py,(int)pw,(int)ph,arc,arc);

                g2.setColor(fg);
                g2.setFont(k.isSpecial ? fSpecial : isSymbolKey(k) ? fSymbol : fNormal);
                drawCenteredFit(g2, k.label,(int)px,(int)py,(int)pw,(int)ph);

                if (!k.shiftLabel.isEmpty() && !k.isSpecial) {
                    g2.setFont(fSmall);
                    g2.setColor(new Color(fg.getRed(),fg.getGreen(),fg.getBlue(),150));
                    FontMetrics fm = g2.getFontMetrics();
                    int maxShiftW = Math.max(8, (int)pw - 10);
                    while (fm.stringWidth(k.shiftLabel) > maxShiftW && g2.getFont().getSize() > 6) {
                        g2.setFont(g2.getFont().deriveFont((float) g2.getFont().getSize() - 1));
                        fm = g2.getFontMetrics();
                    }
                    g2.drawString(k.shiftLabel,(int)px+5,(int)py+fm.getAscent()+2);
                }
            }
        }

        // ---- Legenda ----
        int ly = getHeight() - LEGEND_H + 2;
        g2.setFont(new Font("Poppins", Font.PLAIN, 10));
        drawLegendItem(g2, COL_NORMAL_BG, "Próxima tecla",      pad,       ly);
        drawLegendItem(g2, COL_SHIFT_BG,  "Precisa Shift",       pad + 130, ly);
        drawLegendItem(g2, COL_ERROR_BG,  "Apagar (tecla errada)",pad + 260, ly);

        g2.dispose();
    }

    private void drawCenteredFit(Graphics2D g2, String t, int x, int y, int w, int h) {
        if (t==null||t.isEmpty()) return;
        Font original = g2.getFont();
        FontMetrics fm = g2.getFontMetrics();
        int maxW = Math.max(8, w - 14);
        while (fm.stringWidth(t) > maxW && g2.getFont().getSize() > 7) {
            g2.setFont(g2.getFont().deriveFont((float) g2.getFont().getSize() - 1));
            fm = g2.getFontMetrics();
        }
        g2.drawString(t, x+(w-fm.stringWidth(t))/2, y+(h-fm.getHeight())/2+fm.getAscent());
        g2.setFont(original);
    }
    private boolean isSymbolKey(Key k) {
        if (k.label == null || k.label.isEmpty()) return false;
        return "'-=[]\\;,./~|".contains(k.label) || k.shiftLabel != null && !k.shiftLabel.isEmpty();
    }
    private void drawInRegion(Graphics2D g2, String t, int x, int y, int w, int h) {
        if (t==null||t.isEmpty()) return;
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(t, x+(w-fm.stringWidth(t))/2, y+(h-fm.getHeight())/2+fm.getAscent());
    }
    private void drawLegendItem(Graphics2D g2, Color color, String text, int x, int y) {
        g2.setColor(color);
        g2.fillRoundRect(x,y,12,10,3,3);
        g2.setColor(COL_KEY_BORDER);
        g2.drawRoundRect(x,y,12,10,3,3);
        g2.setColor(new Color(60,60,60));
        g2.drawString(text, x+16, y+9);
    }
}
