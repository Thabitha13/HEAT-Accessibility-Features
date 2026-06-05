import accessibility.ErrorSimplifier;

/**
 * Unit tests for ErrorSimplifier.
 * Tests all 10 error pattern mappings.
 * Run with: javac -cp ../../src ErrorSimplifierTest.java && java -cp .:../../src ErrorSimplifierTest
 */
public class ErrorSimplifierTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=== ErrorSimplifier Unit Tests ===\n");

        // Pattern 1: no instance for
        assertContains("no instance for",
            "    • No instance for 'Fractional Int' arising from literal '3.5'",
            "type of value");

        // Pattern 2: not in scope
        assertContains("not in scope",
            "    Variable not in scope: myFunc :: Int -> Int",
            "not found");

        // Pattern 3: parse error
        assertContains("parse error (on input)",
            "parse error (on input '=')",
            "syntax");

        // Pattern 4: couldn't match type
        assertContains("couldn't match type",
            "Couldn't match type 'Int' with 'Bool'",
            "Type mismatch");

        // Pattern 5: couldn't match expected type
        assertContains("couldn't match expected type",
            "Couldn't match expected type 'String' with actual type 'Int'",
            "Type mismatch");

        // Pattern 6: ambiguous type
        assertContains("ambiguous type",
            "Ambiguous type variable 'a0' arising from use of 'show'",
            "type declaration");

        // Pattern 7: infinite type
        assertContains("infinite type",
            "Occurs check: cannot construct the infinite type: a ~ [a]",
            "Recursive");

        // Pattern 8: non-exhaustive patterns
        assertContains("non-exhaustive patterns",
            "Non-exhaustive patterns in function f",
            "all possible inputs");

        // Pattern 9: variable not in scope (distinct from "not in scope")
        assertContains("variable not in scope",
            "Variable not in scope: x",
            "name not found");

        // Pattern 10: failed modules loaded
        assertContains("failed, modules loaded: none",
            "[1 of 1] Compiling Main  ( Main.hs, interpreted )\nFailed, modules loaded: none.",
            "failed");

        // Pattern 11: ok modules loaded
        assertContains("ok, modules loaded",
            "Ok, one module loaded.",
            "successful");

        // Null / no match — should return null
        assertNull("no match returns null",
            "This is just a normal output line");

        // Case insensitivity check
        assertContains("case insensitive",
            "COULDN'T MATCH TYPE 'Int' with 'Bool'",
            "Type mismatch");

        System.out.println("\n=== Results: " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }

    private static void assertContains(String testName, String input, String expectedFragment) {
        String result = ErrorSimplifier.simplify(input);
        if (result != null && result.toLowerCase().contains(expectedFragment.toLowerCase())) {
            System.out.println("  PASS: " + testName);
            passed++;
        } else {
            System.out.println("  FAIL: " + testName);
            System.out.println("        Input:    " + input);
            System.out.println("        Expected to contain: " + expectedFragment);
            System.out.println("        Got: " + result);
            failed++;
        }
    }

    private static void assertNull(String testName, String input) {
        String result = ErrorSimplifier.simplify(input);
        if (result == null) {
            System.out.println("  PASS: " + testName);
            passed++;
        } else {
            System.out.println("  FAIL: " + testName);
            System.out.println("        Input:    " + input);
            System.out.println("        Expected: null");
            System.out.println("        Got:      " + result);
            failed++;
        }
    }
}
