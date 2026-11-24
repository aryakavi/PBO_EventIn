package frontend.pages;

import backend.PaymentDAO;
import backend.ReportDAO;
import frontend.Async;
import frontend.MainFrame;
import frontend.Theme;
import frontend.Ui;
import frontend.components.RoundedButton;
import frontend.components.RoundedPanel;
import frontend.components.StatusBadgeRenderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import model.Admin;
import model.DashboardStats;
import model.PaymentItem;

public class DashboardPage extends JPanel implements Refreshable {

    private final Admin admin;
    private final MainFrame mainFrame;
    private final JPanel kpiRow = Ui.kpiRow(16);
    private final DefaultTableModel model;
    private final JTable table;
    private final JLabel updatedLabel = Ui.label("", Theme.regular(10), Theme.MUTED_LIGHT);

    public DashboardPage(Admin admin, MainFrame mainFrame) {
        this.admin = admin;
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(Theme.BG);

        RoundedButton create = RoundedButton.primary("+ Buat Event Baru");
        create.addActionListener(e -> new EventFormDialog(mainFrame, admin, null, this::refreshData).setVisible(true));

        model = Ui.readOnlyModel("Event", "Tanggal Daftar", "Peserta", "Status");
        table = new JTable(model);
        Ui.styleTable(table, new int[]{360, 170, 220, 130});
        table.getColumnModel().getColumn(3).setCellRenderer(new StatusBadgeRenderer());

        JPanel body = new JPanel(new BorderLayout(0, 18));
        body.setOpaque(false);

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        kpiRow.setAlignmentX(LEFT_ALIGNMENT);
        top.add(kpiRow);
        body.add(top, BorderLayout.NORTH);

        RoundedPanel tableCard = Ui.card(new BorderLayout(0, 12));
        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setOpaque(false);
        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.add(Ui.label("Aktivitas Pendaftaran Terbaru", Theme.bold(15), Theme.TEXT));
        titles.add(Box.createVerticalStrut(2));
        titles.add(Ui.label("5 pendaftaran terakhir pada event yang Anda kelola.", Theme.regular(11), Theme.MUTED));
        tableHeader.add(titles, BorderLayout.WEST);
        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightHeader.setOpaque(false);
        rightHeader.add(updatedLabel);
        RoundedButton all = RoundedButton.secondary("Lihat Pembayaran");
        all.setFont(Theme.bold(11));
        all.addActionListener(e -> mainFrame.openPage("payment"));
        rightHeader.add(all);
        tableHeader.add(rightHeader, BorderLayout.EAST);
        tableCard.add(tableHeader, BorderLayout.NORTH);

        JScrollPane scroll = Ui.tableScroll(table);
        tableCard.add(scroll, BorderLayout.CENTER);
        body.add(tableCard, BorderLayout.CENTER);

        add(Ui.page("Selamat Datang, " + admin.getNama(), "Berikut ikhtisar aktivitas pendaftaran, "
                + "pendapatan, dan partisipasi event kemahasiswaan Anda.", create, body), BorderLayout.CENTER);
    }

    @Override
    public void refreshData() {
        model.setRowCount(0);
        int id = admin.getIdAdminUkm();
        Async.run(() -> {
            DashboardStats stats = new ReportDAO().dashboard(id);
            List<PaymentItem> recent = new PaymentDAO().listByAdmin(id);
            return new Object[]{stats, recent};
        }, result -> {
            DashboardStats stats = (DashboardStats) result[0];
            @SuppressWarnings("unchecked")
            List<PaymentItem> recent = (List<PaymentItem>) result[1];

            kpiRow.removeAll();
            kpiRow.add(Ui.kpiCard("Total Event", String.valueOf(stats.getTotalEvent()),
                    "Event yang Anda kelola", true));
            kpiRow.add(Ui.kpiCard("Total Peserta", Ui.number(stats.getTotalPeserta()),
                    "Seluruh tiket pada event Anda", true));
            kpiRow.add(Ui.kpiCard("Pendapatan Bersih", stats.getPendapatanTampil(),
                    stats.getPembayaranMenunggu() + " pembayaran menunggu", stats.getPembayaranMenunggu() == 0));
            kpiRow.add(Ui.kpiCard("Event Aktif", String.valueOf(stats.getEventAktif()),
                    "Event yang belum selesai", true));
            kpiRow.revalidate();
            kpiRow.repaint();

            model.setRowCount(0);
            int limit = Math.min(5, recent.size());
            for (int i = 0; i < limit; i++) {
                PaymentItem p = recent.get(i);
                model.addRow(new Object[]{p.getNamaEvent(), p.getTanggalDaftarTampil(), p.getNamaPeserta(), p.getStatusBayar()});
            }
            updatedLabel.setText("Diperbarui: " + java.time.LocalTime.now().withNano(0));
        }, ex -> {
            model.setRowCount(0);
            model.addRow(new Object[]{"Gagal memuat data: " + ex.getMessage(), "", "", ""});
        });
    }
}