/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;
import java.util.ArrayList;
import java.util.List;

public class Karyawan {
    private String nama;
    private List<Absensi> absensiList;
    private static final int GAJI_POKOK = 3000000;
    private static final int POTONGAN_PER_HARI = 10000;

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
        int hariTidakHadir = 0;
        for (Absensi absensi : absensiList) {
            if (!absensi.isHadir()) {
                hariTidakHadir++;
            }
        }
        return GAJI_POKOK - (hariTidakHadir * POTONGAN_PER_HARI);
    }

    public List<Absensi> getAbsensiList() {
        return absensiList;
    }
}