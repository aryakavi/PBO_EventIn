package frontend.components;

import frontend.Theme;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.BorderFactory;
import javax.swing.JTextField;

public class RoundedTextField extends JTextField {

    private final String placeholder;
    private final int radius = 9;

    public RoundedTextField(String placeholder) {
        this.placeholder = placeholder;
        setOpaque(false);
        setFont(Theme.regular(13));
        setForeground(Theme.TEXT);
        setCaretColor(Theme.TEXT);
        setBorder(BorderFactory.createEmptyBorder(0, 13, 0, 13));
        setSelectionColor(Theme.PRIMARY_SOFT);
        setSelectedTextColor(Theme.TEXT);
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                repaint();
            }
        });
    }

    public RoundedTextField() {
        this("");
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        return new Dimension(d.width, Math.max(d.height, 40));
    }

    @Override
    public Insets getInsets() {
        return new Insets(10, 13, 10, 13);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(isEnabled() ? Color.WHITE : Theme.SOFT);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        g2.setColor(isFocusOwner() ? Theme.PRIMARY : Theme.BORDER);
        g2.setStroke(new BasicStroke(isFocusOwner() ? 1.6f : 1f));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        g2.dispose();

        super.paintComponent(g);

        if (getText().isEmpty() && placeholder != null && !placeholder.isEmpty() && !isFocusOwner()) {
            Graphics2D gp = (Graphics2D) g.create();
            gp.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            gp.setColor(Theme.MUTED_LIGHT);
            gp.setFont(getFont());
            Insets in = getInsets();
            int y = (getHeight() - gp.getFontMetrics().getHeight()) / 2 + gp.getFontMetrics().getAscent();
            gp.drawString(placeholder, in.left, y);
            gp.dispose();
        }
    }
}