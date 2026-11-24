package frontend.pages;

/** Halaman yang bisa memuat ulang data dari database saat dibuka. */
public interface Refreshable {

    void refreshData();
}