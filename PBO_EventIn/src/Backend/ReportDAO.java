package backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.DashboardStats;
import model.ReportRow;

public class ReportDAO {

    public DashboardStats dashboard(int idAdminUkm) throws SQLException {
        String sql = "select "
                + "(select count(*) from events e where e.id_pembuat = ?) as total_event, "
                + "(select count(*) from events e where e.id_pembuat = ? "
                + " and coalesce(e.tanggal_selesai, e.tanggal_mulai) >= now()) as event_aktif, "
                + "(select count(*) from tiket t join events e on e.id_event = t.id_event "
                + " where e.id_pembuat = ?) as total_peserta, "
                + "(select coalesce(sum(e.harga), 0) from tiket t join events e on e.id_event = t.id_event "
                + " where e.id_pembuat = ? and t.status_bayar = 'Lunas') as pendapatan, "
                + "(select count(*) from tiket t join events e on e.id_event = t.id_event "
                + " where e.id_pembuat = ? and t.status_bayar = 'Menunggu') as menunggu";
        try (Connection c = ConnectionDB.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 1; i <= 5; i++) {
                ps.setInt(i, idAdminUkm);
            }
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                DashboardStats s = new DashboardStats();
                s.setTotalEvent(rs.getInt("total_event"));
                s.setEventAktif(rs.getInt("event_aktif"));
                s.setTotalPeserta(rs.getInt("total_peserta"));
                s.setPendapatan(rs.getDouble("pendapatan"));
                s.setPembayaranMenunggu(rs.getInt("menunggu"));
                return s;
            }
        }
    }

    public List<ReportRow> rows(int idAdminUkm) throws SQLException {
        List<ReportRow> list = new ArrayList<>();
        String sql = "select p.nama, p.email, t.status_bayar, t.status_sertifikat, "
                + "(h.id_kehadiran is not null) as hadir "
                + "from tiket t "
                + "join pengguna p on p.id_pengguna = t.id_pengguna "
                + "join events e on e.id_event = t.id_event "
                + "left join kehadiran h on h.id_tiket = t.id_tiket "
                + "where e.id_pembuat = ? order by t.tanggal_daftar desc";
        try (Connection c = ConnectionDB.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idAdminUkm);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReportRow r = new ReportRow();
                    r.setNama(rs.getString("nama"));
                    r.setEmail(rs.getString("email"));
                    r.setStatusBayar(rs.getString("status_bayar"));
                    r.setStatusSertifikat(rs.getString("status_sertifikat"));
                    r.setHadir(rs.getBoolean("hadir"));
                    list.add(r);
                }
            }
        }
        return list;
    }

    public int[] reportSummary(int idAdminUkm) throws SQLException {
        String sql = "select "
                + "(select count(*) from tiket t join events e on e.id_event = t.id_event where e.id_pembuat = ?) as pendaftar, "
                + "(select count(*) from kehadiran h join tiket t on t.id_tiket = h.id_tiket "
                + " join events e on e.id_event = t.id_event where e.id_pembuat = ?) as hadir";
        try (Connection c = ConnectionDB.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idAdminUkm);
            ps.setInt(2, idAdminUkm);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return new int[]{rs.getInt("pendaftar"), rs.getInt("hadir")};
            }
        }
    }
}