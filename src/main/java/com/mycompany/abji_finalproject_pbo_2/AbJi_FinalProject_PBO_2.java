/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.abji_finalproject_pbo_2;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import com.mycompany.absence.salary.slip.application.models.Absen;
import com.mycompany.absence.salary.slip.application.models.Jabatan;
import com.mycompany.absence.salary.slip.application.models.JabatanPegawai;
import com.mycompany.absence.salary.slip.application.models.Pegawai;
import com.mycompany.absence.salary.slip.application.models.Shift;
import com.mycompany.absence.salary.slip.application.models.ShiftPegawai;
import com.mycompany.absence.salary.slip.application.repositories.AbsenRepository;
import com.mycompany.absence.salary.slip.application.repositories.JabatanRepository;
import com.mycompany.absence.salary.slip.application.repositories.PegawaiRepository;
import com.mycompany.absence.salary.slip.application.repositories.ShiftPegawaiRepository;
import com.mycompany.absence.salary.slip.application.repositories.ShiftRepository;
import com.mycompany.absence.salary.slip.application.services.AbsenService;
import com.mycompany.absence.salary.slip.application.services.JabatanPegawaiService;
import com.mycompany.absence.salary.slip.application.services.ShiftPegawaiService;
import com.mycompany.absence.salary.slip.application.utils.Response;
import com.mycompany.absence.salary.slip.application.view.loginForm;
/**
 *
 * @author User
 */
public class AbJi_FinalProject_PBO_2 {

    public static void main(String[] args) {
        System.out.println("Hello, Absence Salary Slip Application!");
        // runTests(); 
        runProgram();
        // seedDatabase();
    }

    private static void runProgram() {
        System.out.println("Running Absence Salary Slip Application...");

        loginForm loginPage = new loginForm();
        loginPage.setVisible(true);
    }

