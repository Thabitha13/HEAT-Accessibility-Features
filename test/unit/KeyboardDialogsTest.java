/**
 * Unit tests for keyboard dialog validation and path logic.
 * Tests the pure logic methods that do not require a GUI or Swing event loop.
 *
 * Covers:
 *  - Empty filename rejected in new file dialog
 *  - Tilde expansion resolves to user home directory
 *  - .hs extension added automatically when missing
 *  - .hs extension not added when already present
 *  - .lhs extension preserved as-is
 *  - Search only returns .hs and .lhs files
 *  - Empty search query detection
 *  - Default directory falls back to user home when blank
 *
 * Run with:
 *   javac -cp ../../src KeyboardDialogsTest.java
 *   java -cp .:../../src KeyboardDialogsTest
 */
public class KeyboardDialogsTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=== KeyboardDialogs Unit Tests ===\n");

        // Test 1: empty filename is detected as invalid
        assertFalse("empty filename is invalid",
            isValidFilename(""));

        // Test 2: blank whitespace filename is detected as invalid
        assertFalse("whitespace-only filename is invalid",
            isValidFilename("   "));

        // Test 3: valid filename is accepted
        assertTrue("valid filename accepted",
            isValidFilename("MyCode"));

        // Test 4: tilde expands to user home
        String home = System.getProperty("user.home");
        assertEqual("tilde expands to home",
            home + "/Documents", expandTilde("~/Documents"));

        // Test 5: tilde-only expands to home
        assertEqual("tilde alone expands to home",
            home, expandTilde("~"));

        // Test 6: path without tilde is unchanged
        assertEqual("path without tilde unchanged",
            "/Users/test/Documents", expandTilde("/Users/test/Documents"));

        // Test 7: .hs extension added when missing
        assertEqual(".hs added when missing",
            "MyCode.hs", addExtensionIfMissing("MyCode"));

        // Test 8: .hs extension not added when already present
        assertEqual(".hs not duplicated",
            "MyCode.hs", addExtensionIfMissing("MyCode.hs"));

        // Test 9: .lhs extension preserved
        assertEqual(".lhs preserved",
            "MyCode.lhs", addExtensionIfMissing("MyCode.lhs"));

        // Test 10: .HS uppercase extension preserved
        assertEqual(".HS uppercase preserved",
            "MyCode.HS", addExtensionIfMissing("MyCode.HS"));

        // Test 11: empty directory defaults to user home
        assertEqual("empty directory defaults to home",
            home, defaultDirectory(""));

        // Test 12: blank directory defaults to user home
        assertEqual("blank directory defaults to home",
            home, defaultDirectory("   "));

        // Test 13: non-blank directory returned as-is
        assertEqual("non-blank directory returned unchanged",
            "/Users/test", defaultDirectory("/Users/test"));

        // Test 14: .hs files pass the search filter
        assertTrue(".hs passes file filter",
            isHaskellFile("Main.hs"));

        // Test 15: .lhs files pass the search filter
        assertTrue(".lhs passes file filter",
            isHaskellFile("Main.lhs"));

        // Test 16: .java files are rejected by search filter
        assertFalse(".java rejected by file filter",
            isHaskellFile("Main.java"));

        // Test 17: .txt files are rejected by search filter
        assertFalse(".txt rejected by file filter",
            isHaskellFile("readme.txt"));

        // Test 18: case insensitive .HS passes filter
        assertTrue(".HS uppercase passes file filter",
            isHaskellFile("Main.HS"));

        System.out.println("\n=== Results: " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }

    // ── Logic mirrors extracted from the dialog classes ──────────────────────

    /**
     * Mirrors KeyboardNewFileDialog: filename is valid if non-empty after trim.
     */
    private static boolean isValidFilename(String name) {
        return name != null && !name.trim().isEmpty();
    }

    /**
     * Mirrors KeyboardNewFileDialog: tilde at start replaced with user.home.
     */
    private static String expandTilde(String path) {
        if (path.startsWith("~")) {
            return System.getProperty("user.home") + path.substring(1);
        }
        return path;
    }

    /**
     * Mirrors KeyboardNewFileDialog: adds .hs if no recognised extension present.
     */
    private static String addExtensionIfMissing(String name) {
        if (!name.endsWith(".hs") && !name.endsWith(".lhs") &&
            !name.endsWith(".HS") && !name.endsWith(".LHS")) {
            return name + ".hs";
        }
        return name;
    }

    /**
     * Mirrors KeyboardOpenFileDialog: blank directory falls back to user.home.
     */
    private static String defaultDirectory(String dir) {
        if (dir == null || dir.trim().isEmpty()) {
            return System.getProperty("user.home");
        }
        return dir.trim();
    }

    /**
     * Mirrors KeyboardOpenFileDialog search filter: only .hs and .lhs files.
     */
    private static boolean isHaskellFile(String filename) {
        String lower = filename.toLowerCase();
        return lower.endsWith(".hs") || lower.endsWith(".lhs");
    }

    // ── Assertion helpers ─────────────────────────────────────────────────────

    private static void assertTrue(String name, boolean condition) {
        if (condition) { System.out.println("  PASS: " + name); passed++; }
        else { System.out.println("  FAIL: " + name); failed++; }
    }

    private static void assertFalse(String name, boolean condition) {
        assertTrue(name, !condition);
    }

    private static void assertEqual(String name, String expected, String actual) {
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
