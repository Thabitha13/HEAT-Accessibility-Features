package accessibility;

/**
 * KeyboardGuide - Reads available keyboard shortcuts aloud via TTS.
 * Activated by pressing F1 at any time.
 */
public class KeyboardGuide {

    /**
     * Speaks all available keyboard shortcuts via TTS.
     */
    public static void readShortcuts() {
        String mod = TTSManager.modifier();
        TTSManager tts = TTSManager.getInstance();
        tts.speak("Available keyboard shortcuts for HEAT.");
        tts.speak(mod + " O — open a Haskell file.");
        tts.speak("F5 — compile and load program.");
        tts.speak("F6 — interrupt evaluation.");
        tts.speak("F7 — run property tests.");
        tts.speak("F10 — move focus to toolbar.");
        tts.speak("Tab — move between toolbar buttons.");
        tts.speak("Space — activate focused button.");
        tts.speak(mod + " A — select all text.");
        tts.speak(mod + " Z — undo.");
        tts.speak(mod + " C — copy.");
        tts.speak(mod + " X — cut.");
        tts.speak(mod + " V — paste.");
        tts.speak(mod + " F — find text.");
        tts.speak("F1 — repeat this list.");
    }
}
