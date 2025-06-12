/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;

import java.util.Date; // Menggunakan java.util.Date untuk kemudahan dengan JDateChooser
import java.sql.Time;

public class Absensi {
    private int idAbsensi;
    private int idPegawai; // Bisa juga menggunakan objek Pegawai
    private Date tanggal;
    private Time jamMasuk;
    private Time jamKeluar;
    private String fotoBuktiPath;
    private String statusKehadiran; // "hadir", "tidak hadir"

    // Konstruktor default
    public Absensi() {
    }

    // Konstruktor dengan parameter
    public Absensi(int idAbsensi, int idPegawai, Date tanggal, Time jamMasuk, Time jamKeluar, String fotoBuktiPath, String statusKehadiran) {
        this.idAbsensi = idAbsensi;
        this.idPegawai = idPegawai;
        this.tanggal = tanggal;
        this.jamMasuk = jamMasuk;
        this.jamKeluar = jamKeluar;
        this.fotoBuktiPath = fotoBuktiPath;
        this.statusKehadiran = statusKehadiran;
    }

    // Getter dan Setter
    public int getIdAbsensi() {
        return idAbsensi;
    }

    public void setIdAbsensi(int idAbsensi) {
        this.idAbsensi = idAbsensi;
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

    public Time getJamMasuk() {
        return jamMasuk;
    }

    public void setJamMasuk(Time jamMasuk) {
        this.jamMasuk = jamMasuk;
    }

    public Time getJamKeluar() {
        return jamKeluar;
    }

    public void setJamKeluar(Time jamKeluar) {
        this.jamKeluar = jamKeluar;
    }

    public String getFotoBuktiPath() {
        return fotoBuktiPath;
    }

    public void setFotoBuktiPath(String fotoBuktiPath) {
        this.fotoBuktiPath = fotoBuktiPath;
    }

    public String getStatusKehadiran() {
        return statusKehadiran;
    }

    public void setStatusKehadiran(String statusKehadiran) {
        this.statusKehadiran = statusKehadiran;
    }

    @Override
    public String toString() {
        return "Absensi{" +
               "idAbsensi=" + idAbsensi +
               ", idPegawai=" + idPegawai +
               ", tanggal=" + tanggal +
               ", jamMasuk=" + jamMasuk +
               ", jamKeluar=" + jamKeluar +
               ", statusKehadiran='" + statusKehadiran + '\'' +
               '}';
    }
}