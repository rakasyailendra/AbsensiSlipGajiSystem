package GUI;

import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {
        // Mencoba menerapkan Look and Feel Nimbus
        try {
            boolean nimbusFound = false;
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    System.out.println("Nimbus Look and Feel berhasil diterapkan.");
                    nimbusFound = true;
                    break;
                }
            }

            if (!nimbusFound) {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                System.out.println("Nimbus tidak ditemukan. Menggunakan System Look and Feel.");
            }
        } catch (Exception e) {
            System.err.println("Gagal mengatur Look and Feel: " + e.getMessage());
        }

        // Menampilkan LoginFrame di Event Dispatch Thread
        javax.swing.SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
