package model;

/** Akun admin organisasi yang sedang login. */
public class Admin {

    private int idPengguna;
    private int idAdminUkm;
    private int idUkm;
    private String nama;
    private String email;
    private String nim;
    private String noHp;
    private String role;
    private String namaUkm;
    private String singkatanUkm;

    public int getIdPengguna() {
        return idPengguna;
    }

    public void setIdPengguna(int idPengguna) {
        this.idPengguna = idPengguna;
    }

    public int getIdAdminUkm() {
        return idAdminUkm;
    }

    public void setIdAdminUkm(int idAdminUkm) {
        this.idAdminUkm = idAdminUkm;
    }

    public int getIdUkm() {
        return idUkm;
    }

    public void setIdUkm(int idUkm) {
        this.idUkm = idUkm;
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

    public String getNim() {
        return nim == null ? "" : nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getNoHp() {
        return noHp == null ? "" : noHp;
    }

    public void setNoHp(String noHp) {
        this.noHp = noHp;
    }

    public String getRole() {
        return role == null ? "" : role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getNamaUkm() {
        return namaUkm == null ? "-" : namaUkm.trim();
    }

    public void setNamaUkm(String namaUkm) {
        this.namaUkm = namaUkm;
    }

    public String getSingkatanUkm() {
        return singkatanUkm == null ? "-" : singkatanUkm.trim();
    }

    public void setSingkatanUkm(String singkatanUkm) {
        this.singkatanUkm = singkatanUkm;
    }

    public String getInisial() {
        String n = getNama().trim();
        if (n.isEmpty()) {
            return "A";
        }
        String[] parts = n.split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, 1).toUpperCase();
        }
        return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
    }
}