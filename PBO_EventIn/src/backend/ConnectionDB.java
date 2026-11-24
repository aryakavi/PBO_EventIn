package backend;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Satu pintu koneksi ke database Supabase (PostgreSQL).
 * Project: aypwdgvqcizpsnccrfwc (region ap-southeast-1) via connection pooler.
 *
 * <p>{@link #get()} mengembalikan satu koneksi bersama. DAO memakai
 * try-with-resources, jadi koneksi yang dikembalikan dibungkus agar
 * {@code close()} tidak benar-benar menutup koneksi fisik. Dengan begitu
 * transaksi manual (misal registrasi admin) tetap berjalan pada koneksi yang sama.
 * Untuk benar-benar menutup koneksi, pakai {@link #close()}.</p>
 */
public final class ConnectionDB {

    private static final String DEFAULT_URL
            = "jdbc:postgresql://aws-0-ap-southeast-1.pooler.supabase.com:5432/postgres?sslmode=require";
    private static final String DEFAULT_USER = "postgres.aypwdgvqcizpsnccrfwc";
    private static final String DEFAULT_PASSWORD = "0VGsfE6Teeyu3hIu";

    private static Connection connection;

    private ConnectionDB() {
    }

    public static synchronized Connection get() throws SQLException {
        if (connection == null || connection.isClosed() || !connection.isValid(5)) {
            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException ex) {
                throw new SQLException("Driver PostgreSQL tidak ditemukan. "
                        + "Pastikan lib/postgresql-42.5.4.jar ada di classpath.", ex);
            }
            connection = DriverManager.getConnection(url(), user(), password());
        }
        return shielded(connection);
    }

    /** Bungkus koneksi agar close() dari try-with-resources tidak menutup koneksi bersama. */
    private static Connection shielded(Connection real) {
        return (Connection) Proxy.newProxyInstance(
                ConnectionDB.class.getClassLoader(),
                new Class<?>[]{Connection.class},
                (proxy, method, args) -> {
                    String name = method.getName();
                    if ("close".equals(name)) {
                        return null;
                    }
                    if ("isClosed".equals(name)) {
                        return real.isClosed();
                    }
                    try {
                        return method.invoke(real, args);
                    } catch (InvocationTargetException ex) {
                        throw ex.getCause();
                    }
                });
    }

    private static String url() {
        String v = System.getenv("EVENTIN_DB_URL");
        return v == null || v.isBlank() ? DEFAULT_URL : v;
    }

    private static String user() {
        String v = System.getenv("EVENTIN_DB_USER");
        return v == null || v.isBlank() ? DEFAULT_USER : v;
    }

    private static String password() {
        String v = System.getenv("EVENTIN_DB_PASSWORD");
        return v == null || v.isBlank() ? DEFAULT_PASSWORD : v;
    }

    /** Uji koneksi dan kembalikan versi server untuk indikator status di UI. */
    public static String testConnection() {
        for (int attempt = 0; attempt < 2; attempt++) {
            try (Statement st = get().createStatement();
                 ResultSet rs = st.executeQuery("select version()")) {
                if (rs.next()) {
                    String version = rs.getString(1);
                    return version.length() > 60 ? version.substring(0, 60) + "..." : version;
                }
                return "Terhubung";
            } catch (SQLException ex) {
                close();
                if (attempt == 1) {
                    return "Gagal: " + ex.getMessage();
                }
            }
        }
        return "Gagal";
    }

    public static synchronized void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ignored) {
            }
            connection = null;
        }
    }
}