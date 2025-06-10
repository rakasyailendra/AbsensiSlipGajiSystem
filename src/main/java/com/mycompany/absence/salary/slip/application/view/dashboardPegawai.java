/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.absence.salary.slip.application.view;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import com.mycompany.absence.salary.slip.application.models.Absen;
import com.mycompany.absence.salary.slip.application.models.Pegawai;
import com.mycompany.absence.salary.slip.application.models.Shift;
import com.mycompany.absence.salary.slip.application.models.ShiftPegawai;
import com.mycompany.absence.salary.slip.application.repositories.AbsenRepository;
import com.mycompany.absence.salary.slip.application.repositories.JabatanPegawaiRepository;
import com.mycompany.absence.salary.slip.application.repositories.JabatanRepository;
import com.mycompany.absence.salary.slip.application.repositories.ShiftPegawaiRepository;
import com.mycompany.absence.salary.slip.application.repositories.ShiftRepository;
import com.mycompany.absence.salary.slip.application.utils.Response;
import com.mycompany.absence.salary.slip.application.utils.SessionManager;

/**
 *
 * @author User
 */
public class dashboardPegawai extends javax.swing.JFrame {
    int xx, xy;

    /**
     * Creates new form pegawaiAbsensi
     */
    public dashboardPegawai() {
        initComponents();
        initializeComponents();
    }

    Pegawai currentUser = SessionManager.getInstance().getCurrentUser();
    JabatanPegawaiRepository jabatanPegawaiRepository = new JabatanPegawaiRepository();
    ShiftPegawaiRepository shiftPegawaiRepository = new ShiftPegawaiRepository();
    JabatanRepository jabatanRepository = new JabatanRepository();
    ShiftRepository shiftRepository = new ShiftRepository();
    AbsenRepository absenRepository = new AbsenRepository();

    private void initializeComponents() {
        haloNamaPegawai_isiOtomatis.setText(currentUser.getNama());
        pegawaiInfo();

        Response<ArrayList<Absen>> absensResponse = absenRepository.findByIdPegawai(currentUser.getId());

        if (absensResponse.isSuccess() && absensResponse.getData() != null) {
            populateTableHistoriAbsensi(absensResponse.getData());
        } else {
            // Tambahkan debug informasi di sini
            String message = !absensResponse.isSuccess()
                    ? "Gagal: " + absensResponse.getMessage()
                    : "Data null atau kosong";
            JOptionPane.showMessageDialog(this, "Gagal memuat histori absensi.\n" + message);
            System.err.println("Histori Absen Error: " + message);
        }
    }

    private void pegawaiInfo() {
        namaPegawai_isiOtomatis.setText(currentUser.getNama());
        nipPegawai_isiOtomatis.setText(currentUser.getNip());

        // Jabatan
        var jabatanPegawaiResponse = jabatanPegawaiRepository.findByPegawaiId(currentUser.getId());
        if (jabatanPegawaiResponse.isSuccess() && !jabatanPegawaiResponse.getData().isEmpty()) {
            int idJabatan = jabatanPegawaiResponse.getData().get(0).getIdJabatan();
            var jabatanResponse = jabatanRepository.findById(idJabatan);
            if (jabatanResponse.isSuccess() && jabatanResponse.getData() != null) {
                jabatanPegawai_isiOtomatis.setText(jabatanResponse.getData().getNamaJabatan());
            } else {
                jabatanPegawai_isiOtomatis.setText("Belum diatur");
            }
        } else {
            jabatanPegawai_isiOtomatis.setText("Belum diatur");
        }

        // Shift
        var shiftPegawaiResponse = shiftPegawaiRepository.findByPegawaiId(currentUser.getId());
        if (shiftPegawaiResponse.isSuccess() && !shiftPegawaiResponse.getData().isEmpty()) {
            int idShift = shiftPegawaiResponse.getData().get(0).getIdShift();
            var shiftResponse = shiftRepository.findById(idShift);
            if (shiftResponse.isSuccess() && shiftResponse.getData() != null) {
                shiftPegawai_isiOtomatis.setText(shiftResponse.getData().getNamaShift());
            } else {
                shiftPegawai_isiOtomatis.setText("Belum diatur");
            }
        } else {
            shiftPegawai_isiOtomatis.setText("Belum diatur");
        }
    }

