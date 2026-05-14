

import java.util.Locale;

/**
 * Prints the empirical metrics report for the baseline backend.
 * Structural values are hardcoded from source analysis; runtime counters come from TraceLogger.
 */
public class MetricsReporter {

    private static final String SEP = "=".repeat(60);

    private static final int TOTAL_CLASSES = 12;
    private static final int TOTAL_LOC     = 1183;

    public static void print() {

        System.out.println();
        System.out.println(SEP);
        System.out.println("  EMPIRICAL METRICS REPORT - ORIGINAL");
        System.out.println(SEP);

        System.out.println();
        System.out.println("[METRIC 1] Interaction Count (valid submission scenario)");
        System.out.printf ("  Total method calls (CALL events)  : %d%n", TraceLogger.callCount);
        System.out.printf ("  Database calls                    : %d%n", TraceLogger.dbCallCount);

        System.out.println();
        System.out.println("[METRIC 2] Execution Time (1000 benchmark runs)");
        System.out.printf ("  Average execution time            : %s ms%n", fmt(TraceLogger.benchmarkAvgMs));
        System.out.printf ("  Min                               : %s ms%n", fmt(TraceLogger.benchmarkMinMs));
        System.out.printf ("  Max                               : %s ms%n", fmt(TraceLogger.benchmarkMaxMs));

        System.out.println();
        System.out.println("[METRIC 3] Code Complexity");
        System.out.printf ("  Total classes                     : %d%n", TOTAL_CLASSES);
        System.out.printf ("  Total lines of code               : %d%n", TOTAL_LOC);
        System.out.println("  Largest class (LOC)               : EvaluationManager (240 lines)");
        System.out.println("  Most dependencies (fan-out)       : SubmissionController (4 deps)");
        System.out.println("  Cyclomatic complexity - key class : EvaluationManager CC=6");

        System.out.println();
        System.out.println("[METRIC 4] Class Responsibilities");
        System.out.println("  SubmissionController : 5 responsibilities - validates, persists, selects reviewers, assigns, triggers evaluation");
        System.out.println("  EvaluationManager    : 4 responsibilities - receives scores, saves scores, calculates average, applies rules");
        System.out.println("  ReviewerManager      : 2 responsibilities - fetches reviewers, delegates filtering to Reviewer");
        System.out.println("  Reviewer             : 3 responsibilities - assigns review, submits score, filters reviewer list (misplaced)");
        System.out.println("  Database             : 3 responsibilities - saves submission, fetches reviewers, saves scores");
        System.out.println("  NotificationService  : 3 responsibilities - notifyAcceptance, notifyRejection, notifyRevision");
        System.out.println("  Validator            : 1 responsibility  - validates format");
        System.out.println("  UI                   : 1 responsibility  - relays submit request");
        System.out.println("  Researcher           : 1 responsibility  - initiates submission, receives notification");
        System.out.println("  Submission           : 1 responsibility  - data object");
        System.out.println("  TraceLogger          : 1 responsibility  - logging utility");
        System.out.println("  Main                 : 1 responsibility  - wires components");

        System.out.println();
        System.out.println("[METRIC 5] Fan-out Coupling (constructor dependencies)");
        System.out.println("  SubmissionController : 4 dependencies");
        System.out.println("  ReviewerManager      : 2 dependencies");
        System.out.println("  EvaluationManager    : 2 dependencies");
        System.out.println("  NotificationService  : 1 dependency");
        System.out.println("  UI                   : 1 dependency");
        System.out.println("  Researcher           : 0 dependencies");
        System.out.println("  Database             : 0 dependencies");
        System.out.println("  Validator            : 0 dependencies");
        System.out.println("  Submission           : 0 dependencies");
        System.out.println("  Reviewer             : 0 dependencies");

        System.out.println();
        System.out.println("[METRIC 6] Cyclomatic Complexity (if/else + loop branches per class)");
        System.out.println("  EvaluationManager    : CC = 6");
        System.out.println("  Reviewer             : CC = 4");
        System.out.println("  Validator            : CC = 3");
        System.out.println("  SubmissionController : CC = 2");
        System.out.println("  ReviewerManager      : CC = 1");
        System.out.println("  NotificationService  : CC = 1");

        System.out.println();
        System.out.println("[METRIC 7] Change Impact Analysis");
        System.out.println("  Scenario: Add new outcome (e.g. withdrawn)");
        System.out.println("    Classes requiring changes       : 3 - EvaluationManager, NotificationService, SubmissionController");
        System.out.println("  Scenario: Swap database layer");
        System.out.println("    Classes requiring changes       : 3 - SubmissionController, ReviewerManager, EvaluationManager");

        System.out.println();
        System.out.println(SEP);
        System.out.println("  SUMMARY");
        System.out.println(SEP);
        System.out.printf ("  System                            : ORIGINAL%n");
        System.out.printf ("  Total interactions (valid run)    : %d%n", TraceLogger.callCount);
        System.out.printf ("  DB calls (valid run)              : %d%n", TraceLogger.dbCallCount);
        System.out.printf ("  Avg execution time                : %s ms%n", fmt(TraceLogger.benchmarkAvgMs));
        System.out.printf ("  Total LOC                         : %d%n", TOTAL_LOC);
        System.out.println("  Max fan-out coupling              : 4 (SubmissionController)");
        System.out.println("  Change impact (new outcome)       : 3 classes");
        System.out.println("  Change impact (swap DB)           : 3 classes");
        System.out.println(SEP);
    }

    private static String fmt(double v) {
        return String.format(Locale.US, "%.2f", v);
    }
}
