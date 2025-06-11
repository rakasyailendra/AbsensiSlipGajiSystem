/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.absence.salary.slip.application.view;

import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;

import com.mycompany.absence.salary.slip.application.models.Jabatan;
import com.mycompany.absence.salary.slip.application.repositories.JabatanRepository;
import com.mycompany.absence.salary.slip.application.utils.Response;
import java.awt.event.ActionEvent;

/**
 *
 * @author User
 */
public class form_masterJabatan extends javax.swing.JPanel {

    /**
     * Creates new form form_masterJabatan
     */
    public form_masterJabatan() {
        initComponents();
        initializeComponents();
    }

    JabatanRepository jabatanRepository = new JabatanRepository();

    private void initializeComponents() {
        mainPanel.removeAll();
        mainPanel.repaint();
        mainPanel.revalidate();

        mainPanel.add(dataJabatan);
        mainPanel.repaint();
        mainPanel.revalidate();

        populateTableJabatan();
        resetForm();
    }

    private void editJabatan() {
        int selectedRow = table_dataJabatan.getSelectedRow();
        if (selectedRow != -1) {
            // Get the ID of the selected jabatan
            int jabatanId = (int) table_dataJabatan.getValueAt(selectedRow, 0); // Ambil ID dari kolom tersembunyi
            Jabatan jabatan = jabatanRepository.findById(jabatanId).getData();

            if (jabatan != null) {
                jText_Jabatan.setText(jabatan.getNamaJabatan());
                jText_Gaji.setText(String.valueOf(jabatan.getGajiPokok()));
            } else {
                System.out.println("Jabatan not found.");
            }
        } else {
            System.out.println("No row selected for editing.");
        }
    }

    private void tambahJabatan() {
        String namaJabatan = jText_Jabatan.getText();
        String gajiPokok = jText_Gaji.getText();

        if (namaJabatan.isEmpty() || gajiPokok.isEmpty()) {
            System.out.println("Nama Jabatan dan Gaji Pokok tidak boleh kosong.");
            return;
        }

        Jabatan jabatan = new Jabatan();
        jabatan.setNamaJabatan(namaJabatan);
        jabatan.setGajiPokok(Double.parseDouble(gajiPokok));

        Response<Jabatan> response = jabatanRepository.save(jabatan);
        if (response.isSuccess()) {
            initializeComponents();
        } else {
            System.out.println("Error saving data: " + response.getMessage());
        }
    }

    private void hapusJabatan() {
        int selectedRow = table_dataJabatan.getSelectedRow();
        if (selectedRow != -1) {
            // confirmation dialog can be added here
            int confirmation = javax.swing.JOptionPane.showConfirmDialog(this,
                    "Apakah Anda yakin ingin menghapus data jabatan ini?", "Konfirmasi Hapus",
                    javax.swing.JOptionPane.YES_NO_OPTION);
            if (confirmation != javax.swing.JOptionPane.YES_OPTION) {
                return; // User chose not to delete
            }

            int jabatanId = (int) table_dataJabatan.getValueAt(selectedRow, 0); // Ambil ID dari kolom tersembunyi
            Response<Boolean> response = jabatanRepository.deleteById(jabatanId);
            if (response.isSuccess()) {
                populateTableJabatan();
                resetForm();
            } else {
                System.out.println("Error deleting data: " + response.getMessage());
            }
        } else {
            System.out.println("No row selected for deletion.");
        }
    }

    private void resetForm() {
        jText_Jabatan.setText("");
        jText_Gaji.setText("");
    }

