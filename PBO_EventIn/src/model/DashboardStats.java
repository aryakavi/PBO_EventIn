package model;

public class DashboardStats {

    private int totalEvent;
    private int eventAktif;
    private int totalPeserta;
    private double pendapatan;
    private int pembayaranMenunggu;

    public int getTotalEvent() {
        return totalEvent;
    }

    public void setTotalEvent(int totalEvent) {
        this.totalEvent = totalEvent;
    }

    public int getEventAktif() {
        return eventAktif;
    }

    public void setEventAktif(int eventAktif) {
        this.eventAktif = eventAktif;
    }

    public int getTotalPeserta() {
        return totalPeserta;
    }

    public void setTotalPeserta(int totalPeserta) {
        this.totalPeserta = totalPeserta;
    }

    public double getPendapatan() {
        return pendapatan;
    }

    public void setPendapatan(double pendapatan) {
        this.pendapatan = pendapatan;
    }

    public int getPembayaranMenunggu() {
        return pembayaranMenunggu;
    }

    public void setPembayaranMenunggu(int pembayaranMenunggu) {
        this.pembayaranMenunggu = pembayaranMenunggu;
    }

    public String getPendapatanTampil() {
        return "Rp " + String.format("%,.0f", pendapatan).replace(',', '.');
    }
}