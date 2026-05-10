package baseline;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * [DIAGRAM] Reviewer lifeline.
 *
 * Participates in the following interactions:
 *   1. ReviewerManager -> Reviewer: filterConflicts(reviewerList)   [DESIGN FLAW -- see below]
 *   2. ReviewerManager -> Reviewer: checkWorkload(reviewerList)     [DESIGN FLAW -- see below]
 *   3. SubmissionController -> Reviewer: assignReview()             (loop: assign reviewers)
 *   4. Reviewer -> EvaluationManager: submitScore(score)            (loop: each reviewer)
 *
 * ------------------------------------------------------------------
 * BASELINE DESIGN FLAW (intentionally preserved from diagram):
 * filterConflicts() and checkWorkload() are called ON a Reviewer
 * object by ReviewerManager. These are management/filtering concerns
 * that belong in ReviewerManager itself (violation of the Expert
 * principle in GRASP). This flaw is preserved verbatim for Task 1.
 * ------------------------------------------------------------------
 */
public class Reviewer {

    private static final int WORKLOAD_THRESHOLD = 6; // max active assignments

    private final String  id;
    private final String  name;
    private final int     currentWorkload;
    private final boolean hasConflict;

    private Submission assignedSubmission;

    public Reviewer(String id, String name, int currentWorkload, boolean hasConflict) {
        this.id              = id;
        this.name            = name;
        this.currentWorkload = currentWorkload;
        this.hasConflict     = hasConflict;
    }

    // ------------------------------------------------------------------
    // Interaction 1 -- filterConflicts
    // [DIAGRAM] ReviewerManager -> Reviewer: filterConflicts(reviewerList)
    // [FLAW]    This logic belongs in ReviewerManager, not Reviewer
    // ------------------------------------------------------------------

    /**
     * [DIAGRAM] ReviewerManager -> Reviewer: filterConflicts(reviewerList)
     *
     * Removes any reviewer from the list who has a conflict of interest
     * with the current submission.
     *
     * @param reviewerList the full candidate list from the database
     * @return list with conflicted reviewers removed
     */
    public List<Reviewer> filterConflicts(List<Reviewer> reviewerList) {
        List<Reviewer> noConflicts = new ArrayList<>();
        for (Reviewer r : reviewerList) {
            if (!r.hasConflict) {
                noConflicts.add(r);
            } else {
                TraceLogger.info("Reviewer", "removing " + r.name + " (conflict of interest)");
            }
        }
        TraceLogger.returnVal("Reviewer", "ReviewerManager",
                              "filteredList (" + noConflicts.size() + " remaining)");
        return noConflicts;
    }

    // ------------------------------------------------------------------
    // Interaction 2 -- checkWorkload
    // [DIAGRAM] ReviewerManager -> Reviewer: checkWorkload(reviewerList)
    // [FLAW]    This logic belongs in ReviewerManager, not Reviewer
    // ------------------------------------------------------------------

    /**
     * [DIAGRAM] ReviewerManager -> Reviewer: checkWorkload(reviewerList)
     *
     * Removes any reviewer whose current assignment load exceeds the
     * defined threshold.
     *
     * @param reviewerList list already filtered for conflicts
     * @return list with overloaded reviewers removed
     */
    public List<Reviewer> checkWorkload(List<Reviewer> reviewerList) {
        List<Reviewer> available = new ArrayList<>();
        for (Reviewer r : reviewerList) {
            if (r.currentWorkload < WORKLOAD_THRESHOLD) {
                available.add(r);
            } else {
                TraceLogger.info("Reviewer", "removing " + r.name
                                 + " (workload=" + r.currentWorkload + ", exceeds threshold)");
            }
        }
        TraceLogger.returnVal("Reviewer", "ReviewerManager",
                              "filteredList (" + available.size() + " remaining)");
        return available;
    }

    // ------------------------------------------------------------------
    // Interaction 3 -- assignReview
    // [DIAGRAM] loop [assign reviewers]: SubmissionController -> Reviewer: assignReview()
    // ------------------------------------------------------------------

    /**
     * [DIAGRAM] loop [assign reviewers] -- SubmissionController -> Reviewer: assignReview()
     *
     * Assigns this reviewer to the given submission.
     *
     * @param submission the submission to be reviewed
     */
    public void assignReview(Submission submission) {
        this.assignedSubmission = submission;
        TraceLogger.info("Reviewer:" + id, "assigned to " + submission.getId());
    }

    // ------------------------------------------------------------------
    // Interaction 4 -- submitScore
    // [DIAGRAM] loop [each reviewer]: Reviewer -> EvaluationManager: submitScore(score)
    // ------------------------------------------------------------------

    /**
     * [DIAGRAM] loop [each reviewer] -- Reviewer -> EvaluationManager: submitScore(score)
     *
     * Generates an evaluation score for the assigned submission and
     * pushes it directly to the EvaluationManager.
     *
     * NOTE: The Reviewer holds a direct reference to EvaluationManager
     * here so it can call submitScore() on it -- tight coupling preserved
     * exactly as depicted by the arrow in the sequence diagram.
     *
     * @param evaluationManager the manager to receive the score
     */
    public void submitScoreTo(EvaluationManager evaluationManager) {
        double score = generateScore();
        // [DIAGRAM] Reviewer -> EvaluationManager: submitScore(score)
        TraceLogger.call("Reviewer:" + id, "EvaluationManager",
                         "submitScore(score=" + TraceLogger.fmt(score) + ")");
        evaluationManager.submitScore(id, score);
    }

    // ------------------------------------------------------------------
    // Private helpers
    // ------------------------------------------------------------------

    /** Simulates the reviewer producing an evaluation score (50-100). */
    private double generateScore() {
        Random rng = new Random(id.hashCode());
        return Math.round((50 + rng.nextDouble() * 50) * 10.0) / 10.0;
    }

    // ------------------------------------------------------------------
    // Accessors
    // ------------------------------------------------------------------

    public String  getId()              { return id; }
    public String  getName()            { return name; }
    public int     getCurrentWorkload() { return currentWorkload; }
    public boolean hasConflict()        { return hasConflict; }

    @Override
    public String toString() {
        return "Reviewer{id='" + id + "', name='" + name + "'}";
    }
}
