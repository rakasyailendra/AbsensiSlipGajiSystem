/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */Revisi
package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;
public class AbsensiFrame extends JFrame {
    private Karyawan karyawan;
    private DefaultTableModel model;

    public AbsensiFrame(Karyawan karyawan, DefaultTableModel model) {
        this.karyawan = karyawan;
        this.model = model;
        setTitle("Absensi Karyawan");
        setSize(300, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(5, 2));

        // Input untuk tanggal, hari, jam
        add(new JLabel("Tanggal:"));
        JTextField txtTanggal = new JTextField();
        add(txtTanggal);

        add(new JLabel("Hari:"));
        JTextField txtHari = new JTextField();
        add(txtHari);

        add(new JLabel("Jam:"));
        JTextField txtJam = new JTextField();
        add(txtJam);

        // Checkbox untuk hadir
        JCheckBox chkHadir = new JCheckBox("Hadir");
        add(chkHadir);

        // Tombol submit
        JButton btnSubmit = new JButton("Submit");
        btnSubmit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String tanggal = txtTanggal.getText();
                String hari = txtHari.getText();
                String jam = txtJam.getText();
                boolean hadir = chkHadir.isSelected();

                Absensi absensi = new Absensi(tanggal, hari, jam, hadir);
                karyawan.tambahAbsensi(absensi);
                model.addRow(absensi.toArray());

                // Simpan data ke file
                FileHandler.saveKaryawanData(karyawan);

                JOptionPane.showMessageDialog(null, "Absensi berhasil ditambahkan!");
                dispose();
            }
        });
        add(btnSubmit);

        setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
