package frontend.pages;

import backend.CategoryDAO;
import backend.EventDAO;
import frontend.Async;
import frontend.Theme;
import frontend.Ui;
import frontend.components.RoundedButton;
import frontend.components.RoundedPanel;
import frontend.components.RoundedTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingUtilities;
import model.Admin;
import model.Category;
import model.EventItem;

public class EventFormDialog extends JDialog {

    private final Admin admin;
    private final EventItem existing;
    private final Runnable onSaved;

    private final RoundedTextField namaField = new RoundedTextField("Nama event resmi");
    private final JComboBox<Category> kategoriCombo = Ui.comboObjects();
    private final RoundedTextField posterField = new RoundedTextField("events/banner.png (opsional)");
    private final JSpinner mulaiSpinner = dateSpinner();
    private final JSpinner selesaiSpinner = dateSpinner();
    private final RoundedTextField kuotaField = new RoundedTextField("150");
    private final RoundedTextField hargaField = new RoundedTextField("75000");
    private final RoundedTextField lokasiField = new RoundedTextField("Aula Gedung C, Politeknik Negeri Malang");
    private final JTextArea deskripsiArea = new JTextArea(4, 20);
    private final JLabel errorLabel = Ui.label(" ", Theme.regular(12), Theme.DANGER_FG);
    private final RoundedButton simpan;

    public EventFormDialog(Window owner, Admin admin, EventItem existing, Runnable onSaved) {
        super(owner, existing == null ? "Buat Event Baru" : "Edit Event", ModalityType.APPLICATION_MODAL);
        this.admin = admin;
        this.existing = existing;
        this.onSaved = onSaved;
        simpan = RoundedButton.primary(existing == null ? "Simpan Event" : "Simpan Perubahan");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(720, 840);
        setMinimumSize(new Dimension(680, 720));
        setLocationRelativeTo(owner);
        setContentPane(buildContent());
        loadCategories();
        if (existing != null) {
            fillForm(existing);
        } else {
            applyDefaults();
        }
    }

    /** Nilai awal yang wajar untuk event baru. */
    private void applyDefaults() {
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
        LocalDateTime mulai = now.plusDays(1).withHour(9).withMinute(0);
        LocalDateTime selesai = mulai.withHour(15);
        mulaiSpinner.setValue(toDate(mulai));
        selesaiSpinner.setValue(toDate(selesai));
        kuotaField.setText("100");
        hargaField.setText("0");
    }

    private static JSpinner dateSpinner() {
        JSpinner spinner = new JSpinner(new SpinnerDateModel());
        spinner.setEditor(new JSpinner.DateEditor(spinner, "dd/MM/yyyy HH:mm"));
        spinner.setFont(Theme.regular(13));
        spinner.setPreferredSize(new Dimension(200, 40));
        return spinner;
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(BorderFactory.createEmptyBorder(22, 24, 14, 24));
        header.add(Ui.label(existing == null ? "Buat Event Baru" : "Edit Event", Theme.bold(21), Theme.TEXT));
        header.add(Box.createVerticalStrut(4));
        header.add(Ui.label(existing == null
                ? "Lengkapi data event yang akan dipublikasikan oleh organisasi Anda."
                : "Ubah data event lalu simpan perubahan.", Theme.regular(12), Theme.MUTED));
        root.add(header, BorderLayout.NORTH);

        JPanel form = new JPanel();
        form.setBackground(Color.WHITE);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(4, 24, 10, 24));

        form.add(Ui.formField("Nama Event *", namaField, ""));
        form.add(Box.createVerticalStrut(12));
        form.add(Ui.formField("Kategori Event *", kategoriCombo, ""));
        form.add(Box.createVerticalStrut(12));
        form.add(Ui.formField("Banner / Poster Event", posterRow(), "Path file pada bucket image_event (opsional)."));
        form.add(Box.createVerticalStrut(12));

        JPanel dates = new JPanel(new GridLayout(1, 2, 14, 0));
        dates.setOpaque(false);
        dates.add(Ui.formField("Tanggal Mulai *", mulaiSpinner, ""));
        dates.add(Ui.formField("Tanggal Selesai", selesaiSpinner, ""));
        dates.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(dates);
        form.add(Box.createVerticalStrut(12));

        JPanel numbers = new JPanel(new GridLayout(1, 2, 14, 0));
        numbers.setOpaque(false);
        numbers.add(Ui.formField("Kapasitas Kursi *", kuotaField, ""));
        numbers.add(Ui.formField("Harga Tiket (Rp) *", hargaField, "Isi 0 untuk event gratis."));
        numbers.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(numbers);
        form.add(Box.createVerticalStrut(12));

        form.add(Ui.formField("Lokasi Event *", lokasiField, ""));
        form.add(Box.createVerticalStrut(12));

