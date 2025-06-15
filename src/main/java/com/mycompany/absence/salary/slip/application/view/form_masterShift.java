/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.absence.salary.slip.application.view;

import java.time.LocalTime;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import com.mycompany.absence.salary.slip.application.models.Shift;
import com.mycompany.absence.salary.slip.application.repositories.ShiftRepository;
import com.mycompany.absence.salary.slip.application.utils.Response;

/**
 *
 * @author User
 */
public class form_masterShift extends javax.swing.JPanel {

    /**
     * Creates new form form_masterJabatan
     */
    public form_masterShift() {
        initComponents();
        initializeComponents();
    }

    ShiftRepository shiftRepository = new ShiftRepository();

    private void initializeComponents() {
        mainPanel.removeAll();
        mainPanel.repaint();
        mainPanel.revalidate();

        mainPanel.add(dataShift);
        mainPanel.repaint();
        mainPanel.revalidate();

        btn_edit_masterShift.setEnabled(false);
        btn_hapus_masterShift.setEnabled(false);

        populateTableShift();
        clearInputFields();

        // Add listener to enable/disable buttons based on selection
        table_dataShift.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                if (table_dataShift.getSelectedRow() >= 0) {
                    btn_edit_masterShift.setEnabled(true);
                    btn_hapus_masterShift.setEnabled(true);
                } else {
                    btn_edit_masterShift.setEnabled(false);
                    btn_hapus_masterShift.setEnabled(false);
                    clearForm();
                }
            }
        });
    }
    
    /**
     * Method untuk mengisi form edit shift dari data yang dipilih di tabel.
     * Mengambil data dari tabel dan mengisi otomatis form edit shift.
     */
    private void isiFormDariTabel() {
        int selectedRow = table_dataShift.getSelectedRow();
        if (selectedRow >= 0) {
            int shiftId = (int) table_dataShift.getValueAt(selectedRow, 0);
            Shift shift = shiftRepository.findById(shiftId).getData();

            jText_namaShift1.setText(shift.getNamaShift());
            jText_jamMasuk1.setText(shift.getJamMasuk().toString());
            jText_jamKeluar1.setText(shift.getJamKeluar().toString());

            mainPanel.removeAll();
            mainPanel.repaint();
            mainPanel.revalidate();

            mainPanel.add(editShift);
            mainPanel.repaint();
            mainPanel.revalidate();
        } else {
            JOptionPane.showMessageDialog(this, "Silakan pilih shift yang ingin diedit");
            clearForm();
            mainPanel.removeAll();
            mainPanel.add(dataShift);
            mainPanel.repaint();
            mainPanel.revalidate();
            btn_edit_masterShift.setEnabled(false);
            btn_hapus_masterShift.setEnabled(false);
            return; // No row selected, exit the method
        }
    }

    /**
     * Method untuk mengedit shift yang dipilih dari tabel.
     * Mengambil data dari form edit dan mengirimkan ke repository untuk diperbarui.
     */
    private void editShift() {
        int selectedRow = table_dataShift.getSelectedRow();
        if (selectedRow >= 0) {
            int shiftId = (int) table_dataShift.getValueAt(selectedRow, 0);
            String namaShift = jText_namaShift1.getText();
            String jamMasuk = jText_jamMasuk1.getText();
            String jamKeluar = jText_jamKeluar1.getText();

            // Konversi dari String ke LocalTime jika diperlukan
            LocalTime jamMasukTime = LocalTime.parse(jamMasuk);
            LocalTime jamKeluarTime = LocalTime.parse(jamKeluar);

            Shift shift = new Shift(namaShift, jamMasukTime, jamKeluarTime);
            shift.setId(shiftId); // Set the ID if a setter is available
            Response<Shift> response = shiftRepository.update(shift);

            if (response.isSuccess()) {
                JOptionPane.showMessageDialog(this, "Shift berhasil diperbarui");
                clearForm();
                initializeComponents();
            } else {
                JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat memperbarui shift: " + response.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Silakan pilih shift yang ingin diedit");
        }
    }

    /**
     * Method untuk menghapus shift yang dipilih dari tabel.
     * Menggunakan konfirmasi sebelum menghapus.
     */
    private void hapusShift() {
        int selectedRow = table_dataShift.getSelectedRow();
        if (selectedRow >= 0) {
            int confirmation = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus shift ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            if (confirmation != JOptionPane.YES_OPTION) {
                return; // User chose not to delete
            }

            int shiftId = (int) table_dataShift.getValueAt(selectedRow, 0);
            Response<Boolean> response = shiftRepository.deleteById(shiftId);

            if (response.isSuccess()) {
                JOptionPane.showMessageDialog(this, "Shift berhasil dihapus");
                populateTableShift();
            } else {
                JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat menghapus shift: " + response.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Silakan pilih shift yang ingin dihapus");
        }
    }

    private void tambahShift() {
        String namaShift = jText_namaShift.getText();
        String jamMasuk = jText_jamMasuk.getText();
        String jamKeluar = jText_jamKeluar.getText();

        // Konversi dari String ke LocalTime jika diperlukan
        LocalTime jamMasukTime = LocalTime.parse(jamMasuk);
        LocalTime jamKeluarTime = LocalTime.parse(jamKeluar);

        Shift shift = new Shift(namaShift, jamMasukTime, jamKeluarTime);
        Response<Shift> response = shiftRepository.save(shift);
        if (response.isSuccess()) {
            JOptionPane.showMessageDialog(this, "Shift berhasil ditambahkan");
            clearInputFields();
            initializeComponents();
        } else {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat menambahkan shift: " + response.getMessage());
        }
    }

    private void clearInputFields() {
        jText_namaShift.setText("");
        jText_jamMasuk.setText("");
        jText_jamKeluar.setText("");
    }

    private void populateTableShift() {
        String[] columnNames = { "Id", "Nama Shift", "Jam Masuk", "Jam Keluar"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Prevent editing of cells
            }
        };

        table_dataShift.setModel(model);

        Response<ArrayList<Shift>> response = shiftRepository.findAll();
        if (response.isSuccess()) {
            ArrayList<Shift> shifts = response.getData();
            for (Shift shift : shifts) {
                Object[] row = {shift.getId(), shift.getNamaShift(), shift.getJamMasuk(), shift.getJamKeluar()};
                model.addRow(row);
            }
        } else {
            System.out.println("Error: " + response.getMessage());
        }

        // Sembunyikan kolom pertama (ID)
        table_dataShift.getColumnModel().getColumn(0).setMinWidth(0);
        table_dataShift.getColumnModel().getColumn(0).setMaxWidth(0);
        table_dataShift.getColumnModel().getColumn(0).setWidth(0);
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
        dataShift = new javax.swing.JPanel();
        panelUtama_masterShift_Admin = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_dataShift = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        btn_tambah_masterShift = new javax.swing.JButton();
        btn_hapus_masterShift = new javax.swing.JButton();
        btn_edit_masterShift = new javax.swing.JButton();
        tambahShift = new javax.swing.JPanel();
        panelKedua_masterShift_Admin = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        btn_simpan_masterShift = new javax.swing.JButton();
        btn_batal_masterShift1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jText_namaShift = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jText_jamMasuk = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jText_jamKeluar = new javax.swing.JTextField();
        editShift = new javax.swing.JPanel();
        panelKetiga_masterShift_Admin = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        btn_simpan_masterShift1 = new javax.swing.JButton();
        btn_batal_masterShift2 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jText_namaShift1 = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jText_jamMasuk1 = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jText_jamKeluar1 = new javax.swing.JTextField();

        setPreferredSize(new java.awt.Dimension(900, 525));

        mainPanel.setLayout(new java.awt.CardLayout());

        table_dataShift.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(118, 158, 169)));
        table_dataShift.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Nama Shift", "Jam Masuk", "Jam Keluar"
            }
        ));
        jScrollPane1.setViewportView(table_dataShift);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(2, 84, 106));
        jLabel4.setText("Data Shift");

        btn_tambah_masterShift.setText("TAMBAH");
        btn_tambah_masterShift.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambah_masterShiftActionPerformed(evt);
            }
        });

        btn_hapus_masterShift.setText("HAPUS");
        btn_hapus_masterShift.setToolTipText("");
        btn_hapus_masterShift.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_hapus_masterShiftActionPerformed(evt);
            }
        });

        btn_edit_masterShift.setText("EDIT");
        btn_edit_masterShift.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_edit_masterShiftActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelUtama_masterShift_AdminLayout = new javax.swing.GroupLayout(panelUtama_masterShift_Admin);
        panelUtama_masterShift_Admin.setLayout(panelUtama_masterShift_AdminLayout);
        panelUtama_masterShift_AdminLayout.setHorizontalGroup(
            panelUtama_masterShift_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelUtama_masterShift_AdminLayout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(panelUtama_masterShift_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelUtama_masterShift_AdminLayout.createSequentialGroup()
                        .addComponent(btn_tambah_masterShift)
                        .addGap(12, 12, 12)
                        .addComponent(btn_edit_masterShift)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btn_hapus_masterShift))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 872, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        panelUtama_masterShift_AdminLayout.setVerticalGroup(
            panelUtama_masterShift_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelUtama_masterShift_AdminLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addGap(37, 37, 37)
                .addGroup(panelUtama_masterShift_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_tambah_masterShift)
                    .addComponent(btn_hapus_masterShift)
                    .addComponent(btn_edit_masterShift))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(235, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout dataShiftLayout = new javax.swing.GroupLayout(dataShift);
        dataShift.setLayout(dataShiftLayout);
        dataShiftLayout.setHorizontalGroup(
            dataShiftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 903, Short.MAX_VALUE)
            .addGroup(dataShiftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(panelUtama_masterShift_Admin, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        dataShiftLayout.setVerticalGroup(
            dataShiftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 591, Short.MAX_VALUE)
            .addGroup(dataShiftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(panelUtama_masterShift_Admin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        mainPanel.add(dataShift, "card2");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(2, 84, 106));
        jLabel5.setText("Tambah Data Shift");

        btn_simpan_masterShift.setText("SIMPAN");
        btn_simpan_masterShift.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_simpan_masterShiftActionPerformed(evt);
            }
        });

        btn_batal_masterShift1.setText("BATAL");
        btn_batal_masterShift1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_batal_masterShift1ActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Nama Shift");

        jText_namaShift.setText("jTextField1");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setText("Jam Masuk (00:00)");

        jText_jamMasuk.setText("jTextField1");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setText("Jam Keluar (00:00)");

        jText_jamKeluar.setText("jTextField1");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jText_namaShift, javax.swing.GroupLayout.DEFAULT_SIZE, 845, Short.MAX_VALUE)
                    .addComponent(jLabel1)
                    .addComponent(jText_jamMasuk, javax.swing.GroupLayout.DEFAULT_SIZE, 845, Short.MAX_VALUE)
                    .addComponent(jLabel10)
                    .addComponent(jText_jamKeluar, javax.swing.GroupLayout.DEFAULT_SIZE, 845, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jText_namaShift, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jText_jamMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jText_jamKeluar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(277, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelKedua_masterShift_AdminLayout = new javax.swing.GroupLayout(panelKedua_masterShift_Admin);
        panelKedua_masterShift_Admin.setLayout(panelKedua_masterShift_AdminLayout);
        panelKedua_masterShift_AdminLayout.setHorizontalGroup(
            panelKedua_masterShift_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelKedua_masterShift_AdminLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelKedua_masterShift_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelKedua_masterShift_AdminLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(panelKedua_masterShift_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelKedua_masterShift_AdminLayout.createSequentialGroup()
                                .addComponent(btn_simpan_masterShift)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btn_batal_masterShift1))
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 503, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelKedua_masterShift_AdminLayout.setVerticalGroup(
            panelKedua_masterShift_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelKedua_masterShift_AdminLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addGap(37, 37, 37)
                .addGroup(panelKedua_masterShift_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_simpan_masterShift)
                    .addComponent(btn_batal_masterShift1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(12, 12, 12))
        );

        javax.swing.GroupLayout tambahShiftLayout = new javax.swing.GroupLayout(tambahShift);
        tambahShift.setLayout(tambahShiftLayout);
        tambahShiftLayout.setHorizontalGroup(
            tambahShiftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tambahShiftLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelKedua_masterShift_Admin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        tambahShiftLayout.setVerticalGroup(
            tambahShiftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tambahShiftLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelKedua_masterShift_Admin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        mainPanel.add(tambahShift, "card2");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(2, 84, 106));
        jLabel6.setText("Edit Data Shift");

        btn_simpan_masterShift1.setText("SIMPAN");
        btn_simpan_masterShift1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_simpan_masterShift1ActionPerformed(evt);
            }
        });

        btn_batal_masterShift2.setText("BATAL");
        btn_batal_masterShift2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_batal_masterShift2ActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Nama Shift");

        jText_namaShift1.setText("jTextField1");

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setText("Jam Masuk");

        jText_jamMasuk1.setText("jTextField1");

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel12.setText("Jam Keluar");

        jText_jamKeluar1.setText("jTextField1");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jText_namaShift1, javax.swing.GroupLayout.DEFAULT_SIZE, 845, Short.MAX_VALUE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel11)
                    .addComponent(jText_jamMasuk1, javax.swing.GroupLayout.DEFAULT_SIZE, 845, Short.MAX_VALUE)
                    .addComponent(jLabel12)
                    .addComponent(jText_jamKeluar1, javax.swing.GroupLayout.DEFAULT_SIZE, 845, Short.MAX_VALUE))
                .addContainerGap(19, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jText_namaShift1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jText_jamMasuk1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jText_jamKeluar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(277, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelKetiga_masterShift_AdminLayout = new javax.swing.GroupLayout(panelKetiga_masterShift_Admin);
        panelKetiga_masterShift_Admin.setLayout(panelKetiga_masterShift_AdminLayout);
        panelKetiga_masterShift_AdminLayout.setHorizontalGroup(
            panelKetiga_masterShift_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelKetiga_masterShift_AdminLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelKetiga_masterShift_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelKetiga_masterShift_AdminLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(panelKetiga_masterShift_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelKetiga_masterShift_AdminLayout.createSequentialGroup()
                                .addComponent(btn_simpan_masterShift1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btn_batal_masterShift2))
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 503, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelKetiga_masterShift_AdminLayout.setVerticalGroup(
            panelKetiga_masterShift_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelKetiga_masterShift_AdminLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addGap(37, 37, 37)
                .addGroup(panelKetiga_masterShift_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_simpan_masterShift1)
                    .addComponent(btn_batal_masterShift2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(12, 12, 12))
        );

        javax.swing.GroupLayout editShiftLayout = new javax.swing.GroupLayout(editShift);
        editShift.setLayout(editShiftLayout);
        editShiftLayout.setHorizontalGroup(
            editShiftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editShiftLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelKetiga_masterShift_Admin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        editShiftLayout.setVerticalGroup(
            editShiftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editShiftLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelKetiga_masterShift_Admin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        mainPanel.add(editShift, "card2");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 903, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 591, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btn_tambah_masterShiftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambah_masterShiftActionPerformed
        mainPanel.removeAll();
        mainPanel.repaint();
        mainPanel.revalidate();

        mainPanel.add(tambahShift);
        mainPanel.repaint();
        mainPanel.revalidate();
    }//GEN-LAST:event_btn_tambah_masterShiftActionPerformed

    private void btn_hapus_masterShiftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hapus_masterShiftActionPerformed
        hapusShift();
        initializeComponents();
    }//GEN-LAST:event_btn_hapus_masterShiftActionPerformed

    private void btn_simpan_masterShiftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_simpan_masterShiftActionPerformed
        // Validasi field tidak boleh kosong
        String namaShift = jText_namaShift.getText().trim();
        String jamMasuk = jText_jamMasuk.getText().trim();
        String jamKeluar = jText_jamKeluar.getText().trim();

        if (namaShift.isEmpty() || jamMasuk.isEmpty() || jamKeluar.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Shift, Jam Masuk, dan Jam Keluar tidak boleh kosong.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validasi format jam masuk dan jam keluar
        try {
            LocalTime.parse(jamMasuk);
            LocalTime.parse(jamKeluar);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Format Jam Masuk dan Jam Keluar harus benar (misal: 08:00).", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        tambahShift();
        
        initializeComponents();
    }//GEN-LAST:event_btn_simpan_masterShiftActionPerformed

    private void btn_batal_masterShift1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_batal_masterShift1ActionPerformed
        mainPanel.removeAll();
        mainPanel.add(dataShift);
        mainPanel.repaint();
        mainPanel.revalidate();
    }//GEN-LAST:event_btn_batal_masterShift1ActionPerformed

    private void btn_simpan_masterShift1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_simpan_masterShift1ActionPerformed
        // TODO add your handling code here:
        editShift();
        mainPanel.removeAll();
        mainPanel.add(dataShift);
        mainPanel.repaint();
        mainPanel.revalidate();
        populateTableShift();
    }//GEN-LAST:event_btn_simpan_masterShift1ActionPerformed

    private void btn_batal_masterShift2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_batal_masterShift2ActionPerformed
        mainPanel.removeAll();
        mainPanel.add(dataShift);
        mainPanel.repaint();
        mainPanel.revalidate();
    }//GEN-LAST:event_btn_batal_masterShift2ActionPerformed

    private void btn_edit_masterShiftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_edit_masterShiftActionPerformed
        mainPanel.removeAll();
        mainPanel.repaint();
        mainPanel.revalidate();

        mainPanel.add(editShift);
        mainPanel.repaint();
        mainPanel.revalidate();
        isiFormDariTabel();
    }//GEN-LAST:event_btn_edit_masterShiftActionPerformed


    /**
     * Method untuk membersihkan form edit shift.
     */
    private void clearForm() {
        jText_namaShift1.setText("");
        jText_jamMasuk1.setText("");
        jText_jamKeluar1.setText("");
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_batal_masterShift1;
    private javax.swing.JButton btn_batal_masterShift2;
    private javax.swing.JButton btn_edit_masterShift;
    private javax.swing.JButton btn_hapus_masterShift;
    private javax.swing.JButton btn_simpan_masterShift;
    private javax.swing.JButton btn_simpan_masterShift1;
    private javax.swing.JButton btn_tambah_masterShift;
    private javax.swing.JPanel dataShift;
    private javax.swing.JPanel editShift;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jText_jamKeluar;
    private javax.swing.JTextField jText_jamKeluar1;
    private javax.swing.JTextField jText_jamMasuk;
    private javax.swing.JTextField jText_jamMasuk1;
    private javax.swing.JTextField jText_namaShift;
    private javax.swing.JTextField jText_namaShift1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel panelKedua_masterShift_Admin;
    private javax.swing.JPanel panelKetiga_masterShift_Admin;
    private javax.swing.JPanel panelUtama_masterShift_Admin;
    private javax.swing.JTable table_dataShift;
    private javax.swing.JPanel tambahShift;
    // End of variables declaration//GEN-END:variables
}