    private static void seedDatabase() {
        System.out.println("Seeding database...");

        Pegawai pegawai = new Pegawai();
        Pegawai pegawai2 = new Pegawai();
        Jabatan jabatan = new Jabatan();
        Shift shift = new Shift();
        JabatanPegawai jabatanPegawai = new JabatanPegawai();
        ShiftPegawai shiftPegawai = new ShiftPegawai();
        Absen absen = new Absen();

        PegawaiRepository pegawaiRes = new PegawaiRepository();
        JabatanRepository jabatanRepository = new JabatanRepository();
        ShiftRepository shiftRepository = new ShiftRepository();
        JabatanPegawaiService jabatanPegawaiService = new JabatanPegawaiService();
        ShiftPegawaiService shiftPegawaiService = new ShiftPegawaiService();
        AbsenService absenService = new AbsenService();
        

        pegawai.setNip("");
        pegawai.setNama("");
        pegawai.setTanggalLahir(LocalDate.now());
        pegawai.setAlamat("");
        pegawai.setPassword("");
        pegawai.setIsAdmin(true);

        Response<Pegawai> pegawaiResponse = pegawaiRes.save(pegawai);
        if (pegawaiResponse.isSuccess()) {
            System.out.println("Pegawai saved successfully: " + pegawaiResponse.getData());
        } else {
            System.out.println("Failed to save Pegawai: " + pegawaiResponse.getMessage());
            return;
        }

        pegawai2.setNip("");
        pegawai2.setNama("");
        pegawai2.setTanggalLahir(LocalDate.now());
        pegawai2.setAlamat("");
        pegawai2.setPassword("");
        pegawai2.setIsAdmin(false);

        Response<Pegawai> pegawai2Response = pegawaiRes.save(pegawai2);
        if (pegawai2Response.isSuccess()) {
            System.out.println("Pegawai2 saved successfully: " + pegawai2Response.getData());
        } else {
            System.out.println("Failed to save Pegawai2: " + pegawai2Response.getMessage());
            return;
        }

        jabatan.setNamaJabatan("Software Engineer");
        jabatan.setGajiPokok(5000000.0);

        Response<Jabatan> saveJabatanResponse = jabatanRepository.save(jabatan);
        if (saveJabatanResponse.isSuccess()) {
            System.out.println("Jabatan saved successfully: " + saveJabatanResponse.getData());
        } else {
            System.out.println("Failed to save Jabatan: " + saveJabatanResponse.getMessage());
            return;
        }

        shift.setNamaShift("Morning Shift");
        shift.setJamMasuk(LocalTime.of(8, 0));
        shift.setJamKeluar(LocalTime.of(16, 0));

        Response<Shift> saveShiftResponse = shiftRepository.save(shift);
        if (saveShiftResponse.isSuccess()) {
            System.out.println("Shift saved successfully: " + saveShiftResponse.getData());
        } else {
            System.out.println("Failed to save Shift: " + saveShiftResponse.getMessage());
            return;
        }

        jabatanPegawai.setIdPegawai(pegawai.getId());
        jabatanPegawai.setIdJabatan(jabatan.getId());

        Response<JabatanPegawai> saveJabatanPegawaiResponse = jabatanPegawaiService.assignJabatanToPegawai(jabatanPegawai);
        if (saveJabatanPegawaiResponse.isSuccess()) {
            System.out.println("JabatanPegawai saved successfully: " + saveJabatanPegawaiResponse.getData());
        } else {
            System.out.println("Failed to save JabatanPegawai: " + saveJabatanPegawaiResponse.getMessage());
            return;
        }

        shiftPegawai.setIdPegawai(pegawai.getId());
        shiftPegawai.setIdShift(shift.getId());

        Response<ShiftPegawai> saveShiftPegawaiResponse = shiftPegawaiService.assignShiftToPegawai(shiftPegawai);
        if (saveShiftPegawaiResponse.isSuccess()) {
            System.out.println("ShiftPegawai saved successfully: " + saveShiftPegawaiResponse.getData());
        } else {
            System.out.println("Failed to save ShiftPegawai: " + saveShiftPegawaiResponse.getMessage());
            return;
        }

        absen.setIdPegawai(pegawai.getId());
        absen.setIdShift(shift.getId());
        absen.setTanggal(LocalDate.now());
        absen.setJamMasuk(LocalTime.of(8, 30));
        absen.setJamKeluar(LocalTime.of(16, 30));

        Response<Absen> saveAbsenResponse = absenService.createAbsen(absen);
        if (saveAbsenResponse.isSuccess()) {
            System.out.println("Absen saved successfully: " + saveAbsenResponse.getData());
        } else {
            System.out.println("Failed to save Absen: " + saveAbsenResponse.getMessage());
            return;
        }
    }

    private static void runTests() {
        System.out.println("Running tests...");

        // testPegawai();
        // testJabatan();
        // testShift();
        // jabatanPegawai();
        // testShiftPegawai();
        // absenTest();
    }

    private static void testPegawai() {
        System.out.println("Testing Pegawai class...");

        Pegawai pegawai = new Pegawai();
        pegawai.setNip("123456789");
        pegawai.setNama("John Doe");
        pegawai.setTanggalLahir(LocalDate.now());
        pegawai.setAlamat("123 Main St");
        pegawai.setPassword("password123");
        pegawai.setIsAdmin(true);

        PegawaiRepository repository = new PegawaiRepository();
        Response<Pegawai> saveResponse = repository.save(pegawai);
        if (saveResponse.isSuccess()) {
            System.out.println("Pegawai saved successfully: " + saveResponse.getData());
        } else {
            System.out.println("Failed to save Pegawai: " + saveResponse.getMessage());
            return;
        }

        // Response<Pegawai> findByIdResponse = repository.findById(saveResponse.getData().getId());
        // if (findByIdResponse.isSuccess()) {
        //     System.out.println("Pegawai found: " + findByIdResponse.getData());
        // } else {
        //     System.out.println("Failed to find Pegawai: " + findByIdResponse.getMessage());
        // }

        // Response<ArrayList<Pegawai>> findAllResponse = repository.findAll();
        // if (findAllResponse.isSuccess() && findAllResponse.getData() != null && !findAllResponse.getData().isEmpty()) {
        //     System.out.println("All Pegawai:");
        //     for (Pegawai p : findAllResponse.getData()) {
        //         System.out.println(p);
        //     }
        // } else {
        //     System.out.println("No Pegawai found.");
        // }

        // AuthenticationService authenticationService = new AuthenticationService();

        // Response<Pegawai> authenticateResponse = authenticationService.authenticate(saveResponse.getData().getNip(),
        //         saveResponse.getData().getPassword());
        // if (authenticateResponse.isSuccess()) {
        //     System.out.println("Authentication successful: " + authenticateResponse.getData());
        // } else {
        //     System.out.println("Authentication failed: " + authenticateResponse.getMessage());
        // }

        // pegawai.setNama("Jane Doe");
        // Response<Pegawai> updateResponse = repository.update(pegawai);
        // if (updateResponse.isSuccess()) {
        //     System.out.println("Pegawai updated successfully: " + updateResponse.getData());
        // } else {
        //     System.out.println("Failed to update Pegawai: " + updateResponse.getMessage());
        // }

        // Response<Boolean> deleteResponse = repository.deleteById(pegawai.getId());
        // if (deleteResponse.isSuccess()) {
        //     System.out.println("Pegawai deleted successfully.");
        // } else {
        //     System.out.println("Failed to delete Pegawai: " + deleteResponse.getMessage());
        // }
    }

