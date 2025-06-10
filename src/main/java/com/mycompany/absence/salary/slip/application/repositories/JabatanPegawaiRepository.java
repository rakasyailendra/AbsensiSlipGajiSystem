/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.absence.salary.slip.application.repositories;

import com.mycompany.absence.salary.slip.application.models.JabatanPegawai;
import com.mycompany.absence.salary.slip.application.utils.CrudRepository;
import com.mycompany.absence.salary.slip.application.utils.DatabaseConnection;
import com.mycompany.absence.salary.slip.application.utils.Response;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author User
 */
public class JabatanPegawaiRepository implements CrudRepository<JabatanPegawai> {

    @Override
    public Response<JabatanPegawai> save(JabatanPegawai entity) {
        String query = "INSERT INTO jabatan_pegawai (id_pegawai, id_jabatan) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                var preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, entity.getIdPegawai());
            preparedStatement.setInt(2, entity.getIdJabatan());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                var generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getInt(1));
                }
                return Response.success("Jabatan Pegawai saved successfully", entity);
            } else {
                return Response.failure("Failed to save Jabatan Pegawai");
            }

        } catch (Exception e) {
            return Response.failure("Exception occurred while saving: " + e.getMessage());
        }
    }

    @Override
    public Response<JabatanPegawai> update(JabatanPegawai entity) {
        String query = "UPDATE jabatan_pegawai SET id_pegawai = ?, id_jabatan = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                var preparedStatement = conn.prepareStatement(query)) {

            preparedStatement.setInt(1, entity.getIdPegawai());
            preparedStatement.setInt(2, entity.getIdJabatan());
            preparedStatement.setInt(3, entity.getId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                return Response.success("Jabatan Pegawai updated successfully", entity);
            } else {
                return Response.failure("Jabatan Pegawai not found for update");
            }

        } catch (Exception e) {
            return Response.failure("Exception occurred while updating: " + e.getMessage());
        }
    }

    @Override
    public Response<JabatanPegawai> findById(Integer id) {
        String query = "SELECT * FROM jabatan_pegawai WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                var preparedStatement = conn.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                JabatanPegawai entity = mapResultToEntity(resultSet);
                return Response.success("Jabatan Pegawai found", entity);
            } else {
                return Response.failure("Jabatan Pegawai not found");
            }

        } catch (Exception e) {
            return Response.failure("Exception occurred while finding by ID: " + e.getMessage());
        }
    }

    @Override
    public Response<Boolean> deleteById(Integer id) {
        String query = "DELETE FROM jabatan_pegawai WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                var preparedStatement = conn.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                return Response.success("Jabatan Pegawai deleted successfully", true);
            } else {
                return Response.failure("Jabatan Pegawai not found");
            }

        } catch (Exception e) {
            return Response.failure("Exception occurred while deleting: " + e.getMessage());
        }
    }

    @Override
    public Response<ArrayList<JabatanPegawai>> findAll() {
        String query = "SELECT * FROM jabatan_pegawai";
        try (Connection conn = DatabaseConnection.getConnection();
                var preparedStatement = conn.prepareStatement(query);
                var resultSet = preparedStatement.executeQuery()) {

            ArrayList<JabatanPegawai> list = new ArrayList<>();
            while (resultSet.next()) {
                list.add(mapResultToEntity(resultSet));
            }
            return Response.success("All Jabatan Pegawai found", list);

        } catch (Exception e) {
            return Response.failure("Exception occurred while finding all: " + e.getMessage());
        }
    }

    public Response<ArrayList<JabatanPegawai>> findByPegawaiId(Integer idPegawai) {
        String query = "SELECT * FROM jabatan_pegawai WHERE id_pegawai = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                var preparedStatement = conn.prepareStatement(query)) {

            preparedStatement.setInt(1, idPegawai);
            var resultSet = preparedStatement.executeQuery();
            ArrayList<JabatanPegawai> list = new ArrayList<>();
            while (resultSet.next()) {
                list.add(mapResultToEntity(resultSet));
            }
            return Response.success("Jabatan Pegawai by Pegawai ID found", list);

        } catch (Exception e) {
            return Response.failure("Exception occurred while finding by Pegawai ID: " + e.getMessage());
        }
    }

    public Response<Boolean> deleteAll() {
        String query = "DELETE FROM jabatan_pegawai";
        try (Connection conn = DatabaseConnection.getConnection();
                var preparedStatement = conn.prepareStatement(query)) {

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                return Response.success("All Jabatan Pegawai deleted successfully", true);
            } else {
                return Response.failure("No Jabatan Pegawai found to delete");
            }

        } catch (Exception e) {
            return Response.failure("Exception occurred while deleting all: " + e.getMessage());
        }
    }

    // Mapper untuk mengubah ResultSet menjadi Entity
    private JabatanPegawai mapResultToEntity(java.sql.ResultSet resultSet) throws Exception {
        JabatanPegawai entity = new JabatanPegawai();
        entity.setId(resultSet.getInt("id"));
        entity.setIdPegawai(resultSet.getInt("id_pegawai"));
        entity.setIdJabatan(resultSet.getInt("id_jabatan"));
        return entity;
    }
}