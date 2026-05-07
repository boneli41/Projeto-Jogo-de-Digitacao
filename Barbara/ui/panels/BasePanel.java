package ui.panels;

import model.Player;
import ui.TypingGame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public abstract class BasePanel extends JPanel {

    protected TypingGame game;

    protected static final Color COLOR_BG          = new Color(245, 245, 220);
    protected static final Color COLOR_PRIMARY     = new Color( 30, 105, 210);  // azul vivo
    protected static final Color COLOR_SUCCESS     = new Color( 32, 148,  70);  // verde limpo
    protected static final Color COLOR_DANGER      = new Color(204,  40,  40);
    protected static final Color COLOR_WARNING     = new Color(230, 115,   0);
    protected static final Color COLOR_TEXT        = new Color( 28,  28,  28);
    protected static final Color COLOR_WHITE       = Color.WHITE;
    protected static final Color COLOR_HEADER      = new Color( 22,  68, 145);  // azul escuro
    protected static final Color COLOR_GOLD        = new Color(218, 165,  10);
    protected static final Color COLOR_FOOTER_BG   = new Color(218, 218, 192);
    protected static final Color COLOR_CARD_BG     = new Color(255, 255, 250);
    protected static final Color COLOR_CARD_BORDER = new Color(188, 188, 162);
    protected static final Color COLOR_BTN_BACK    = new Color( 72,  95, 118);  // cinza-azul para "Menu"

    protected static final Font FONT_TITLE    = new Font("Arial", Font.BOLD,  32);
    protected static final Font FONT_SUBTITLE = new Font("Arial", Font.BOLD,  22);
    protected static final Font FONT_BODY     = new Font("Arial", Font.PLAIN, 20);
    protected static final Font FONT_BUTTON   = new Font("Arial", Font.BOLD,  20);
    protected static final Font FONT_EXERCISE = new Font("Courier New", Font.BOLD, 28);
    protected static final Font FONT_INPUT    = new Font("Arial", Font.PLAIN, 24);
    protected static final Font FONT_SMALL    = new Font("Arial", Font.PLAIN, 15);
    protected static final Font FONT_HEARTS   = new Font("Dialog", Font.BOLD, 24);
    protected static final Font FONT_STARS    = new Font("Dialog", Font.BOLD, 48);
    protected static final Font FONT_INFO     = new Font("Arial", Font.BOLD,  18);

    public BasePanel(TypingGame game) {
        this.game = game;
        setBackground(COLOR_BG);
        initialize();
    }

    protected abstract void initialize();

    protected JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        // Força renderizacao Java pura para setBackground funcionar em qualquer OS
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btn.setFont(FONT_BUTTON);
        btn.setBackground(bg);
        btn.setForeground(COLOR_WHITE);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(220, 52));

        // Hover: 15% mais claro para ficar convidativo
        float[] hsb = Color.RGBtoHSB(bg.getRed(), bg.getGreen(), bg.getBlue(), null);
        Color hover = Color.getHSBColor(hsb[0], Math.max(0f, hsb[1] - 0.10f), Math.min(1f, hsb[2] + 0.12f));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(bg);    }
        });
        return btn;
    }

    protected JLabel label(String text, Font font, Color color, int align) {
        JLabel lbl = new JLabel(text, align);
        lbl.setFont(font);
        lbl.setForeground(color);
        return lbl;
    }

    protected JPanel buildHeader(String title) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_HEADER);
        header.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));
        JLabel lbl = label(title, FONT_TITLE, COLOR_WHITE, SwingConstants.CENTER);
        header.add(lbl, BorderLayout.CENTER);
        return header;
    }

    protected String heartsString(int lives) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Player.MAX_LIVES; i++) {
            sb.append(i < lives ? "♥ " : "♡ ");
        }
        return sb.toString().trim();
    }

    protected String starsString(int stars) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            sb.append(i < stars ? "★ " : "☆ ");
        }
        return sb.toString().trim();
    }
}
