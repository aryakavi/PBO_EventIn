package backend;

import model.Admin;

/** Menyimpan admin yang sedang login selama aplikasi berjalan. */
public final class Session {

    private static Admin admin;

    private Session() {
    }

    public static Admin getAdmin() {
        return admin;
    }

    public static void setAdmin(Admin value) {
        admin = value;
    }

    public static void clear() {
        admin = null;
    }

    public static boolean isLoggedIn() {
        return admin != null;
    }

    public static void requireAdmin() {
        if (admin == null) {
            throw new IllegalStateException("Sesi admin tidak ditemukan. Silakan login ulang.");
        }
    }
}