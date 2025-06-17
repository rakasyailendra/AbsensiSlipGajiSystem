// Nama File: Shift.java
package GUI;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Kelas Shift merepresentasikan jadwal kerja seorang pegawai.
 * Kelas ini menggunakan API java.time modern untuk penanganan tanggal dan waktu yang lebih baik.
 *
 * @author [Nama Anda]
 * @version 2.0
 * @since 2024-05-21
 */
public class Shift {

    // --- Atribut ---

    private int idShift;
    private int idPegawai;
    private LocalDate tanggal;
    private LocalTime jamMulai;
    private LocalTime jamSelesai;
    private String keterangan;
    private StatusShift status;

    // Konstanta untuk logika bisnis
    private static final int JAM_KERJA_NORMAL = 8; // Jam kerja standar per hari
    private static final double FAKTOR_UPAH_LEMBUR = 1.5; // Faktor pengali untuk upah lembur

    /**
     * Enumerasi untuk merepresentasikan status shift.
     * Membuat kode lebih mudah dibaca dan dikelola.
     */
    public enum StatusShift {
        BELUM_MULAI("Belum Dimulai"),
        BERLANGSUNG("Sedang Berlangsung"),
        SELESAI("Selesai"),
        DIBATALKAN("Dibatalkan");

        private final String deskripsi;

        StatusShift(String deskripsi) {
            this.deskripsi = deskripsi;
        }

        public String getDeskripsi() {
            return deskripsi;
        }
    }

    /**
     * Kelas pengecualian kustom untuk kesalahan terkait Shift.
     */
    public static class InvalidShiftException extends IllegalArgumentException {
        public InvalidShiftException(String message) {
            super(message);
        }
    }


    // --- Konstruktor ---

    /**
     * Konstruktor default.
     * Menginisialisasi shift dengan nilai default.
     */
    public Shift() {
        this.status = StatusShift.BELUM_MULAI;
        this.tanggal = LocalDate.now(); // Default ke hari ini
    }

    /**
     * Konstruktor dengan parameter lengkap.
     *
     * @param idShift      ID unik untuk shift.
     * @param idPegawai    ID pegawai yang bertugas.
     * @param tanggal      Tanggal shift.
     * @param jamMulai     Waktu mulai shift.
     * @param jamSelesai   Waktu selesai shift.
     * @param keterangan   Deskripsi atau catatan tambahan untuk shift.
     */
    public Shift(int idShift, int idPegawai, LocalDate tanggal, LocalTime jamMulai, LocalTime jamSelesai, String keterangan) {
        setIdShift(idShift);
        setIdPegawai(idPegawai);
        setTanggal(tanggal);
        setJamMulai(jamMulai);
        setJamSelesai(jamSelesai); // Setter ini juga memvalidasi urutan waktu
        setKeterangan(keterangan);
        this.status = StatusShift.BELUM_MULAI; // Status awal
        this.updateStatus(); // Memperbarui status berdasarkan waktu saat ini
    }


    // --- Getter dan Setter dengan Validasi ---

    public int getIdShift() {
        return idShift;
    }

    /**
     * Mengatur ID Shift.
     * @param idShift ID harus positif.
     * @throws InvalidShiftException jika idShift non-positif.
     */
    public void setIdShift(int idShift) {
        if (idShift <= 0) {
            throw new InvalidShiftException("ID Shift harus merupakan angka positif.");
        }
        this.idShift = idShift;
    }

    public int getIdPegawai() {
        return idPegawai;
    }

    /**
     * Mengatur ID Pegawai.
     * @param idPegawai ID harus positif.
     * @throws InvalidShiftException jika idPegawai non-positif.
     */
    public void setIdPegawai(int idPegawai) {
        if (idPegawai <= 0) {
            throw new InvalidShiftException("ID Pegawai harus merupakan angka positif.");
        }
        this.idPegawai = idPegawai;
    }

    public LocalDate getTanggal() {
        return tanggal;
    }

    /**
     * Mengatur tanggal shift.
     * @param tanggal Tanggal tidak boleh null.
     * @throws InvalidShiftException jika tanggal adalah null.
     */
    public void setTanggal(LocalDate tanggal) {
        if (tanggal == null) {
            throw new InvalidShiftException("Tanggal tidak boleh null.");
        }
        this.tanggal = tanggal;
    }

    public LocalTime getJamMulai() {
        return jamMulai;
    }

    /**
     * Mengatur jam mulai shift.
     * @param jamMulai Waktu mulai tidak boleh null.
     * @throws InvalidShiftException jika jamMulai adalah null.
     */
    public void setJamMulai(LocalTime jamMulai) {
        if (jamMulai == null) {
            throw new InvalidShiftException("Jam mulai tidak boleh null.");
        }
        this.jamMulai = jamMulai;
    }

    public LocalTime getJamSelesai() {
        return jamSelesai;
    }

