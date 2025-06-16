// Nama File: Shift.java
package GUI;

import java.util.Date;
import java.sql.Time;

public class Shift {
    private int idShift;
    private int idPegawai; // Bisa juga objek Pegawai
    private Date tanggal;
    private Time jamMulai;
    private Time jamSelesai;
    private String keterangan;

    // Konstruktor default
    public Shift() {
    }

    // Konstruktor dengan parameter
    public Shift(int idShift, int idPegawai, Date tanggal, Time jamMulai, Time jamSelesai, String keterangan) {
        this.idShift = idShift;
        this.idPegawai = idPegawai;
        this.tanggal = tanggal;
        setJamMulai(jamMulai);       // Gunakan setter untuk validasi
        setJamSelesai(jamSelesai);   // Gunakan setter untuk validasi
        this.keterangan = keterangan;
    }

    // Getter dan Setter
    public int getIdShift() {
        return idShift;
    }

    public void setIdShift(int idShift) {
        this.idShift = idShift;
    }

    public int getIdPegawai() {
        return idPegawai;
    }

    public void setIdPegawai(int idPegawai) {
        this.idPegawai = idPegawai;
    }

    public Date getTanggal() {
        return tanggal;
    }

    public void setTanggal(Date tanggal) {
        this.tanggal = tanggal;
    }

    public Time getJamMulai() {
        return jamMulai;
    }

    public void setJamMulai(Time jamMulai) {
        this.jamMulai = jamMulai;
    }

    public Time getJamSelesai() {
        return jamSelesai;
    }

    public void setJamSelesai(Time jamSelesai) {
        if (jamMulai != null && jamSelesai != null && jamSelesai.before(jamMulai)) {
            throw new IllegalArgumentException("Jam selesai tidak boleh lebih awal dari jam mulai.");
        }
        this.jamSelesai = jamSelesai;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    // Menghitung durasi shift dalam jam (pembulatan ke jam terdekat)
    public long getDurasiShiftDalamJam() {
        if (jamMulai != null && jamSelesai != null) {
            long millis = jamSelesai.getTime() - jamMulai.getTime();
            return millis / (1000 * 60 * 60); // konversi ke jam
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Shift{" +
               "idShift=" + idShift +
               ", idPegawai=" + idPegawai +
               ", tanggal=" + tanggal +
               ", jamMulai=" + jamMulai +
               ", jamSelesai=" + jamSelesai +
               ", keterangan='" + keterangan + '\'' +
               ", durasi=" + getDurasiShiftDalamJam() + " jam" +
               '}';
    }
}
