/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.absence.salary.slip.application.repositories;

import com.mycompany.absence.salary.slip.application.models.Pegawai;
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

public class PegawaiRepository implements CrudRepository<Pegawai> {

    @Override
    public Response<Pegawai> save(Pegawai entity) {
        String query = "INSERT INTO pegawai (nip, nama, tanggal_lahir, alamat, password, is_admin) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            var preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, entity.getNip());
            preparedStatement.setString(2, entity.getNama());
            preparedStatement.setDate(3, java.sql.Date.valueOf(entity.getTanggalLahir()));
            preparedStatement.setString(4, entity.getAlamat());
            preparedStatement.setString(5, entity.getPassword());
            preparedStatement.setBoolean(6, entity.getIsAdmin());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                var generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getInt(1));
                    return Response.success("Pegawai saved successfully", entity);
                }
            }
            return Response.failure("Failed to save Pegawai");
        } catch (Exception e) {
            return Response.failure("Exception occurred: " + e.getMessage());
        }
    }

    @Override
    public Response<Pegawai> update(Pegawai entity) {
        String query = "UPDATE pegawai SET nip = ?, nama = ?, tanggal_lahir = ?, alamat = ?, password = ?, is_admin = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            var preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, entity.getNip());
            preparedStatement.setString(2, entity.getNama());
            preparedStatement.setDate(3, java.sql.Date.valueOf(entity.getTanggalLahir()));
            preparedStatement.setString(4, entity.getAlamat());
            preparedStatement.setString(5, entity.getPassword());
            preparedStatement.setBoolean(6, entity.getIsAdmin());
            preparedStatement.setInt(7, entity.getId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                return Response.success("Pegawai updated successfully", entity);
            } else {
                return Response.failure("Pegawai not found or no changes made");
            }
        } catch (Exception e) {
            return Response.failure("Exception occurred: " + e.getMessage());
        }
    }

    @Override
    public Response<Pegawai> findById(Integer id) {
        String query = "SELECT * FROM pegawai WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            var preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, id);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Pegawai pegawai = new Pegawai();
                pegawai.setId(resultSet.getInt("id"));
                pegawai.setNip(resultSet.getString("nip"));
                pegawai.setNama(resultSet.getString("nama"));
                pegawai.setTanggalLahir(resultSet.getDate("tanggal_lahir").toLocalDate());
                pegawai.setAlamat(resultSet.getString("alamat"));
                pegawai.setPassword(resultSet.getString("password"));
                pegawai.setIsAdmin(resultSet.getBoolean("is_admin"));
                return Response.success("Pegawai found", pegawai);
            } else {
                return Response.failure("Pegawai not found");
            }
        } catch (Exception e) {
            return Response.failure("Exception occurred: " + e.getMessage());
        }
    }

    @Override
    public Response<Boolean> deleteById(Integer id) {
        String query = "DELETE FROM pegawai WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            var preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, id);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                return Response.success("Pegawai deleted successfully", true);
            } else {
                return Response.failure("Pegawai not found");
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            return Response.failure("Cannot delete Pegawai due to foreign key constraints: " + e.getMessage());
        } catch (Exception e) {
            return Response.failure("Exception occurred: " + e.getMessage());
        }
    }

    @Override
    public Response<ArrayList<Pegawai>> findAll() {
        String query = "SELECT * FROM pegawai";
        ArrayList<Pegawai> pegawaiList = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            var preparedStatement = conn.prepareStatement(query);
            var resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Pegawai pegawai = new Pegawai();
                pegawai.setId(resultSet.getInt("id"));
                pegawai.setNip(resultSet.getString("nip"));
                pegawai.setNama(resultSet.getString("nama"));
                pegawai.setTanggalLahir(resultSet.getDate("tanggal_lahir").toLocalDate());
                pegawai.setAlamat(resultSet.getString("alamat"));
                pegawai.setPassword(resultSet.getString("password"));
                pegawai.setIsAdmin(resultSet.getBoolean("is_admin"));
                pegawaiList.add(pegawai);
            }
            return Response.success("Pegawai list retrieved successfully", pegawaiList);
        } catch (Exception e) {
            return Response.failure("Exception occurred: " + e.getMessage());
        }
    }

    public Response<Pegawai> findByNip(String nip) {
        String query = "SELECT * FROM pegawai WHERE nip = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            var preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, nip);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Pegawai pegawai = new Pegawai();
                pegawai.setId(resultSet.getInt("id"));
                pegawai.setNip(resultSet.getString("nip"));
                pegawai.setNama(resultSet.getString("nama"));
                pegawai.setTanggalLahir(resultSet.getDate("tanggal_lahir").toLocalDate());
                pegawai.setAlamat(resultSet.getString("alamat"));
                pegawai.setPassword(resultSet.getString("password"));
                pegawai.setIsAdmin(resultSet.getBoolean("is_admin"));
                return Response.success("Pegawai found", pegawai);
            } else {
                return Response.failure("Pegawai not found");
            }
        } catch (Exception e) {
            return Response.failure("Exception occurred: " + e.getMessage());
        }
    }

    public Response<Boolean> deleteAll() {
        String query = "DELETE FROM pegawai";
        try (Connection conn = DatabaseConnection.getConnection()) {
            var preparedStatement = conn.prepareStatement(query);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                return Response.success("All Pegawai deleted successfully", true);
            } else {
                return Response.failure("No Pegawai found to delete");
            }
        } catch (Exception e) {
            return Response.failure("Exception occurred: " + e.getMessage());
        }
    }
}