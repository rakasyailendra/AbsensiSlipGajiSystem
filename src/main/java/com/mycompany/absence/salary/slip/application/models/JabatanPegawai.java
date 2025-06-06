/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.absence.salary.slip.application.models;

/**
 *
 * @author User
 */
public class JabatanPegawai extends BaseEntity {
    private Integer idPegawai;
    private Integer idJabatan;

    public JabatanPegawai() {
        super();
    }

    public JabatanPegawai(Integer idPegawai, Integer idJabatan) {
        super();
        this.idPegawai = idPegawai;
        this.idJabatan = idJabatan;
    }

    public Integer getIdPegawai() {
        return idPegawai;
    }

    public void setIdPegawai(Integer idPegawai) {
        this.idPegawai = idPegawai;
    }

    public Integer getIdJabatan() {
        return idJabatan;
    }

    public void setIdJabatan(Integer idJabatan) {
        this.idJabatan = idJabatan;
    }

    @Override
    public String toString() {
        return "JabatanPegawai{" +
                "id=" + getId() +
                ", idPegawai=" + idPegawai +
                ", idJabatan=" + idJabatan +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                '}';
    }
}