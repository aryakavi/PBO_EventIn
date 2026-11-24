package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PaymentItem {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    private int idTiket;
    private String namaPeserta;
    private String email;
    private String namaEvent;
    private double nominal;
    private String statusBayar;
    private String bukti;
    private String metode;
    private LocalDateTime waktuUnggah;
    private LocalDateTime tanggalDaftar;

    public int getIdTiket() {
        return idTiket;
    }

    public void setIdTiket(int idTiket) {
        this.idTiket = idTiket;
    }

    public String getNamaPeserta() {
        return namaPeserta == null ? "" : namaPeserta;
    }

    public void setNamaPeserta(String namaPeserta) {
        this.namaPeserta = namaPeserta;
    }

    public String getEmail() {
        return email == null ? "" : email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNamaEvent() {
        return namaEvent == null ? "" : namaEvent;
    }

    public void setNamaEvent(String namaEvent) {
        this.namaEvent = namaEvent;
    }

    public double getNominal() {
        return nominal;
    }

    public void setNominal(double nominal) {
        this.nominal = nominal;
    }

    public String getStatusBayar() {
        return statusBayar == null ? "-" : statusBayar;
    }

    public void setStatusBayar(String statusBayar) {
        this.statusBayar = statusBayar;
    }

    public String getBukti() {
        return bukti == null ? "-" : bukti;
    }

    public void setBukti(String bukti) {
        this.bukti = bukti;
    }

    public String getMetode() {
        return metode == null ? "-" : metode;
    }

    public void setMetode(String metode) {
        this.metode = metode;
    }

    public LocalDateTime getWaktuUnggah() {
        return waktuUnggah;
    }

    public void setWaktuUnggah(LocalDateTime waktuUnggah) {
        this.waktuUnggah = waktuUnggah;
    }

    public String getNominalTampil() {
        return "Rp " + String.format("%,.0f", nominal).replace(',', '.');
    }

    public String getWaktuTampil() {
        return waktuUnggah == null ? "-" : DATE.format(waktuUnggah);
    }

    public LocalDateTime getTanggalDaftar() {
        return tanggalDaftar;
    }

    public void setTanggalDaftar(LocalDateTime tanggalDaftar) {
        this.tanggalDaftar = tanggalDaftar;
    }

    public String getTanggalDaftarTampil() {
        return tanggalDaftar == null ? "-" : DATE.format(tanggalDaftar);
    }
}