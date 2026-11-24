package backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import model.PaymentItem;

public class PaymentDAO {

    public List<PaymentItem> listByAdmin(int idAdminUkm) throws SQLException {
        List<PaymentItem> list = new ArrayList<>();
        String sql = "select t.id_tiket, p.nama, p.email, e.nama_event, e.harga as nominal, t.status_bayar, "
                + "pm.bukti_pembayaran, pm.metode, pm.waktu_unggah, t.tanggal_daftar "
                + "from tiket t "
                + "join pengguna p on p.id_pengguna = t.id_pengguna "
                + "join events e on e.id_event = t.id_event "
                + "left join pembayaran pm on pm.id_tiket = t.id_tiket "
                + "where e.id_pembuat = ? order by t.tanggal_daftar desc, t.id_tiket desc";
        try (Connection c = ConnectionDB.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idAdminUkm);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PaymentItem p = new PaymentItem();
                    p.setIdTiket(rs.getInt("id_tiket"));
                    p.setNamaPeserta(rs.getString("nama"));
                    p.setEmail(rs.getString("email"));
                    p.setNamaEvent(rs.getString("nama_event"));
                    p.setNominal(rs.getDouble("nominal"));
                    p.setStatusBayar(rs.getString("status_bayar"));
                    p.setBukti(rs.getString("bukti_pembayaran"));
                    p.setMetode(rs.getString("metode"));
                    Timestamp ts = rs.getTimestamp("waktu_unggah");
                    p.setWaktuUnggah(ts == null ? null : ts.toLocalDateTime());
                    Timestamp daftar = rs.getTimestamp("tanggal_daftar");
                    p.setTanggalDaftar(daftar == null ? null : daftar.toLocalDateTime());
                    list.add(p);
                }
            }
        }
        return list;
    }

    /** Hanya tiket dari event milik admin yang boleh diubah. */
    public void updateStatus(int idTiket, String status, int idAdminUkm) throws SQLException {
        String sql = "update tiket t set status_bayar = ? "
                + "where t.id_tiket = ? and exists ("
                + "select 1 from events e where e.id_event = t.id_event and e.id_pembuat = ?)";
        try (Connection c = ConnectionDB.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, idTiket);
            ps.setInt(3, idAdminUkm);
            int n = ps.executeUpdate();
            if (n == 0) {
                throw new SQLException("Tiket tidak ditemukan atau bukan bagian dari event Anda.");
            }
        }
    }
}