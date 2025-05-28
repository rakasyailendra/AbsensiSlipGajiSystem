/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FileHandler {
    public static void saveKaryawanData(Karyawan karyawan) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("karyawan_data.txt", true))) {
            writer.write("Nama: " + karyawan.getNama() + "\n");
            for (Absensi absensi : karyawan.getAbsensiList()) {
                writer.write("Absensi: " + absensi.toArray()[0] + ", " + absensi.toArray()[1] + ", " + absensi.toArray()[2] + ", " + (absensi.isHadir() ? "Hadir" : "Tidak Hadir") + "\n");
            }
            writer.write("Gaji Bulanan: " + karyawan.hitungGaji() + "\n\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}