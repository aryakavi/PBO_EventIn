package model;

public class Category {

    private final int id;
    private final String nama;

    public Category(int id, String nama) {
        this.id = id;
        this.nama = nama == null ? "" : nama.trim();
    }

    public int getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    @Override
    public String toString() {
        return nama;
    }
}