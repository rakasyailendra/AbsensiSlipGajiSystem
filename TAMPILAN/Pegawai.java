/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;

public class Pegawai {
    private int idPegawai;
    private String username;
    private String password; // Pertimbangkan keamanan
    private String namaLengkap;
    private String jabatan;
    private String nomorKontak;
    private boolean statusAktif;
    private final String peran = "pegawai"; // Peran bersifat final

    // Konstruktor default
    public Pegawai() {
    }

    // Konstruktor dengan parameter
    public Pegawai(int idPegawai, String username, String password, String namaLengkap, String jabatan, String nomorKontak, boolean statusAktif) {
        this.idPegawai = idPegawai;
        this.username = username;
        this.password = password;
        this.namaLengkap = namaLengkap;
        this.jabatan = jabatan;
        this.nomorKontak = nomorKontak;
        this.statusAktif = statusAktif;
    }

    // Getter dan Setter
    public int getIdPegawai() {
        return idPegawai;
    }

    public void setIdPegawai(int idPegawai) {
        this.idPegawai = idPegawai;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
    }

    public String getJabatan() {
        return jabatan;
    }

    public void setJabatan(String jabatan) {
        this.jabatan = jabatan;
    }

    public String getNomorKontak() {
        return nomorKontak;
    }

    public void setNomorKontak(String nomorKontak) {
        this.nomorKontak = nomorKontak;
    }

    public boolean isStatusAktif() {
        return statusAktif;
    }

    public void setStatusAktif(boolean statusAktif) {
        this.statusAktif = statusAktif;
    }

    public String getPeran() {
        return peran;
    }

    // Untuk ComboBox, seringkali method toString() di-override untuk menampilkan nama
    @Override
    public String toString() {
        return namaLengkap + " (ID: " + idPegawai + ")";
    }
}