package frontend;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/** Token warna, font, dan helper format yang diturunkan dari desain Figma "p3 (Copy)". */
public final class Theme {

    public static final Color BG = new Color(0xF8, 0xFA, 0xFC);
    public static final Color WHITE = Color.WHITE;
    public static final Color SIDEBAR = new Color(0x0F, 0x17, 0x2A);
    public static final Color SIDEBAR_ACTIVE = new Color(0x1E, 0x29, 0x3B);
    public static final Color SIDEBAR_TEXT = new Color(0x94, 0xA3, 0xB8);
    public static final Color PRIMARY = new Color(0xF9, 0x73, 0x16);
    public static final Color PRIMARY_DARK = new Color(0xEA, 0x58, 0x0C);
    public static final Color PRIMARY_SOFT = new Color(0xFF, 0xED, 0xD5);
    public static final Color TEXT = new Color(0x0F, 0x17, 0x2A);
    public static final Color MUTED = new Color(0x64, 0x74, 0x8B);
    public static final Color MUTED_LIGHT = new Color(0x94, 0xA3, 0xB8);
    public static final Color BORDER = new Color(0xE2, 0xE8, 0xF0);
    public static final Color SOFT = new Color(0xF1, 0xF5, 0xF9);

    public static final Color SUCCESS_BG = new Color(0xDC, 0xFC, 0xE7);
    public static final Color SUCCESS_FG = new Color(0x16, 0x65, 0x34);
    public static final Color WARN_BG = new Color(0xFE, 0xF3, 0xC7);
    public static final Color WARN_FG = new Color(0xB4, 0x53, 0x09);
    public static final Color DANGER_BG = new Color(0xFE, 0xE2, 0xE2);
    public static final Color DANGER_FG = new Color(0xB9, 0x1C, 0x1C);
    public static final Color INFO_BG = new Color(0xDB, 0xEA, 0xFE);
    public static final Color INFO_FG = new Color(0x1D, 0x4E, 0xD8);

    private static final String FAMILY = pickFamily();

    private Theme() {
    }

    private static String pickFamily() {
        try {
            Set<String> installed = new HashSet<>(Arrays.asList(
                    GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
            for (String candidate : new String[]{"Geist", "Inter", "Segoe UI", "SansSerif"}) {
                if (installed.contains(candidate)) {
                    return candidate;
                }
            }
        } catch (Exception ignored) {
        }
        return Font.SANS_SERIF;
    }

    public static Font font(int style, int size) {
        return new Font(FAMILY, style, size);
    }

    public static Font regular(int size) {
        return font(Font.PLAIN, size);
    }

    public static Font medium(int size) {
        return font(Font.PLAIN, size);
    }

    public static Font bold(int size) {
        return font(Font.BOLD, size);
    }

    public static Color statusBg(String status) {
        if (status == null) {
            return SOFT;
        }
        switch (status.trim().toLowerCase()) {
            case "lunas":
            case "hadir":
            case "sudah":
            case "aktif":
            case "terbit":
            case "sudah dikirim":
                return SUCCESS_BG;
            case "menunggu":
            case "tertunda":
            case "ditangguhkan":
                return WARN_BG;
            case "ditolak":
            case "batal":
            case "gagal":
                return DANGER_BG;
            case "belum bayar":
            case "tidak hadir":
            case "selesai":
            case "draft":
                return SOFT;
            default:
                return INFO_BG;
        }
    }

    public static Color statusFg(String status) {
        if (status == null) {
            return MUTED;
        }
        switch (status.trim().toLowerCase()) {
            case "lunas":
            case "hadir":
            case "sudah":
            case "aktif":
            case "terbit":
            case "sudah dikirim":
                return SUCCESS_FG;
            case "menunggu":
            case "tertunda":
            case "ditangguhkan":
                return WARN_FG;
            case "ditolak":
            case "batal":
            case "gagal":
                return DANGER_FG;
            case "belum bayar":
            case "tidak hadir":
            case "selesai":
            case "draft":
                return MUTED;
            default:
                return INFO_FG;
        }
    }

    public static String rupiah(double value) {
        return "Rp " + String.format("%,.0f", value).replace(',', '.');
    }

    public static String initials(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "A";
        }
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, 1).toUpperCase();
        }
        return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
    }
}