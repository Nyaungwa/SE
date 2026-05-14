import java.util.Locale;

/**
 * Console trace formatter. All class interactions route through here for consistent output.
 * Labels padded to 9 chars; decimals always use a dot regardless of JVM locale.
 */
public class TraceLogger {

    private static final String SEP = "=".repeat(60);
    private static final String DIV = "-".repeat(60);

    public static long    callCount      = 0;
    public static long    dbCallCount    = 0;
    public static boolean silent         = false;

    // Benchmark results set by Main after the timed loop.
    public static double benchmarkAvgMs = 0;
    public static double benchmarkMinMs = 0;
    public static double benchmarkMaxMs = 0;

    /** Zeroes callCount and dbCallCount. */
    public static void reset() {
        callCount   = 0;
        dbCallCount = 0;
    }

    public static void scenario(String title) {
        if (silent) return;
        System.out.println(SEP);
        System.out.println("  " + title);
        System.out.println(SEP);
    }

    public static void call(String from, String to, String method) {
        if (silent) return;
        callCount++;
        System.out.println("[CALL]   " + from + " -> " + to + ": " + method);
    }

    public static void returnVal(String from, String to, String value) {
        if (silent) return;
        System.out.println("[RETURN] " + from + " -> " + to + ": " + value);
    }

    public static void alt(String condition) {
        if (silent) return;
        System.out.println("[ALT]    " + condition);
    }

    public static void loop(String description) {
        if (silent) return;
        System.out.println("[LOOP]   " + description);
    }

    public static void db(String detail) {
        if (silent) return;
        dbCallCount++;
        System.out.println("[DB]     " + detail);
    }

    public static void info(String component, String message) {
        if (silent) return;
        System.out.println("[INFO]   " + component + ": " + message);
    }

    public static void result(String message) {
        if (silent) return;
        System.out.println("[RESULT] " + message);
    }

    public static void separator() {
        if (silent) return;
        System.out.println(DIV);
    }

    public static String fmt(double value) {
        return String.format(Locale.US, "%.2f", value);
    }
}
