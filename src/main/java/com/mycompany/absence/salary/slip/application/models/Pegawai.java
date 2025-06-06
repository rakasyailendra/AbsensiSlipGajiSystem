/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.absence.salary.slip.application.models;

import java.util.Date;

/**
 *
 * @author User
 */
public class Pegawai extends BaseEntity {
    private String nip;
    private String nama;
    private Date tanggalLahir;
    private String alamat;
    private String password;
    private Boolean isAdmin;

    public Pegawai() {
        super();
    }

    public Pegawai(String nip, String nama, Date tanggalLahir, String alamat, String password, Boolean isAdmin) {
        super();
        this.nip = nip;
        this.nama = nama;
        this.tanggalLahir = tanggalLahir;
        this.alamat = alamat;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    public String getNip() {
        return nip;
    }

    public void setNip(String nip) {
        this.nip = nip;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public Date getTanggalLahir() {
        return tanggalLahir;
    }

    public void setTanggalLahir(Date tanggalLahir) {
        this.tanggalLahir = tanggalLahir;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    @Override
    public String toString() {
        return "Pegawai{" +
                "id=" + getId() +
                ", nip='" + nip + '\'' +
                ", nama='" + nama + '\'' +
                ", tanggalLahir=" + tanggalLahir +
                ", alamat='" + alamat + '\'' +
                ", password='" + password + '\'' +
                ", isAdmin=" + isAdmin +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                '}';
    }
}