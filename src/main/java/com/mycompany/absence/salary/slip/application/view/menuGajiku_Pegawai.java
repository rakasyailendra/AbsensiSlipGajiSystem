/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.absence.salary.slip.application.view;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.table.DefaultTableModel;

import com.mycompany.absence.salary.slip.application.models.Absen;
import com.mycompany.absence.salary.slip.application.models.Jabatan;
import com.mycompany.absence.salary.slip.application.models.JabatanPegawai;
import com.mycompany.absence.salary.slip.application.models.Pegawai;
import com.mycompany.absence.salary.slip.application.repositories.AbsenRepository;
import com.mycompany.absence.salary.slip.application.repositories.JabatanPegawaiRepository;
import com.mycompany.absence.salary.slip.application.repositories.JabatanRepository;
import com.mycompany.absence.salary.slip.application.repositories.PegawaiRepository;
import com.mycompany.absence.salary.slip.application.repositories.ShiftPegawaiRepository;
import com.mycompany.absence.salary.slip.application.utils.Response;
import com.mycompany.absence.salary.slip.application.utils.SessionManager;

/**
 *
 * @author User
 */
public class menuGajiku_Pegawai extends javax.swing.JFrame {

    /**
     * Creates new form shiftkuPegawai1
     */
    public menuGajiku_Pegawai() {
        initComponents();
        initializeComponents();
    }

    AbsenRepository absenRepository = new AbsenRepository();
    ShiftPegawaiRepository shiftPegawaiRepository = new ShiftPegawaiRepository();
    JabatanPegawaiRepository jabatanPegawaiRepository = new JabatanPegawaiRepository();
    JabatanRepository jabatanRepository = new JabatanRepository();

    Pegawai currentUser = SessionManager.getInstance().getCurrentUser();

    // Tambahkan di atas (misal, sebagai class field atau constant)
    private static final String[] MONTHS_ID = {
            "JANUARI", "FEBRUARI", "MARET", "APRIL", "MEI", "JUNI",
            "JULI", "AGUSTUS", "SEPTEMBER", "OKTOBER", "NOVEMBER", "DESEMBER"
    };

    // Mapping dari bulan Indonesia ke enum Month Java (untuk logic filter)
    private String indoToEnglishMonth(String indoMonth) {
        switch (indoMonth.toUpperCase()) {
            case "JANUARI":
                return "JANUARY";
            case "FEBRUARI":
                return "FEBRUARY";
            case "MARET":
                return "MARCH";
            case "APRIL":
                return "APRIL";
            case "MEI":
                return "MAY";
            case "JUNI":
                return "JUNE";
            case "JULI":
                return "JULY";
            case "AGUSTUS":
                return "AUGUST";
            case "SEPTEMBER":
                return "SEPTEMBER";
            case "OKTOBER":
                return "OCTOBER";
            case "NOVEMBER":
                return "NOVEMBER";
            case "DESEMBER":
                return "DECEMBER";
            default:
                return null;
        }
    }

    private void initializeComponents() {
        haloNamaPegawai_isiOtomatis.setText(currentUser.getNama());
        populateComboBox();
        populateTableGaji();

        // Update table on year/month selection change
        jCombo_Tahun.addActionListener(evt -> populateTableGaji());
        jCombo_Bulan.addActionListener(evt -> populateTableGaji());
    }

    private void populateComboBox() {
        Response<ArrayList<Absen>> absenResponse = absenRepository.findByIdPegawai(currentUser.getId());
        Set<Integer> availableYears = new LinkedHashSet<>();

        // Ambil tahun dari data absen
        if (absenResponse != null && absenResponse.getData() != null) {
            for (Absen absen : absenResponse.getData()) {
                int year = absen.getTanggal().getYear();
                availableYears.add(year);
            }
        }

        List<Integer> sortedYears = new ArrayList<>(availableYears);
        Collections.sort(sortedYears);

        jCombo_Tahun.removeAllItems();
        for (Integer year : sortedYears) {
            jCombo_Tahun.addItem(String.valueOf(year));
        }
        jCombo_Tahun.setSelectedIndex(-1);

        // --- ComboBox bulan: selalu pakai urutan bulan Indonesia ---
        jCombo_Bulan.removeAllItems();
        for (String bulan : MONTHS_ID) {
            jCombo_Bulan.addItem(bulan);
        }
        jCombo_Bulan.setSelectedIndex(-1);
    }

