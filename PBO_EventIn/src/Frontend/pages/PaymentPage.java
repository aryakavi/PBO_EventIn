package frontend.pages;

import backend.PaymentDAO;
import frontend.Async;
import frontend.Theme;
import frontend.Ui;
import frontend.components.RoundedButton;
import frontend.components.RoundedPanel;
import frontend.components.RoundedTextField;
import frontend.components.StatusBadgeRenderer;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import model.Admin;
import model.PaymentItem;

public class PaymentPage extends JPanel implements Refreshable {

    private final Admin admin;
    private final DefaultTableModel model = Ui.readOnlyModel(
            "ID Tiket", "Peserta", "Event", "Jumlah", "Status");
    private final JTable table = new JTable(model);
    private final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
    private final List<PaymentItem> items = new ArrayList<>();
    private final RoundedTextField searchField = new RoundedTextField("Cari peserta / event...");
    private final JComboBox<String> statusFilter = Ui.combo(
            "Semua Status", "Menunggu", "Lunas", "Ditolak", "Belum Bayar", "Batal");
    private final JLabel info = Ui.label("", Theme.regular(11), Theme.MUTED);

    public PaymentPage(Admin admin) {
        this.admin = admin;
        setLayout(new BorderLayout());
        setBackground(Theme.BG);

        table.setRowSorter(sorter);
        Ui.styleTable(table, new int[]{90, 200, 300, 130, 120});
        table.getColumnModel().getColumn(4).setCellRenderer(new StatusBadgeRenderer());
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() >= 0) {
                    openDetail();
                }
            }
        });

        RoundedButton refresh = RoundedButton.secondary("Muat Ulang");
        refresh.addActionListener(e -> refreshData());

        JPanel body = new JPanel(new BorderLayout(0, 14));
        body.setOpaque(false);
        body.add(buildToolbar(), BorderLayout.NORTH);
        body.add(buildCard(), BorderLayout.CENTER);

        add(Ui.page("Verifikasi Pembayaran", "Periksa bukti pembayaran peserta dan ubah status tiket event Anda.",
                refresh, body), BorderLayout.CENTER);
    }

    private JPanel buildToolbar() {
        RoundedPanel bar = new RoundedPanel(new BorderLayout(12, 0), 10, Theme.WHITE, Theme.BORDER);
        bar.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);
        searchField.setPreferredSize(new Dimension(260, 38));
        left.add(searchField);
        statusFilter.setPreferredSize(new Dimension(160, 38));
        left.add(statusFilter);
        bar.add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        right.add(info);
        bar.add(right, BorderLayout.EAST);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applyFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applyFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applyFilter();
            }
        });
        statusFilter.addActionListener(e -> applyFilter());
        return bar;
    }

    private JPanel buildCard() {
        RoundedPanel card = Ui.card(new BorderLayout(0, 12));
        card.add(Ui.tableScroll(table), BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.add(Ui.hint("Klik dua kali baris untuk memeriksa transaksi."), BorderLayout.WEST);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        RoundedButton periksa = RoundedButton.primary("Periksa Transaksi");
        periksa.addActionListener(e -> openDetail());
        buttons.add(periksa);
        footer.add(buttons, BorderLayout.EAST);
        card.add(footer, BorderLayout.SOUTH);
        return card;
    }

    private void applyFilter() {
        String keyword = searchField.getText().trim().toLowerCase();
        String status = statusFilter.getSelectedItem() == null ? "Semua Status"
                : statusFilter.getSelectedItem().toString();
        List<RowFilter<DefaultTableModel, Integer>> filters = new ArrayList<>();
        if (!keyword.isEmpty()) {
            filters.add(new RowFilter<DefaultTableModel, Integer>() {
                @Override
                public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                    return entry.getStringValue(1).toLowerCase().contains(keyword)
                            || entry.getStringValue(2).toLowerCase().contains(keyword)
                            || entry.getStringValue(0).contains(keyword);
                }
            });
        }
        if (!"Semua Status".equals(status)) {
            filters.add(RowFilter.regexFilter("^" + java.util.regex.Pattern.quote(status) + "$", 4));
        }
        sorter.setRowFilter(filters.isEmpty() ? null : RowFilter.andFilter(filters));
    }

    private PaymentItem selectedItem() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            return null;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        return modelRow >= 0 && modelRow < items.size() ? items.get(modelRow) : null;
    }

    private void openDetail() {
        PaymentItem item = selectedItem();
        if (item == null) {
            Ui.info(this, "Pilih satu transaksi terlebih dahulu.");
            return;
        }
        new PaymentDialog((Window) this.getTopLevelAncestor(), admin, item, this::refreshData).setVisible(true);
    }

    @Override
    public void refreshData() {
        int id = admin.getIdAdminUkm();
        info.setText("Memuat...");
        Async.run(() -> new PaymentDAO().listByAdmin(id), (List<PaymentItem> list) -> {
            items.clear();
            items.addAll(list);
            model.setRowCount(0);
            for (PaymentItem p : list) {
                model.addRow(new Object[]{"#" + p.getIdTiket(), p.getNamaPeserta(), p.getNamaEvent(),
                    p.getNominalTampil(), p.getStatusBayar()});
            }
            info.setText(list.size() + " transaksi");
            applyFilter();
        }, ex -> {
            info.setText("Gagal memuat");
            Ui.error(this, "Gagal memuat pembayaran: " + ex.getMessage());
        });
    }
}