    public static void testShift() {
        System.out.println("Testing Shift class...");

        Shift shift = new Shift();
        shift.setNamaShift("Morning Shift");
        shift.setJamMasuk(LocalTime.of(8, 0));
        shift.setJamKeluar(LocalTime.of(16, 0));

        ShiftRepository repository = new ShiftRepository();

        Response<Shift> saveResponse = repository.save(shift);
        if (saveResponse.isSuccess()) {
            System.out.println("Shift saved successfully: " + saveResponse.getData());
        } else {
            System.out.println("Failed to save Shift: " + saveResponse.getMessage());
            return;
        }

        int id = saveResponse.getData().getId();

        Response<Shift> findByIdResponse = repository.findById(id);
        if (findByIdResponse.isSuccess()) {
            System.out.println("Shift found: " + findByIdResponse.getData());
        } else {
            System.out.println("Failed to find Shift: " + findByIdResponse.getMessage());
        }

        Response<ArrayList<Shift>> findAllResponse = repository.findAll();
        if (findAllResponse.isSuccess() && findAllResponse.getData() != null && !findAllResponse.getData().isEmpty()) {
            System.out.println("All Shifts:");
            for (Shift s : findAllResponse.getData()) {
                System.out.println(s);
            }
        } else {
            System.out.println("No Shifts found.");
        }

        shift.setNamaShift("Evening Shift");
        shift.setJamKeluar(LocalTime.of(20, 0)); // 20:00
        Response<Shift> updateResponse = repository.update(shift);
        if (updateResponse.isSuccess()) {
            System.out.println("Shift updated successfully: " + updateResponse.getData());
        } else {
            System.out.println("Failed to update Shift: " + updateResponse.getMessage());
        }

        Response<Boolean> deleteResponse = repository.deleteById(shift.getId());
        if (deleteResponse.isSuccess()) {
            System.out.println("Shift deleted successfully.");
        } else {
            System.out.println("Failed to delete Shift: " + deleteResponse.getMessage());
        }
    }

