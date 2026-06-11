package ui.panels;

import model.Player;
import ui.TypingGame;
import javax.swing.*;
import java.awt.*;

public abstract class BasePanel extends JPanel {

    protected TypingGame game;

    protected static final Color COLOR_BG          = new Color(245, 247, 251); 
    protected static final Color COLOR_PRIMARY     = new Color( 37,  99, 235); // Azul Principal
    protected static final Color COLOR_DARK        = new Color( 30,  58, 138); // Azul Escuro
    protected static final Color COLOR_ACCENT      = new Color( 37,  99, 235); // Azul igual ao texto de "Bem-vindo"
    protected static final Color COLOR_SUCCESS     = new Color( 34, 197,  94); // Verde vibrante
    protected static final Color COLOR_TEXT        = new Color( 31,  41,  55); // Texto escuro
    protected static final Color COLOR_SECONDARY   = new Color(107, 114, 128); // Texto secundário
    protected static final Color COLOR_WHITE       = Color.WHITE;
    protected static final Color COLOR_GOLD        = new Color(250, 204,  21);
    protected static final Color COLOR_DANGER      = new Color(239,  68,  68);
    protected static final Color COLOR_WARNING     = new Color(245, 158,  11);
    protected static final Color COLOR_HEADER      = COLOR_DARK;
    protected static final Color COLOR_CARD_BORDER = new Color(209, 213, 219);
    protected static final Color COLOR_CARD_BG     = Color.WHITE;
    protected static final Color COLOR_FOOTER_BG   = new Color(229, 231, 235);
    protected static final Color COLOR_BTN_BACK    = new Color(75, 85, 99);

    protected static final Font FONT_TITLE    = new Font("Poppins SemiBold", Font.BOLD, 28);
    protected static final Font FONT_SUBTITLE = new Font("Poppins SemiBold", Font.BOLD, 22);
    protected static final Font FONT_BODY     = new Font("Poppins", Font.PLAIN, 16);
    protected static final Font FONT_BUTTON   = new Font("Poppins SemiBold", Font.BOLD, 18);
    protected static final Font FONT_EXERCISE = new Font("Poppins", Font.PLAIN, 28);
    protected static final Font FONT_INPUT    = new Font("Poppins", Font.PLAIN, 24);
    protected static final Font FONT_SMALL    = new Font("Poppins", Font.PLAIN, 14);
    protected static final Font FONT_HEARTS   = new Font(Font.DIALOG, Font.BOLD, 24);
    protected static final Font FONT_INFO     = new Font("Poppins SemiBold", Font.BOLD, 18);
    protected static final Font FONT_STARS    = new Font(Font.DIALOG, Font.BOLD, 48);
    protected static final Font FONT_TIMER_BIG = new Font("Poppins SemiBold", Font.BOLD, 36);

    public BasePanel(TypingGame game) {
        this.game = game;
        setBackground(COLOR_BG);
        initialize();
    }

    protected abstract void initialize();

    protected static class RoundedPanel extends JPanel {
        private int radius;
        private Color shadowColor = new Color(0, 0, 0, 20);

        public RoundedPanel(int radius, Color bg) {
            this.radius = radius;
            setBackground(bg);
            setOpaque(false);
        }

        @Override
        public void paint(Graphics g) {
            // Forçamos o componente a não ser opaco durante todo o ciclo de renderização
            super.paint(g);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Desenha a sombra com um pequeno recuo para garantir que não seja cortada
            g2.setColor(shadowColor);
            g2.fillRoundRect(3, 3, getWidth() - 7, getHeight() - 7, radius, radius);
            
            // Desenha o corpo do painel
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, radius, radius);
            g2.dispose();
        }
    }

    protected static class GradientPanel extends JPanel {
        private Color color1, color2;
        private boolean horizontal;

        public GradientPanel(Color c1, Color c2, boolean horizontal) {
            this.color1 = c1; this.color2 = c2; this.horizontal = horizontal;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp = horizontal ? new GradientPaint(0, 0, color1, getWidth(), 0, color2)
                                          : new GradientPaint(0, 0, color1, 0, getHeight(), color2);
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }

    protected JButton createModernButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) g2.setColor(bg.darker());
                else if (getModel().isRollover()) g2.setColor(bg.brighter());
                else g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BUTTON);
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
            sb.append(i < lives ? "\u2764 " : "\u2661 ");
        }
        return sb.toString().trim();
    }

    protected String starsString(int stars) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            sb.append(i < stars ? "\u2605 " : "\u2606 ");
        }
        return sb.toString().trim();
    }
}
