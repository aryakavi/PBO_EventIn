package model;

public class ReportRow {

    private String nama;
    private String email;
    private String statusBayar;
    private boolean hadir;
    private String statusSertifikat;

    public String getNama() {
        return nama == null ? "" : nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getEmail() {
        return email == null ? "" : email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatusBayar() {
        return statusBayar == null ? "-" : statusBayar;
    }

    public void setStatusBayar(String statusBayar) {
        this.statusBayar = statusBayar;
    }

    public boolean isHadir() {
        return hadir;
    }

    public void setHadir(boolean hadir) {
        this.hadir = hadir;
    }

    public String getStatusSertifikat() {
        return statusSertifikat == null ? "-" : statusSertifikat;
    }

    public void setStatusSertifikat(String statusSertifikat) {
        this.statusSertifikat = statusSertifikat;
    }

    public String getHadirTampil() {
        return hadir ? "Hadir" : "Tidak Hadir";
    }
}