/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.absence.salary.slip.application.repositories;

import com.mycompany.absence.salary.slip.application.models.Absen;
import com.mycompany.absence.salary.slip.application.utils.CrudRepository;
import com.mycompany.absence.salary.slip.application.utils.DatabaseConnection;
import com.mycompany.absence.salary.slip.application.utils.Response;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;

/**
 *
 * @author User
 */
public class AbsenRepository implements CrudRepository<Absen> {

    @Override
    public Response<Absen> save(Absen entity) {
        String query = "INSERT INTO absen (id_pegawai, id_shift, tanggal, jam_masuk, jam_keluar) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            var preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, entity.getIdPegawai());
            preparedStatement.setInt(2, entity.getIdShift());
            preparedStatement.setDate(3, new java.sql.Date(entity.getTanggal().getTime()));
            preparedStatement.setTime(4, Time.valueOf(entity.getJamMasuk()));
            preparedStatement.setTime(5, Time.valueOf(entity.getJamKeluar()));
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                var generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getInt(1)); // Assuming the ID is auto-generated
                    return Response.success("Absen saved successfully", entity);
                } else {
                    return Response.failure("Failed to retrieve generated ID");
                }
            } else {
                return Response.failure("Failed to save absen");
            }
        } catch (Exception e) {
            return Response.failure("Error saving absen: " + e.getMessage());
        }
    }

    @Override
    public Response<Absen> update(Absen entity) {
        String query = "UPDATE absen SET id_pegawai = ?, id_shift = ?, tanggal = ?, jam_masuk = ?, jam_keluar = ?, "
                + "foto_masuk = ?, foto_keluar = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            var preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, entity.getIdPegawai());
            preparedStatement.setInt(2, entity.getIdShift());
            preparedStatement.setDate(3, new java.sql.Date(entity.getTanggal().getTime()));
            preparedStatement.setTime(4, Time.valueOf(entity.getJamMasuk()));
            preparedStatement.setTime(5, Time.valueOf(entity.getJamKeluar()));
            preparedStatement.setString(6, entity.getFotoMasuk());
            preparedStatement.setString(7, entity.getFotoKeluar());
            preparedStatement.setInt(8, entity.getId());
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                return Response.success("Absen updated successfully", entity);
            } else {
                return Response.failure("Failed to update absen");
            }
        } catch (Exception e) {
            return Response.failure("Error updating absen: " + e.getMessage());
        }
    }

    @Override
    public Response<Absen> findById(Integer id) {
        String query = "SELECT * FROM absen WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            var preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, id);
            var resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Absen absen = new Absen();
                absen.setId(resultSet.getInt("id"));
                absen.setIdPegawai(resultSet.getInt("id_pegawai"));
                absen.setIdShift(resultSet.getInt("id_shift"));
                absen.setTanggal(resultSet.getDate("tanggal"));
                absen.setJamMasuk(resultSet.getTime("jam_masuk").toLocalTime());
                absen.setJamKeluar(resultSet.getTime("jam_keluar").toLocalTime());

                return Response.success("Absen found", absen);
            } else {
                return Response.failure("Absen not found");
            }
        } catch (Exception e) {
            return Response.failure("Error finding absen: " + e.getMessage());
        }
    }

    @Override
    public Response<Boolean> deleteById(Integer id) {
        String query = "DELETE FROM absen WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            var preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, id);
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                return Response.success("Absen deleted successfully", true);
            } else {
                return Response.failure("Failed to delete absen");
            }
        } catch (Exception e) {
            return Response.failure("Error deleting absen: " + e.getMessage());
        }
    }

    @Override
    public Response<ArrayList<Absen>> findAll() {
        String query = "SELECT * FROM absen";

        try (Connection conn = DatabaseConnection.getConnection()) {
            var preparedStatement = conn.prepareStatement(query);
            var resultSet = preparedStatement.executeQuery();

            ArrayList<Absen> absens = new ArrayList<>();
            while (resultSet.next()) {
                Absen absen = new Absen();
                absen.setId(resultSet.getInt("id"));
                absen.setIdPegawai(resultSet.getInt("id_pegawai"));
                absen.setIdShift(resultSet.getInt("id_shift"));
                absen.setTanggal(resultSet.getDate("tanggal"));
                absen.setJamMasuk(resultSet.getTime("jam_masuk").toLocalTime());
                absen.setJamKeluar(resultSet.getTime("jam_keluar").toLocalTime());
                absens.add(absen);
            }

            return Response.success("Absen found", absens);
        } catch (Exception e) {
            return Response.failure("Error finding absen: " + e.getMessage());
        }
    }


    public Response<ArrayList<Absen>> findByIdPegawai(Integer idPegawai) {
        String query = "SELECT * FROM absen WHERE id_pegawai = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            var preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, idPegawai);
            var resultSet = preparedStatement.executeQuery();

            ArrayList<Absen> absens = new ArrayList<>();
            while (resultSet.next()) {
                Absen absen = new Absen();
                absen.setId(resultSet.getInt("id"));
                absen.setIdPegawai(resultSet.getInt("id_pegawai"));
                absen.setIdShift(resultSet.getInt("id_shift"));
                absen.setTanggal(resultSet.getDate("tanggal"));
                absen.setJamMasuk(resultSet.getTime("jam_masuk").toLocalTime());
                absen.setJamKeluar(resultSet.getTime("jam_keluar").toLocalTime());
                absens.add(absen);
            }

            return Response.success("Absen found", absens);
        } catch (Exception e) {
            return Response.failure("Error finding absen: " + e.getMessage());
        }
    }
}