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
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Types;
import java.util.ArrayList;

/**
 *
 * @author User
 */
public class AbsenRepository implements CrudRepository<Absen> {

    private Absen mapResultSetToAbsen(ResultSet rs) throws SQLException {
        Absen absen = new Absen();
        absen.setId(rs.getInt("id"));
        absen.setIdPegawai(rs.getInt("id_pegawai"));
        absen.setIdShift(rs.getInt("id_shift"));
        absen.setTanggal(rs.getDate("tanggal").toLocalDate());

        Time jamMasuk = rs.getTime("jam_masuk");
        absen.setJamMasuk(jamMasuk != null ? jamMasuk.toLocalTime() : null);

        Time jamKeluar = rs.getTime("jam_keluar");
        absen.setJamKeluar(jamKeluar != null ? jamKeluar.toLocalTime() : null);

        return absen;
    }

    @Override
    public Response<Absen> save(Absen entity) {
        String query = "INSERT INTO absen (id_pegawai, id_shift, tanggal, jam_masuk, jam_keluar) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, entity.getIdPegawai());
            ps.setInt(2, entity.getIdShift());
            ps.setDate(3, Date.valueOf(entity.getTanggal()));
            ps.setTime(4, Time.valueOf(entity.getJamMasuk()));
            if (entity.getJamKeluar() != null)
                ps.setTime(5, Time.valueOf(entity.getJamKeluar()));
            else
                ps.setNull(5, Types.TIME);

            int affected = ps.executeUpdate();

            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        entity.setId(rs.getInt(1));
                        return Response.success("Absen saved successfully", entity);
                    }
                }
                return Response.failure("Absen saved but no ID returned");
            }
            return Response.failure("Failed to save absen");

        } catch (Exception e) {
            return Response.failure("Error saving absen: " + e.getMessage());
        }
    }

    @Override
    public Response<Absen> update(Absen entity) {
        String query = "UPDATE absen SET id_pegawai = ?, id_shift = ?, tanggal = ?, jam_masuk = ?, jam_keluar = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, entity.getIdPegawai());
            ps.setInt(2, entity.getIdShift());
            ps.setDate(3, Date.valueOf(entity.getTanggal()));
            ps.setTime(4, Time.valueOf(entity.getJamMasuk()));
            if (entity.getJamKeluar() != null)
                ps.setTime(5, Time.valueOf(entity.getJamKeluar()));
            else
                ps.setNull(5, Types.TIME);

            ps.setInt(6, entity.getId());

            int affected = ps.executeUpdate();
            return affected > 0
                    ? Response.success("Absen updated successfully", entity)
                    : Response.failure("No absen updated");

        } catch (Exception e) {
            return Response.failure("Error updating absen: " + e.getMessage());
        }
    }

    @Override
    public Response<Absen> findById(Integer id) {
        String query = "SELECT * FROM absen WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Response.success("Absen found", mapResultSetToAbsen(rs));
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

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, id);
            int affected = ps.executeUpdate();

            return affected > 0
                    ? Response.success("Absen deleted successfully", true)
                    : Response.failure("No absen deleted");

        } catch (Exception e) {
            return Response.failure("Error deleting absen: " + e.getMessage());
        }
    }

    @Override
    public Response<ArrayList<Absen>> findAll() {
        String query = "SELECT * FROM absen";
        ArrayList<Absen> resultList = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                resultList.add(mapResultSetToAbsen(rs));
            }

            return Response.success("All absens retrieved", resultList);

        } catch (Exception e) {
            return Response.failure("Error retrieving absens: " + e.getMessage());
        }
    }

    public Response<ArrayList<Absen>> findByIdPegawai(Integer idPegawai) {
        String query = "SELECT * FROM absen WHERE id_pegawai = ?";
        ArrayList<Absen> resultList = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, idPegawai);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                resultList.add(mapResultSetToAbsen(rs));
            }

            return Response.success("Absen(s) found by pegawai", resultList);

        } catch (Exception e) {
            return Response.failure("Error retrieving absen by pegawai: " + e.getMessage());
        }
    }

    public Response<Boolean> deleteAll() {
        String query = "DELETE FROM absen";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            int affected = ps.executeUpdate();

            return affected > 0
                    ? Response.success("All absens deleted", true)
                    : Response.failure("No absens deleted");

        } catch (Exception e) {
            return Response.failure("Error deleting all absens: " + e.getMessage());
        }
    }
}
