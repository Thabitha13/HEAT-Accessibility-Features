package managers;

import java.util.logging.Logger;
import utils.Settings;
import java.io.*;
import java.util.Properties;
import javax.swing.JOptionPane;

/**
 * The manager Class responsible for creating, loading and saving settings
 */
public class SettingsManager {

  private static Logger log = Logger.getLogger("heat");
  private static SettingsManager instance = null;
  private String SETTINGS_PATH = System.getProperty("user.home") +
    System.getProperty("file.separator");
  private String SETTINGS_FILE = "heat.settings";

  private Properties heatSettings = new Properties();
  private File settingsFile;
  private boolean newSettingsFile = false;
  private boolean haveChanges = false;

  protected SettingsManager() { /* Exists to prevent instantiation */ }

  public static SettingsManager getInstance() {
    if (instance == null) instance = new SettingsManager();
    return instance;
  }

  public void loadSettings() {
    try {
      settingsFile = new File(SETTINGS_PATH + SETTINGS_FILE);
      if (settingsFile.exists()) {
        try (InputStream stream = new FileInputStream(settingsFile)) {
          heatSettings.load(stream);
        }
        Properties defaultSettings = createDefaultProperties();
        if (heatSettings.getProperty(Settings.INTERPRETER_PATH) == null) {
          newSettingsFile = true;
        }
        for (String name : defaultSettings.stringPropertyNames()) {
          if (heatSettings.getProperty(name) == null) {
            heatSettings.setProperty(name, getDefault(name));
          }
        }
      } else {
        log.warning("[SettingsManager] - No settings file (" +
          settingsFile.getAbsolutePath() + ") found");
        heatSettings = createDefaultProperties();
        newSettingsFile = true;
      }
    } catch (IOException ioe) {
      log.warning("[SettingsManager] - No settings file found");
      newSettingsFile = true;
    }
  }

  private Properties createDefaultProperties() {
    Properties p = new Properties();
    p.setProperty(Settings.INTERPRETER_PATH, "C:\\Program Files\\Hugs98\\hugs.exe");
    p.setProperty(Settings.LIBRARY_PATH, System.getProperty("user.home"));
    p.setProperty(Settings.OUTPUT_FONT_SIZE, "12");
    p.setProperty(Settings.CODE_FONT_SIZE, "14");
    p.setProperty(Settings.INTERPRETER_OPTS, "");
    p.setProperty(Settings.TEST_FUNCTION, "");
    p.setProperty(Settings.TEST_POSITIVE, "True");
    // YOUR: TTS on by default
    p.setProperty(Settings.TTS_ENABLED, "true");
    // Goutham: high-contrast off by default
    p.setProperty(Settings.HIGH_CONTRAST_MODE, "false");
    // Thabitha: deuteranopia off by default
    p.setProperty(Settings.DEUTERANOPIA_MODE, "false");
    return p;
  }

  public String getDefault(String s) {
    return createDefaultProperties().getProperty(s);
  }

  public void saveSettings() {
    try {
      try (OutputStream out = new FileOutputStream(SETTINGS_PATH + SETTINGS_FILE)) {
        heatSettings.store(out, "HEAT SETTINGS");
      }
      JOptionPane.showMessageDialog(WindowManager.getInstance().getMainScreenFrame(),
        "Settings Saved", "Saved", JOptionPane.INFORMATION_MESSAGE);
    } catch (IOException ioe) {
      JOptionPane.showMessageDialog(WindowManager.getInstance().getMainScreenFrame(),
        "Error saving settings file", "Error", JOptionPane.ERROR_MESSAGE);
      log.warning("[SettingsManager] - IO Exception when writing settings file");
    }
  }

  public String getSetting(String name) { return heatSettings.getProperty(name); }

  public void setSetting(String key, String value) {
    setHaveChanges(true);
    heatSettings.setProperty(key, value);
  }

  public boolean isNewSettingsFile() { return newSettingsFile; }
  public void setHaveChanges(boolean haveChanges) { this.haveChanges = haveChanges; }
  public boolean isHaveChanges() { return haveChanges; }

  /** Goutham: returns true when high-contrast mode is on */
  public boolean isHighContrastEnabled() {
    return "true".equalsIgnoreCase(getSetting(Settings.HIGH_CONTRAST_MODE));
  }
}
