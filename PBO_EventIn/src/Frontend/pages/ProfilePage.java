package frontend.pages;

import backend.AuthDAO;
import backend.ProfileDAO;
import backend.Session;
import frontend.Async;
import frontend.MainFrame;
import frontend.Theme;
import frontend.Ui;
import frontend.components.Avatar;
import frontend.components.BadgeLabel;
import frontend.components.RoundedButton;
import frontend.components.RoundedPanel;
import frontend.components.RoundedTextField;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import model.Admin;
import model.Ukm;

public class ProfilePage extends JPanel implements Refreshable {

    private final Admin admin;
    private final MainFrame mainFrame;

    private final RoundedTextField namaField = new RoundedTextField("Nama lengkap");
    private final RoundedTextField nimField = new RoundedTextField("NIM / NIP / NIK");
    private final RoundedTextField emailField = new RoundedTextField("admin@polinema.ac.id");
    private final RoundedTextField hpField = new RoundedTextField("08xxxxxxxxxx");
    private final JComboBox<Ukm> ukmCombo = Ui.comboObjects();
    private final JLabel errorLabel = Ui.label(" ", Theme.regular(12), Theme.DANGER_FG);
    private final RoundedButton simpan = RoundedButton.primary("Simpan Perubahan");
    private final Avatar avatar;
    private final JLabel nameLabel;
    private final JLabel roleLabel;

    public ProfilePage(Admin admin, MainFrame mainFrame) {
        this.admin = admin;
        this.mainFrame = mainFrame;
        this.avatar = new Avatar(admin.getInisial(), 72, Theme.PRIMARY_SOFT, Theme.PRIMARY_DARK);
        this.nameLabel = Ui.label(admin.getNama(), Theme.bold(18), Theme.TEXT);
        this.roleLabel = Ui.label(admin.getSingkatanUkm() + " · ADMIN", Theme.regular(11), Theme.MUTED);
        setLayout(new BorderLayout());
        setBackground(Theme.BG);

        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setOpaque(false);
        body.add(buildIdentityCard(), BorderLayout.NORTH);
        body.add(buildFormCard(), BorderLayout.CENTER);
        body.add(buildInfoCard(), BorderLayout.SOUTH);

        add(Ui.page("Pengaturan Profil", "Perbarui data akun admin dan organisasi yang Anda kelola.",
                null, body), BorderLayout.CENTER);

        fill(admin);
    }

    private JPanel buildIdentityCard() {
        RoundedPanel card = new RoundedPanel(new BorderLayout(16, 0), 12, Theme.WHITE, Theme.BORDER);
        card.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        left.setOpaque(false);
        left.add(avatar);
        JPanel texts = new JPanel();
        texts.setOpaque(false);
        texts.setLayout(new BoxLayout(texts, BoxLayout.Y_AXIS));
        texts.add(nameLabel);
        texts.add(Box.createVerticalStrut(4));
        texts.add(roleLabel);
        texts.add(Box.createVerticalStrut(6));
        JPanel badge = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        badge.setOpaque(false);
        badge.add(new BadgeLabel("AKUN ADMIN", Theme.INFO_BG, Theme.INFO_FG));
        texts.add(badge);
        left.add(texts);
        card.add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        JLabel id = Ui.label("ID Pengguna #" + admin.getIdPengguna(), Theme.regular(11), Theme.MUTED_LIGHT);
        right.add(id);
        card.add(right, BorderLayout.EAST);
        return card;
    }

