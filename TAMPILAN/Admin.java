/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;

public class Admin {
    private int idAdmin;
    private String username;
    private String password; // Pertimbangkan keamanan untuk password di aplikasi nyata
    private String namaLengkap;
    private String jabatan;
    private final String peran = "admin"; // Peran bersifat final dan spesifik untuk admin

    // Konstruktor default
    public Admin() {
    }

    // Konstruktor dengan parameter
    public Admin(int idAdmin, String username, String password, String namaLengkap, String jabatan) {
        this.idAdmin = idAdmin;
        this.username = username;
        this.password = password;
        this.namaLengkap = namaLengkap;
        this.jabatan = jabatan;
    }

    // Getter dan Setter
    public int getIdAdmin() {
        return idAdmin;
    }

    public void setIdAdmin(int idAdmin) {
        this.idAdmin = idAdmin;
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

    public String getPeran() {
        return peran;
    }

    @Override
    public String toString() {
        return "Admin{" +
               "idAdmin=" + idAdmin +
               ", username='" + username + '\'' +
               ", namaLengkap='" + namaLengkap + '\'' +
               ", jabatan='" + jabatan + '\'' +
               ", peran='" + peran + '\'' +
               '}';
    }
}