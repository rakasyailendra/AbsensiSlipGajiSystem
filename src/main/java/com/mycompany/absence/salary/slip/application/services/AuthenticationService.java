/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.absence.salary.slip.application.services;

import com.mycompany.absence.salary.slip.application.models.Pegawai;
import com.mycompany.absence.salary.slip.application.repositories.PegawaiRepository;
import com.mycompany.absence.salary.slip.application.utils.Response;

/**
 *
 * @author User
 */
public class AuthenticationService {
    PegawaiRepository pegawaiRepository = new PegawaiRepository();

    public Response<Pegawai> authenticate(String nip, String password) {
        if (nip == null || nip.isEmpty() || password == null || password.isEmpty()) {
            return Response.failure("NIP and password cannot be null or empty");
        }

        Response<Pegawai> pegawaiResponse = pegawaiRepository.findByNip(nip);
        if (!pegawaiResponse.isSuccess() || pegawaiResponse.getData() == null) {
            return Response.failure("Authentication failed: NIP not found");
        }

        Pegawai pegawai = pegawaiResponse.getData();
        if (pegawai.getPassword().equals(password)) {
            return Response.success("Authentication successful", pegawai);
        } else {
            return Response.failure("Authentication failed: Invalid NIP or password");
        }
    }
}