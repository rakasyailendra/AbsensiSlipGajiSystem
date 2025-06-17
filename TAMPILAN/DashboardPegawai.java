package apk;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Kelas AdminDashboard yang telah di-refactor dan dikembangkan sepenuhnya.
 * Menggunakan CardLayout untuk navigasi panel, struktur kode yang bersih,
 * dan penambahan fitur-fitur baru seperti Dashboard Statistik dan Manajemen Pegawai.
 *
 * @author [Pengembang]
 * @version 2.0
 */
public class AdminDashboard extends JFrame {

    // --- Atribut Utama ---
    private final String adminNip;
    private final String adminNama;
    private final boolean isAdmin;

    // --- Komponen Utama ---
    private JPanel mainPanelContainer;
    private CardLayout cardLayout;

    // --- Komponen Panel Dasbor ---
    private JLabel totalPegawaiLabel;
    private JLabel pegawaiShiftLabel;
    private JLabel absensiHariIniLabel;

    // --- Komponen Panel Manajemen Shift ---
    private JTable shiftTable;
    private DefaultTableModel shiftTableModel;
    private JComboBox<String> shiftComboBox;
    private JDateChooser dateChooser;
    private JTextField searchNipField;
    private JTextField updateNipField;
    private JLabel shiftStatusLabel;

    // --- Komponen Panel Manajemen Pegawai ---
    private JTable pegawaiTable;
    private DefaultTableModel pegawaiTableModel;
    private JTextField nipPegawaiField, namaPegawaiField, jabatanPegawaiField;
    
    // --- Simulasikan koneksi ke database/entity ---
    // private AdminEntity adminEntity = new AdminEntity();
    // private PegawaiEntity pegawaiEntity = new PegawaiEntity();

    public AdminDashboard(String nip, String nama, boolean isAdmin) {
        this.adminNip = nip;
        this.adminNama = nama;
        this.isAdmin = isAdmin;

        setupFrame();
        initComponents();
        loadInitialData();
    }