    private void populateTableJabatan() {
        String[] columnNames = { "ID", "Jabatan", "Gaji" }; // Tambahkan kolom ID
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table_dataJabatan.setModel(model);

        Response<ArrayList<Jabatan>> response = jabatanRepository.findAll();
        if (response.isSuccess()) {
            ArrayList<Jabatan> jabatanList = response.getData();
            for (Jabatan jabatan : jabatanList) {
                model.addRow(new Object[] {
                        jabatan.getId(), // disimpan di kolom tersembunyi
                        jabatan.getNamaJabatan(),
                        jabatan.getGajiPokok()
                });
            }
        } else {
            System.out.println("Error fetching data: " + response.getMessage());
        }

        // Sembunyikan kolom pertama (ID)
        table_dataJabatan.getColumnModel().getColumn(0).setMinWidth(0);
        table_dataJabatan.getColumnModel().getColumn(0).setMaxWidth(0);
        table_dataJabatan.getColumnModel().getColumn(0).setWidth(0);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        dataJabatan = new javax.swing.JPanel();
        panelUtama_masterJabatan_Admin = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_dataJabatan = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        btn_tambah_masterJabatan = new javax.swing.JButton();
        btn_hapus_masterJabatan = new javax.swing.JButton();
        btn_batal_masterJabatan = new javax.swing.JButton();
        btn_edit_masterJabatan = new javax.swing.JButton();
        tambahJabatan = new javax.swing.JPanel();
        panelKedua_masterJabatan_Admin = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        btn_simpan_masterJabatan = new javax.swing.JButton();
        btn_batal_masterJabatan1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jText_Jabatan = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jText_Gaji = new javax.swing.JTextField();
        editJabatan = new javax.swing.JPanel();
        panelKedua_masterJabatan_Admin1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        btn_simpan_masterJabatan1 = new javax.swing.JButton();
        btn_batal_masterJabatan2 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jText_Edit_Jabatan = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jText_Edit_Gaji = new javax.swing.JTextField();

        setPreferredSize(new java.awt.Dimension(900, 525));

        mainPanel.setLayout(new java.awt.CardLayout());

        table_dataJabatan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(118, 158, 169)));
        table_dataJabatan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Jabatan", "Gaji"
            }
        ));
        jScrollPane1.setViewportView(table_dataJabatan);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(2, 84, 106));
        jLabel4.setText("Data Jabatan");

        btn_tambah_masterJabatan.setText("TAMBAH");
        btn_tambah_masterJabatan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambah_masterJabatanActionPerformed(evt);
            }
        });

        btn_hapus_masterJabatan.setText("HAPUS");
        btn_hapus_masterJabatan.setToolTipText("");
        btn_hapus_masterJabatan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_hapus_masterJabatanActionPerformed(evt);
            }
        });

        btn_batal_masterJabatan.setText("BATAL");
        btn_batal_masterJabatan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_batal_masterJabatanActionPerformed(evt);
            }
        });

        btn_edit_masterJabatan.setText("EDIT");
        btn_edit_masterJabatan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_edit_masterJabatanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelUtama_masterJabatan_AdminLayout = new javax.swing.GroupLayout(panelUtama_masterJabatan_Admin);
        panelUtama_masterJabatan_Admin.setLayout(panelUtama_masterJabatan_AdminLayout);
        panelUtama_masterJabatan_AdminLayout.setHorizontalGroup(
            panelUtama_masterJabatan_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelUtama_masterJabatan_AdminLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelUtama_masterJabatan_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelUtama_masterJabatan_AdminLayout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelUtama_masterJabatan_AdminLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 870, Short.MAX_VALUE)
                        .addGap(12, 12, 12))
                    .addGroup(panelUtama_masterJabatan_AdminLayout.createSequentialGroup()
                        .addComponent(btn_tambah_masterJabatan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btn_edit_masterJabatan)
                        .addGap(12, 12, 12)
                        .addComponent(btn_batal_masterJabatan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btn_hapus_masterJabatan)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        panelUtama_masterJabatan_AdminLayout.setVerticalGroup(
            panelUtama_masterJabatan_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelUtama_masterJabatan_AdminLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addGap(37, 37, 37)
                .addGroup(panelUtama_masterJabatan_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_tambah_masterJabatan)
                    .addComponent(btn_hapus_masterJabatan)
                    .addComponent(btn_batal_masterJabatan)
                    .addComponent(btn_edit_masterJabatan))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(177, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout dataJabatanLayout = new javax.swing.GroupLayout(dataJabatan);
        dataJabatan.setLayout(dataJabatanLayout);
        dataJabatanLayout.setHorizontalGroup(
            dataJabatanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 900, Short.MAX_VALUE)
            .addGroup(dataJabatanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(dataJabatanLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panelUtama_masterJabatan_Admin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        dataJabatanLayout.setVerticalGroup(
            dataJabatanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 525, Short.MAX_VALUE)
            .addGroup(dataJabatanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(dataJabatanLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panelUtama_masterJabatan_Admin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        mainPanel.add(dataJabatan, "card2");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(2, 84, 106));
        jLabel5.setText("Tambah Data Jabatan");

        btn_simpan_masterJabatan.setText("SIMPAN");
        btn_simpan_masterJabatan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_simpan_masterJabatanActionPerformed(evt);
            }
        });

        btn_batal_masterJabatan1.setText("BATAL");
        btn_batal_masterJabatan1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_batal_masterJabatan1ActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Jabatan");

        jText_Jabatan.setText("jTextField1");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setText("Gaji");

        jText_Gaji.setText("jTextField1");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jText_Jabatan, javax.swing.GroupLayout.DEFAULT_SIZE, 845, Short.MAX_VALUE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel9)
                    .addComponent(jText_Gaji, javax.swing.GroupLayout.DEFAULT_SIZE, 845, Short.MAX_VALUE))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jText_Jabatan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jText_Gaji, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(277, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelKedua_masterJabatan_AdminLayout = new javax.swing.GroupLayout(panelKedua_masterJabatan_Admin);
        panelKedua_masterJabatan_Admin.setLayout(panelKedua_masterJabatan_AdminLayout);
        panelKedua_masterJabatan_AdminLayout.setHorizontalGroup(
            panelKedua_masterJabatan_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelKedua_masterJabatan_AdminLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelKedua_masterJabatan_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelKedua_masterJabatan_AdminLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(panelKedua_masterJabatan_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelKedua_masterJabatan_AdminLayout.createSequentialGroup()
                                .addComponent(btn_simpan_masterJabatan)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btn_batal_masterJabatan1))
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 500, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelKedua_masterJabatan_AdminLayout.setVerticalGroup(
            panelKedua_masterJabatan_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelKedua_masterJabatan_AdminLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addGap(37, 37, 37)
                .addGroup(panelKedua_masterJabatan_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_simpan_masterJabatan)
                    .addComponent(btn_batal_masterJabatan1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(12, 12, 12))
        );

        javax.swing.GroupLayout tambahJabatanLayout = new javax.swing.GroupLayout(tambahJabatan);
        tambahJabatan.setLayout(tambahJabatanLayout);
        tambahJabatanLayout.setHorizontalGroup(
            tambahJabatanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tambahJabatanLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelKedua_masterJabatan_Admin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        tambahJabatanLayout.setVerticalGroup(
            tambahJabatanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tambahJabatanLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelKedua_masterJabatan_Admin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        mainPanel.add(tambahJabatan, "card2");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(2, 84, 106));
        jLabel6.setText("Edit Data Jabatan");

        btn_simpan_masterJabatan1.setText("SIMPAN");
        btn_simpan_masterJabatan1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_simpan_masterJabatan1ActionPerformed(evt);
            }
        });

        btn_batal_masterJabatan2.setText("BATAL");
        btn_batal_masterJabatan2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_batal_masterJabatan2ActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Jabatan");

        jText_Edit_Jabatan.setText("jTextField1");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setText("Gaji");

        jText_Edit_Gaji.setText("jTextField1");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jText_Edit_Jabatan, javax.swing.GroupLayout.DEFAULT_SIZE, 845, Short.MAX_VALUE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel10)
                    .addComponent(jText_Edit_Gaji, javax.swing.GroupLayout.DEFAULT_SIZE, 845, Short.MAX_VALUE))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jText_Edit_Jabatan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jText_Edit_Gaji, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(277, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelKedua_masterJabatan_Admin1Layout = new javax.swing.GroupLayout(panelKedua_masterJabatan_Admin1);
        panelKedua_masterJabatan_Admin1.setLayout(panelKedua_masterJabatan_Admin1Layout);
        panelKedua_masterJabatan_Admin1Layout.setHorizontalGroup(
            panelKedua_masterJabatan_Admin1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelKedua_masterJabatan_Admin1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelKedua_masterJabatan_Admin1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelKedua_masterJabatan_Admin1Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(panelKedua_masterJabatan_Admin1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelKedua_masterJabatan_Admin1Layout.createSequentialGroup()
                                .addComponent(btn_simpan_masterJabatan1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btn_batal_masterJabatan2))
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 500, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelKedua_masterJabatan_Admin1Layout.setVerticalGroup(
            panelKedua_masterJabatan_Admin1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelKedua_masterJabatan_Admin1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addGap(37, 37, 37)
                .addGroup(panelKedua_masterJabatan_Admin1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_simpan_masterJabatan1)
                    .addComponent(btn_batal_masterJabatan2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(12, 12, 12))
        );

        javax.swing.GroupLayout editJabatanLayout = new javax.swing.GroupLayout(editJabatan);
        editJabatan.setLayout(editJabatanLayout);
        editJabatanLayout.setHorizontalGroup(
            editJabatanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editJabatanLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelKedua_masterJabatan_Admin1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        editJabatanLayout.setVerticalGroup(
            editJabatanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editJabatanLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelKedua_masterJabatan_Admin1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        mainPanel.add(editJabatan, "card2");

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
            .addGap(0, 525, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btn_simpan_masterJabatan1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_simpan_masterJabatan1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_simpan_masterJabatan1ActionPerformed

    private void btn_batal_masterJabatan2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_batal_masterJabatan2ActionPerformed
        mainPanel.removeAll();
        mainPanel.add(dataJabatan);
        mainPanel.repaint();
        mainPanel.revalidate();
    }//GEN-LAST:event_btn_batal_masterJabatan2ActionPerformed

    private void btn_edit_masterJabatanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_edit_masterJabatanActionPerformed
        mainPanel.removeAll();
        mainPanel.repaint();
        mainPanel.revalidate();

        mainPanel.add(editJabatan);
        mainPanel.repaint();
        mainPanel.revalidate();
    }//GEN-LAST:event_btn_edit_masterJabatanActionPerformed

    private void btn_tambah_masterJabatanActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btn_tambah_masterJabatanActionPerformed
        mainPanel.removeAll();
        mainPanel.repaint();
        mainPanel.revalidate();

        mainPanel.add(tambahJabatan);
        mainPanel.repaint();
        mainPanel.revalidate();
    }// GEN-LAST:event_btn_tambah_masterJabatanActionPerformed

    private void btn_hapus_masterJabatanActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btn_hapus_masterJabatanActionPerformed
        // TODO add your handling code here:
        hapusJabatan();
        initializeComponents();
    }// GEN-LAST:event_btn_hapus_masterJabatanActionPerformed

    private void btn_batal_masterJabatanActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btn_batal_masterJabatanActionPerformed
        // Cancel selected action and return to the main panel
        mainPanel.removeAll();
        mainPanel.add(dataJabatan);
        mainPanel.repaint();
        mainPanel.revalidate();
        resetForm();
    }// GEN-LAST:event_btn_batal_masterJabatanActionPerformed

    private void btn_simpan_masterJabatanActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btn_simpan_masterJabatanActionPerformed
        // TODO add your handling code here:
        if (jText_Jabatan.getText().isEmpty() || jText_Gaji.getText().isEmpty()) {
            System.out.println("Nama Jabatan dan Gaji Pokok tidak boleh kosong.");
            return;
        }

        if (table_dataJabatan.getSelectedRow() != -1) {
            editJabatan();
        } else {
            tambahJabatan();
        }

        initializeComponents();
    }// GEN-LAST:event_btn_simpan_masterJabatanActionPerformed

    private void btn_batal_masterJabatan1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btn_batal_masterPegawai2ActionPerformed
        mainPanel.removeAll();
        mainPanel.add(dataJabatan);
        mainPanel.repaint();
        mainPanel.revalidate();
    }// GEN-LAST:event_btn_batal_masterPegawai2ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_batal_masterJabatan;
    private javax.swing.JButton btn_batal_masterJabatan1;
    private javax.swing.JButton btn_batal_masterJabatan2;
    private javax.swing.JButton btn_edit_masterJabatan;
    private javax.swing.JButton btn_hapus_masterJabatan;
    private javax.swing.JButton btn_simpan_masterJabatan;
    private javax.swing.JButton btn_simpan_masterJabatan1;
    private javax.swing.JButton btn_tambah_masterJabatan;
    private javax.swing.JPanel dataJabatan;
    private javax.swing.JPanel editJabatan;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jText_Edit_Gaji;
    private javax.swing.JTextField jText_Edit_Jabatan;
    private javax.swing.JTextField jText_Gaji;
    private javax.swing.JTextField jText_Jabatan;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel panelKedua_masterJabatan_Admin;
    private javax.swing.JPanel panelKedua_masterJabatan_Admin1;
    private javax.swing.JPanel panelUtama_masterJabatan_Admin;
    private javax.swing.JTable table_dataJabatan;
    private javax.swing.JPanel tambahJabatan;
    // End of variables declaration//GEN-END:variables
}