    public static void testJabatan() {
        System.out.println("Testing Jabatan class...");

        Jabatan jabatan = new Jabatan();
        jabatan.setNamaJabatan("Software Engineer");
        jabatan.setGajiPokok(5000000.0);

        JabatanRepository repository = new JabatanRepository();
        Response<Jabatan> saveResponse = repository.save(jabatan);
        if (saveResponse.isSuccess()) {
            System.out.println("Jabatan saved successfully: " + saveResponse.getData());
        } else {
            System.out.println("Failed to save Jabatan: " + saveResponse.getMessage());
            return;
        }

        int id = saveResponse.getData().getId();
        Response<Jabatan> findByIdResponse = repository.findById(id);
        if (findByIdResponse.isSuccess()) {
            System.out.println("Jabatan found: " + findByIdResponse.getData());
        } else {
            System.out.println("Failed to find Jabatan: " + findByIdResponse.getMessage());
        }

        Response<ArrayList<Jabatan>> findAllResponse = repository.findAll();
        if (findAllResponse.isSuccess() && findAllResponse.getData() != null && !findAllResponse.getData().isEmpty()) {
            System.out.println("All Jabatan:");
            for (Jabatan j : findAllResponse.getData()) {
                System.out.println(j);
            }
        } else {
            System.out.println("No Jabatan found.");
        }

        // Update Jabatan
        jabatan.setNamaJabatan("Senior Software Engineer");
        jabatan.setGajiPokok(7000000.0);
        Response<Jabatan> updateResponse = repository.update(jabatan);
        if (updateResponse.isSuccess()) {
            System.out.println("Jabatan updated successfully: " + updateResponse.getData());
        } else {
            System.out.println("Failed to update Jabatan: " + updateResponse.getMessage());
        }

        Response<Boolean> deleteResponse = repository.deleteById(jabatan.getId());
        if (deleteResponse.isSuccess()) {
            System.out.println("Jabatan deleted successfully.");
        } else {
            System.out.println("Failed to delete Jabatan: " + deleteResponse.getMessage());
        }
    }

    public static void jabatanPegawai() {
        System.out.println("Testing JabatanPegawai class...");

        Pegawai pegawai = new Pegawai();
        Jabatan jabatan1 = new Jabatan();
        Jabatan jabatan2 = new Jabatan();
        JabatanPegawai jabatanPegawai = new JabatanPegawai();

        PegawaiRepository pegawaiRepository = new PegawaiRepository();
        JabatanRepository jabatanRepository = new JabatanRepository();
        JabatanPegawaiService jabatanPegawaiService = new JabatanPegawaiService();

        // Step 1: Create & Save Pegawai
        pegawai.setNip("123456789");
        pegawai.setNama("John Doe");
        pegawai.setTanggalLahir(LocalDate.now());
        pegawai.setAlamat("123 Main St");
        pegawai.setPassword("password123");
        pegawai.setIsAdmin(true);

        Response<Pegawai> savePegawaiResponse = pegawaiRepository.save(pegawai);
        if (!savePegawaiResponse.isSuccess()) {
            System.out.println("Failed to save Pegawai: " + savePegawaiResponse.getMessage());
            return;
        }
        pegawai = savePegawaiResponse.getData(); // get assigned ID
        System.out.println("Pegawai saved successfully: " + pegawai);

        // Step 2: Create & Save Jabatan #1
        jabatan1.setNamaJabatan("Software Engineer");
        jabatan1.setGajiPokok(5000000.0);

        Response<Jabatan> saveJabatan1Response = jabatanRepository.save(jabatan1);
        if (!saveJabatan1Response.isSuccess()) {
            System.out.println("Failed to save Jabatan 1: " + saveJabatan1Response.getMessage());
            return;
        }
        jabatan1 = saveJabatan1Response.getData();
        System.out.println("Jabatan 1 saved successfully: " + jabatan1);

        // Step 3: Assign Jabatan #1 ke Pegawai
        jabatanPegawai.setIdPegawai(pegawai.getId());
        jabatanPegawai.setIdJabatan(jabatan1.getId());

        Response<JabatanPegawai> assignResponse = jabatanPegawaiService.assignJabatanToPegawai(jabatanPegawai);
        if (!assignResponse.isSuccess()) {
            System.out.println("Failed to assign Jabatan to Pegawai: " + assignResponse.getMessage());
            return;
        }

        jabatanPegawai = assignResponse.getData();
        System.out.println("Jabatan assigned successfully: " + jabatanPegawai);

        // Step 4: Create & Save Jabatan #2 (Update Simulation)
        jabatan2.setNamaJabatan("Senior Software Engineer");
        jabatan2.setGajiPokok(7000000.0);

        Response<Jabatan> saveJabatan2Response = jabatanRepository.save(jabatan2);
        if (!saveJabatan2Response.isSuccess()) {
            System.out.println("Failed to save Jabatan 2: " + saveJabatan2Response.getMessage());
            return;
        }
        jabatan2 = saveJabatan2Response.getData();
        System.out.println("Jabatan 2 saved successfully: " + jabatan2);

        // Step 5: Update JabatanPegawai dengan Jabatan #2
        jabatanPegawai.setIdJabatan(jabatan2.getId());

        Response<JabatanPegawai> updateResponse = jabatanPegawaiService.updateJabatanPegawai(jabatanPegawai);
        if (!updateResponse.isSuccess()) {
            System.out.println("Failed to update JabatanPegawai: " + updateResponse.getMessage());
        } else {
            System.out.println("JabatanPegawai updated successfully: " + updateResponse.getData());
        }

        // Step 6: Retrieve all JabatanPegawai data
        Response<ArrayList<Object>> allDataResponse = jabatanPegawaiService.getAllData();
        if (allDataResponse.isSuccess()) {
            System.out.println("All JabatanPegawai data retrieved successfully:");
            for (Object data : allDataResponse.getData()) {
                System.out.println(data);
            }
        } else {
            System.out.println("Failed to retrieve all JabatanPegawai data: " + allDataResponse.getMessage());
        }

        // Step 7: Delete Jabatan from Pegawai
        Response<Boolean> deleteRelation = jabatanPegawaiService.deleteJabatanFromPegawai(jabatanPegawai.getId());
        System.out.println(deleteRelation.isSuccess() ? "Jabatan unassigned from Pegawai."
                : "Failed to unassign Jabatan: " + deleteRelation.getMessage());

        // Step 8: Cleanup: delete Pegawai
        Response<Boolean> deletePegawai = pegawaiRepository.deleteById(pegawai.getId());
        System.out.println(deletePegawai.isSuccess() ? "Pegawai deleted."
                : "Failed to delete Pegawai: " + deletePegawai.getMessage());

        // Step 9: Cleanup: delete both Jabatans
        Response<Boolean> deleteJabatan1 = jabatanRepository.deleteById(jabatan1.getId());
        System.out.println(deleteJabatan1.isSuccess() ? "Jabatan 1 deleted."
                : "Failed to delete Jabatan 1: " + deleteJabatan1.getMessage());

        Response<Boolean> deleteJabatan2 = jabatanRepository.deleteById(jabatan2.getId());
        System.out.println(deleteJabatan2.isSuccess() ? "Jabatan 2 deleted."
                : "Failed to delete Jabatan 2: " + deleteJabatan2.getMessage());
    }

