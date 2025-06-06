/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.absence.salary.slip.application.services;

import com.mycompany.absence.salary.slip.application.models.ShiftPegawai;
import com.mycompany.absence.salary.slip.application.repositories.PegawaiRepository;
import com.mycompany.absence.salary.slip.application.repositories.ShiftPegawaiRepository;
import com.mycompany.absence.salary.slip.application.repositories.ShiftRepository;
import com.mycompany.absence.salary.slip.application.utils.Response;

/**
 *
 * @author User
 */
public class ShiftPegawaiService {
    private final PegawaiRepository pegawaiRepository = new PegawaiRepository();
    private final ShiftRepository shiftRepository = new ShiftRepository();
    private final ShiftPegawaiRepository shiftPegawaiRepository = new ShiftPegawaiRepository();

    private Response<String> validateShiftAssignment(Integer idPegawai, Integer idShift) {
        if (idPegawai == null || idShift == null) {
            return Response.failure("ID Pegawai dan ID Shift tidak boleh null.");
        }

        var pegawaiResult = pegawaiRepository.findById(idPegawai);
        if (!pegawaiResult.isSuccess()) {
            return Response.failure("ID Pegawai tidak valid atau tidak ditemukan.");
        }

        var shiftResult = shiftRepository.findById(idShift);
        if (!shiftResult.isSuccess()) {
            return Response.failure("ID Shift tidak valid atau tidak ditemukan.");
        }

        return Response.success("Validasi berhasil.");
    }

    public Response<ShiftPegawai> assignShiftToPegawai(ShiftPegawai shiftPegawai) {
        var validation = validateShiftAssignment(shiftPegawai.getIdPegawai(), shiftPegawai.getIdShift());
        if (!validation.isSuccess())
            return Response.failure(validation.getMessage());

        var saveResult = shiftPegawaiRepository.save(shiftPegawai);
        return saveResult.isSuccess()
                ? Response.success("Shift berhasil di-assign ke pegawai.", saveResult.getData())
                : Response.failure("Gagal mengassign shift: " + saveResult.getMessage());
    }

    public Response<ShiftPegawai> updateShiftPegawai(ShiftPegawai shiftPegawai) {
        var validation = validateShiftAssignment(shiftPegawai.getIdPegawai(), shiftPegawai.getIdShift());
        if (!validation.isSuccess())
            return Response.failure(validation.getMessage());

        // Pastikan ID yang akan diupdate ada
        var existingShiftsResult = shiftPegawaiRepository.findByPegawaiId(shiftPegawai.getIdPegawai());
        if (!existingShiftsResult.isSuccess()) {
            return Response.failure("Gagal mengambil data shift sebelumnya: " + existingShiftsResult.getMessage());
        }

        boolean found = false;
        for (ShiftPegawai existing : existingShiftsResult.getData()) {
            if (existing.getId().equals(shiftPegawai.getId())) {
                found = true;
                break;
            }
        }

        if (!found) {
            return Response.failure("Data shift pegawai tidak ditemukan untuk diupdate.");
        }

        var updateResult = shiftPegawaiRepository.update(shiftPegawai);
        return updateResult.isSuccess()
                ? Response.success("Shift pegawai berhasil diperbarui.", updateResult.getData())
                : Response.failure("Gagal mengupdate shift pegawai: " + updateResult.getMessage());
    }
}