    private JPanel buildFormCard() {
        RoundedPanel card = Ui.card(new BorderLayout(0, 14));

        JPanel head = new JPanel();
        head.setOpaque(false);
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        head.add(Ui.label("Data Admin", Theme.bold(15), Theme.TEXT));
        head.add(Box.createVerticalStrut(3));
        head.add(Ui.label("Perubahan langsung tersimpan ke tabel pengguna dan admin_ukm.",
                Theme.regular(11), Theme.MUTED));
        card.add(head, BorderLayout.NORTH);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        form.add(Ui.formField("Nama Lengkap *", namaField, ""));
        form.add(Box.createVerticalStrut(12));

        JPanel two = new JPanel(new GridLayout(1, 2, 14, 0));
        two.setOpaque(false);
        two.add(Ui.formField("NIM / NIP", nimField, ""));
        two.add(Ui.formField("No. Telepon", hpField, ""));
        two.setAlignmentX(Component.LEFT_ALIGNMENT);
        two.setMaximumSize(new Dimension(Integer.MAX_VALUE, 94));
        form.add(two);
        form.add(Box.createVerticalStrut(12));

        form.add(Ui.formField("Email Admin *", emailField, "Email dipakai untuk login."));
        form.add(Box.createVerticalStrut(12));
        form.add(Ui.formField("Organisasi (UKM) *", ukmCombo, "Admin hanya mengelola event UKM ini."));
        form.add(Box.createVerticalStrut(10));

        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(errorLabel);
        form.add(Box.createVerticalStrut(6));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.setAlignmentX(Component.LEFT_ALIGNMENT);
        RoundedButton reset = RoundedButton.secondary("Batalkan Perubahan");
        reset.addActionListener(e -> fill(admin));
        simpan.addActionListener(e -> save());
        actions.add(reset);
        actions.add(simpan);
        form.add(actions);

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildInfoCard() {
        RoundedPanel card = new RoundedPanel(new BorderLayout(12, 0), 12, Theme.SOFT, null);
        card.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
        card.add(Ui.label("<html><b>Catatan keamanan:</b> kata sandi tidak dapat diubah dari "
                + "halaman ini. Hubungi pengelola database untuk reset kata sandi.</html>",
                Theme.regular(11), Theme.MUTED), BorderLayout.CENTER);
        return card;
    }

    private void fill(Admin a) {
        namaField.setText(a.getNama());
        nimField.setText(a.getNim());
        emailField.setText(a.getEmail());
        hpField.setText(a.getNoHp());
    }

    private void save() {
        String nama = namaField.getText().trim();
        String email = emailField.getText().trim();
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

        errorLabel.setText(" ");
        simpan.setEnabled(false);
        simpan.setText("Menyimpan...");
        int idUkm = ((Ukm) selected).getId();
        String nim = nimField.getText().trim();
        String hp = hpField.getText().trim();

        Async.run(() -> new ProfileDAO().update(admin, nama, nim, email, hp, idUkm), updated -> {
            simpan.setEnabled(true);
            simpan.setText("Simpan Perubahan");
            if (updated == null) {
                errorLabel.setText("Gagal memuat ulang data admin.");
                return;
            }
            Session.setAdmin(updated);
            copy(updated);
            nameLabel.setText(updated.getNama());
            roleLabel.setText(updated.getSingkatanUkm() + " · ADMIN");
            avatar.setInitials(updated.getInisial());
            if (mainFrame != null) {
                mainFrame.updateAdminHeader(updated);
            }
            Ui.success(this, "Profil berhasil diperbarui.");
        }, ex -> {
            simpan.setEnabled(true);
            simpan.setText("Simpan Perubahan");
            errorLabel.setText(ex.getMessage());
        });
    }

    private void copy(Admin updated) {
        admin.setNama(updated.getNama());
        admin.setEmail(updated.getEmail());
        admin.setNim(updated.getNim());
        admin.setNoHp(updated.getNoHp());
        admin.setIdUkm(updated.getIdUkm());
        admin.setNamaUkm(updated.getNamaUkm());
        admin.setSingkatanUkm(updated.getSingkatanUkm());
    }

    @Override
    public void refreshData() {
        Async.run(() -> new AuthDAO().listUkm(), (List<Ukm> list) -> {
            ukmCombo.removeAllItems();
            for (Ukm u : list) {
                ukmCombo.addItem(u);
            }
            for (int i = 0; i < ukmCombo.getItemCount(); i++) {
                if (ukmCombo.getItemAt(i).getId() == admin.getIdUkm()) {
                    ukmCombo.setSelectedIndex(i);
                    break;
                }
            }
        }, ex -> errorLabel.setText("Gagal memuat UKM: " + ex.getMessage()));
    }
}