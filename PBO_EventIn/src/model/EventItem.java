package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class EventItem {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.forLanguageTag("id-ID"));

    private int idEvent;
    private int idKategori;
    private String namaKategori;
    private String nama;
    private String deskripsi;
    private LocalDateTime tanggalMulai;
    private LocalDateTime tanggalSelesai;
    private String lokasi;
    private int kuota;
    private double harga;
    private String poster;
    private int jumlahPeserta;

    public int getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(int idEvent) {
        this.idEvent = idEvent;
    }

    public int getIdKategori() {
        return idKategori;
    }

    public void setIdKategori(int idKategori) {
        this.idKategori = idKategori;
    }

    public String getNamaKategori() {
        return namaKategori == null ? "-" : namaKategori;
    }

    public void setNamaKategori(String namaKategori) {
        this.namaKategori = namaKategori;
    }

    public String getNama() {
        return nama == null ? "" : nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getDeskripsi() {
        return deskripsi == null ? "" : deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public LocalDateTime getTanggalMulai() {
        return tanggalMulai;
    }

    public void setTanggalMulai(LocalDateTime tanggalMulai) {
        this.tanggalMulai = tanggalMulai;
    }

    public LocalDateTime getTanggalSelesai() {
        return tanggalSelesai;
    }

    public void setTanggalSelesai(LocalDateTime tanggalSelesai) {
        this.tanggalSelesai = tanggalSelesai;
    }

    public String getLokasi() {
        return lokasi == null ? "" : lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public int getKuota() {
        return kuota;
    }

    public void setKuota(int kuota) {
        this.kuota = kuota;
    }

    public double getHarga() {
        return harga;
    }

    public void setHarga(double harga) {
        this.harga = harga;
    }

    public String getPoster() {
        return poster == null ? "" : poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public int getJumlahPeserta() {
        return jumlahPeserta;
    }

    public void setJumlahPeserta(int jumlahPeserta) {
        this.jumlahPeserta = jumlahPeserta;
    }

    public String getTanggalTampil() {
        if (tanggalMulai == null) {
            return "-";
        }
        if (tanggalSelesai == null) {
            return DATE.format(tanggalMulai);
        }
        return DATE.format(tanggalMulai) + " - " + DATE.format(tanggalSelesai);
    }

    public String getPesertaTampil() {
        return jumlahPeserta + "/" + kuota;
    }

    public String getStatus() {
        LocalDateTime akhir = tanggalSelesai != null ? tanggalSelesai : tanggalMulai;
        if (akhir == null) {
            return "Draft";
        }
        return akhir.isBefore(LocalDateTime.now()) ? "Selesai" : "Aktif";
    }

    public String getHargaTampil() {
        return "Rp " + String.format("%,.0f", harga).replace(',', '.');
    }
}