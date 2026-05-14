import java.util.Random;

/**
 * Reviewer domain object. Pure data + score submission only — filtering logic has been
 * moved to ReviewerSelectionService, fixing the Expert pattern violation from the baseline.
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

    public void assignReview(Submission submission) {
        this.assignedSubmission = submission;
        TraceLogger.info("Reviewer:" + id, "assigned to " + submission.getId());
    }

    public void submitScoreTo(EvaluationService evaluationService) {
        double score = generateScore();
        TraceLogger.call("Reviewer:" + id, "EvaluationService",
                         "submitScores(score=" + TraceLogger.fmt(score) + ")");
        evaluationService.receiveScore(id, score);
    }

    public boolean hasConflict()       { return hasConflict; }
    public boolean isOverloaded()      { return currentWorkload >= WORKLOAD_THRESHOLD; }
    public int     getCurrentWorkload(){ return currentWorkload; }
    public String  getId()             { return id; }
    public String  getName()           { return name; }

    // Score seeded from reviewer id so benchmark runs are deterministic.
    private double generateScore() {
        Random rng = new Random(id.hashCode());
        return Math.round((50 + rng.nextDouble() * 50) * 10.0) / 10.0;
    }

    @Override
    public String toString() {
        return "Reviewer{id='" + id + "', name='" + name + "'}";
    }
}
