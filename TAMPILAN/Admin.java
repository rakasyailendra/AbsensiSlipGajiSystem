package GUI;

public class Admin {
    private int idAdmin;
    private String username;
    private String password; // Hindari plaintext di aplikasi nyata
    private String namaLengkap;
    private String jabatan;
    private final String peran = "admin"; // Final karena peran khusus

    // Konstruktor default
    public Admin() {
    }

    // Konstruktor parameter
    public Admin(int idAdmin, String username, String password, String namaLengkap, String jabatan) {
        this.idAdmin = idAdmin;
        this.setUsername(username);
        this.setPassword(password);
        this.namaLengkap = namaLengkap;
        this.jabatan = jabatan;
    }

    // Getter dan Setter
    public int getIdAdmin() {
        return idAdmin;
    }

    public void setIdAdmin(int idAdmin) {
        this.idAdmin = idAdmin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username != null && !username.trim().isEmpty()) {
            this.username = username;
        } else {
            throw new IllegalArgumentException("Username tidak boleh kosong.");
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password != null && password.length() >= 4) {
            this.password = password;
        } else {
            throw new IllegalArgumentException("Password minimal 4 karakter.");
        }
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
    }

    public String getJabatan() {
        return jabatan;
    }

    public void setJabatan(String jabatan) {
        this.jabatan = jabatan;
    }

    public String getPeran() {
        return peran;
    }

    // Cek login sederhana (untuk simulasi lokal, bukan produksi)
    public boolean loginCheck(String inputUsername, String inputPassword) {
        return this.username.equals(inputUsername) && this.password.equals(inputPassword);
    }

    @Override
    public String toString() {
        return "Admin{" +
               "idAdmin=" + idAdmin +
               ", username='" + username + '\'' +
               ", namaLengkap='" + namaLengkap + '\'' +
               ", jabatan='" + jabatan + '\'' +
               ", peran='" + peran + '\'' +
               '}';
    }
}
