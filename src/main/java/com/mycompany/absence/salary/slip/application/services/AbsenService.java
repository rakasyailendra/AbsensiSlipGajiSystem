/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.absence.salary.slip.application.services;

import java.time.LocalTime;

import com.mycompany.absence.salary.slip.application.models.Absen;
import com.mycompany.absence.salary.slip.application.repositories.AbsenRepository;
import com.mycompany.absence.salary.slip.application.utils.Response;

/**
 *
 * @author User
 */
public class AbsenService {
    AbsenRepository absenRepository = new AbsenRepository();

    private Response<String> validateAbsenData(Integer idPegawai, Integer idShift, java.util.Date tanggal, LocalTime jamMasuk, LocalTime jamKeluar, String fotoMasuk, String fotoKeluar) {
        if (idPegawai == null || idShift == null || tanggal == null) {
            return Response.failure("ID Pegawai, ID Shift, dan Tanggal tidak boleh null.");
        }
        if (jamMasuk == null || jamKeluar == null) {
            return Response.failure("Jam Masuk dan Jam Keluar tidak boleh null.");
        }
        if (fotoMasuk == null || fotoKeluar == null) {
            return Response.failure("Foto Masuk dan Foto Keluar tidak boleh null.");
        }
        // Tambahkan validasi lain jika diperlukan
        return Response.success("Validasi berhasil.");
    }

    public Response<Absen> createAbsen(Absen absen) {
        var validation = validateAbsenData(absen.getIdPegawai(), absen.getIdShift(), absen.getTanggal(), absen.getJamMasuk(), absen.getJamKeluar(), absen.getFotoMasuk(), absen.getFotoKeluar());
        if (!validation.isSuccess()) {
            return Response.failure(validation.getMessage());
        }
        
        var saveResult = absenRepository.save(absen);
        return saveResult.isSuccess()
                ? Response.success("Absen berhasil dibuat.", saveResult.getData())
                : Response.failure("Gagal membuat absen: " + saveResult.getMessage());
    }
}