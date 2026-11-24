package backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import model.CheckinResult;
import model.CheckinRow;

public class CheckinDAO {

    public CheckinResult findByCode(String kode, int idAdminUkm) throws SQLException {
        String sql = "select t.id_tiket, p.nama, p.email, e.nama_event, e.lokasi, t.kode_qr, t.status_bayar, "
                + "(h.id_kehadiran is not null) as hadir, h.waktu_checkin "
                + "from tiket t "
                + "join pengguna p on p.id_pengguna = t.id_pengguna "
                + "join events e on e.id_event = t.id_event "
                + "left join kehadiran h on h.id_tiket = t.id_tiket "
                + "where upper(t.kode_qr) = upper(?) and e.id_pembuat = ?";
        try (Connection c = ConnectionDB.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, kode.trim());
            ps.setInt(2, idAdminUkm);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                CheckinResult r = new CheckinResult();
                r.setIdTiket(rs.getInt("id_tiket"));
                r.setNama(rs.getString("nama"));
                r.setEmail(rs.getString("email"));
                r.setNamaEvent(rs.getString("nama_event"));
                r.setLokasi(rs.getString("lokasi"));
                r.setKodeQr(rs.getString("kode_qr"));
                r.setStatusBayar(rs.getString("status_bayar"));
                r.setHadir(rs.getBoolean("hadir"));
                Timestamp ts = rs.getTimestamp("waktu_checkin");
                r.setWaktuCheckin(ts == null ? null : ts.toLocalDateTime());
                return r;
            }
        }
    }

    public void checkin(int idTiket, int idAdminUkm) throws SQLException {
        String sql = "insert into kehadiran (id_tiket, id_petugas, waktu_checkin) "
                + "select ?, ?, current_timestamp where exists ("
                + "select 1 from tiket t join events e on e.id_event = t.id_event "
                + "where t.id_tiket = ? and e.id_pembuat = ?) "
                + "returning id_kehadiran";
        try (Connection c = ConnectionDB.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idTiket);
            ps.setInt(2, idAdminUkm);
            ps.setInt(3, idTiket);
            ps.setInt(4, idAdminUkm);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Check-in gagal: tiket bukan bagian dari event Anda.");
                }
            }
        } catch (SQLException ex) {
            if ("23505".equals(ex.getSQLState())) {
                throw new SQLException("Tiket ini sudah pernah check-in.");
            }
            throw ex;
        }
    }

    public List<CheckinRow> history(int idAdminUkm) throws SQLException {
        List<CheckinRow> list = new ArrayList<>();
        String sql = "select p.nama, p.email, e.nama_event, t.kode_qr, h.waktu_checkin "
                + "from kehadiran h "
                + "join tiket t on t.id_tiket = h.id_tiket "
                + "join pengguna p on p.id_pengguna = t.id_pengguna "
                + "join events e on e.id_event = t.id_event "
                + "where e.id_pembuat = ? order by h.waktu_checkin desc limit 200";
        try (Connection c = ConnectionDB.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idAdminUkm);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CheckinRow r = new CheckinRow();
                    r.setNama(rs.getString("nama"));
                    r.setEmail(rs.getString("email"));
                    r.setNamaEvent(rs.getString("nama_event"));
                    r.setKodeQr(rs.getString("kode_qr"));
                    Timestamp ts = rs.getTimestamp("waktu_checkin");
                    r.setWaktuCheckin(ts == null ? null : ts.toLocalDateTime());
                    list.add(r);
                }
            }
        }
        return list;
    }
}