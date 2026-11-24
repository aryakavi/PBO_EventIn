package frontend.pages;

import backend.EventDAO;
import frontend.Async;
import frontend.Theme;
import frontend.Ui;
import frontend.components.RoundedButton;
import frontend.components.StatusBadgeRenderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import model.Admin;
import model.EventItem;
import model.Participant;

public class EventDetailDialog extends JDialog {

    private final Admin admin;
    private final EventItem event;
    private final Runnable onChanged;

    private final DefaultTableModel model = Ui.readOnlyModel(
            "Nama", "Email", "Kode QR", "Nominal", "Status Bayar", "Check-in");
    private final JTable table = new JTable(model);
    private final JLabel infoLabel = Ui.label("Memuat peserta...", Theme.regular(11), Theme.MUTED);

    public EventDetailDialog(Window owner, Admin admin, EventItem event, Runnable onChanged) {
        super(owner, "Detail Event", ModalityType.APPLICATION_MODAL);
        this.admin = admin;
        this.event = event;
        this.onChanged = onChanged;
        setSize(920, 700);
        setMinimumSize(new Dimension(840, 620));
        setLocationRelativeTo(owner);
        setContentPane(buildContent());
        loadParticipants();
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBackground(Color.WHITE);
        root.setBorder(BorderFactory.createEmptyBorder(22, 24, 20, 24));

        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setOpaque(false);
        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.add(Ui.label(event.getNama(), Theme.bold(21), Theme.TEXT));
        titles.add(Box.createVerticalStrut(4));
        titles.add(Ui.label("Informasi event dan daftar peserta terdaftar.", Theme.regular(12), Theme.MUTED));
        header.add(titles, BorderLayout.WEST);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.add(Ui.badgeRow(event.getStatus(), Theme.statusBg(event.getStatus()), Theme.statusFg(event.getStatus())));
        RoundedButton edit = RoundedButton.primary("Edit Event");
        edit.addActionListener(e -> {
            new EventFormDialog(this, admin, event, () -> {
                if (onChanged != null) {
                    onChanged.run();
                }
                loadParticipants();
            }).setVisible(true);
        });
        actions.add(edit);
        header.add(actions, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        JPanel info = new JPanel(new GridLayout(2, 3, 14, 12));
        info.setOpaque(false);
        info.add(infoBox("Kategori", event.getNamaKategori()));
        info.add(infoBox("Tanggal", event.getTanggalTampil()));
        info.add(infoBox("Lokasi", event.getLokasi()));
        info.add(infoBox("Kuota / Harga", event.getPesertaTampil() + " kursi / " + event.getHargaTampil()));
        info.add(infoBox("Deskripsi", event.getDeskripsi().isEmpty() ? "-" : event.getDeskripsi()));
        info.add(infoBox("Status Event", event.getStatus()));
        info.setPreferredSize(new Dimension(10, 130));

        Ui.styleTable(table, new int[]{170, 210, 110, 100, 110, 90});
        table.getColumnModel().getColumn(4).setCellRenderer(new StatusBadgeRenderer());
        table.getColumnModel().getColumn(5).setCellRenderer(new StatusBadgeRenderer());

        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setOpaque(false);
        tableHeader.add(Ui.label("Daftar Peserta", Theme.bold(14), Theme.TEXT), BorderLayout.WEST);
        infoLabel.setHorizontalAlignment(JLabel.RIGHT);
        tableHeader.add(infoLabel, BorderLayout.EAST);

        JPanel tableHolder = new JPanel(new BorderLayout(0, 10));
        tableHolder.setOpaque(false);
        tableHolder.add(tableHeader, BorderLayout.NORTH);
        tableHolder.add(Ui.tableScroll(table), BorderLayout.CENTER);

        JPanel holder = new JPanel(new BorderLayout(0, 14));
        holder.setOpaque(false);
        holder.add(info, BorderLayout.NORTH);
        holder.add(tableHolder, BorderLayout.CENTER);
        root.add(holder, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        footer.setOpaque(false);
        RoundedButton tutup = RoundedButton.secondary("Tutup");
        tutup.addActionListener(e -> dispose());
        footer.add(tutup);
        root.add(footer, BorderLayout.SOUTH);
        return root;
    }

    private JPanel infoBox(String label, String value) {
        JPanel box = new JPanel();
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.add(Ui.label(label, Theme.regular(10), Theme.MUTED));
        box.add(Box.createVerticalStrut(3));
        JLabel v = Ui.label("<html><body style='width:220px'>" + escape(value) + "</body></html>",
                Theme.bold(12), Theme.TEXT);
        box.add(v);
        return box;
    }

    private String escape(String value) {
        return value == null ? "-" : value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private void loadParticipants() {
        model.setRowCount(0);
        infoLabel.setText("Memuat peserta...");
        Async.run(() -> new EventDAO().participants(event.getIdEvent(), admin.getIdAdminUkm()),
                (List<Participant> list) -> {
                    model.setRowCount(0);
                    for (Participant p : list) {
                        model.addRow(new Object[]{p.getNama(), p.getEmail(), p.getKodeQr(), p.getNominalTampil(),
                            p.getStatusBayar(), p.isHadir() ? "Hadir" : "Belum"});
                    }
                    infoLabel.setText(list.size() + " peserta terdaftar");
                }, ex -> infoLabel.setText("Gagal memuat: " + ex.getMessage()));
    }
}