    private void populateTableHistoriAbsensi(ArrayList<Absen> absens) {
        String[] columnNames = { "Tanggal", "Nama Shift", "Jam Masuk", "Jam Keluar" };
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (Absen absen : absens) {
            String namaShift = "-";
            var shiftResponse = shiftRepository.findById(absen.getIdShift());
            if (shiftResponse.isSuccess() && shiftResponse.getData() != null) {
                namaShift = shiftResponse.getData().getNamaShift();
            }

            Object[] row = {
                    absen.getTanggal(),
                    namaShift,
                    absen.getJamMasuk() != null ? absen.getJamMasuk().toString() : "-",
                    absen.getJamKeluar() != null ? absen.getJamKeluar().toString() : "-"
            };
            model.addRow(row);
        }

        table_pegawaiHistori_isiOtomatis.setModel(model);
    }

    private void absenCheckIn() {
        int pegawaiId = currentUser.getId();

        // Cek shift
        var shiftPegawaiResponse = shiftPegawaiRepository.findByPegawaiId(pegawaiId);
        if (!shiftPegawaiResponse.isSuccess() || shiftPegawaiResponse.getData().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Shift pegawai tidak ditemukan.");
            return;
        }

        // Cek apakah sudah check-in hari ini
        LocalDate today = LocalDate.now();
        var absenHariIni = absenRepository.findByIdPegawai(pegawaiId);

        boolean sudahCheckIn = absenHariIni.isSuccess() && absenHariIni.getData().stream()
                .map(Absen::getTanggal)
                .anyMatch(tanggal -> tanggal.isEqual(today));

        if (sudahCheckIn) {
            JOptionPane.showMessageDialog(this, "Anda sudah Check In hari ini.");
            return;
        }

        // Pop up keterlambatan check-in
        LocalTime now = LocalTime.now();
        Integer shift = shiftPegawaiResponse.getData().get(0).getIdShift();
        Response<Shift> shiftResponse = shiftRepository.findById(shift);
        if (now.isAfter(shiftResponse.getData().getJamMasuk().plusMinutes(30))) {
            // Jika terlambat lebih dari 30 menit, tampilkan konfirmasi
            int response = JOptionPane.showConfirmDialog(this,
                    "Anda terlambat Check In. Apakah Anda ingin tetap Check In?",
                    "Konfirmasi Check In",
                    JOptionPane.YES_NO_OPTION);

            if (response != JOptionPane.YES_OPTION) {
                return; // Keluar jika tidak ingin Check In
            }
        }

        // Simpan data absen
        Absen absen = new Absen();
        absen.setIdPegawai(pegawaiId);
        absen.setIdShift(shiftPegawaiResponse.getData().get(0).getIdShift());
        absen.setTanggal(today);
        absen.setJamMasuk(LocalTime.now());

        Response<Absen> response = absenRepository.save(absen);
        if (response.isSuccess()) {
            JOptionPane.showMessageDialog(this, "Check In Berhasil");
            initializeComponents();
        } else {
            JOptionPane.showMessageDialog(this, "Check In Gagal: " + response.getMessage());
            System.err.println("Error: " + response.getMessage());
        }
    }

