package frontend.pages;

import backend.ReportDAO;
import frontend.Async;
import frontend.Theme;
import frontend.Ui;
import frontend.components.RoundedButton;
import frontend.components.RoundedPanel;
import frontend.components.StatusBadgeRenderer;
import java.awt.BorderLayout;
import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import model.Admin;
import model.DashboardStats;
import model.ReportRow;

public class ReportPage extends JPanel implements Refreshable {

    private final Admin admin;
    private final JPanel kpiRow = Ui.kpiRow(16);
    private final DefaultTableModel model = Ui.readOnlyModel(
            "Peserta", "Email", "Status Bayar", "Kehadiran", "Sertifikat");
    private final JTable table = new JTable(model);
    private final JLabel info = Ui.label("", Theme.regular(11), Theme.MUTED);
    private List<ReportRow> rows;

    public ReportPage(Admin admin) {
        this.admin = admin;
        setLayout(new BorderLayout());
        setBackground(Theme.BG);

        Ui.styleTable(table, new int[]{180, 240, 130, 120, 140});
        table.getColumnModel().getColumn(2).setCellRenderer(new StatusBadgeRenderer());
        table.getColumnModel().getColumn(3).setCellRenderer(new StatusBadgeRenderer());

        RoundedButton export = RoundedButton.secondary("Ekspor CSV");
        export.addActionListener(e -> exportCsv());

        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setOpaque(false);
        body.add(kpiRow, BorderLayout.NORTH);
        body.add(buildTableCard(), BorderLayout.CENTER);

        add(Ui.page("Laporan Event", "Rekap pendaftaran, kehadiran, dan pendapatan seluruh event organisasi Anda.",
                export, body), BorderLayout.CENTER);
    }

    private JPanel buildTableCard() {
        RoundedPanel card = Ui.card(new BorderLayout(0, 12));
        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        head.add(Ui.label("Rekap Peserta", Theme.bold(15), Theme.TEXT), BorderLayout.WEST);
        JLabel right = info;
        head.add(right, BorderLayout.EAST);
        card.add(head, BorderLayout.NORTH);
        card.add(Ui.tableScroll(table), BorderLayout.CENTER);
        return card;
    }

    private void exportCsv() {
        if (rows == null || rows.isEmpty()) {
            Ui.info(this, "Belum ada data untuk diekspor.");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Simpan laporan sebagai CSV");
        chooser.setSelectedFile(new File("laporan-eventin.csv"));
        chooser.setFileFilter(new FileNameExtensionFilter("Berkas CSV", "csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File target = chooser.getSelectedFile();
        if (!target.getName().toLowerCase().endsWith(".csv")) {
            target = new File(target.getAbsolutePath() + ".csv");
        }
        try (PrintWriter pw = new PrintWriter(target, StandardCharsets.UTF_8)) {
            pw.println("Peserta;Email;Status Bayar;Kehadiran;Sertifikat");
            for (ReportRow r : rows) {
                pw.println(csv(r.getNama()) + ";" + csv(r.getEmail()) + ";" + csv(r.getStatusBayar())
                        + ";" + csv(r.getHadirTampil()) + ";" + csv(r.getStatusSertifikat()));
            }
            JOptionPane.showMessageDialog(this, "Laporan tersimpan di:\n" + target.getAbsolutePath(),
                    "Ekspor Berhasil", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            Ui.error(this, "Gagal mengekspor CSV: " + ex.getMessage());
        }
    }

    private String csv(String value) {
        String v = value == null ? "" : value.replace(";", ",").replace("\n", " ");
        return "\"" + v + "\"";
    }

    @Override
    public void refreshData() {
        int id = admin.getIdAdminUkm();
        info.setText("Memuat...");
        Async.run(() -> {
            DashboardStats stats = new ReportDAO().dashboard(id);
            List<ReportRow> list = new ReportDAO().rows(id);
            return new Object[]{stats, list};
        }, result -> {
            DashboardStats stats = (DashboardStats) result[0];
            @SuppressWarnings("unchecked")
            List<ReportRow> list = (List<ReportRow>) result[1];
            rows = list;

            int pendaftar = list.size();
            int hadir = 0;
            for (ReportRow r : list) {
                if (r.isHadir()) {
                    hadir++;
                }
            }
            double rate = pendaftar == 0 ? 0 : (hadir * 100.0 / pendaftar);

            kpiRow.removeAll();
            kpiRow.add(Ui.kpiCard("Total Pendaftar", Ui.number(pendaftar),
                    "Tiket pada semua event Anda", true));
            kpiRow.add(Ui.kpiCard("Total Hadir", Ui.number(hadir),
                    "Peserta yang sudah check-in", true));
            kpiRow.add(Ui.kpiCard("Pendapatan", stats.getPendapatanTampil(),
                    "Dari tiket berstatus Lunas", true));
            kpiRow.add(Ui.kpiCard("Tingkat Kehadiran", String.format("%.1f%%", rate),
                    "Hadir dibanding total pendaftar", rate >= 50));
            kpiRow.revalidate();
            kpiRow.repaint();

            model.setRowCount(0);
            for (ReportRow r : list) {
                model.addRow(new Object[]{r.getNama(), r.getEmail(), r.getStatusBayar(),
                    r.getHadirTampil(), r.getStatusSertifikat()});
            }
            info.setText(pendaftar + " baris data");
        }, ex -> {
            info.setText("Gagal memuat");
            Ui.error(this, "Gagal memuat laporan: " + ex.getMessage());
        });
    }
}