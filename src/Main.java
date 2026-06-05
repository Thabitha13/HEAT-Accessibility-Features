import java.io.File;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import managers.InterpreterManager;
import managers.SettingsManager;
import managers.UndoManager;
import managers.WindowManager;
import view.windows.SplashWindow;

/**
 * Main HEAT class
 */
public class Main {

  public static void main(String[] args) {
    Logger log = Logger.getLogger("heat");
    try {
      log.setUseParentHandlers(false);
      FileHandler handler = new FileHandler(System.getProperty("user.home") + File.separator + "heat.log");
      handler.setFormatter(new SimpleFormatter());
      log.addHandler(handler);
    } catch (Exception e) {
      System.out.println("Could not install file handler for logging.");
    }

    System.setProperty("com.apple.mrj.application.apple.menu.about.name", "");
    System.setProperty("apple.laf.useScreenMenuBar", "true");

    // Goutham: show splash screen while loading
    SplashWindow splash = new SplashWindow();
    splash.setVisible(true);

    SettingsManager sm = SettingsManager.getInstance();
    WindowManager wm = WindowManager.getInstance();

    sm.loadSettings();
    // Thabitha: initialize font sizes from saved settings
    accessibility.FontSizeManager.initializeFontSizes();
    WindowManager.setLookAndFeel();
    wm.createGUI();
    wm.setSplashWindow(splash);

    if (sm.isNewSettingsFile()) {
      wm.dismissSplash();
      wm.showWizardWindow();
      // will also start interpreter process
    } else {
      InterpreterManager im = InterpreterManager.getInstance();
      im.startProcess(false);
      try { Thread.sleep(2000); } catch (Exception e) {} // wait for startup
    }

    if (args.length > 0) {
      wm.openFile(new java.io.File(args[0]));
      wm.showAll();
      wm.getEditorWindow().grabFocus();
    } else {
      wm.getEditorWindow().setEditorContent("Use menu to open an existing or create a new program in the editor.");
      wm.setCloseEnabled(false);
      UndoManager.getInstance().reset();
      wm.onlyConsole();
      wm.getConsoleWindow().getFocus();
    }

    wm.getMainScreenFrame().setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
    wm.setVisible();

    // YOUR feature: delay TTS startup to avoid speaking GHCi boot messages
    new Thread(() -> {
      try { Thread.sleep(2000); } catch (Exception e) {}
      accessibility.TTSManager.getInstance().setStartupComplete();
    }).start();
  }
}
