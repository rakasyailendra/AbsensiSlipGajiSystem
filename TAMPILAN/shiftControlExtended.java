package apk;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * shiftControlExtended merupakan kelas pengelola absensi dengan UI Swing,
 * fitur ekspor/import file, validasi, logging, paging, dan pengujian.
 */
public class shiftControlExtended {

    // Waktu shift validasi
    private LocalTime jamMasukShift;
    private LocalTime jamKeluarShift;

    // Komponen UI
    private JFrame frame;
    private JTextField nipField, jamMasukField, menitMasukField, jamKeluarField, menitKeluarField, namaShiftField;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> shiftComboBox;
    private JButton btnSimpan, btnLoad, btnExportCSV, btnImportJSON, btnUndo, btnRedo, btnSearch;

    // Undo/Redo stack untuk history aksi
    private Deque<Map<String,Object>> undoStack = new ArrayDeque<>();
    private Deque<Map<String,Object>> redoStack = new ArrayDeque<>();

    public shiftControlExtended() {
        initializeGUI();
        // Inisialisasi shift options
        List<String> shifts = shift_entity.getShiftList();
        for(String shift : shifts) {
            shiftComboBox.addItem(shift);
        }
    }

    /**
     * Membuat dan menampilkan GUI utama
     */
    private void initializeGUI() {
        frame = new JFrame("Shift Absensi Control");
        frame.setSize(900, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10,10));

        // Panel input
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel lblNIP = new JLabel("NIP:");
        nipField = new JTextField(10);
        JLabel lblShift = new JLabel("Nama Shift:");
        shiftComboBox = new JComboBox<>();
        JLabel lblJamMasuk = new JLabel("Jam Masuk (jam:min):");
        jamMasukField = new JTextField(2);
        menitMasukField = new JTextField(2);
        JLabel lblJamKeluar = new JLabel("Jam Keluar (jam:min):");
        jamKeluarField = new JTextField(2);
        menitKeluarField = new JTextField(2);

        btnSimpan = new JButton("Simpan Absensi");
        btnUndo = new JButton("Undo");
        btnRedo = new JButton("Redo");
        btnLoad = new JButton("Muat Data");
        btnExportCSV = new JButton("Export CSV");
        btnImportJSON = new JButton("Import JSON");
        btnSearch = new JButton("Cari");

