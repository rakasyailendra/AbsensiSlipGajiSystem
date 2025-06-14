u /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

package GUI;

// Import yang sudah ada di file Anda, ditambah yang mungkin kurang
import GUI.DatabaseConnection; // Pastikan ini benar dan ada di package GUI
import GUI.LoginFrame;         // Untuk kembali ke LoginFrame saat logout
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date; // Menggunakan java.util.Date untuk JDateChooser
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import javax.lang.model.SourceVersion;

public class PegawaiDashboard extends javax.swing.JFrame {

    // Variabel instance untuk menyimpan informasi pegawai dan file
    private int pegawaiId;
    private String namaPegawai;
    private File selectedFileBukti; // Untuk menyimpan file foto yang dipilih

    /**
     * Konstruktor default - biasanya digunakan oleh NetBeans GUI Builder.
     * Sebaiknya tidak dipanggil langsung jika frame ini membutuhkan data login.
     */
    public PegawaiDashboard() {
        initComponents();
        setLocationRelativeTo(null); // Tengahkan frame
        // Inisialisasi default lain jika diperlukan saat frame dibuka tanpa data login
        // Contoh: Disable beberapa tombol atau tampilkan pesan
    }

    /**
     * Konstruktor yang dimodifikasi untuk menerima info pegawai yang login.
     * Ini yang akan dipanggil dari LoginFrame.
     */
    public PegawaiDashboard(int pegawaiId, String namaPegawai) {
        this.pegawaiId = pegawaiId;
        this.namaPegawai = namaPegawai;

        initComponents(); // HARUS dipanggil pertama untuk menginisialisasi komponen GUI
        setLocationRelativeTo(null); // Tengahkan frame

        // Set tampilan awal berdasarkan data pegawai
        if (lblWelcome != null) { // Pemeriksaan null untuk keamanan
            lblWelcome.setText("SELAMAT DATANG, " + (this.namaPegawai != null ? this.namaPegawai.toUpperCase() : "PEGAWAI"));
        }
        if (txtNama != null) {
            txtNama.setText(this.namaPegawai != null ? this.namaPegawai : "");
            txtNama.setEditable(false); // Nama tidak bisa diubah di form absensi
        }
        if (txtJabatan != null) {
             txtJabatan.setEditable(false); // Jabatan juga tidak bisa diubah
        }


        // Panggil method untuk memuat data awal
        loadJabatanPegawai();
        loadShiftPegawai();
        loadSlipGajiPegawai();
    }

    // METHOD UNTUK MEMUAT JABATAN PEGAWAI
    private void loadJabatanPegawai() {
        if (txtJabatan == null) return; // Pastikan komponen sudah diinisialisasi

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                 System.err.println("Koneksi DB gagal untuk loadJabatanPegawai.");
                return;
            }

