package backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import model.Admin;

public class ProfileDAO {

    public Admin update(Admin admin, String nama, String nim, String email, String noHp, int idUkm)
            throws SQLException {
        String cek = "select 1 from pengguna where lower(email) = lower(?) and id_pengguna <> ?";
        String sqlUser = "update pengguna set nama = ?, nim = ?, email = ?, no_hp = ? where id_pengguna = ?";
        String sqlAdmin = "update admin_ukm set id_ukm = ? where id_admin_ukm = ?";

        Connection c = ConnectionDB.get();
        boolean autoCommit = c.getAutoCommit();
        c.setAutoCommit(false);
        try {
            try (PreparedStatement ps = c.prepareStatement(cek)) {
                ps.setString(1, email.trim());
                ps.setInt(2, admin.getIdPengguna());
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        throw new SQLException("Email sudah dipakai akun lain.");
                    }
                }
            }
            try (PreparedStatement ps = c.prepareStatement(sqlUser)) {
                ps.setString(1, nama.trim());
                ps.setString(2, nim == null || nim.trim().isEmpty() ? null : nim.trim());
                ps.setString(3, email.trim().toLowerCase());
                ps.setString(4, noHp == null || noHp.trim().isEmpty() ? null : noHp.trim());
                ps.setInt(5, admin.getIdPengguna());
                ps.executeUpdate();
            }
            try (PreparedStatement ps = c.prepareStatement(sqlAdmin)) {
                ps.setInt(1, idUkm);
                ps.setInt(2, admin.getIdAdminUkm());
                ps.executeUpdate();
            }
            c.commit();
            return new AuthDAO().refreshAdmin(admin.getIdPengguna(), admin.getIdAdminUkm());
        } catch (SQLException ex) {
            c.rollback();
            throw ex;
        } finally {
            c.setAutoCommit(autoCommit);
        }
    }
}