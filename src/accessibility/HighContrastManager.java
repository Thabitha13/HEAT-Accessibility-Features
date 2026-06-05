package accessibility;

import javax.swing.UIManager;
import javax.swing.ImageIcon;
import javax.swing.plaf.ColorUIResource;
import java.awt.Image;
import java.util.logging.Logger;

public class HighContrastManager {

    private static Logger log = Logger.getLogger("heat");
    private static boolean highContrastEnabled = false;
    private static final int LARGE_ICON_SIZE = 24;
    private static final int DEFAULT_ICON_SIZE = 16;

    // Called at runtime (after GUI exists) — sets colors AND repaints
    public static void setHighContrastMode(boolean enabled) {
        highContrastEnabled = enabled;
        applyColors(enabled);
        try {
            javax.swing.SwingUtilities.updateComponentTreeUI(
                managers.WindowManager.getInstance().getMainScreenFrame()
            );
        } catch (Exception e) {
            log.warning("[HighContrastManager] Error repainting: " + e.getMessage());
        }
    }

    // Called at startup (before GUI exists) — sets colors only, no repaint
    public static void applyColorsOnly(boolean enabled) {
        highContrastEnabled = enabled;
        applyColors(enabled);
    }

    public static boolean isHighContrastEnabled() {
        return highContrastEnabled;
    }

    private static void applyColors(boolean isHighContrast) {
        if (isHighContrast) {
            ColorUIResource black = new ColorUIResource(0, 0, 0);
            ColorUIResource white = new ColorUIResource(255, 255, 255);
            ColorUIResource darkGray = new ColorUIResource(40, 40, 40);
            ColorUIResource lightGray = new ColorUIResource(200, 200, 200);
            ColorUIResource veryLightGray = new ColorUIResource(220, 220, 220);

            UIManager.put("Panel.background", black);
            UIManager.put("Panel.foreground", white);
            UIManager.put("Label.background", black);
            UIManager.put("Label.foreground", white);
            UIManager.put("Button.background", darkGray);
            UIManager.put("Button.foreground", white);
            UIManager.put("Button.select", lightGray);
            UIManager.put("Button.focus", veryLightGray);
            UIManager.put("TextField.background", darkGray);
            UIManager.put("TextField.foreground", white);
            UIManager.put("TextField.caretForeground", white);
            UIManager.put("TextField.border", new javax.swing.border.LineBorder(lightGray, 1));
            UIManager.put("TextArea.background", darkGray);
            UIManager.put("TextArea.foreground", white);
            UIManager.put("TextArea.caretForeground", white);
            UIManager.put("TextPane.background", darkGray);
            UIManager.put("TextPane.foreground", white);
            UIManager.put("TextPane.caretForeground", white);
            UIManager.put("ComboBox.background", darkGray);
            UIManager.put("ComboBox.foreground", white);
            UIManager.put("Menu.background", darkGray);
            UIManager.put("Menu.foreground", white);
            UIManager.put("MenuItem.background", darkGray);
            UIManager.put("MenuItem.foreground", white);
            UIManager.put("MenuItem.selectionBackground", lightGray);
            UIManager.put("MenuItem.selectionForeground", black);
            UIManager.put("MenuBar.background", darkGray);
            UIManager.put("MenuBar.foreground", white);
            UIManager.put("TabbedPane.background", black);
            UIManager.put("TabbedPane.foreground", white);
            UIManager.put("TabbedPane.selected", darkGray);
            UIManager.put("TabbedPane.tabAreaBackground", darkGray);
            UIManager.put("ScrollBar.background", darkGray);
            UIManager.put("ScrollBar.foreground", white);
            UIManager.put("Dialog.background", black);
            UIManager.put("Dialog.foreground", white);
            UIManager.put("Slider.background", black);
            UIManager.put("Slider.foreground", white);
            UIManager.put("TitledBorder.titleColor", white);
            UIManager.put("CheckBox.background", black);
            UIManager.put("CheckBox.foreground", white);
            UIManager.put("Tree.background", black);
            UIManager.put("Tree.foreground", white);
            UIManager.put("Tree.selectionBackground", lightGray);
            UIManager.put("Tree.selectionForeground", black);
        } else {
            try {
                UIManager.setLookAndFeel(UIManager.getLookAndFeel());
            } catch (Exception e) {
                log.warning("[HighContrastManager] Error resetting theme: " + e.getMessage());
            }
        }
    }

    public static ImageIcon scaleIcon(ImageIcon icon) {
        if (!highContrastEnabled || icon == null) return icon;
        try {
            Image scaledImg = icon.getImage().getScaledInstance(LARGE_ICON_SIZE, LARGE_ICON_SIZE, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImg);
        } catch (Exception e) {
            return icon;
        }
    }

    public static ImageIcon scaleIcon(ImageIcon icon, int width, int height) {
        if (!highContrastEnabled || icon == null) return icon;
        try {
            Image scaledImg = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImg);
        } catch (Exception e) {
            return icon;
        }
    }

    public static int getLargeIconSize() { return LARGE_ICON_SIZE; }
    public static int getDefaultIconSize() { return DEFAULT_ICON_SIZE; }
}