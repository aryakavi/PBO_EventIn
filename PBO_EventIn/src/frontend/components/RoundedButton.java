package frontend.components;

import frontend.Theme;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.JButton;

public class RoundedButton extends JButton {

    private final int radius;
    private Color bg;
    private Color hover;
    private Color pressed;
    private Color borderColor;

    public RoundedButton(String text, Color bg, Color fg, Color borderColor) {
        super(text);
        this.radius = 9;
        this.bg = bg;
        this.hover = blend(bg, Color.BLACK, 0.10);
        this.pressed = blend(bg, Color.BLACK, 0.18);
        this.borderColor = borderColor;
        setForeground(fg);
        setFont(Theme.bold(13));
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setMargin(new Insets(9, 18, 9, 18));
        setRolloverEnabled(true);
    }

    public static RoundedButton primary(String text) {
        return new RoundedButton(text, Theme.PRIMARY, Color.WHITE, null);
    }

    public static RoundedButton secondary(String text) {
        return new RoundedButton(text, Color.WHITE, Theme.TEXT, Theme.BORDER);
    }

    public static RoundedButton danger(String text) {
        return new RoundedButton(text, Theme.DANGER_BG, Theme.DANGER_FG, null);
    }

    public static RoundedButton success(String text) {
        return new RoundedButton(text, Theme.SUCCESS_BG, Theme.SUCCESS_FG, null);
    }

    public static RoundedButton ghost(String text) {
        return new RoundedButton(text, Theme.SOFT, Theme.TEXT, null);
    }

    public RoundedButton withFont(Font f) {
        setFont(f);
        return this;
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        return new Dimension(Math.max(d.width, 90), Math.max(d.height, 38));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color fill = bg;
        if (!isEnabled()) {
            fill = blend(bg, Color.WHITE, 0.55);
        } else if (getModel().isPressed()) {
            fill = pressed;
        } else if (getModel().isRollover()) {
            fill = hover;
        }
        g2.setColor(fill);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        }
        g2.dispose();
        super.paintComponent(g);
    }

    private static Color blend(Color a, Color b, double ratio) {
        int r = (int) Math.round(a.getRed() * (1 - ratio) + b.getRed() * ratio);
        int g = (int) Math.round(a.getGreen() * (1 - ratio) + b.getGreen() * ratio);
        int bl = (int) Math.round(a.getBlue() * (1 - ratio) + b.getBlue() * ratio);
        return new Color(Math.min(255, r), Math.min(255, g), Math.min(255, bl));
    }
}