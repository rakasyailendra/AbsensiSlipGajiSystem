package GUI;

import java.util.ArrayList;
import java.util.List;

public class Karyawan {
    private String nama;
    private List<Absensi> absensiList;
    private static final int GAJI_PER_HARI = 50000;

    public Karyawan(String nama) {
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
        int hariHadir = 0;
        for (Absensi absensi : absensiList) {
            if (absensi.isHadir()) {
                hariHadir++;
            }
        }
        return hariHadir * GAJI_PER_HARI;
    }

    public List<Absensi> getAbsensiList() {
        return absensiList;
    }
}
