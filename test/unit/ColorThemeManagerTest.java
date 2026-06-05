import accessibility.ColorThemeManager;
import java.awt.Color;

/**
 * Unit tests for ColorThemeManager.
 * Tests toggle, colour values, and persistence of state.
 * Run with: javac -cp ../../src ColorThemeManagerTest.java && java -cp .:../../src ColorThemeManagerTest
 */
public class ColorThemeManagerTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=== ColorThemeManager Unit Tests ===\n");

        // Test 1: default mode is off
        ColorThemeManager.setDeuteranopiaMode(false);
        assertFalse("default mode is off", ColorThemeManager.isDeuteranopiaEnabled());

        // Test 2: default error colour is red
        ColorThemeManager.setDeuteranopiaMode(false);
        assertEqual("default error color is red",
            new Color(255, 0, 0), ColorThemeManager.getErrorColor());

        // Test 3: default prompt colour is green
        assertEqual("default prompt color is green",
            new Color(0, 170, 0), ColorThemeManager.getPromptColor());

        // Test 4: enabling deuteranopia changes error colour to orange
        ColorThemeManager.setDeuteranopiaMode(true);
        assertEqual("deuteranopia error color is orange",
            new Color(230, 159, 0), ColorThemeManager.getErrorColor());

        // Test 5: enabling deuteranopia changes prompt colour to blue
        assertEqual("deuteranopia prompt color is blue",
            new Color(0, 114, 178), ColorThemeManager.getPromptColor());

        // Test 6: toggle off restores default error colour
        ColorThemeManager.setDeuteranopiaMode(false);
        assertEqual("toggle off restores red",
            new Color(255, 0, 0), ColorThemeManager.getErrorColor());

        // Test 7: isDeuteranopiaEnabled returns true when on
        ColorThemeManager.setDeuteranopiaMode(true);
        assertTrue("isDeuteranopiaEnabled returns true when on",
            ColorThemeManager.isDeuteranopiaEnabled());

        // Test 8: isDeuteranopiaEnabled returns false when off
        ColorThemeManager.setDeuteranopiaMode(false);
        assertFalse("isDeuteranopiaEnabled returns false when off",
            ColorThemeManager.isDeuteranopiaEnabled());

        // Test 9: double toggle returns to original colour
        ColorThemeManager.setDeuteranopiaMode(true);
        ColorThemeManager.setDeuteranopiaMode(false);
        assertEqual("double toggle restores original error color",
            new Color(255, 0, 0), ColorThemeManager.getErrorColor());

        System.out.println("\n=== Results: " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }

    private static void assertTrue(String name, boolean condition) {
        if (condition) { System.out.println("  PASS: " + name); passed++; }
        else { System.out.println("  FAIL: " + name); failed++; }
    }

    private static void assertFalse(String name, boolean condition) {
        assertTrue(name, !condition);
    }

    private static void assertEqual(String name, Color expected, Color actual) {
        if (expected.equals(actual)) {
            System.out.println("  PASS: " + name);
            passed++;
        } else {
            System.out.println("  FAIL: " + name);
            System.out.println("        Expected: " + expected);
            System.out.println("        Got:      " + actual);
            failed++;
        }
    }
}
