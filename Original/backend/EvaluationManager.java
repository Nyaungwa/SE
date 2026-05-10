package baseline;

import java.util.ArrayList;
import java.util.List;

/**
 * [DIAGRAM] EvaluationManager lifeline.
 *
 * Participates in the following interactions:
 *   1. SubmissionController  -> EvaluationManager: startEvaluation()
 *   2. Reviewer              -> EvaluationManager: submitScore(score)      [loop: each reviewer]
 *   3. EvaluationManager     -> Database:          saveScore(score)
 *   4. EvaluationManager self-call:                calculateAverage()     [self-call]
 *   5. EvaluationManager self-call:                checkConsensus()       [self-call]
 *   6. EvaluationManager self-call:                applyRules()           [self-call]
 *   7. alt [accepted]  -> NotificationService:      notifyAcceptance()
 *      alt [rejected]  -> NotificationService:      notifyRejection()
 *      alt [revision]  -> NotificationService:      notifyRevision()
 *
 * ------------------------------------------------------------------
 * BASELINE DESIGN FLAWS (intentionally preserved from diagram):
 *
 * A) Three exposed self-calls (calculateAverage, checkConsensus,
 *    applyRules) should be encapsulated as internal implementation
 *    details of a single evaluate() method.
 *
 * B) The alt decision block has three separate branches with three
 *    separate notification calls -- this scattered conditional logic
 *    should be replaced with a decision table.
 *
 * Both flaws are preserved verbatim for Task 1.
 * ------------------------------------------------------------------
 */
public class EvaluationManager {

    // Decision thresholds
    private static final double ACCEPTANCE_THRESHOLD  = 75.0;
    private static final double REJECTION_THRESHOLD   = 50.0;
    private static final double CONSENSUS_VARIANCE    = 15.0; // max allowed score spread

    private final Database            database;
    private final NotificationService notificationService;

    private final List<Double>  scores      = new ArrayList<>();
    private final List<String>  reviewerIds = new ArrayList<>();

    public EvaluationManager(Database database, NotificationService notificationService) {
        this.database            = database;
        this.notificationService = notificationService;
    }

    // ------------------------------------------------------------------
    // Interaction 1 -- startEvaluation
    // [DIAGRAM] SubmissionController -> EvaluationManager: startEvaluation()
    // ------------------------------------------------------------------

    /**
     * [DIAGRAM] SubmissionController -> EvaluationManager: startEvaluation()
     *
     * Initialises the evaluation session, resetting any prior state.
     */
    public void startEvaluation() {
        scores.clear();
        reviewerIds.clear();
    }

    // ------------------------------------------------------------------
    // Interaction 2 + 3 -- submitScore & saveScore
    // [DIAGRAM] loop [each reviewer]: Reviewer -> EvaluationManager: submitScore(score)
    //           EvaluationManager -> Database: saveScore(score)
    // ------------------------------------------------------------------

    /**
     * [DIAGRAM] Reviewer -> EvaluationManager: submitScore(score)
     *           EvaluationManager -> Database: saveScore(score)
     *
     * Accepts a score from an individual reviewer, persists it to the
     * database, and records it locally for later aggregation.
     *
     * @param reviewerId the reviewer submitting the score
     * @param score      the numeric evaluation score
     */
    public void submitScore(String reviewerId, double score) {
        // [DIAGRAM] EvaluationManager -> Database: saveScore(score)
        TraceLogger.call("EvaluationManager", "Database",
                         "saveScore(score=" + TraceLogger.fmt(score)
                         + ", reviewer=" + reviewerId + ")");
        database.saveScore(reviewerId, score);

        scores.add(score);
        reviewerIds.add(reviewerId);
    }

    // ------------------------------------------------------------------
    // Self-call 1 -- calculateAverage
    // [DIAGRAM] EvaluationManager self-call: calculateAverage()
    // ------------------------------------------------------------------

    /**
     * [DIAGRAM] EvaluationManager self-call: calculateAverage()
     *
     * Computes the arithmetic mean of all submitted scores.
     *
     * NOTE (Baseline flaw): Exposed as a visible interaction in the diagram
     * rather than being a private implementation detail.
     *
     * @return average score
     */
    private double calculateAverage() {
        if (scores.isEmpty()) {
            TraceLogger.info("EvaluationManager", "average = 0.00 (no scores)");
            return 0.0;
        }
        double sum = 0;
        for (double s : scores) sum += s;
        double avg = Math.round((sum / scores.size()) * 100.0) / 100.0;
        TraceLogger.info("EvaluationManager", "average = " + TraceLogger.fmt(avg));
        return avg;
    }

