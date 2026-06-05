package utils;

import managers.WindowManager;
import java.util.logging.Logger;
import java.io.*;

/**
 * InterpreterToConsole
 * Handles input from an external process.
 */
public class InterpreterToConsole extends Thread {
  private InputStream in;
  private Logger log = Logger.getLogger("heat");
  private view.windows.ConsoleWindow cw = WindowManager.getInstance().getConsoleWindow();

  public InterpreterToConsole(InputStream in, String type) {
    this.in = in;
  }

  public void abort() {
    try { in.close(); } catch (IOException e) {
      log.warning("Closing input stream raises IOException.");
    }
  }

  public void run() {
    BufferedReader br = new BufferedReader(new InputStreamReader(in));
    int letter;
    try {
      while (!isInterrupted() && (letter = br.read()) != -1) {
        final char character = (char) letter;
        javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
          public void run() {
            cw.charFromInterpreter(character);
            // YOUR: feed each character to the TTS line buffer
            accessibility.TTSManager.getInstance().bufferChar(character);
          }
        });
      }
    } catch (IOException ioe) {
      log.warning("Error getting stream from interpreter.");
    } catch (java.lang.InterruptedException e) {
      log.warning("InterpreterToConsole interrupted by exception.");
    } catch (java.lang.reflect.InvocationTargetException e) {
      log.warning("InterpreterToConsole InvocationTargetException.");
      e.printStackTrace();
    }
  }
}