    private void populateTableGaji() {
        String[] columnNames = { "Nama Pegawai", "Jabatan", "Gaji Pokok", "Jumlah Masuk", "Total Gaji" };
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table_shiftkuPegawai_isiOtomatis.setModel(model);

        // Fetch jabatanPegawai
        Response<ArrayList<JabatanPegawai>> jabatanPegawaiResponse = jabatanPegawaiRepository
                .findByPegawaiId(currentUser.getId());
        JabatanPegawai jabatanPegawai = (jabatanPegawaiResponse != null && jabatanPegawaiResponse.getData() != null
                && !jabatanPegawaiResponse.getData().isEmpty())
                        ? jabatanPegawaiResponse.getData().get(0)
                        : null;

        // Fetch jabatan
        Jabatan jabatanData = null;
        if (jabatanPegawai != null) {
            Response<Jabatan> jabatanResponse = jabatanRepository.findById(jabatanPegawai.getIdJabatan());
            jabatanData = (jabatanResponse != null && jabatanResponse.getData() != null) ? jabatanResponse.getData()
                    : null;
        }

        // Attendance - filter by selected month and year
        Response<ArrayList<Absen>> absenResponse = absenRepository.findByIdPegawai(currentUser.getId());
        int jumlahMasuk = 0;
        if (absenResponse != null && absenResponse.getData() != null) {
            // Get selected year and month
            String selectedYearStr = (String) jCombo_Tahun.getSelectedItem();
            String selectedMonthStr = (String) jCombo_Bulan.getSelectedItem();
            Integer selectedYear = null;
            Month selectedMonth = null;

            if (selectedYearStr != null && !selectedYearStr.isEmpty()) {
                selectedYear = Integer.parseInt(selectedYearStr);
            }
            if (selectedMonthStr != null && !selectedMonthStr.isEmpty()) {
                String monthEnglish = indoToEnglishMonth(selectedMonthStr);
                if (monthEnglish != null) {
                    selectedMonth = Month.valueOf(monthEnglish);
                }
            }

            for (Absen absen : absenResponse.getData()) {
                LocalDate tanggal = absen.getTanggal();
                boolean yearMatch = (selectedYear == null) || (tanggal.getYear() == selectedYear);
                boolean monthMatch = (selectedMonth == null) || (tanggal.getMonth() == selectedMonth);

                if (yearMatch && monthMatch) {
                    jumlahMasuk++;
                }
            }
        }

        // Salary calculation
        double gajiPokok = (jabatanData != null && jabatanData.getGajiPokok() != null) ? jabatanData.getGajiPokok() : 0;
        double totalGaji = jumlahMasuk * gajiPokok;

        model.addRow(new Object[] {
                currentUser.getNama(),
                jabatanData != null ? jabatanData.getNamaJabatan() : "",
                gajiPokok,
                jumlahMasuk,
                totalGaji
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
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
        panelUtama_shiftkuPegawai = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_shiftkuPegawai_isiOtomatis = new javax.swing.JTable();
        jCombo_Tahun = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        btn_cetakGajiku = new javax.swing.JButton();
        jCombo_Bulan = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel1.setPreferredSize(new java.awt.Dimension(1200, 640));

        sideBar.setBackground(new java.awt.Color(2, 84, 106));
        sideBar.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/logo pesantren (sidebar).png"))); // NOI18N

        MenuDashboardPegawai.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        MenuDashboardPegawai.setForeground(new java.awt.Color(179, 201, 208));
        MenuDashboardPegawai.setText("Dashboard");
        MenuDashboardPegawai.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                MenuDashboardPegawaiMouseClicked(evt);
            }
        });

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

