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

import javax.swing.JOptionPane;
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

    // Daftar nama bulan dalam Bahasa Indonesia
    private static final String[] MONTHS_ID = {
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    };

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

    private void populateComboBoxBulanBerdasarkanTahun(int tahunTerpilih) {
        jCombo_Bulan.removeAllItems();

        // Ambil semua absen untuk pegawai ini
        Response<ArrayList<Absen>> absenResponse = absenRepository.findByIdPegawai(currentUser.getId());
        Set<Month> bulanUnik = new LinkedHashSet<>();

        if (absenResponse.isSuccess()) {
            for (Absen absen : absenResponse.getData()) {
                LocalDate tanggal = absen.getTanggal();
                if (tanggal.getYear() == tahunTerpilih) {
                    bulanUnik.add(tanggal.getMonth());
                }
            }
        }

        // Sort dan masukkan ke combo (pakai format Indonesia)
        List<Month> bulanSorted = new ArrayList<>(bulanUnik);
        Collections.sort(bulanSorted);

        for (Month bulan : bulanSorted) {
            String namaBulanIndonesia = getBulanIndonesia(bulan);
            jCombo_Bulan.addItem(namaBulanIndonesia);
        }

        jCombo_Bulan.setSelectedIndex(-1); // Tidak otomatis pilih, biarkan user pilih
    }

    // Mapping dari bulan Indonesia ke enum Month Java (untuk logic filter)
    private String getBulanIndonesia(Month month) {
        switch (month) {
            case JANUARY: return "Januari";
            case FEBRUARY: return "Februari";
            case MARCH: return "Maret";
            case APRIL: return "April";
            case MAY: return "Mei";
            case JUNE: return "Juni";
            case JULY: return "Juli";
            case AUGUST: return "Agustus";
            case SEPTEMBER: return "September";
            case OCTOBER: return "Oktober";
            case NOVEMBER: return "November";
            case DECEMBER: return "Desember";
            default: return "";
        }
    }

    private void initializeComponents() {
        haloNamaPegawai_isiOtomatis.setText(currentUser.getNama());
        populateComboBox();
        populateTableGaji();

        // Set style for jamLabel
        jamLabel2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 22));
        jamLabel2.setForeground(new java.awt.Color(0, 123, 255));
        jamLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        // Add shadow effect (simple workaround)
        jamLabel2.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createEmptyBorder(2, 10, 2, 10),
            javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(2, 84, 106))
        ));

        // Timer for updating time with date
        javax.swing.Timer timer = new javax.swing.Timer(1000, e -> {
            java.time.LocalDate today = java.time.LocalDate.now();
            java.time.LocalTime now = java.time.LocalTime.now();
            java.time.format.DateTimeFormatter dateFmt = java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy");
            java.time.format.DateTimeFormatter timeFmt = java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss");
            jamLabel2.setText("<html><div style='text-align:right;'>" +
            "<span style='font-size:10px;color:#02546A;'>" + today.format(dateFmt) + "</span><br>" +
            "<span style='font-size:14px;color:#007bff;'>" + now.format(timeFmt) + "</span></div></html>");
        });
        timer.setInitialDelay(0);
        timer.start();

        // Update table on year/month selection change
        jCombo_Tahun.addActionListener(evt -> populateTableGaji());
        jCombo_Bulan.addActionListener(evt -> populateTableGaji());

        jCombo_Tahun.addActionListener(evt -> {
        String selectedYearStr = (String) jCombo_Tahun.getSelectedItem();
            if (selectedYearStr != null && !selectedYearStr.contains("Pilih")) {
                int tahun = Integer.parseInt(selectedYearStr);
                populateComboBoxBulanBerdasarkanTahun(tahun); //isi bulan berdasarkan tahun
                populateTableGaji(); // Tampilkan data langsung jika perlu
            }
        });

        jCombo_Bulan.addActionListener(evt -> populateTableGaji());
    }

    private void populateComboBox() {
        Response<ArrayList<Absen>> absenResponse = absenRepository.findByIdPegawai(currentUser.getId());
        Set<Integer> availableYears = new LinkedHashSet<>();

        if (absenResponse != null && absenResponse.getData() != null) {
            for (Absen absen : absenResponse.getData()) {
                int year = absen.getTanggal().getYear();
                availableYears.add(year);
            }
        }

        List<Integer> sortedYears = new ArrayList<>(availableYears);
        Collections.sort(sortedYears);

        jCombo_Tahun.removeAllItems();
        jCombo_Tahun.addItem("-- Pilih Tahun --");
        for (Integer year : sortedYears) {
            jCombo_Tahun.addItem(String.valueOf(year));
        }
        jCombo_Tahun.setSelectedIndex(0);

        jCombo_Bulan.removeAllItems();
        jCombo_Bulan.addItem("-- Pilih Bulan --");
        for (String bulan : MONTHS_ID) {
            jCombo_Bulan.addItem(bulan);
        }
        jCombo_Bulan.setSelectedIndex(0);
    }

    private void populateTableGaji() {
        String selectedYearStr = (String) jCombo_Tahun.getSelectedItem();
        String selectedMonthStr = (String) jCombo_Bulan.getSelectedItem();

        // Validasi tahun wajib
        if (selectedYearStr == null || selectedYearStr.isEmpty() || selectedYearStr.contains("Pilih")) {
            DefaultTableModel emptyModel = new DefaultTableModel(new String[] {
                "Nama Pegawai", "Jabatan", "Gaji Pokok", "Jumlah Masuk", "Total Gaji"
            }, 0);
            emptyModel.addRow(new Object[] { "Silakan pilih tahun dan bulan terlebih dahulu", "", "", "", "" });
            table_shiftkuPegawai_isiOtomatis.setModel(emptyModel);
            return;
        }

        Integer selectedYear = Integer.parseInt(selectedYearStr);
        Month selectedMonth = null;

        // Validasi bulan wajib juga (tapi hanya jika combo sudah diaktifkan)
        if (selectedMonthStr == null || selectedMonthStr.isEmpty() || selectedMonthStr.contains("Pilih")) {
            // Jika belum pilih bulan, munculkan tetap tapi dari data 1 tahun penuh
            // Catatan: kalau kamu ingin strict bulan wajib, bisa tambahkan return disini
        } else {
            String monthEnglish = null;
            switch (selectedMonthStr) {
                case "Januari": monthEnglish = "JANUARY"; break;
                case "Februari": monthEnglish = "FEBRUARY"; break;
                case "Maret": monthEnglish = "MARCH"; break;
                case "April": monthEnglish = "APRIL"; break;
                case "Mei": monthEnglish = "MAY"; break;
                case "Juni": monthEnglish = "JUNE"; break;
                case "Juli": monthEnglish = "JULY"; break;
                case "Agustus": monthEnglish = "AUGUST"; break;
                case "September": monthEnglish = "SEPTEMBER"; break;
                case "Oktober": monthEnglish = "OCTOBER"; break;
                case "November": monthEnglish = "NOVEMBER"; break;
                case "Desember": monthEnglish = "DECEMBER"; break;
            }
            if (monthEnglish != null) {
                selectedMonth = Month.valueOf(monthEnglish);
            }
        }

        // Ambil jabatan pegawai
        Response<ArrayList<JabatanPegawai>> jabatanPegawaiResponse = jabatanPegawaiRepository.findByPegawaiId(currentUser.getId());
        JabatanPegawai jabatanPegawai = (jabatanPegawaiResponse != null && jabatanPegawaiResponse.getData() != null
                && !jabatanPegawaiResponse.getData().isEmpty())
                ? jabatanPegawaiResponse.getData().get(0)
                : null;

        // Ambil data jabatan
        Jabatan jabatanData = null;
        if (jabatanPegawai != null) {
            Response<Jabatan> jabatanResponse = jabatanRepository.findById(jabatanPegawai.getIdJabatan());
            if (jabatanResponse != null && jabatanResponse.isSuccess()) {
                jabatanData = jabatanResponse.getData();
            }
        }

        if (jabatanData == null) {
            JOptionPane.showMessageDialog(this, "Anda belum memiliki jabatan, tidak dapat menampilkan slip gaji.");
            return;
        }

        // Hitung absen
        int jumlahMasuk = 0;
        Response<ArrayList<Absen>> absenResponse = absenRepository.findByIdPegawai(currentUser.getId());
        if (absenResponse.isSuccess()) {
            for (Absen absen : absenResponse.getData()) {
                LocalDate tanggal = absen.getTanggal();
                boolean yearMatch = tanggal.getYear() == selectedYear;
                boolean monthMatch = selectedMonth == null || tanggal.getMonth() == selectedMonth;

                if (yearMatch && monthMatch) {
                    jumlahMasuk++;
                }
            }
        }

        double gajiPokok = jabatanData.getGajiPokok() != null ? jabatanData.getGajiPokok() : 0;
        double totalGaji = jumlahMasuk * gajiPokok;

        DefaultTableModel model = new DefaultTableModel(new String[] {
            "Nama Pegawai", "Jabatan", "Gaji Pokok", "Jumlah Masuk", "Total Gaji"
        }, 0);

        model.addRow(new Object[] {
            currentUser.getNama(),
            jabatanData.getNamaJabatan(),
            gajiPokok,
            jumlahMasuk,
            totalGaji
        });

        table_shiftkuPegawai_isiOtomatis.setModel(model);
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
        jamLabel2 = new javax.swing.JLabel();
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

        jamLabel2.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jamLabel2.setText("Tanggal, jam");
        jamLabel2.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                jamLabel2AncestorAdded(evt);
            }
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });

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
                .addComponent(jamLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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
                        .addComponent(btmCancel))
                    .addComponent(jamLabel2))
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
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addGap(36, 36, 36)
                .addGroup(panelUtama_shiftkuPegawaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jCombo_Bulan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(jCombo_Tahun, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_cetakGajiku))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
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
        javax.swing.JTable tableAnda = table_shiftkuPegawai_isiOtomatis;
        javax.swing.table.TableModel modelAnda = tableAnda.getModel();

        String namaPegawai = haloNamaPegawai_isiOtomatis.getText().trim();
        String namaFile = "Slip_Gaji_" + namaPegawai.replace(" ", "_") + ".pdf";

        javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
        fileChooser.setDialogTitle("Simpan Slip Gaji sebagai PDF");
        fileChooser.setSelectedFile(new java.io.File(namaFile));
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == javax.swing.JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            try {
                com.itextpdf.text.Document document = new com.itextpdf.text.Document();
                com.itextpdf.text.pdf.PdfWriter.getInstance(document, new java.io.FileOutputStream(fileToSave));
                document.open();

                // Logo & Header
                try {
                    java.net.URL logoUrl = getClass().getResource("/img/logoMahadBesar.png");
                    if (logoUrl != null) {
                        com.itextpdf.text.Image logo = com.itextpdf.text.Image.getInstance(logoUrl);
                        logo.scaleAbsolute(60, 60);
                        logo.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                        document.add(logo);
                    }
                } catch (Exception e) {}

                com.itextpdf.text.Font fontTitle = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 20, com.itextpdf.text.Font.BOLD);
                com.itextpdf.text.Paragraph title = new com.itextpdf.text.Paragraph("Pondok Pesantren Baitul Hikmah", fontTitle);
                title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                document.add(title);

                com.itextpdf.text.Font fontSub = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12);
                com.itextpdf.text.Paragraph alamat = new com.itextpdf.text.Paragraph("JL. Medokan Asri Tengah, Medokan Ayu, Kec. Rungkut, Kota Surabaya\nHP: 089616194482", fontSub);
                alamat.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                document.add(alamat);

                document.add(new com.itextpdf.text.Chunk(new com.itextpdf.text.pdf.draw.LineSeparator()));

                // Info Pegawai
                com.itextpdf.text.Font fontNormal = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12);
                com.itextpdf.text.pdf.PdfPTable infoTable = new com.itextpdf.text.pdf.PdfPTable(new float[]{1, 3, 3});
                infoTable.setWidthPercentage(100);
                infoTable.getDefaultCell().setBorder(com.itextpdf.text.Rectangle.NO_BORDER);

                String periode = "";
                if (jCombo_Bulan.getSelectedItem() != null && jCombo_Tahun.getSelectedItem() != null) {
                    periode = jCombo_Bulan.getSelectedItem().toString() + " " + jCombo_Tahun.getSelectedItem().toString();
                }

                // Baris 1
                infoTable.addCell(getCell("Nama", fontNormal, com.itextpdf.text.Element.ALIGN_LEFT));
                infoTable.addCell(getCell(": " + currentUser.getNama(), fontNormal, com.itextpdf.text.Element.ALIGN_LEFT));
                infoTable.addCell(getCell(new java.text.SimpleDateFormat("EEEE, dd MMMM yyyy").format(new java.util.Date()), fontNormal, com.itextpdf.text.Element.ALIGN_RIGHT));

                // Baris 2
                infoTable.addCell(getCell("NIP", fontNormal, com.itextpdf.text.Element.ALIGN_LEFT));
                infoTable.addCell(getCell(": " + currentUser.getNip(), fontNormal, com.itextpdf.text.Element.ALIGN_LEFT));
                infoTable.addCell(getCell("", fontNormal, com.itextpdf.text.Element.ALIGN_RIGHT));

                // Baris 3
                infoTable.addCell(getCell("Jabatan", fontNormal, com.itextpdf.text.Element.ALIGN_LEFT));
                infoTable.addCell(getCell(": " + modelAnda.getValueAt(0, 1), fontNormal, com.itextpdf.text.Element.ALIGN_LEFT));
                infoTable.addCell(getCell("", fontNormal, com.itextpdf.text.Element.ALIGN_RIGHT));

                // Baris 4
                infoTable.addCell(getCell("Periode", fontNormal, com.itextpdf.text.Element.ALIGN_LEFT));
                infoTable.addCell(getCell(": " + periode, fontNormal, com.itextpdf.text.Element.ALIGN_LEFT));
                infoTable.addCell(getCell("", fontNormal, com.itextpdf.text.Element.ALIGN_RIGHT));

                document.add(infoTable);

                document.add(new com.itextpdf.text.Paragraph(" ")); // spasi

                // Judul Tabel Slip Gaji
                com.itextpdf.text.Font fontTableTitle = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 13, com.itextpdf.text.Font.BOLD);
                com.itextpdf.text.Paragraph tableTitle = new com.itextpdf.text.Paragraph("Slip Gaji Anda", fontTableTitle);
                tableTitle.setAlignment(com.itextpdf.text.Element.ALIGN_LEFT);
                document.add(tableTitle);

                // Tabel Gaji
                addTableToPdf(document, modelAnda);

                document.close();

                javax.swing.JOptionPane.showMessageDialog(this, "Slip Gaji berhasil dicetak:\n" + fileToSave.getAbsolutePath());

                if (java.awt.Desktop.isDesktopSupported()) {
                    java.awt.Desktop.getDesktop().open(fileToSave);
                }

            } catch (Exception ex) {
                javax.swing.JOptionPane.showMessageDialog(this, "Gagal mencetak Slip Gaji: " + ex.getMessage());
            }
        }
    }//GEN-LAST:event_btn_cetakGajikuActionPerformed

    private void jamLabel2AncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_jamLabel2AncestorAdded
        // TODO add your handling code here:
    }//GEN-LAST:event_jamLabel2AncestorAdded

    // Helper untuk cell info (tanpa border)
    private com.itextpdf.text.pdf.PdfPCell getCell(String text, com.itextpdf.text.Font font, int alignment) {
        com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(text, font));
        cell.setPadding(5f);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
        return cell;
    }

    // Helper untuk menambah tabel dari TableModel ke PDF
    private void addTableToPdf(com.itextpdf.text.Document document, javax.swing.table.TableModel model) throws com.itextpdf.text.DocumentException {
        com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD, com.itextpdf.text.BaseColor.WHITE);
        com.itextpdf.text.Font cellFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 11, com.itextpdf.text.Font.NORMAL, com.itextpdf.text.BaseColor.BLACK);

        com.itextpdf.text.pdf.PdfPTable pdfTable = new com.itextpdf.text.pdf.PdfPTable(model.getColumnCount());
        pdfTable.setWidthPercentage(100);
        pdfTable.setSpacingBefore(10f);   // jarak atas tabel
        pdfTable.setSpacingAfter(15f);    // jarak bawah tabel

        // Add table header
        for (int i = 0; i < model.getColumnCount(); i++) {
            com.itextpdf.text.pdf.PdfPCell headerCell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(model.getColumnName(i), headerFont));
            headerCell.setBackgroundColor(new com.itextpdf.text.BaseColor(2, 84, 106));
            headerCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            headerCell.setVerticalAlignment(com.itextpdf.text.Element.ALIGN_MIDDLE);
            headerCell.setPaddingTop(8f);
            headerCell.setPaddingBottom(8f);
            headerCell.setBorderColor(new com.itextpdf.text.BaseColor(118, 158, 169));
            pdfTable.addCell(headerCell);
        }

        // Add table rows
        for (int rows = 0; rows < model.getRowCount(); rows++) {
            for (int cols = 0; cols < model.getColumnCount(); cols++) {
                Object value = model.getValueAt(rows, cols);
                String displayValue = value == null ? "" : value.toString();
                // Untuk kolom Nama Shift (biasanya index 2)
                if (cols == 2) {
                    displayValue = displayValue.replace("\n", " ").replace("\r", " ");
                }
                com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(displayValue, cellFont));
                cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                cell.setVerticalAlignment(com.itextpdf.text.Element.ALIGN_MIDDLE);
                cell.setPaddingTop(6f);
                cell.setPaddingBottom(6f);
                // Alternating row color
                if (rows % 2 == 0) {
                    cell.setBackgroundColor(new com.itextpdf.text.BaseColor(232, 245, 251));
                } else {
                    cell.setBackgroundColor(com.itextpdf.text.BaseColor.WHITE);
                }
                cell.setBorderColor(new com.itextpdf.text.BaseColor(118, 158, 169));
                pdfTable.addCell(cell);
            }
        }
        document.add(pdfTable);
    }

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
    private javax.swing.JLabel jamLabel2;
    private javax.swing.JPanel panelUtama_shiftkuPegawai;
    private javax.swing.JPanel sideBar;
    private javax.swing.JTable table_shiftkuPegawai_isiOtomatis;
    // End of variables declaration//GEN-END:variables
}