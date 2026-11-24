package frontend.pages;

import backend.EventDAO;
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
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
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
import model.EventItem;

public class EventPage extends JPanel implements Refreshable {

    private final Admin admin;
    private final DefaultTableModel model = Ui.readOnlyModel(
            "Nama Event", "Kategori", "Tanggal", "Lokasi", "Peserta", "Status");
    private final JTable table = new JTable(model);
    private final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
    private final List<EventItem> events = new ArrayList<>();
    private final RoundedTextField searchField = new RoundedTextField("Cari event Anda...");
    private final JComboBox<String> kategoriFilter = Ui.combo();
    private final JComboBox<String> statusFilter = Ui.combo("Semua Status", "Aktif", "Selesai");
    private final JLabel info = Ui.label("", Theme.regular(11), Theme.MUTED);

    public EventPage(Admin admin) {
        this.admin = admin;
        setLayout(new BorderLayout());
        setBackground(Theme.BG);

        table.setRowSorter(sorter);
        Ui.styleTable(table, new int[]{280, 110, 180, 240, 90, 90});
        table.getColumnModel().getColumn(5).setCellRenderer(new StatusBadgeRenderer());
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() >= 0) {
                    openDetail();
                }
            }
        });

        RoundedButton create = RoundedButton.primary("+ Buat Event Baru");
        create.addActionListener(e -> new EventFormDialog(this.getTopLevelAncestor() == null ? null
                : (java.awt.Window) this.getTopLevelAncestor(), admin, null, this::refreshData).setVisible(true));

        JPanel body = new JPanel(new BorderLayout(0, 14));
        body.setOpaque(false);
        body.add(buildToolbar(), BorderLayout.NORTH);
        body.add(buildTableCard(), BorderLayout.CENTER);

        add(Ui.page("Kelola Event", "Atur publikasi, pantau kuota, dan lihat daftar peserta event organisasi Anda.",
                create, body), BorderLayout.CENTER);
    }

    private JPanel buildToolbar() {
        RoundedPanel bar = new RoundedPanel(new BorderLayout(12, 0), 10, Theme.WHITE, Theme.BORDER);
        bar.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);
        searchField.setPreferredSize(new Dimension(260, 38));
        left.add(searchField);
        kategoriFilter.setPreferredSize(new Dimension(160, 38));
        left.add(kategoriFilter);
        statusFilter.setPreferredSize(new Dimension(140, 38));
        left.add(statusFilter);
        RoundedButton reset = RoundedButton.ghost("Reset");
        reset.setFont(Theme.bold(11));
        reset.addActionListener(e -> {
            searchField.setText("");
            kategoriFilter.setSelectedIndex(0);
            statusFilter.setSelectedIndex(0);
        });
        left.add(reset);
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
        kategoriFilter.addActionListener(e -> applyFilter());
        statusFilter.addActionListener(e -> applyFilter());
        return bar;
    }

    private JPanel buildTableCard() {
        RoundedPanel card = Ui.card(new BorderLayout(0, 12));
        card.add(Ui.tableScroll(table), BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);

        RoundedButton detail = RoundedButton.secondary("Detail");
        detail.addActionListener(e -> openDetail());
        RoundedButton edit = RoundedButton.secondary("Edit");
        edit.addActionListener(e -> openEdit());
        RoundedButton hapus = RoundedButton.danger("Hapus");
        hapus.addActionListener(e -> deleteSelected());
        buttons.add(detail);
        buttons.add(edit);
        buttons.add(hapus);
        footer.add(buttons, BorderLayout.EAST);
        JLabel hint = Ui.hint("Klik dua kali baris untuk melihat detail event.");
        footer.add(hint, BorderLayout.WEST);
        card.add(footer, BorderLayout.SOUTH);
        return card;
    }

    private void applyFilter() {
        String keyword = searchField.getText().trim().toLowerCase();
        String kategori = kategoriFilter.getSelectedItem() == null ? "Semua Kategori" : kategoriFilter.getSelectedItem().toString();
        String status = statusFilter.getSelectedItem() == null ? "Semua Status" : statusFilter.getSelectedItem().toString();

        List<RowFilter<DefaultTableModel, Integer>> filters = new ArrayList<>();
        if (!keyword.isEmpty()) {
            filters.add(new RowFilter<DefaultTableModel, Integer>() {
                @Override
                public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                    return entry.getStringValue(0).toLowerCase().contains(keyword)
                            || entry.getStringValue(3).toLowerCase().contains(keyword);
                }
            });
        }
        if (!"Semua Kategori".equals(kategori)) {
            filters.add(RowFilter.regexFilter("^" + java.util.regex.Pattern.quote(kategori) + "$", 1));
        }
        if (!"Semua Status".equals(status)) {
            filters.add(RowFilter.regexFilter("^" + java.util.regex.Pattern.quote(status) + "$", 5));
        }
        sorter.setRowFilter(filters.isEmpty() ? null : RowFilter.andFilter(filters));
    }

    private EventItem selectedEvent() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            return null;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        return modelRow >= 0 && modelRow < events.size() ? events.get(modelRow) : null;
    }

    private void openDetail() {
        EventItem e = selectedEvent();
        if (e == null) {
            Ui.info(this, "Pilih satu event terlebih dahulu.");
            return;
        }
        new EventDetailDialog((java.awt.Window) this.getTopLevelAncestor(), admin, e, this::refreshData).setVisible(true);
    }

    private void openEdit() {
        EventItem e = selectedEvent();
        if (e == null) {
            Ui.info(this, "Pilih satu event yang ingin diubah.");
            return;
        }
        new EventFormDialog((java.awt.Window) this.getTopLevelAncestor(), admin, e, this::refreshData).setVisible(true);
    }

    private void deleteSelected() {
        EventItem e = selectedEvent();
        if (e == null) {
            Ui.info(this, "Pilih satu event yang ingin dihapus.");
            return;
        }
        if (!Ui.confirm(this, "Hapus event \"" + e.getNama() + "\"?\n"
                + "Tiket, pembayaran, dan kehadiran terkait ikut terhapus permanen.")) {
            return;
        }
        Async.run(() -> {
            new EventDAO().delete(e.getIdEvent(), admin.getIdAdminUkm());
            return true;
        }, ok -> {
            Ui.success(this, "Event berhasil dihapus.");
            refreshData();
        }, ex -> Ui.error(this, "Gagal menghapus event: " + ex.getMessage()));
    }

    @Override
    public void refreshData() {
        int id = admin.getIdAdminUkm();
        info.setText("Memuat...");
        Async.run(() -> new EventDAO().listByAdmin(id), (List<EventItem> list) -> {
            events.clear();
            events.addAll(list);
            model.setRowCount(0);
            Set<String> categories = new LinkedHashSet<>();
            categories.add("Semua Kategori");
            for (EventItem e : list) {
                model.addRow(new Object[]{e.getNama(), e.getNamaKategori(), e.getTanggalTampil(),
                    e.getLokasi(), e.getPesertaTampil(), e.getStatus()});
                if (!e.getNamaKategori().isEmpty() && !"-".equals(e.getNamaKategori())) {
                    categories.add(e.getNamaKategori());
                }
            }
            String selected = kategoriFilter.getSelectedItem() == null ? "Semua Kategori"
                    : kategoriFilter.getSelectedItem().toString();
            kategoriFilter.removeAllItems();
            for (String c : categories) {
                kategoriFilter.addItem(c);
            }
            kategoriFilter.setSelectedItem(selected);
            info.setText(events.size() + " event");
            applyFilter();
        }, ex -> {
            info.setText("Gagal memuat");
            Ui.error(this, "Gagal memuat event: " + ex.getMessage());
        });
    }
}