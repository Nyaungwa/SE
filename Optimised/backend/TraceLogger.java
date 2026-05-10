import java.util.Locale;

/**
 * Centralised console output formatter for the optimised sequence trace.
 * Same logging style as the baseline for direct comparison in Task 6.
 */
public class TraceLogger {

    private static final String SEP = "=".repeat(60);
    private static final String DIV = "-".repeat(60);

    public static void scenario(String title) {
        System.out.println(SEP);
        System.out.println("  " + title);
        System.out.println(SEP);
    }

    public static void call(String from, String to, String method) {
        System.out.println("[CALL]   " + from + " -> " + to + ": " + method);
    }

    public static void returnVal(String from, String to, String value) {
        System.out.println("[RETURN] " + from + " -> " + to + ": " + value);
    }

    public static void alt(String condition) {
        System.out.println("[ALT]    " + condition);
    }

    public static void loop(String description) {
        System.out.println("[LOOP]   " + description);
    }

    public static void db(String detail) {
        System.out.println("[DB]     " + detail);
    }

    public static void info(String component, String message) {
        System.out.println("[INFO]   " + component + ": " + message);
    }

    public static void result(String message) {
        System.out.println("[RESULT] " + message);
    }

    public static void separator() {
        System.out.println(DIV);
    }

    public static String fmt(double value) {
        return String.format(Locale.US, "%.2f", value);
    }
}
