package GUI;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

// Untuk simulasi hashing. Di aplikasi nyata, gunakan library seperti jBcrypt atau Spring Security.
import org.mindrot.jbcrypt.BCrypt; 

/**
 * Kelas Admin yang disempurnakan dengan fitur keamanan, manajemen hak akses, dan logging.
 * Kelas ini merepresentasikan seorang administrator sistem dengan fungsionalitas yang lengkap.
 *
 * @author [Nama Anda]
 * @version 3.0
 * @since 2025-06-17
 */
public class Admin {

    // --- Enumerasi untuk Status dan Hak Akses ---

    /**
     * Mendefinisikan status akun seorang admin.
     */
    public enum StatusAkun {
        AKTIF("Aktif"),
        NONAKTIF("Nonaktif"),
        TERKUNCI("Terkunci");

        private final String deskripsi;
        StatusAkun(String deskripsi) { this.deskripsi = deskripsi; }
        public String getDeskripsi() { return deskripsi; }
    }

    /**
     * Mendefinisikan hak akses spesifik yang bisa dimiliki oleh admin.
     */
    public enum HakAkses {
        KELOLA_PEGAWAI("Mengelola data pegawai (CRUD)"),
        ATUR_JADWAL_SHIFT("Mengatur jadwal shift kerja"),
        BUAT_LAPORAN("Membuat dan mencetak laporan"),
        KELOLA_GAJI("Menghitung dan memproses slip gaji"),
        KELOLA_ADMIN("Mengelola akun administrator lain");

        private final String deskripsi;
        HakAkses(String deskripsi) { this.deskripsi = deskripsi; }
        public String getDeskripsi() { return deskripsi; }
    }

    /**
     * Pengecualian kustom untuk kesalahan terkait operasi Admin.
     */
    public static class AdminException extends RuntimeException {
        public AdminException(String message) {
            super(message);
        }
    }

    // --- Atribut Kelas ---

    private int idAdmin;
    private String username;
    private String hashedPassword; // Menyimpan hash dari password, bukan plaintext
    private String namaLengkap;
    private String email;
    private String jabatan;
    private StatusAkun statusAkun;
    private final LocalDateTime tanggalDibuat;
    private LocalDateTime terakhirLogin;
    private Set<HakAkses> daftarHakAkses;

    // Pola regex sederhana untuk validasi email
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    // --- Konstruktor ---

    /**
     * Konstruktor untuk membuat Admin baru.
     *
     * @param idAdmin      ID unik admin.
     * @param username     Username untuk login.
     * @param namaLengkap  Nama lengkap admin.
     * @param email        Alamat email yang valid.
     * @param jabatan      Jabatan admin di perusahaan.
     */
    public Admin(int idAdmin, String username, String namaLengkap, String email, String jabatan) {
        this.idAdmin = idAdmin;
        this.setUsername(username);
        this.setNamaLengkap(namaLengkap);
        this.setEmail(email);
        this.setJabatan(jabatan);
        
        this.tanggalDibuat = LocalDateTime.now();
        this.statusAkun = StatusAkun.AKTIF; // Default status adalah aktif
        this.daftarHakAkses = new HashSet<>();
        log("Admin baru dibuat: " + username);
    }

    // --- Getter dan Setter dengan Validasi dan Keamanan ---

    public int getIdAdmin() { return idAdmin; }
    public void setIdAdmin(int idAdmin) { this.idAdmin = idAdmin; }

