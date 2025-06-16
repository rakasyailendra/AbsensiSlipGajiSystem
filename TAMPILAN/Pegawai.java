package GUI;

// Import yang dibutuhkan
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date; // Menggunakan java.util.Date untuk JDateChooser
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;


// Jika menggunakan DatabaseConnection dari package lain (disarankan)
// import pbokelompok2.util.DatabaseConnection; // Sesuaikan nama package jika berbeda

/**
 * Merepresentasikan frame utama untuk dashboard pegawai.
 * Frame ini berisi fungsionalitas untuk absensi, melihat shift, melihat slip gaji,
 * mengajukan cuti, dan mengelola profil.
 *
 * @author Kelompok 2 PBOUAD
 */
public class PegawaiFrame extends javax.swing.JFrame {

    //<editor-fold defaultstate="collapsed" desc="Variabel Instance dan Logger">
    /**
     * ID unik dari pegawai yang sedang login.
     */
    private int pegawaiId;

    /**
     * Nama lengkap dari pegawai yang sedang login.
     */
    private String namaPegawai;

    /**
     * File foto yang dipilih oleh pegawai untuk bukti absensi.
     */
    private File selectedFileBukti;

    /**
     * Logger untuk mencatat error atau informasi penting ke dalam file.
     */
    private static final Logger LOGGER = Logger.getLogger(PegawaiFrame.class.getName());

    /**
     * Gaji pokok per hari. Digunakan dalam kalkulasi slip gaji.
     */
    private static final double GAJI_HARIAN_POKOK = 75000;

    /**
     * Tarif lembur per jam.
     */
    private static final double TARIF_LEMBUR_PER_JAM = 15000;

    /**
     * Potongan keterlambatan per menit.
     */
    private static final double POTONGAN_TERLAMBAT_PER_MENIT = 250;
    
