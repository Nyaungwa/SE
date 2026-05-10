package baseline;

import java.util.List;
import java.util.UUID;

/**
 * [DIAGRAM] SubmissionController lifeline -- the central orchestrator.
 *
 * Participates in the following interactions exactly as specified:
 *
 *   Receives:
 *     UI -> SubmissionController: submit(data)
 *
 *   Sends:
 *     SubmissionController -> Validator:       validateFormat(data)
 *     SubmissionController -> Database:        saveSubmission(data)   [alt: valid]
 *     SubmissionController -> ReviewerManager: getAvailableReviewers()
 *     SubmissionController -> Reviewer:        assignReview()         [loop]
 *     SubmissionController -> EvaluationManager: startEvaluation()
 *
 *   Returns:
 *     return error                                                    [alt: invalid]
 *
 * ------------------------------------------------------------------
 * BASELINE DESIGN FLAWS (intentionally preserved from diagram):
 *
 * A) SubmissionController is coupled to FOUR separate components
 *    (Validator, Database, ReviewerManager, EvaluationManager).
 *    This violates the Low Coupling GRASP principle and makes
 *    SubmissionController a "God Controller".
 *
 * B) SubmissionController calls Database directly for saveSubmission()
 *    instead of delegating persistence entirely to a repository layer.
 *
 * C) SubmissionController orchestrates both the reviewer assignment
 *    loop AND triggers evaluation -- these are two distinct concerns.
 *
 * All flaws are preserved verbatim for Task 1.
 * ------------------------------------------------------------------
 */
public class SubmissionController {

    private final Validator         validator;
    private final Database          database;
    private final ReviewerManager   reviewerManager;
    private final EvaluationManager evaluationManager;

    public SubmissionController(Validator         validator,
                                Database          database,
                                ReviewerManager   reviewerManager,
                                EvaluationManager evaluationManager) {
        this.validator         = validator;
        this.database          = database;
        this.reviewerManager   = reviewerManager;
        this.evaluationManager = evaluationManager;
    }

    // ------------------------------------------------------------------
    // [DIAGRAM] UI -> SubmissionController: submit(data)
    // ------------------------------------------------------------------

    /**
     * [DIAGRAM] UI -> SubmissionController: submit(data)
     *
     * Central workflow method. Executes all interactions in the sequence
     * diagram in order, preserving every interaction and control structure.
     *
     * Flow:
     *   1. validateFormat(data)              -> Validator
     *   2. alt [invalid]: return error        (early exit)
     *   3. alt [valid]:
     *      a. saveSubmission(data)            -> Database
     *      b. getAvailableReviewers()         -> ReviewerManager
     *      c. loop [assign reviewers]         -> Reviewer
     *      d. startEvaluation()               -> EvaluationManager
     *      e. loop [each reviewer submits]    -> (Reviewer -> EvaluationManager)
     *      f. finalizeEvaluation()            -> EvaluationManager
     *
     * @param data the raw submission data
     * @return result message ("ERROR:..." or "SUCCESS:...")
     */
    public String submit(String data) {

        // ------------------------------------------------------------------
        // [DIAGRAM] SubmissionController -> Validator: validateFormat(data)
        // ------------------------------------------------------------------
        TraceLogger.call("SubmissionController", "Validator", "validateFormat(data)");
        boolean isValid = validator.validateFormat(data);
        System.out.println();

        // ------------------------------------------------------------------
        // [DIAGRAM] alt [invalid]: return error
        // ------------------------------------------------------------------
        if (!isValid) {
            TraceLogger.alt("[invalid] - return error");
            String errorMsg = "ERROR: Submission format invalid -- workflow aborted.";
            TraceLogger.returnVal("SubmissionController", "UI", errorMsg);
            return errorMsg;
        }

        // ------------------------------------------------------------------
        // [DIAGRAM] alt [valid]
        // ------------------------------------------------------------------
        TraceLogger.alt("[valid] - continuing workflow");

        // [DIAGRAM] SubmissionController -> Database: saveSubmission(data) -> confirmation
        TraceLogger.call("SubmissionController", "Database", "saveSubmission(data)");
        Submission submission = new Submission(generateId(), data);
        String confirmation = database.saveSubmission(submission);
        System.out.println();

        // [DIAGRAM] SubmissionController -> ReviewerManager: getAvailableReviewers()
        //           ReviewerManager -> SubmissionController: filteredReviewers
        TraceLogger.call("SubmissionController", "ReviewerManager", "getAvailableReviewers()");
        List<Reviewer> filteredReviewers = reviewerManager.getAvailableReviewers();
        System.out.println();

        // ------------------------------------------------------------------
        // [DIAGRAM] loop [assign reviewers]: assignReview() -> Reviewer
        // ------------------------------------------------------------------
        TraceLogger.loop("assign reviewers [for each eligible reviewer]");
        for (Reviewer reviewer : filteredReviewers) {
            // [DIAGRAM] SubmissionController -> Reviewer: assignReview()
            TraceLogger.call("SubmissionController", "Reviewer:" + reviewer.getId(), "assignReview()");
            reviewer.assignReview(submission);
        }
        System.out.println();

        // [DIAGRAM] SubmissionController -> EvaluationManager: startEvaluation()
        TraceLogger.call("SubmissionController", "EvaluationManager", "startEvaluation()");
        evaluationManager.startEvaluation();
        System.out.println();

        // ------------------------------------------------------------------
        // [DIAGRAM] loop [each reviewer]: Reviewer -> EvaluationManager: submitScore(score)
        //           EvaluationManager   -> Database: saveScore(score)
        // ------------------------------------------------------------------
        TraceLogger.loop("each reviewer submits score [for each reviewer]");
        for (Reviewer reviewer : filteredReviewers) {
            // [DIAGRAM] Reviewer -> EvaluationManager: submitScore(score)
            reviewer.submitScoreTo(evaluationManager);
        }
        System.out.println();

        // [DIAGRAM] EvaluationManager self-calls + alt notification block
        //           (calculateAverage, checkConsensus, applyRules, notify*)
        TraceLogger.call("SubmissionController", "EvaluationManager", "finalizeEvaluation()");
        evaluationManager.finalizeEvaluation();
        System.out.println();

        String result = "SUCCESS: Submission " + submission.getId() + " processed.";
        TraceLogger.result(result);
        return result;
    }

    // ------------------------------------------------------------------
    // Private helpers
    // ------------------------------------------------------------------

    private String generateId() {
        return "SUB-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
