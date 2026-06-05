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

    // Tracks whether we are currently inside a GHCi error block
    private boolean inErrorBlock = false;
    // Once a simplified message has been spoken for this error block, don't repeat it
    private boolean simplifiedSpokenForBlock = false;

    protected TTSManager() { /* Exists to prevent instantiation */ }

    public static TTSManager getInstance() {
        if (instance == null)
            instance = new TTSManager();
        return instance;
    }

    public void setStartupComplete() {
        startupComplete = true;
        speak("Welcome to HEAT. Press " + modifier() + " N to create a new Haskell file. Press " + modifier() + " O to open an existing file. Press Escape to skip. Press F1 for all keyboard shortcuts.");
        new Thread(new Runnable() {
            public void run() {
                try { Thread.sleep(4000); } catch (Exception e) {}
                focusAnnouncementsEnabled = true;
            }
        }).start();
    }

    public boolean isFocusAnnouncementsEnabled() { return focusAnnouncementsEnabled; }
    public boolean isStartupComplete() { return startupComplete; }

    public static String modifier() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("mac") ? "Command" : "Control";
    }

    public boolean isEnabled() {
        String val = SettingsManager.getInstance().getSetting(Settings.TTS_ENABLED);
        return "true".equalsIgnoreCase(val);
    }

    public void speak(final String text) {
        if (!startupComplete) return;
        if (!isEnabled() || text == null || text.trim().isEmpty()) return;
        speechQueue.add(text);
        if (!isSpeaking) {
            processQueue();
        }
    }

    private void processQueue() {
        isSpeaking = true;
        new Thread(new Runnable() {
            public void run() {
                try {
                    String text;
                    while ((text = speechQueue.poll()) != null) {
                        String os = System.getProperty("os.name").toLowerCase();
                        if (os.contains("mac")) {
                            ttsProcess = Runtime.getRuntime().exec(new String[]{"say", text});
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
                            ttsProcess = Runtime.getRuntime().exec(new String[]{"espeak", text});
                            ttsProcess.waitFor();
                        }
                    }
                } catch (Exception e) {
                    /* TTS unavailable — silent fail */
                } finally {
                    isSpeaking = false;
                }
            }
        }).start();
    }

    public void stopSpeaking() {
        speechQueue.clear();
        if (ttsProcess != null) {
            ttsProcess.destroy();
            ttsProcess = null;
        }
        isSpeaking = false;
    }

    /**
     * Buffers characters from the GHCi output stream one at a time.
     * On each complete line, decides what to speak.
     *
     * Error block logic:
     * - A line containing "error:" starts the error block.
     * - Every line inside the block is checked against ErrorSimplifier first.
     *   If a simplified message exists, speak that ONCE per block and skip
     *   subsequent lines that match the same error type.
     *   If no simplification, speak the raw line verbatim (strips bullet char).
     * - A blank line ends the block.
     */
    public void bufferChar(char c) {
        if (!isEnabled()) return;

        if (c == '\n') {
            String line = lineBuffer.toString().trim();
            lineBuffer.setLength(0);

            if (line.isEmpty()) {
                // Blank line ends the error block
                inErrorBlock = false;
                simplifiedSpokenForBlock = false;
                return;
            }

            processLine(line);
        } else {
            lineBuffer.append(c);
        }
    }

    private void processLine(String line) {
        String lower = line.toLowerCase();

        // ── Always skip noise lines ───────────────────────────────────────────
        if (lower.contains("ghci") && lower.contains("version")) return;
        if (lower.contains("loading package")) return;
        if (lower.matches("\\s*\\^+\\s*")) return;        // caret underlines  ^^^
        if (lower.matches("\\d+\\s*\\|.*")) return;        // source code context  4 | square x = ...
        if (lower.contains("changing directory")) return;
        if (lower.contains("interpreted")) return;

        // ── Compilation outcome ───────────────────────────────────────────────
        if (lower.contains("failed, modules loaded: none") ||
            lower.contains("failed, no modules loaded")) {
            inErrorBlock = false;
            simplifiedSpokenForBlock = false;
            speak("Compilation failed. Fix the errors and press F5 to try again.");
            return;
        }
        if (lower.contains("ok, one module loaded") ||
            lower.contains("ok, modules loaded")) {
            inErrorBlock = false;
            simplifiedSpokenForBlock = false;
            speak("Compilation successful. Your program is ready.");
            return;
        }

        // ── Error header — starts the error block ─────────────────────────────
        // e.g.  <interactive>:4:8: error: [GHC-39999]
        // e.g.  MyFile.hs:12:5: error:
        if (lower.contains("error:") || lower.contains(": error")) {
            inErrorBlock = true;
            simplifiedSpokenForBlock = false;
            // The header line itself rarely contains the useful detail,
            // so just announce "Error found" — the bullet lines below carry the message.
            // However if the header itself matches a simplifier pattern, use it.
            String simplified = ErrorSimplifier.simplify(line);
            if (simplified != null) {
                speak(simplified);
                simplifiedSpokenForBlock = true;
            } else {
                speak("Error found. " + cleanLine(line));
            }
            return;
        }

        // ── Lines inside an active error block ────────────────────────────────
        if (inErrorBlock) {
            // Strip bullet character • and leading whitespace
            String content = line.replaceAll("^[•\\-\\*]\\s*", "").trim();
            if (content.isEmpty()) return;

            // Check if this line (or the bullet content) matches a simplifier pattern.
            // Try both the original line and the stripped content.
            String simplified = ErrorSimplifier.simplify(line);
            if (simplified == null) simplified = ErrorSimplifier.simplify(content);

            if (simplified != null) {
                // Only speak the simplified message once per error block,
                // so we don't repeat the same explanation for every related bullet.
                if (!simplifiedSpokenForBlock) {
                    speak(simplified);
                    simplifiedSpokenForBlock = true;
                }
                // Also speak the raw detail line so the user hears the full context
                speak(content);
            } else {
                // No simplification — speak the detail line verbatim
                speak(content);
            }
            return;
        }

        // ── Warning lines (outside error block) ───────────────────────────────
        if (lower.contains("warning:")) {
            speak("Warning: " + cleanLine(line));
            return;
        }

        // ── Evaluation results ────────────────────────────────────────────────
        if (line.contains(">")) {
            String after = line.substring(line.lastIndexOf(">") + 1).trim();
            if (!after.isEmpty() &&
                !after.startsWith("[") &&
                !after.startsWith("(") &&
                !after.contains("::")) {
                speak("Result: " + after);
            }
        }
    }

    /** Strips file path prefixes like "<interactive>:4:8: error: " from header lines */
    private String cleanLine(String line) {
        return line.replaceAll("^[^:]+:\\d+:\\d+:\\s*(error:\\s*)?", "").trim();
    }
}
