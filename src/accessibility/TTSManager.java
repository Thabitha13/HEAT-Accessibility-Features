package accessibility;

import managers.SettingsManager;
import utils.Settings;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * TTSManager - Singleton class responsible for text-to-speech functionality.
 * Uses the operating system's built-in speech engine.
 * Supports macOS (say), Windows (PowerShell), and Linux (espeak).
 */
public class TTSManager {

    private static TTSManager instance = null;
    private Process ttsProcess = null;
    private StringBuilder lineBuffer = new StringBuilder();
    private BlockingQueue<String> speechQueue = new LinkedBlockingQueue<String>();
    private boolean isSpeaking = false;
    private boolean startupComplete = false;
    private boolean focusAnnouncementsEnabled = false;
  

    protected TTSManager() {
        /* Exists to prevent instantiation */
    }

    /**
     * Returns a single consistent instance of TTSManager
     */
    public static TTSManager getInstance() {
        if (instance == null)
            instance = new TTSManager();
        return instance;
    }

    /**
     * Called after HEAT has finished initialising.
     * Prevents TTS from speaking during startup sequence.
     */


    public void setStartupComplete() {
        startupComplete = true;
        speak("Welcome to HEAT. Press " + modifier() + " O to open a Haskell file. Press F1 for all keyboard shortcuts.");
        new Thread(new Runnable() {
            public void run() {
                try { Thread.sleep(4000); } catch (Exception e) {}
                focusAnnouncementsEnabled = true;
            }
        }).start();
}

    public boolean isFocusAnnouncementsEnabled() {
        return focusAnnouncementsEnabled;
}

    public boolean isStartupComplete() {
    return startupComplete;
}

    /**
 * Returns the platform modifier key name for TTS announcements.
 * Returns "Command" on macOS and "Control" on Windows and Linux.
 */
    public static String modifier() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("mac") ? "Command" : "Control";
    }

    /**
     * Returns whether TTS is currently enabled in settings
     */
    public boolean isEnabled() {
        String val = SettingsManager.getInstance()
            .getSetting(Settings.TTS_ENABLED);
        return "true".equalsIgnoreCase(val);
    }

    /**
     * Adds text to the speech queue.
     * Messages are spoken in order without cutting each other off.
     * Silent during startup sequence.
     */
    public void speak(final String text) {
        if (!startupComplete) return;
        if (!isEnabled() || text == null || text.trim().isEmpty()) return;
        System.out.println("DEBUG speak called with: " + text); // TEMP DEBUG
        speechQueue.add(text);
        if (!isSpeaking) {
            processQueue();
        }
    }

    /**
     * Processes the speech queue on a separate thread.
     * Speaks each message in order, waiting for each to finish.
     */
    private void processQueue() {
        isSpeaking = true;
        new Thread(new Runnable() {
            public void run() {
                try {
                    String text;
                    while ((text = speechQueue.poll()) != null) {
                        String os = System.getProperty("os.name").toLowerCase();
                        if (os.contains("mac")) {
                            ttsProcess = Runtime.getRuntime().exec(
                                new String[]{"say", text});
                            ttsProcess.waitFor();
                        } else if (os.contains("win")) {
                            String safe = text.replace("\"", " ");
                            ttsProcess = Runtime.getRuntime().exec(new String[]{
                                "powershell", "-Command",
                                "Add-Type -AssemblyName System.Speech; " +
                                "(New-Object System.Speech.Synthesis.SpeechSynthesizer)" +
                                ".Speak(\"" + safe + "\")"});
                            ttsProcess.waitFor();
                        } else {
                            ttsProcess = Runtime.getRuntime().exec(
                                new String[]{"espeak", text});
                            ttsProcess.waitFor();
                        }
                    }
                } catch (Exception e) {
                    /* TTS unavailable — silent fail, never crash HEAT */
                } finally {
                    isSpeaking = false;
                }
            }
        }).start();
    }

    /**
     * Stops any currently playing speech and clears the queue.
     */
    public void stopSpeaking() {
        speechQueue.clear();
        if (ttsProcess != null) {
            ttsProcess.destroy();
            ttsProcess = null;
        }
        isSpeaking = false;
    }



    /**
 * Buffers characters from the interpreter stream.
 * Speaks simplified error explanation when a complete line is received.
 * Only speaks error lines, warnings, key status lines, and results.
 */
public void bufferChar(char c) {
    if (!isEnabled()) return;
    if (c == '\n') {
        String line = lineBuffer.toString().trim();
        if (!line.isEmpty()) {
            System.out.println("DEBUG bufferChar processing: " + line); // TEMP DEBUG
            String simplified = ErrorSimplifier.simplify(line);
            if (simplified != null) {
                speak(simplified);
            } else if (line.startsWith("\u2022") && line.toLowerCase().contains("no instance")) {
                // Speak first bullet point of no instance errors only
                speak(ErrorSimplifier.simplify("no instance for") != null ?
                    ErrorSimplifier.simplify("no instance for") : line);
            } else if (line.toLowerCase().contains("error:") &&
                       !line.startsWith("In ") &&
                       !line.startsWith("|") &&
                       !line.startsWith("<interactive>") &&
                       !line.matches(".*\\.hs:\\d+.*")) {
                // Speak raw error header lines only — not detail lines
                speak(line);
            } else {
                // Extract result — strip any prompt prefix like *MyCode>
                String result = line;
                if (line.contains(">")) {
                    result = line.substring(line.lastIndexOf(">") + 1).trim();
                }
                // Only speak clean results — filter out noise
                if (!result.isEmpty() &&
                    !result.startsWith("[") &&
                    !result.startsWith("(compiling)") &&
                    !result.startsWith("In ") &&
                    !result.startsWith("|") &&
                    !result.startsWith("•") &&
                    !result.startsWith("Suggested") &&
                    !result.startsWith("Warning") &&
                    !result.startsWith("because") &&
                    !result.contains("interpreted") &&
                    !result.contains("GHCi") &&
                    !result.contains("version") &&
                    !result.contains("changing directory") &&
                    !result.matches("\\s*\\^+\\s*") &&
                    !result.matches("\\d+\\s*\\|.*")) {
                    // Speak evaluation results — numbers, booleans, strings etc.
                    speak("Result: " + result);
                }
            }
        }
        lineBuffer.setLength(0);
    } else {
        lineBuffer.append(c);
    }
}
}