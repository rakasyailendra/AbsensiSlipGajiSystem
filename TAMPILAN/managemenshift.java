package apk;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.*;
import java.io.*;

/**
 * shift_entity class - manajemen absensi shift pegawai dengan fitur CRUD lengkap,
 * pencarian, laporan, undo/redo history, notifikasi, export data, dan handling error.
 */
public class shift_entity {

    private static Connection koneksi = null;

    // Undo/Redo stack untuk simulasikan riwayat aksi (simpel)
    private static final Deque<Map<String, Object>> undoStack = new ArrayDeque<>();
    private static final Deque<Map<String, Object>> redoStack = new ArrayDeque<>();

    // Koneksi database getter
    private static Connection getConnection() throws SQLException {
        if (koneksi == null || koneksi.isClosed()) {
            koneksi = connection.connection;
        }
        return koneksi;
    }

    // Ambil data personal user berdasarkan NIP
    static String[] getUserPersonalData(String NIP){
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
               "SELECT Shift.Nama_Shift , jenis_golongan , jam_masuk, jam_keluar " +
               "FROM shift_pegawai JOIN golongan_pegawai ON shift_pegawai.NIP = golongan_pegawai.NIP " +
               "JOIN Shift ON shift_pegawai.Nama_Shift = Shift.Nama_Shift " +
               "WHERE shift_pegawai.NIP = '" + NIP + "'")) {
            if(rs.next()) {
                String data[] = new String[5];
                data[0] = rs.getString("jenis_golongan");
                data[2] = rs.getString("jam_masuk");
                data[3] = rs.getString("jam_keluar");
                data[4] = rs.getString("Nama_Shift");
                data[1] = rs.getString("Nama_Shift") + " ( " + rs.getString("jam_masuk") + " - " + rs.getString("jam_keluar") + " ) ";
                return data;
            }
            return null;
        } catch (SQLException t){
            System.out.println("getUserPersonalData error: " + t);
            return null;
        }
    }

    // Insert data absen baru dengan validasi absensi hari ini
    static boolean insertAbsen(String NIP, Date tanggal, String namaShift, Time jam_masuk, Time jam_keluar, int total_lembur){
        try (Connection conn = getConnection()){
            // Cek apakah sudah absen hari ini
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            LocalDateTime now = LocalDateTime.now();
            PreparedStatement checkStmt = conn.prepareStatement(
                "SELECT * FROM absen WHERE tanggal = ? AND NIP = ?");
            checkStmt.setDate(1, Date.valueOf(now.toLocalDate()));
            checkStmt.setString(2, NIP);
            ResultSet rsCheck = checkStmt.executeQuery();
            if(rsCheck.next()){
                JOptionPane.showMessageDialog(null, "Kamu sudah absen hari ini!");
                return false;
            }

            // Simpan data absen baru
            String query = "INSERT INTO absen (NIP, nama_shift, tanggal, jam_masuk, jam_keluar, total_lembur) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(query);
            insertStmt.setString(1, NIP);
            insertStmt.setString(2, namaShift);
            insertStmt.setDate(3, tanggal);
            insertStmt.setTime(4, jam_masuk);
            insertStmt.setTime(5, jam_keluar);
            insertStmt.setInt(6, total_lembur);
            insertStmt.execute();

            // Simpan ke undo stack
            Map<String,Object> undoData = new HashMap<>();
            undoData.put("action","insert");
            undoData.put("NIP", NIP);
            undoData.put("tanggal", tanggal);
            undoStack.push(undoData);
            redoStack.clear();

            return true;
        } catch (SQLException t){
            System.out.println("insertAbsen error: " + t);
            return false;
        }
    }

    // Update data absen jam_keluar & total_lembur
    static boolean updateAbsen(String NIP, Date tanggal, Time jam_keluar, int total_lembur){
        try (Connection conn = getConnection()){
            // Ambil data lama untuk undo
            PreparedStatement selectStmt = conn.prepareStatement("SELECT * FROM absen WHERE NIP = ? AND tanggal = ?");
            selectStmt.setString(1, NIP);
            selectStmt.setDate(2, tanggal);
            ResultSet oldDataRS = selectStmt.executeQuery();
            if(!oldDataRS.next()){
                JOptionPane.showMessageDialog(null, "Data absen tidak ditemukan untuk update");
                return false;
            }
            Time oldJamKeluar = oldDataRS.getTime("jam_keluar");
            int oldLembur = oldDataRS.getInt("total_lembur");

            // Update data
            PreparedStatement updateStmt = conn.prepareStatement(
                "UPDATE absen SET jam_keluar = ?, total_lembur = ? WHERE NIP = ? AND tanggal = ?");
            updateStmt.setTime(1, jam_keluar);
            updateStmt.setInt(2, total_lembur);
            updateStmt.setString(3, NIP);
            updateStmt.setDate(4, tanggal);
            updateStmt.executeUpdate();

            // Simpan ke undo stack
            Map<String,Object> undoData = new HashMap<>();
            undoData.put("action", "update");
            undoData.put("NIP", NIP);
            undoData.put("tanggal", tanggal);
            undoData.put("oldJamKeluar", oldJamKeluar);
            undoData.put("oldLembur", oldLembur);
            undoStack.push(undoData);
            redoStack.clear();

            return true;
        } catch (SQLException t){
            System.out.println("updateAbsen error: " + t);
            return false;
        }
    }

    // Hapus data absen
    static boolean deleteAbsen(String NIP, Date tanggal){
        try (Connection conn = getConnection()){
            // Ambil data untuk undo
            PreparedStatement selectStmt = conn.prepareStatement("SELECT * FROM absen WHERE NIP = ? AND tanggal = ?");
            selectStmt.setString(1, NIP);
            selectStmt.setDate(2, tanggal);
            ResultSet rs = selectStmt.executeQuery();
            if(!rs.next()){
                JOptionPane.showMessageDialog(null, "Data absen tidak ditemukan untuk dihapus");
                return false;
            }
            Map<String,Object> deletedData = new HashMap<>();
            deletedData.put("NIP", NIP);
            deletedData.put("tanggal", tanggal);
            deletedData.put("nama_shift", rs.getString("nama_shift"));
            deletedData.put("jam_masuk", rs.getTime("jam_masuk"));
            deletedData.put("jam_keluar", rs.getTime("jam_keluar"));
            deletedData.put("total_lembur", rs.getInt("total_lembur"));

            // Hapus data
            PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM absen WHERE NIP = ? AND tanggal = ?");
            deleteStmt.setString(1, NIP);
            deleteStmt.setDate(2, tanggal);
            deleteStmt.executeUpdate();

            // Simpan undo
            Map<String,Object> undoData = new HashMap<>();
            undoData.put("action", "delete");
            undoData.put("data", deletedData);
            undoStack.push(undoData);
            redoStack.clear();

            return true;
        } catch(SQLException t){
            System.out.println("deleteAbsen error: " + t);
            return false;
        }
    }

    // Undo terakhir: insert -> delete, update -> restore old data, delete -> restore data
    static boolean undo() {
        if(undoStack.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Tidak ada aksi untuk dibatalkan");
            return false;
        }
        Map<String,Object> lastAction = undoStack.pop();
        String actionType = (String) lastAction.get("action");
        try(Connection conn = getConnection()) {
            switch (actionType) {
                case "insert":
                    // Undo insert = delete
                    String NIPIns = (String) lastAction.get("NIP");
                    Date tglIns = (Date) lastAction.get("tanggal");
                    PreparedStatement delStmt = conn.prepareStatement("DELETE FROM absen WHERE NIP = ? AND tanggal = ?");
                    delStmt.setString(1, NIPIns);
                    delStmt.setDate(2, tglIns);
                    delStmt.executeUpdate();
                    redoStack.push(lastAction);
                    JOptionPane.showMessageDialog(null,"Undo Insert berhasil");
                    break;
                case "update":
                    // Undo update = restore old jam keluar dan lembur
                    String NIPUpd = (String) lastAction.get("NIP");
                    Date tglUpd = (Date) lastAction.get("tanggal");
                    Time oldJamKeluar = (Time) lastAction.get("oldJamKeluar");
                    int oldLembur = (int) lastAction.get("oldLembur");
                    PreparedStatement updStmt = conn.prepareStatement("UPDATE absen SET jam_keluar = ?, total_lembur = ? WHERE NIP = ? AND tanggal = ?");
                    updStmt.setTime(1, oldJamKeluar);
                    updStmt.setInt(2, oldLembur);
                    updStmt.setString(3, NIPUpd);
                    updStmt.setDate(4, tglUpd);
                    updStmt.executeUpdate();
                    redoStack.push(lastAction);
                    JOptionPane.showMessageDialog(null, "Undo Update berhasil");
                    break;
                case "delete":
                    // Undo delete = insert kembali data yang dihapus
                    Map<String,Object> dataDel = (Map<String,Object>) lastAction.get("data");
                    PreparedStatement insStmt = conn.prepareStatement(
                        "INSERT INTO absen (NIP, nama_shift, tanggal, jam_masuk, jam_keluar, total_lembur) VALUES (?, ?, ?, ?, ?, ?)");
                    insStmt.setString(1, (String)dataDel.get("NIP"));
                    insStmt.setString(2, (String)dataDel.get("nama_shift"));
                    insStmt.setDate(3, (Date)dataDel.get("tanggal"));
                    insStmt.setTime(4, (Time)dataDel.get("jam_masuk"));
                    insStmt.setTime(5, (Time)dataDel.get("jam_keluar"));
                    insStmt.setInt(6, (int)dataDel.get("total_lembur"));
                    insStmt.execute();
                    redoStack.push(lastAction);
                    JOptionPane.showMessageDialog(null, "Undo Delete berhasil");
                    break;
                default:
                    JOptionPane.showMessageDialog(null,"Aksi undo tidak dikenal");
                    return false;
            }
            return true;
        } catch(SQLException t) {
            System.out.println("undo error: "+t);
            return false;
        }
    }

    // Redo terakhir
    static boolean redo() {
        if(redoStack.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Tidak ada aksi untuk diulangi");
            return false;
        }
        Map<String,Object> lastAction = redoStack.pop();
        String actionType = (String) lastAction.get("action");
        try (Connection conn = getConnection()) {
            switch(actionType) {
                case "insert":
                    // Redo insert kembali data (sama seperti insertAbsen)
                    String NIPIns = (String) lastAction.get("NIP");
                    Date tglIns = (Date) lastAction.get("tanggal");
                    // Kalau data sudah ada, abaikan
                    PreparedStatement checkStmt = conn.prepareStatement("SELECT * FROM absen WHERE NIP = ? AND tanggal = ?");
                    checkStmt.setString(1, NIPIns);
                    checkStmt.setDate(2, tglIns);
                    ResultSet rs = checkStmt.executeQuery();
                    if(rs.next()) {
                        JOptionPane.showMessageDialog(null, "Data sudah ada, redo insert dibatalkan");
                        return false;
                    }
                    // Data insert lengkap tidak ada di undo stack, jadi tidak bisa lengkap redo insert
                    // Jadi untuk contoh ini abaikan dan tulis pesan
                    JOptionPane.showMessageDialog(null, "Redo insert tidak dapat dilakukan karena data tidak lengkap");
                    return false;
                case "update":
                    // Redo update = apply perubahan jam_keluar dan lembur (tidak menyimpan data baru)
                    String NIPUpd = (String) lastAction.get("NIP");
                    Date tglUpd = (Date) lastAction.get("tanggal");

                    // Tidak ada data baru pada stack redo, perlu implementasi lengkap jika diperlukan
                    JOptionPane.showMessageDialog(null, "Redo update perlu implementasi tambahan");
                    return false;
                case "delete":
                    // Redo delete = hapus data lagi
                    Map<String,Object> dataDel = (Map<String,Object>) lastAction.get("data");
                    PreparedStatement delStmt = conn.prepareStatement("DELETE FROM absen WHERE NIP = ? AND tanggal = ?");
                    delStmt.setString(1, (String)dataDel.get("NIP"));
                    delStmt.setDate(2, (Date)dataDel.get("tanggal"));
                    delStmt.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Redo Delete berhasil");
                    return true;
                default:
                    JOptionPane.showMessageDialog(null,"Aksi redo tidak dikenal");
                    return false;
            }
        } catch(SQLException t) {
            System.out.println("redo error: "+t);
            return false;
        }
    }

    // Cari data absensi dengan filter nama shift dan tanggal range
    static List<Map<String,Object>> searchAbsensi(String namaShift, Date tanggalAwal, Date tanggalAkhir) {
        List<Map<String,Object>> resultList = new ArrayList<>();
        try (Connection conn = getConnection()) {
            StringBuilder query = new StringBuilder("SELECT * FROM absen WHERE 1=1 ");
            if(namaShift != null && !namaShift.isEmpty()) {
                query.append("AND nama_shift LIKE ? ");
            }
            if(tanggalAwal != null && tanggalAkhir != null) {
                query.append("AND tanggal BETWEEN ? AND ? ");
            }
            PreparedStatement stmt = conn.prepareStatement(query.toString());
            int paramIndex = 1;
            if(namaShift != null && !namaShift.isEmpty()) {
                stmt.setString(paramIndex++, "%" + namaShift + "%");
            }
            if(tanggalAwal != null && tanggalAkhir != null) {
                stmt.setDate(paramIndex++, tanggalAwal);
                stmt.setDate(paramIndex++, tanggalAkhir);
            }
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                Map<String,Object> row = new HashMap<>();
                row.put("NIP", rs.getString("NIP"));
                row.put("nama_shift", rs.getString("nama_shift"));
                row.put("tanggal", rs.getDate("tanggal"));
                row.put("jam_masuk", rs.getTime("jam_masuk"));
                row.put("jam_keluar", rs.getTime("jam_keluar"));
                row.put("total_lembur", rs.getInt("total_lembur"));
                resultList.add(row);
            }
        } catch(SQLException t) {
            System.out.println("searchAbsensi error: " + t);
        }
        return resultList;
    }

    // Export data absensi ke file CSV
    static boolean exportAbsensiToCSV(String filename, List<Map<String,Object>> data) {
        try(PrintWriter writer = new PrintWriter(new File(filename))) {
            writer.println("NIP,Nama_Shift,Tanggal,Jam_Masuk,Jam_Keluar,Total_Lembur");
            for(Map<String,Object> row : data) {
                writer.printf("%s,%s,%s,%s,%s,%d\n",
                        row.get("NIP"),
                        row.get("nama_shift"),
                        row.get("tanggal"),
                        row.get("jam_masuk"),
                        row.get("jam_keluar"),
                        row.get("total_lembur"));
            }
            return true;
        } catch(IOException t) {
            System.out.println("exportAbsensiToCSV error: "+t);
            return false;
        }
    }

    // Load data absen ke dalam DefaultTableModel dan update JTable model
    public static void loadAbsenToModel(DefaultTableModel model, String NIP){
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT tanggal, nama_shift, jam_masuk, jam_keluar FROM absen WHERE NIP = ?")) {
            stmt.setString(1, NIP);
            ResultSet rs = stmt.executeQuery();

            model.setRowCount(0);
            while(rs.next()){
                String tanggal = rs.getString("tanggal");
                String namaShift = rs.getString("nama_shift");
                String jamMasuk = rs.getString("jam_masuk");
                String jamKeluar = rs.getString("jam_keluar");
                model.addRow(new Object[]{tanggal, namaShift, jamMasuk, jamKeluar});
            }
            shift.table.setModel(model);
        } catch(SQLException t){
            System.out.println("loadAbsenToModel error: " + t);
        }
    }

    // Dapatkan daftar shift yang tersedia dari tabel Shift
    static List<String> getShiftList() {
        List<String> shifts = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Nama_Shift FROM Shift")) {
            while (rs.next()) {
                shifts.add(rs.getString("Nama_Shift"));
            }
        } catch (SQLException t) {
            System.out.println("getShiftList error: " + t);
        }
        return shifts;
    }

    // Dapatkan golongan pegawai dari tabel golongan_pegawai
    static Map<String,String> getGolonganPegawai(String NIP){
        Map<String,String> data = new HashMap<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT jenis_golongan FROM golongan_pegawai WHERE NIP = ?")) {
            stmt.setString(1, NIP);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                data.put("jenis_golongan", rs.getString("jenis_golongan"));
            }
        } catch(SQLException t) {
            System.out.println("getGolonganPegawai error: " + t);
        }
        return data;
    }

    // Input log kesalahan ke log file sederhana
    static void logError(String message, Throwable ex) {
        try(PrintWriter log = new PrintWriter(new FileWriter("error.log", true))) {
            log.println("[" + new java.util.Date() + "] ERROR: " + message);
            ex.printStackTrace(log);
        } catch(IOException e) {
            System.out.println("Error writing to log file: " + e);
        }
    }

    // Contoh method notify pengguna via JOptionPane
    static void notifyUser(String message, String title, int messageType){
        JOptionPane.showMessageDialog(null, message, title, messageType);
    }

    // Ambil data absen lengkap dengan filter dan paging untuk skala besar
    static List<Map<String,Object>> getAbsensiPaging(int page, int pageSize, String filterShift){
        List<Map<String,Object>> result = new ArrayList<>();
        try(Connection conn = getConnection()){
            String sql = "SELECT * FROM absen WHERE 1=1 ";
            if(filterShift != null && !filterShift.trim().isEmpty()){
                sql += " AND nama_shift LIKE ? ";
            }
            sql += " ORDER BY tanggal DESC LIMIT ? OFFSET ? ";
            PreparedStatement stmt = conn.prepareStatement(sql);
            int param = 1;
            if(filterShift != null && !filterShift.trim().isEmpty()){
                stmt.setString(param++, "%" + filterShift + "%");
            }
            stmt.setInt(param++, pageSize);
            stmt.setInt(param++, (page-1)*pageSize);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                Map<String,Object> row = new HashMap<>();
                row.put("NIP", rs.getString("NIP"));
                row.put("nama_shift", rs.getString("nama_shift"));
                row.put("tanggal", rs.getDate("tanggal"));
                row.put("jam_masuk", rs.getTime("jam_masuk"));
                row.put("jam_keluar", rs.getTime("jam_keluar"));
                row.put("total_lembur", rs.getInt("total_lembur"));
                result.add(row);
            }
        } catch(SQLException t){
            System.out.println("getAbsensiPaging error: "+t);
        }
        return result;
    }

    // Count total record absensi dengan filter
    static int countAbsensi(String filterShift){
        int total = 0;
        try(Connection conn = getConnection()){
            String sql = "SELECT COUNT(*) as total FROM absen WHERE 1=1 ";
            if(filterShift != null && !filterShift.trim().isEmpty()) {
                sql += " AND nama_shift LIKE ? ";
            }
            PreparedStatement stmt = conn.prepareStatement(sql);
            if(filterShift != null && !filterShift.trim().isEmpty()){
                stmt.setString(1, "%" + filterShift + "%");
            }
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                total = rs.getInt("total");
            }
        } catch(SQLException t) {
            System.out.println("countAbsensi error: "+t);
        }
        return total;
    }
    
    // Contoh method reset table model/apply paging results to JTable (dengan asumsi shift.table adalah JTable)
    public static void refreshTableWithPaging(DefaultTableModel model, int page, int pageSize, String filterShift) {
        List<Map<String,Object>> data = getAbsensiPaging(page, pageSize, filterShift);
        model.setRowCount(0);
        for(Map<String,Object> row : data){
            model.addRow(new Object[]{
                    row.get("NIP"),
                    row.get("nama_shift"),
                    row.get("tanggal").toString(),
                    row.get("jam_masuk").toString(),
                    row.get("jam_keluar").toString(),
                    row.get("total_lembur")
            });
        }
        shift.table.setModel(model);
    }

    // Fitur logging aktivitas user (misal log tanggal akses)
    static void logUserActivity(String NIP, String activity){
        try(Connection conn = getConnection()){
            String query = "INSERT INTO user_activity (NIP, activity, timestamp) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, NIP);
            stmt.setString(2, activity);
            stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            stmt.execute();
        } catch(SQLException t){
            System.out.println("logUserActivity error: " + t);
        }
    }

    // Sebutkan lebih banyak metode yang relevan dapat ditambahkan sesuai kebutuhan aplikasi

}