    /**
     * Mengatur jam selesai shift.
     * Melakukan validasi untuk memastikan jam selesai tidak lebih awal dari jam mulai,
     * kecuali untuk shift malam (overnight).
     * @param jamSelesai Waktu selesai tidak boleh null.
     */
    public void setJamSelesai(LocalTime jamSelesai) {
        if (jamSelesai == null) {
            throw new InvalidShiftException("Jam selesai tidak boleh null.");
        }
        // Validasi tidak diperlukan lagi di sini karena getDurasi menangani shift malam
        this.jamSelesai = jamSelesai;
    }

    public String getKeterangan() {
        return keterangan;
    }

    /**
     * Mengatur keterangan shift.
     * @param keterangan Keterangan tidak boleh null atau kosong.
     * @throws InvalidShiftException jika keterangan null atau kosong.
     */
    public void setKeterangan(String keterangan) {
        if (keterangan == null || keterangan.trim().isEmpty()) {
            throw new InvalidShiftException("Keterangan tidak boleh kosong.");
        }
        this.keterangan = keterangan;
    }

    public StatusShift getStatus() {
        return status;
    }

    public void setStatus(StatusShift status) {
        if (status == null) {
            throw new InvalidShiftException("Status tidak boleh null.");
        }
        this.status = status;
    }


    // --- Metode Logika Bisnis & Utilitas ---

    /**
     * Menghitung total durasi shift, sudah menangani shift malam (overnight).
     *
     * @return Objek {@link Duration} yang merepresentasikan durasi shift. Mengembalikan Duration.ZERO jika waktu tidak valid.
     */
    public Duration getDurasi() {
        if (jamMulai == null || jamSelesai == null) {
            return Duration.ZERO;
        }

        LocalDateTime startDateTime = LocalDateTime.of(tanggal, jamMulai);
        LocalDateTime endDateTime;

        // Cek apakah shift melewati tengah malam (jam selesai lebih awal dari jam mulai)
        if (jamSelesai.isBefore(jamMulai)) {
            // Jika ya, tambahkan satu hari ke tanggal selesai
            endDateTime = LocalDateTime.of(tanggal.plusDays(1), jamSelesai);
        } else {
            endDateTime = LocalDateTime.of(tanggal, jamSelesai);
        }

        return Duration.between(startDateTime, endDateTime);
    }

    /**
     * Mengembalikan durasi shift dalam format "X jam Y menit".
     *
     * @return String representasi durasi.
     */
    public String getDurasiFormatted() {
        Duration durasi = getDurasi();
        if (durasi.isZero()) {
            return "0 jam 0 menit";
        }
        long hours = durasi.toHours();
        long minutes = durasi.toMinutes() % 60;
        return String.format("%d jam %d menit", hours, minutes);
    }

    /**
     * Menghitung jam lembur.
     * Lembur dihitung sebagai kelebihan dari jam kerja normal.
     *
     * @return Durasi lembur dalam objek {@link Duration}.
     */
    public Duration getDurasiLembur() {
        Duration durasiTotal = getDurasi();
        Duration durasiNormal = Duration.ofHours(JAM_KERJA_NORMAL);

        if (durasiTotal.compareTo(durasiNormal) > 0) {
            return durasiTotal.minus(durasiNormal);
        } else {
            return Duration.ZERO;
        }
    }

    /**
     * Menghitung estimasi biaya lembur berdasarkan durasi lembur dan faktor pengali.
     *
     * @param tarifPerJam Tarif upah per jam normal pegawai.
     * @return Total biaya lembur.
     */
    public double hitungBiayaLembur(double tarifPerJam) {
        Duration durasiLembur = getDurasiLembur();
        if (durasiLembur.isZero()) {
            return 0.0;
        }
        // Konversi durasi lembur ke jam dalam format desimal
        double jamLembur = durasiLembur.toMinutes() / 60.0;
        return jamLembur * tarifPerJam * FAKTOR_UPAH_LEMBUR;
    }


    /**
     * Memeriksa apakah shift jatuh pada akhir pekan (Sabtu atau Minggu).
     *
     * @return true jika shift terjadi pada akhir pekan, false sebaliknya.
     */
    public boolean isAkhirPekan() {
        if (this.tanggal == null) return false;
        DayOfWeek hari = this.tanggal.getDayOfWeek();
        return hari == DayOfWeek.SATURDAY || hari == DayOfWeek.SUNDAY;
    }

    /**
     * Memperbarui status shift berdasarkan waktu sistem saat ini.
     * Metode ini dapat dipanggil secara berkala untuk menjaga status tetap update.
     */
    public void updateStatus() {
        if (this.status == StatusShift.DIBATALKAN || jamMulai == null || jamSelesai == null) {
            return; // Jangan ubah status jika sudah dibatalkan atau waktu tidak valid
        }

        LocalDateTime sekarang = LocalDateTime.now();
        LocalDateTime waktuMulai = LocalDateTime.of(tanggal, jamMulai);
        LocalDateTime waktuSelesai = waktuMulai.plus(getDurasi());

        if (sekarang.isBefore(waktuMulai)) {
            this.status = StatusShift.BELUM_MULAI;
        } else if (sekarang.isAfter(waktuSelesai)) {
            this.status = StatusShift.SELESAI;
        } else {
            this.status = StatusShift.BERLANGSUNG;
        }
    }


