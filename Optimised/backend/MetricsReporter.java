import java.util.Locale;

/**
 * Prints the empirical metrics report for the optimised backend.
 * Structural values are hardcoded from source analysis; runtime counters come from TraceLogger.
 */
public class MetricsReporter {

    private static final String SEP = "=".repeat(60);

    private static final int TOTAL_CLASSES = 16;
    private static final int TOTAL_LOC     = 799;

    public static void print() {

        System.out.println();
        System.out.println(SEP);
        System.out.println("  EMPIRICAL METRICS REPORT - OPTIMISED");
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
        System.out.println("  Largest class (LOC)               : ReviewerSelectionService (79 lines)");
        System.out.println("  Most dependencies (fan-out)       : SubmissionService (4 deps)");
        System.out.println("  Cyclomatic complexity - key class : DecisionEngine CC=4");

        System.out.println();
        System.out.println("[METRIC 4] Class Responsibilities");
        System.out.println("  SubmissionService          : 3 responsibilities - validate, persist, coordinate reviewer selection and evaluation");
        System.out.println("  EvaluationService          : 3 responsibilities - collect scores, trigger bulk save, trigger decision");
        System.out.println("  ReviewerSelectionService   : 2 responsibilities - fetch reviewers, filter eligible (internal)");
        System.out.println("  DecisionEngine             : 1 responsibility  - determines outcome from scores");
        System.out.println("  SubmissionController       : 1 responsibility  - relays submit request to SubmissionService");
        System.out.println("  NotificationService        : 1 responsibility  - notifyResearcher(outcome)");
        System.out.println("  SubmissionRepository       : 1 responsibility  - saves submission");
        System.out.println("  ReviewerRepository         : 1 responsibility  - fetches reviewer pool");
        System.out.println("  EvaluationRepository       : 1 responsibility  - bulk saves scores");
        System.out.println("  Validator                  : 1 responsibility  - validates format");
        System.out.println("  Reviewer                   : 1 responsibility  - domain object, submits score");
        System.out.println("  UI                         : 1 responsibility  - relays submitResearchOutput");
        System.out.println("  Researcher                 : 1 responsibility  - initiates, receives notification");
        System.out.println("  Submission                 : 1 responsibility  - data object");
        System.out.println("  TraceLogger                : 1 responsibility  - logging utility");
        System.out.println("  Main                       : 1 responsibility  - wires components");

        System.out.println();
        System.out.println("[METRIC 5] Fan-out Coupling (constructor dependencies)");
        System.out.println("  SubmissionService          : 4 dependencies");
        System.out.println("  EvaluationService          : 3 dependencies");
        System.out.println("  ReviewerSelectionService   : 1 dependency");
        System.out.println("  SubmissionController       : 1 dependency");
        System.out.println("  UI                         : 1 dependency");
        System.out.println("  NotificationService        : 0 dependencies");
        System.out.println("  DecisionEngine             : 0 dependencies");
        System.out.println("  SubmissionRepository       : 0 dependencies");
        System.out.println("  ReviewerRepository         : 0 dependencies");
        System.out.println("  EvaluationRepository       : 0 dependencies");
        System.out.println("  Researcher                 : 0 dependencies");
        System.out.println("  Reviewer                   : 0 dependencies");
        System.out.println("  Submission                 : 0 dependencies");
        System.out.println("  Validator                  : 0 dependencies");

        System.out.println();
        System.out.println("[METRIC 6] Cyclomatic Complexity (if/else + loop branches per class)");
        System.out.println("  DecisionEngine             : CC = 4");
        System.out.println("  ReviewerSelectionService   : CC = 3");
        System.out.println("  Validator                  : CC = 3");
        System.out.println("  EvaluationService          : CC = 2");
        System.out.println("  SubmissionService          : CC = 2");
        System.out.println("  NotificationService        : CC = 1");
        System.out.println("  SubmissionController       : CC = 1");

        System.out.println();
        System.out.println("[METRIC 7] Change Impact Analysis");
        System.out.println("  Scenario: Add new outcome (e.g. withdrawn)");
        System.out.println("    Classes requiring changes       : 1 - DecisionEngine only");
        System.out.println("  Scenario: Swap database layer");
        System.out.println("    Classes requiring changes       : 1 - swap the relevant Repository only");

        System.out.println();
        System.out.println(SEP);
        System.out.println("  SUMMARY");
        System.out.println(SEP);
        System.out.printf ("  System                            : OPTIMISED%n");
        System.out.printf ("  Total interactions (valid run)    : %d%n", TraceLogger.callCount);
        System.out.printf ("  DB calls (valid run)              : %d%n", TraceLogger.dbCallCount);
        System.out.printf ("  Avg execution time                : %s ms%n", fmt(TraceLogger.benchmarkAvgMs));
        System.out.printf ("  Total LOC                         : %d%n", TOTAL_LOC);
        System.out.println("  Max fan-out coupling              : 4 (SubmissionService)");
        System.out.println("  Change impact (new outcome)       : 1 class");
        System.out.println("  Change impact (swap DB)           : 1 class");
        System.out.println(SEP);
    }

    private static String fmt(double v) {
        return String.format(Locale.US, "%.2f", v);
    }
}
