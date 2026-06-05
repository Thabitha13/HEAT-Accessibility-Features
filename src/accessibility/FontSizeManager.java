package accessibility;

import managers.WindowManager;
import managers.SettingsManager;
import utils.Settings;
import java.util.logging.Logger;

/**
 * Manages font size for accessibility.
 * Provides methods to increase/decrease font size across editor and console.
 */
public class FontSizeManager {

    private static java.util.List<Runnable> listeners = new java.util.ArrayList<>();

    public static void addFontSizeListener(Runnable listener) {
        listeners.add(listener);
    }

    public static void removeFontSizeListener(Runnable listener) {
        listeners.remove(listener);
    }

    private static void notifyListeners() {
        for (Runnable r : listeners) r.run();
    }

    private static Logger log = Logger.getLogger("heat");
    private static final int MIN_FONT_SIZE = 8;
    private static final int MAX_FONT_SIZE = 36;
    private static final int DEFAULT_EDITOR_SIZE = 12;
    private static final int DEFAULT_CONSOLE_SIZE = 12;
    private static final int INCREMENT = 2;

    private static int currentEditorFontSize = DEFAULT_EDITOR_SIZE;
    private static int currentConsoleFontSize = DEFAULT_CONSOLE_SIZE;

    /**
     * Initialize font sizes from saved settings
     */
    public static void initializeFontSizes() {
        SettingsManager sm = SettingsManager.getInstance();
        
        String editorSize = sm.getSetting(Settings.CODE_FONT_SIZE);
        if (editorSize != null && !editorSize.isEmpty()) {
            try {
                currentEditorFontSize = Integer.parseInt(editorSize);
            } catch (NumberFormatException e) {
                currentEditorFontSize = DEFAULT_EDITOR_SIZE;
            }
        }
        
        String consoleSize = sm.getSetting(Settings.OUTPUT_FONT_SIZE);
        if (consoleSize != null && !consoleSize.isEmpty()) {
            try {
                currentConsoleFontSize = Integer.parseInt(consoleSize);
            } catch (NumberFormatException e) {
                currentConsoleFontSize = DEFAULT_CONSOLE_SIZE;
            }
        }

        log.info("[FontSizeManager] Initialized: editor=" + currentEditorFontSize + 
                 "pt, console=" + currentConsoleFontSize + "pt");
    }

    /**
     * Get current editor font size
     */
    public static int getEditorFontSize() {
        return currentEditorFontSize;
    }

    /**
     * Get current console font size
     */
    public static int getConsoleFontSize() {
        return currentConsoleFontSize;
    }

    /**
     * Increase font size by INCREMENT for both editor and console
     */
    public static void increaseFontSize() {
        int newEditorSize = currentEditorFontSize + INCREMENT;
        int newConsoleSize = currentConsoleFontSize + INCREMENT;
        
        if (newEditorSize <= MAX_FONT_SIZE) {
            setEditorFontSize(newEditorSize);
        }
        if (newConsoleSize <= MAX_FONT_SIZE) {
            setConsoleFontSize(newConsoleSize);
        }
        notifyListeners();
    }

    /**
     * Decrease font size by INCREMENT for both editor and console
     */
    public static void decreaseFontSize() {
        int newEditorSize = currentEditorFontSize - INCREMENT;
        int newConsoleSize = currentConsoleFontSize - INCREMENT;
        
        if (newEditorSize >= MIN_FONT_SIZE) {
            setEditorFontSize(newEditorSize);
        }
        if (newConsoleSize >= MIN_FONT_SIZE) {
            setConsoleFontSize(newConsoleSize);
        }
        notifyListeners();
    }

    /**
     * Set editor font size and apply immediately
     */
    public static void setEditorFontSize(int size) {
        if (size >= MIN_FONT_SIZE && size <= MAX_FONT_SIZE) {
            currentEditorFontSize = size;

            WindowManager wm = WindowManager.getInstance();
            if (wm != null && wm.getEditorWindow() != null) {
                wm.getEditorWindow().setFontSize(size);
                log.info("[FontSizeManager] Editor font size changed to " + size + "pt");
            }
            if (wm != null && wm.getTreeWindow() != null) {
                wm.getTreeWindow().setFontSize(size);
            }

            // Save to settings
            SettingsManager.getInstance().setSetting(
                Settings.CODE_FONT_SIZE,
                String.valueOf(size)
            );
        }
    }

    /**
     * Set console font size and apply immediately
     */
    public static void setConsoleFontSize(int size) {
        if (size >= MIN_FONT_SIZE && size <= MAX_FONT_SIZE) {
            currentConsoleFontSize = size;
            
            WindowManager wm = WindowManager.getInstance();
            if (wm != null && wm.getConsoleWindow() != null) {
                wm.getConsoleWindow().setFontSize(size);
                log.info("[FontSizeManager] Console font size changed to " + size + "pt");
            }
            
            // Save to settings
            SettingsManager.getInstance().setSetting(
                Settings.OUTPUT_FONT_SIZE, 
                String.valueOf(size)
            );
        }
    }

    /**
     * Set both editor and console to the same font size
     */
    public static void setFontSize(int size) {
        setEditorFontSize(size);
        setConsoleFontSize(size);
    }

    /**
     * Check if size can be increased further
     */
    public static boolean canIncrease() {
        return Math.max(currentEditorFontSize, currentConsoleFontSize) < MAX_FONT_SIZE;
    }

    /**
     * Check if size can be decreased further
     */
    public static boolean canDecrease() {
        return Math.min(currentEditorFontSize, currentConsoleFontSize) > MIN_FONT_SIZE;
    }

    public static int getMinFontSize() {
        return MIN_FONT_SIZE;
    }

    public static int getMaxFontSize() {
        return MAX_FONT_SIZE;
    }

    public static int getIncrement() {
        return INCREMENT;
    }
}