            String sql = "SELECT jabatan FROM pengguna WHERE id_pengguna = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, this.pegawaiId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                txtJabatan.setText(rs.getString("jabatan"));
            } else {
                txtJabatan.setText("-"); // Default jika tidak ada jabatan
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat jabatan: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    // METHOD UNTUK MEMUAT SHIFT PEGAWAI
    private void loadShiftPegawai() {
        if (tblShiftPegawai == null) return; // Pastikan komponen sudah diinisialisasi
        DefaultTableModel model = (DefaultTableModel) tblShiftPegawai.getModel();
        model.setRowCount(0);

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("Koneksi DB gagal untuk loadShiftPegawai.");
                return;
            }

            String sql = "SELECT tanggal, jam_mulai, jam_selesai, keterangan FROM shift WHERE id_pegawai = ? ORDER BY tanggal DESC, jam_mulai ASC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, this.pegawaiId);
            rs = pstmt.executeQuery();

            // Kolom di tblShiftPegawai Anda: "Nama", "ID", "Jam Masuk", "Tanggal"
            while (rs.next()) {
                model.addRow(new Object[]{
                    this.namaPegawai,         // Kolom "Nama"
                    this.pegawaiId,           // Kolom "ID"
                    rs.getTime("jam_mulai"),  // Kolom "Jam Masuk"
                    rs.getDate("tanggal")     // Kolom "Tanggal"
                    // Jika tabel GUI punya kolom Jam Keluar atau Keterangan, tambahkan di sini:
                    // rs.getTime("jam_selesai"),
                    // rs.getString("keterangan")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data shift: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
             try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    // METHOD UNTUK MEMUAT SLIP GAJI PEGAWAI
    private void loadSlipGajiPegawai() {
        if (jTable1 == null || cbBulanSlip == null) return; // Pastikan komponen sudah diinisialisasi

        DefaultTableModel model = (DefaultTableModel) jTable1.getModel(); // jTable1 adalah tabel slip gaji
        model.setRowCount(0);

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("Koneksi DB gagal untuk loadSlipGajiPegawai.");
                return;
            }

            String sql = "SELECT YEAR(a.tanggal) as tahun, MONTH(a.tanggal) as bulan, COUNT(a.id_absensi) as total_hari_hadir, " +
                         "(COUNT(a.id_absensi) * 50000) as total_gaji " + // Gaji 50rb/hari
                         "FROM absensi a " +
                         "WHERE a.id_pegawai = ? AND a.status_kehadiran = 'hadir' " +
                         "GROUP BY YEAR(a.tanggal), MONTH(a.tanggal) " +
                         "ORDER BY tahun DESC, bulan DESC";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, this.pegawaiId);
            rs = pstmt.executeQuery();

            cbBulanSlip.removeAllItems();
            cbBulanSlip.addItem("--Pilih Periode--");

            while (rs.next()) {
                int tahun = rs.getInt("tahun");
                int bulan = rs.getInt("bulan");
                int totalHariHadir = rs.getInt("total_hari_hadir");
                double totalGaji = rs.getDouble("total_gaji");
                String periode = String.format("%02d-%d", bulan, tahun);

                // Kolom di jTable1: "Tanggal" (Periode), "nama", "Total Kehadiran", "Total Gaji"
                model.addRow(new Object[]{
                    periode,
                    this.namaPegawai,
                    totalHariHadir,
                    totalGaji
                });

                boolean exists = false;
                for (int i = 0; i < cbBulanSlip.getItemCount(); i++) {
                    if (periode.equals(cbBulanSlip.getItemAt(i))) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    cbBulanSlip.addItem(periode);
                }
            }
            if (cbBulanSlip.getItemCount() > 1) {
                cbBulanSlip.setSelectedIndex(1); // Pilih item pertama setelah default jika ada data
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data slip gaji: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
             try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    // METHOD UNTUK LOGOUT
    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Anda yakin ingin logout?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // Di LoginFrame, Anda mungkin ingin menambahkan method static untuk mereset info login global jika ada.
            // LoginFrame.resetLoggedInUserInfo(); // Contoh
            new LoginFrame().setVisible(true); // Kembali ke halaman login
            this.dispose(); // Tutup dashboard ini
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        tabDashboardPegawai = new javax.swing.JTabbedPane();
        panelPegawai = new javax.swing.JPanel();
        PanelMaincontent = new javax.swing.JPanel();
        btnSimpanAbsen = new javax.swing.JButton();
        chkAbsen = new javax.swing.JCheckBox();
        btnUploadFoto = new javax.swing.JButton();
        lblFoto = new javax.swing.JLabel();
        spinnerKeluar = new javax.swing.JSpinner();
        lblJamKeluar = new javax.swing.JLabel();
        spinnerMasuk = new javax.swing.JSpinner();
        lblJamMasuk = new javax.swing.JLabel();
        lblTanggal = new javax.swing.JLabel();
        txtJabatan = new java.awt.TextField();
        lblJabatan = new javax.swing.JLabel();
        txtNama = new java.awt.TextField();
        lblNamapPegawai = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        lblWelcome = new javax.swing.JLabel();
        dateChooser = new com.toedter.calendar.JDateChooser();
        MonthChooser = new com.toedter.calendar.JMonthChooser();
        YearChooser = new com.toedter.calendar.JYearChooser();
        PanelSide = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        IconPegawai = new javax.swing.JLabel();
        panelShiftPegawai = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        lblShiftTitle = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblShiftPegawai = new javax.swing.JTable();
        jButton2 = new javax.swing.JButton();
        panelSlipGaji = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        cbNamaSlip = new javax.swing.JComboBox<>();
        cbTanggalSlip = new javax.swing.JComboBox<>();
        cbHariSlip = new javax.swing.JComboBox<>();
        cbBulanSlip = new javax.swing.JComboBox<>();
        btnLihatSlip = new javax.swing.JButton();
        btnLogout = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        panelPegawai.setBackground(new java.awt.Color(255, 255, 255));

        PanelMaincontent.setBackground(new java.awt.Color(0, 102, 102));

        btnSimpanAbsen.setText("OK");
        btnSimpanAbsen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanAbsenActionPerformed(evt);
            }
        });

        chkAbsen.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        chkAbsen.setForeground(new java.awt.Color(255, 255, 255));
        chkAbsen.setText("ABSEN");
        chkAbsen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkAbsenActionPerformed(evt);
            }
        });

        btnUploadFoto.setText("UPLOD FOTO");
        btnUploadFoto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadFotoActionPerformed(evt);
            }
        });

        lblFoto.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblFoto.setForeground(new java.awt.Color(255, 255, 255));
        lblFoto.setText("Bukti Foto");

        spinnerKeluar.setModel(new javax.swing.SpinnerDateModel());

        lblJamKeluar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblJamKeluar.setForeground(new java.awt.Color(255, 255, 255));
        lblJamKeluar.setText("Jam Keluar");

        spinnerMasuk.setModel(new javax.swing.SpinnerDateModel());

        lblJamMasuk.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblJamMasuk.setForeground(new java.awt.Color(255, 255, 255));
        lblJamMasuk.setText("Jam Masuk ");

        lblTanggal.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTanggal.setForeground(new java.awt.Color(255, 255, 255));
        lblTanggal.setText("Tanggal      ");

        txtJabatan.setEditable(false);
        txtJabatan.setText("textField1");

        lblJabatan.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        lblJabatan.setForeground(new java.awt.Color(255, 255, 255));
        lblJabatan.setText("Jabatan     ");

        txtNama.setEditable(false);
        txtNama.setText("textField1");
        txtNama.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNamaActionPerformed(evt);
            }
        });

        lblNamapPegawai.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        lblNamapPegawai.setForeground(new java.awt.Color(255, 255, 255));
        lblNamapPegawai.setText("Nama       ");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("ABSENSI HARI INI");

        lblWelcome.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        lblWelcome.setForeground(new java.awt.Color(255, 255, 255));
        lblWelcome.setText("SELAMAT DATANG");

        dateChooser.setDateFormatString("yyyy-MM-dd");

        javax.swing.GroupLayout PanelMaincontentLayout = new javax.swing.GroupLayout(PanelMaincontent);
        PanelMaincontent.setLayout(PanelMaincontentLayout);
        PanelMaincontentLayout.setHorizontalGroup(
            PanelMaincontentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelMaincontentLayout.createSequentialGroup()
                .addGroup(PanelMaincontentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelMaincontentLayout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addGroup(PanelMaincontentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(PanelMaincontentLayout.createSequentialGroup()
                                .addGroup(PanelMaincontentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelMaincontentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelMaincontentLayout.createSequentialGroup()
                                            .addGroup(PanelMaincontentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(lblNamapPegawai)
                                                .addComponent(lblJabatan, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGap(44, 44, 44))
                                        .addGroup(PanelMaincontentLayout.createSequentialGroup()
                                            .addGroup(PanelMaincontentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                .addComponent(lblJamKeluar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(lblTanggal, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(lblJamMasuk, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                            .addGap(48, 48, 48)))
                                    .addGroup(PanelMaincontentLayout.createSequentialGroup()
                                        .addComponent(lblFoto, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(28, 28, 28)))
                                .addGroup(PanelMaincontentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(btnUploadFoto)
                                    .addGroup(PanelMaincontentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(spinnerKeluar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
                                        .addComponent(spinnerMasuk, javax.swing.GroupLayout.Alignment.LEADING))
                                    .addComponent(txtJabatan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelMaincontentLayout.createSequentialGroup()
                                        .addComponent(dateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(MonthChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(YearChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(txtNama, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(chkAbsen))
                        .addGap(8, 8, 8))
                    .addGroup(PanelMaincontentLayout.createSequentialGroup()
                        .addGap(165, 165, 165)
                        .addComponent(lblWelcome)))
                .addContainerGap(173, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelMaincontentLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnSimpanAbsen)
                .addGap(256, 256, 256))
        );
        PanelMaincontentLayout.setVerticalGroup(
            PanelMaincontentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelMaincontentLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelMaincontentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(PanelMaincontentLayout.createSequentialGroup()
                        .addComponent(lblWelcome)
                        .addGap(109, 109, 109)
                        .addGroup(PanelMaincontentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelMaincontentLayout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(16, 16, 16)
                                .addComponent(lblNamapPegawai))
                            .addComponent(txtNama, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(14, 14, 14)
                        .addGroup(PanelMaincontentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblJabatan)
                            .addComponent(txtJabatan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PanelMaincontentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblTanggal)
                            .addComponent(dateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(MonthChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(YearChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelMaincontentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblJamMasuk)
                    .addComponent(spinnerMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelMaincontentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblJamKeluar)
                    .addComponent(spinnerKeluar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelMaincontentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnUploadFoto)
                    .addComponent(lblFoto, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkAbsen)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
                .addComponent(btnSimpanAbsen)
                .addGap(133, 133, 133))
        );

        PanelSide.setBackground(new java.awt.Color(0, 153, 153));

        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton1.setText("LOGOUT");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        IconPegawai.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ASSEST/Plakat Ma'had MTSN 3 2.png"))); // NOI18N

        javax.swing.GroupLayout PanelSideLayout = new javax.swing.GroupLayout(PanelSide);
        PanelSide.setLayout(PanelSideLayout);
        PanelSideLayout.setHorizontalGroup(
            PanelSideLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelSideLayout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(49, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelSideLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(IconPegawai, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        PanelSideLayout.setVerticalGroup(
            PanelSideLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelSideLayout.createSequentialGroup()
                .addGap(164, 164, 164)
                .addComponent(IconPegawai, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(130, 130, 130))
        );

        javax.swing.GroupLayout panelPegawaiLayout = new javax.swing.GroupLayout(panelPegawai);
        panelPegawai.setLayout(panelPegawaiLayout);
        panelPegawaiLayout.setHorizontalGroup(
            panelPegawaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPegawaiLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PanelSide, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PanelMaincontent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        panelPegawaiLayout.setVerticalGroup(
            panelPegawaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PanelMaincontent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(PanelSide, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        tabDashboardPegawai.addTab("PEGAWAI", panelPegawai);

        jPanel1.setBackground(new java.awt.Color(0, 153, 153));

        lblShiftTitle.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        lblShiftTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblShiftTitle.setText("DAFTAR SHIFT PEGAWAI");

        tblShiftPegawai.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Nama", "ID", "Jam Masuk", "Tanggal"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.sql.Time.class, java.util.Date.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblShiftPegawai);

        jButton2.setText("LOGOUT");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 61, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 720, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(63, 63, 63))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(163, 163, 163)
                .addComponent(lblShiftTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton2)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblShiftTitle)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(93, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelShiftPegawaiLayout = new javax.swing.GroupLayout(panelShiftPegawai);
        panelShiftPegawai.setLayout(panelShiftPegawaiLayout);
        panelShiftPegawaiLayout.setHorizontalGroup(
            panelShiftPegawaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelShiftPegawaiLayout.setVerticalGroup(
            panelShiftPegawaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        tabDashboardPegawai.addTab("SHIFT PEGAWAI", panelShiftPegawai);

        jPanel2.setBackground(new java.awt.Color(0, 102, 102));

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("SLIP GAJI PEGAWAI");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Periode", "Nama", "Total Kehadiran", "Total Gaji"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTable1);

        // Inisialisasi ComboBox dengan item default, akan diisi oleh loadSlipGajiPegawai()
        cbNamaSlip.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--Pilih Pegawai--" }));
        cbTanggalSlip.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--Pilih Tanggal--" }));
        cbHariSlip.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--Pilih Hari--" }));
        cbBulanSlip.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--Pilih Periode--" }));

        btnLihatSlip.setText("CARI");
        btnLihatSlip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLihatSlipActionPerformed(evt);
            }
        });

        btnLogout.setText("LOGOUT");
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 392, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(271, 271, 271))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(70, 70, 70)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(cbNamaSlip, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cbTanggalSlip, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(cbHariSlip, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cbBulanSlip, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(163, 163, 163)
                            .addComponent(btnLihatSlip, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 793, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnLogout))
                .addContainerGap(92, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 343, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbNamaSlip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbTanggalSlip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbHariSlip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbBulanSlip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLihatSlip))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 51, Short.MAX_VALUE)
                .addComponent(btnLogout)
                .addGap(29, 29, 29))
        );

        javax.swing.GroupLayout panelSlipGajiLayout = new javax.swing.GroupLayout(panelSlipGaji);
        panelSlipGaji.setLayout(panelSlipGajiLayout);
        panelSlipGajiLayout.setHorizontalGroup(
            panelSlipGajiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelSlipGajiLayout.setVerticalGroup(
            panelSlipGajiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSlipGajiLayout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 34, Short.MAX_VALUE))
        );

        tabDashboardPegawai.addTab("SLIP GAJI", panelSlipGaji);

        getContentPane().add(tabDashboardPegawai, java.awt.BorderLayout.PAGE_START);

        pack();
    }// </editor-fold>                        

    private void btnSimpanAbsenActionPerformed(java.awt.event.ActionEvent evt) {                                               
        Date tanggalPilih = dateChooser.getDate();
        Date jamMasukPilih = (Date) spinnerMasuk.getValue();
        Date jamKeluarPilih = null;
        // Cek apakah spinnerKeluar memiliki nilai yang valid dan tidak sama dengan nilai default SpinnerDateModel
        // Cara sederhana: jika spinnerKeluar telah diubah dari waktu defaultnya (misal tengah malam)
        // Atau, Anda bisa menambahkan checkbox "Sudah Pulang?" untuk mengaktifkan spinnerKeluar
        if (spinnerKeluar.getValue() != null ) { // Cukup cek null, atau bisa lebih kompleks
             Object jamKeluarValue = spinnerKeluar.getValue();
             if (jamKeluarValue instanceof Date) { // Pastikan itu adalah Date
                jamKeluarPilih = (Date) jamKeluarValue;
                // Hindari jam keluar yang sama dengan jam default spinner jika tidak diubah
                // Ini bisa rumit, tergantung bagaimana SpinnerDateModel diinisialisasi
             }
        }


        if (tanggalPilih == null || jamMasukPilih == null) {
            JOptionPane.showMessageDialog(this, "Tanggal dan Jam Masuk harus diisi!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!chkAbsen.isSelected()) {
            JOptionPane.showMessageDialog(this, "Harap centang checkbox 'ABSEN' untuk konfirmasi.", "Konfirmasi Absen", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (selectedFileBukti == null) {
            JOptionPane.showMessageDialog(this, "Harap unggah foto bukti kehadiran!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        java.sql.Date sqlTanggal = new java.sql.Date(tanggalPilih.getTime());
        java.sql.Time sqlJamMasuk = new java.sql.Time(jamMasukPilih.getTime());
        java.sql.Time sqlJamKeluar = (jamKeluarPilih != null) ? new java.sql.Time(jamKeluarPilih.getTime()) : null;
        String pathFoto = selectedFileBukti.getAbsolutePath(); // Pertimbangkan untuk menyalin file ke folder proyek

        Connection conn = null;
        PreparedStatement pstmtCheck = null;
        ResultSet rsCheck = null;
        PreparedStatement pstmtInsert = null;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Koneksi ke database gagal.", "Database Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String checkSql = "SELECT id_absensi FROM absensi WHERE id_pegawai = ? AND tanggal = ?";
            pstmtCheck = conn.prepareStatement(checkSql);
            pstmtCheck.setInt(1, this.pegawaiId);
            pstmtCheck.setDate(2, sqlTanggal);
            rsCheck = pstmtCheck.executeQuery();

            if (rsCheck.next()) {
                JOptionPane.showMessageDialog(this, "Anda sudah melakukan absensi untuk tanggal " + new SimpleDateFormat("dd-MM-yyyy").format(sqlTanggal), "Informasi", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            // Tutup resource pengecekan sebelum membuka yang baru
            if(rsCheck != null) rsCheck.close();
            if(pstmtCheck != null) pstmtCheck.close();


            String insertSql = "INSERT INTO absensi (id_pegawai, tanggal, jam_masuk, jam_keluar, foto_bukti_path, status_kehadiran) VALUES (?, ?, ?, ?, ?, 'hadir')";
            pstmtInsert = conn.prepareStatement(insertSql);
            pstmtInsert.setInt(1, this.pegawaiId);
            pstmtInsert.setDate(2, sqlTanggal);
            pstmtInsert.setTime(3, sqlJamMasuk);
            if (sqlJamKeluar != null) {
                pstmtInsert.setTime(4, sqlJamKeluar);
            } else {
                pstmtInsert.setNull(4, java.sql.Types.TIME);
            }
            pstmtInsert.setString(5, pathFoto);

            int affectedRows = pstmtInsert.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Absensi berhasil disimpan!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dateChooser.setDate(null);
                // Reset spinner, misal ke waktu sekarang atau null jika didukung
                // spinnerMasuk.setValue(new Date());
                // spinnerKeluar.setValue(new Date()); // Atau null jika bisa
                chkAbsen.setSelected(false);
                btnUploadFoto.setText("UPLOD FOTO");
                selectedFileBukti = null;
                loadSlipGajiPegawai();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan absensi.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saat menyimpan absensi: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try {
                if (rsCheck != null && !rsCheck.isClosed()) rsCheck.close();
                if (pstmtCheck != null && !pstmtCheck.isClosed()) pstmtCheck.close();
                if (pstmtInsert != null && !pstmtInsert.isClosed()) pstmtInsert.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }                                              

    private void chkAbsenActionPerformed(java.awt.event.ActionEvent evt) {                                         
        // Tidak ada logika khusus saat ini
    }                                        

    private void txtNamaActionPerformed(java.awt.event.ActionEvent evt) {                                        
        // Tidak ada aksi khusus
    }                                       

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        handleLogout(); // Tombol Logout di sidebar tab "PEGAWAI"
    }                                        

    private void btnLihatSlipActionPerformed(java.awt.event.ActionEvent evt) {                                             
        String selectedPeriode = (String) cbBulanSlip.getSelectedItem();
        if (selectedPeriode == null || "--Pilih Periode--".equals(selectedPeriode)) {
            JOptionPane.showMessageDialog(this, "Silakan pilih periode untuk melihat detail slip gaji.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
            // Jika ingin menampilkan semua data jika tidak ada filter:
            // loadSlipGajiPegawai();
            return;
        }

        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0); // Kosongkan tabel sebelum menampilkan hasil filter

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Koneksi ke database gagal.", "Database Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String[] parts = selectedPeriode.split("-");
            if (parts.length != 2) { // Validasi format MM-YYYY
                JOptionPane.showMessageDialog(this, "Format periode tidak valid: " + selectedPeriode, "Error Format", JOptionPane.ERROR_MESSAGE);
                loadSlipGajiPegawai(); // Muat ulang semua data
                return;
            }

            int bulan = Integer.parseInt(parts[0]);
            int tahun = Integer.parseInt(parts[1]);

            String sql = "SELECT YEAR(a.tanggal) as tahun_absensi, MONTH(a.tanggal) as bulan_absensi, COUNT(a.id_absensi) as total_hari_hadir, " +
                         "(COUNT(a.id_absensi) * 50000) as total_gaji " +
                         "FROM absensi a " +
                         "WHERE a.id_pegawai = ? AND a.status_kehadiran = 'hadir' AND MONTH(a.tanggal) = ? AND YEAR(a.tanggal) = ? " +
                         "GROUP BY YEAR(a.tanggal), MONTH(a.tanggal)";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, this.pegawaiId);
            pstmt.setInt(2, bulan);
            pstmt.setInt(3, tahun);
            rs = pstmt.executeQuery();

            if (rs.next()) { // Harusnya hanya ada satu baris hasil jika query benar
                int totalHariHadir = rs.getInt("total_hari_hadir");
                double totalGaji = rs.getDouble("total_gaji");
                model.addRow(new Object[]{
                    selectedPeriode, // Tampilkan periode yang dipilih dari ComboBox
                    this.namaPegawai,
                    totalHariHadir,
                    totalGaji
                });
            } else {
                JOptionPane.showMessageDialog(this, "Tidak ada data gaji ditemukan untuk periode " + selectedPeriode + ".", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                // Anda bisa membiarkan tabel kosong atau menambahkan baris "Tidak ada data".
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data slip gaji (filter): " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Format periode tidak valid (angka): " + selectedPeriode, "Error Format Angka", JOptionPane.ERROR_MESSAGE);
            loadSlipGajiPegawai(); // Muat ulang semua data
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }                                            

    // Event handler untuk tombol Upload Foto
    private void btnUploadFotoActionPerformed(java.awt.event.ActionEvent evt) {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Gambar (JPG, JPEG, PNG, GIF)", "jpg", "jpeg", "png", "gif");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
           selectedFileBukti = chooser.getSelectedFile();
           btnUploadFoto.setText(selectedFileBukti.getName()); // Tampilkan nama file di tombol
           // System.out.println("You chose to open this file: " + selectedFileBukti.getName());
        }
    }

    // Event handler untuk tombol Logout di tab SHIFT PEGAWAI (jButton2)
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
        handleLogout();
    }

    // Event handler untuk tombol Logout di tab SLIP GAJI (btnLogout)
     private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {
        handleLogout();
    }


    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PegawaiDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PegawaiDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PegawaiDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PegawaiDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                // Untuk testing langsung frame ini (tanpa login), Anda bisa hardcode ID dan nama
                // Contoh: new PegawaiDashboard(8, "Ustadz Testing").setVisible(true);
                // Jika tidak, panggil konstruktor default saja (akan kosong tanpa data pegawai)
                 new PegawaiDashboard().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify                     
    private javax.swing.JLabel IconPegawai;
    private com.toedter.calendar.JMonthChooser MonthChooser;
    private javax.swing.JPanel PanelMaincontent;
    private javax.swing.JPanel PanelSide;
    private com.toedter.calendar.JYearChooser YearChooser;
    private javax.swing.JButton btnLihatSlip;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnSimpanAbsen;
    private javax.swing.JButton btnUploadFoto;
    private javax.swing.JComboBox<String> cbBulanSlip;
    private javax.swing.JComboBox<String> cbHariSlip;
    private javax.swing.JComboBox<String> cbNamaSlip;
    private javax.swing.JComboBox<String> cbTanggalSlip;
    private javax.swing.JCheckBox chkAbsen;
    private com.toedter.calendar.JDateChooser dateChooser;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblFoto;
    private javax.swing.JLabel lblJabatan;
    private javax.swing.JLabel lblJamKeluar;
    private javax.swing.JLabel lblJamMasuk;
    private javax.swing.JLabel lblNamapPegawai;
    private javax.swing.JLabel lblShiftTitle;
    private javax.swing.JLabel lblTanggal;
    private javax.swing.JLabel lblWelcome;
    private javax.swing.JPanel panelPegawai;
    private javax.swing.JPanel panelShiftPegawai;
    private javax.swing.JPanel panelSlipGaji;
    private javax.swing.JSpinner spinnerKeluar;
    private javax.swing.JSpinner spinnerMasuk;
    private javax.swing.JTabbedPane tabDashboardPegawai;
    private javax.swing.JTable tblShiftPegawai;
    private java.awt.TextField txtJabatan;
    private java.awt.TextField txtNama;
    // End of variables declaration                   

}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabDashboardPegawai = new javax.swing.JTabbedPane();
        panelPegawai = new javax.swing.JPanel();
        PanelMaincontent = new javax.swing.JPanel();
        btnSimpanAbsen = new javax.swing.JButton();
        chkAbsen = new javax.swing.JCheckBox();
        spinnerKeluar = new javax.swing.JSpinner();
        lblJamKeluar = new javax.swing.JLabel();
        spinnerMasuk = new javax.swing.JSpinner();
        lblJamMasuk = new javax.swing.JLabel();
        lblTanggal = new javax.swing.JLabel();
        txtJabatan = new java.awt.TextField();
        lblJabatan = new javax.swing.JLabel();
        txtNama = new java.awt.TextField();
        lblNamapPegawai = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        lblWelcome = new javax.swing.JLabel();
        dateChooser = new com.toedter.calendar.JDateChooser();
        MonthChooser = new com.toedter.calendar.JMonthChooser();
        YearChooser = new com.toedter.calendar.JYearChooser();
        PanelSide = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        IconPegawai = new javax.swing.JLabel();
        panelShiftPegawai = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        lblShiftTitle = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblShiftPegawai = new javax.swing.JTable();
        jButton2 = new javax.swing.JButton();
        panelSlipGaji = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        cbNamaSlip = new javax.swing.JComboBox<>();
        cbTanggalSlip = new javax.swing.JComboBox<>();
        cbHariSlip = new javax.swing.JComboBox<>();
        cbBulanSlip = new javax.swing.JComboBox<>();
        btnLihatSlip = new javax.swing.JButton();
        btnLogout = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        panelPegawai.setBackground(new java.awt.Color(255, 255, 255));

        PanelMaincontent.setBackground(new java.awt.Color(0, 102, 102));

        btnSimpanAbsen.setText("OK");
        btnSimpanAbsen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanAbsenActionPerformed(evt);
            }
        });

        chkAbsen.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        chkAbsen.setForeground(new java.awt.Color(255, 255, 255));
        chkAbsen.setText("ABSEN");
        chkAbsen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkAbsenActionPerformed(evt);
            }
        });

        lblJamKeluar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblJamKeluar.setForeground(new java.awt.Color(255, 255, 255));
        lblJamKeluar.setText("Jam Keluar");

        lblJamMasuk.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblJamMasuk.setForeground(new java.awt.Color(255, 255, 255));
        lblJamMasuk.setText("Jam Masuk ");

        lblTanggal.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTanggal.setForeground(new java.awt.Color(255, 255, 255));
        lblTanggal.setText("Tanggal      ");

        txtJabatan.setText("textField1");

        lblJabatan.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        lblJabatan.setForeground(new java.awt.Color(255, 255, 255));
        lblJabatan.setText("Jabatan     ");

        txtNama.setText("textField1");
        txtNama.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNamaActionPerformed(evt);
            }
        });

        lblNamapPegawai.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        lblNamapPegawai.setForeground(new java.awt.Color(255, 255, 255));
        lblNamapPegawai.setText("Nama       ");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("ABSENSI HARI INI");

        lblWelcome.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        lblWelcome.setForeground(new java.awt.Color(255, 255, 255));
        lblWelcome.setText("SELAMAT DATANG");

        javax.swing.GroupLayout PanelMaincontentLayout = new javax.swing.GroupLayout(PanelMaincontent);
        PanelMaincontent.setLayout(PanelMaincontentLayout);
        PanelMaincontentLayout.setHorizontalGroup(
            PanelMaincontentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelMaincontentLayout.createSequentialGroup()
                .addGroup(PanelMaincontentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelMaincontentLayout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addGroup(PanelMaincontentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(PanelMaincontentLayout.createSequentialGroup()
                                .addGroup(PanelMaincontentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelMaincontentLayout.createSequentialGroup()
                                        .addGroup(PanelMaincontentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(lblNamapPegawai)
                                            .addComponent(lblJabatan, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(44, 44, 44))
                                    .addGroup(PanelMaincontentLayout.createSequentialGroup()
                                        .addGroup(PanelMaincontentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(lblJamKeluar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(lblTanggal, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(lblJamMasuk, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGap(48, 48, 48)))
                                .addGroup(PanelMaincontentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(chkAbsen)
                                    .addGroup(PanelMaincontentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(spinnerKeluar)
                                        .addComponent(spinnerMasuk)
                                        .addComponent(txtJabatan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelMaincontentLayout.createSequentialGroup()
                                            .addComponent(dateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(MonthChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(YearChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(txtNama, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
                    .addGroup(PanelMaincontentLayout.createSequentialGroup()
                        .addGap(165, 165, 165)
                        .addComponent(lblWelcome)))
                .addContainerGap(234, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelMaincontentLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnSimpanAbsen)
                .addGap(256, 256, 256))
        );
        PanelMaincontentLayout.setVerticalGroup(
            PanelMaincontentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelMaincontentLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelMaincontentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(PanelMaincontentLayout.createSequentialGroup()
                        .addComponent(lblWelcome)
                        .addGap(109, 109, 109)
                        .addGroup(PanelMaincontentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelMaincontentLayout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(16, 16, 16)
                                .addComponent(lblNamapPegawai))
                            .addComponent(txtNama, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(14, 14, 14)
                        .addGroup(PanelMaincontentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblJabatan)
                            .addComponent(txtJabatan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PanelMaincontentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblTanggal)
                            .addComponent(dateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(MonthChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(YearChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelMaincontentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblJamMasuk)
                    .addComponent(spinnerMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelMaincontentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblJamKeluar)
                    .addComponent(spinnerKeluar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkAbsen)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 85, Short.MAX_VALUE)
                .addComponent(btnSimpanAbsen)
                .addGap(133, 133, 133))
        );

        PanelSide.setBackground(new java.awt.Color(0, 153, 153));

        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton1.setText("LOGOUT");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        IconPegawai.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ASSEST/Plakat Ma'had MTSN 3 2.png"))); // NOI18N

        javax.swing.GroupLayout PanelSideLayout = new javax.swing.GroupLayout(PanelSide);
        PanelSide.setLayout(PanelSideLayout);
        PanelSideLayout.setHorizontalGroup(
            PanelSideLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelSideLayout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(49, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelSideLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(IconPegawai, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        PanelSideLayout.setVerticalGroup(
            PanelSideLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelSideLayout.createSequentialGroup()
                .addGap(164, 164, 164)
                .addComponent(IconPegawai, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(130, 130, 130))
        );

        javax.swing.GroupLayout panelPegawaiLayout = new javax.swing.GroupLayout(panelPegawai);
        panelPegawai.setLayout(panelPegawaiLayout);
        panelPegawaiLayout.setHorizontalGroup(
            panelPegawaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPegawaiLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PanelSide, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PanelMaincontent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        panelPegawaiLayout.setVerticalGroup(
            panelPegawaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PanelMaincontent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(PanelSide, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        tabDashboardPegawai.addTab("PEGAWAI", panelPegawai);

        jPanel1.setBackground(new java.awt.Color(0, 153, 153));

        lblShiftTitle.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        lblShiftTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblShiftTitle.setText("DAFTAR SHIFT PEGAWAI");

        tblShiftPegawai.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Nama", "ID", "Jam Masuk", "Tanggal"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblShiftPegawai);

        jButton2.setText("LOGOUT");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 61, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 720, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(63, 63, 63))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(163, 163, 163)
                .addComponent(lblShiftTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton2)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblShiftTitle)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(93, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelShiftPegawaiLayout = new javax.swing.GroupLayout(panelShiftPegawai);
        panelShiftPegawai.setLayout(panelShiftPegawaiLayout);
        panelShiftPegawaiLayout.setHorizontalGroup(
            panelShiftPegawaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelShiftPegawaiLayout.setVerticalGroup(
            panelShiftPegawaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        tabDashboardPegawai.addTab("SHIFT PEGAWAI", panelShiftPegawai);

        jPanel2.setBackground(new java.awt.Color(0, 102, 102));

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("SLIP GAJI PEGAWAI");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Tanggal", "nama", "Total Kehadiran", "Total Gaji"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTable1);

        cbNamaSlip.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cbTanggalSlip.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cbHariSlip.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cbBulanSlip.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnLihatSlip.setText("CARI");
        btnLihatSlip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLihatSlipActionPerformed(evt);
            }
        });

        btnLogout.setText("LOGOUT");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 392, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(271, 271, 271))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(70, 70, 70)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(cbNamaSlip, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cbTanggalSlip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(cbHariSlip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cbBulanSlip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(299, 299, 299)
                            .addComponent(btnLihatSlip, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 793, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnLogout))
                .addContainerGap(92, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 343, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbNamaSlip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbTanggalSlip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbHariSlip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbBulanSlip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLihatSlip))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 51, Short.MAX_VALUE)
                .addComponent(btnLogout)
                .addGap(29, 29, 29))
        );

        javax.swing.GroupLayout panelSlipGajiLayout = new javax.swing.GroupLayout(panelSlipGaji);
        panelSlipGaji.setLayout(panelSlipGajiLayout);
        panelSlipGajiLayout.setHorizontalGroup(
            panelSlipGajiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelSlipGajiLayout.setVerticalGroup(
            panelSlipGajiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSlipGajiLayout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 34, Short.MAX_VALUE))
        );

        tabDashboardPegawai.addTab("SLIP GAJI", panelSlipGaji);

        getContentPane().add(tabDashboardPegawai, java.awt.BorderLayout.PAGE_START);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSimpanAbsenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanAbsenActionPerformed

    

    }//GEN-LAST:event_btnSimpanAbsenActionPerformed

    private void chkAbsenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkAbsenActionPerformed

    }//GEN-LAST:event_chkAbsenActionPerformed

    private void txtNamaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNamaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNamaActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnLihatSlipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLihatSlipActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnLihatSlipActionPerformed

 

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel IconPegawai;
    private com.toedter.calendar.JMonthChooser MonthChooser;
    private javax.swing.JPanel PanelMaincontent;
    private javax.swing.JPanel PanelSide;
    private com.toedter.calendar.JYearChooser YearChooser;
    private javax.swing.JButton btnLihatSlip;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnSimpanAbsen;
    private javax.swing.JComboBox<String> cbBulanSlip;
    private javax.swing.JComboBox<String> cbHariSlip;
    private javax.swing.JComboBox<String> cbNamaSlip;
    private javax.swing.JComboBox<String> cbTanggalSlip;
    private javax.swing.JCheckBox chkAbsen;
    private com.toedter.calendar.JDateChooser dateChooser;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblJabatan;
    private javax.swing.JLabel lblJamKeluar;
    private javax.swing.JLabel lblJamMasuk;
    private javax.swing.JLabel lblNamapPegawai;
    private javax.swing.JLabel lblShiftTitle;
    private javax.swing.JLabel lblTanggal;
    private javax.swing.JLabel lblWelcome;
    private javax.swing.JPanel panelPegawai;
    private javax.swing.JPanel panelShiftPegawai;
    private javax.swing.JPanel panelSlipGaji;
    private javax.swing.JSpinner spinnerKeluar;
    private javax.swing.JSpinner spinnerMasuk;
    private javax.swing.JTabbedPane tabDashboardPegawai;
    private javax.swing.JTable tblShiftPegawai;
    private java.awt.TextField txtJabatan;
    private java.awt.TextField txtNama;
    // End of variables declaration//GEN-END:variables
}
