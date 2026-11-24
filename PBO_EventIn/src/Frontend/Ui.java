package frontend;

import frontend.components.RoundedPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/** Kumpulan helper UI agar seluruh halaman terlihat konsisten. */
public final class Ui {

    private Ui() {
    }

    public static JLabel label(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        return l;
    }

    public static JLabel title(String text) {
        return label(text, Theme.bold(26), Theme.TEXT);
    }

    public static JLabel subtitle(String text) {
        return label(text, Theme.regular(13), Theme.MUTED);
    }

    public static JLabel hint(String text) {
        return label(text, Theme.regular(11), Theme.MUTED_LIGHT);
    }

    public static RoundedPanel card() {
        return card(new BorderLayout());
    }

    public static RoundedPanel card(LayoutManager layout) {
        RoundedPanel p = new RoundedPanel(layout, 12, Theme.WHITE, Theme.BORDER);
        p.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        return p;
    }

    public static JPanel page(String title, String subtitle, JComponent action, JComponent body) {
        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(Theme.BG);
        root.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setOpaque(false);
        JPanel texts = new JPanel();
        texts.setOpaque(false);
        texts.setLayout(new BoxLayout(texts, BoxLayout.Y_AXIS));
        texts.add(title(title));
        texts.add(Box.createVerticalStrut(4));
        texts.add(subtitle(subtitle));
        header.add(texts, BorderLayout.CENTER);
        if (action != null) {
            JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            right.setOpaque(false);
            right.add(action);
            header.add(right, BorderLayout.EAST);
        }
        root.add(header, BorderLayout.NORTH);
        if (body != null) {
            root.add(body, BorderLayout.CENTER);
        }
        return root;
    }

    public static RoundedPanel kpiCard(String title, String value, String note, boolean positive) {
        RoundedPanel p = new RoundedPanel(new BorderLayout(0, 6), 12, Theme.WHITE, Theme.BORDER);
        p.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        p.add(label(title, Theme.regular(12), Theme.MUTED), BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(label(value, Theme.bold(26), Theme.TEXT));
        center.add(Box.createVerticalStrut(4));
        JLabel trend = label(note, Theme.regular(11), positive ? Theme.PRIMARY : Theme.MUTED);
        center.add(trend);
        p.add(center, BorderLayout.CENTER);
        return p;
    }

    public static JPanel kpiRow(int gap) {
        JPanel row = new JPanel(new GridLayout(1, 4, gap, 0));
        row.setOpaque(false);
        return row;
    }

    public static JPanel formField(String labelText, JComponent field, String hintText) {
        JPanel box = new JPanel();
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.add(label(labelText, Theme.bold(12), Theme.TEXT));
        box.add(Box.createVerticalStrut(6));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.add(field);
        if (hintText != null && !hintText.isEmpty()) {
            box.add(Box.createVerticalStrut(5));
            box.add(hint(hintText));
        }
        box.setAlignmentX(Component.LEFT_ALIGNMENT);
        return box;
    }

    public static JComboBox<String> combo(String... items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(Theme.regular(13));
        cb.setForeground(Theme.TEXT);
        cb.setBackground(Color.WHITE);
        cb.setPreferredSize(new Dimension(160, 40));
        cb.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        return cb;
    }

    public static <T> JComboBox<T> comboObjects() {
        JComboBox<T> cb = new JComboBox<>();
        cb.setFont(Theme.regular(13));
        cb.setForeground(Theme.TEXT);
        cb.setBackground(Color.WHITE);
        cb.setPreferredSize(new Dimension(240, 40));
        cb.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        return cb;
    }

    public static JPanel bar() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        return p;
    }

    public static JPanel column() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        return p;
    }

    public static DefaultTableModel readOnlyModel(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    public static void styleTable(JTable table, int[] widths) {
        table.setRowHeight(46);
        table.setFont(Theme.regular(13));
        table.setForeground(Theme.TEXT);
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(new Color(0xF1, 0xF5, 0xF9));
        table.setSelectionForeground(Theme.TEXT);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(0xF1, 0xF5, 0xF9));
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        if (table.getRowSorter() == null) {
            table.setAutoCreateRowSorter(true);
        }

        JTableHeader header = table.getTableHeader();
        header.setFont(Theme.bold(12));
        header.setForeground(new Color(0x47, 0x55, 0x69));
        header.setBackground(new Color(0xF8, 0xFA, 0xFC));
        header.setPreferredSize(new Dimension(10, 42));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER));
        header.setReorderingAllowed(false);

        if (widths != null) {
            for (int i = 0; i < widths.length && i < table.getColumnCount(); i++) {
                table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
            }
        }
    }

    public static JScrollPane tableScroll(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        sp.setBackground(Color.WHITE);
        JViewport vp = sp.getViewport();
        vp.setBackground(Color.WHITE);
        sp.getVerticalScrollBar().setUnitIncrement(14);
        return sp;
    }

    public static JPanel badgeRow(String text, Color bg, Color fg) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setOpaque(false);
        p.add(new frontend.components.BadgeLabel(text, bg, fg));
        return p;
    }

    public static void info(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Informasi", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void error(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Terjadi Kesalahan", JOptionPane.ERROR_MESSAGE);
    }

    public static void success(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Berhasil", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "Konfirmasi",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }

    public static String number(int value) {
        return String.format("%,d", value).replace(',', '.');
    }
}