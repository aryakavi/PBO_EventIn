package frontend.pages;

import backend.PaymentDAO;
import frontend.Async;
import frontend.Theme;
import frontend.Ui;
import frontend.components.BadgeLabel;
import frontend.components.RoundedButton;
import frontend.components.RoundedPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import model.Admin;
import model.PaymentItem;

public class PaymentDialog extends JDialog {

    private final Admin admin;
    private final PaymentItem item;
    private final Runnable onChanged;
    private final BadgeLabel statusBadge;
    private final RoundedButton verifikasi = RoundedButton.success("Verifikasi / Lunas");
    private final RoundedButton tolak = RoundedButton.danger("Tolak / Gagal");
    private final RoundedButton menunggu = RoundedButton.secondary("Kembalikan ke Menunggu");

    public PaymentDialog(Window owner, Admin admin, PaymentItem item, Runnable onChanged) {
        super(owner, "Periksa Transaksi", ModalityType.APPLICATION_MODAL);
        this.admin = admin;
        this.item = item;
        this.onChanged = onChanged;
        this.statusBadge = new BadgeLabel(item.getStatusBayar(),
                Theme.statusBg(item.getStatusBayar()), Theme.statusFg(item.getStatusBayar()));
        setSize(640, 660);
        setMinimumSize(new Dimension(600, 620));
        setLocationRelativeTo(owner);
        setContentPane(buildContent());
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBackground(Color.WHITE);
        root.setBorder(BorderFactory.createEmptyBorder(22, 24, 20, 24));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(Ui.label("Periksa Transaksi", Theme.bold(21), Theme.TEXT));
        header.add(Box.createVerticalStrut(4));
        header.add(Ui.label("Verifikasi bukti pembayaran peserta. Perubahan status langsung tersimpan.",
                Theme.regular(12), Theme.MUTED));
        root.add(header, BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.add(infoRow("ID Tiket", "#" + item.getIdTiket()));
        body.add(infoRow("Peserta", item.getNamaPeserta() + " (" + item.getEmail() + ")"));
        body.add(infoRow("Event", item.getNamaEvent()));
        body.add(infoRow("Jumlah", item.getNominalTampil()));
        body.add(infoRow("Metode", item.getMetode()));
        body.add(infoRow("Waktu unggah", item.getWaktuTampil()));
        body.add(infoRow("Bukti pembayaran", item.getBukti()));

        JPanel statusRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        statusRow.setOpaque(false);
        statusRow.setAlignmentX(LEFT_ALIGNMENT);
        statusRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        JLabel label = Ui.label("Status saat ini", Theme.regular(11), Theme.MUTED);
        label.setPreferredSize(new Dimension(150, 30));
        statusRow.add(label);
        statusRow.add(statusBadge);
        body.add(Box.createVerticalStrut(6));
        body.add(statusRow);

        RoundedPanel warning = new RoundedPanel(new BorderLayout(10, 0), 10, new Color(0xFE, 0xF3, 0xC7), null);
        warning.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        warning.setAlignmentX(LEFT_ALIGNMENT);
        warning.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        JLabel warn = Ui.label("<html><b>Catatan:</b> pembayaran diverifikasi manual oleh admin. "
                + "Pastikan nominal dan bukti transfer sesuai sebelum menekan tombol verifikasi.</html>",
                Theme.regular(11), Theme.WARN_FG);
        warning.add(warn, BorderLayout.CENTER);
        body.add(Box.createVerticalStrut(14));
        body.add(warning);

        root.add(body, BorderLayout.CENTER);

        JPanel footer = new JPanel();
        footer.setOpaque(false);
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        JPanel buttons = new JPanel(new GridLayout(1, 3, 8, 0));
        buttons.setOpaque(false);
        verifikasi.setFont(Theme.bold(12));
        tolak.setFont(Theme.bold(12));
        menunggu.setFont(Theme.bold(11));
        verifikasi.addActionListener(e -> update("Lunas"));
        tolak.addActionListener(e -> update("Ditolak"));
        menunggu.addActionListener(e -> update("Menunggu"));
        buttons.add(tolak);
        buttons.add(menunggu);
        buttons.add(verifikasi);
        footer.add(buttons);
        footer.add(Box.createVerticalStrut(8));
        RoundedButton tutup = RoundedButton.ghost("Tutup");
        tutup.addActionListener(e -> dispose());
        JPanel closeRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        closeRow.setOpaque(false);
        closeRow.add(tutup);
        footer.add(closeRow);
        root.add(footer, BorderLayout.SOUTH);
        return root;
    }

    private JPanel infoRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        JLabel l = Ui.label(label, Theme.regular(11), Theme.MUTED);
        l.setPreferredSize(new Dimension(150, 30));
        row.add(l, BorderLayout.WEST);
        JLabel v = Ui.label("<html>" + escape(value) + "</html>", Theme.bold(12), Theme.TEXT);
        row.add(v, BorderLayout.CENTER);
        row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER));
        return row;
    }

    private String escape(String value) {
        return value == null ? "-" : value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private void update(String status) {
        if (!Ui.confirm(this, "Ubah status pembayaran tiket #" + item.getIdTiket() + " menjadi \"" + status + "\"?")) {
            return;
        }
        setBusy(true);
        Async.run(() -> {
            new PaymentDAO().updateStatus(item.getIdTiket(), status, admin.getIdAdminUkm());
            return true;
        }, ok -> {
            setBusy(false);
            Ui.success(this, "Status pembayaran berhasil diubah menjadi " + status + ".");
            if (onChanged != null) {
                onChanged.run();
            }
            dispose();
        }, ex -> {
            setBusy(false);
            Ui.error(this, "Gagal mengubah status: " + ex.getMessage());
        });
    }

    private void setBusy(boolean busy) {
        verifikasi.setEnabled(!busy);
        tolak.setEnabled(!busy);
        menunggu.setEnabled(!busy);
    }
}