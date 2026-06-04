package utils;

import java.awt.Color;

/**
 * Colour constants for the HEAT high-contrast accessibility theme.
 * All colours meet WCAG 2.2 AA contrast requirements (4.5:1 for text).
 */
public class HeatTheme {

    // Background colours
    public static final Color HIGH_CONTRAST_BACKGROUND  = new Color(18, 18, 18);
    public static final Color HIGH_CONTRAST_PANEL       = new Color(30, 30, 30);
    public static final Color HIGH_CONTRAST_EDITOR_BG   = new Color(20, 20, 20);

    // Text colours
    public static final Color HIGH_CONTRAST_FOREGROUND  = new Color(240, 240, 240);
    public static final Color HIGH_CONTRAST_ERROR_TEXT  = new Color(255, 100, 100);
    public static final Color HIGH_CONTRAST_INFO_TEXT   = new Color(100, 180, 255);
    public static final Color HIGH_CONTRAST_PROMPT_TEXT = new Color(100, 230, 100);

    // Status bar colours — replaces the existing toolbar status icons
    public static final Color STATUS_COMPILED_OK        = new Color(80, 200, 80);
    public static final Color STATUS_COMPILED_ERROR     = new Color(220, 60, 60);
    public static final Color STATUS_EVALUATING         = new Color(220, 160, 40);
    public static final Color STATUS_UNCOMPILED         = new Color(160, 160, 160);
    public static final Color STATUS_NO_PROGRAM         = new Color(100, 100, 100);

    private HeatTheme() {
        // Utility class — no instances
    }
}