        // Layout input field dan label
        gbc.insets = new Insets(5,5,5,5);
        gbc.gridx = 0; gbc.gridy = 0; inputPanel.add(lblNIP, gbc);
        gbc.gridx = 1; gbc.gridy = 0; inputPanel.add(nipField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; inputPanel.add(lblShift, gbc);
        gbc.gridx = 1; gbc.gridy = 1; inputPanel.add(shiftComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 2; inputPanel.add(lblJamMasuk, gbc);
        JPanel masukPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        masukPanel.add(jamMasukField); masukPanel.add(new JLabel(":")); masukPanel.add(menitMasukField);
        gbc.gridx = 1; gbc.gridy = 2; inputPanel.add(masukPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 3; inputPanel.add(lblJamKeluar, gbc);
        JPanel keluarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        keluarPanel.add(jamKeluarField); keluarPanel.add(new JLabel(":")); keluarPanel.add(menitKeluarField);
        gbc.gridx = 1; gbc.gridy = 3; inputPanel.add(keluarPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 4; inputPanel.add(btnSimpan, gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        JPanel undoRedoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        undoRedoPanel.add(btnUndo);
        undoRedoPanel.add(btnRedo);
        inputPanel.add(undoRedoPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 5; inputPanel.add(btnLoad, gbc);
        gbc.gridx = 1; gbc.gridy = 5;
        JPanel exportImportPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        exportImportPanel.add(btnExportCSV);
        exportImportPanel.add(btnImportJSON);
        inputPanel.add(exportImportPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 6; inputPanel.add(btnSearch, gbc);

        // Tabel absensi
        tableModel = new DefaultTableModel(new Object[]{"NIP","Nama Shift","Tanggal","Jam Masuk","Jam Keluar","Total Lembur"},0);
        table = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(table);

        frame.add(inputPanel, BorderLayout.WEST);
        frame.add(tableScroll, BorderLayout.CENTER);

        // Event handler tombol simpan
        btnSimpan.addActionListener(e -> simpanAbsensi());
        btnLoad.addActionListener(e -> muatData());
        btnUndo.addActionListener(e -> undo());
        btnRedo.addActionListener(e -> redo());
        btnExportCSV.addActionListener(e -> exportCSV());
        btnImportJSON.addActionListener(e -> importJSON());
        btnSearch.addActionListener(e -> cariAbsensi());

        frame.setVisible(true);
    }

    /**
     * Proses simpan absensi dengan validasi dan penyimpanan ke database
     */
    private void simpanAbsensi() {
        String nip = nipField.getText().trim();
        String namaShift = (String) shiftComboBox.getSelectedItem();
        String jamMasuk = jamMasukField.getText().trim();
        String menitMasuk = menitMasukField.getText().trim();
        String jamKeluar = jamKeluarField.getText().trim();
        String menitKeluar = menitKeluarField.getText().trim();

        if(nip.isEmpty() || namaShift == null || jamMasuk.isEmpty() || menitMasuk.isEmpty() || jamKeluar.isEmpty() || menitKeluar.isEmpty()) {
            JOptionPane.showMessageDialog(frame,"Mohon isi semua field");
            return;
        }

        // Validasi format jam dan menit
        if(!validJamMenit(jamMasuk, menitMasuk) || !validJamMenit(jamKeluar, menitKeluar)) {
            JOptionPane.showMessageDialog(frame,"Format jam atau menit tidak valid! Harus angka antara 0-23 dan 0-59.");
            return;
        }

        // Ambil waktu shift dari database
        String shiftMasuk = getShiftJamMasuk(namaShift);
        String shiftKeluar = getShiftJamKeluar(namaShift);
        if(shiftMasuk == null || shiftKeluar == null) {
            JOptionPane.showMessageDialog(frame, "Data shift tidak ditemukan");
            return;
        }

        boolean berhasil = shiftControl.ValidasiShift(jamMasuk, menitMasuk, jamKeluar, menitKeluar, shiftMasuk, shiftKeluar, nip, namaShift);
        if(berhasil){
            JOptionPane.showMessageDialog(frame, "Absensi berhasil disimpan");
            simpanLogAktivitas(nip, "Simpan absensi shift " + namaShift);
            muatData();
        } else {
            JOptionPane.showMessageDialog(frame, "Absensi gagal disimpan");
        }
    }

    /**
     * Validasi format jam dan menit (antara 0 sampai max standar)
     */
    private boolean validJamMenit(String jam, String menit) {
        try {
            int intJam = Integer.parseInt(jam);
            int intMenit = Integer.parseInt(menit);
            if(intJam < 0 || intJam > 23) return false;
            if(intMenit < 0 || intMenit > 59) return false;
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }

    /**
     * Mendapatkan jam masuk shift dari DB (format "HH:mm")
     */
    private String getShiftJamMasuk(String namaShift) {
        try {
            List<String> shifts = shift_entity.getShiftList();
            if(shifts.contains(namaShift)) {
                switch(namaShift) {
                    case "Shift Pagi": return "07:00";
                    case "Shift Siang": return "15:00";
                    case "Shift Malam": return "23:00";
                    default: return "07:00";
                }
            }
        } catch(Exception e) {
            System.out.println("Error getShiftJamMasuk: "+e);
        }
        return null;
    }

    /**
     * Mendapatkan jam keluar shift dari DB (format "HH:mm")
     */
    private String getShiftJamKeluar(String namaShift) {
        try {
            switch(namaShift) {
                case "Shift Pagi": return "15:00";
                case "Shift Siang": return "23:00";
                case "Shift Malam": return "07:00";
                default: return "15:00";
            }
        } catch(Exception e) {
            System.out.println("Error getShiftJamKeluar: "+e);
        }
        return null;
    }

    /**
     * Muat data absensi dari database dan tampilkan di tabel
     */
    private void muatData() {
        String nip = nipField.getText().trim();
        if(nip.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Masukkan NIP untuk muat data");
            return;
        }
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        shift_entity.loadAbsenToModel(model, nip);
    }

    /**
     * Undo aksi terakhir
     */
    private void undo() {
        boolean hasil = shift_entity.undo();
        if(hasil) {
            JOptionPane.showMessageDialog(frame, "Undo berhasil");
            muatData();
        }
    }

    /**
     * Redo aksi terakhir yang di-undo
     */
    private void redo() {
        boolean hasil = shift_entity.redo();
        if(hasil) {
            JOptionPane.showMessageDialog(frame, "Redo berhasil");
            muatData();
        }
    }

    /**
     * Ekspor data absensi ke CSV dengan dialog save file
     */
    private void exportCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV File", "csv"));
        if(fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if(!file.getAbsolutePath().endsWith(".csv")) {
                file = new File(file.getAbsolutePath() + ".csv");
            }
            String nip = nipField.getText().trim();
            List<Map<String,Object>> data = shift_entity.searchAbsensi(nip, null, null);
            boolean success = shift_entity.exportAbsensiToCSV(file.getAbsolutePath(), data);
            if(success) {
                JOptionPane.showMessageDialog(frame, "Export CSV berhasil:\n" + file.getAbsolutePath());
            } else {
                JOptionPane.showMessageDialog(frame, "Export CSV gagal!");
            }
        }
    }

    /**
     * Impor data absensi dari file JSON
     */
    private void importJSON() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("JSON File", "json"));
        if(fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                String content = new String(Files.readAllBytes(file.toPath()));
                JSONArray jsonArr = new JSONArray(content);
                int successCount = 0;
                for(int i=0; i<jsonArr.length(); i++) {
                    JSONObject obj = jsonArr.getJSONObject(i);
                    String nip = obj.getString("NIP");
                    String shift = obj.getString("nama_shift");
                    Date tanggal = Date.valueOf(obj.getString("tanggal"));
                    Time jam_masuk = Time.valueOf(obj.getString("jam_masuk"));
                    Time jam_keluar = Time.valueOf(obj.getString("jam_keluar"));
                    int lembur = obj.getInt("total_lembur");
                    if(shift_entity.insertAbsen(nip, tanggal, shift, jam_masuk, jam_keluar, lembur)) {
                        successCount++;
                    }
                }
                JOptionPane.showMessageDialog(frame, "Impor JSON selesai. Berhasil: " + successCount + "/" + jsonArr.length());
                muatData();
            } catch(Exception e) {
                JOptionPane.showMessageDialog(frame, "Gagal impor JSON: " + e.getMessage());
            }
        }
    }

    /**
     * Fungsi pencarian absensi dengan input filter shift dan tanggal
     */
    private void cariAbsensi() {
        String shiftFilter = JOptionPane.showInputDialog(frame, "Masukkan filter nama shift (kosongkan untuk semua):");
        String nip = nipField.getText().trim();
        if(nip.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Masukkan NIP terlebih dahulu");
            return;
        }
        List<Map<String,Object>> hasil = shift_entity.searchAbsensi(nip, null, null);
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        for(Map<String,Object> row : hasil) {
            if(shiftFilter == null || shiftFilter.trim().isEmpty() || ((String)row.get("nama_shift")).contains(shiftFilter)) {
                model.addRow(new Object[]{
                        row.get("NIP"),
                        row.get("nama_shift"),
                        row.get("tanggal"),
                        row.get("jam_masuk"),
                        row.get("jam_keluar"),
                        row.get("total_lembur")
                });
            }
        }
        shift.table.setModel(model);
    }

    /**
     * Simpan log aktivitas pengguna
     */
    private void simpanLogAktivitas(String nip, String aktivitas) {
        shift_entity.logUserActivity(nip, aktivitas);
    }

    /**
     * Main method untuk jalankan GUI aplikasi
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new shiftControlExtended();
        });
    }
}

