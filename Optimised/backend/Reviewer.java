import java.util.Random;

/**
 * [DIAGRAM] Reviewer — domain object.
 *
 * Step 13 (loop): Reviewer -> EvaluationService: submitScores(scores)
 *
 * [OPTIMISATION vs Baseline]
 * In the baseline, Reviewer was incorrectly used as a delegate to run
 * filterConflicts() and checkWorkload() on behalf of ReviewerManager.
 * In the optimised design, Reviewer is a pure domain object — it only
 * knows about itself (id, workload, conflict flag) and submits its own score.
 * All filtering is handled internally by ReviewerSelectionService.
 */
public class Reviewer {

    private static final int WORKLOAD_THRESHOLD = 6;

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

    // [DIAGRAM] assign reviewer to submission (called from SubmissionService loop)
    public void assignReview(Submission submission) {
        this.assignedSubmission = submission;
        TraceLogger.info("Reviewer:" + id, "assigned to " + submission.getId());
    }

    // [DIAGRAM] Step 13 loop: Reviewer -> EvaluationService: submitScores(scores)
    public void submitScoreTo(EvaluationService evaluationService) {
        double score = generateScore();
        TraceLogger.call("Reviewer:" + id, "EvaluationService",
                         "submitScores(score=" + TraceLogger.fmt(score) + ")");
        evaluationService.receiveScore(id, score);
    }

    // ── Eligibility checks (used by ReviewerSelectionService internally) ──
    public boolean hasConflict()      { return hasConflict; }
    public boolean isOverloaded()     { return currentWorkload >= WORKLOAD_THRESHOLD; }
    public int     getCurrentWorkload(){ return currentWorkload; }

    public String getId()   { return id; }
    public String getName() { return name; }

    private double generateScore() {
        Random rng = new Random(id.hashCode());
        return Math.round((50 + rng.nextDouble() * 50) * 10.0) / 10.0;
    }

    @Override
    public String toString() {
        return "Reviewer{id='" + id + "', name='" + name + "'}";
    }
}
