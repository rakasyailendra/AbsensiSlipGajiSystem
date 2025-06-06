/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.absence.salary.slip.application.models;

import java.time.LocalTime;

/**
 *
 * @author User
 */

public class Shift extends BaseEntity {
    private String namaShift;
    private LocalTime jamMasuk;
    private LocalTime jamKeluar;

    public Shift() {
        super();
    }

    public Shift(String namaShift, LocalTime jamMasuk, LocalTime jamKeluar) {
        super();
        this.namaShift = namaShift;
        this.jamMasuk = jamMasuk;
        this.jamKeluar = jamKeluar;
    }

    public String getNamaShift() {
        return namaShift;
    }

    public void setNamaShift(String namaShift) {
        this.namaShift = namaShift;
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

    @Override
    public String toString() {
        return "Shift{" +
                "id=" + getId() +
                ", namaShift='" + namaShift + '\'' +
                ", jamMasuk=" + jamMasuk +
                ", jamKeluar=" + jamKeluar +
                '}';
    }
}