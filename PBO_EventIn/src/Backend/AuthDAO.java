package backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Admin;
import model.Ukm;

public class AuthDAO {

    /**
     * Login khusus role admin. Password diverifikasi memakai bcrypt (pgcrypto),
     * sehingga hash lama di database tetap kompatibel.
     */
    public Admin login(String email, String password) throws SQLException {
        String sql = "select p.id_pengguna, p.nama, p.email, p.nim, p.no_hp, p.role, "
                + "a.id_admin_ukm, a.id_ukm, u.nama_ukm, u.singkatan "
                + "from pengguna p "
                + "left join admin_ukm a on a.id_pengguna = p.id_pengguna "
                + "left join ukm u on u.id_ukm = a.id_ukm "
                + "where lower(p.email) = lower(?) and p.role = 'admin' "
                + "and p.password_hash = crypt(?, p.password_hash) "
                + "order by a.id_admin_ukm nulls last limit 1";
        try (Connection c = ConnectionDB.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return mapAdmin(rs);
            }
        }
    }

    /** Registrasi admin baru: pengguna (role admin) + relasi admin_ukm. */
    public Admin register(String nama, String nim, String email, String noHp,
            int idUkm, String password) throws SQLException {
        String cek = "select 1 from pengguna where lower(email) = lower(?)";
        String insertUser = "insert into pengguna (nama, email, no_hp, nim, password_hash, role) "
                + "values (?, ?, ?, ?, crypt(?, gen_salt('bf', 10)), 'admin') "
                + "returning id_pengguna";
        String insertAdmin = "insert into admin_ukm (id_pengguna, id_ukm) values (?, ?) returning id_admin_ukm";

        Connection c = ConnectionDB.get();
        boolean autoCommit = c.getAutoCommit();
        c.setAutoCommit(false);
        try {
            try (PreparedStatement ps = c.prepareStatement(cek)) {
                ps.setString(1, email.trim());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        throw new SQLException("Email sudah terdaftar. Gunakan email lain.");
                    }
                }
            }

            int idPengguna;
            try (PreparedStatement ps = c.prepareStatement(insertUser)) {
                ps.setString(1, nama.trim());
                ps.setString(2, email.trim().toLowerCase());
                ps.setString(3, emptyToNull(noHp));
                ps.setString(4, emptyToNull(nim));
                ps.setString(5, password);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    idPengguna = rs.getInt(1);
                }
            }

            int idAdminUkm;
            try (PreparedStatement ps = c.prepareStatement(insertAdmin)) {
                ps.setInt(1, idPengguna);
                ps.setInt(2, idUkm);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    idAdminUkm = rs.getInt(1);
                }
            }

            c.commit();
            return refreshAdmin(idPengguna, idAdminUkm);
        } catch (SQLException ex) {
            c.rollback();
            throw ex;
        } finally {
            c.setAutoCommit(autoCommit);
        }
    }

    /** Angka ringkas untuk panel informasi di halaman login. */
    public int[] publicStats() throws SQLException {
        String sql = "select (select count(*) from pengguna where role = 'admin') as admins, "
                + "(select count(*) from events where tanggal_selesai >= now()) as events";
        try (Connection c = ConnectionDB.get();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new int[]{rs.getInt("admins"), rs.getInt("events")};
            }
        }
        return new int[]{0, 0};
    }

    public List<Ukm> listUkm() throws SQLException {
        List<Ukm> list = new ArrayList<>();
        String sql = "select id_ukm, nama_ukm, singkatan from ukm order by singkatan";
        try (Connection c = ConnectionDB.get();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Ukm(rs.getInt("id_ukm"), rs.getString("nama_ukm"), rs.getString("singkatan")));
            }
        }
        return list;
    }

    public Admin refreshAdmin(int idPengguna, int idAdminUkm) throws SQLException {
        String sql = "select p.id_pengguna, p.nama, p.email, p.nim, p.no_hp, p.role, "
                + "a.id_admin_ukm, a.id_ukm, u.nama_ukm, u.singkatan "
                + "from pengguna p "
                + "left join admin_ukm a on a.id_pengguna = p.id_pengguna "
                + "left join ukm u on u.id_ukm = a.id_ukm "
                + "where p.id_pengguna = ? and (a.id_admin_ukm = ? or ? = 0) "
                + "order by a.id_admin_ukm nulls last limit 1";
        try (Connection c = ConnectionDB.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idPengguna);
            ps.setInt(2, idAdminUkm);
            ps.setInt(3, idAdminUkm);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return mapAdmin(rs);
            }
        }
    }

    private Admin mapAdmin(ResultSet rs) throws SQLException {
        Admin a = new Admin();
        a.setIdPengguna(rs.getInt("id_pengguna"));
        a.setNama(rs.getString("nama"));
        a.setEmail(rs.getString("email"));
        a.setNim(rs.getString("nim"));
        a.setNoHp(rs.getString("no_hp"));
        a.setRole(rs.getString("role"));
        a.setIdAdminUkm(rs.getInt("id_admin_ukm"));
        a.setIdUkm(rs.getInt("id_ukm"));
        a.setNamaUkm(rs.getString("nama_ukm"));
        a.setSingkatanUkm(rs.getString("singkatan"));
        return a;
    }

    private String emptyToNull(String v) {
        return v == null || v.trim().isEmpty() ? null : v.trim();
    }
}