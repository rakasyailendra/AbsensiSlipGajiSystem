/*
 *
 */ Revisi
package GUI;
public class Absensi {
    private String TANGGAL;
    private String HARI;
    private String JAM;
    private boolean HADIR;

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