    // ------------------------------------------------------------------
    // Self-call 2 -- checkConsensus
    // [DIAGRAM] EvaluationManager self-call: checkConsensus()
    // ------------------------------------------------------------------

    /**
     * [DIAGRAM] EvaluationManager self-call: checkConsensus()
     *
     * Determines whether reviewers reached consensus by checking that
     * the spread between the highest and lowest score is within tolerance.
     *
     * NOTE (Baseline flaw): Exposed as a visible interaction in the diagram.
     *
     * @return true if consensus is reached, false otherwise
     */
    private boolean checkConsensus() {
        if (scores.size() < 2) {
            TraceLogger.info("EvaluationManager", "spread=0.00, consensus=true (single reviewer)");
            return true;
        }
        double min = scores.stream().mapToDouble(Double::doubleValue).min().orElse(0);
        double max = scores.stream().mapToDouble(Double::doubleValue).max().orElse(0);
        boolean consensus = (max - min) <= CONSENSUS_VARIANCE;
        TraceLogger.info("EvaluationManager",
                         "spread=" + TraceLogger.fmt(max - min) + ", consensus=" + consensus);
        return consensus;
    }

    // ------------------------------------------------------------------
    // Self-call 3 -- applyRules
    // [DIAGRAM] EvaluationManager self-call: applyRules()
    // ------------------------------------------------------------------

    /**
     * [DIAGRAM] EvaluationManager self-call: applyRules()
     *
     * Applies the decision rules to determine the final submission outcome.
     * Returns one of: "accepted", "rejected", or "revision".
     *
     * NOTE (Baseline flaw): Exposed as a visible interaction. The decision
     * logic is also scattered -- it should be replaced with a decision table
     * (Task 3). Preserved as specified.
     *
     * @param average   the calculated average score
     * @param consensus whether reviewers reached consensus
     * @return outcome string
     */
    private String applyRules(double average, boolean consensus) {
        String outcome;

        if (average >= ACCEPTANCE_THRESHOLD && consensus) {
            outcome = "accepted";
        } else if (average < REJECTION_THRESHOLD || !consensus) {
            outcome = "rejected";
        } else {
            outcome = "revision";
        }

        TraceLogger.info("EvaluationManager", "outcome = " + outcome.toUpperCase());
        return outcome;
    }

    // ------------------------------------------------------------------
    // Finalise evaluation -- triggers self-calls + alt notification block
    // Called by SubmissionController after the score loop completes
    // ------------------------------------------------------------------

    /**
     * Invoked by SubmissionController after all reviewer scores have been
     * submitted. Executes the three diagram self-calls in sequence, then
     * routes to the correct notification branch.
     *
     * Sequence:
     *   calculateAverage()  <- self-call
     *   checkConsensus()    <- self-call
     *   applyRules()        <- self-call
     *   alt block:
     *     [accepted]  -> notifyAcceptance()
     *     [rejected]  -> notifyRejection()
     *     [revision]  -> notifyRevision()
     */
    public void finalizeEvaluation() {

        // [DIAGRAM] Self-call: calculateAverage()
        TraceLogger.info("EvaluationManager", "[self-call] calculateAverage()");
        double average = calculateAverage();

        // [DIAGRAM] Self-call: checkConsensus()
        TraceLogger.info("EvaluationManager", "[self-call] checkConsensus()");
        boolean consensus = checkConsensus();

        // [DIAGRAM] Self-call: applyRules()
        TraceLogger.info("EvaluationManager", "[self-call] applyRules()");
        String outcome = applyRules(average, consensus);

        System.out.println();

        // ------------------------------------------------------------------
        // [DIAGRAM] alt block -- scattered conditional notification dispatch
        // ------------------------------------------------------------------
        if ("accepted".equals(outcome)) {
            // [DIAGRAM] alt [accepted]: EvaluationManager -> NotificationService: notifyAcceptance()
            TraceLogger.alt("[accepted]");
            TraceLogger.call("EvaluationManager", "NotificationService", "notifyAcceptance()");
            notificationService.notifyAcceptance();

        } else if ("rejected".equals(outcome)) {
            // [DIAGRAM] alt [rejected]: EvaluationManager -> NotificationService: notifyRejection()
            TraceLogger.alt("[rejected]");
            TraceLogger.call("EvaluationManager", "NotificationService", "notifyRejection()");
            notificationService.notifyRejection();

        } else {
            // [DIAGRAM] alt [revision]: EvaluationManager -> NotificationService: notifyRevision()
            TraceLogger.alt("[revision]");
            TraceLogger.call("EvaluationManager", "NotificationService", "notifyRevision()");
            notificationService.notifyRevision();
        }
    }
}
