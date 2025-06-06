/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.absence.salary.slip.application.repositories;

import com.mycompany.absence.salary.slip.application.models.ShiftPegawai;
import com.mycompany.absence.salary.slip.application.utils.CrudRepository;
import com.mycompany.absence.salary.slip.application.utils.DatabaseConnection;
import com.mycompany.absence.salary.slip.application.utils.Response;

import java.sql.Statement;
import java.sql.Connection;
import java.util.ArrayList;

/**
 *
 * @author User
 */

public class ShiftPegawaiRepository implements CrudRepository<ShiftPegawai> {

    @Override
    public Response<ShiftPegawai> save(ShiftPegawai entity) {
        String query = "INSERT INTO shift_pegawai (id_pegawai, id_shift) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            var preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, entity.getIdPegawai());
            preparedStatement.setInt(2, entity.getIdShift());
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                var generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getInt(1));
                    return Response.success("Shift Pegawai saved successfully", entity);
                } else {
                    return Response.failure("Failed to retrieve generated ID");
                }
            } else {
                return Response.failure("Failed to save Shift Pegawai");
            }

        } catch (Exception e) {
            return Response.failure("Error saving Shift Pegawai: " + e.getMessage());
        }
    }

    @Override
    public Response<ShiftPegawai> update(ShiftPegawai entity) {
        String query = "UPDATE shift_pegawai SET id_pegawai = ?, id_shift = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            var preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, entity.getIdPegawai());
            preparedStatement.setInt(2, entity.getIdShift());
            preparedStatement.setInt(3, entity.getId());
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                return Response.success("Shift Pegawai updated successfully", entity);
            } else {
                return Response.failure("Failed to update Shift Pegawai");
            }

        } catch (Exception e) {
            return Response.failure("Error updating Shift Pegawai: " + e.getMessage());
        }
    }

    @Override
    public Response<ShiftPegawai> findById(Integer id) {
        String query = "SELECT * FROM shift_pegawai WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            var preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, id);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                ShiftPegawai entity = new ShiftPegawai();
                entity.setId(resultSet.getInt("id"));
                entity.setIdPegawai(resultSet.getInt("id_pegawai"));
                entity.setIdShift(resultSet.getInt("id_shift"));
                return Response.success("Shift Pegawai found", entity);
            } else {
                return Response.failure("Shift Pegawai not found");
            }
        } catch (Exception e) {
            return Response.failure("Error finding Shift Pegawai: " + e.getMessage());
        }
    }

    @Override
    public Response<Boolean> deleteById(Integer id) {
        String query = "DELETE FROM shift_pegawai WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            var preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, id);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                return Response.success("Shift Pegawai deleted successfully", true);
            } else {
                return Response.failure("Failed to delete Shift Pegawai");
            }
        } catch (Exception e) {
            return Response.failure("Error deleting Shift Pegawai: " + e.getMessage());
        }
    }

    @Override
    public Response<ArrayList<ShiftPegawai>> findAll() {
        String query = "SELECT * FROM shift_pegawai";
        try (Connection conn = DatabaseConnection.getConnection()) {
            var preparedStatement = conn.prepareStatement(query);
            var resultSet = preparedStatement.executeQuery();
            ArrayList<ShiftPegawai> result = new ArrayList<>();
            while (resultSet.next()) {
                ShiftPegawai entity = new ShiftPegawai();
                entity.setId(resultSet.getInt("id"));
                entity.setIdPegawai(resultSet.getInt("id_pegawai"));
                entity.setIdShift(resultSet.getInt("id_shift"));
                result.add(entity);
            }
            return Response.success("All Shift Pegawai found", result);
        } catch (Exception e) {
            return Response.failure("Error finding all Shift Pegawai: " + e.getMessage());
        }
    }

    public Response<ArrayList<ShiftPegawai>> findByPegawaiId(Integer idPegawai) {
        String query = "SELECT * FROM shift_pegawai WHERE id_pegawai = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            var preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, idPegawai);
            var resultSet = preparedStatement.executeQuery();
            ArrayList<ShiftPegawai> result = new ArrayList<>();
            while (resultSet.next()) {
                ShiftPegawai entity = new ShiftPegawai();
                entity.setId(resultSet.getInt("id"));
                entity.setIdPegawai(resultSet.getInt("id_pegawai"));
                entity.setIdShift(resultSet.getInt("id_shift"));
                result.add(entity);
            }
            return Response.success("Shift Pegawai found for Pegawai ID " + idPegawai, result);
        } catch (Exception e) {
            return Response.failure("Error finding Shift Pegawai by Pegawai ID: " + e.getMessage());
        }
    }
}