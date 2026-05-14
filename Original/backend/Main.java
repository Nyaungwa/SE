

/**
 * Wires the baseline component graph and runs the two demonstration scenarios,
 * then benchmarks the valid submission workflow 1000 times.
 */
public class Main {

    public static void main(String[] args) {

        Database database = new Database();
        Researcher researcher = new Researcher("Edwin Nyaungwa");
        NotificationService notificationService = new NotificationService(researcher);
        EvaluationManager evaluationManager = new EvaluationManager(database, notificationService);
        Validator validator = new Validator();

        // Reviewer delegate is required because ReviewerManager incorrectly calls
        // filterConflicts() and checkWorkload() on a Reviewer instance — baseline flaw.
        Reviewer reviewerDelegate = new Reviewer("DELEGATE", "Filter Delegate", 0, false);
        ReviewerManager reviewerManager = new ReviewerManager(database, reviewerDelegate);

        SubmissionController submissionController =
                new SubmissionController(validator, database, reviewerManager, evaluationManager);

        UI ui = new UI(submissionController);
        researcher.setUI(ui);

        String validData = "An empirical study of machine learning model "
                         + "optimisation techniques in distributed systems.";

        // ====================================================================
        // Scenario 1: Valid submission
        // ====================================================================
        TraceLogger.reset();

        System.out.println();
        TraceLogger.scenario("SCENARIO 1: Valid submission");
        System.out.println();

        researcher.submitResearchOutput(validData);

        // ====================================================================
        // Scenario 2: Invalid submission
        // ====================================================================
        System.out.println();
        TraceLogger.scenario("SCENARIO 2: Invalid submission (too short)");
        System.out.println();

        researcher.submitResearchOutput("short");

        // ====================================================================
        // Benchmark: 1000 silent runs of the valid submission workflow
        // ====================================================================
        TraceLogger.silent = true;

        // Redirect System.out to silence raw println calls in other classes during the benchmark.
        java.io.PrintStream realOut = System.out;
        System.setOut(new java.io.PrintStream(new java.io.OutputStream() {
            public void write(int b) {}
        }));

        long totalNs = 0;
        long minNs   = Long.MAX_VALUE;
        long maxNs   = Long.MIN_VALUE;

        for (int i = 0; i < 1000; i++) {
            Database            bDb       = new Database();
            Researcher          bRes      = new Researcher("Edwin Nyaungwa");
            NotificationService bNs       = new NotificationService(bRes);
            EvaluationManager   bEm       = new EvaluationManager(bDb, bNs);
            Validator           bVal      = new Validator();
            Reviewer            bDelegate = new Reviewer("DELEGATE", "Filter Delegate", 0, false);
            ReviewerManager     bRm       = new ReviewerManager(bDb, bDelegate);
            SubmissionController bSc      = new SubmissionController(bVal, bDb, bRm, bEm);
            UI                  bUi       = new UI(bSc);
            bRes.setUI(bUi);

            long start = System.nanoTime();
            bRes.submitResearchOutput(validData);
            long end   = System.nanoTime();

            long dur = end - start;
            totalNs += dur;
            if (dur < minNs) minNs = dur;
            if (dur > maxNs) maxNs = dur;
        }

        TraceLogger.silent = false;
        System.setOut(realOut);

        TraceLogger.benchmarkAvgMs = (totalNs / 1_000_000.0) / 1000.0;
        TraceLogger.benchmarkMinMs =  minNs   / 1_000_000.0;
        TraceLogger.benchmarkMaxMs =  maxNs   / 1_000_000.0;

        MetricsReporter.print();
    }
}