    private static void testShiftPegawai() {
        System.out.println("Testing ShiftPegawai class...");

        Pegawai pegawai = new Pegawai();
        Shift shift1 = new Shift();
        Shift shift2 = new Shift();
        ShiftPegawai shiftPegawai = new ShiftPegawai();
        PegawaiRepository pegawaiRepository = new PegawaiRepository();
        ShiftRepository shiftRepository = new ShiftRepository();
        ShiftPegawaiRepository shiftPegawaiRepository = new ShiftPegawaiRepository();
        ShiftPegawaiService shiftPegawaiService = new ShiftPegawaiService();

        // Step 1: Create & Save Pegawai
        pegawai.setNip("987654321");
        pegawai.setNama("Alice Smith");
        pegawai.setTanggalLahir(LocalDate.now());
        pegawai.setAlamat("456 Elm St");
        pegawai.setPassword("password456");
        pegawai.setIsAdmin(false);

        Response<Pegawai> savePegawaiResponse = pegawaiRepository.save(pegawai);
        if (!savePegawaiResponse.isSuccess()) {
            System.out.println("Failed to save Pegawai: " + savePegawaiResponse.getMessage());
            return;
        }

        pegawai = savePegawaiResponse.getData(); // get assigned ID
        System.out.println("Pegawai saved successfully: " + pegawai);

        // Step 2: Create & Save Shift #1
        shift1.setNamaShift("Night Shift");
        shift1.setJamMasuk(LocalTime.of(22, 0));
        shift1.setJamKeluar(LocalTime.of(6, 0));

        Response<Shift> saveShift1Response = shiftRepository.save(shift1);
        if (!saveShift1Response.isSuccess()) {
            System.out.println("Failed to save Shift 1: " + saveShift1Response.getMessage());
            return;
        }

        shift1 = saveShift1Response.getData();
        System.out.println("Shift 1 saved successfully: " + shift1);

        // Step 3: Assign Shift #1 ke Pegawai
        shiftPegawai.setIdPegawai(pegawai.getId());
        shiftPegawai.setIdShift(shift1.getId());
        Response<ShiftPegawai> assignResponse = shiftPegawaiService.assignShiftToPegawai(shiftPegawai);
        if (!assignResponse.isSuccess()) {
            System.out.println("Failed to assign Shift to Pegawai: " + assignResponse.getMessage());
            return;
        }

        shiftPegawai = assignResponse.getData();
        System.out.println("Shift assigned successfully: " + shiftPegawai);

        // Step 4: Create & Save Shift #2 (Update Simulation)
        shift2.setNamaShift("Early Morning Shift");
        shift2.setJamMasuk(LocalTime.of(4, 0));
        shift2.setJamKeluar(LocalTime.of(12, 0));

        Response<Shift> saveShift2Response = shiftRepository.save(shift2);
        if (!saveShift2Response.isSuccess()) {
            System.out.println("Failed to save Shift 2: " + saveShift2Response.getMessage());
            return;
        }

        shift2 = saveShift2Response.getData();
        System.out.println("Shift 2 saved successfully: " + shift2);

        // Step 5: Update ShiftPegawai dengan Shift #2
        shiftPegawai.setIdShift(shift2.getId());
        Response<ShiftPegawai> updateResponse = shiftPegawaiService.updateShiftPegawai(shiftPegawai);
        if (!updateResponse.isSuccess()) {
            System.out.println("Failed to update ShiftPegawai: " + updateResponse.getMessage());
        } else {
            System.out.println("ShiftPegawai updated successfully: " + updateResponse.getData());
        }

        // Step 6: Retrieve all ShiftPegawai data
        Response<ArrayList<ShiftPegawai>> allDataResponse = shiftPegawaiRepository.findByPegawaiId(pegawai.getId());
        if (allDataResponse.isSuccess()) {
            System.out.println("All ShiftPegawai data retrieved successfully:");
            for (ShiftPegawai data : allDataResponse.getData()) {
                System.out.println(data);
            }
        } else {
            System.out.println("Failed to retrieve all ShiftPegawai data: " + allDataResponse.getMessage());
        }

        // Step 7: Delete Shift from Pegawai
        Response<Boolean> deleteRelation = shiftPegawaiRepository.deleteById(shiftPegawai.getId());
        System.out.println(deleteRelation.isSuccess() ? "Shift unassigned from Pegawai."
                : "Failed to unassign Shift: " + deleteRelation.getMessage());

        // Step 8: Cleanup: delete Pegawai
        Response<Boolean> deletePegawai = pegawaiRepository.deleteById(pegawai.getId());
        System.out.println(deletePegawai.isSuccess() ? "Pegawai deleted."
                : "Failed to delete Pegawai: " + deletePegawai.getMessage());

        // Step 9: Cleanup: delete both Shifts
        Response<Boolean> deleteShift1 = shiftRepository.deleteById(shift1.getId());
        System.out.println(deleteShift1.isSuccess() ? "Shift 1 deleted."
                : "Failed to delete Shift 1: " + deleteShift1.getMessage());

        Response<Boolean> deleteShift2 = shiftRepository.deleteById(shift2.getId());
        System.out.println(deleteShift2.isSuccess() ? "Shift 2 deleted."
                : "Failed to delete Shift 2: " + deleteShift2.getMessage());
    }

