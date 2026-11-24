package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Participant {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    private int idTiket;
    private String nama;
    private String email;
    private String kodeQr;
    private String statusBayar;
    private String statusSertifikat;
    private double nominal;
    private LocalDateTime tanggalDaftar;
    private boolean hadir;

    public int getIdTiket() {
        return idTiket;
    }

    public void setIdTiket(int idTiket) {
        this.idTiket = idTiket;
    }

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

    public String getKodeQr() {
        return kodeQr == null ? "" : kodeQr;
    }

    public void setKodeQr(String kodeQr) {
        this.kodeQr = kodeQr;
    }

    public String getStatusBayar() {
        return statusBayar == null ? "-" : statusBayar;
    }

    public void setStatusBayar(String statusBayar) {
        this.statusBayar = statusBayar;
    }

    public String getStatusSertifikat() {
        return statusSertifikat == null ? "-" : statusSertifikat;
    }

    public void setStatusSertifikat(String statusSertifikat) {
        this.statusSertifikat = statusSertifikat;
    }

    public double getNominal() {
        return nominal;
    }

    public void setNominal(double nominal) {
        this.nominal = nominal;
    }

    public LocalDateTime getTanggalDaftar() {
        return tanggalDaftar;
    }

    public void setTanggalDaftar(LocalDateTime tanggalDaftar) {
        this.tanggalDaftar = tanggalDaftar;
    }

    public boolean isHadir() {
        return hadir;
    }

    public void setHadir(boolean hadir) {
        this.hadir = hadir;
    }

    public String getTanggalTampil() {
        return tanggalDaftar == null ? "-" : DATE.format(tanggalDaftar);
    }

    public String getNominalTampil() {
        return "Rp " + String.format("%,.0f", nominal).replace(',', '.');
    }
}