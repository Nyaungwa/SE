/**
 * Entry point for the Optimised Implementation (Task 5).
 *
 * Component wiring — optimised dependency graph:
 *
 *   Researcher
 *       └── UI
 *             └── SubmissionController          [thin relay — 1 dependency only]
 *                       └── SubmissionService   [orchestrates business logic]
 *                               ├── Validator
 *                               ├── SubmissionRepository
 *                               ├── ReviewerSelectionService
 *                               │       └── ReviewerRepository
 *                               └── EvaluationService
 *                                       ├── EvaluationRepository
 *                                       ├── DecisionEngine
 *                                       └── NotificationService
 *                                               └── Researcher
 */
public class Main {

    public static void main(String[] args) {

        // ── Infrastructure layer ─────────────────────────────────────────
        SubmissionRepository     submissionRepository     = new SubmissionRepository();
        ReviewerRepository       reviewerRepository       = new ReviewerRepository();
        EvaluationRepository     evaluationRepository     = new EvaluationRepository();

        // ── Domain / service layer ───────────────────────────────────────
        Validator                validator                = new Validator();
        ReviewerSelectionService reviewerSelectionService = new ReviewerSelectionService(reviewerRepository);
        DecisionEngine           decisionEngine           = new DecisionEngine();
        NotificationService      notificationService      = new NotificationService();

        // ── Researcher needed by NotificationService ─────────────────────
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

        // ════════════════════════════════════════════════════════════════
        // Scenario 1: Valid submission — full optimised workflow
        // ════════════════════════════════════════════════════════════════
        System.out.println();
        TraceLogger.scenario("SCENARIO 1: Valid submission");
        System.out.println();

        String validData = "An empirical study of machine learning model "
                         + "optimisation techniques in distributed systems.";
        researcher.submitResearchOutput(validData);

        // ════════════════════════════════════════════════════════════════
        // Scenario 2: Invalid submission — alt [invalid] branch
        // ════════════════════════════════════════════════════════════════
        System.out.println();
        TraceLogger.scenario("SCENARIO 2: Invalid submission (too short)");
        System.out.println();

        String invalidData = "short";
        researcher.submitResearchOutput(invalidData);
    }
}
