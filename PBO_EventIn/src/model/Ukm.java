package model;

public class Ukm {

    private final int id;
    private final String nama;
    private final String singkatan;

    public Ukm(int id, String nama, String singkatan) {
        this.id = id;
        this.nama = nama;
        this.singkatan = singkatan;
    }

    public int getId() {
        return id;
    }

    public String getNama() {
        return nama == null ? "" : nama.trim();
    }

    public String getSingkatan() {
        return singkatan == null ? "" : singkatan.trim();
    }

    @Override
    public String toString() {
        String singkat = getSingkatan();
        return singkat.isEmpty() ? getNama() : singkat + " - " + getNama();
    }
}