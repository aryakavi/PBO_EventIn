package frontend;

import backend.AuthDAO;
import backend.ConnectionDB;
import backend.Session;
import frontend.components.RoundedButton;
import frontend.components.RoundedPanel;
import frontend.components.RoundedPasswordField;
import frontend.components.RoundedTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import model.Admin;

public class LoginFrame extends JFrame {

    private final RoundedTextField emailField = new RoundedTextField("admin@polinema.ac.id");
    private final RoundedPasswordField passwordField = new RoundedPasswordField("Minimal 8 karakter");
    private final JLabel errorLabel = Ui.label(" ", Theme.regular(12), Theme.DANGER_FG);
    private final RoundedButton loginButton = RoundedButton.primary("Masuk Sekarang");
    private final JLabel dbStatus = Ui.label("Menghubungkan database...", Theme.regular(11), Theme.MUTED_LIGHT);
    private final JLabel statAdminValue = Ui.label("-", Theme.bold(22), Color.WHITE);
    private final JLabel statEventValue = Ui.label("-", Theme.bold(22), Color.WHITE);

    public LoginFrame() {
        setTitle("EventIn - Login Admin");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1040, 700));
        setSize(1180, 760);
        setLocationRelativeTo(null);
        setContentPane(buildContent());
        checkDatabase();
        loadStats();
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new GridLayout(1, 2));
        root.add(buildFormSide());
        root.add(buildBrandSide());
        return root;
    }

    private JPanel buildFormSide() {
        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setBackground(Color.WHITE);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setPreferredSize(new Dimension(380, 560));

        form.add(brandRow());
        form.add(Box.createVerticalStrut(34));
        form.add(Ui.label("Masuk ke EventIn", Theme.bold(27), Theme.TEXT));
        form.add(Box.createVerticalStrut(6));
        JLabel formDesc = Ui.label("<html>Kelola event, verifikasi pembayaran, dan check-in tiket "
                + "dari satu dashboard admin.</html>", Theme.regular(13), Theme.MUTED);
        formDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        formDesc.setPreferredSize(new Dimension(380, 46));
        formDesc.setMaximumSize(new Dimension(380, 46));
        form.add(formDesc);
        form.add(Box.createVerticalStrut(26));

        JLabel emailLabel = Ui.label("Email Admin", Theme.bold(12), Theme.TEXT);
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(emailLabel);
        form.add(Box.createVerticalStrut(6));
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        form.add(emailField);
        form.add(Box.createVerticalStrut(16));

        JPanel passwordHeader = new JPanel(new BorderLayout());
        passwordHeader.setOpaque(false);
        passwordHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        passwordHeader.add(Ui.label("Kata Sandi", Theme.bold(12), Theme.TEXT), BorderLayout.WEST);
        passwordHeader.add(Ui.label("Lupa kata sandi?", Theme.regular(11), Theme.PRIMARY), BorderLayout.EAST);
        form.add(passwordHeader);
        form.add(Box.createVerticalStrut(6));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        form.add(passwordField);

        JPanel options = new JPanel(new BorderLayout());
        options.setOpaque(false);
        options.setAlignmentX(Component.LEFT_ALIGNMENT);
        options.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        JCheckBox show = new JCheckBox("Lihat kata sandi");
        show.setOpaque(false);
        show.setFont(Theme.regular(11));
        show.setForeground(Theme.MUTED);
        show.setFocusPainted(false);
        show.addActionListener(e -> passwordField.setEchoChar(show.isSelected() ? (char) 0 : '\u2022'));
        options.add(show, BorderLayout.WEST);
        form.add(options);

        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(errorLabel);
        form.add(Box.createVerticalStrut(6));

        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        loginButton.setPreferredSize(new Dimension(380, 44));
        loginButton.addActionListener(e -> doLogin());
        form.add(loginButton);
        form.add(Box.createVerticalStrut(18));

        JPanel registerRow = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 4, 0));
        registerRow.setOpaque(false);
        registerRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerRow.add(Ui.label("Belum memiliki akun admin?", Theme.regular(12), Theme.MUTED));
        JLabel register = Ui.label("Daftar sebagai Admin", Theme.bold(12), Theme.PRIMARY);
        register.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        register.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new RegisterFrame().setVisible(true);
                dispose();
            }
        });
        registerRow.add(register);
        form.add(registerRow);
        form.add(Box.createVerticalStrut(22));

        JPanel statusRow = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 6, 0));
        statusRow.setOpaque(false);
        statusRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel dot = Ui.label("●", Theme.regular(10), Theme.MUTED_LIGHT);
        statusRow.add(dot);
        statusRow.add(dbStatus);
        form.add(statusRow);

        wrap.add(form);
        return wrap;
    }

    private JPanel brandRow() {
        JPanel row = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.add(logo(40));
        JPanel texts = new JPanel();
        texts.setOpaque(false);
        texts.setLayout(new BoxLayout(texts, BoxLayout.Y_AXIS));
        texts.add(Ui.label("EventIn", Theme.bold(19), Theme.TEXT));
        texts.add(Ui.label("Dashboard Penyelenggara", Theme.regular(10), Theme.MUTED));
        row.add(texts);
        return row;
    }

    static JPanel logo(int size) {
        JPanel logo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.PRIMARY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(Color.WHITE);
                g2.setFont(Theme.bold(Math.round(size * 0.42f)));
                String text = "EI";
                int w = g2.getFontMetrics().stringWidth(text);
                int y = (getHeight() - g2.getFontMetrics().getHeight()) / 2 + g2.getFontMetrics().getAscent();
                g2.drawString(text, (getWidth() - w) / 2, y);
                g2.dispose();
            }
        };
        logo.setOpaque(false);
        logo.setPreferredSize(new Dimension(size, size));
        logo.setMinimumSize(new Dimension(size, size));
        logo.setMaximumSize(new Dimension(size, size));
        return logo;
    }

    private JPanel buildBrandSide() {
        JPanel side = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                float[] fractions = {0f, 1f};
                Color[] colors = {new Color(0x0F, 0x17, 0x2A), new Color(0x1E, 0x1B, 0x4B)};
                g2.setPaint(new LinearGradientPaint(0, 0, getWidth(), getHeight(), fractions, colors));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(249, 115, 22, 38));
                g2.fillOval(getWidth() - 260, -90, 380, 380);
                g2.setColor(new Color(255, 255, 255, 14));
                g2.fillOval(-120, getHeight() - 260, 360, 360);
                g2.dispose();
            }
        };
        side.setLayout(new GridBagLayout());
        side.setBorder(BorderFactory.createEmptyBorder(52, 48, 52, 48));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel pill = Ui.label("  AUTENTIKASI ADMIN  ", Theme.bold(10), Theme.PRIMARY);
        pill.setOpaque(false);
        pill.setBorder(BorderFactory.createLineBorder(new Color(249, 115, 22, 140)));
        pill.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(pill);
        content.add(Box.createVerticalStrut(22));

        JLabel heading = Ui.label("<html>Satu dashboard<br>untuk semua event.</html>",
                Theme.bold(30), Color.WHITE);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(heading);
        content.add(Box.createVerticalStrut(12));

        JLabel desc = Ui.label("<html>Kelola publikasi event, verifikasi bukti pembayaran, "
                + "dan catat kehadiran peserta tanpa scanner QR.</html>",
                Theme.regular(13), new Color(0xCB, 0xD5, 0xE1));
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(desc);
        content.add(Box.createVerticalStrut(30));

        content.add(feature("01", "Kelola Event", "Buat, ubah, dan hapus event organisasi Anda."));
        content.add(Box.createVerticalStrut(14));
        content.add(feature("02", "Verifikasi Pembayaran", "Periksa bukti bayar lalu ubah status tiket."));
        content.add(Box.createVerticalStrut(14));
        content.add(feature("03", "Check-in Kode Tiket", "Masukkan kode tiket manual untuk absensi."));
        content.add(Box.createVerticalStrut(34));

        JPanel stats = new JPanel(new GridLayout(1, 2, 18, 0));
        stats.setOpaque(false);
        stats.setAlignmentX(Component.LEFT_ALIGNMENT);
        stats.add(statBox(statAdminValue, "Akun admin"));
        stats.add(statBox(statEventValue, "Event berjalan"));
        content.add(stats);

        java.awt.GridBagConstraints gc = new java.awt.GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 1;
        gc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gc.anchor = java.awt.GridBagConstraints.WEST;
        side.add(content, gc);
        return side;
    }

    private JPanel feature(String number, String title, String desc) {
        JPanel row = new JPanel(new BorderLayout(14, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));

        JLabel num = Ui.label(number, Theme.bold(11), Theme.PRIMARY);
        num.setHorizontalAlignment(SwingConstants.CENTER);
        num.setPreferredSize(new Dimension(34, 34));
        num.setOpaque(true);
        num.setBackground(new Color(249, 115, 22, 28));
        row.add(num, BorderLayout.WEST);

        JPanel texts = new JPanel();
        texts.setOpaque(false);
        texts.setLayout(new BoxLayout(texts, BoxLayout.Y_AXIS));
        texts.add(Ui.label(title, Theme.bold(13), Color.WHITE));
        texts.add(Ui.label("<html>" + desc + "</html>", Theme.regular(11), new Color(0x94, 0xA3, 0xB8)));
        row.add(texts, BorderLayout.CENTER);
        return row;
    }

    private JPanel statBox(JLabel value, String label) {
        RoundedPanel p = new RoundedPanel(new BorderLayout(0, 4), 10, new Color(255, 255, 255, 18), null);
        p.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        p.add(value, BorderLayout.NORTH);
        p.add(Ui.label(label, Theme.regular(10), new Color(0x94, 0xA3, 0xB8)), BorderLayout.CENTER);
        return p;
    }

    private void loadStats() {
        Async.run(() -> new AuthDAO().publicStats(), stats -> {
            if (stats.length < 2) {
                return;
            }
            statAdminValue.setText(String.valueOf(stats[0]));
            statEventValue.setText(String.valueOf(stats[1]));
        }, ex -> {
            statAdminValue.setText("-");
            statEventValue.setText("-");
        });
    }

    private void doLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Email dan kata sandi wajib diisi.");
            return;
        }
        errorLabel.setText(" ");
        setLoading(true);
        Async.run(() -> new AuthDAO().login(email, password), admin -> {
            setLoading(false);
            if (admin == null) {
                errorLabel.setText("Email atau kata sandi salah, atau akun bukan admin.");
                return;
            }
            if (admin.getIdAdminUkm() <= 0) {
                errorLabel.setText("Akun admin ini belum terhubung ke UKM mana pun.");
                return;
            }
            Session.setAdmin(admin);
            new MainFrame(admin).setVisible(true);
            dispose();
        }, ex -> {
            setLoading(false);
            errorLabel.setText("Gagal terhubung: " + ex.getMessage());
        });
    }

    private void setLoading(boolean loading) {
        loginButton.setEnabled(!loading);
        loginButton.setText(loading ? "Memproses..." : "Masuk Sekarang");
        emailField.setEnabled(!loading);
        passwordField.setEnabled(!loading);
    }

    private void checkDatabase() {
        Async.run(ConnectionDB::testConnection, version -> {
            if (version.startsWith("Gagal")) {
                dbStatus.setText("Gagal terhubung ke database. Periksa koneksi internet.");
                dbStatus.setToolTipText(version);
                dbStatus.setForeground(Theme.DANGER_FG);
            } else {
                dbStatus.setText("Terhubung ke Supabase PostgreSQL");
                dbStatus.setForeground(Theme.SUCCESS_FG);
            }
        }, ex -> {
            dbStatus.setText("Database tidak terjangkau");
            dbStatus.setForeground(Theme.DANGER_FG);
        });
    }
}