    private static void absenTest() {
        System.out.println("Testing Absen class...");

        Pegawai pegawai = new Pegawai();
        Shift shift = new Shift();
        Absen absen = new Absen();
        PegawaiRepository pegawaiRepository = new PegawaiRepository();
        ShiftRepository shiftRepository = new ShiftRepository();
        AbsenRepository absenRepository = new AbsenRepository();
        AbsenService absenService = new AbsenService();

        // Step 1: Create & Save Pegawai
        pegawai.setNip("123456789");
        pegawai.setNama("John Doe");
        pegawai.setTanggalLahir(LocalDate.now());
        pegawai.setAlamat("123 Main St");
        pegawai.setPassword("password123");
        pegawai.setIsAdmin(true);

        Response<Pegawai> savePegawaiResponse = pegawaiRepository.save(pegawai);
        if (!savePegawaiResponse.isSuccess()) {
            System.out.println("Failed to save Pegawai: " + savePegawaiResponse.getMessage());
            return;
        }

        pegawai = savePegawaiResponse.getData(); // get assigned ID
        System.out.println("Pegawai saved successfully: " + pegawai);

        // Step 2: Create & Save Shift
        shift.setNamaShift("Morning Shift");
        shift.setJamMasuk(LocalTime.of(8, 0));
        shift.setJamKeluar(LocalTime.of(16, 0));

        Response<Shift> saveShiftResponse = shiftRepository.save(shift);
        if (!saveShiftResponse.isSuccess()) {
            System.out.println("Failed to save Shift: " + saveShiftResponse.getMessage());
            return;
        }

        shift = saveShiftResponse.getData();
        System.out.println("Shift saved successfully: " + shift);

        // Step 3: Create Absen
        absen.setIdPegawai(pegawai.getId());
        absen.setIdShift(shift.getId());
        absen.setTanggal(LocalDate.now());
        absen.setJamMasuk(LocalTime.of(8, 30));
        absen.setJamKeluar(LocalTime.of(16, 30));

        Response<Absen> createAbsenResponse = absenService.createAbsen(absen);
        if (createAbsenResponse.isSuccess()) {
            System.out.println("Absen created successfully: " + createAbsenResponse.getData());
        } else {
            System.out.println("Failed to create Absen: " + createAbsenResponse.getMessage());
            return;
        }

        // // Step 4: Retrieve Absen by ID
        // Response<Absen> findByIdResponse = absenRepository.findById(createAbsenResponse.getData().getId());
        // if (findByIdResponse.isSuccess()) {
        //     System.out.println("Absen found: " + findByIdResponse.getData());
        // } else {
        //     System.out.println("Failed to find Absen: " + findByIdResponse.getMessage());
        // }

        // // Step 5: Retrieve all Absen for Pegawai
        // Response<ArrayList<Absen>> findAllResponse = absenRepository.findByIdPegawai(pegawai.getId());
        // if (findAllResponse.isSuccess() && findAllResponse.getData() != null && !findAllResponse.getData().isEmpty()) {
        //     System.out.println("All Absen for Pegawai:");
        //     for (Absen a : findAllResponse.getData()) {
        //         System.out.println(a);
        //     }
        // } else {
        //     System.out.println("No Absen found for Pegawai.");
        // }

        // // Step 6: Retrieve all Absen
        // Response<ArrayList<Absen>> allAbsenResponse = absenRepository.findAll();
        // if (allAbsenResponse.isSuccess() && allAbsenResponse.getData() != null && !allAbsenResponse.getData().isEmpty()) {
        //     System.out.println("All Absen:");
        //     for (Absen a : allAbsenResponse.getData()) {
        //         System.out.println(a);
        //     }
        // } else {
        //     System.out.println("No Absen found.");
        // }

        // // Step 7: Delete Absen
        // Response<Boolean> deleteAbsenResponse = absenRepository.deleteById(createAbsenResponse.getData().getId());
        // if (deleteAbsenResponse.isSuccess()) {
        //     System.out.println("Absen deleted successfully.");
        // } else {
        //     System.out.println("Failed to delete Absen: " + deleteAbsenResponse.getMessage());
        // }

        // // Step 8: Cleanup: delete Pegawai
        // Response<Boolean> deletePegawaiResponse = pegawaiRepository.deleteById(pegawai.getId());
        // System.out.println(deletePegawaiResponse.isSuccess() ? "Pegawai deleted."
        //         : "Failed to delete Pegawai: " + deletePegawaiResponse.getMessage());

        // // Step 9: Cleanup: delete Shift
        // Response<Boolean> deleteShiftResponse = shiftRepository.deleteById(shift.getId());
        // System.out.println(deleteShiftResponse.isSuccess() ? "Shift deleted."
        //         : "Failed to delete Shift: " + deleteShiftResponse.getMessage());
    }
}