    public String getUsername() { return username; }
    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new AdminException("Username tidak boleh kosong.");
        }
        this.username = username;
    }

    // Getter untuk hash tidak disediakan untuk keamanan.
    
    /**
     * Mengatur dan melakukan hashing pada password.
     * Metode ini menerapkan kebijakan keamanan password.
     *
     * @param plainTextPassword Password asli yang akan di-hash.
     */
    public void setAndHashPassword(String plainTextPassword) {
        if (plainTextPassword == null || plainTextPassword.length() < 8) {
            throw new AdminException("Password minimal harus 8 karakter.");
        }
        // Di aplikasi nyata, bisa ditambahkan kebijakan lebih kompleks (angka, huruf besar, simbol)
        this.hashedPassword = BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(12));
        log("Password untuk admin '" + this.username + "' telah diatur/diubah.");
    }

    public String getNamaLengkap() { return namaLengkap; }
    public void setNamaLengkap(String namaLengkap) { this.namaLengkap = namaLengkap; }

    public String getEmail() { return email; }
    public void setEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new AdminException("Format email tidak valid.");
        }
        this.email = email;
    }

    public String getJabatan() { return jabatan; }
    public void setJabatan(String jabatan) { this.jabatan = jabatan; }

    public StatusAkun getStatusAkun() { return statusAkun; }
    public void setStatusAkun(StatusAkun statusAkun) {
        this.statusAkun = statusAkun;
        log("Status akun untuk '" + this.username + "' diubah menjadi: " + statusAkun.getDeskripsi());
    }

    public String getTanggalDibuatFormatted() {
        return tanggalDibuat.format(DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm"));
    }

    public String getTerakhirLoginFormatted() {
        if (terakhirLogin == null) {
            return "Belum pernah login";
        }
        return terakhirLogin.format(DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm"));
    }
    
    public Set<HakAkses> getDaftarHakAkses() {
        return new HashSet<>(daftarHakAkses); // Kembalikan salinan untuk melindungi data asli
    }

    // --- Metode Fungsional ---

    /**
     * Memeriksa apakah password yang diberikan cocok dengan hash yang tersimpan.
     *
     * @param plainTextPassword Password yang akan dicek.
     * @return true jika cocok, false jika tidak.
     */
    public boolean checkPassword(String plainTextPassword) {
        if (this.hashedPassword == null || plainTextPassword == null) {
            return false;
        }
        if (statusAkun != StatusAkun.AKTIF) {
            log("Upaya login gagal untuk akun '" + this.username + "' karena status: " + statusAkun.getDeskripsi());
            return false;
        }
        
        boolean isPasswordMatch = BCrypt.checkpw(plainTextPassword, this.hashedPassword);
        
        if (isPasswordMatch) {
            this.terakhirLogin = LocalDateTime.now();
            log("Admin '" + this.username + "' berhasil login.");
        } else {
            log("Upaya login gagal untuk admin '" + this.username + "' (password salah).");
        }
        return isPasswordMatch;
    }
    
    public void beriHakAkses(HakAkses hak) {
        this.daftarHakAkses.add(hak);
        log("Hak akses '" + hak.name() + "' diberikan kepada '" + this.username + "'.");
    }

    public void cabutHakAkses(HakAkses hak) {
        this.daftarHakAkses.remove(hak);
        log("Hak akses '" + hak.name() + "' dicabut dari '" + this.username + "'.");
    }
    
    public boolean punyaHakAkses(HakAkses hak) {
        return this.daftarHakAkses.contains(hak);
    }
    
    /**
     * Metode logging sederhana untuk melacak aktivitas penting.
     * @param message Pesan log.
     */
    private void log(String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        System.out.println("[LOG - Admin] " + timestamp + " | " + message);
    }

    // --- Override Metode Dasar ---

    @Override
    public String toString() {
        return "Admin{" +
                "idAdmin=" + idAdmin +
                ", username='" + username + '\'' +
                ", namaLengkap='" + namaLengkap + '\'' +
                ", email='" + email + '\'' +
                ", jabatan='" + jabatan + '\'' +
                ", statusAkun=" + statusAkun +
                ", tanggalDibuat=" + getTanggalDibuatFormatted() +
                ", hakAkses=" + daftarHakAkses.size() +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Admin admin = (Admin) o;
        return idAdmin == admin.idAdmin && username.equals(admin.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idAdmin, username);
    }
    
    // --- Metode Main untuk Demonstrasi ---
    
    public static void main(String[] args) {
        System.out.println("--- Skenario Demonstrasi Kelas Admin ---");
        
        try {
            // 1. Membuat admin baru
            Admin superAdmin = new Admin(1, "superadmin", "Administrator Utama", "admin@perusahaan.com", "IT Manager");
            
            // 2. Mengatur password dengan aman
            superAdmin.setAndHashPassword("PasswordKuat123!");
            
            // 3. Memberikan semua hak akses
            superAdmin.beriHakAkses(HakAkses.KELOLA_PEGAWAI);
            superAdmin.beriHakAkses(HakAkses.KELOLA_ADMIN);
            superAdmin.beriHakAkses(HakAkses.BUAT_LAPORAN);
            
            System.out.println("\nInfo Admin Awal:\n" + superAdmin);
            
            // 4. Skenario login
            System.out.println("\n--- Tes Login ---");
            System.out.println("Login dengan password salah: " + superAdmin.checkPassword("passwordsalah"));
            System.out.println("Login dengan password benar: " + superAdmin.checkPassword("PasswordKuat123!"));
            System.out.println("Waktu terakhir login: " + superAdmin.getTerakhirLoginFormatted());
            
            // 5. Skenario pengecekan hak akses
            System.out.println("\n--- Tes Hak Akses ---");
            System.out.println("Punya hak kelola pegawai? " + superAdmin.punyaHakAkses(HakAkses.KELOLA_PEGAWAI));
            System.out.println("Punya hak kelola gaji? " + superAdmin.punyaHakAkses(HakAkses.KELOLA_GAJI));
            
            // 6. Mencabut hak akses
            superAdmin.cabutHakAkses(HakAkses.BUAT_LAPORAN);
            System.out.println("Hak akses setelah 'BUAT_LAPORAN' dicabut: " + superAdmin.getDaftarHakAkses());

            // 7. Mengubah status akun
            superAdmin.setStatusAkun(StatusAkun.NONAKTIF);
            System.out.println("\nLogin setelah akun dinonaktifkan: " + superAdmin.checkPassword("PasswordKuat123!"));
            
        } catch (AdminException e) {
            System.err.println("\n[ERROR] Terjadi kesalahan: " + e.getMessage());
        }
    }
}