    private void absenCheckOut() {
        int pegawaiId = currentUser.getId();
        LocalDate today = LocalDate.now();

        // Ambil data absensi hari ini
        var absenHariIni = absenRepository.findByIdPegawai(pegawaiId);

        boolean sudahCheckIn = absenHariIni.isSuccess() && absenHariIni.getData().stream()
                .anyMatch(absen -> absen.getTanggal().isEqual(today) && absen.getJamMasuk() != null);

        if (!sudahCheckIn) {
            JOptionPane.showMessageDialog(this, "Anda belum Check In hari ini.");
            return;
        }

        boolean sudahCheckOut = absenHariIni.getData().stream()
                .anyMatch(absen -> absen.getTanggal().isEqual(today) && absen.getJamKeluar() != null);

        if (sudahCheckOut) {
            JOptionPane.showMessageDialog(this, "Anda sudah Check Out hari ini.");
            return;
        }

        // Ambil jam keluar shift
        Response<ArrayList<ShiftPegawai>> shiftPegawaiResponse = shiftPegawaiRepository.findByPegawaiId(pegawaiId);
        if (!shiftPegawaiResponse.isSuccess() || shiftPegawaiResponse.getData().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Data shift tidak ditemukan.");
            return;
        }

        ShiftPegawai shiftPegawai = shiftPegawaiResponse.getData().get(0);
        Response<Shift> shiftResponse = shiftRepository.findById(shiftPegawai.getIdShift());
        if (!shiftResponse.isSuccess() || shiftResponse.getData() == null) {
            JOptionPane.showMessageDialog(this, "Shift pegawai tidak valid.");
            return;
        }

        LocalTime now = LocalTime.now();
        LocalTime batasCheckOut = shiftResponse.getData().getJamKeluar();
        if (now.isBefore(batasCheckOut)) {
            JOptionPane.showMessageDialog(this,
                    "Anda belum bisa Check Out. Batas Check Out adalah pukul " + batasCheckOut + ".");
            // Jika belum waktunya check out, keluar dari method
            return;
        } else if (now.isAfter(batasCheckOut.plusMinutes(60))) {
            JOptionPane.showMessageDialog(this,
                    "Anda sudah melewati batas Check Out. Batas Check Out adalah pukul " + batasCheckOut + ".");

            // Tampilkan opsi untuk tetap Check Out
            int response = JOptionPane.showConfirmDialog(this,
                    "Anda sudah melewati batas Check Out. Apakah Anda ingin tetap Check Out?",
                    "Konfirmasi Check Out",
                    JOptionPane.YES_NO_OPTION);
            if (response != JOptionPane.YES_OPTION) {
                return; // Keluar jika tidak ingin Check Out
            }

            Absen absen = absenHariIni.getData().stream()
                    .filter(a -> a.getTanggal().isEqual(today) && a.getJamKeluar() == null)
                    .findFirst()
                    .orElse(null);
            if (absen == null) {
                JOptionPane.showMessageDialog(this, "Data absensi tidak ditemukan.");
                return;
            }
            absen.setJamKeluar(now);
            Response<Absen> updateResponse = absenRepository.update(absen);
            if (updateResponse.isSuccess()) {
                JOptionPane.showMessageDialog(this, "Check Out Berhasil");
                initializeComponents();
                return;
            } else {
                JOptionPane.showMessageDialog(this, "Check Out Gagal: " + updateResponse.getMessage());
                System.err.println("Error: " + updateResponse.getMessage());
                return;
            }
        } else {
            // Simpan jam keluar
            Absen absen = absenHariIni.getData().stream()
                    .filter(a -> a.getTanggal().isEqual(today) && a.getJamKeluar() == null)
                    .findFirst()
                    .orElse(null);

            if (absen != null) {
                absen.setJamKeluar(now);
                Response<Absen> response = absenRepository.update(absen);
                if (response.isSuccess()) {
                    JOptionPane.showMessageDialog(this, "Check Out Berhasil");
                    initializeComponents();
                } else {
                    JOptionPane.showMessageDialog(this, "Check Out Gagal: " + response.getMessage());
                    System.err.println("Error: " + response.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Data absensi tidak ditemukan.");
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        sideBar = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        MenuDashboardPegawai = new javax.swing.JLabel();
        MenuShiftkuPegawai = new javax.swing.JLabel();
        MenuLogout = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        MenuGajikuPegawai = new javax.swing.JLabel();
        headerPegawai = new javax.swing.JPanel();
        btmCancel = new javax.swing.JLabel();
        halo = new javax.swing.JLabel();
        haloNamaPegawai_isiOtomatis = new javax.swing.JLabel();
        panelUtama_dashboardPegawai = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        buttonCheckIn = new javax.swing.JToggleButton();
        ButtonCheckOut = new javax.swing.JToggleButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_pegawaiHistori_isiOtomatis = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        namaPegawai_isiOtomatis = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        nipPegawai_isiOtomatis = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jabatanPegawai_isiOtomatis = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        shiftPegawai_isiOtomatis = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel1.setPreferredSize(new java.awt.Dimension(1200, 640));

        sideBar.setBackground(new java.awt.Color(2, 84, 106));
        sideBar.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/logo pesantren (sidebar).png"))); // NOI18N

        MenuDashboardPegawai.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        MenuDashboardPegawai.setForeground(new java.awt.Color(255, 255, 255));
        MenuDashboardPegawai.setText("Dashboard");

        MenuShiftkuPegawai.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        MenuShiftkuPegawai.setForeground(new java.awt.Color(179, 201, 208));
        MenuShiftkuPegawai.setText("Shift Ku");
        MenuShiftkuPegawai.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                MenuShiftkuPegawaiMouseClicked(evt);
            }
        });

        MenuLogout.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        MenuLogout.setForeground(new java.awt.Color(179, 201, 208));
        MenuLogout.setText("Logout");
        MenuLogout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                MenuLogoutMouseClicked(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(179, 201, 208));
        jLabel2.setForeground(new java.awt.Color(179, 201, 208));
        jLabel2.setText("LAPORAN");

        MenuGajikuPegawai.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        MenuGajikuPegawai.setForeground(new java.awt.Color(179, 201, 208));
        MenuGajikuPegawai.setText("Gaji Ku");
        MenuGajikuPegawai.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                MenuGajikuPegawaiMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout sideBarLayout = new javax.swing.GroupLayout(sideBar);
        sideBar.setLayout(sideBarLayout);
        sideBarLayout.setHorizontalGroup(
                sideBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sideBarLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(sideBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, sideBarLayout
                                                .createSequentialGroup()
                                                .addGroup(sideBarLayout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(jLabel1)
                                                        .addGroup(sideBarLayout
                                                                .createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.LEADING)
                                                                .addComponent(jLabel2)
                                                                .addComponent(MenuDashboardPegawai,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 193,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, sideBarLayout
                                                .createSequentialGroup()
                                                .addGap(19, 19, 19)
                                                .addGroup(sideBarLayout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(MenuGajikuPegawai,
                                                                javax.swing.GroupLayout.Alignment.TRAILING,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(MenuShiftkuPegawai,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addGroup(sideBarLayout.createSequentialGroup()
                                                                .addComponent(MenuLogout,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 85,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(0, 0, Short.MAX_VALUE)))))
                                .addContainerGap()));
        sideBarLayout.setVerticalGroup(
                sideBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(sideBarLayout.createSequentialGroup()
                                .addGap(14, 14, 14)
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(MenuDashboardPegawai)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(MenuShiftkuPegawai)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(MenuGajikuPegawai)
                                .addGap(18, 18, 18)
                                .addComponent(MenuLogout)
                                .addContainerGap(344, Short.MAX_VALUE)));

        headerPegawai.setBackground(new java.awt.Color(255, 255, 255));
        headerPegawai.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        btmCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cancel.png"))); // NOI18N
        btmCancel.setPreferredSize(new java.awt.Dimension(50, 25));
        btmCancel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btmCancelMouseClicked(evt);
            }
        });

        halo.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        halo.setForeground(new java.awt.Color(2, 84, 106));
        halo.setText("Halo,");

        haloNamaPegawai_isiOtomatis.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        haloNamaPegawai_isiOtomatis.setForeground(new java.awt.Color(2, 84, 106));
        haloNamaPegawai_isiOtomatis.setText("Nama Pegawai");

        javax.swing.GroupLayout headerPegawaiLayout = new javax.swing.GroupLayout(headerPegawai);
        headerPegawai.setLayout(headerPegawaiLayout);
        headerPegawaiLayout.setHorizontalGroup(
                headerPegawaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, headerPegawaiLayout
                                .createSequentialGroup()
                                .addGap(29, 29, 29)
                                .addComponent(halo)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(haloNamaPegawai_isiOtomatis, javax.swing.GroupLayout.PREFERRED_SIZE, 437,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btmCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 25,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap()));
        headerPegawaiLayout.setVerticalGroup(
                headerPegawaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(headerPegawaiLayout.createSequentialGroup()
                                .addGroup(headerPegawaiLayout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(headerPegawaiLayout.createSequentialGroup()
                                                .addGap(21, 21, 21)
                                                .addGroup(headerPegawaiLayout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(halo)
                                                        .addComponent(haloNamaPegawai_isiOtomatis)))
                                        .addGroup(headerPegawaiLayout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(btmCancel, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(30, Short.MAX_VALUE)));

        panelUtama_dashboardPegawai.setPreferredSize(new java.awt.Dimension(900, 525));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(2, 84, 106));
        jLabel4.setText("Form Absensi - Check In / Check Out");

        buttonCheckIn.setBackground(new java.awt.Color(118, 158, 169));
        buttonCheckIn.setFont(new java.awt.Font("Segoe UI", 3, 36)); // NOI18N
        buttonCheckIn.setForeground(new java.awt.Color(255, 255, 255));
        buttonCheckIn.setText("Check In");
        buttonCheckIn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                buttonCheckInMouseClicked(evt);
            }
        });
        buttonCheckIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCheckInActionPerformed(evt);
            }
        });

        ButtonCheckOut.setBackground(new java.awt.Color(118, 158, 169));
        ButtonCheckOut.setFont(new java.awt.Font("Segoe UI", 3, 36)); // NOI18N
        ButtonCheckOut.setForeground(new java.awt.Color(255, 255, 255));
        ButtonCheckOut.setText("Check Out");
        ButtonCheckOut.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ButtonCheckOutMouseClicked(evt);
            }
        });
        ButtonCheckOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonCheckOutActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Nama :");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("NIP :");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Jabatan :");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Nama Shift :");

        table_pegawaiHistori_isiOtomatis
                .setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(118, 158, 169)));
        table_pegawaiHistori_isiOtomatis.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {
                        { null, null, null, null },
                        { null, null, null, null },
                        { null, null, null, null },
                        { null, null, null, null },
                        { null, null, null, null },
                        { null, null, null, null },
                        { null, null, null, null },
                        { null, null, null, null },
                        { null, null, null, null },
                        { null, null, null, null },
                        { null, null, null, null }
                },
                new String[] {
                        "Tanggal", "Nama Shift", "Jam Masuk", "Jam Keluar"
                }));
        jScrollPane1.setViewportView(table_pegawaiHistori_isiOtomatis);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null,
                new java.awt.Color(2, 84, 106), new java.awt.Color(2, 84, 106), null));
        jPanel2.setPreferredSize(new java.awt.Dimension(100, 40));

        namaPegawai_isiOtomatis.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        namaPegawai_isiOtomatis.setText("Nama Pegawai");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(namaPegawai_isiOtomatis, javax.swing.GroupLayout.DEFAULT_SIZE, 261,
                                Short.MAX_VALUE));
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(namaPegawai_isiOtomatis, javax.swing.GroupLayout.Alignment.TRAILING,
                                javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null,
                new java.awt.Color(2, 84, 106), new java.awt.Color(2, 84, 106), null));
        jPanel3.setPreferredSize(new java.awt.Dimension(100, 40));

        nipPegawai_isiOtomatis.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        nipPegawai_isiOtomatis.setText("NIP Pegawai");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(nipPegawai_isiOtomatis, javax.swing.GroupLayout.Alignment.TRAILING,
                                javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE));
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(nipPegawai_isiOtomatis, javax.swing.GroupLayout.Alignment.TRAILING,
                                javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null,
                new java.awt.Color(2, 84, 106), new java.awt.Color(2, 84, 106), null));
        jPanel4.setPreferredSize(new java.awt.Dimension(100, 40));

        jabatanPegawai_isiOtomatis.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jabatanPegawai_isiOtomatis.setText("Jabatan Pegawai");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jabatanPegawai_isiOtomatis, javax.swing.GroupLayout.DEFAULT_SIZE, 261,
                                Short.MAX_VALUE));
        jPanel4Layout.setVerticalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jabatanPegawai_isiOtomatis, javax.swing.GroupLayout.Alignment.TRAILING,
                                javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null,
                new java.awt.Color(2, 84, 106), new java.awt.Color(2, 84, 106), null));
        jPanel5.setPreferredSize(new java.awt.Dimension(100, 40));

        shiftPegawai_isiOtomatis.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        shiftPegawai_isiOtomatis.setText("Shift Pegawai");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
                jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(shiftPegawai_isiOtomatis, javax.swing.GroupLayout.DEFAULT_SIZE, 261,
                                Short.MAX_VALUE));
        jPanel5Layout.setVerticalGroup(
                jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(shiftPegawai_isiOtomatis, javax.swing.GroupLayout.Alignment.TRAILING,
                                javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE));

        javax.swing.GroupLayout panelUtama_dashboardPegawaiLayout = new javax.swing.GroupLayout(
                panelUtama_dashboardPegawai);
        panelUtama_dashboardPegawai.setLayout(panelUtama_dashboardPegawaiLayout);
        panelUtama_dashboardPegawaiLayout.setHorizontalGroup(
                panelUtama_dashboardPegawaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelUtama_dashboardPegawaiLayout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addGroup(panelUtama_dashboardPegawaiLayout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 424,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(panelUtama_dashboardPegawaiLayout.createSequentialGroup()
                                                .addGroup(panelUtama_dashboardPegawaiLayout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(panelUtama_dashboardPegawaiLayout
                                                                .createSequentialGroup()
                                                                .addGroup(panelUtama_dashboardPegawaiLayout
                                                                        .createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                                false)
                                                                        .addGroup(panelUtama_dashboardPegawaiLayout
                                                                                .createSequentialGroup()
                                                                                .addComponent(jLabel3,
                                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                        Short.MAX_VALUE)
                                                                                .addPreferredGap(
                                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                                                                        .addGroup(panelUtama_dashboardPegawaiLayout
                                                                                .createSequentialGroup()
                                                                                .addComponent(jLabel7,
                                                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                        92, Short.MAX_VALUE)
                                                                                .addGap(6, 6, 6)))
                                                                .addGroup(panelUtama_dashboardPegawaiLayout
                                                                        .createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(jPanel2,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                265,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(jPanel5,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                265,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                        .addGroup(panelUtama_dashboardPegawaiLayout
                                                                .createSequentialGroup()
                                                                .addGroup(panelUtama_dashboardPegawaiLayout
                                                                        .createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                                false)
                                                                        .addComponent(jLabel5,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE)
                                                                        .addComponent(jLabel6,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                92, Short.MAX_VALUE))
                                                                .addPreferredGap(
                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(panelUtama_dashboardPegawaiLayout
                                                                        .createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(jPanel3,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                265,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(jPanel4,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                265,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))))
                                                .addGap(46, 46, 46)
                                                .addComponent(buttonCheckIn, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        240, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(ButtonCheckOut, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        240, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(jScrollPane1))
                                .addContainerGap(30, Short.MAX_VALUE)));
        panelUtama_dashboardPegawaiLayout.setVerticalGroup(
                panelUtama_dashboardPegawaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelUtama_dashboardPegawaiLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel4)
                                .addGap(27, 27, 27)
                                .addGroup(panelUtama_dashboardPegawaiLayout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(panelUtama_dashboardPegawaiLayout.createSequentialGroup()
                                                .addGroup(panelUtama_dashboardPegawaiLayout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel3)
                                                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(25, 25, 25)
                                                .addGroup(panelUtama_dashboardPegawaiLayout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel5)
                                                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(25, 25, 25)
                                                .addGroup(panelUtama_dashboardPegawaiLayout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel6)
                                                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(24, 24, 24)
                                                .addGroup(panelUtama_dashboardPegawaiLayout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel7)
                                                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addComponent(buttonCheckIn, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(ButtonCheckOut, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 22, Short.MAX_VALUE)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 203,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(17, 17, 17)));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(sideBar, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(panelUtama_dashboardPegawai, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                962, Short.MAX_VALUE)
                                        .addComponent(headerPegawai, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))));
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(sideBar, javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(headerPegawai, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panelUtama_dashboardPegawai, javax.swing.GroupLayout.DEFAULT_SIZE, 544,
                                        Short.MAX_VALUE)));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1200, -1));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void MenuGajikuPegawaiMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_MenuGajikuPegawaiMouseClicked
        menuGajiku_Pegawai menuGaji = new menuGajiku_Pegawai();
        menuGaji.setVisible(true); // Menampilkan form tujuan
        this.dispose(); // Menutup form saat ini
    }// GEN-LAST:event_MenuGajikuPegawaiMouseClicked

    private void MenuShiftkuPegawaiMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_MenuShiftkuPegawaiMouseClicked
        menuShiftku_Pegawai menuShift = new menuShiftku_Pegawai();
        menuShift.setVisible(true); // Menampilkan form tujuan
        this.dispose(); // Menutup form saat ini
    }// GEN-LAST:event_MenuShiftkuPegawaiMouseClicked

    private void ButtonCheckOutMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_ButtonCheckOutMouseClicked
        absenCheckOut(); // Panggil method untuk melakukan check-out
    }// GEN-LAST:event_ButtonCheckOutMouseClicked

    private void buttonCheckInMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_buttonCheckInMouseClicked
        absenCheckIn();
    }// GEN-LAST:event_buttonCheckInMouseClicked

    private void btmCancelMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_btmCancelMouseClicked
        dispose();
    }// GEN-LAST:event_btmCancelMouseClicked

    private void formMousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_formMousePressed
        xx = evt.getX();
        xy = evt.getY();
    }// GEN-LAST:event_formMousePressed

    private void formMouseDragged(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_formMouseDragged
        int x = evt.getXOnScreen();
        int y = evt.getYOnScreen();
        this.setLocation(x - xx, y - xy);
    }// GEN-LAST:event_formMouseDragged

    private void buttonCheckInActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_buttonCheckInActionPerformed
        absenCheckIn(); // Panggil method untuk melakukan check-in
    }// GEN-LAST:event_buttonCheckInActionPerformed

    private void ButtonCheckOutActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_ButtonCheckOutActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_ButtonCheckOutActionPerformed

    private void MenuLogoutMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_MenuLogoutMouseClicked
        int confirm = javax.swing.JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin logout?",
                "Konfirmasi Logout", javax.swing.JOptionPane.YES_NO_OPTION);
        if (confirm == java.awt.event.KeyEvent.VK_Y || confirm == javax.swing.JOptionPane.YES_OPTION) {
            SessionManager.getInstance().logout();
            loginForm login = new loginForm(); // Membuat objek form tujuan
            login.setVisible(true); // Menampilkan form tujuan
            this.dispose(); // Menutup form saat ini
        }
    }// GEN-LAST:event_MenuLogoutMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        // <editor-fold defaultstate="collapsed" desc=" Look and feel setting code
        // (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the default
         * look and feel.
         * For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager
                    .getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(dashboardPegawai.class.getName()).log(
                    java.util.logging.Level.SEVERE,
                    null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(dashboardPegawai.class.getName()).log(
                    java.util.logging.Level.SEVERE,
                    null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(dashboardPegawai.class.getName()).log(
                    java.util.logging.Level.SEVERE,
                    null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(dashboardPegawai.class.getName()).log(
                    java.util.logging.Level.SEVERE,
                    null, ex);
        }
        // </editor-fold>
        // </editor-fold>
        // </editor-fold>
        // </editor-fold>
        // </editor-fold>
        // </editor-fold>
        // </editor-fold>
        // </editor-fold>
        // </editor-fold>
        // </editor-fold>
        // </editor-fold>
        // </editor-fold>
        // </editor-fold>
        // </editor-fold>
        // </editor-fold>
        // </editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new dashboardPegawai().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton ButtonCheckOut;
    private javax.swing.JLabel MenuDashboardPegawai;
    private javax.swing.JLabel MenuGajikuPegawai;
    private javax.swing.JLabel MenuLogout;
    private javax.swing.JLabel MenuShiftkuPegawai;
    private javax.swing.JLabel btmCancel;
    private javax.swing.JToggleButton buttonCheckIn;
    private javax.swing.JLabel halo;
    private javax.swing.JLabel haloNamaPegawai_isiOtomatis;
    private javax.swing.JPanel headerPegawai;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel jabatanPegawai_isiOtomatis;
    private javax.swing.JLabel namaPegawai_isiOtomatis;
    private javax.swing.JLabel nipPegawai_isiOtomatis;
    private javax.swing.JPanel panelUtama_dashboardPegawai;
    private javax.swing.JLabel shiftPegawai_isiOtomatis;
    private javax.swing.JPanel sideBar;
    private javax.swing.JTable table_pegawaiHistori_isiOtomatis;
    // End of variables declaration//GEN-END:variables
}
