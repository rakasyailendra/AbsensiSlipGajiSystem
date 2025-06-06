/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.absence.salary.slip.application.models;

/**
 *
 * @author User
 */
public class Jabatan extends BaseEntity {
    private String namaJabatan;
    private Double gajiPokok;

    public Jabatan() {
        super();
    }

    public Jabatan(String namaJabatan, Double gajiPokok) {
        super();
        this.namaJabatan = namaJabatan;
        this.gajiPokok = gajiPokok;
    }

    public String getNamaJabatan() {
        return namaJabatan;
    }

    public void setNamaJabatan(String namaJabatan) {
        this.namaJabatan = namaJabatan;
    }

    public Double getGajiPokok() {
        return gajiPokok;
    }

    public void setGajiPokok(Double gajiPokok) {
        this.gajiPokok = gajiPokok;
    }

    @Override
    public String toString() {
        return "Jabatan{" +
                "namaJabatan='" + namaJabatan + '\'' +
                ", gajiPokok=" + gajiPokok +
                '}';
    }
}