        deskripsiArea.setFont(Theme.regular(13));
        deskripsiArea.setForeground(Theme.TEXT);
        deskripsiArea.setLineWrap(true);
        deskripsiArea.setWrapStyleWord(true);
        deskripsiArea.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        JScrollPane descScroll = new JScrollPane(deskripsiArea);
        descScroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        descScroll.setPreferredSize(new Dimension(10, 100));
        form.add(Ui.formField("Deskripsi Event", descScroll, ""));

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        root.add(scroll, BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);
        return root;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout(0, 8));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER),
                BorderFactory.createEmptyBorder(12, 24, 16, 24)));

        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        footer.add(errorLabel, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);
        RoundedButton batal = RoundedButton.secondary("Batal");
        batal.addActionListener(e -> dispose());
        simpan.addActionListener(e -> save());
        buttons.add(batal);
        buttons.add(simpan);
        footer.add(buttons, BorderLayout.CENTER);
        return footer;
    }

    private JPanel posterRow() {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.add(posterField, BorderLayout.CENTER);
        RoundedButton pilih = RoundedButton.secondary("Pilih...");
        pilih.setPreferredSize(new Dimension(86, 40));
        pilih.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Pilih banner event");
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                posterField.setText("events/" + chooser.getSelectedFile().getName());
            }
        });
        row.add(pilih, BorderLayout.EAST);
        return row;
    }

    private void loadCategories() {
        kategoriCombo.removeAllItems();
        Async.run(() -> new CategoryDAO().list(), (List<Category> list) -> {
            kategoriCombo.removeAllItems();
            for (Category c : list) {
                kategoriCombo.addItem(c);
            }
            if (existing != null) {
                for (int i = 0; i < kategoriCombo.getItemCount(); i++) {
                    Category c = kategoriCombo.getItemAt(i);
                    if (c.getId() == existing.getIdKategori()) {
                        kategoriCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }, ex -> errorLabel.setText("Gagal memuat kategori: " + ex.getMessage()));
    }

    private void fillForm(EventItem e) {
        namaField.setText(e.getNama());
        posterField.setText(e.getPoster());
        kuotaField.setText(String.valueOf(e.getKuota()));
        hargaField.setText(String.format("%.0f", e.getHarga()));
        lokasiField.setText(e.getLokasi());
        deskripsiArea.setText(e.getDeskripsi());
        if (e.getTanggalMulai() != null) {
            mulaiSpinner.setValue(toDate(e.getTanggalMulai()));
        }
        if (e.getTanggalSelesai() != null) {
            selesaiSpinner.setValue(toDate(e.getTanggalSelesai()));
        }
    }

    private Date toDate(LocalDateTime ldt) {
        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }

    private LocalDateTime toLocalDate(Date date) {
        return new Timestamp(date.getTime()).toLocalDateTime();
    }

    private void save() {
        String nama = namaField.getText().trim();
        String lokasi = lokasiField.getText().trim();
        Object cat = kategoriCombo.getSelectedItem();

        if (nama.isEmpty()) {
            errorLabel.setText("Nama event wajib diisi.");
            return;
        }
        if (!(cat instanceof Category)) {
            errorLabel.setText("Pilih kategori event.");
            return;
        }
        if (lokasi.isEmpty()) {
            errorLabel.setText("Lokasi event wajib diisi.");
            return;
        }
        int kuota;
        double harga;
        try {
            kuota = Integer.parseInt(kuotaField.getText().trim().replace(".", "").replace(",", ""));
            if (kuota <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            errorLabel.setText("Kapasitas kursi harus angka lebih dari 0.");
            return;
        }
        try {
            String raw = hargaField.getText().trim().replace("Rp", "").replace(".", "").replace(",", "").trim();
            harga = raw.isEmpty() ? 0 : Double.parseDouble(raw);
            if (harga < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            errorLabel.setText("Harga tiket tidak valid.");
            return;
        }

        LocalDateTime mulai = toLocalDate((Date) mulaiSpinner.getValue());
        LocalDateTime selesai = toLocalDate((Date) selesaiSpinner.getValue());
        if (selesai.isBefore(mulai)) {
            errorLabel.setText("Tanggal selesai tidak boleh sebelum tanggal mulai.");
            return;
        }

        EventItem e = new EventItem();
        e.setIdEvent(existing == null ? 0 : existing.getIdEvent());
        e.setIdKategori(((Category) cat).getId());
        e.setNama(nama);
        e.setDeskripsi(deskripsiArea.getText().trim());
        e.setTanggalMulai(mulai);
        e.setTanggalSelesai(selesai);
        e.setLokasi(lokasi);
        e.setKuota(kuota);
        e.setHarga(harga);
        e.setPoster(posterField.getText().trim());

        errorLabel.setText(" ");
        simpan.setEnabled(false);
        simpan.setText("Menyimpan...");
        Async.run(() -> {
            EventDAO dao = new EventDAO();
            if (existing == null) {
                return dao.create(e, admin.getIdAdminUkm());
            }
            dao.update(e, admin.getIdAdminUkm());
            return e.getIdEvent();
        }, id -> {
            if (onSaved != null) {
                onSaved.run();
            }
            Ui.success(this, existing == null ? "Event berhasil dibuat." : "Perubahan event berhasil disimpan.");
            dispose();
        }, ex -> {
            simpan.setEnabled(true);
            simpan.setText(existing == null ? "Simpan Event" : "Simpan Perubahan");
            errorLabel.setText(ex.getMessage());
        });
    }
}