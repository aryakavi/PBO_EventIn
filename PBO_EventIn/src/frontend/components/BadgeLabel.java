package frontend.components;

import frontend.Theme;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JLabel;

public class BadgeLabel extends JLabel {

    private Color bg;
    private final int radius = 6;

    public BadgeLabel(String text, Color bg, Color fg) {
        super(text);
        this.bg = bg;
        setForeground(fg);
        setFont(Theme.bold(11));
        setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        setOpaque(false);
    }

    public void setColors(Color bg, Color fg) {
        this.bg = bg;
        setForeground(fg);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        g2.dispose();
        super.paintComponent(g);
    }
}