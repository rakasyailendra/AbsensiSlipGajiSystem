package GUI;

import java.util.ArrayList;
import java.util.List;

public class Karyawan {
    private String nama;
    private List<Absensi> absensiList;
    private static final int GAJI_PER_HARI = 50000;

    public Karyawan(String nama) {
        if (nama == null || nama.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama karyawan tidak boleh kosong");
        }
        this.nama = nama;
        this.absensiList = new ArrayList<>();
    }

    public String getNama() {
        return nama;
    }

    public void tambahAbsensi(Absensi absensi) {
        absensiList.add(absensi);
    }

    public int hitungGaji() {
        return (int) absensiList.stream()
                .filter(Absensi::isHadir)
                .count() * GAJI_PER_HARI;
    }

    public List<Absensi> getAbsensiList() {
        return absensiList;
    }

    @Override
    public String toString() {
        return "Karyawan{" +
                "nama='" + nama + '\'' +
                ", jumlahHadir=" + absensiList.stream().filter(Absensi::isHadir).count() +
                ", gaji=" + hitungGaji() +
                '}';
    }
}
