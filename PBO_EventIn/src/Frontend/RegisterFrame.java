package frontend;

import backend.AuthDAO;
import backend.ConnectionDB;
import frontend.components.RoundedButton;
import frontend.components.RoundedPanel;
import frontend.components.RoundedPasswordField;
import frontend.components.RoundedTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import model.Admin;
import model.Ukm;

public class RegisterFrame extends JFrame {

    private final RoundedTextField namaField = new RoundedTextField("Nama lengkap admin");
    private final RoundedTextField nimField = new RoundedTextField("NIM / NIP / NIK (opsional)");
    private final RoundedTextField emailField = new RoundedTextField("admin@polinema.ac.id");
    private final RoundedTextField hpField = new RoundedTextField("08xxxxxxxxxx");
    private final JComboBox<Ukm> ukmCombo = Ui.comboObjects();
    private final RoundedPasswordField passwordField = new RoundedPasswordField("Minimal 8 karakter");
    private final RoundedPasswordField confirmField = new RoundedPasswordField("Ulangi kata sandi");
    private final JLabel errorLabel = Ui.label(" ", Theme.regular(12), Theme.DANGER_FG);
    private final RoundedButton submit = RoundedButton.primary("Daftar Sekarang");

    public RegisterFrame() {
        setTitle("EventIn - Registrasi Admin");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(720, 720));
        setSize(780, 860);
        setLocationRelativeTo(null);
        setContentPane(buildContent());
        loadUkm();
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(Theme.BG);
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel card = new JPanel();
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(600, 760));
        card.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel back = Ui.label("← Kembali ke Login", Theme.bold(12), Theme.PRIMARY);
        back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        back.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });
        top.add(back, BorderLayout.WEST);
        card.add(top);
        card.add(Box.createVerticalStrut(14));

        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brand.setOpaque(false);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        brand.add(LoginFrame.logo(36));
        JPanel brandText = new JPanel();
        brandText.setOpaque(false);
        brandText.setLayout(new BoxLayout(brandText, BoxLayout.Y_AXIS));
        brandText.add(Ui.label("EventIn", Theme.bold(17), Theme.TEXT));
        brandText.add(Ui.label("Registrasi Admin Organisasi", Theme.regular(10), Theme.MUTED));
        brand.add(brandText);
        card.add(brand);
        card.add(Box.createVerticalStrut(22));

        card.add(Ui.label("Daftar sebagai Admin", Theme.bold(25), Theme.TEXT));
        card.add(Box.createVerticalStrut(5));
        card.add(Ui.label("<html>Akun admin terhubung ke satu UKM dan dapat mengelola event UKM tersebut.</html>",
                Theme.regular(12), Theme.MUTED));
        card.add(Box.createVerticalStrut(20));

        card.add(Ui.formField("Nama Lengkap *", namaField, ""));
        card.add(Box.createVerticalStrut(12));
        card.add(Ui.formField("Nomor Induk (NIM / NIP / NIK)", nimField, ""));
        card.add(Box.createVerticalStrut(12));
        card.add(Ui.formField("Email Admin *", emailField, "Gunakan email aktif yang belum terdaftar."));
        card.add(Box.createVerticalStrut(12));
        card.add(Ui.formField("No. Telepon (WhatsApp)", hpField, ""));
        card.add(Box.createVerticalStrut(12));
        card.add(Ui.formField("Asal Organisasi (UKM) *", ukmCombo, ""));
        card.add(Box.createVerticalStrut(12));
        card.add(Ui.formField("Kata Sandi *", passwordField, "Minimal 8 karakter."));
        card.add(Box.createVerticalStrut(12));
        card.add(Ui.formField("Ulangi Kata Sandi *", confirmField, ""));

        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(Box.createVerticalStrut(10));
        card.add(errorLabel);
        card.add(Box.createVerticalStrut(6));

        submit.setAlignmentX(Component.LEFT_ALIGNMENT);
        submit.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        submit.setPreferredSize(new Dimension(560, 44));
        submit.addActionListener(e -> doRegister());
        card.add(submit);
        card.add(Box.createVerticalStrut(10));
        card.add(Ui.hint("Dengan mendaftar, akun Anda langsung memiliki role admin di database."));

        JScrollPane scroll = new JScrollPane(card);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        scroll.setBackground(Color.WHITE);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setPreferredSize(new Dimension(640, 780));

        RoundedPanel holder = new RoundedPanel(new BorderLayout(), 14, Color.WHITE, Theme.BORDER);
        holder.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        holder.add(scroll, BorderLayout.CENTER);
        root.add(holder);
        return root;
    }

    private void loadUkm() {
        ukmCombo.removeAllItems();
        ukmCombo.addItem(new Ukm(-1, "Memuat data UKM...", ""));
        ukmCombo.setEnabled(false);
        Async.run(() -> new AuthDAO().listUkm(), (List<Ukm> list) -> {
            ukmCombo.removeAllItems();
            for (Ukm u : list) {
                ukmCombo.addItem(u);
            }
            ukmCombo.setEnabled(true);
        }, ex -> {
            ukmCombo.removeAllItems();
            errorLabel.setText("Gagal memuat UKM: " + ex.getMessage());
        });
    }

    private void doRegister() {
        String nama = namaField.getText().trim();
        String nim = nimField.getText().trim();
        String email = emailField.getText().trim();
        String hp = hpField.getText().trim();
        String pass = new String(passwordField.getPassword());
        String confirm = new String(confirmField.getPassword());
        Object selected = ukmCombo.getSelectedItem();

        if (nama.isEmpty()) {
            errorLabel.setText("Nama lengkap wajib diisi.");
            return;
        }
        if (email.isEmpty() || !email.contains("@")) {
            errorLabel.setText("Email tidak valid.");
            return;
        }
        if (!(selected instanceof Ukm) || ((Ukm) selected).getId() <= 0) {
            errorLabel.setText("Pilih organisasi (UKM) terlebih dahulu.");
            return;
        }
        if (pass.length() < 8) {
            errorLabel.setText("Kata sandi minimal 8 karakter.");
            return;
        }
        if (!pass.equals(confirm)) {
            errorLabel.setText("Konfirmasi kata sandi tidak sama.");
            return;
        }

        errorLabel.setText(" ");
        submit.setEnabled(false);
        submit.setText("Menyimpan...");
        int idUkm = ((Ukm) selected).getId();
        Async.run(() -> new AuthDAO().register(nama, nim, email, hp, idUkm, pass), (Admin admin) -> {
            submit.setEnabled(true);
            submit.setText("Daftar Sekarang");
            Ui.success(this, "Registrasi admin berhasil. Silakan login dengan akun baru Anda.");
            new LoginFrame().setVisible(true);
            dispose();
        }, ex -> {
            submit.setEnabled(true);
            submit.setText("Daftar Sekarang");
            errorLabel.setText(ex.getMessage());
        });
    }
}