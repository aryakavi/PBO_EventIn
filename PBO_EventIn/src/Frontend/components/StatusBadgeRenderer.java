package frontend.components;

import frontend.Theme;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/** Renderer kolom status berupa badge berwarna. */
public class StatusBadgeRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
            boolean focus, int row, int column) {
        String text = value == null ? "" : value.toString();
        BadgeLabel badge = new BadgeLabel(text, Theme.statusBg(text), Theme.statusFg(text));

        JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrap.setOpaque(true);
        wrap.setBackground(selected ? table.getSelectionBackground() : Color.WHITE);
        wrap.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 6, 8, 6));
        wrap.add(badge);
        return wrap;
    }
}