/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
        this.jamMulai = jamMulai;
        this.jamSelesai = jamSelesai;
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
        this.jamSelesai = jamSelesai;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    @Override
    public String toString() {
        return "Shift{" +
               "idShift=" + idShift +
               ", idPegawai=" + idPegawai +
               ", tanggal=" + tanggal +
               ", jamMulai=" + jamMulai +
               ", jamSelesai=" + jamSelesai +
               '}';
    }
}
