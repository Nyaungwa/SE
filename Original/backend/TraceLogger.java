package baseline;

import java.util.Locale;

/**
 * Centralised console output formatter for the baseline sequence trace.
 *
 * All System.out calls in the baseline pass through here so that:
 *   - labels are consistently padded to 9 characters
 *   - every label uses plain ASCII only (no Unicode arrows or box-drawing)
 *   - decimal values always use a dot separator regardless of JVM locale
 *
 * Label widths (all padded to 9 chars so content aligns):
 *   [CALL]    [RETURN]  [ALT]     [LOOP]    [DB]      [INFO]    [RESULT]
 */
public class TraceLogger {

    private static final String SEP = "=".repeat(60);
    private static final String DIV = "-".repeat(60);

    // ------------------------------------------------------------------
    // Structural markers
    // ------------------------------------------------------------------

    /** Prints the scenario banner (heavy separator + title + heavy separator). */
    public static void scenario(String title) {
        System.out.println(SEP);
        System.out.println("  " + title);
        System.out.println(SEP);
    }

    /** [CALL] from -> to: method() */
    public static void call(String from, String to, String method) {
        System.out.println("[CALL]   " + from + " -> " + to + ": " + method);
    }

    /** [RETURN] from -> to: value */
    public static void returnVal(String from, String to, String value) {
        System.out.println("[RETURN] " + from + " -> " + to + ": " + value);
    }

    /** [ALT] condition */
    public static void alt(String condition) {
        System.out.println("[ALT]    " + condition);
    }

    /** [LOOP] description */
    public static void loop(String description) {
        System.out.println("[LOOP]   " + description);
    }

    // ------------------------------------------------------------------
    // Informational
    // ------------------------------------------------------------------

    /** [DB] detail -- used for persistence operations inside Database */
    public static void db(String detail) {
        System.out.println("[DB]     " + detail);
    }

    /** [INFO] component: message -- used for supplementary context */
    public static void info(String component, String message) {
        System.out.println("[INFO]   " + component + ": " + message);
    }

    /** [RESULT] message -- used for final workflow outcomes */
    public static void result(String message) {
        System.out.println("[RESULT] " + message);
    }

    // ------------------------------------------------------------------
    // Layout helpers
    // ------------------------------------------------------------------

    /** Prints a light horizontal divider (60 dashes). */
    public static void separator() {
        System.out.println(DIV);
    }

    /**
     * Formats a double to two decimal places using a dot separator,
     * independent of the JVM default locale.
     */
    public static String fmt(double value) {
        return String.format(Locale.US, "%.2f", value);
    }
}
