package baseline;

/**
 * Entry point for the Baseline Implementation (Task 1).
 *
 * Wires all components together and demonstrates two scenarios:
 *   1. Valid submission   -- full workflow executes end-to-end
 *   2. Invalid submission -- alt [invalid] branch returns error early
 *
 * Component wiring follows the dependency graph implied by the diagram:
 *
 *   Researcher
 *       +-- UI
 *             +-- SubmissionController
 *                       +-- Validator
 *                       +-- Database
 *                       +-- ReviewerManager
 *                       |       +-- Database
 *                       |       +-- Reviewer (delegate for filter calls)
 *                       +-- EvaluationManager
 *                               +-- Database
 *                               +-- NotificationService
 *                                         +-- Researcher
 */
public class Main {

    public static void main(String[] args) {

        // -- Shared infrastructure -------------------------------------------
        Database database = new Database();

        // -- Researcher (needed by NotificationService) ----------------------
        Researcher researcher = new Researcher("Edwin Nyaungwa");

        // -- NotificationService ---------------------------------------------
        NotificationService notificationService =
                new NotificationService(researcher);

        // -- EvaluationManager -----------------------------------------------
        EvaluationManager evaluationManager =
                new EvaluationManager(database, notificationService);

        // -- Validator -------------------------------------------------------
        Validator validator = new Validator();

        // -- ReviewerManager (with delegate Reviewer for filter calls) -------
        // [DIAGRAM FLAW] A Reviewer instance is passed as a delegate so
        // ReviewerManager can call filterConflicts() and checkWorkload()
        // on it -- misplaced responsibility preserved from the diagram.
        Reviewer reviewerDelegate = new Reviewer("DELEGATE", "Filter Delegate", 0, false);
        ReviewerManager reviewerManager =
                new ReviewerManager(database, reviewerDelegate);

        // -- SubmissionController --------------------------------------------
        SubmissionController submissionController =
                new SubmissionController(validator, database,
                                         reviewerManager, evaluationManager);

        // -- UI --------------------------------------------------------------
        UI ui = new UI(submissionController);

        // -- Complete the Researcher <-> UI circular reference ---------------
        researcher.setUI(ui);

        // ====================================================================
        // Scenario 1: Valid submission -- full workflow
        // ====================================================================
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
    }
}
