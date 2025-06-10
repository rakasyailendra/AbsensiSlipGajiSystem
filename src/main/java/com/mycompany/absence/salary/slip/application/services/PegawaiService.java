/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.absence.salary.slip.application.services;

import com.mycompany.absence.salary.slip.application.models.JabatanPegawai;
import com.mycompany.absence.salary.slip.application.models.Pegawai;
import com.mycompany.absence.salary.slip.application.repositories.PegawaiRepository;
import com.mycompany.absence.salary.slip.application.utils.Response;

/**
 *
 * @author c0delb08
 */
public class PegawaiService {
    PegawaiRepository pegawaiRepository = new PegawaiRepository();
    JabatanPegawaiService jabatanPegawaiService = new JabatanPegawaiService();

    public Response<Pegawai> savePegawai(Pegawai pegawai, Integer idJabatan) {
        Response<Pegawai> pegawaiResponse = pegawaiRepository.save(pegawai);

        if (pegawaiResponse.isSuccess()) {
            JabatanPegawai jabatanPegawai = new JabatanPegawai();
            jabatanPegawai.setIdPegawai(pegawaiResponse.getData().getId());
            jabatanPegawai.setIdJabatan(idJabatan);

            jabatanPegawaiService.assignJabatanToPegawai(jabatanPegawai);
        }
        return pegawaiResponse;
    }
}
