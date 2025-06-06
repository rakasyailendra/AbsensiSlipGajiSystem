/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.absence.salary.slip.application.services;

import java.util.ArrayList;
import java.util.List;

import com.mycompany.absence.salary.slip.application.models.Jabatan;
import com.mycompany.absence.salary.slip.application.models.JabatanPegawai;
import com.mycompany.absence.salary.slip.application.models.Pegawai;
import com.mycompany.absence.salary.slip.application.repositories.JabatanPegawaiRepository;
import com.mycompany.absence.salary.slip.application.repositories.JabatanRepository;
import com.mycompany.absence.salary.slip.application.repositories.PegawaiRepository;
import com.mycompany.absence.salary.slip.application.utils.Response;

/**
 *
 * @author User
 */
public class JabatanPegawaiService {
    PegawaiRepository pegawaiRepository = new PegawaiRepository();
    JabatanRepository jabatanRepository = new JabatanRepository();
    JabatanPegawaiRepository jabatanPegawaiRepository = new JabatanPegawaiRepository();

    private Response<String> validateAssignJabatan(JabatanPegawai jabatanPegawai) {
        if (jabatanPegawai == null) {
            return Response.failure("JabatanPegawai cannot be null");
        }
        if (jabatanPegawai.getIdJabatan() == null || jabatanPegawai.getIdPegawai() == null) {
            return Response.failure("ID Jabatan and ID Pegawai cannot be null");
        }
        if (!pegawaiRepository.findById(jabatanPegawai.getIdPegawai()).isSuccess()) {
            return Response.failure("Pegawai with ID " + jabatanPegawai.getIdPegawai() + " does not exist");
        }
        if (!jabatanRepository.findById(jabatanPegawai.getIdJabatan()).isSuccess()) {
            return Response.failure("Jabatan with ID " + jabatanPegawai.getIdJabatan() + " does not exist");
        }
        return Response.success("Validation passed");
    }

    private Response<JabatanPegawai> saveJabatanPegawai(JabatanPegawai jabatanPegawai) {
        return jabatanPegawaiRepository.save(jabatanPegawai);
    }

    public Response<JabatanPegawai> assignJabatanToPegawai(JabatanPegawai jabatanPegawai) {
        Response<String> validation = validateAssignJabatan(jabatanPegawai);
        if (!validation.isSuccess()) {
            return Response.failure(validation.getMessage());
        }

        Response<JabatanPegawai> saveRes = saveJabatanPegawai(jabatanPegawai);
        if (saveRes.isSuccess()) {
            return Response.success("JabatanPegawai assigned successfully", saveRes.getData());
        } else {
            return Response.failure("Failed to assign JabatanPegawai: " + saveRes.getMessage());
        }
    }

    public Response<ArrayList<Object>> getAllData() {
        Response<ArrayList<JabatanPegawai>> jabatanPegawaiRes = jabatanPegawaiRepository.findAll();

        if (!jabatanPegawaiRes.isSuccess()) {
            return Response.failure("Failed to retrieve JabatanPegawai: " + jabatanPegawaiRes.getMessage());
        }

        Response<Pegawai> pegawaiRes = pegawaiRepository.findById(jabatanPegawaiRes.getData().get(0).getIdPegawai());
        if (!pegawaiRes.isSuccess()) {
            return Response.failure("Failed to retrieve Pegawai: " + pegawaiRes.getMessage());
        }

        Response<Jabatan> jabatanRes = jabatanRepository.findById(jabatanPegawaiRes.getData().get(0).getIdJabatan());
        if (!jabatanRes.isSuccess()) {
            return Response.failure("Failed to retrieve Jabatan: " + jabatanRes.getMessage());
        }

        ArrayList<Object> result = new ArrayList<>();

        for (JabatanPegawai jabatanPegawai : jabatanPegawaiRes.getData()) {
            List<Object> data = new ArrayList<>();
            data.add(jabatanPegawai);
            data.add(pegawaiRes.getData());
            data.add(jabatanRes.getData());
            result.add(data);
        }

        return Response.success("Data retrieved successfully", result);
    }

    public Response<JabatanPegawai> updateJabatanPegawai(JabatanPegawai jabatanPegawai) {
        if (jabatanPegawai == null || jabatanPegawai.getId() == null) {
            return Response.failure("JabatanPegawai or ID cannot be null");
        }

        Response<JabatanPegawai> existingJabatanPegawai = jabatanPegawaiRepository.findById(jabatanPegawai.getId());
        if (!existingJabatanPegawai.isSuccess()) {
            return Response.failure("JabatanPegawai with ID " + jabatanPegawai.getId() + " does not exist");
        }

        Response<String> validation = validateAssignJabatan(jabatanPegawai);
        if (!validation.isSuccess()) {
            return Response.failure(validation.getMessage());
        }

        Response<ArrayList<JabatanPegawai>> findJabatanByIdPegawaiResponse = jabatanPegawaiRepository
                .findByPegawaiId(jabatanPegawai.getIdPegawai());
        if (!findJabatanByIdPegawaiResponse.isSuccess()) {
            return Response.failure("Failed to retrieve JabatanPegawai for Pegawai ID " + jabatanPegawai.getIdPegawai()
                    + ": " + findJabatanByIdPegawaiResponse.getMessage());
        }

        for (JabatanPegawai existingJabatan : findJabatanByIdPegawaiResponse.getData()) {
            if (existingJabatan.getIdJabatan().equals(jabatanPegawai.getIdJabatan())
                    && !existingJabatan.getId().equals(jabatanPegawai.getId())) {
                Response<Boolean> deleteExistingRes = jabatanPegawaiRepository.deleteById(existingJabatan.getId());
                if (!deleteExistingRes.isSuccess()) {
                    return Response.failure("Failed to delete existing JabatanPegawai with ID "
                            + existingJabatan.getId() + ": " + deleteExistingRes.getMessage());
                }
                System.out.println("Deleted existing JabatanPegawai with ID " + existingJabatan.getId()
                        + " to avoid duplication.");
            }
        }

        Response<JabatanPegawai> updateRes = jabatanPegawaiRepository.update(jabatanPegawai);
        if (updateRes.isSuccess()) {
            return Response.success("JabatanPegawai updated successfully", updateRes.getData());
        } else {
            return Response.failure("Failed to update JabatanPegawai: " + updateRes.getMessage());
        }
    }

    public Response<Boolean> deleteJabatanFromPegawai(Integer id) {
        if (id == null) {
            return Response.failure("ID cannot be null");
        }

        Response<JabatanPegawai> existingJabatanPegawai = jabatanPegawaiRepository.findById(id);
        if (!existingJabatanPegawai.isSuccess()) {
            return Response.failure("JabatanPegawai with ID " + id + " does not exist");
        }

        Response<Boolean> deleteRes = jabatanPegawaiRepository.deleteById(id);
        if (deleteRes.isSuccess()) {
            return Response.success("JabatanPegawai deleted successfully", true);
        } else {
            return Response.failure("Failed to delete JabatanPegawai: " + deleteRes.getMessage());
        }
    }
}