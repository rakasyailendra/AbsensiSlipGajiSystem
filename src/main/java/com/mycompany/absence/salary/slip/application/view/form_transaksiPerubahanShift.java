/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.absence.salary.slip.application.view;

import java.time.LocalTime;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import com.mycompany.absence.salary.slip.application.models.Jabatan;
import com.mycompany.absence.salary.slip.application.models.JabatanPegawai;
import com.mycompany.absence.salary.slip.application.models.Pegawai;
import com.mycompany.absence.salary.slip.application.models.Shift;
import com.mycompany.absence.salary.slip.application.models.ShiftPegawai;
import com.mycompany.absence.salary.slip.application.repositories.JabatanPegawaiRepository;
import com.mycompany.absence.salary.slip.application.repositories.JabatanRepository;
import com.mycompany.absence.salary.slip.application.repositories.PegawaiRepository;
import com.mycompany.absence.salary.slip.application.repositories.ShiftPegawaiRepository;
import com.mycompany.absence.salary.slip.application.repositories.ShiftRepository;
import com.mycompany.absence.salary.slip.application.utils.Response;

/**
 *
 * @author User
 */
public class form_transaksiPerubahanShift extends javax.swing.JPanel {

    /**
     * Creates new form form_masterJabatan
     */
    public form_transaksiPerubahanShift() {
        initComponents();
        initializeComponents();
    }

    ShiftPegawaiRepository shiftPegawaiRepository = new ShiftPegawaiRepository();
    PegawaiRepository pegawaiRepository = new PegawaiRepository();
    JabatanPegawaiRepository jabatanPegawaiRepository = new JabatanPegawaiRepository();
    JabatanRepository jabatanRepository = new JabatanRepository();
    ShiftRepository shiftRepository = new ShiftRepository();

