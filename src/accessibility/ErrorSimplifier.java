package accessibility;

/**
 * ErrorSimplifier - Maps common GHCi error messages to plain English explanations.
 * Used by TTSManager to speak simplified errors aloud instead of raw GHCi output.
 */
public class ErrorSimplifier {

    /**
     * Takes a raw GHCi error line and returns a plain English explanation.
     * Returns null if no simplification is available for this error.
     */
    public static String simplify(String errorLine) {
        if (errorLine == null) return null;
        String lower = errorLine.toLowerCase();

        if (lower.contains("no instance for")) {
            return "Error: wrong type of value. Check what type your function expects.";
        }
        if (lower.contains("not in scope")) {
            return "Error: name not found. Check your spelling or missing definition.";
        }
        if (lower.contains("parse error")) {
            return "Error: syntax problem. Check your brackets, indentation, or missing symbols.";
        }
        if (lower.contains("couldn't match type") || lower.contains("couldn't match expected type")) {
            return "Error: type mismatch. Two parts of your code expect different types.";
        }
        if (lower.contains("ambiguous type")) {
            return "Error: ambiguous type. Add a type declaration to clarify what type you mean.";
        }
        if (lower.contains("infinite type")) {
            return "Error: infinite type detected. You may have a recursive type error.";
        }
        if (lower.contains("non-exhaustive patterns")) {
            return "Error: missing pattern. Your function does not handle all possible inputs.";
        }
        if (lower.contains("variable not in scope")) {
            return "Error: variable not defined. Check the name is spelled correctly.";
        }
        if (lower.contains("failed, modules loaded: none") || lower.contains("failed, no modules loaded")) {
            return "Compilation failed. Please check the errors shown above.";
        }
        if (lower.contains("ok, one module loaded") || lower.contains("ok, modules loaded")) {
            return "Compilation successful. Your program is ready to run.";
        }

        return null;
    }
}
