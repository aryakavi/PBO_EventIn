package frontend.pages;

import backend.CheckinDAO;
import frontend.Async;
import frontend.Theme;
import frontend.Ui;
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
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import model.Admin;
import model.CheckinResult;
import model.CheckinRow;

public class CheckinPage extends JPanel implements Refreshable {

    private final Admin admin;
    private final RoundedTextField kodeField = new RoundedTextField("Contoh: QR-EVENT-003");
    private final RoundedButton checkinButton = RoundedButton.primary("Check-in Peserta");
    private final RoundedButton cariButton = RoundedButton.secondary("Cari Tiket");
    private final DefaultTableModel model = Ui.readOnlyModel(
            "Peserta", "Event", "Kode Tiket", "Waktu Check-in");
    private final JTable table = new JTable(model);
    private final JLabel info = Ui.label("", Theme.regular(11), Theme.MUTED);
    private final JPanel resultHolder = new JPanel(new BorderLayout());
    private CheckinResult current;

    public CheckinPage(Admin admin) {
        this.admin = admin;
        setLayout(new BorderLayout());
        setBackground(Theme.BG);

        resultHolder.setOpaque(false);
        resultHolder.add(emptyResult(), BorderLayout.CENTER);

        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setOpaque(false);
        body.add(buildScanCard(), BorderLayout.NORTH);
        body.add(buildContentRow(), BorderLayout.CENTER);

        add(Ui.page("Check-in Tiket", "Masukkan kode tiket peserta secara manual untuk mencatat kehadiran.",
                null, body), BorderLayout.CENTER);

        kodeField.addActionListener(e -> cariTiket());
    }

