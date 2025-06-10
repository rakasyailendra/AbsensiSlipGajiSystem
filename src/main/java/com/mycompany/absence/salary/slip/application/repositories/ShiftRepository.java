/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.absence.salary.slip.application.repositories;

import com.mycompany.absence.salary.slip.application.models.Shift;
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
public class ShiftRepository implements CrudRepository<Shift> {

    @Override
    public Response<Shift> save(Shift entity) {
        String query = "INSERT INTO shift (nama_shift, jam_masuk, jam_keluar) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            var preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, entity.getNamaShift());
            preparedStatement.setTime(2, Time.valueOf(entity.getJamMasuk()));
            preparedStatement.setTime(3, Time.valueOf(entity.getJamKeluar()));

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                var generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getInt(1));
                    return Response.success("Shift saved successfully", entity);
                }
            } else {
                return Response.failure("Failed to save Shift");
            }
        } catch (Exception e) {
            return Response.failure("Exception occurred: " + e.getMessage());
        }
        return Response.failure("Failed to save Shift due to unknown error");
    }

    @Override
    public Response<Shift> update(Shift entity) {
        String query = "UPDATE shift SET nama_shift = ?, jam_masuk = ?, jam_keluar = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            var preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, entity.getNamaShift());
            preparedStatement.setTime(2, Time.valueOf(entity.getJamMasuk()));
            preparedStatement.setTime(3, Time.valueOf(entity.getJamKeluar()));
            preparedStatement.setInt(4, entity.getId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                return Response.success("Shift updated successfully", entity);
            } else {
                return Response.failure("Shift not found for update");
            }
        } catch (Exception e) {
            return Response.failure("Exception occurred: " + e.getMessage());
        }
    }

    @Override
    public Response<Shift> findById(Integer id) {
        String query = "SELECT * FROM shift WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            var preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, id);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Shift shift = new Shift();
                shift.setId(resultSet.getInt("id"));
                shift.setNamaShift(resultSet.getString("nama_shift"));
                shift.setJamMasuk(resultSet.getTime("jam_masuk").toLocalTime());
                shift.setJamKeluar(resultSet.getTime("jam_keluar").toLocalTime());
                return Response.success("Shift found", shift);
            } else {
                return Response.failure("Shift not found");
            }
        } catch (Exception e) {
            return Response.failure("Exception occurred: " + e.getMessage());
        }
    }

    @Override
    public Response<Boolean> deleteById(Integer id) {
        String query = "DELETE FROM shift WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            var preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, id);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                return Response.success("Shift deleted successfully", true);
            } else {
                return Response.failure("Shift not found");
            }
        } catch (Exception e) {
            return Response.failure("Exception occurred: " + e.getMessage());
        }
    }

    @Override
    public Response<ArrayList<Shift>> findAll() {
        String query = "SELECT * FROM shift";
        try (Connection conn = DatabaseConnection.getConnection()) {
            var preparedStatement = conn.prepareStatement(query);
            var resultSet = preparedStatement.executeQuery();
            ArrayList<Shift> shiftList = new ArrayList<>();
            while (resultSet.next()) {
                Shift shift = new Shift();
                shift.setId(resultSet.getInt("id"));
                shift.setNamaShift(resultSet.getString("nama_shift"));
                shift.setJamMasuk(resultSet.getTime("jam_masuk").toLocalTime());
                shift.setJamKeluar(resultSet.getTime("jam_keluar").toLocalTime());
                shiftList.add(shift);
            }
            return Response.success("Shifts found", shiftList);
        } catch (Exception e) {
            return Response.failure("Exception occurred: " + e.getMessage());
        }
    }

    public Response<Boolean> deleteAll() {
        String query = "DELETE FROM shift";
        try (Connection conn = DatabaseConnection.getConnection()) {
            var preparedStatement = conn.prepareStatement(query);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                return Response.success("All shifts deleted successfully", true);
            } else {
                return Response.failure("No shifts found to delete");
            }
        } catch (Exception e) {
            return Response.failure("Exception occurred: " + e.getMessage());
        }
    }
}