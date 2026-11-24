package frontend.components;

import frontend.Theme;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class Avatar extends JLabel {

    private Color bg;
    private Color fg;

    public Avatar(String initials, int size, Color bg, Color fg) {
        super(initials, SwingConstants.CENTER);
        this.bg = bg;
        this.fg = fg;
        setFont(Theme.bold(Math.max(11, Math.round(size * 0.36f))));
        setForeground(fg);
        setOpaque(false);
        setPreferredSize(new Dimension(size, size));
        setMinimumSize(new Dimension(size, size));
        setMaximumSize(new Dimension(size, size));
    }

    public void setInitials(String initials) {
        setText(initials);
        repaint();
    }

    public void setColors(Color bg, Color fg) {
        this.bg = bg;
        this.fg = fg;
        setForeground(fg);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(bg);
        g2.fillOval(0, 0, getWidth(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }
}