package pbo_eventin;

import frontend.LoginFrame;
import java.util.Enumeration;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

/**
 * Entry point aplikasi EventIn Admin Dashboard.
 *
 * @author aryakavi
 */
public class PBO_EventIn {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Pakai look and feel default Java bila sistem tidak mendukung.
        }
        applyGlobalFont();
        java.awt.EventQueue.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    /** Samakan font dialog dan komponen standar dengan tema aplikasi. */
    private static void applyGlobalFont() {
        java.awt.Font base = frontend.Theme.regular(12);
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, new FontUIResource(base));
            }
        }
    }
}