    /**
     * Mengatur properti dasar untuk JFrame.
     */
    private void setupFrame() {
        setTitle("Admin Dashboard - Aplikasi Absensi dan Gaji");
        Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("logo.png"));
        setIconImage(icon);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1200, 700));
        setLocationRelativeTo(null);
    }

    /**
     * Menginisialisasi dan menyusun semua komponen GUI.
     */
    private void initComponents() {
        // --- Layout Utama ---
        setLayout(new BorderLayout(5, 5));

        // --- Panel Sidebar (Navigasi) ---
        JPanel sidebarPanel = createSidebar();
        add(sidebarPanel, BorderLayout.WEST);

        // --- Panel Kontainer Utama dengan CardLayout ---
        cardLayout = new CardLayout();
        mainPanelContainer = new JPanel(cardLayout);

        mainPanelContainer.add(createDashboardPanel(), "DASHBOARD");
        mainPanelContainer.add(createShiftManagementPanel(), "MANAJEMEN_SHIFT");
        mainPanelContainer.add(createEmployeeManagementPanel(), "MANAJEMEN_PEGAWAI");

        add(mainPanelContainer, BorderLayout.CENTER);
    }

    /**
     * Membuat panel sidebar untuk navigasi.
     */
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sidebar.setBackground(new Color(240, 240, 240));
        sidebar.setPreferredSize(new Dimension(200, 0));

        // Info Admin
        JPanel adminInfoPanel = new JPanel(new GridLayout(0, 1));
        adminInfoPanel.setBorder(BorderFactory.createTitledBorder("Admin Info"));
        adminInfoPanel.add(new JLabel("NIP: " + adminNip));
        adminInfoPanel.add(new JLabel("Nama: " + adminNama));
        
        // Tombol Navigasi
        JButton dashboardButton = createNavButton("Dasbor", "DASHBOARD");
        JButton shiftButton = createNavButton("Manajemen Shift", "MANAJEMEN_SHIFT");
        JButton pegawaiButton = createNavButton("Manajemen Pegawai", "MANAJEMEN_PEGAWAI");
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());

        sidebar.add(adminInfoPanel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
        sidebar.add(dashboardButton);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(shiftButton);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(pegawaiButton);
        sidebar.add(Box.createVerticalGlue()); // Spacer
        sidebar.add(logoutButton);

        return sidebar;
    }
    
    private JButton createNavButton(String text, String cardName) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, button.getPreferredSize().height));
        button.addActionListener(e -> cardLayout.show(mainPanelContainer, cardName));
        return button;
    }

    /**
     * Membuat panel Dasbor Utama.
     */
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 20));
        panel.setBorder(BorderFactory.createTitledBorder(null, "Dasbor Statistik", TitledBorder.CENTER, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 18)));
        
        totalPegawaiLabel = createStatLabel("Total Pegawai", "0");
        pegawaiShiftLabel = createStatLabel("Pegawai Sedang Shift", "0");
        absensiHariIniLabel = createStatLabel("Absensi Hari Ini", "0");

        panel.add(totalPegawaiLabel);
        panel.add(pegawaiShiftLabel);
        panel.add(absensiHariIniLabel);
        
        return panel;
    }

    private JLabel createStatLabel(String title, String value) {
        JLabel label = new JLabel("<html><div style='text-align: center;'>" + title + "<br><b style='font-size: 24pt;'>" + value + "</b></div></html>", SwingConstants.CENTER);
        label.setBorder(BorderFactory.createEtchedBorder());
        label.setOpaque(true);
        label.setBackground(Color.WHITE);
        return label;
    }

    /**
     * Membuat panel untuk Manajemen Shift.
     */
    private JPanel createShiftManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel Kiri: Update dan Cari Shift
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        // Panel Update
        JPanel updatePanel = new JPanel(new GridLayout(0, 2, 5, 5));
        updatePanel.setBorder(BorderFactory.createTitledBorder("Update Shift Pegawai"));
        updateNipField = new JTextField();
        // String[] jadwal = admin_entity.getShiftFromDatabase(); // Simulasi
        String[] jadwal = {"Pagi", "Siang", "Malam", "Libur"};
        shiftComboBox = new JComboBox<>(jadwal);
        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(e -> updateShift());
        
        updatePanel.add(new JLabel("NIP Pegawai:"));
        updatePanel.add(updateNipField);
        updatePanel.add(new JLabel("Pilih Shift:"));
        updatePanel.add(shiftComboBox);
        updatePanel.add(new JLabel("")); // Spacer
        updatePanel.add(updateButton);

        // Panel Cari
        JPanel searchPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Cari Data Shift"));
        searchNipField = new JTextField();
        searchNipField.addActionListener(e -> searchShiftByNip()); // Cari saat menekan Enter
        dateChooser = new JDateChooser(new Date()); // Default hari ini
        dateChooser.setDateFormatString("dd MMMM yyyy");
        JButton searchDateButton = new JButton("Cari per Tanggal");
        searchDateButton.addActionListener(e -> searchShiftByDate());
        
        searchPanel.add(new JLabel("Cari NIP:"));
        searchPanel.add(searchNipField);
        searchPanel.add(new JLabel("Cari Tanggal:"));
        searchPanel.add(dateChooser);
        searchPanel.add(new JLabel("")); // Spacer
        searchPanel.add(searchDateButton);
        
        leftPanel.add(updatePanel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        leftPanel.add(searchPanel);
        leftPanel.add(Box.createVerticalGlue());

        // Panel Kanan: Tabel Shift
        shiftTableModel = new DefaultTableModel(new String[]{"NIP", "Tanggal", "Nama Shift", "Jam Masuk", "Jam Keluar"}, 0);
        shiftTable = new JTable(shiftTableModel);
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Data Shift Kerja"));
        tablePanel.add(new JScrollPane(shiftTable), BorderLayout.CENTER);

        // Status bar
        shiftStatusLabel = new JLabel("Selamat datang di Manajemen Shift.");
        tablePanel.add(shiftStatusLabel, BorderLayout.SOUTH);

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(tablePanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Membuat panel untuk Manajemen Pegawai.
     */
    private JPanel createEmployeeManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Form Data Pegawai"));
        nipPegawaiField = new JTextField();
        namaPegawaiField = new JTextField();
        jabatanPegawaiField = new JTextField();
        
        formPanel.add(new JLabel("NIP:"));
        formPanel.add(nipPegawaiField);
        formPanel.add(new JLabel("Nama Lengkap:"));
        formPanel.add(namaPegawaiField);
        formPanel.add(new JLabel("Jabatan:"));
        formPanel.add(jabatanPegawaiField);
        
        // Tombol Aksi Form
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Simpan");
        JButton clearButton = new JButton("Bersihkan");
        saveButton.addActionListener(e -> savePegawai());
        clearButton.addActionListener(e -> clearPegawaiForm());
        actionPanel.add(saveButton);
        actionPanel.add(clearButton);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(actionPanel, BorderLayout.SOUTH);

        // Table Panel
        pegawaiTableModel = new DefaultTableModel(new String[]{"NIP", "Nama", "Jabatan"}, 0);
        pegawaiTable = new JTable(pegawaiTableModel);
        pegawaiTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pegawaiTable.getSelectionModel().addListSelectionListener(e -> fillPegawaiFormFromTable());
        
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Daftar Pegawai"));
        tablePanel.add(new JScrollPane(pegawaiTable), BorderLayout.CENTER);
        
        // Tombol Aksi Tabel
        JPanel tableActionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton deleteButton = new JButton("Hapus Pegawai Terpilih");
        deleteButton.addActionListener(e -> deletePegawai());
        tableActionPanel.add(deleteButton);
        tablePanel.add(tableActionPanel, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(tablePanel, BorderLayout.CENTER);
        
        return panel;
    }

    // --- Logika Aplikasi dan Event Handling ---

    private void loadInitialData() {
        // Memuat data awal saat aplikasi dibuka
        updateDashboardStats();
        loadShiftTableData();
        loadPegawaiTableData();
    }

    private void updateDashboardStats() {
        // Simulasi pengambilan data
        int totalPegawai = 50; // pegawaiEntity.count();
        int onShift = 12;      // adminEntity.countOnShift();
        int todayAttendance = 45; // adminEntity.countTodayAttendance();
        
        totalPegawaiLabel.setText("<html><div style='text-align: center;'>Total Pegawai<br><b style='font-size: 24pt;'>" + totalPegawai + "</b></div></html>");
        pegawaiShiftLabel.setText("<html><div style='text-align: center;'>Pegawai Sedang Shift<br><b style='font-size: 24pt;'>" + onShift + "</b></div></html>");
        absensiHariIniLabel.setText("<html><div style='text-align: center;'>Absensi Hari Ini<br><b style='font-size: 24pt;'>" + todayAttendance + "</b></div></html>");
    }

    private void loadShiftTableData() {
        // Simulasi memuat semua data shift
        // adminEntity.showTable(shiftTableModel);
        shiftTableModel.setRowCount(0); // Bersihkan tabel
        shiftTableModel.addRow(new Object[]{"1001", "17/06/2025", "Pagi", "08:00", "16:00"});
        shiftTableModel.addRow(new Object[]{"1002", "17/06/2025", "Pagi", "08:00", "16:00"});
    }

    private void loadPegawaiTableData() {
        // Simulasi memuat data pegawai
        pegawaiTableModel.setRowCount(0);
        pegawaiTableModel.addRow(new Object[]{"1001", "Budi Santoso", "Staff IT"});
        pegawaiTableModel.addRow(new Object[]{"1002", "Citra Lestari", "HRD"});
    }

    private void updateShift() {
        String nip = updateNipField.getText();
        String shift = (String) shiftComboBox.getSelectedItem();
        if (nip.isEmpty()) {
            JOptionPane.showMessageDialog(this, "NIP tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // adminEntity.pergantianShift(nip, shift);
        shiftStatusLabel.setText("Shift untuk NIP " + nip + " berhasil diperbarui menjadi " + shift + ".");
        loadShiftTableData(); // Refresh tabel
        updateNipField.setText("");
    }

    private void searchShiftByNip() {
        // adminEntity.showTableOnNIP(shiftTableModel, searchNipField.getText());
        shiftStatusLabel.setText("Menampilkan hasil pencarian untuk NIP: " + searchNipField.getText());
    }
    
    private void searchShiftByDate() {
        if (dateChooser.getDate() == null) return;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = sdf.format(dateChooser.getDate());
        // adminEntity.showTableOnDate(shiftTableModel, formattedDate);
        shiftStatusLabel.setText("Menampilkan hasil pencarian untuk tanggal: " + formattedDate);
    }
    
    private void savePegawai() {
        String nip = nipPegawaiField.getText();
        // Validasi dan simpan data
        // pegawaiEntity.save(nip, ...);
        JOptionPane.showMessageDialog(this, "Data pegawai berhasil disimpan.");
        loadPegawaiTableData(); // Refresh
        clearPegawaiForm();
    }
    
    private void deletePegawai() {
        int selectedRow = pegawaiTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih pegawai yang akan dihapus dari tabel.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String nip = (String) pegawaiTableModel.getValueAt(selectedRow, 0);
        int choice = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus pegawai dengan NIP: " + nip + "?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            // pegawaiEntity.delete(nip);
            loadPegawaiTableData();
        }
    }

    private void fillPegawaiFormFromTable() {
        int selectedRow = pegawaiTable.getSelectedRow();
        if (selectedRow != -1) {
            nipPegawaiField.setText((String) pegawaiTableModel.getValueAt(selectedRow, 0));
            namaPegawaiField.setText((String) pegawaiTableModel.getValueAt(selectedRow, 1));
            jabatanPegawaiField.setText((String) pegawaiTableModel.getValueAt(selectedRow, 2));
        }
    }
    
    private void clearPegawaiForm() {
        nipPegawaiField.setText("");
        namaPegawaiField.setText("");
        jabatanPegawaiField.setText("");
        pegawaiTable.clearSelection();
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin logout?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            // new LoginFrame().setVisible(true); // Kembali ke frame login
        }
    }

    public static void main(String[] args) {
        // Menggunakan Look and Feel sistem untuk tampilan yang lebih modern
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Menjalankan frame di Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            new AdminDashboard("A001", "Admin Utama", true).setVisible(true);
        });
    }
}
