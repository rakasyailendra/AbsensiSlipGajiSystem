package GUI; 

import GUI.LoginFrame; // Sesuaikan dengan package LoginFrame Anda
import javax.swing.UIManager;

public class Main { // atau public class Pbokelompok2

    public static void main(String[] args) {
        // Terapkan Look and Feel Nimbus atau sistem default
        try {
            // Coba set Nimbus Look and Feel
            boolean nimbusFound = false;
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    nimbusFound = true;
                    break;
                }
            }
            // Jika Nimbus tidak ditemukan, coba gunakan System Look and Feel
            if (!nimbusFound) {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
        } catch (Exception e) {
            // Jika gagal, biarkan Java menggunakan default Look and Feel
            System.err.println("Failed to set Look and Feel: " + e.getMessage());
            // e.printStackTrace();
        }

        /* Create and display the login form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoginFrame().setVisible(true);
            }
        });
    }
}