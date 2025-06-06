/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.absence.salary.slip.application.models;

import java.time.LocalTime;
import java.util.Date;

/**
 *
 * @author User
 */
public class Absen extends BaseEntity {
    private Integer idPegawai;
    private Integer idShift;
    private Date tanggal;
    private LocalTime jamMasuk;
    private LocalTime jamKeluar;
    private String fotoMasuk;
    private String fotoKeluar;

    public Absen() {
        super();
    }

    public Absen(Integer idPegawai, Integer idShift, Date tanggal, LocalTime jamMasuk, LocalTime jamKeluar,
            String fotoMasuk, String fotoKeluar) {
        super();
        this.idPegawai = idPegawai;
        this.idShift = idShift;
        this.tanggal = tanggal;
        this.jamMasuk = jamMasuk;
        this.jamKeluar = jamKeluar;
        this.fotoMasuk = fotoMasuk;
        this.fotoKeluar = fotoKeluar;
    }

    public Integer getIdPegawai() {
        return idPegawai;
    }

    public void setIdPegawai(Integer idPegawai) {
        this.idPegawai = idPegawai;
    }

    public Integer getIdShift() {
        return idShift;
    }

    public void setIdShift(Integer idShift) {
        this.idShift = idShift;
    }

    public Date getTanggal() {
        return tanggal;
    }

    public void setTanggal(Date tanggal) {
        this.tanggal = tanggal;
    }

    public LocalTime getJamMasuk() {
        return jamMasuk;
    }

    public void setJamMasuk(LocalTime jamMasuk) {
        this.jamMasuk = jamMasuk;
    }

    public LocalTime getJamKeluar() {
        return jamKeluar;
    }

    public void setJamKeluar(LocalTime jamKeluar) {
        this.jamKeluar = jamKeluar;
    }

    public String getFotoMasuk() {
        return fotoMasuk;
    }

    public void setFotoMasuk(String fotoMasuk) {
        this.fotoMasuk = fotoMasuk;
    }

    public String getFotoKeluar() {
        return fotoKeluar;
    }

    public void setFotoKeluar(String fotoKeluar) {
        this.fotoKeluar = fotoKeluar;
    }

    @Override
    public String toString() {
        return "Absen{" +
                "id=" + getId() +
                ", idPegawai=" + idPegawai +
                ", idShift=" + idShift +
                ", tanggal=" + tanggal +
                ", jamMasuk=" + jamMasuk +
                ", jamKeluar=" + jamKeluar +
                ", fotoMasuk='" + fotoMasuk + '\'' +
                ", fotoKeluar='" + fotoKeluar + '\'' +
                '}';
    }
}