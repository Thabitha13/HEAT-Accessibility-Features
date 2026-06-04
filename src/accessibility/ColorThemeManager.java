package accessibility;

import java.awt.Color;

public class ColorThemeManager {

    private static boolean deuteranopiaEnabled = false;

    public static class DefaultTheme {
        public static final Color ERROR_COLOR = new Color(255, 0, 0);
        public static final Color PROMPT_COLOR = new Color(0, 170, 0);
    }

    public static class DeuteranopiaTheme {
        public static final Color ERROR_COLOR = new Color(230, 159, 0);
        public static final Color PROMPT_COLOR = new Color(0, 114, 178);
    }

    public static Color getErrorColor() {
        return deuteranopiaEnabled
            ? DeuteranopiaTheme.ERROR_COLOR
            : DefaultTheme.ERROR_COLOR;
    }

    public static Color getPromptColor() {
        return deuteranopiaEnabled
            ? DeuteranopiaTheme.PROMPT_COLOR
            : DefaultTheme.PROMPT_COLOR;
    }

    public static void setDeuteranopiaMode(boolean enabled) {
        deuteranopiaEnabled = enabled;
    }

    public static boolean isDeuteranopiaEnabled() {
        return deuteranopiaEnabled;
    }
}