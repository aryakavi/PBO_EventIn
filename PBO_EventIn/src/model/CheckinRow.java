package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CheckinRow {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    private String nama;
    private String email;
    private String namaEvent;
    private String kodeQr;
    private LocalDateTime waktuCheckin;

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

    public String getKodeQr() {
        return kodeQr == null ? "" : kodeQr;
    }

    public void setKodeQr(String kodeQr) {
        this.kodeQr = kodeQr;
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