    private void initializeComponents() {
        populateTableDataPerubahanShift();

        populateNamaComboBox();
        populateShiftComboBoxTambah();
        populateShiftComboBoxEdit();
        
        jText_nipPerubahanShift.setEditable(false);
        jText_jabatanPerubahanShift.setEditable(false);
        jText_jamMasuk.setEditable(false);
        jText_jamKeluar.setEditable(false);
        
        jText_nipPerubahanShift1.setEditable(false);
        jText_jabatanPerubahanShift1.setEditable(false);
        jText_jamMasuk2.setEditable(false);
        jText_jamKeluar2.setEditable(false);

        btn_hapus_transaksiPerubahanShift.setEnabled(false); // default: nonaktif
        btn_edit_transaksiPerubahanShift.setEnabled(false); // default: nonaktif
        
        table_dataPerubahanShift.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = table_dataPerubahanShift.getSelectedRow();
                boolean rowSelected = selectedRow >= 0;
                btn_edit_transaksiPerubahanShift.setEnabled(rowSelected);
                btn_hapus_transaksiPerubahanShift.setEnabled(rowSelected); // sekaligus pastikan ini juga sinkron
            }
        });
    }

    private void populateTableDataPerubahanShift() {
        String[] columnNames = { "NIP", "Nama Pegawai", "Jabatan", "Nama Shift", "Jam Masuk", "Jam Pulang" };
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        Response<ArrayList<ShiftPegawai>> response = shiftPegawaiRepository.findAll();
        if (response.isSuccess() && response.getData() != null) {
            for (ShiftPegawai sp : response.getData()) {
                // Ambil pegawai
                Pegawai pegawai = null;
                Response<Pegawai> pegawaiResp = pegawaiRepository.findById(sp.getIdPegawai());
                if (pegawaiResp != null && pegawaiResp.isSuccess()) {
                    pegawai = pegawaiResp.getData();
                }

                // Ambil jabatan
                Jabatan jabatan = null;
                Response<ArrayList<JabatanPegawai>> jpResponse = jabatanPegawaiRepository.findByPegawaiId(sp.getIdPegawai());
                if (jpResponse != null && jpResponse.isSuccess() && !jpResponse.getData().isEmpty()) {
                    JabatanPegawai jp = jpResponse.getData().get(0);
                    Response<Jabatan> jResponse = jabatanRepository.findById(jp.getIdJabatan());
                    if (jResponse != null && jResponse.isSuccess()) {
                        jabatan = jResponse.getData();
                    }
                }

                // Ambil shift
                Shift shift = null;
                Response<Shift> shiftResp = shiftRepository.findById(sp.getIdShift());
                if (shiftResp != null && shiftResp.isSuccess()) {
                    shift = shiftResp.getData();
                }

                // Validasi sebelum ditambahkan
                if (pegawai != null && shift != null) {
                    model.addRow(new Object[] {
                        pegawai.getNip(),
                        pegawai.getNama(),
                        (jabatan != null) ? jabatan.getNamaJabatan() : "-",
                        shift.getNamaShift(),
                        shift.getJamMasuk(),
                        shift.getJamKeluar()
                    });
                }
            }
        }

        table_dataPerubahanShift.setModel(model);
    }

    private void populateNamaComboBox() {
        jCombo_nama.removeAllItems();

        Response<ArrayList<Pegawai>> pegawaiResponse = pegawaiRepository.findAll();
        if (!pegawaiResponse.isSuccess()) return;

        for (Pegawai pegawai : pegawaiResponse.getData()) {
            // skip admin
            if (pegawai.getIsAdmin()) continue;

            // skip jika sudah punya shift
            Response<ArrayList<ShiftPegawai>> shiftPegawaiResp = shiftPegawaiRepository.findByPegawaiId(pegawai.getId());
            if (shiftPegawaiResp.isSuccess() && !shiftPegawaiResp.getData().isEmpty()) {
                continue; // Pegawai sudah punya shift
            }

            // hanya pegawai yang belum punya shift
            jCombo_nama.addItem(pegawai.getNama());
        }
    }

    private void populateNamaComboBoxEdit() {
        jCombo_nama1.removeAllItems();

        Response<ArrayList<Pegawai>> pegawaiResponse = pegawaiRepository.findAll();
        if (!pegawaiResponse.isSuccess()) return;

        for (Pegawai pegawai : pegawaiResponse.getData()) {
            if (pegawai.getIsAdmin()) continue;

            Response<ArrayList<ShiftPegawai>> shiftPegawaiResp = shiftPegawaiRepository.findByPegawaiId(pegawai.getId());
            if (shiftPegawaiResp.isSuccess() && !shiftPegawaiResp.getData().isEmpty()) {
                // ✅ Pegawai punya shift, boleh diedit
                jCombo_nama1.addItem(pegawai.getNama());
            }
        }

        if (jCombo_nama1.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "Belum ada pegawai yang memiliki shift untuk diedit.", 
                "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }


    private void populateShiftComboBoxTambah() {
        jCombo_namaShift.removeAllItems();
        Response<ArrayList<Shift>> response = shiftRepository.findAll();
        if (response.isSuccess()) {
            for (Shift s : response.getData()) {
                jCombo_namaShift.addItem(s.getNamaShift());
            }
        }
    }
    
    private void populateShiftComboBoxEdit() {
        jCombo_namaShift1.removeAllItems();
        Response<ArrayList<Shift>> response = shiftRepository.findAll();
        if (response.isSuccess()) {
            for (Shift s : response.getData()) {
                jCombo_namaShift1.addItem(s.getNamaShift());
            }
        }
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        dataPerubahanShift = new javax.swing.JPanel();
        panelUtama_transaksiPerubahanShift_Admin = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        btn_tambah_transaksiPerubahanShift = new javax.swing.JButton();
        btn_hapus_transaksiPerubahanShift = new javax.swing.JButton();
        btn_edit_transaksiPerubahanShift = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        table_dataPerubahanShift = new javax.swing.JTable();
        tambahPerubahanShift = new javax.swing.JPanel();
        panelKedua_transaksiPerubahanShift_Admin = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        btn_simpan_transaksiPerubahanShift = new javax.swing.JButton();
        btn_batal_transaksiPerubahanShift = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jText_jamMasuk = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jText_jamKeluar = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jText_nipPerubahanShift = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jText_jabatanPerubahanShift = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jCombo_nama = new javax.swing.JComboBox<>();
        jCombo_namaShift = new javax.swing.JComboBox<>();
        editPerubahanShift = new javax.swing.JPanel();
        panelKetiga_transaksiPerubahanShift_Admin1 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        btn_simpan_transaksiPerubahanShift1 = new javax.swing.JButton();
        btn_batal_transaksiPerubahanShift1 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jText_jamMasuk2 = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jText_jamKeluar2 = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jText_nipPerubahanShift1 = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jText_jabatanPerubahanShift1 = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jCombo_nama1 = new javax.swing.JComboBox<>();
        jCombo_namaShift1 = new javax.swing.JComboBox<>();

        setPreferredSize(new java.awt.Dimension(900, 525));

        mainPanel.setLayout(new java.awt.CardLayout());

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(2, 84, 106));
        jLabel4.setText("Data Perubahan Shift");

        btn_tambah_transaksiPerubahanShift.setText("TAMBAH");
        btn_tambah_transaksiPerubahanShift.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambah_transaksiPerubahanShiftActionPerformed(evt);
            }
        });

        btn_hapus_transaksiPerubahanShift.setText("HAPUS");
        btn_hapus_transaksiPerubahanShift.setToolTipText("");
        btn_hapus_transaksiPerubahanShift.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_hapus_transaksiPerubahanShiftActionPerformed(evt);
            }
        });

        btn_edit_transaksiPerubahanShift.setText("EDIT");
        btn_edit_transaksiPerubahanShift.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_edit_transaksiPerubahanShiftActionPerformed(evt);
            }
        });

        table_dataPerubahanShift.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "NIP", "Nama", "Jabatan", "Nama Shift", "Jam Masuk", "Jam Keluar"
            }
        ));
        jScrollPane2.setViewportView(table_dataPerubahanShift);

        javax.swing.GroupLayout panelUtama_transaksiPerubahanShift_AdminLayout = new javax.swing.GroupLayout(panelUtama_transaksiPerubahanShift_Admin);
        panelUtama_transaksiPerubahanShift_Admin.setLayout(panelUtama_transaksiPerubahanShift_AdminLayout);
        panelUtama_transaksiPerubahanShift_AdminLayout.setHorizontalGroup(
            panelUtama_transaksiPerubahanShift_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelUtama_transaksiPerubahanShift_AdminLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelUtama_transaksiPerubahanShift_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelUtama_transaksiPerubahanShift_AdminLayout.createSequentialGroup()
                        .addComponent(btn_tambah_transaksiPerubahanShift)
                        .addGap(12, 12, 12)
                        .addComponent(btn_edit_transaksiPerubahanShift)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btn_hapus_transaksiPerubahanShift)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(panelUtama_transaksiPerubahanShift_AdminLayout.createSequentialGroup()
                        .addGroup(panelUtama_transaksiPerubahanShift_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2)
                            .addGroup(panelUtama_transaksiPerubahanShift_AdminLayout.createSequentialGroup()
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 514, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        panelUtama_transaksiPerubahanShift_AdminLayout.setVerticalGroup(
            panelUtama_transaksiPerubahanShift_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelUtama_transaksiPerubahanShift_AdminLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addGap(37, 37, 37)
                .addGroup(panelUtama_transaksiPerubahanShift_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_tambah_transaksiPerubahanShift)
                    .addComponent(btn_hapus_transaksiPerubahanShift)
                    .addComponent(btn_edit_transaksiPerubahanShift))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(342, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout dataPerubahanShiftLayout = new javax.swing.GroupLayout(dataPerubahanShift);
        dataPerubahanShift.setLayout(dataPerubahanShiftLayout);
        dataPerubahanShiftLayout.setHorizontalGroup(
            dataPerubahanShiftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 900, Short.MAX_VALUE)
            .addGroup(dataPerubahanShiftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(dataPerubahanShiftLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panelUtama_transaksiPerubahanShift_Admin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        dataPerubahanShiftLayout.setVerticalGroup(
            dataPerubahanShiftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 616, Short.MAX_VALUE)
            .addGroup(dataPerubahanShiftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(dataPerubahanShiftLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panelUtama_transaksiPerubahanShift_Admin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        mainPanel.add(dataPerubahanShift, "card2");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(2, 84, 106));
        jLabel5.setText("Tambah Data Perubahan Shift");

        btn_simpan_transaksiPerubahanShift.setText("SIMPAN");
        btn_simpan_transaksiPerubahanShift.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_simpan_transaksiPerubahanShiftActionPerformed(evt);
            }
        });

        btn_batal_transaksiPerubahanShift.setText("BATAL");
        btn_batal_transaksiPerubahanShift.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_batal_transaksiPerubahanShiftActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("NAMA");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setText("Jam Masuk");

        jText_jamMasuk.setText("Auto");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setText("Jam Keluar");

        jText_jamKeluar.setText("Auto");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("NIP");

        jText_nipPerubahanShift.setText("Auto");
        jText_nipPerubahanShift.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jText_nipPerubahanShiftActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Jabatan");

        jText_jabatanPerubahanShift.setText("Auto");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText("Nama Shift");

        jCombo_nama.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jCombo_nama.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCombo_namaActionPerformed(evt);
            }
        });

        jCombo_namaShift.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jCombo_namaShift.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCombo_namaShiftActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1)
                    .addComponent(jLabel9)
                    .addComponent(jText_jamMasuk, javax.swing.GroupLayout.DEFAULT_SIZE, 845, Short.MAX_VALUE)
                    .addComponent(jLabel10)
                    .addComponent(jText_jamKeluar, javax.swing.GroupLayout.DEFAULT_SIZE, 845, Short.MAX_VALUE)
                    .addComponent(jLabel3)
                    .addComponent(jText_nipPerubahanShift, javax.swing.GroupLayout.DEFAULT_SIZE, 845, Short.MAX_VALUE)
                    .addComponent(jLabel7)
                    .addComponent(jText_jabatanPerubahanShift, javax.swing.GroupLayout.DEFAULT_SIZE, 845, Short.MAX_VALUE)
                    .addComponent(jLabel8)
                    .addComponent(jCombo_nama, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jCombo_namaShift, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCombo_nama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jText_nipPerubahanShift, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jText_jabatanPerubahanShift, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCombo_namaShift, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jText_jamMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jText_jamKeluar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(104, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelKedua_transaksiPerubahanShift_AdminLayout = new javax.swing.GroupLayout(panelKedua_transaksiPerubahanShift_Admin);
        panelKedua_transaksiPerubahanShift_Admin.setLayout(panelKedua_transaksiPerubahanShift_AdminLayout);
        panelKedua_transaksiPerubahanShift_AdminLayout.setHorizontalGroup(
            panelKedua_transaksiPerubahanShift_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelKedua_transaksiPerubahanShift_AdminLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelKedua_transaksiPerubahanShift_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelKedua_transaksiPerubahanShift_AdminLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(panelKedua_transaksiPerubahanShift_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelKedua_transaksiPerubahanShift_AdminLayout.createSequentialGroup()
                                .addComponent(btn_simpan_transaksiPerubahanShift)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btn_batal_transaksiPerubahanShift))
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 500, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelKedua_transaksiPerubahanShift_AdminLayout.setVerticalGroup(
            panelKedua_transaksiPerubahanShift_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelKedua_transaksiPerubahanShift_AdminLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addGap(37, 37, 37)
                .addGroup(panelKedua_transaksiPerubahanShift_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_simpan_transaksiPerubahanShift)
                    .addComponent(btn_batal_transaksiPerubahanShift))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(12, 12, 12))
        );

        javax.swing.GroupLayout tambahPerubahanShiftLayout = new javax.swing.GroupLayout(tambahPerubahanShift);
        tambahPerubahanShift.setLayout(tambahPerubahanShiftLayout);
        tambahPerubahanShiftLayout.setHorizontalGroup(
            tambahPerubahanShiftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tambahPerubahanShiftLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelKedua_transaksiPerubahanShift_Admin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        tambahPerubahanShiftLayout.setVerticalGroup(
            tambahPerubahanShiftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tambahPerubahanShiftLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelKedua_transaksiPerubahanShift_Admin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        mainPanel.add(tambahPerubahanShift, "card2");

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(2, 84, 106));
        jLabel13.setText("Edit Data Perubahan Shift");

        btn_simpan_transaksiPerubahanShift1.setText("SIMPAN");
        btn_simpan_transaksiPerubahanShift1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_simpan_transaksiPerubahanShift1ActionPerformed(evt);
            }
        });

        btn_batal_transaksiPerubahanShift1.setText("BATAL");
        btn_batal_transaksiPerubahanShift1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_batal_transaksiPerubahanShift1ActionPerformed(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel14.setText("NAMA");

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel15.setText("Jam Masuk");

        jText_jamMasuk2.setText("Auto");

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel16.setText("Jam Keluar");

        jText_jamKeluar2.setText("Auto");

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel17.setText("NIP");

        jText_nipPerubahanShift1.setText("Auto");
        jText_nipPerubahanShift1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jText_nipPerubahanShift1ActionPerformed(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel18.setText("Jabatan");

        jText_jabatanPerubahanShift1.setText("Auto");

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel19.setText("Nama Shift");

        jCombo_nama1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jCombo_nama1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCombo_nama1ActionPerformed(evt);
            }
        });

        jCombo_namaShift1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jCombo_namaShift1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCombo_namaShift1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel14)
                    .addComponent(jLabel15)
                    .addComponent(jText_jamMasuk2, javax.swing.GroupLayout.DEFAULT_SIZE, 845, Short.MAX_VALUE)
                    .addComponent(jLabel16)
                    .addComponent(jText_jamKeluar2, javax.swing.GroupLayout.DEFAULT_SIZE, 845, Short.MAX_VALUE)
                    .addComponent(jLabel17)
                    .addComponent(jText_nipPerubahanShift1, javax.swing.GroupLayout.DEFAULT_SIZE, 845, Short.MAX_VALUE)
                    .addComponent(jLabel18)
                    .addComponent(jText_jabatanPerubahanShift1, javax.swing.GroupLayout.DEFAULT_SIZE, 845, Short.MAX_VALUE)
                    .addComponent(jLabel19)
                    .addComponent(jCombo_nama1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jCombo_namaShift1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCombo_nama1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jText_nipPerubahanShift1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jText_jabatanPerubahanShift1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCombo_namaShift1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jText_jamMasuk2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jText_jamKeluar2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(104, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelKetiga_transaksiPerubahanShift_Admin1Layout = new javax.swing.GroupLayout(panelKetiga_transaksiPerubahanShift_Admin1);
        panelKetiga_transaksiPerubahanShift_Admin1.setLayout(panelKetiga_transaksiPerubahanShift_Admin1Layout);
        panelKetiga_transaksiPerubahanShift_Admin1Layout.setHorizontalGroup(
            panelKetiga_transaksiPerubahanShift_Admin1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelKetiga_transaksiPerubahanShift_Admin1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelKetiga_transaksiPerubahanShift_Admin1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelKetiga_transaksiPerubahanShift_Admin1Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(panelKetiga_transaksiPerubahanShift_Admin1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelKetiga_transaksiPerubahanShift_Admin1Layout.createSequentialGroup()
                                .addComponent(btn_simpan_transaksiPerubahanShift1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btn_batal_transaksiPerubahanShift1))
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 500, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelKetiga_transaksiPerubahanShift_Admin1Layout.setVerticalGroup(
            panelKetiga_transaksiPerubahanShift_Admin1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelKetiga_transaksiPerubahanShift_Admin1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addGap(37, 37, 37)
                .addGroup(panelKetiga_transaksiPerubahanShift_Admin1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_simpan_transaksiPerubahanShift1)
                    .addComponent(btn_batal_transaksiPerubahanShift1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(12, 12, 12))
        );

        javax.swing.GroupLayout editPerubahanShiftLayout = new javax.swing.GroupLayout(editPerubahanShift);
        editPerubahanShift.setLayout(editPerubahanShiftLayout);
        editPerubahanShiftLayout.setHorizontalGroup(
            editPerubahanShiftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editPerubahanShiftLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelKetiga_transaksiPerubahanShift_Admin1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        editPerubahanShiftLayout.setVerticalGroup(
            editPerubahanShiftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editPerubahanShiftLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelKetiga_transaksiPerubahanShift_Admin1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        mainPanel.add(editPerubahanShift, "card2");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 900, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 616, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btn_tambah_transaksiPerubahanShiftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambah_transaksiPerubahanShiftActionPerformed
        mainPanel.removeAll();
        mainPanel.repaint();
        mainPanel.revalidate();

        mainPanel.add(tambahPerubahanShift);
        mainPanel.repaint();
        mainPanel.revalidate();

        populateNamaComboBox();
        populateShiftComboBoxTambah();
    }//GEN-LAST:event_btn_tambah_transaksiPerubahanShiftActionPerformed

    private void btn_hapus_transaksiPerubahanShiftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hapus_transaksiPerubahanShiftActionPerformed
        int selectedRow = table_dataPerubahanShift.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Silakan pilih pegawai terlebih dahulu.");
            return;
        }

        String nip = table_dataPerubahanShift.getValueAt(selectedRow, 0).toString();

        // Konfirmasi
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus shift pegawai ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        // Cari pegawai
        Response<Pegawai> pegawaiResp = pegawaiRepository.findByNip(nip);
        if (!pegawaiResp.isSuccess()) {
            JOptionPane.showMessageDialog(this, "Pegawai tidak ditemukan.");
            return;
        }

        Pegawai pegawai = pegawaiResp.getData();

        // Cari shift pegawai
        Response<ArrayList<ShiftPegawai>> shiftPegResp = shiftPegawaiRepository.findByPegawaiId(pegawai.getId());
        if (shiftPegResp.isSuccess() && !shiftPegResp.getData().isEmpty()) {
            ShiftPegawai sp = shiftPegResp.getData().get(0);
            Response<Boolean> deleteResp = shiftPegawaiRepository.deleteById(sp.getId());
            if (deleteResp.isSuccess()) {
            JOptionPane.showMessageDialog(this, "Shift pegawai berhasil dihapus.");
            populateTableDataPerubahanShift();
            } else {
            JOptionPane.showMessageDialog(this, "Gagal menghapus shift pegawai: " + deleteResp.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pegawai tidak memiliki shift.");
        }
    }//GEN-LAST:event_btn_hapus_transaksiPerubahanShiftActionPerformed

    private void btn_simpan_transaksiPerubahanShiftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_simpan_transaksiPerubahanShiftActionPerformed
        String nama = (String) jCombo_nama.getSelectedItem();
        String shiftNama = (String) jCombo_namaShift.getSelectedItem();

        // Validasi awal: combo box
        if (nama == null || shiftNama == null) {
            JOptionPane.showMessageDialog(this, "Nama pegawai dan shift harus dipilih.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // ✅ Tambahkan validasi field auto disini:
        if (jText_nipPerubahanShift.getText().isEmpty() || 
            jText_jabatanPerubahanShift.getText().isEmpty() || 
            jText_jamMasuk.getText().isEmpty() || 
            jText_jamKeluar.getText().isEmpty()) {

            JOptionPane.showMessageDialog(this, 
                "Pastikan semua data otomatis (NIP, jabatan, jam masuk, jam keluar) sudah terisi.\n" +
                "Kemungkinan pegawai belum memiliki jabatan atau shift belum memiliki jam lengkap.", 
                "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Lanjut cari pegawai dan shift → SIMPAN
        Pegawai selectedPegawai = null;
        Response<ArrayList<Pegawai>> responsePegawai = pegawaiRepository.findAll();
        if (responsePegawai.isSuccess()) {
            for (Pegawai p : responsePegawai.getData()) {
                if (p.getNama().equals(nama)) {
                    selectedPegawai = p;
                    break;
                }
            }
        }

        if (selectedPegawai == null) {
            JOptionPane.showMessageDialog(this, "Data pegawai tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Shift selectedShift = null;
        Response<ArrayList<Shift>> responseShift = shiftRepository.findAll();
        if (responseShift.isSuccess()) {
            for (Shift s : responseShift.getData()) {
                if (s.getNamaShift().equals(shiftNama)) {
                    selectedShift = s;
                    break;
                }
            }
        }

        if (selectedShift == null) {
            JOptionPane.showMessageDialog(this, "Data shift tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Simpan ke ShiftPegawai
        ShiftPegawai newEntry = new ShiftPegawai();
        newEntry.setIdPegawai(selectedPegawai.getId());
        newEntry.setIdShift(selectedShift.getId());

        Response<ShiftPegawai> saveResp = shiftPegawaiRepository.save(newEntry);
        if (saveResp.isSuccess()) {
            JOptionPane.showMessageDialog(this, "Perubahan shift berhasil disimpan.");
            mainPanel.removeAll();
            mainPanel.add(dataPerubahanShift);
            mainPanel.repaint();
            mainPanel.revalidate();
            populateTableDataPerubahanShift(); // refresh tabel
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan shift: " + saveResp.getMessage());
        }
    }//GEN-LAST:event_btn_simpan_transaksiPerubahanShiftActionPerformed

    private void btn_batal_transaksiPerubahanShiftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_batal_transaksiPerubahanShiftActionPerformed
        mainPanel.removeAll();
        mainPanel.add(dataPerubahanShift);
        mainPanel.repaint();
        mainPanel.revalidate();
    }//GEN-LAST:event_btn_batal_transaksiPerubahanShiftActionPerformed

    private void btn_edit_transaksiPerubahanShiftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_edit_transaksiPerubahanShiftActionPerformed
        int selectedRow = table_dataPerubahanShift.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Silakan pilih baris pegawai terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Ambil NIP dari baris yang dipilih
        String nip = table_dataPerubahanShift.getValueAt(selectedRow, 0).toString();

        // Ambil data pegawai berdasarkan NIP
        Response<Pegawai> pegawaiResp = pegawaiRepository.findByNip(nip);
        if (!pegawaiResp.isSuccess()) {
            JOptionPane.showMessageDialog(this, "Data pegawai tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Pegawai pegawai = pegawaiResp.getData();

        populateNamaComboBoxEdit();
        populateShiftComboBoxEdit();

        // Set nama di combo box edit
        for (int i = 0; i < jCombo_nama1.getItemCount(); i++) {
            String namaCombo = jCombo_nama1.getItemAt(i);
            if (namaCombo.equals(pegawai.getNama())) {
                jCombo_nama1.setSelectedIndex(i);
                break;
            }
        }

        // Set NIP
        jText_nipPerubahanShift1.setText(pegawai.getNip());

        // Set Jabatan
        Response<ArrayList<JabatanPegawai>> jpResp = jabatanPegawaiRepository.findByPegawaiId(pegawai.getId());
        if (jpResp.isSuccess() && !jpResp.getData().isEmpty()) {
            JabatanPegawai jp = jpResp.getData().get(0);
            Response<Jabatan> jabatanResp = jabatanRepository.findById(jp.getIdJabatan());
            if (jabatanResp.isSuccess()) {
                jText_jabatanPerubahanShift1.setText(jabatanResp.getData().getNamaJabatan());
            }
        }

        // Ambil shift aktif
        Response<ArrayList<ShiftPegawai>> shiftPegResp = shiftPegawaiRepository.findByPegawaiId(pegawai.getId());
        if (shiftPegResp.isSuccess() && !shiftPegResp.getData().isEmpty()) {
            ShiftPegawai shiftPeg = shiftPegResp.getData().get(0);
            Response<Shift> shiftResp = shiftRepository.findById(shiftPeg.getIdShift());
            if (shiftResp.isSuccess()) {
                Shift s = shiftResp.getData();

                // Set shift di combo box
                for (int i = 0; i < jCombo_namaShift1.getItemCount(); i++) {
                    if (jCombo_namaShift1.getItemAt(i).equals(s.getNamaShift())) {
                        jCombo_namaShift1.setSelectedIndex(i);
                        break;
                    }
                }

                // Set jam masuk dan keluar
                jText_jamMasuk2.setText(s.getJamMasuk().toString());
                jText_jamKeluar2.setText(s.getJamKeluar().toString());
            }
        }

        // Ganti tampilan ke panel edit
        mainPanel.removeAll();
        mainPanel.add(editPerubahanShift);
        mainPanel.repaint();
        mainPanel.revalidate();
    }//GEN-LAST:event_btn_edit_transaksiPerubahanShiftActionPerformed

    private void jText_nipPerubahanShiftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jText_nipPerubahanShiftActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jText_nipPerubahanShiftActionPerformed

    private void jCombo_namaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCombo_namaActionPerformed
        String selectedNama = (String) jCombo_nama.getSelectedItem();
        if (selectedNama == null) return;

        // Kosongkan field otomatis
        jText_nipPerubahanShift.setText("");
        jText_jabatanPerubahanShift.setText("");
        jText_jamMasuk.setText("");
        jText_jamKeluar.setText("");

        Response<ArrayList<Pegawai>> response = pegawaiRepository.findAll();
        if (response.isSuccess()) {
            for (Pegawai p : response.getData()) {
                if (p.getNama().equals(selectedNama)) {
                    jText_nipPerubahanShift.setText(p.getNip());

                    // Isi jabatan
                    Response<ArrayList<JabatanPegawai>> jpResp = jabatanPegawaiRepository.findByPegawaiId(p.getId());
                    if (jpResp.isSuccess() && !jpResp.getData().isEmpty()) {
                        JabatanPegawai jp = jpResp.getData().get(0);
                        Response<Jabatan> jabatanResp = jabatanRepository.findById(jp.getIdJabatan());
                        if (jabatanResp.isSuccess()) {
                            jText_jabatanPerubahanShift.setText(jabatanResp.getData().getNamaJabatan());
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Pegawai ini belum memiliki jabatan. Tidak bisa diproses.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    // Jika sudah punya shift, isi otomatis
                    Response<ArrayList<ShiftPegawai>> shiftPegawaiResponse = shiftPegawaiRepository.findByPegawaiId(p.getId());
                    if (shiftPegawaiResponse.isSuccess() && !shiftPegawaiResponse.getData().isEmpty()) {
                        ShiftPegawai currentShiftPegawai = shiftPegawaiResponse.getData().get(0);
                        Response<Shift> shiftResp = shiftRepository.findById(currentShiftPegawai.getIdShift());
                        if (shiftResp.isSuccess()) {
                            Shift s = shiftResp.getData();

                            // Set combo shift
                            for (int i = 0; i < jCombo_namaShift.getItemCount(); i++) {
                                String shiftName = jCombo_namaShift.getItemAt(i);
                                if (shiftName.equals(s.getNamaShift())) {
                                    jCombo_namaShift.setSelectedIndex(i);
                                    break;
                                }
                            }

                            // Set jam
                            jText_jamMasuk.setText(s.getJamMasuk().toString());
                            jText_jamKeluar.setText(s.getJamKeluar().toString());
                        }
                    }

                    break;
                }
            }
        }
    }//GEN-LAST:event_jCombo_namaActionPerformed

    private void btn_simpan_transaksiPerubahanShift1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_simpan_transaksiPerubahanShift1ActionPerformed
        String selectedNama = (String) jCombo_nama1.getSelectedItem();
        String selectedShiftNama = (String) jCombo_namaShift1.getSelectedItem();

        if (selectedNama == null || selectedShiftNama == null) {
            JOptionPane.showMessageDialog(this, "Nama pegawai dan shift harus dipilih.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Ambil pegawai
        Pegawai selectedPegawai = null;
        Response<ArrayList<Pegawai>> pegawaiResp = pegawaiRepository.findAll();
        if (pegawaiResp.isSuccess()) {
            for (Pegawai p : pegawaiResp.getData()) {
                if (p.getNama().equals(selectedNama)) {
                    selectedPegawai = p;
                    break;
                }
            }
        }

        if (selectedPegawai == null) {
            JOptionPane.showMessageDialog(this, "Pegawai tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Ambil shift
        Shift selectedShift = null;
        Response<ArrayList<Shift>> shiftResp = shiftRepository.findAll();
        if (shiftResp.isSuccess()) {
            for (Shift s : shiftResp.getData()) {
                if (s.getNamaShift().equals(selectedShiftNama)) {
                    selectedShift = s;
                    break;
                }
            }
        }

        if (selectedShift == null) {
            JOptionPane.showMessageDialog(this, "Shift tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Hapus shift lama (jika ada)
        Response<ArrayList<ShiftPegawai>> shiftPegResp = shiftPegawaiRepository.findByPegawaiId(selectedPegawai.getId());
        if (shiftPegResp.isSuccess() && !shiftPegResp.getData().isEmpty()) {
            ShiftPegawai old = shiftPegResp.getData().get(0);
            shiftPegawaiRepository.deleteById(old.getId());
        }

        // Simpan shift baru
        ShiftPegawai newShift = new ShiftPegawai();
        newShift.setIdPegawai(selectedPegawai.getId());
        newShift.setIdShift(selectedShift.getId());

        Response<ShiftPegawai> saveResp = shiftPegawaiRepository.save(newShift);
        if (saveResp.isSuccess()) {
            JOptionPane.showMessageDialog(this, "Shift pegawai berhasil diperbarui.");

            // Kembali ke panel utama
            mainPanel.removeAll();
            mainPanel.add(dataPerubahanShift);
            mainPanel.repaint();
            mainPanel.revalidate();

            // Refresh tabel
            populateTableDataPerubahanShift();

            // Reset tombol
            btn_edit_transaksiPerubahanShift.setEnabled(false);
            btn_hapus_transaksiPerubahanShift.setEnabled(false);
        } else {
            JOptionPane.showMessageDialog(this, "Gagal memperbarui shift pegawai: " + saveResp.getMessage());
        }
    }//GEN-LAST:event_btn_simpan_transaksiPerubahanShift1ActionPerformed

    private void btn_batal_transaksiPerubahanShift1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_batal_transaksiPerubahanShift1ActionPerformed
        mainPanel.removeAll();
        mainPanel.add(dataPerubahanShift);
        mainPanel.repaint();
        mainPanel.revalidate();
    }//GEN-LAST:event_btn_batal_transaksiPerubahanShift1ActionPerformed

    private void jText_nipPerubahanShift1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jText_nipPerubahanShift1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jText_nipPerubahanShift1ActionPerformed

    private void jCombo_nama1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCombo_nama1ActionPerformed
            String selectedNama = (String) jCombo_nama1.getSelectedItem();
        if (selectedNama == null) return;

        // Kosongkan field otomatis
        jText_nipPerubahanShift1.setText("");
        jText_jabatanPerubahanShift1.setText("");
        jText_jamMasuk2.setText("");
        jText_jamKeluar2.setText("");

        Response<ArrayList<Pegawai>> pegawaiResp = pegawaiRepository.findAll();
        if (!pegawaiResp.isSuccess()) return;

        for (Pegawai p : pegawaiResp.getData()) {
            if (p.getNama().equals(selectedNama)) {

                // Set NIP
                jText_nipPerubahanShift1.setText(p.getNip());

                // Set jabatan
                Response<ArrayList<JabatanPegawai>> jpResp = jabatanPegawaiRepository.findByPegawaiId(p.getId());
                if (jpResp.isSuccess() && !jpResp.getData().isEmpty()) {
                    JabatanPegawai jp = jpResp.getData().get(0);
                    Response<Jabatan> jResp = jabatanRepository.findById(jp.getIdJabatan());
                    if (jResp.isSuccess()) {
                        jText_jabatanPerubahanShift1.setText(jResp.getData().getNamaJabatan());
                    }
                }

                // Set shift aktif
                Response<ArrayList<ShiftPegawai>> spResp = shiftPegawaiRepository.findByPegawaiId(p.getId());
                if (spResp.isSuccess() && !spResp.getData().isEmpty()) {
                    ShiftPegawai sp = spResp.getData().get(0);
                    Response<Shift> shiftResp = shiftRepository.findById(sp.getIdShift());
                    if (shiftResp.isSuccess()) {
                        Shift s = shiftResp.getData();

                        // Pilih shift di combo box
                        for (int i = 0; i < jCombo_namaShift1.getItemCount(); i++) {
                            String shiftName = jCombo_namaShift1.getItemAt(i);
                            if (shiftName.equals(s.getNamaShift())) {
                                jCombo_namaShift1.setSelectedIndex(i);
                                break;
                            }
                        }

                        // Set jam otomatis
                        jText_jamMasuk2.setText(s.getJamMasuk().toString());
                        jText_jamKeluar2.setText(s.getJamKeluar().toString());
                    }
                }

                break;
            }
        }
    }//GEN-LAST:event_jCombo_nama1ActionPerformed

    private void jCombo_namaShiftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCombo_namaShiftActionPerformed
        String selectedShift = (String) jCombo_namaShift.getSelectedItem();
        jText_jamMasuk.setText("");
        jText_jamKeluar.setText("");

        if (selectedShift == null) return;

        Response<ArrayList<Shift>> response = shiftRepository.findAll();
        if (response.isSuccess()) {
            for (Shift s : response.getData()) {
                if (s.getNamaShift().equals(selectedShift)) {
                    if (s.getJamMasuk() != null && s.getJamKeluar() != null) {
                        jText_jamMasuk.setText(s.getJamMasuk().toString());
                        jText_jamKeluar.setText(s.getJamKeluar().toString());
                    } else {
                        JOptionPane.showMessageDialog(this, "Shift belum memiliki jam masuk/keluar yang lengkap.");
                    }
                    break;
                }
            }
        }
    }//GEN-LAST:event_jCombo_namaShiftActionPerformed

    private void jCombo_namaShift1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCombo_namaShift1ActionPerformed
        String selectedShiftNama = (String) jCombo_namaShift1.getSelectedItem();
        if (selectedShiftNama == null) return;

        Response<ArrayList<Shift>> shiftResp = shiftRepository.findAll();
        if (shiftResp.isSuccess()) {
            for (Shift s : shiftResp.getData()) {
                if (s.getNamaShift().equals(selectedShiftNama)) {
                    jText_jamMasuk2.setText(s.getJamMasuk().toString());
                    jText_jamKeluar2.setText(s.getJamKeluar().toString());
                    break;
                }
            }
        }
    }//GEN-LAST:event_jCombo_namaShift1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_batal_transaksiPerubahanShift;
    private javax.swing.JButton btn_batal_transaksiPerubahanShift1;
    private javax.swing.JButton btn_edit_transaksiPerubahanShift;
    private javax.swing.JButton btn_hapus_transaksiPerubahanShift;
    private javax.swing.JButton btn_simpan_transaksiPerubahanShift;
    private javax.swing.JButton btn_simpan_transaksiPerubahanShift1;
    private javax.swing.JButton btn_tambah_transaksiPerubahanShift;
    private javax.swing.JPanel dataPerubahanShift;
    private javax.swing.JPanel editPerubahanShift;
    private javax.swing.JComboBox<String> jCombo_nama;
    private javax.swing.JComboBox<String> jCombo_nama1;
    private javax.swing.JComboBox<String> jCombo_namaShift;
    private javax.swing.JComboBox<String> jCombo_namaShift1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField jText_jabatanPerubahanShift;
    private javax.swing.JTextField jText_jabatanPerubahanShift1;
    private javax.swing.JTextField jText_jamKeluar;
    private javax.swing.JTextField jText_jamKeluar2;
    private javax.swing.JTextField jText_jamMasuk;
    private javax.swing.JTextField jText_jamMasuk2;
    private javax.swing.JTextField jText_nipPerubahanShift;
    private javax.swing.JTextField jText_nipPerubahanShift1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel panelKedua_transaksiPerubahanShift_Admin;
    private javax.swing.JPanel panelKetiga_transaksiPerubahanShift_Admin1;
    private javax.swing.JPanel panelUtama_transaksiPerubahanShift_Admin;
    private javax.swing.JTable table_dataPerubahanShift;
    private javax.swing.JPanel tambahPerubahanShift;
    // End of variables declaration//GEN-END:variables
}