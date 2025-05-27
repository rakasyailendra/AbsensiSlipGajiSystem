/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 *Revisi
 */
package GUI;

import java.util.List;

//Kelas untuk mencetak slip gaji
public class SlipGaji {
    private Karyawan karyawan;

    public SlipGaji(Karyawan karyawan) {
        this.karyawan = karyawan;
    }

    public int hitungGaji() {
        return karyawan.hitungGaji();
    }

    public String getSlipGaji() {
        StringBuilder slip = new StringBuilder();
        slip.append("Slip Gaji Karyawan\n");
        slip.append("================================\n");
        slip.append("Nama: ").append(karyawan.getNama()).append("\n");
        slip.append("Gaji Bulanan: ").append(hitungGaji()).append("\n");
        slip.append("Detail Absensi:\n");

        for (Absensi absensi : karyawan.getAbsensiList()) {
            slip.append("Tanggal: ").append(absensi.toArray()[0])
                .append(", Status: ").append(absensi.isHadir() ? "Hadir" : "Tidak Hadir").append("\n");
        }

        return slip.toString();
    }
}
