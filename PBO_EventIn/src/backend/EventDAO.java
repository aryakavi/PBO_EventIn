package backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import model.EventItem;
import model.Participant;

public class EventDAO {

    private static final String BASE_SELECT = "select e.id_event, e.id_kategori, k.nama_kategori, e.nama_event, "
            + "e.deskripsi, e.tanggal_mulai, e.tanggal_selesai, e.lokasi, e.kuota, e.harga, e.poster, "
            + "(select count(*) from tiket t where t.id_event = e.id_event) as peserta "
            + "from events e left join kategori_event k on k.id_kategori = e.id_kategori ";

    public List<EventItem> listByAdmin(int idAdminUkm) throws SQLException {
        List<EventItem> list = new ArrayList<>();
        String sql = BASE_SELECT + "where e.id_pembuat = ? order by e.tanggal_mulai desc, e.id_event desc";
        try (Connection c = ConnectionDB.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idAdminUkm);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public int create(EventItem e, int idAdminUkm) throws SQLException {
        String sql = "insert into events (id_pembuat, id_kategori, nama_event, deskripsi, tanggal_mulai, "
                + "tanggal_selesai, lokasi, kuota, harga, poster, payment_method) "
                + "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) returning id_event";
        try (Connection c = ConnectionDB.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idAdminUkm);
            ps.setInt(2, e.getIdKategori());
            ps.setString(3, e.getNama());
            ps.setString(4, emptyToNull(e.getDeskripsi()));
            ps.setTimestamp(5, Timestamp.valueOf(e.getTanggalMulai()));
            if (e.getTanggalSelesai() == null) {
                ps.setNull(6, Types.TIMESTAMP);
            } else {
                ps.setTimestamp(6, Timestamp.valueOf(e.getTanggalSelesai()));
            }
            ps.setString(7, e.getLokasi());
            ps.setInt(8, e.getKuota());
            ps.setDouble(9, e.getHarga());
            ps.setString(10, emptyToNull(e.getPoster()));
            ps.setNull(11, Types.OTHER);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public void update(EventItem e, int idAdminUkm) throws SQLException {
        String sql = "update events set id_kategori = ?, nama_event = ?, deskripsi = ?, tanggal_mulai = ?, "
                + "tanggal_selesai = ?, lokasi = ?, kuota = ?, harga = ?, poster = ? "
                + "where id_event = ? and id_pembuat = ?";
        try (Connection c = ConnectionDB.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, e.getIdKategori());
            ps.setString(2, e.getNama());
            ps.setString(3, emptyToNull(e.getDeskripsi()));
            ps.setTimestamp(4, Timestamp.valueOf(e.getTanggalMulai()));
            if (e.getTanggalSelesai() == null) {
                ps.setNull(5, Types.TIMESTAMP);
            } else {
                ps.setTimestamp(5, Timestamp.valueOf(e.getTanggalSelesai()));
            }
            ps.setString(6, e.getLokasi());
            ps.setInt(7, e.getKuota());
            ps.setDouble(8, e.getHarga());
            ps.setString(9, emptyToNull(e.getPoster()));
            ps.setInt(10, e.getIdEvent());
            ps.setInt(11, idAdminUkm);
            int n = ps.executeUpdate();
            if (n == 0) {
                throw new SQLException("Event tidak ditemukan atau bukan milik akun ini.");
            }
        }
    }

    /** Tiket, pembayaran, dan kehadiran ikut terhapus (FK on delete cascade). */
    public void delete(int idEvent, int idAdminUkm) throws SQLException {
        String sql = "delete from events where id_event = ? and id_pembuat = ?";
        try (Connection c = ConnectionDB.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idEvent);
            ps.setInt(2, idAdminUkm);
            int n = ps.executeUpdate();
            if (n == 0) {
                throw new SQLException("Event tidak ditemukan atau bukan milik akun ini.");
            }
        }
    }

    public List<Participant> participants(int idEvent, int idAdminUkm) throws SQLException {
        List<Participant> list = new ArrayList<>();
        String sql = "select t.id_tiket, p.nama, p.email, t.kode_qr, t.status_bayar, t.status_sertifikat, "
                + "e.harga as nominal, t.tanggal_daftar, (h.id_kehadiran is not null) as hadir "
                + "from tiket t "
                + "join pengguna p on p.id_pengguna = t.id_pengguna "
                + "join events e on e.id_event = t.id_event "
                + "left join kehadiran h on h.id_tiket = t.id_tiket "
                + "where t.id_event = ? and e.id_pembuat = ? order by t.tanggal_daftar desc";
        try (Connection c = ConnectionDB.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idEvent);
            ps.setInt(2, idAdminUkm);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Participant p = new Participant();
                    p.setIdTiket(rs.getInt("id_tiket"));
                    p.setNama(rs.getString("nama"));
                    p.setEmail(rs.getString("email"));
                    p.setKodeQr(rs.getString("kode_qr"));
                    p.setStatusBayar(rs.getString("status_bayar"));
                    p.setStatusSertifikat(rs.getString("status_sertifikat"));
                    p.setNominal(rs.getDouble("nominal"));
                    Timestamp ts = rs.getTimestamp("tanggal_daftar");
                    p.setTanggalDaftar(ts == null ? null : ts.toLocalDateTime());
                    p.setHadir(rs.getBoolean("hadir"));
                    list.add(p);
                }
            }
        }
        return list;
    }

    private EventItem map(ResultSet rs) throws SQLException {
        EventItem e = new EventItem();
        e.setIdEvent(rs.getInt("id_event"));
        e.setIdKategori(rs.getInt("id_kategori"));
        e.setNamaKategori(rs.getString("nama_kategori"));
        e.setNama(rs.getString("nama_event"));
        e.setDeskripsi(rs.getString("deskripsi"));
        Timestamp mulai = rs.getTimestamp("tanggal_mulai");
        e.setTanggalMulai(mulai == null ? null : mulai.toLocalDateTime());
        Timestamp selesai = rs.getTimestamp("tanggal_selesai");
        e.setTanggalSelesai(selesai == null ? null : selesai.toLocalDateTime());
        e.setLokasi(rs.getString("lokasi"));
        e.setKuota(rs.getInt("kuota"));
        e.setHarga(rs.getDouble("harga"));
        e.setPoster(rs.getString("poster"));
        e.setJumlahPeserta(rs.getInt("peserta"));
        return e;
    }

    private String emptyToNull(String v) {
        return v == null || v.trim().isEmpty() ? null : v.trim();
    }
}