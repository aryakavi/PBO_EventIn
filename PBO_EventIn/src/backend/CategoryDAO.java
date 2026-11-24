package backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Category;

public class CategoryDAO {

    public List<Category> list() throws SQLException {
        List<Category> list = new ArrayList<>();
        String sql = "select id_kategori, nama_kategori from kategori_event order by nama_kategori";
        try (Connection c = ConnectionDB.get();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Category(rs.getInt("id_kategori"), rs.getString("nama_kategori")));
            }
        }
        return list;
    }
}