        MenuGajikuPegawai.setBackground(new java.awt.Color(255, 255, 255));
        MenuGajikuPegawai.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        MenuGajikuPegawai.setForeground(new java.awt.Color(255, 255, 255));
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
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, sideBarLayout.createSequentialGroup()
                        .addGroup(sideBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1)
                            .addGroup(sideBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel2)
                                .addComponent(MenuDashboardPegawai, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, sideBarLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(sideBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(MenuGajikuPegawai, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(MenuShiftkuPegawai, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(sideBarLayout.createSequentialGroup()
                                .addComponent(MenuLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
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
                .addContainerGap(344, Short.MAX_VALUE))
        );

        headerPegawai.setBackground(new java.awt.Color(255, 255, 255));
        headerPegawai.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        btmCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Cancel.png"))); // NOI18N
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, headerPegawaiLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(halo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(haloNamaPegawai_isiOtomatis, javax.swing.GroupLayout.PREFERRED_SIZE, 437, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btmCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        headerPegawaiLayout.setVerticalGroup(
            headerPegawaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPegawaiLayout.createSequentialGroup()
                .addGroup(headerPegawaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(headerPegawaiLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(headerPegawaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(halo)
                            .addComponent(haloNamaPegawai_isiOtomatis)))
                    .addGroup(headerPegawaiLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btmCancel)))
                .addContainerGap(30, Short.MAX_VALUE))
        );

        panelUtama_shiftkuPegawai.setPreferredSize(new java.awt.Dimension(900, 525));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(2, 84, 106));
        jLabel4.setText("Slip Gaji Saya");

        table_shiftkuPegawai_isiOtomatis.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(118, 158, 169)));
        table_shiftkuPegawai_isiOtomatis.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Nama Pegawai", "Jabatan", "Gaji Pokok", "Jumlah Masuk", "Total Gaji"
            }
        ));
        jScrollPane1.setViewportView(table_shiftkuPegawai_isiOtomatis);

        jLabel8.setText("Bulan :");

        jLabel9.setText("Tahun :");

        btn_cetakGajiku.setText("Cetak");
        btn_cetakGajiku.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cetakGajikuActionPerformed(evt);
            }
        });

        jCombo_Bulan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCombo_BulanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelUtama_shiftkuPegawaiLayout = new javax.swing.GroupLayout(panelUtama_shiftkuPegawai);
        panelUtama_shiftkuPegawai.setLayout(panelUtama_shiftkuPegawaiLayout);
        panelUtama_shiftkuPegawaiLayout.setHorizontalGroup(
            panelUtama_shiftkuPegawaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelUtama_shiftkuPegawaiLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(panelUtama_shiftkuPegawaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 424, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelUtama_shiftkuPegawaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(panelUtama_shiftkuPegawaiLayout.createSequentialGroup()
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jCombo_Tahun, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(30, 30, 30)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jCombo_Bulan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btn_cetakGajiku))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 901, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(31, Short.MAX_VALUE))
        );
        panelUtama_shiftkuPegawaiLayout.setVerticalGroup(
            panelUtama_shiftkuPegawaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelUtama_shiftkuPegawaiLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel4)
                .addGap(36, 36, 36)
                .addGroup(panelUtama_shiftkuPegawaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jCombo_Bulan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(jCombo_Tahun, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_cetakGajiku))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(sideBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelUtama_shiftkuPegawai, javax.swing.GroupLayout.DEFAULT_SIZE, 962, Short.MAX_VALUE)
                    .addComponent(headerPegawai, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sideBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(headerPegawai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelUtama_shiftkuPegawai, javax.swing.GroupLayout.DEFAULT_SIZE, 541, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1200, -1));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btn_cetakGajikuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cetakGajikuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_cetakGajikuActionPerformed

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

    private void btmCancelMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_btmCancelMouseClicked
        dispose();
    }// GEN-LAST:event_btmCancelMouseClicked

    private void jCombo_BulanActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jCombo_BulanActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_jCombo_BulanActionPerformed

    private void MenuGajikuPegawaiMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_MenuGajikuPegawaiMouseClicked
        menuShiftku_Pegawai shiftkuPegawai = new menuShiftku_Pegawai();
        shiftkuPegawai.setVisible(true);
        this.dispose();
    }// GEN-LAST:event_MenuGajikuPegawaiMouseClicked

    private void MenuShiftkuPegawaiMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_MenuShiftkuPegawaiMouseClicked
        // TODO add your handling code here:
        menuShiftku_Pegawai shiftkuPegawai = new menuShiftku_Pegawai();
        shiftkuPegawai.setVisible(true);
        this.dispose();
    }// GEN-LAST:event_MenuShiftkuPegawaiMouseClicked

    private void MenuDashboardPegawaiMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_MenuDashboardPegawaiMouseClicked
        dashboardPegawai dashboardPegawai = new dashboardPegawai();
        dashboardPegawai.setVisible(true);
        this.dispose();
    }// GEN-LAST:event_MenuDashboardPegawaiMouseClicked

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
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(menuGajiku_Pegawai.class.getName()).log(java.util.logging.Level.SEVERE,
                    null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(menuGajiku_Pegawai.class.getName()).log(java.util.logging.Level.SEVERE,
                    null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(menuGajiku_Pegawai.class.getName()).log(java.util.logging.Level.SEVERE,
                    null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(menuGajiku_Pegawai.class.getName()).log(java.util.logging.Level.SEVERE,
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

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new menuGajiku_Pegawai().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel MenuDashboardPegawai;
    private javax.swing.JLabel MenuGajikuPegawai;
    private javax.swing.JLabel MenuLogout;
    private javax.swing.JLabel MenuShiftkuPegawai;
    private javax.swing.JLabel btmCancel;
    private javax.swing.JButton btn_cetakGajiku;
    private javax.swing.JLabel halo;
    private javax.swing.JLabel haloNamaPegawai_isiOtomatis;
    private javax.swing.JPanel headerPegawai;
    private javax.swing.JComboBox<String> jCombo_Bulan;
    private javax.swing.JComboBox<String> jCombo_Tahun;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel panelUtama_shiftkuPegawai;
    private javax.swing.JPanel sideBar;
    private javax.swing.JTable table_shiftkuPegawai_isiOtomatis;
    // End of variables declaration//GEN-END:variables
}
