package frontend.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/** Panel dengan sudut membulat dan border tipis. */
public class RoundedPanel extends JPanel {

    private final int radius;
    private Color fill;
    private Color border;

    public RoundedPanel(LayoutManager layout, int radius, Color fill, Color border) {
        super(layout);
        this.radius = radius;
        this.fill = fill;
        this.border = border;
        setOpaque(false);
    }

    public RoundedPanel(LayoutManager layout) {
        this(layout, 12, Color.WHITE, null);
    }

    public void setFill(Color fill) {
        this.fill = fill;
        repaint();
    }

    public void setBorderColor(Color border) {
        this.border = border;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (fill != null) {
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        }
        if (border != null) {
            g2.setColor(border);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}