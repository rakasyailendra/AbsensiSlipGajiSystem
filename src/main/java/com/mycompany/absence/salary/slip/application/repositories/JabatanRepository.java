/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.absence.salary.slip.application.repositories;

import com.mycompany.absence.salary.slip.application.models.Jabatan;
import com.mycompany.absence.salary.slip.application.utils.CrudRepository;
import com.mycompany.absence.salary.slip.application.utils.DatabaseConnection;
import com.mycompany.absence.salary.slip.application.utils.Response;

import java.sql.Connection;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author User
 */

public class JabatanRepository implements CrudRepository<Jabatan> {

    @Override
    public Response<Jabatan> save(Jabatan entity) {
        String query = "INSERT INTO jabatan (nama_jabatan, gaji_pokok) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            var preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, entity.getNamaJabatan());
            preparedStatement.setDouble(2, entity.getGajiPokok());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                var generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getInt(1));
                    return Response.success("Jabatan saved successfully", entity);
                }
            }
            return Response.failure("Failed to save Jabatan");
        } catch (Exception e) {
            return Response.failure("Exception occurred: " + e.getMessage());
        }
    }

    @Override
    public Response<Jabatan> update(Jabatan entity) {
        String query = "UPDATE jabatan SET nama_jabatan = ?, gaji_pokok = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            var preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, entity.getNamaJabatan());
            preparedStatement.setDouble(2, entity.getGajiPokok());
            preparedStatement.setInt(3, entity.getId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                return Response.success("Jabatan updated successfully", entity);
            } else {
                return Response.failure("Jabatan not found for update");
            }
        } catch (Exception e) {
            return Response.failure("Exception occurred: " + e.getMessage());
        }
    }

    @Override
    public Response<Jabatan> findById(Integer id) {
        String query = "SELECT * FROM jabatan WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            var preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, id);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Jabatan jabatan = new Jabatan();
                jabatan.setId(resultSet.getInt("id"));
                jabatan.setNamaJabatan(resultSet.getString("nama_jabatan"));
                jabatan.setGajiPokok(resultSet.getDouble("gaji_pokok"));
                return Response.success("Jabatan found", jabatan);
            } else {
                return Response.failure("Jabatan not found");
            }
        } catch (Exception e) {
            return Response.failure("Exception occurred: " + e.getMessage());
        }
    }

    @Override
    public Response<Boolean> deleteById(Integer id) {
        String query = "DELETE FROM jabatan WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            var preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, id);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                return Response.success("Jabatan deleted successfully", true);
            } else {
                return Response.failure("Jabatan not found for deletion");
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            return Response.failure("Cannot delete Jabatan due to foreign key constraints: " + e.getMessage());
        } catch (Exception e) {
            return Response.failure("Exception occurred: " + e.getMessage());
        }
    }

    @Override
    public Response<ArrayList<Jabatan>> findAll() {
        String query = "SELECT * FROM jabatan";
        ArrayList<Jabatan> jabatans = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            var preparedStatement = conn.prepareStatement(query);
            var resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Jabatan jabatan = new Jabatan();
                jabatan.setId(resultSet.getInt("id"));
                jabatan.setNamaJabatan(resultSet.getString("nama_jabatan"));
                jabatan.setGajiPokok(resultSet.getDouble("gaji_pokok"));
                jabatans.add(jabatan);
            }
            return Response.success("All Jabatans found", jabatans);
        } catch (Exception e) {
            return Response.failure("Exception occurred: " + e.getMessage());
        }
    }
}