
public class Main {

    public static void main(String[] args) {

        // -- Infrastructure layer --------------------------------------------
        SubmissionRepository     submissionRepository     = new SubmissionRepository();
        ReviewerRepository       reviewerRepository       = new ReviewerRepository();
        EvaluationRepository     evaluationRepository     = new EvaluationRepository();

        // -- Domain / service layer ------------------------------------------
        Validator                validator                = new Validator();
        ReviewerSelectionService reviewerSelectionService = new ReviewerSelectionService(reviewerRepository);
        DecisionEngine           decisionEngine           = new DecisionEngine();
        NotificationService      notificationService      = new NotificationService();

        // -- Researcher needed by NotificationService ------------------------
        Researcher researcher = new Researcher("Edwin Nyaungwa");

        EvaluationService evaluationService = new EvaluationService(
                evaluationRepository, decisionEngine, notificationService);

        SubmissionService submissionService = new SubmissionService(
                validator, submissionRepository,
                reviewerSelectionService, evaluationService);

        SubmissionController submissionController = new SubmissionController(submissionService);
        submissionController.setResearcher(researcher);

        UI ui = new UI(submissionController);
        researcher.setUI(ui);

        // ====================================================================
        // Scenario 1: Valid submission -- full optimised workflow
        // ====================================================================
        TraceLogger.reset();

        System.out.println();
        TraceLogger.scenario("SCENARIO 1: Valid submission");
        System.out.println();

        String validData = "An empirical study of machine learning model "
                         + "optimisation techniques in distributed systems.";
        researcher.submitResearchOutput(validData);

        // ====================================================================
        // Scenario 2: Invalid submission -- alt [invalid] branch
        // ====================================================================
        System.out.println();
        TraceLogger.scenario("SCENARIO 2: Invalid submission (too short)");
        System.out.println();

        String invalidData = "short";
        researcher.submitResearchOutput(invalidData);

        // ====================================================================
        // Benchmark: 1000 silent runs of the valid submission workflow
        // ====================================================================
        TraceLogger.silent = true;

        // Redirect System.out to suppress raw println calls in other classes
        java.io.PrintStream realOut = System.out;
        System.setOut(new java.io.PrintStream(new java.io.OutputStream() {
            public void write(int b) {}
        }));

        long totalNs = 0;
        long minNs   = Long.MAX_VALUE;
        long maxNs   = Long.MIN_VALUE;

        for (int i = 0; i < 1000; i++) {
            SubmissionRepository     bSubRepo  = new SubmissionRepository();
            ReviewerRepository       bRevRepo  = new ReviewerRepository();
            EvaluationRepository     bEvalRepo = new EvaluationRepository();
            Validator                bVal      = new Validator();
            ReviewerSelectionService bRss      = new ReviewerSelectionService(bRevRepo);
            DecisionEngine           bDe       = new DecisionEngine();
            NotificationService      bNs       = new NotificationService();
            Researcher               bRes      = new Researcher("Edwin Nyaungwa");
            EvaluationService        bEs       = new EvaluationService(bEvalRepo, bDe, bNs);
            SubmissionService        bSs       = new SubmissionService(bVal, bSubRepo, bRss, bEs);
            SubmissionController     bSc       = new SubmissionController(bSs);
            bSc.setResearcher(bRes);
            UI bUi = new UI(bSc);
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
