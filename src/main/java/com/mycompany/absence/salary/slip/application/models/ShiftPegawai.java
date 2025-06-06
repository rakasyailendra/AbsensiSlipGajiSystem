/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.absence.salary.slip.application.models;

/**
 *
 * @author User
 */
public class ShiftPegawai extends BaseEntity {
    private Integer idPegawai;
    private Integer idShift;

    public ShiftPegawai() {
        super();
    }

    public ShiftPegawai(Integer idPegawai, Integer idShift) {
        super();
        this.idPegawai = idPegawai;
        this.idShift = idShift;
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

    @Override
    public String toString() {
        return "ShiftPegawai{" + "id=" + getId() + ", idPegawai=" + idPegawai + ", idShift=" + idShift + '}';
    }
}