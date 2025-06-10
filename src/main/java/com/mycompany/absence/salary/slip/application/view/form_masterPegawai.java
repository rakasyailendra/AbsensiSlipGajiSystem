/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.absence.salary.slip.application.view;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import com.mycompany.absence.salary.slip.application.models.ComboItem;
import com.mycompany.absence.salary.slip.application.models.Jabatan;
import com.mycompany.absence.salary.slip.application.models.JabatanPegawai;
import com.mycompany.absence.salary.slip.application.models.Pegawai;
import com.mycompany.absence.salary.slip.application.repositories.JabatanPegawaiRepository;
import com.mycompany.absence.salary.slip.application.repositories.JabatanRepository;
import com.mycompany.absence.salary.slip.application.repositories.PegawaiRepository;
import com.mycompany.absence.salary.slip.application.utils.Response;

/**
 *
 * @author User
 */
public class form_masterPegawai extends javax.swing.JPanel {

    /**
     * Creates new form form_dataPegawai
     */
    public form_masterPegawai() {
        initComponents();
        initializeComponents();
    }

    PegawaiRepository pegawaiRepository = new PegawaiRepository();
    JabatanPegawaiRepository jabatanPegawaiRepository = new JabatanPegawaiRepository();
    JabatanRepository jabatanRepository = new JabatanRepository();
    private Pegawai pegawaiYangDipilih = null;

