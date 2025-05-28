/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;
public class Absensi {
    private String tanggal;
    private String hari;
    private String jam;
    private boolean hadir;

    public Absensi(String tanggal, String hari, String jam, boolean hadir) {
        this.tanggal = tanggal;
        this.hari = hari;
        this.jam = jam;
        this.hadir = hadir;
    }

    public boolean isHadir() {
        return hadir;
    }

    public String[] toArray() {
        return new String[]{tanggal, hari, jam, hadir ? "Hadir" : "Tidak Hadir"};
    }
}
