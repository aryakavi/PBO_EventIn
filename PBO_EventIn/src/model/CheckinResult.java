package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CheckinResult {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    private int idTiket;
    private String nama;
    private String email;
    private String namaEvent;
    private String lokasi;
    private String kodeQr;
    private String statusBayar;
    private boolean hadir;
    private LocalDateTime waktuCheckin;

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

    public String getNamaEvent() {
        return namaEvent == null ? "" : namaEvent;
    }

    public void setNamaEvent(String namaEvent) {
        this.namaEvent = namaEvent;
    }

    public String getLokasi() {
        return lokasi == null ? "" : lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
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

    public boolean isHadir() {
        return hadir;
    }

    public void setHadir(boolean hadir) {
        this.hadir = hadir;
    }

    public LocalDateTime getWaktuCheckin() {
        return waktuCheckin;
    }

    public void setWaktuCheckin(LocalDateTime waktuCheckin) {
        this.waktuCheckin = waktuCheckin;
    }

    public String getWaktuTampil() {
        return waktuCheckin == null ? "-" : DATE.format(waktuCheckin);
    }
}