    private void initializeComponents() {
        populateTablePegawai();
        populateJabatanComboBox();
        clearForm();

        table_absensiPegawaiHariini.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                isiFormDariTabel();
            }
        });

    }

    private void populateTablePegawai() {
        Response<ArrayList<Pegawai>> response = pegawaiRepository.findAll();
        if (response.isSuccess()) {
            ArrayList<Pegawai> pegawaiList = response.getData();

            String[] columnNames = { "NIP", "Nama", "Tanggal Lahir", "Alamat", "Jabatan" };
            DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Mencegah pengeditan sel
                }
            };
            table_absensiPegawaiHariini.setModel(model);

            for (Pegawai pegawai : pegawaiList) {
                Response<ArrayList<JabatanPegawai>> jabatanPegawaiResponse = jabatanPegawaiRepository
                        .findByPegawaiId(pegawai.getId());

                if (jabatanPegawaiResponse.isSuccess() && !jabatanPegawaiResponse.getData().isEmpty()) {
                    JabatanPegawai jp = jabatanPegawaiResponse.getData().get(0);

                    Response<Jabatan> jabatanResponse = jabatanRepository.findById(jp.getIdJabatan());
                    if (jabatanResponse.isSuccess()) {
                        Jabatan jabatan = jabatanResponse.getData();

                        model.addRow(new Object[] {
                                pegawai.getNip(),
                                pegawai.getNama(),
                                pegawai.getTanggalLahir(),
                                pegawai.getAlamat(),
                                jabatan.getNamaJabatan()
                        });
                    }
                }
            }
        } else {
            System.out.println("Error fetching data: " + response.getMessage());
        }
    }

    private void isiFormDariTabel() {
        int selectedRow = table_absensiPegawaiHariini.getSelectedRow();
        if (selectedRow >= 0) {
            String nip = table_absensiPegawaiHariini.getValueAt(selectedRow, 0).toString();
            Response<Pegawai> response = pegawaiRepository.findByNip(nip);
            if (response.isSuccess()) {
                Pegawai pegawai = response.getData();

                jText_NIP.setText(pegawai.getNip());
                jText_nama.setText(pegawai.getNama());
                jText_tanggalLahir.setText(pegawai.getTanggalLahir().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                jText_alamat.setText(pegawai.getAlamat());
                jText_password.setText(pegawai.getPassword());

                // Cari dan set jabatan pada combo box
                Response<ArrayList<JabatanPegawai>> jabatanPegawaiResponse = jabatanPegawaiRepository
                        .findByPegawaiId(pegawai.getId());
                if (jabatanPegawaiResponse.isSuccess() && !jabatanPegawaiResponse.getData().isEmpty()) {
                    Integer idJabatan = jabatanPegawaiResponse.getData().get(0).getIdJabatan();
                    for (int i = 0; i < jCombo_jabatan.getItemCount(); i++) {
                        ComboItem item = (ComboItem) jCombo_jabatan.getItemAt(i);
                        if (item.getId() != null && item.getId().equals(idJabatan)) {
                            jCombo_jabatan.setSelectedIndex(i);
                            break;
                        }
                    }
                }

                // Simpan objek pegawai terpilih ke variabel global jika diperlukan
                pegawaiYangDipilih = pegawai;
            } else {
                System.out.println("Gagal mengambil data pegawai: " + response.getMessage());
            }
        }
    }

    private void hapusPegawai() {
        int selectedRow = table_absensiPegawaiHariini.getSelectedRow();
        if (selectedRow >= 0) {
            String nip = table_absensiPegawaiHariini.getValueAt(selectedRow, 0).toString();
            Response<Pegawai> response = pegawaiRepository.findByNip(nip);
            if (response.isSuccess()) {
                int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus pegawai ini?",
                        "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    Pegawai pegawai = response.getData();
                    Response<ArrayList<JabatanPegawai>> jabatanPegawaiResponse = jabatanPegawaiRepository
                            .findByPegawaiId(pegawai.getId());
                    if (jabatanPegawaiResponse.isSuccess() && !jabatanPegawaiResponse.getData().isEmpty()) {
                        JabatanPegawai jabatanPegawai = jabatanPegawaiResponse.getData().get(0);
                        // Hapus relasi JabatanPegawai terlebih dahulu
                        jabatanPegawaiRepository.deleteById(jabatanPegawai.getId());
                    }
                    Response<Boolean> deleteResponse = pegawaiRepository.deleteById(pegawai.getId());
                    if (deleteResponse.isSuccess()) {
                        System.out.println("Pegawai berhasil dihapus.");
                        populateTablePegawai();
                    } else {
                        System.out.println("Gagal menghapus pegawai: " + deleteResponse.getMessage());
                    }
                } else {
                    System.out.println("Gagal mengambil data pegawai: " + response.getMessage());
                }
            }
        } else {
            System.out.println("Tidak ada pegawai yang dipilih untuk dihapus.");
        }
    }

    private void editPegawai() {
        if (pegawaiYangDipilih == null) {
            System.out.println("Tidak ada pegawai yang dipilih.");
            return;
        }

        String nama = jText_nama.getText();
        String tanggalLahir = jText_tanggalLahir.getText();
        String alamat = jText_alamat.getText();
        ComboItem selectedJabatan = (ComboItem) jCombo_jabatan.getSelectedItem();
        Integer idJabatan = selectedJabatan != null ? selectedJabatan.getId() : null;
        String password = jText_password.getText();

        if (nama.isEmpty() || tanggalLahir.isEmpty() || alamat.isEmpty() || idJabatan == null || password.isEmpty()) {
            System.out.println("Semua field harus diisi.");
            return;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate parsedTanggal = LocalDate.parse(tanggalLahir, formatter);

            pegawaiYangDipilih.setId(pegawaiYangDipilih.getId()); // Pastikan ID tetap sama
            pegawaiYangDipilih.setNama(nama);
            pegawaiYangDipilih.setTanggalLahir(parsedTanggal);
            pegawaiYangDipilih.setAlamat(alamat);
            pegawaiYangDipilih.setPassword(password);

            Response<Pegawai> updateResponse = pegawaiRepository.update(pegawaiYangDipilih);
            if (updateResponse.isSuccess()) {
                // Perbarui relasi jabatan
                Response<ArrayList<JabatanPegawai>> existing = jabatanPegawaiRepository
                        .findByPegawaiId(pegawaiYangDipilih.getId());
                if (existing.isSuccess() && !existing.getData().isEmpty()) {
                    JabatanPegawai jp = existing.getData().get(0);
                    jp.setIdJabatan(idJabatan);
                    jabatanPegawaiRepository.update(jp);
                }

                System.out.println("Pegawai berhasil diperbarui.");
                pegawaiYangDipilih = null;
                populateTablePegawai();
                clearForm();
            } else {
                System.out.println("Gagal update pegawai: " + updateResponse.getMessage());
            }
        } catch (DateTimeParseException e) {
            System.out.println("Format tanggal tidak valid. Gunakan format dd-MM-yyyy.");
        }
    }

    private void tambahPegawai() {
        String nip = jText_NIP.getText();
        String nama = jText_nama.getText();
        String tanggalLahir = jText_tanggalLahir.getText();
        String alamat = jText_alamat.getText();
        ComboItem selectedJabatan = (ComboItem) jCombo_jabatan.getSelectedItem();
        Integer idJabatan = selectedJabatan != null ? selectedJabatan.getId() : null;
        String password = jText_password.getText();

        if (nip.isEmpty() || nama.isEmpty() || tanggalLahir.isEmpty() || alamat.isEmpty() || idJabatan == null
                || password.isEmpty()) {
            System.out.println("Semua field harus diisi.");
            return;
        }

        LocalDate parsedTanggalLahir;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            parsedTanggalLahir = LocalDate.parse(tanggalLahir, formatter);
        } catch (DateTimeParseException e) {
            System.out.println("Format tanggal tidak valid. Gunakan format dd-MM-yyyy.");
            return;
        }

        Pegawai pegawai = new Pegawai();
        pegawai.setNip(nip);
        pegawai.setNama(nama);
        pegawai.setTanggalLahir(parsedTanggalLahir);
        pegawai.setAlamat(alamat);
        pegawai.setPassword(password);
        pegawai.setIsAdmin(false); // Default non-admin

        Response<Pegawai> response = pegawaiRepository.save(pegawai);
        if (response.isSuccess()) {
            // Simpan relasi ke JabatanPegawai
            JabatanPegawai jabatanPegawai = new JabatanPegawai();
            jabatanPegawai.setIdPegawai(response.getData().getId());
            jabatanPegawai.setIdJabatan(idJabatan);
            jabatanPegawaiRepository.save(jabatanPegawai);

            System.out.println("Pegawai berhasil ditambahkan.");
            populateTablePegawai();
            clearForm();
        } else {
            System.out.println("Gagal menambahkan Pegawai: " + response.getMessage());
        }
    }

    private void populateJabatanComboBox() {
        Response<ArrayList<Jabatan>> response = jabatanRepository.findAll();
        if (response.isSuccess()) {
            ArrayList<Jabatan> jabatanList = response.getData();
            jCombo_jabatan.removeAllItems(); // Clear existing items
            jCombo_jabatan.addItem(new ComboItem(null, "")); // Add default item
            for (Jabatan jabatan : jabatanList) {
                jCombo_jabatan.addItem(new ComboItem(jabatan.getId(), jabatan.getNamaJabatan()));
            }
        } else {
            System.out.println("Error fetching Jabatan data: " + response.getMessage());
        }
    }

    private void clearForm() {
        jText_NIP.setText("");
        jText_nama.setText("");
        jText_tanggalLahir.setText("");
        jText_alamat.setText("");
        jCombo_jabatan.setSelectedIndex(0); // Reset to default item
        jText_password.setText("");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        dataPegawai = new javax.swing.JPanel();
        panelUtama_masterPegawai_Admin = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_absensiPegawaiHariini = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        btn_tambah_masterPegawai = new javax.swing.JButton();
        btn_hapus_masterPegawai = new javax.swing.JButton();
        btn_batal_masterPegawai = new javax.swing.JButton();
        tambahPegawai = new javax.swing.JPanel();
        panelKedua_masterPegawai_Admin1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        btn_simpan_masterPegawai1 = new javax.swing.JButton();
        btn_batal_masterPegawai2 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jText_NIP = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jText_tanggalLahir = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jText_nama = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jText_alamat = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jCombo_jabatan = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        jText_password = new javax.swing.JTextField();

        setPreferredSize(new java.awt.Dimension(900, 525));
        setLayout(new java.awt.CardLayout());

        mainPanel.setLayout(new java.awt.CardLayout());

        table_absensiPegawaiHariini
                .setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(118, 158, 169)));
        table_absensiPegawaiHariini.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null }
                },
                new String[] {
                        "NIP", "Nama", "Tanggal Lahir", "Alamat", "Jabatan"
                }));
        jScrollPane1.setViewportView(table_absensiPegawaiHariini);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(2, 84, 106));
        jLabel4.setText("Data Pegawai");

        btn_tambah_masterPegawai.setText("TAMBAH");
        btn_tambah_masterPegawai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambah_masterPegawaiActionPerformed(evt);
            }
        });

        btn_hapus_masterPegawai.setText("HAPUS");
        btn_hapus_masterPegawai.setToolTipText("");
        btn_hapus_masterPegawai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_hapus_masterPegawaiActionPerformed(evt);
            }
        });

        btn_batal_masterPegawai.setText("BATAL");
        btn_batal_masterPegawai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_batal_masterPegawaiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelUtama_masterPegawai_AdminLayout = new javax.swing.GroupLayout(
                panelUtama_masterPegawai_Admin);
        panelUtama_masterPegawai_Admin.setLayout(panelUtama_masterPegawai_AdminLayout);
        panelUtama_masterPegawai_AdminLayout.setHorizontalGroup(
                panelUtama_masterPegawai_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelUtama_masterPegawai_AdminLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(panelUtama_masterPegawai_AdminLayout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(panelUtama_masterPegawai_AdminLayout.createSequentialGroup()
                                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 362,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(panelUtama_masterPegawai_AdminLayout.createSequentialGroup()
                                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 870,
                                                        Short.MAX_VALUE)
                                                .addGap(12, 12, 12))
                                        .addGroup(panelUtama_masterPegawai_AdminLayout.createSequentialGroup()
                                                .addComponent(btn_tambah_masterPegawai)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(btn_hapus_masterPegawai)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(btn_batal_masterPegawai)
                                                .addGap(0, 0, Short.MAX_VALUE)))));
        panelUtama_masterPegawai_AdminLayout.setVerticalGroup(
                panelUtama_masterPegawai_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelUtama_masterPegawai_AdminLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel4)
                                .addGap(37, 37, 37)
                                .addGroup(panelUtama_masterPegawai_AdminLayout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btn_tambah_masterPegawai)
                                        .addComponent(btn_hapus_masterPegawai)
                                        .addComponent(btn_batal_masterPegawai))
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 220,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(177, Short.MAX_VALUE)));

        btn_hapus_masterPegawai.getAccessibleContext().setAccessibleName("btn_hapusPegawai");

        javax.swing.GroupLayout dataPegawaiLayout = new javax.swing.GroupLayout(dataPegawai);
        dataPegawai.setLayout(dataPegawaiLayout);
        dataPegawaiLayout.setHorizontalGroup(
                dataPegawaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 900, Short.MAX_VALUE)
                        .addGroup(dataPegawaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(dataPegawaiLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(panelUtama_masterPegawai_Admin,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addContainerGap())));
        dataPegawaiLayout.setVerticalGroup(
                dataPegawaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 525, Short.MAX_VALUE)
                        .addGroup(dataPegawaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(dataPegawaiLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(panelUtama_masterPegawai_Admin,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addContainerGap())));

        mainPanel.add(dataPegawai, "card2");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(2, 84, 106));
        jLabel5.setText("Tambah Data Pegawai");

        btn_simpan_masterPegawai1.setText("SIMPAN");
        btn_simpan_masterPegawai1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_simpan_masterPegawai1ActionPerformed(evt);
            }
        });

        btn_batal_masterPegawai2.setText("BATAL");
        btn_batal_masterPegawai2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_batal_masterPegawai2ActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("NIP");

        jText_NIP.setText("jTextField1");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText("Tanggal Lahir");

        jText_tanggalLahir.setText("jTextField1");
        jText_tanggalLahir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jText_tanggalLahirActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setText("Nama");

        jText_nama.setText("jTextField1");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setText("Alamat");

        jText_alamat.setText("jTextField1");
        jText_alamat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jText_alamatActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setText("Jabatan");

        jCombo_jabatan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCombo_jabatanActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel12.setText("Password");

        jText_password.setText("jTextField1");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(15, 15, 15)
                                .addGroup(jPanel1Layout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jText_NIP, javax.swing.GroupLayout.DEFAULT_SIZE, 845,
                                                Short.MAX_VALUE)
                                        .addComponent(jLabel1)
                                        .addComponent(jLabel8)
                                        .addComponent(jText_tanggalLahir, javax.swing.GroupLayout.DEFAULT_SIZE, 845,
                                                Short.MAX_VALUE)
                                        .addComponent(jLabel9)
                                        .addComponent(jText_nama, javax.swing.GroupLayout.DEFAULT_SIZE, 845,
                                                Short.MAX_VALUE)
                                        .addComponent(jLabel10)
                                        .addComponent(jText_alamat, javax.swing.GroupLayout.DEFAULT_SIZE, 845,
                                                Short.MAX_VALUE)
                                        .addComponent(jLabel11)
                                        .addComponent(jCombo_jabatan, 0, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)
                                        .addComponent(jLabel12)
                                        .addComponent(jText_password, javax.swing.GroupLayout.DEFAULT_SIZE, 845,
                                                Short.MAX_VALUE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jText_NIP, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jText_nama, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jText_tanggalLahir, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jText_alamat, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCombo_jabatan, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jText_password, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(13, Short.MAX_VALUE)));

        javax.swing.GroupLayout panelKedua_masterPegawai_Admin1Layout = new javax.swing.GroupLayout(
                panelKedua_masterPegawai_Admin1);
        panelKedua_masterPegawai_Admin1.setLayout(panelKedua_masterPegawai_Admin1Layout);
        panelKedua_masterPegawai_Admin1Layout.setHorizontalGroup(
                panelKedua_masterPegawai_Admin1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelKedua_masterPegawai_Admin1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(panelKedua_masterPegawai_Admin1Layout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(panelKedua_masterPegawai_Admin1Layout.createSequentialGroup()
                                                .addGap(14, 14, 14)
                                                .addGroup(panelKedua_masterPegawai_Admin1Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(panelKedua_masterPegawai_Admin1Layout
                                                                .createSequentialGroup()
                                                                .addComponent(btn_simpan_masterPegawai1)
                                                                .addPreferredGap(
                                                                        javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(btn_batal_masterPegawai2))
                                                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                362, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(0, 500, Short.MAX_VALUE)))
                                .addContainerGap()));
        panelKedua_masterPegawai_Admin1Layout.setVerticalGroup(
                panelKedua_masterPegawai_Admin1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelKedua_masterPegawai_Admin1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel5)
                                .addGap(37, 37, 37)
                                .addGroup(panelKedua_masterPegawai_Admin1Layout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btn_simpan_masterPegawai1)
                                        .addComponent(btn_batal_masterPegawai2))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(12, 12, 12)));

        javax.swing.GroupLayout tambahPegawaiLayout = new javax.swing.GroupLayout(tambahPegawai);
        tambahPegawai.setLayout(tambahPegawaiLayout);
        tambahPegawaiLayout.setHorizontalGroup(
                tambahPegawaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(tambahPegawaiLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(panelKedua_masterPegawai_Admin1, javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap()));
        tambahPegawaiLayout.setVerticalGroup(
                tambahPegawaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(tambahPegawaiLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(panelKedua_masterPegawai_Admin1, javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap()));

        mainPanel.add(tambahPegawai, "card2");

        add(mainPanel, "card2");
    }// </editor-fold>//GEN-END:initComponents

    private void btn_batal_masterPegawaiActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btn_batal_masterPegawaiActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_btn_batal_masterPegawaiActionPerformed

    private void btn_hapus_masterPegawaiActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btn_hapus_masterPegawaiActionPerformed
        hapusPegawai();
        mainPanel.removeAll();
        mainPanel.add(dataPegawai);
        mainPanel.repaint();
        mainPanel.revalidate();
        populateTablePegawai();
        clearForm();
    }// GEN-LAST:event_btn_hapus_masterPegawaiActionPerformed

    private void btn_tambah_masterPegawaiActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btn_tambah_masterPegawaiActionPerformed
        mainPanel.removeAll();
        mainPanel.repaint();
        mainPanel.revalidate();

        mainPanel.add(tambahPegawai);
        mainPanel.repaint();
        mainPanel.revalidate();
    }// GEN-LAST:event_btn_tambah_masterPegawaiActionPerformed

    private void btn_simpan_masterPegawai1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btn_simpan_masterPegawai1ActionPerformed
        if (pegawaiYangDipilih != null) {
            // Jika ada pegawai yang dipilih, lakukan update
            editPegawai();
        } else {
            // Jika tidak ada pegawai yang dipilih, lakukan penambahan
            tambahPegawai();
        }

        mainPanel.removeAll();
        mainPanel.add(dataPegawai);
        mainPanel.repaint();
        mainPanel.revalidate();
        populateTablePegawai();
        clearForm();
    }// GEN-LAST:event_btn_simpan_masterPegawai1ActionPerformed

    private void btn_batal_masterPegawai2ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btn_batal_masterPegawai2ActionPerformed
        mainPanel.removeAll();
        mainPanel.add(dataPegawai);
        mainPanel.repaint();
        mainPanel.revalidate();
    }// GEN-LAST:event_btn_batal_masterPegawai2ActionPerformed

    private void jCombo_jabatanActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jCombo_jabatanActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_jCombo_jabatanActionPerformed

    private void jText_tanggalLahirActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jText_tanggalLahirActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_jText_tanggalLahirActionPerformed

    private void jText_alamatActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jText_alamatActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_jText_alamatActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_batal_masterPegawai;
    private javax.swing.JButton btn_batal_masterPegawai2;
    private javax.swing.JButton btn_hapus_masterPegawai;
    private javax.swing.JButton btn_simpan_masterPegawai1;
    private javax.swing.JButton btn_tambah_masterPegawai;
    private javax.swing.JPanel dataPegawai;
    private javax.swing.JComboBox<ComboItem> jCombo_jabatan;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jText_NIP;
    private javax.swing.JTextField jText_alamat;
    private javax.swing.JTextField jText_nama;
    private javax.swing.JTextField jText_password;
    private javax.swing.JTextField jText_tanggalLahir;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel panelKedua_masterPegawai_Admin1;
    private javax.swing.JPanel panelUtama_masterPegawai_Admin;
    private javax.swing.JTable table_absensiPegawaiHariini;
    private javax.swing.JPanel tambahPegawai;
    // End of variables declaration//GEN-END:variables
}