    // --- Override Metode Dasar ---

    /**
     * Menghasilkan representasi string dari objek Shift.
     *
     * @return String yang mendeskripsikan shift.
     */
    @Override
    public String toString() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        return "Shift {" +
                "\n  ID Shift    : " + idShift +
                "\n  ID Pegawai  : " + idPegawai +
                "\n  Tanggal     : " + (tanggal != null ? tanggal.format(dateFormatter) : "N/A") +
                "\n  Mulai       : " + (jamMulai != null ? jamMulai.format(timeFormatter) : "N/A") +
                "\n  Selesai     : " + (jamSelesai != null ? jamSelesai.format(timeFormatter) : "N/A") +
                "\n  Durasi      : " + getDurasiFormatted() +
                "\n  Keterangan  : '" + keterangan + '\'' +
                "\n  Status      : " + status.getDeskripsi() +
                "\n}";
    }

    /**
     * Membandingkan objek Shift ini dengan objek lain.
     * Dua shift dianggap sama jika memiliki ID Shift yang sama.
     *
     * @param o Objek yang akan dibandingkan.
     * @return true jika objek sama, false sebaliknya.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shift shift = (Shift) o;
        return idShift == shift.idShift; // Asumsi ID Shift adalah unik
    }

    /**
     * Menghasilkan nilai hash code untuk objek Shift.
     * Didasarkan pada ID Shift yang unik.
     *
     * @return nilai hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(idShift);
    }

    // --- Metode Main untuk Contoh Penggunaan ---

    /**
     * Metode main untuk mendemonstrasikan fungsionalitas kelas Shift.
     */
    public static void main(String[] args) {
        System.out.println("--- Contoh 1: Shift Siang Hari ---");
        try {
            Shift shiftSiang = new Shift(
                    101,
                    501,
                    LocalDate.of(2024, 5, 22),
                    LocalTime.of(8, 0),
                    LocalTime.of(17, 30),
                    "Shift reguler bagian produksi"
            );
            System.out.println(shiftSiang);
            System.out.println("Durasi Lembur: " + shiftSiang.getDurasiLembur().toHours() + " jam " + shiftSiang.getDurasiLembur().toMinutesPart() + " menit");
            System.out.println("Estimasi Biaya Lembur (Rp50,000/jam): Rp" + String.format("%,.2f", shiftSiang.hitungBiayaLembur(50000)));
            System.out.println("Apakah akhir pekan? " + shiftSiang.isAkhirPekan());

        } catch (InvalidShiftException e) {
            System.err.println("Error membuat shift: " + e.getMessage());
        }

        System.out.println("\n--- Contoh 2: Shift Malam (Overnight) ---");
        try {
            Shift shiftMalam = new Shift(
                    102,
                    502,
                    LocalDate.of(2024, 5, 24), // Mulai hari Jumat
                    LocalTime.of(22, 0),      // Jam 10 malam
                    LocalTime.of(6, 0),       // Jam 6 pagi (hari berikutnya)
                    "Shift malam penjaga keamanan"
            );
            System.out.println(shiftMalam);
            System.out.println("Shift ini jatuh di hari: " + shiftMalam.getTanggal().getDayOfWeek());
            System.out.println("Apakah (mulainya) akhir pekan? " + shiftMalam.isAkhirPekan());

        } catch (InvalidShiftException e) {
            System.err.println("Error membuat shift: " + e.getMessage());
        }

        System.out.println("\n--- Contoh 3: Error, ID Negatif ---");
        try {
            Shift shiftError = new Shift(-1, 503, LocalDate.now(), LocalTime.of(9,0), LocalTime.of(17,0), "Data salah");
            System.out.println(shiftError);
        } catch (InvalidShiftException e) {
            System.err.println("Error terdeteksi: " + e.getMessage());
        }

        System.out.println("\n--- Contoh 4: Memperbarui Status ---");
        try {
            // Membuat shift yang akan dimulai 1 menit dari sekarang
            Shift shiftSekarang = new Shift(
                104,
                504,
                LocalDate.now(),
                LocalTime.now().plusMinutes(1),
                LocalTime.now().plusHours(8),
                "Shift yang akan segera mulai"
            );
            System.out.println("Status Awal: " + shiftSekarang.getStatus().getDeskripsi());
            // Simulasi menunggu
            // Thread.sleep(61000); // Tunggu 61 detik (uncomment untuk mencoba)
            // shiftSekarang.updateStatus();
            // System.out.println("Status Setelah 1 Menit: " + shiftSekarang.getStatus().getDeskripsi());

        } catch (InvalidShiftException e) {
             System.err.println("Error membuat shift: " + e.getMessage());
        }
    }
}
