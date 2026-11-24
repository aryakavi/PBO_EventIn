package frontend;

import backend.Session;
import frontend.components.Avatar;
import frontend.components.RoundedButton;
import frontend.pages.CheckinPage;
import frontend.pages.DashboardPage;
import frontend.pages.EventPage;
import frontend.pages.PaymentPage;
import frontend.pages.ProfilePage;
import frontend.pages.ReportPage;
import frontend.pages.Refreshable;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import model.Admin;

public class MainFrame extends JFrame {

    private final Admin admin;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel content = new JPanel(cardLayout);
    private final Map<String, JPanel> pages = new LinkedHashMap<>();
    private final Map<String, MenuItem> menuItems = new LinkedHashMap<>();
    private final Avatar headerAvatar;
    private final JLabel headerName;
    private final JLabel headerRole;

    private String activeKey = "dashboard";

    public MainFrame(Admin admin) {
        this.admin = admin;
        setTitle("EventIn - Dashboard Admin");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1180, 720));
        setSize(1360, 860);
        setLocationRelativeTo(null);

        headerAvatar = new Avatar(admin.getInisial(), 36, Theme.PRIMARY_SOFT, Theme.PRIMARY_DARK);
        headerName = Ui.label(admin.getNama(), Theme.bold(12), Theme.TEXT);
        headerRole = Ui.label(admin.getSingkatanUkm() + " · ADMIN", Theme.regular(10), Theme.MUTED);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG);
        root.add(buildHeader(), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(Theme.BG);
        body.add(buildSidebar(), BorderLayout.WEST);
        content.setBackground(Theme.BG);
        body.add(content, BorderLayout.CENTER);
        root.add(body, BorderLayout.CENTER);
        setContentPane(root);

        showPage("dashboard");
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(10, 68));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER));

        JPanel brand = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 0));
        brand.setOpaque(false);
        brand.setPreferredSize(new Dimension(232, 68));
        brand.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 0));
        brand.add(LoginFrame.logo(38));
        JPanel brandText = new JPanel();
        brandText.setOpaque(false);
        brandText.setLayout(new BoxLayout(brandText, BoxLayout.Y_AXIS));
        brandText.add(Ui.label("EventIn", Theme.bold(17), Theme.TEXT));
        brandText.add(Ui.label("Dashboard Penyelenggara", Theme.regular(9), Theme.MUTED));
        brand.add(brandText);
        header.add(brand, BorderLayout.WEST);

        JPanel right = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        right.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 18));

        JPanel account = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 0));
        account.setOpaque(false);
        JPanel accountText = new JPanel();
        accountText.setOpaque(false);
        accountText.setLayout(new BoxLayout(accountText, BoxLayout.Y_AXIS));
        headerName.setAlignmentX(Component.RIGHT_ALIGNMENT);
        headerRole.setAlignmentX(Component.RIGHT_ALIGNMENT);
        accountText.add(headerName);
        accountText.add(headerRole);
        account.add(accountText);
        account.add(headerAvatar);
        right.add(account);

        RoundedButton logout = RoundedButton.secondary("Keluar");
        logout.setPreferredSize(new Dimension(84, 34));
        logout.setFont(Theme.bold(12));
        logout.addActionListener(e -> doLogout());
        right.add(logout);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(Theme.SIDEBAR);
        sidebar.setPreferredSize(new Dimension(232, 10));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(22, 14, 18, 14));

        JLabel section = Ui.label("MENU NAVIGASI", Theme.bold(9), new Color(0x64, 0x74, 0x8B));
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
        sidebar.add(section);
        sidebar.add(Box.createVerticalStrut(12));

        addMenu(sidebar, "dashboard", "D", "Dashboard");
        addMenu(sidebar, "event", "E", "Event Saya");
        addMenu(sidebar, "payment", "P", "Pembayaran");
        addMenu(sidebar, "checkin", "C", "Check-in Tiket");
        addMenu(sidebar, "report", "L", "Laporan");
        addMenu(sidebar, "profile", "S", "Pengaturan");

        sidebar.add(Box.createVerticalGlue());

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setAlignmentX(Component.LEFT_ALIGNMENT);
        info.setBorder(BorderFactory.createEmptyBorder(10, 8, 0, 8));
        info.add(Ui.label("EVENTIN ADMIN", Theme.bold(9), new Color(0x64, 0x74, 0x8B)));
        info.add(Box.createVerticalStrut(4));
        info.add(Ui.label("Terhubung ke Supabase", Theme.regular(10), new Color(0x94, 0xA3, 0xB8)));
        info.add(Ui.label("PostgreSQL 17 - ap-southeast-1", Theme.regular(9), new Color(0x47, 0x55, 0x69)));
        sidebar.add(info);
        return sidebar;
    }

    private void addMenu(JPanel sidebar, String key, String icon, String label) {
        MenuItem item = new MenuItem(key, icon, label);
        item.setAlignmentX(Component.LEFT_ALIGNMENT);
        menuItems.put(key, item);
        sidebar.add(item);
        sidebar.add(Box.createVerticalStrut(4));
    }

    private void showPage(String key) {
        activeKey = key;
        JPanel page = pages.get(key);
        if (page == null) {
            page = createPage(key);
            pages.put(key, page);
            content.add(page, key);
        }
        if (page instanceof Refreshable) {
            ((Refreshable) page).refreshData();
        }
        cardLayout.show(content, key);
        menuItems.forEach((k, item) -> item.setActive(k.equals(key)));
    }

    private JPanel createPage(String key) {
        switch (key) {
            case "event":
                return new EventPage(admin);
            case "payment":
                return new PaymentPage(admin);
            case "checkin":
                return new CheckinPage(admin);
            case "report":
                return new ReportPage(admin);
            case "profile":
                return new ProfilePage(admin, this);
            case "dashboard":
            default:
                return new DashboardPage(admin, this);
        }
    }

    public void openPage(String key) {
        showPage(key);
    }

    /** Dipanggil halaman profil setelah data admin berubah. */
    public void updateAdminHeader(Admin updated) {
        headerName.setText(updated.getNama());
        headerRole.setText(updated.getSingkatanUkm() + " · ADMIN");
        headerAvatar.setInitials(updated.getInisial());
    }

    private void doLogout() {
        if (!Ui.confirm(this, "Keluar dari dashboard admin?")) {
            return;
        }
        Session.clear();
        new LoginFrame().setVisible(true);
        dispose();
    }

    private class MenuItem extends JPanel {

        private final String key;
        private final IconBox icon;
        private final JLabel text;
        private boolean active;
        private boolean hover;

        MenuItem(String key, String iconText, String label) {
            this.key = key;
            setLayout(new BorderLayout(12, 0));
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
            setPreferredSize(new Dimension(204, 46));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            icon = new IconBox(iconText);
            add(icon, BorderLayout.WEST);
            text = Ui.label(label, Theme.medium(13), Theme.SIDEBAR_TEXT);
            add(text, BorderLayout.CENTER);

            MouseAdapter handler = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showPage(MenuItem.this.key);
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }
            };
            addMouseListener(handler);
            icon.addMouseListener(handler);
            text.addMouseListener(handler);
            icon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            text.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        void setActive(boolean value) {
            active = value;
            icon.setActive(value);
            text.setForeground(value ? Color.WHITE : Theme.SIDEBAR_TEXT);
            text.setFont(value ? Theme.bold(13) : Theme.medium(13));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (active) {
                g2.setColor(Theme.SIDEBAR_ACTIVE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            } else if (hover) {
                g2.setColor(new Color(255, 255, 255, 10));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class IconBox extends JLabel {

        private boolean active;

        IconBox(String letter) {
            super(letter, SwingConstants.CENTER);
            setFont(Theme.bold(10));
            setForeground(Theme.SIDEBAR_TEXT);
            setOpaque(false);
            setPreferredSize(new Dimension(26, 26));
            setMinimumSize(new Dimension(26, 26));
            setMaximumSize(new Dimension(26, 26));
        }

        void setActive(boolean value) {
            active = value;
            setForeground(value ? Color.WHITE : Theme.SIDEBAR_TEXT);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(active ? Theme.PRIMARY : Theme.SIDEBAR_ACTIVE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            if (active) {
                g2.setColor(new Color(255, 255, 255, 60));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }
}