    /**
     * Batas jam masuk normal untuk shift pagi.
     */
    private static final Time BATAS_JAM_MASUK = Time.valueOf("08:00:00");
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Inisialisasi Logger Statis">
    static {
        try {
            // Konfigurasi logger untuk menyimpan log ke file "pegawai_app_log.txt"
            FileHandler fh = new FileHandler("pegawai_app_log.txt", true); // true for append mode
            fh.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fh);
            LOGGER.setLevel(Level.INFO);
        } catch (IOException | SecurityException e) {
            LOGGER.log(Level.SEVERE, "Gagal menginisialisasi logger file.", e);
        }
    }
    //</editor-fold>
    
    /**
     * Konstruktor default - biasanya digunakan oleh NetBeans GUI Builder.
     * Sebaiknya tidak digunakan untuk membuat instance secara langsung jika perlu data pegawai.
     */
    public PegawaiFrame() {
        initComponents();
        setLocationRelativeTo(null); // Tengahkan frame
        LOGGER.info("PegawaiFrame diinisialisasi tanpa data pegawai (mode desainer).");
    }

    /**
     * Konstruktor yang dimodifikasi untuk menerima info pegawai yang login.
     * Ini adalah konstruktor yang seharusnya digunakan saat aplikasi berjalan.
     *
     * @param pegawaiId ID unik pegawai yang login.
     * @param namaPegawai Nama lengkap pegawai yang login.
     */
    public PegawaiFrame(int pegawaiId, String namaPegawai) {
        this.pegawaiId = pegawaiId;
        this.namaPegawai = namaPegawai;
        
        initComponents(); // HARUS dipanggil pertama untuk menginisialisasi komponen GUI
        
        // Setup UI dan pemuatan data awal
        setupInitialUI();
        loadAllData();
        
        LOGGER.log(Level.INFO, "PegawaiFrame berhasil diinisialisasi untuk pegawai: {0} (ID: {1})", new Object[]{this.namaPegawai, this.pegawaiId});
    }

    /**
     * Mengatur properti awal komponen UI setelah inisialisasi.
     */
    private void setupInitialUI() {
        setLocationRelativeTo(null); // Tengahkan frame
        
        // Set tampilan selamat datang
        lblWelcome.setText("SELAMAT DATANG, " + this.namaPegawai.toUpperCase());
        txtNama.setText(this.namaPegawai);
        txtNama.setEditable(false); // Nama tidak bisa diubah di form absensi
        
        // Set ikon untuk tab
        // Asumsikan ikon ada di dalam package ASSEST
        try {
            tabDashboardPegawai.setIconAt(0, new ImageIcon(getClass().getResource("/ASSEST/icon_absen.png"))); // Ganti dengan path ikon yang sesuai
            tabDashboardPegawai.setIconAt(1, new ImageIcon(getClass().getResource("/ASSEST/icon_shift.png"))); // Ganti dengan path ikon yang sesuai
            tabDashboardPegawai.setIconAt(2, new ImageIcon(getClass().getResource("/ASSEST/icon_gaji.png")));   // Ganti dengan path ikon yang sesuai
            tabDashboardPegawai.setIconAt(3, new ImageIcon(getClass().getResource("/ASSEST/icon_cuti.png")));   // Ganti dengan path ikon yang sesuai
            tabDashboardPegawai.setIconAt(4, new ImageIcon(getClass().getResource("/ASSEST/icon_profil.png"))); // Ganti dengan path ikon yang sesuai
        } catch (Exception e) {
            LOGGER.warning("Gagal memuat ikon untuk tab. Pastikan file ikon ada di path yang benar.");
            // Tidak perlu menampilkan dialog error untuk hal ini, cukup log saja
        }
    }

    /**
     * Memanggil semua metode pemuatan data awal untuk mengisi semua tab.
     */
    private void loadAllData() {
        loadJabatanPegawai();
        loadShiftPegawai();
        loadSlipGajiPegawai();
        loadProfileData();      // Data untuk Tab Profil
        loadRiwayatCuti();      // Data untuk Tab Cuti
    }

    //<editor-fold defaultstate="collapsed" desc="Metode-metode Pemuatan Data (Database)">
    
    /**
     * Memuat jabatan pegawai dari database dan menampilkannya di form absensi.
     */
    private void loadJabatanPegawai() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = "SELECT jabatan FROM pengguna WHERE id_pengguna = ?";
        
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                LOGGER.severe("Koneksi DB gagal untuk loadJabatanPegawai.");
                return;
            }
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, this.pegawaiId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                txtJabatan.setText(rs.getString("jabatan"));
                txtJabatan.setEditable(false);
                txtProfilJabatan.setText(rs.getString("jabatan")); // Juga set di tab profil
                txtProfilJabatan.setEditable(false);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Gagal memuat jabatan pegawai.", e);
            JOptionPane.showMessageDialog(this, "Gagal memuat jabatan: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            DbUtils.closeQuietly(rs, pstmt, conn);
        }
    }

    /**
     * Memuat jadwal shift pegawai dari database dan menampilkannya pada tabel.
     */
    private void loadShiftPegawai() {
        DefaultTableModel model = (DefaultTableModel) tblShiftPegawai.getModel();
        model.setRowCount(0); // Kosongkan tabel
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = "SELECT tanggal, jam_mulai, jam_selesai, keterangan FROM shift WHERE id_pegawai = ? ORDER BY tanggal DESC, jam_mulai ASC";

        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                LOGGER.severe("Koneksi DB gagal untuk loadShiftPegawai.");
                return;
            }
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, this.pegawaiId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    this.namaPegawai,
                    this.pegawaiId,
                    rs.getTime("jam_mulai"),
                    rs.getTime("jam_selesai"), // Menambahkan jam selesai
                    rs.getDate("tanggal"),
                    rs.getString("keterangan") // Menambahkan keterangan
                });
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Gagal memuat data shift.", e);
            JOptionPane.showMessageDialog(this, "Gagal memuat data shift: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            DbUtils.closeQuietly(rs, pstmt, conn);
        }
    }

    /**
     * Memuat data slip gaji bulanan pegawai.
     * Kalkulasi gaji kini mencakup gaji pokok, lembur, dan potongan.
     */
    private void loadSlipGajiPegawai() {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        // Query yang lebih kompleks untuk menghitung detail gaji
        String sql = "SELECT " +
                     "  YEAR(tanggal) as tahun, " +
                     "  MONTH(tanggal) as bulan, " +
                     "  COUNT(id_absensi) as total_hari_hadir, " +
                     "  SUM(CASE WHEN jam_masuk > ? THEN 1 ELSE 0 END) as total_terlambat, " + // Hitung hari terlambat
                     "  SUM(TIMESTAMPDIFF(MINUTE, ?, jam_masuk)) as total_menit_terlambat, " + // Hitung total menit terlambat
                     "  SUM(TIMESTAMPDIFF(HOUR, jam_masuk, jam_keluar)) as total_jam_kerja " + // Hitung total jam kerja
                     "FROM absensi " +
                     "WHERE id_pegawai = ? AND status_kehadiran = 'hadir' " +
                     "GROUP BY YEAR(tanggal), MONTH(tanggal) " +
                     "ORDER BY tahun DESC, bulan DESC";

        try {
            conn = DatabaseConnection.getConnection();
             if (conn == null) {
                LOGGER.severe("Koneksi DB gagal untuk loadSlipGajiPegawai.");
                return;
            }
            pstmt = conn.prepareStatement(sql);
            pstmt.setTime(1, BATAS_JAM_MASUK);
            pstmt.setTime(2, BATAS_JAM_MASUK);
            pstmt.setInt(3, this.pegawaiId);
            rs = pstmt.executeQuery();
            
            cbBulanSlip.removeAllItems();
            cbBulanSlip.addItem("--Pilih Periode--");

            while (rs.next()) {
                int tahun = rs.getInt("tahun");
                int bulan = rs.getInt("bulan");
                int totalHariHadir = rs.getInt("total_hari_hadir");
                long totalJamKerja = rs.getLong("total_jam_kerja");
                long totalMenitTerlambat = rs.getLong("total_menit_terlambat");

                // Kalkulasi Gaji
                double gajiPokok = totalHariHadir * GAJI_HARIAN_POKOK;
                // Asumsi 8 jam kerja normal per hari
                double jamLembur = Math.max(0, totalJamKerja - (totalHariHadir * 8));
                double totalUangLembur = jamLembur * TARIF_LEMBUR_PER_JAM;
                // Hanya hitung potongan jika terlambat
                double totalPotongan = (totalMenitTerlambat > 0) ? totalMenitTerlambat * POTONGAN_TERLAMBAT_PER_MENIT : 0;
                
                double totalGaji = gajiPokok + totalUangLembur - totalPotongan;

                String periode = String.format("%02d-%d", bulan, tahun);
                
                model.addRow(new Object[]{
                    periode,
                    this.namaPegawai,
                    totalHariHadir,
                    String.format("Rp %,.2f", gajiPokok),
                    String.format("Rp %,.2f", totalUangLembur),
                    String.format("Rp %,.2f", totalPotongan),
                    String.format("Rp %,.2f", totalGaji)
                });
                
                // Isi ComboBox filter
                cbBulanSlip.addItem(periode);
            }
            if (cbBulanSlip.getItemCount() > 1) {
                cbBulanSlip.setSelectedIndex(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Gagal memuat data slip gaji.", e);
            JOptionPane.showMessageDialog(this, "Gagal memuat data slip gaji: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            DbUtils.closeQuietly(rs, pstmt, conn);
        }
    }

    /**
     * Memuat data profil pegawai (alamat & telepon) dari tabel 'profil_pegawai'.
     */
    private void loadProfileData() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        // Asumsi ada tabel 'profil_pegawai' yang terhubung dengan 'pengguna'
        String sql = "SELECT alamat, no_telepon FROM profil_pegawai WHERE id_pegawai = ?";
        
        try {
            conn = DatabaseConnection.getConnection();
             if (conn == null) {
                LOGGER.severe("Koneksi DB gagal untuk loadProfileData.");
                return;
            }
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, this.pegawaiId);
            rs = pstmt.executeQuery();
            
            // Set default text jika tidak ada data
            txtProfilAlamat.setText("Data belum diisi.");
            txtProfilTelepon.setText("Data belum diisi.");

            if (rs.next()) {
                txtProfilAlamat.setText(rs.getString("alamat"));
                txtProfilTelepon.setText(rs.getString("no_telepon"));
            }
            
            // Set data non-editable
            txtProfilNama.setText(this.namaPegawai);
            txtProfilNama.setEditable(false);
            txtProfilID.setText(String.valueOf(this.pegawaiId));
            txtProfilID.setEditable(false);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Gagal memuat data profil.", e);
            JOptionPane.showMessageDialog(this, "Gagal memuat data profil: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            DbUtils.closeQuietly(rs, pstmt, conn);
        }
    }
    
    /**
     * Memuat riwayat pengajuan cuti dari database untuk pegawai yang login.
     */
    private void loadRiwayatCuti() {
        DefaultTableModel model = (DefaultTableModel) tblRiwayatCuti.getModel();
        model.setRowCount(0);
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        // Asumsi ada tabel 'pengajuan_cuti'
        String sql = "SELECT id_cuti, tgl_mulai, tgl_selesai, jenis_cuti, keterangan, status, catatan_admin FROM pengajuan_cuti WHERE id_pegawai = ? ORDER BY id_cuti DESC";

        try {
            conn = DatabaseConnection.getConnection();
             if (conn == null) {
                LOGGER.severe("Koneksi DB gagal untuk loadRiwayatCuti.");
                return;
            }
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, this.pegawaiId);
            rs = pstmt.executeQuery();

            while(rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id_cuti"),
                    rs.getDate("tgl_mulai"),
                    rs.getDate("tgl_selesai"),
                    rs.getString("jenis_cuti"),
                    rs.getString("keterangan"),
                    rs.getString("status"),
                    rs.getString("catatan_admin")
                });
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Gagal memuat riwayat cuti.", e);
            JOptionPane.showMessageDialog(this, "Gagal memuat riwayat cuti: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            DbUtils.closeQuietly(rs, pstmt, conn);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Metode-metode Handler Aksi (Events)">
    
    /**
     * Menangani aksi klik pada tombol simpan absensi.
     * Melakukan validasi input sebelum menyimpan ke database.
     * @param evt Event klik tombol.
     */
    private void btnSimpanAbsenActionPerformed(java.awt.event.ActionEvent evt) {
        if (!validateAbsensiInput()) {
            return; // Hentikan proses jika validasi gagal
        }
        
        Date tanggalPilih = dateChooser.getDate();
        Date jamMasukPilih = (Date) spinnerMasuk.getValue();
        Date jamKeluarPilih = (Date) spinnerKeluar.getValue();
        
        java.sql.Date sqlTanggal = new java.sql.Date(tanggalPilih.getTime());
        java.sql.Time sqlJamMasuk = new java.sql.Time(jamMasukPilih.getTime());
        java.sql.Time sqlJamKeluar = (jamKeluarPilih != null) ? new java.sql.Time(jamKeluarPilih.getTime()) : null;
        String pathFoto = selectedFileBukti.getAbsolutePath();

        Connection conn = null;
        PreparedStatement pstmtCheck = null;
        ResultSet rsCheck = null;
        PreparedStatement pstmtInsert = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                 LOGGER.severe("Koneksi DB gagal untuk simpanAbsen.");
                 JOptionPane.showMessageDialog(this, "Koneksi database gagal.", "Fatal Error", JOptionPane.ERROR_MESSAGE);
                 return;
            }

            // 1. Cek apakah sudah ada absensi pada tanggal tersebut
            String checkSql = "SELECT id_absensi FROM absensi WHERE id_pegawai = ? AND tanggal = ?";
            pstmtCheck = conn.prepareStatement(checkSql);
            pstmtCheck.setInt(1, this.pegawaiId);
            pstmtCheck.setDate(2, sqlTanggal);
            rsCheck = pstmtCheck.executeQuery();
            if (rsCheck.next()) {
                JOptionPane.showMessageDialog(this, "Anda sudah melakukan absensi untuk tanggal " + new SimpleDateFormat("dd-MM-yyyy").format(sqlTanggal), "Informasi", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // 2. Jika belum ada, lakukan insert
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
                JOptionPane.showMessageDialog(this, "Absensi berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                resetAbsensiForm(); // Panggil method reset
                loadSlipGajiPegawai(); // Refresh data gaji setelah absensi baru
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan absensi.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saat menyimpan absensi.", e);
            JOptionPane.showMessageDialog(this, "Error saat menyimpan absensi: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            DbUtils.closeQuietly(rsCheck);
            DbUtils.closeQuietly(pstmtCheck);
            DbUtils.closeQuietly(pstmtInsert);
            DbUtils.closeQuietly(conn);
        }
    }
    
    /**
     * Menangani aksi klik pada tombol unggah foto.
     * Membuka JFileChooser dan menampilkan pratinjau gambar yang dipilih.
     * @param evt Event klik tombol.
     */
    private void btnUploadFotoActionPerformed(java.awt.event.ActionEvent evt) {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Gambar (JPG, PNG, GIF)", "jpg", "jpeg", "png", "gif");
        chooser.setFileFilter(filter);
        chooser.setDialogTitle("Pilih Foto Bukti Kehadiran");
        
        int returnValue = chooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            selectedFileBukti = chooser.getSelectedFile();
            btnUploadFoto.setText(selectedFileBukti.getName());
            
            // Tampilkan pratinjau gambar
            try {
                BufferedImage bimg = ImageIO.read(selectedFileBukti);
                if(bimg == null) {
                    throw new IOException("Format gambar tidak didukung.");
                }
                // Skalakan gambar agar pas di label pratinjau
                Image scaledImage = bimg.getScaledInstance(lblPreviewFoto.getWidth(), lblPreviewFoto.getHeight(), Image.SCALE_SMOOTH);
                lblPreviewFoto.setIcon(new ImageIcon(scaledImage));
                lblPreviewFoto.setText(""); // Hapus teks placeholder
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Gagal membaca atau menampilkan pratinjau gambar.", e);
                JOptionPane.showMessageDialog(this, "Gagal menampilkan pratinjau: " + e.getMessage(), "Error Gambar", JOptionPane.WARNING_MESSAGE);
                resetPratinjauFoto();
            }
        }
    }

    /**
     * Menangani aksi klik pada tombol "Lihat Detail" di tab Slip Gaji.
     * Menampilkan dialog baru dengan rincian perhitungan gaji.
     * @param evt Event klik tombol.
     */
    private void btnDetailSlipActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Silakan pilih satu baris periode gaji terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        TableModel model = jTable1.getModel();
        String periode = model.getValueAt(selectedRow, 0).toString();
        
        String detailText = getDetailGaji(periode);

        // Tampilkan detail dalam JDialog
        JDialog detailDialog = new JDialog(this, "Detail Gaji Periode: " + periode, true);
        JTextArea textArea = new JTextArea(detailText);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new java.awt.Font("Monospaced", 0, 14));
        detailDialog.add(new JScrollPane(textArea));
        detailDialog.setSize(450, 500);
        detailDialog.setLocationRelativeTo(this);
        detailDialog.setVisible(true);
    }
    
    /**
     * Menangani aksi klik pada tombol "Simpan Perubahan" di tab Profil.
     * Memperbarui data alamat dan telepon pegawai di database.
     * @param evt Event klik tombol.
     */
    private void btnSimpanProfilActionPerformed(java.awt.event.ActionEvent evt) {
        String alamat = txtProfilAlamat.getText();
        String telepon = txtProfilTelepon.getText();

        if (alamat.trim().isEmpty() || telepon.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Alamat dan No. Telepon tidak boleh kosong.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Connection conn = null;
        PreparedStatement pstmtUpdate = null;
        PreparedStatement pstmtInsert = null;
        PreparedStatement pstmtCheck = null;
        ResultSet rsCheck = null;
        
        // Coba UPDATE dulu, jika tidak ada baris yang terpengaruh (artinya data belum ada), lakukan INSERT.
        String checkSql = "SELECT id_pegawai FROM profil_pegawai WHERE id_pegawai = ?";
        String updateSql = "UPDATE profil_pegawai SET alamat = ?, no_telepon = ? WHERE id_pegawai = ?";
        String insertSql = "INSERT INTO profil_pegawai (id_pegawai, alamat, no_telepon) VALUES (?, ?, ?)";

        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                 LOGGER.severe("Koneksi DB gagal untuk update profil.");
                 return;
            }
            
            // Cek dulu apakah data sudah ada
            pstmtCheck = conn.prepareStatement(checkSql);
            pstmtCheck.setInt(1, this.pegawaiId);
            rsCheck = pstmtCheck.executeQuery();

            if (rsCheck.next()) { // Data sudah ada, lakukan UPDATE
                 pstmtUpdate = conn.prepareStatement(updateSql);
                 pstmtUpdate.setString(1, alamat);
                 pstmtUpdate.setString(2, telepon);
                 pstmtUpdate.setInt(3, this.pegawaiId);
                 pstmtUpdate.executeUpdate();
            } else { // Data belum ada, lakukan INSERT
                 pstmtInsert = conn.prepareStatement(insertSql);
                 pstmtInsert.setInt(1, this.pegawaiId);
                 pstmtInsert.setString(2, alamat);
                 pstmtInsert.setString(3, telepon);
                 pstmtInsert.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Profil berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Gagal memperbarui profil.", e);
            JOptionPane.showMessageDialog(this, "Gagal memperbarui profil: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            DbUtils.closeQuietly(rsCheck);
            DbUtils.closeQuietly(pstmtCheck, pstmtUpdate, pstmtInsert);
            DbUtils.closeQuietly(conn);
        }
    }

    /**
     * Menangani aksi klik pada tombol "Ajukan Cuti".
     * Mengambil data dari form, melakukan validasi, dan menyimpannya ke database.
     * @param evt Event klik tombol.
     */
    private void btnAjukanCutiActionPerformed(java.awt.event.ActionEvent evt) {
        // Validasi Input
        Date tglMulai = dateCutiMulai.getDate();
        Date tglSelesai = dateCutiSelesai.getDate();
        String jenisCuti = (String) cbJenisCuti.getSelectedItem();
        String keterangan = txtAreaKeteranganCuti.getText();

        if (tglMulai == null || tglSelesai == null || jenisCuti.equals("--Pilih Jenis--") || keterangan.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (tglMulai.after(tglSelesai)) {
            JOptionPane.showMessageDialog(this, "Tanggal mulai tidak boleh setelah tanggal selesai.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Proses ke Database
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "INSERT INTO pengajuan_cuti (id_pegawai, tgl_mulai, tgl_selesai, jenis_cuti, keterangan, status) VALUES (?, ?, ?, ?, ?, 'Menunggu')";

        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                LOGGER.severe("Koneksi DB gagal untuk pengajuan cuti.");
                return;
            }
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, this.pegawaiId);
            pstmt.setDate(2, new java.sql.Date(tglMulai.getTime()));
            pstmt.setDate(3, new java.sql.Date(tglSelesai.getTime()));
            pstmt.setString(4, jenisCuti);
            pstmt.setString(5, keterangan);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Pengajuan cuti berhasil dikirim dan sedang menunggu persetujuan.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                resetFormCuti();
                loadRiwayatCuti(); // Refresh tabel riwayat
            } else {
                 JOptionPane.showMessageDialog(this, "Gagal mengirim pengajuan cuti.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
             LOGGER.log(Level.SEVERE, "Gagal menyimpan pengajuan cuti.", e);
             JOptionPane.showMessageDialog(this, "Gagal menyimpan pengajuan cuti: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            DbUtils.closeQuietly(pstmt);
            DbUtils.closeQuietly(conn);
        }
    }
    
    /**
     * Menangani proses logout dari aplikasi.
     * Menampilkan dialog konfirmasi sebelum menutup frame ini dan membuka frame Login.
     */
    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Anda yakin ingin logout?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            LOGGER.info("Pegawai " + this.namaPegawai + " telah logout.");
            // Buka frame login baru
            new LoginFrame().setVisible(true);
            // Tutup frame ini
            this.dispose();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Metode-metode Utilitas dan Pembantu">
    
    /**
     * Melakukan validasi pada semua input di form absensi.
     * @return true jika semua input valid, false jika ada yang tidak valid.
     */
    private boolean validateAbsensiInput() {
        Date tanggalPilih = dateChooser.getDate();
        Date jamMasukPilih = (Date) spinnerMasuk.getValue();
        Date jamKeluarPilih = (Date) spinnerKeluar.getValue();
        
        if (tanggalPilih == null) {
            JOptionPane.showMessageDialog(this, "Tanggal harus diisi!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Validasi tanggal tidak di masa depan
        Date today = new Date();
        if (tanggalPilih.after(today)) {
             JOptionPane.showMessageDialog(this, "Anda tidak dapat melakukan absensi untuk tanggal di masa depan.", "Input Error", JOptionPane.ERROR_MESSAGE);
             return false;
        }

        if (jamMasukPilih == null) {
            JOptionPane.showMessageDialog(this, "Jam Masuk harus diisi!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Validasi jam keluar harus setelah jam masuk
        if (jamKeluarPilih != null && jamKeluarPilih.before(jamMasukPilih)) {
            JOptionPane.showMessageDialog(this, "Jam Keluar tidak boleh sebelum Jam Masuk.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!chkAbsen.isSelected()) {
            JOptionPane.showMessageDialog(this, "Harap centang checkbox 'ABSEN' untuk konfirmasi.", "Konfirmasi Absen", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (selectedFileBukti == null) {
            JOptionPane.showMessageDialog(this, "Harap unggah foto bukti kehadiran!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }

    /**
     * Mereset semua field pada form absensi ke kondisi awal.
     */
    private void resetAbsensiForm() {
        dateChooser.setDate(null);
        chkAbsen.setSelected(false);
        resetPratinjauFoto();
        // Reset spinner ke waktu sekarang
        spinnerMasuk.setValue(new Date());
        spinnerKeluar.setValue(new Date());
    }

    /**
     * Mereset komponen pratinjau foto ke keadaan default.
     */
    private void resetPratinjauFoto() {
        btnUploadFoto.setText("UPLOD FOTO");
        selectedFileBukti = null;
        lblPreviewFoto.setIcon(null);
        lblPreviewFoto.setText("Pratinjau Foto");
    }
    
    /**
     * Mereset semua field pada form pengajuan cuti.
     */
    private void resetFormCuti() {
        dateCutiMulai.setDate(null);
        dateCutiSelesai.setDate(null);
        cbJenisCuti.setSelectedIndex(0);
        txtAreaKeteranganCuti.setText("");
    }
    
    /**
     * Mengambil dan memformat detail perhitungan gaji untuk periode tertentu dari database.
     * @param periode Periode gaji dalam format "MM-YYYY".
     * @return String yang berisi rincian perhitungan gaji.
     */
    private String getDetailGaji(String periode) {
        String[] parts = periode.split("-");
        int bulan = Integer.parseInt(parts[0]);
        int tahun = Integer.parseInt(parts[1]);
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuilder detailBuilder = new StringBuilder();

        String sql = "SELECT tanggal, jam_masuk, jam_keluar FROM absensi " +
                     "WHERE id_pegawai = ? AND MONTH(tanggal) = ? AND YEAR(tanggal) = ? AND status_kehadiran = 'hadir' " +
                     "ORDER BY tanggal ASC";
        
        detailBuilder.append(String.format("DETAIL GAJI PERIODE: %s\n", periode));
        detailBuilder.append(String.format("Nama Pegawai: %s (ID: %d)\n", this.namaPegawai, this.pegawaiId));
        detailBuilder.append("==================================================\n\n");
        
        double totalGajiPokok = 0;
        double totalLembur = 0;
        double totalPotongan = 0;
        int hariHadir = 0;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, this.pegawaiId);
            pstmt.setInt(2, bulan);
            pstmt.setInt(3, tahun);
            rs = pstmt.executeQuery();

            detailBuilder.append(String.format("%-12s | %-10s | %-10s | %-15s\n", "Tanggal", "Jam Masuk", "Durasi", "Keterangan"));
            detailBuilder.append("--------------------------------------------------\n");
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

            while(rs.next()) {
                hariHadir++;
                Date tanggal = rs.getDate("tanggal");
                Time jamMasuk = rs.getTime("jam_masuk");
                Time jamKeluar = rs.getTime("jam_keluar");
                
                long durasiMillis = (jamKeluar != null) ? jamKeluar.getTime() - jamMasuk.getTime() : 0;
                long durasiJam = TimeUnit.MILLISECONDS.toHours(durasiMillis);
                
                String keterangan = "";
                
                // Hitung Potongan
                long menitTerlambat = 0;
                if (jamMasuk.after(BATAS_JAM_MASUK)) {
                    menitTerlambat = TimeUnit.MILLISECONDS.toMinutes(jamMasuk.getTime() - BATAS_JAM_MASUK.getTime());
                    double potonganHariIni = menitTerlambat * POTONGAN_TERLAMBAT_PER_MENIT;
                    totalPotongan += potonganHariIni;
                    keterangan += String.format("Terlambat %d mnt. ", menitTerlambat);
                }
                
                // Hitung Lembur
                long jamLembur = Math.max(0, durasiJam - 8);
                if (jamLembur > 0) {
                    double lemburHariIni = jamLembur * TARIF_LEMBUR_PER_JAM;
                    totalLembur += lemburHariIni;
                    keterangan += String.format("Lembur %d jam.", jamLembur);
                }

                if (keterangan.isEmpty()) keterangan = "Tepat Waktu";
                
                totalGajiPokok += GAJI_HARIAN_POKOK;
                
                detailBuilder.append(String.format("%-12s | %-10s | %-10s | %-15s\n", 
                        sdf.format(tanggal), 
                        jamMasuk.toString(), 
                        durasiJam + " jam", 
                        keterangan));
            }
            
            detailBuilder.append("\n==================================================\n");
            detailBuilder.append("RINGKASAN\n");
            detailBuilder.append("--------------------------------------------------\n");
            detailBuilder.append(String.format("%-25s: %d hari\n", "Total Hari Hadir", hariHadir));
            detailBuilder.append(String.format("%-25s: Rp %,.2f\n", "Total Gaji Pokok", totalGajiPokok));
            detailBuilder.append(String.format("%-25s: Rp %,.2f\n", "Total Uang Lembur", totalLembur));
            detailBuilder.append(String.format("%-25s: -Rp %,.2f\n", "Total Potongan Keterlambatan", totalPotongan));
            detailBuilder.append("--------------------------------------------------\n");
            detailBuilder.append(String.format("%-25s: Rp %,.2f\n", "GAJI BERSIH (TOTAL)", (totalGajiPokok + totalLembur - totalPotongan)));
            detailBuilder.append("==================================================\n");

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Gagal mengambil detail gaji.", e);
            return "Gagal memuat detail gaji: " + e.getMessage();
        } finally {
            DbUtils.closeQuietly(rs, pstmt, conn);
        }

        return detailBuilder.toString();
    }
    
    /**
     * Inner class statis untuk utilitas database.
     * Menyediakan metode untuk menutup sumber daya JDBC dengan aman.
     */
    private static class DbUtils {
        /**
         * Menutup satu atau lebih objek AutoCloseable (seperti Connection, Statement, ResultSet)
         * tanpa melempar exception. Exception akan di-log.
         * @param closeables Varargs dari objek yang akan ditutup.
         */
        public static void closeQuietly(AutoCloseable... closeables) {
            for (AutoCloseable c : closeables) {
                if (c != null) {
                    try {
                        c.close();
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Gagal menutup sumber daya JDBC.", e);
                    }
                }
            }
        }
    }
    //</editor-fold>
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     * * NOTE: Penambahan komponen baru (Tab Profil, Tab Cuti, dan komponen di dalamnya)
     * dilakukan secara manual pada kode di bawah ini.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
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
        btnResetAbsen = new javax.swing.JButton();
        panelPreview = new javax.swing.JPanel();
        lblPreviewFoto = new javax.swing.JLabel();
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
        cbBulanSlip = new javax.swing.JComboBox<>();
        btnLihatSlip = new javax.swing.JButton();
        btnLogout = new javax.swing.JButton();
        btnDetailSlip = new javax.swing.JButton();
        panelCuti = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        lblTitleCuti = new javax.swing.JLabel();
        panelFormCuti = new javax.swing.JPanel();
        lblTglMulai = new javax.swing.JLabel();
        dateCutiMulai = new com.toedter.calendar.JDateChooser();
        lblTglSelesai = new javax.swing.JLabel();
        dateCutiSelesai = new com.toedter.calendar.JDateChooser();
        lblJenisCuti = new javax.swing.JLabel();
        cbJenisCuti = new javax.swing.JComboBox<>();
        lblKeteranganCuti = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtAreaKeteranganCuti = new javax.swing.JTextArea();
        btnAjukanCuti = new javax.swing.JButton();
        btnResetCuti = new javax.swing.JButton();
        panelRiwayatCuti = new javax.swing.JPanel();
        lblRiwayatCuti = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblRiwayatCuti = new javax.swing.JTable();
        btnLogoutCuti = new javax.swing.JButton();
        panelProfil = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        lblTitleProfil = new javax.swing.JLabel();
        btnLogoutProfil = new javax.swing.JButton();
        panelDataProfil = new javax.swing.JPanel();
        lblProfilID = new javax.swing.JLabel();
        txtProfilID = new javax.swing.JTextField();
        lblProfilNama = new javax.swing.JLabel();
        txtProfilNama = new javax.swing.JTextField();
        lblProfilJabatan = new javax.swing.JLabel();
        txtProfilJabatan = new javax.swing.JTextField();
        lblProfilAlamat = new javax.swing.JLabel();
        txtProfilAlamat = new javax.swing.JTextField();
        lblProfilTelepon = new javax.swing.JLabel();
        txtProfilTelepon = new javax.swing.JTextField();
        btnSimpanProfil = new javax.swing.JButton();
        lblProfilInfo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Dashboard Pegawai");

        tabDashboardPegawai.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        panelPegawai.setBackground(new java.awt.Color(255, 255, 255));

        PanelMaincontent.setBackground(new java.awt.Color(0, 102, 102));

        btnSimpanAbsen.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnSimpanAbsen.setText("SIMPAN ABSENSI");
        btnSimpanAbsen.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSimpanAbsen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanAbsenActionPerformed(evt);
            }
        });

        chkAbsen.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        chkAbsen.setForeground(new java.awt.Color(255, 255, 255));
        chkAbsen.setText("Saya mengkonfirmasi kebenaran data absensi ini.");
        chkAbsen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkAbsenActionPerformed(evt);
            }
        });

        btnUploadFoto.setText("UPLOD FOTO");
        btnUploadFoto.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUploadFoto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadFotoActionPerformed(evt);
            }
        });

        lblFoto.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblFoto.setForeground(new java.awt.Color(255, 255, 255));
        lblFoto.setText("Bukti Foto");

        spinnerKeluar.setModel(new javax.swing.SpinnerDateModel());
        JSpinner.DateEditor deKeluar = new JSpinner.DateEditor(spinnerKeluar, "HH:mm:ss");
        spinnerKeluar.setEditor(deKeluar);

        lblJamKeluar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblJamKeluar.setForeground(new java.awt.Color(255, 255, 255));
        lblJamKeluar.setText("Jam Keluar");

        spinnerMasuk.setModel(new javax.swing.SpinnerDateModel());
        JSpinner.DateEditor deMasuk = new JSpinner.DateEditor(spinnerMasuk, "HH:mm:ss");
        spinnerMasuk.setEditor(deMasuk);

        lblJamMasuk.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblJamMasuk.setForeground(new java.awt.Color(255, 255, 255));
        lblJamMasuk.setText("Jam Masuk ");

        lblTanggal.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTanggal.setForeground(new java.awt.Color(255, 255, 255));
        lblTanggal.setText("Tanggal      ");

        txtJabatan.setEditable(false);

        lblJabatan.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        lblJabatan.setForeground(new java.awt.Color(255, 255, 255));
        lblJabatan.setText("Jabatan     ");

        txtNama.setEditable(false);
        txtNama.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNamaActionPerformed(evt);
            }
        });

        lblNamapPegawai.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        lblNamapPegawai.setForeground(new java.awt.Color(255, 255, 255));
        lblNamapPegawai.setText("Nama       ");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("FORMULIR ABSENSI HARIAN");

        lblWelcome.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblWelcome.setForeground(new java.awt.Color(255, 255, 255));
        lblWelcome.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblWelcome.setText("SELAMAT DATANG");

        dateChooser.setDateFormatString("yyyy-MM-dd");

        btnResetAbsen.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnResetAbsen.setText("RESET FORM");
        btnResetAbsen.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnResetAbsen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetAbsenActionPerformed(evt);
            }
        });

        panelPreview.setBackground(new java.awt.Color(204, 204, 204));
        panelPreview.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));

        lblPreviewFoto.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPreviewFoto.setText("Pratinjau Foto");

        javax.swing.GroupLayout panelPreviewLayout = new javax.swing.GroupLayout(panelPreview);
        panelPreview.setLayout(panelPreviewLayout);
        panelPreviewLayout.setHorizontalGroup(
            panelPreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblPreviewFoto, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
        );
        panelPreviewLayout.setVerticalGroup(
            panelPreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblPreviewFoto, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout PanelMaincontentLayout = new javax.swing.GroupLayout(PanelMaincontent);
        PanelMaincontent.setLayout(PanelMaincontentLayout);
        PanelMaincontentLayout.setHorizontalGroup(
            PanelMaincontentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblWelcome, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(PanelMaincontentLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(PanelMaincontentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelMaincontentLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelMaincontentLayout.createSequentialGroup()
                        .addGroup(PanelMaincontentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(chkAbsen, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(PanelMaincontentLayout.createSequentialGroup()
                                .addComponent(btnResetAbsen, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnSimpanAbsen, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, PanelMaincontentLayout.createSequentialGroup()
                                .addGroup(PanelMaincontentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(lblJamKeluar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblTanggal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblJamMasuk, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblJabatan, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblNamapPegawai)
                                    .addComponent(lblFoto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(44, 44, 44)
                                .addGroup(PanelMaincontentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtNama, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtJabatan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(spinnerMasuk)
                                    .addComponent(spinnerKeluar)
                                    .addGroup(PanelMaincontentLayout.createSequentialGroup()
                                        .addGroup(PanelMaincontentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(PanelMaincontentLayout.createSequentialGroup()
                                                .addComponent(dateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(MonthChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(YearChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(btnUploadFoto, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(0, 0, Short.MAX_VALUE)))
                                .addGap(40, 40, 40)
                                .addComponent(panelPreview, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(50, 50, 50))))
        );
        PanelMaincontentLayout.setVerticalGroup(
            PanelMaincontentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelMaincontentLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(lblWelcome)
                .addGap(30, 30, 30)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(PanelMaincontentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblNamapPegawai)
                    .addComponent(txtNama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelMaincontentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtJabatan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblJabatan))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelMaincontentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblTanggal)
                    .addComponent(dateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(MonthChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(YearChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelMaincontentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblJamMasuk)
                    .addComponent(spinnerMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelMaincontentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblJamKeluar)
                    .addComponent(spinnerKeluar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(PanelMaincontentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelMaincontentLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(PanelMaincontentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblFoto)
                            .addComponent(btnUploadFoto)))
                    .addGroup(PanelMaincontentLayout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(panelPreview, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(chkAbsen)
                .addGap(18, 18, 18)
                .addGroup(PanelMaincontentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSimpanAbsen, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnResetAbsen, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(50, 50, 50))
        );

        PanelSide.setBackground(new java.awt.Color(0, 153, 153));

        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton1.setText("LOGOUT");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        IconPegawai.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        IconPegawai.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ASSEST/Plakat Ma'had MTSN 3 2.png"))); // NOI18N

        javax.swing.GroupLayout PanelSideLayout = new javax.swing.GroupLayout(PanelSide);
        PanelSide.setLayout(PanelSideLayout);
        PanelSideLayout.setHorizontalGroup(
            PanelSideLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelSideLayout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(62, Short.MAX_VALUE))
            .addGroup(PanelSideLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(IconPegawai, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        PanelSideLayout.setVerticalGroup(
            PanelSideLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelSideLayout.createSequentialGroup()
                .addGap(120, 120, 120)
                .addComponent(IconPegawai, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 203, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40))
        );

        javax.swing.GroupLayout panelPegawaiLayout = new javax.swing.GroupLayout(panelPegawai);
        panelPegawai.setLayout(panelPegawaiLayout);
        panelPegawaiLayout.setHorizontalGroup(
            panelPegawaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPegawaiLayout.createSequentialGroup()
                .addComponent(PanelSide, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PanelMaincontent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelPegawaiLayout.setVerticalGroup(
            panelPegawaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PanelSide, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(PanelMaincontent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        tabDashboardPegawai.addTab("ABSENSI", panelPegawai);

        jPanel1.setBackground(new java.awt.Color(0, 153, 153));

        lblShiftTitle.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        lblShiftTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblShiftTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblShiftTitle.setText("DAFTAR SHIFT PEGAWAI");

        tblShiftPegawai.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Nama", "ID Pegawai", "Jam Masuk", "Jam Selesai", "Tanggal", "Keterangan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblShiftPegawai);

        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
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
            .addComponent(lblShiftTitle, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 955, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 895, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(lblShiftTitle)
                .addGap(30, 30, 30)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40))
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

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("SLIP GAJI PEGAWAI");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Periode (Bln-Thn)", "Nama", "Total Hadir", "Gaji Pokok", "Tunj./Lembur", "Potongan", "Total Gaji"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTable1);

        cbBulanSlip.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--Pilih Periode--" }));

        btnLihatSlip.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLihatSlip.setText("CARI PERIODE");
        btnLihatSlip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLihatSlipActionPerformed(evt);
            }
        });

        btnLogout.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLogout.setText("LOGOUT");
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });

        btnDetailSlip.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnDetailSlip.setText("LIHAT DETAIL");
        btnDetailSlip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDetailSlipActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 955, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 895, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(cbBulanSlip, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnLihatSlip, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnDetailSlip, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(jLabel2)
                .addGap(30, 30, 30)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbBulanSlip, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLihatSlip, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDetailSlip, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 56, Short.MAX_VALUE)
                .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40))
        );

        javax.swing.GroupLayout panelSlipGajiLayout = new javax.swing.GroupLayout(panelSlipGaji);
        panelSlipGaji.setLayout(panelSlipGajiLayout);
        panelSlipGajiLayout.setHorizontalGroup(
            panelSlipGajiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelSlipGajiLayout.setVerticalGroup(
            panelSlipGajiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        tabDashboardPegawai.addTab("SLIP GAJI", panelSlipGaji);

        jPanel3.setBackground(new java.awt.Color(0, 153, 153));

        lblTitleCuti.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        lblTitleCuti.setForeground(new java.awt.Color(255, 255, 255));
        lblTitleCuti.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitleCuti.setText("PENGAJUAN & RIWAYAT CUTI");

        panelFormCuti.setBackground(new java.awt.Color(0, 153, 153));
        panelFormCuti.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Formulir Pengajuan Cuti", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14), new java.awt.Color(255, 255, 255))); // NOI18N
        panelFormCuti.setForeground(new java.awt.Color(255, 255, 255));

        lblTglMulai.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTglMulai.setForeground(new java.awt.Color(255, 255, 255));
        lblTglMulai.setText("Tanggal Mulai");

        dateCutiMulai.setDateFormatString("yyyy-MM-dd");

        lblTglSelesai.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTglSelesai.setForeground(new java.awt.Color(255, 255, 255));
        lblTglSelesai.setText("Tanggal Selesai");

        dateCutiSelesai.setDateFormatString("yyyy-MM-dd");

        lblJenisCuti.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblJenisCuti.setForeground(new java.awt.Color(255, 255, 255));
        lblJenisCuti.setText("Jenis Cuti");

        cbJenisCuti.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--Pilih Jenis--", "Cuti Tahunan", "Cuti Sakit", "Cuti Alasan Penting", "Cuti Melahirkan" }));

        lblKeteranganCuti.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblKeteranganCuti.setForeground(new java.awt.Color(255, 255, 255));
        lblKeteranganCuti.setText("Keterangan / Alasan");

        txtAreaKeteranganCuti.setColumns(20);
        txtAreaKeteranganCuti.setRows(5);
        jScrollPane3.setViewportView(txtAreaKeteranganCuti);

        btnAjukanCuti.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnAjukanCuti.setText("AJUKAN CUTI");
        btnAjukanCuti.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAjukanCutiActionPerformed(evt);
            }
        });

        btnResetCuti.setText("RESET");
        btnResetCuti.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetCutiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelFormCutiLayout = new javax.swing.GroupLayout(panelFormCuti);
        panelFormCuti.setLayout(panelFormCutiLayout);
        panelFormCutiLayout.setHorizontalGroup(
            panelFormCutiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFormCutiLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFormCutiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(panelFormCutiLayout.createSequentialGroup()
                        .addComponent(lblKeteranganCuti)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFormCutiLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnResetCuti)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnAjukanCuti, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelFormCutiLayout.createSequentialGroup()
                        .addGroup(panelFormCutiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTglMulai)
                            .addComponent(lblJenisCuti))
                        .addGap(18, 18, 18)
                        .addGroup(panelFormCutiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(dateCutiMulai, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                            .addComponent(cbJenisCuti, 0, 1, Short.MAX_VALUE))
                        .addGap(28, 28, 28)
                        .addComponent(lblTglSelesai)
                        .addGap(18, 18, 18)
                        .addComponent(dateCutiSelesai, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelFormCutiLayout.setVerticalGroup(
            panelFormCutiLayout.createParallelGroup