    private JPanel buildScanCard() {
        RoundedPanel card = Ui.card(new BorderLayout(0, 14));

        JPanel head = new JPanel();
        head.setOpaque(false);
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        head.add(Ui.label("Input Kode Tiket", Theme.bold(15), Theme.TEXT));
        head.add(Box.createVerticalStrut(3));
        head.add(Ui.label("Kode tiket tercetak pada e-ticket peserta (contoh: QR-EVENT-001).",
                Theme.regular(11), Theme.MUTED));

        JPanel inputRow = new JPanel(new BorderLayout(10, 0));
        inputRow.setOpaque(false);
        kodeField.setPreferredSize(new Dimension(320, 42));
        inputRow.add(kodeField, BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        cariButton.addActionListener(e -> cariTiket());
        checkinButton.addActionListener(e -> doCheckin());
        actions.add(cariButton);
        actions.add(checkinButton);
        inputRow.add(actions, BorderLayout.EAST);

        card.add(head, BorderLayout.NORTH);
        card.add(inputRow, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildContentRow() {
        JPanel row = new JPanel(new GridLayout(1, 2, 16, 0));
        row.setOpaque(false);

        RoundedPanel resultCard = Ui.card(new BorderLayout(0, 12));
        resultCard.add(Ui.label("Hasil Pencarian", Theme.bold(15), Theme.TEXT), BorderLayout.NORTH);
        resultCard.add(resultHolder, BorderLayout.CENTER);
        row.add(resultCard);

        RoundedPanel historyCard = Ui.card(new BorderLayout(0, 12));
        JPanel historyHead = new JPanel(new BorderLayout());
        historyHead.setOpaque(false);
        historyHead.add(Ui.label("Riwayat Check-in", Theme.bold(15), Theme.TEXT), BorderLayout.WEST);
        info.setHorizontalAlignment(JLabel.RIGHT);
        historyHead.add(info, BorderLayout.EAST);
        historyCard.add(historyHead, BorderLayout.NORTH);

        Ui.styleTable(table, new int[]{150, 170, 120, 130});
        historyCard.add(Ui.tableScroll(table), BorderLayout.CENTER);
        row.add(historyCard);
        return row;
    }

    private JComponent emptyResult() {
        JPanel wrap = new JPanel();
        wrap.setOpaque(false);
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
        wrap.setBorder(BorderFactory.createEmptyBorder(20, 8, 20, 8));
        JLabel icon = Ui.label("CARI", Theme.bold(11), Theme.MUTED_LIGHT);
        icon.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrap.add(icon);
        wrap.add(Box.createVerticalStrut(8));
        JLabel text = Ui.label("<html>Belum ada tiket yang dicari.<br>Masukkan kode tiket lalu tekan "
                + "<b>Cari Tiket</b>.</html>", Theme.regular(12), Theme.MUTED);
        text.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrap.add(text);
        return wrap;
    }

    private void cariTiket() {
        String kode = kodeField.getText().trim();
        if (kode.isEmpty()) {
            Ui.info(this, "Masukkan kode tiket terlebih dahulu.");
            return;
        }
        setBusy(true, "Mencari...");
        Async.run(() -> new CheckinDAO().findByCode(kode, admin.getIdAdminUkm()), result -> {
            setBusy(false, null);
            current = result;
            if (result == null) {
                showResult(null, "Tiket tidak ditemukan",
                        "Kode \"" + kode + "\" tidak terdaftar pada event milik organisasi Anda.");
                return;
            }
            showResult(result, result.getNama(), result.getNamaEvent());
        }, ex -> {
            setBusy(false, null);
            Ui.error(this, "Gagal mencari tiket: " + ex.getMessage());
        });
    }

    private void doCheckin() {
        if (current == null) {
            cariTiket();
            return;
        }
        if (!"Lunas".equalsIgnoreCase(current.getStatusBayar())) {
            Ui.error(this, "Tiket belum lunas (status: " + current.getStatusBayar()
                    + "). Verifikasi pembayaran terlebih dahulu.");
            return;
        }
        if (current.isHadir()) {
            Ui.info(this, "Peserta ini sudah check-in pada " + current.getWaktuTampil() + ".");
            return;
        }
        if (!Ui.confirm(this, "Check-in peserta \"" + current.getNama() + "\" untuk event \""
                + current.getNamaEvent() + "\"?")) {
            return;
        }
        setBusy(true, "Menyimpan...");
        int idTiket = current.getIdTiket();
        Async.run(() -> {
            new CheckinDAO().checkin(idTiket, admin.getIdAdminUkm());
            return true;
        }, ok -> {
            setBusy(false, null);
            Ui.success(this, "Check-in berhasil dicatat.");
            kodeField.setText("");
            current = null;
            resultHolder.removeAll();
            resultHolder.add(emptyResult(), BorderLayout.CENTER);
            resultHolder.revalidate();
            resultHolder.repaint();
            refreshData();
        }, ex -> {
            setBusy(false, null);
            Ui.error(this, "Check-in gagal: " + ex.getMessage());
        });
    }

    private void showResult(CheckinResult r, String title, String subtitle) {
        resultHolder.removeAll();

        JPanel box = new JPanel();
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        JLabel heading = Ui.label("<html><b>" + escape(title) + "</b></html>", Theme.bold(15), Theme.TEXT);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.add(heading);
        box.add(Box.createVerticalStrut(3));
        JLabel sub = Ui.label("<html>" + escape(subtitle) + "</html>", Theme.regular(12), Theme.MUTED);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.add(sub);
        box.add(Box.createVerticalStrut(14));

        if (r != null) {
            box.add(detailRow("Peserta", r.getNama()));
            box.add(detailRow("Email", r.getEmail()));
            box.add(detailRow("Event", r.getNamaEvent()));
            box.add(detailRow("Lokasi", r.getLokasi()));
            box.add(detailRow("Kode Tiket", r.getKodeQr()));
            box.add(detailRow("Status Bayar", r.getStatusBayar()));
            box.add(detailRow("Status Hadir", r.isHadir() ? "Sudah check-in " + r.getWaktuTampil() : "Belum check-in"));

            box.add(Box.createVerticalStrut(14));
            JPanel badge = Ui.badgeRow(r.isHadir() ? "SUDAH CHECK-IN" : "SIAP CHECK-IN",
                    r.isHadir() ? Theme.WARN_BG : Theme.SUCCESS_BG,
                    r.isHadir() ? Theme.WARN_FG : Theme.SUCCESS_FG);
            badge.setAlignmentX(Component.LEFT_ALIGNMENT);
            box.add(badge);
            checkinButton.setEnabled(!r.isHadir() && "Lunas".equalsIgnoreCase(r.getStatusBayar()));
        } else {
            checkinButton.setEnabled(false);
        }

        resultHolder.add(box, BorderLayout.NORTH);
        resultHolder.revalidate();
        resultHolder.repaint();
    }

    private JPanel detailRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        JLabel l = Ui.label(label, Theme.regular(11), Theme.MUTED);
        l.setPreferredSize(new Dimension(110, 26));
        row.add(l, BorderLayout.WEST);
        row.add(Ui.label("<html>" + escape(value == null || value.isEmpty() ? "-" : value) + "</html>",
                Theme.bold(12), Theme.TEXT), BorderLayout.CENTER);
        return row;
    }

    private String escape(String value) {
        return value == null ? "-" : value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private void setBusy(boolean busy, String label) {
        cariButton.setEnabled(!busy);
        checkinButton.setEnabled(!busy && current != null && !current.isHadir());
        if (label != null) {
            checkinButton.setText(label);
        } else {
            checkinButton.setText("Check-in Peserta");
        }
    }

    @Override
    public void refreshData() {
        int id = admin.getIdAdminUkm();
        Async.run(() -> new CheckinDAO().history(id), (List<CheckinRow> list) -> {
            model.setRowCount(0);
            for (CheckinRow r : list) {
                model.addRow(new Object[]{r.getNama(), r.getNamaEvent(), r.getKodeQr(), r.getWaktuTampil()});
            }
            info.setText(list.size() + " check-in");
        }, ex -> info.setText("Gagal memuat riwayat"));
    }
}